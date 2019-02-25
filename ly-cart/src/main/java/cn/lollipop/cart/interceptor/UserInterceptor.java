package cn.lollipop.cart.interceptor;

import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.auth.utils.JwtUtils;
import cn.lollipop.cart.config.JwtProperties;
import cn.lollipop.cart.utils.ThreadLocalUtils;
import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {
    private final JwtProperties prop;

    @Autowired
    public UserInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取Cookie中的token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        try {
            // 解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            ThreadLocalUtils.set(user);
            return true;
        } catch (Exception e) {
            log.error("[购物车服务]，用户认证失败！");
            throw new LyException(ExceptionConstant.UNAUTHORIZED);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.remove();
    }
}
