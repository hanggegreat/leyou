package cn.lollipop.item.web;

import cn.lollipop.item.pojo.Brand;
import cn.lollipop.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cn.lollipop.common.vo.PageResult;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * 分页查询品牌信息
     *
     * @param key    查询关键字
     * @param page   页数
     * @param rows   每页行数
     * @param sortBy 排序关键字
     * @param desc   升降序
     * @return
     */
    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(required = false) String key,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer rows,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "false") Boolean desc) {
        return ResponseEntity.ok(brandService.queryBrandByPage(key, page, rows, sortBy, desc));
    }

    /**
     * 新增品牌信息
     *
     * @param brand 品牌
     * @param cids  分类编号
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 删除品牌信息
     *
     * @param bid 品牌编号
     * @return
     */
    @PutMapping("{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid") Long bid) {
        brandService.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 修改品牌信息
     *
     * @param brand 品牌
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Void> editBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.editBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 查询商品分类下的所有品牌信息
     *
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable Long cid) {
        return ResponseEntity.ok(brandService.queryByCid(cid));
    }

    /**
     * 根据品牌id查询品牌信息
     *
     * @param id 品牌id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(brandService.queryById(id));
    }

    /**
     * 根据id查询全部品牌信息
     * @param ids
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }
}
