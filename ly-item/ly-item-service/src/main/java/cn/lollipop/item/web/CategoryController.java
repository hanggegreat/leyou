package cn.lollipop.item.web;

import cn.lollipop.item.pojo.Category;
import cn.lollipop.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 查询分类信息
     * @param pid 父分类id
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(Long pid) {
        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    @GetMapping("/bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryByBid(@PathVariable("bid") Long bid) {
        return ResponseEntity.ok(categoryService.queryCategoryByBid(bid));
    }
}
