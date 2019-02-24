package cn.lollipop.cart.service;

import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.cart.pojo.Cart;
import cn.lollipop.cart.utils.ThreadLocalUtils;
import cn.lollipop.common.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "cart:user:id:";

    @Autowired
    public CartService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addCart(Cart cart) {
        // 获取当前登录用户
        UserInfo user = ThreadLocalUtils.get();
        // key
        String key = KEY_PREFIX + user.getId();
        // hashKey
        String hashKey = String.valueOf(cart.getSkuId());
        int num = cart.getNum();

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        // 判断当前购物车商品是否存在
        if (operations.hasKey(hashKey)) {
            // 修改数量
            String json = operations.get(hashKey).toString();
            cart = JsonUtils.parse(json, Cart.class);
            cart.setNum(num + cart.getNum());
        }
        operations.put(hashKey, JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList() {
        // 获取当前登录用户
        UserInfo user = ThreadLocalUtils.get();
        // key
        String key = KEY_PREFIX + user.getId();
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionConstant.CART_NOT_FOUND);
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        return operations.values().stream()
                .map(o -> JsonUtils.parse(o.toString(), Cart.class)).collect(Collectors.toList());
    }
}
