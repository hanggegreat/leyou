package cn.lollipop.page.service;

import cn.lollipop.item.pojo.*;
import cn.lollipop.page.client.ItemClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageService {

    private final ItemClient itemClient;
    private final TemplateEngine templateEngine;

    @Autowired
    public PageService(ItemClient itemClient, TemplateEngine templateEngine) {
        this.itemClient = itemClient;
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> map = Maps.newHashMap();
        // 查询spu
        Spu spu = itemClient.querySpuById(spuId);
        // 查询skus
        List<Sku> skus = spu.getSkus();
        // 查询detail
        SpuDetail detail = spu.getSpuDetail();
        // 查询brand
        Brand brand = itemClient.queryBrandById(spu.getBrandId());
        // 查询商品分类
        List<Category> categories = itemClient.queryCategoryByIds(Lists.newArrayList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询specs
        List<SpecGroup> specs = itemClient.queryListByCid(spu.getCid3());

        map.put("spu", spu);
        map.put("skus", skus);
        map.put("detail", detail);
        map.put("brand", brand);
        map.put("categories", categories);
        map.put("specs", specs);

        return map;
    }

    public void createStaticHtml(Long spuId) {
        // 上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        // 输出流
        File dest = new File("F:/usr/" + spuId + ".html");
        if (dest.exists()) {
            dest.delete();
        }
        try (PrintWriter writer = new PrintWriter(dest, "UTF8")) {
            // 生成HTML
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("[静态服务]：生成静态页面异常", e);
        }
    }

    public void deleteHtml(Long spuId) {
        File dest = new File("F:/usr/" + spuId + ".html");
        if (dest.exists()) {
            dest.delete();
        }
    }
}
