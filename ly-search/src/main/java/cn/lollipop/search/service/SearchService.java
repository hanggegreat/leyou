package cn.lollipop.search.service;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.JsonUtils;
import cn.lollipop.item.pojo.*;
import cn.lollipop.search.GoodsRepository;
import cn.lollipop.search.client.ItemClient;
import cn.lollipop.search.pojo.Goods;
import cn.lollipop.search.pojo.SearchPageResult;
import cn.lollipop.search.pojo.SearchRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import cn.lollipop.common.vo.PageResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SearchService {

    private final ItemClient itemClient;
    private final GoodsRepository goodsRepository;
    private final ElasticsearchTemplate template;

    @Autowired
    public SearchService(ItemClient itemClient, GoodsRepository goodsRepository, ElasticsearchTemplate template) {
        this.itemClient = itemClient;
        this.goodsRepository = goodsRepository;
        this.template = template;
    }


    /**
     * 根据spu构建Goods对象
     *
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu) {
        // 搜索字段
        StringBuilder all = new StringBuilder();
        // 查询标题
        all.append(spu.getTitle());
        // 查询分类
        List<Category> categoryList = itemClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if (CollectionUtils.isEmpty(categoryList)) {
            throw new LyException(ExceptionConstant.CATEGORY_NOT_FOUND);
        }

        all.append(StringUtils.join(categoryList.stream().map(Category::getName).collect(Collectors.toList()), " "));
        // 查询品牌
        Brand brand = itemClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new LyException(ExceptionConstant.BRAND_NOT_FOUNT);
        }

        all.append(brand.getName());
        // 查询sku
        List<Sku> skuList = itemClient.querySkuListBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)) {
            throw new LyException(ExceptionConstant.GOODS_SKU_NOT_FOUND);
        }
        // 对sku进行处理
        List<Map<String, Object>> skus = new ArrayList<>();
        skuList.forEach(sku -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skus.add(map);
        });
        // 查询规格参数
        List<SpecParam> params = itemClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new LyException(ExceptionConstant.SPEC_PARAM_NOT_FOUND);
        }
        // 查询商品详情
        SpuDetail detail = itemClient.querySpuDetailById(spu.getId());
        // 获取通用规格参数
        Map<Long, String> genericSpecs = JsonUtils.parseMap(detail.getGenericSpec(), Long.class, String.class);
        // 获取特殊规格参数
        Map<Long, List<String>> specialSpecs = JsonUtils.nativeRead(detail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        // 规格参数
        Map<String, Object> specs = new HashMap<>();

        if (!CollectionUtils.isEmpty(genericSpecs) && !CollectionUtils.isEmpty(specialSpecs)) {
            params.forEach(param -> {
                String key = param.getName();
                Object value;
                if (param.getGeneric()) {
                    value = genericSpecs.get(param.getId());
                    if (param.getNumeric()) {
                        value = chooseSegment(value.toString(), param);
                    }
                } else {
                    value = specialSpecs.get(param.getId());
                }
                specs.put(key, value);
            });
        }
        // 构建goods对象
        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(all.toString()); // 搜索字段，包含标题、分类、品牌、规格等
        goods.setPrice(skuList.stream().map(Sku::getPrice).collect(Collectors.toSet())); // 所有sku的价格集合
        goods.setSkus(JsonUtils.serialize(skus)); // 所有sku的集合的json格式
        goods.setSpecs(specs); // 所有可搜索的规格参数
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage() - 1;
        int size = request.getSize();
        // 1.创建查询构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 2.结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        // 3.分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        // 4.过滤
        QueryBuilder basicQuery = buildBasicBoolQuery(request);
        queryBuilder.withQuery(basicQuery);
        // 5.聚合分类和品牌
        // 5.1 聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        // 5.2 品牌分类
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        // 6 查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        // 7 解析聚合结果
        Aggregations aggregations = result.getAggregations();
        List<Category> categoryList = parseCategoryAgg(aggregations.get(categoryAggName));
        List<Brand> brandList = parseBrandAgg(aggregations.get(brandAggName));
        // 8 解析规格参数聚合
        List<Map<String, Object>> specs = null;
        if (categoryList != null && categoryList.size() == 1) {
            specs = buildSpecificationAgg(categoryList.get(0), basicQuery);
        }
        // 解析结果
        return new SearchPageResult(result.getTotalElements(), (long) result.getTotalPages(), result.getContent(), categoryList, brandList, specs);
    }

    private QueryBuilder buildBasicBoolQuery(SearchRequest request) {
        // 创建布尔查询
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        // 查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()));
        // 过滤条件
        Map<String, String> map = request.getFilter();
        map.forEach((k, v) -> {
            queryBuilder.filter(QueryBuilders.termQuery(
                    ("cid3".equals(k) || "brandId".equals(k)) ? k : ("specs." + k + ".keyword"), v));
        });
        return queryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Category category, QueryBuilder basicQuery) {
        List<Map<String, Object>> res = new ArrayList<>();
        // 1.查询需要聚合的规格参数
        List<SpecParam> params = itemClient.queryParamList(null, category.getId(), true);
        // 2.聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        params.forEach(param -> {
            String name = param.getName();
            queryBuilder.withQuery(basicQuery);
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        });
        // 3.获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        // 4.解析结果
        Aggregations aggs = result.getAggregations();
        params.forEach(param -> {
            StringTerms aggregation = aggs.get(param.getName());
            HashMap<String, Object> map = Maps.newHashMap();
            map.put("k", param.getName());
            map.put("options", aggregation.getBuckets().stream()
                    .map(StringTerms.Bucket::getKeyAsString).collect(Collectors.toList()));
            res.add(map);
        });
        return res;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            return itemClient.queryBrandByIds(ids);
        } catch (Exception e) {
            log.error("[搜索服务]查询品牌信息异常：{}", e);
            return null;
        }
    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
            return itemClient.queryCategoryByIds(ids);
        } catch (Exception e) {
            log.error("[搜索服务]查询分类信息异常：{}", e);
            return null;
        }
    }

    public void createOrUpdateIndex(Long spuId) {
        // 查询spu
        Spu spu = itemClient.querySpuById(spuId);
        // 构建goods, 并存入索引库
        goodsRepository.save(buildGoods(spu));
    }

    public void deleteIndex(Long spuId) {
        goodsRepository.deleteById(spuId);
    }
}
