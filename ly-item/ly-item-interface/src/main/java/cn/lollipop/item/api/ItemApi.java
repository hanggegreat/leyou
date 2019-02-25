package cn.lollipop.item.api;

import cn.lollipop.common.dto.CartDTO;
import cn.lollipop.common.vo.PageResult;
import cn.lollipop.item.pojo.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ItemApi {
    /**
     * 根据品牌id查询品牌信息
     *
     * @param id 品牌id
     * @return
     */
    @GetMapping("brand/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据id查询商品分类
     *
     * @param ids
     * @return
     */
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids") List<Long> ids);

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
    PageResult<Spu> querySpuByPage(@RequestParam String key, @RequestParam Boolean saleable, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer row);

    /**
     * 根据spu查询其detail信息
     *
     * @param id spuId
     * @return
     */
    @GetMapping("spu/detail/{id}")
    SpuDetail querySpuDetailById(@PathVariable("id") Long id);

    /**
     * 根据spu查询其对应所有sku信息
     *
     * @param id spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuListBySpuId(@RequestParam Long id);

    /**
     * 根据商品规格分组编号或商品分类编号查询其全部商品规格参数信息
     *
     * @param gid 商品规格分组编号
     * @param cid 商品分类编号
     * @return
     */
    @GetMapping("spec/params")
    List<SpecParam> queryParamList(@RequestParam Long gid, @RequestParam Long cid, @RequestParam Boolean searching);

    /**
     * 根据id查询全部品牌信息
     *
     * @param ids
     * @return
     */
    @GetMapping("brand/list")
    List<Brand> queryBrandByIds(@RequestParam List<Long> ids);

    /**
     * 根据id查询spu信息
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据分类查询规格组及组内参数
     *
     * @param cid
     * @return
     */
    @GetMapping("spec/group")
    List<SpecGroup> queryListByCid(@RequestParam("cid") Long cid);

    /**
     * 批量查询sku信息
     *
     * @param ids skuId集合
     * @return
     */
    @GetMapping("sku/list/ids")
    List<Sku> querySkuListByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 减少库存
     *
     * @param cartDTOList
     */
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDTO> cartDTOList);
}
