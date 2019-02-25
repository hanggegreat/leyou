package cn.lollipop.search.pojo;

import cn.lollipop.item.pojo.Brand;
import cn.lollipop.item.pojo.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import cn.lollipop.common.vo.PageResult;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SearchPageResult extends PageResult<Goods> {
    private List<Category> categories;

    private List<Brand> brands;

    private List<Map<String, Object>> specs;

    public SearchPageResult(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
}
