package cn.lollipop.search;


import cn.lollipop.item.pojo.Spu;
import cn.lollipop.search.client.ItemClient;
import cn.lollipop.search.pojo.Goods;
import cn.lollipop.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import cn.lollipop.common.vo.PageResult;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemClient itemClient;

    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadData() {
        int page = 1;
        int row = 100;
        while (true) {
            PageResult<Spu> spuPageResult = itemClient.querySpuByPage(null, true, page++, row);
            List<Spu> spuList = spuPageResult.getItems();
            // 构建成Goods
            List<Goods> goodsList = spuList.stream()
                    .map(searchService::buildGoods).collect(Collectors.toList());
            System.out.println(goodsList);
            goodsRepository.saveAll(goodsList);
            if (spuList.size() < 100) {
                break;
            }
        }
    }
}