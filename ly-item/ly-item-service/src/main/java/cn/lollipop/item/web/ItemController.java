package cn.lollipop.item.web;

import cn.lollipop.common.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.item.pojo.Item;
import cn.lollipop.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Item> saveItem(Item item) {
        // 校验价格
        if (item.getPrice() == null) {
            throw new LyException(ExceptionConstant.PRICE_CANNOT_BE_NULL);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.saveItem(item));
    }
}
