package cn.lollipop.auth.service;

import cn.lollipop.auth.client.UserClient;
import cn.lollipop.auth.config.JwtProperties;
import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.auth.utils.JwtUtils;
import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.CookieUtils;
import cn.lollipop.user.pojo.User;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class AuthService {

    private final UserClient userClient;
    private final JwtProperties prop;

    @Autowired
    public AuthService(UserClient userClient, JwtProperties prop) {
        this.userClient = userClient;
        this.prop = prop;
    }

    public void login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 校验用户名和密码
            User user = userClient.queryUserByUsernameAndPassword(username, password);

            if (user == null) {
                throw new LyException(ExceptionConstant.INVALID_USERNAME_OR_PASSWORD);
            }
            // 生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), prop.getPrivateKey(), prop.getExpire());
            CookieUtils.setCookie(request, response, prop.getCookieName(), token);
        } catch (FeignException e) {
            throw new LyException(ExceptionConstant.INVALID_USERNAME_OR_PASSWORD);
        } catch (Exception e) {
            throw new LyException(ExceptionConstant.CREATE_TOKEN_ERROR);
        }
    }

    public UserInfo verify(String token, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(token)) {
            throw new LyException(ExceptionConstant.UNAUTHORIZED);
        }
        // 解析token
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            // 刷新token
            String newToken = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            // 写入cookie
            CookieUtils.setCookie(request, response, prop.getCookieName(), newToken, prop.getExpire());
            return userInfo;
        } catch (Exception e) {
            throw new LyException(ExceptionConstant.UNAUTHORIZED);
        }
    }
}
