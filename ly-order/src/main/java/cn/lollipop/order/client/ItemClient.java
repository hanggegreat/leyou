package cn.lollipop.order.client;

import cn.lollipop.item.api.ItemApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface ItemClient extends ItemApi {
}
