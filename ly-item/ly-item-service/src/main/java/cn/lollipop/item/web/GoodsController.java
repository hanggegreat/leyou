package cn.lollipop.item.web;

import cn.lollipop.item.pojo.Sku;
import cn.lollipop.item.pojo.Spu;
import cn.lollipop.item.pojo.SpuDetail;
import cn.lollipop.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vo.PageResult;

import java.util.List;

@RestController
public class GoodsController {

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 分页查询Spu信息
     *
     * @param key      title字段关键字
     * @param saleable 是否上架
     * @param page     页数
     * @param row      每页行数
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(String key, Boolean saleable, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer row) {
        return ResponseEntity.ok(goodsService.querySpuByPage(key, saleable, page, row));
    }

    /**
     * 增加Spu信息
     *
     * @param spu Spu信息
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改Spu信息
     *
     * @param spu 新的spu信息
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> editGoods(@RequestBody Spu spu) {
        goodsService.editGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spu查询其detail信息
     *
     * @param id spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuDetailById(id));
    }

    /**
     * 根据spu查询其对应所有sku信息
     *
     * @param id spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuListBySpuId(Long id) {
        return ResponseEntity.ok(goodsService.querySkuListBySpuId(id));
    }

    /**
     * 根据id查询spu信息
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(goodsService.querySpuById(id));
    }
}
