package cn.lollipop.item.web;

import cn.lollipop.item.pojo.Category;
import cn.lollipop.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类信息
     *
     * @param pid 父分类id
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 查询商品所属分类
     *
     * @param bid 商品id
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryByBid(@PathVariable("bid") Long bid) {
        return ResponseEntity.ok(categoryService.queryCategoryByBid(bid));
    }

    /**
     * 根据id查询全部商品分类
     *
     * @param ids
     * @return
     */
    @GetMapping("list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(categoryService.queryByIdList(ids));
    }

    /**
     * 根据id查询商品分类
     *
     * @param id
     * @return
     */
    @GetMapping("list/{id}")
    public ResponseEntity<Category> queryCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.queryCategoryById(id));
    }
}
