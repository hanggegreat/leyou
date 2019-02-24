package cn.lollipop.user.api;

import cn.lollipop.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/query")
    User queryUserByUsernameAndPassword(@RequestParam String username, @RequestParam String password);
}
