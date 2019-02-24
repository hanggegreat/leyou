package cn.lollipop.gateway.filter;

import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.auth.utils.JwtUtils;
import cn.lollipop.common.util.CookieUtils;
import cn.lollipop.gateway.config.FilterProperties;
import cn.lollipop.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthFilter extends ZuulFilter {

    private final JwtProperties jwtProp;
    private final FilterProperties filterProp;

    @Autowired
    public AuthFilter(JwtProperties jwtProp, FilterProperties filterProp) {
        this.jwtProp = jwtProp;
        this.filterProp = filterProp;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;// 过滤器类型，前置过滤
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;// 过滤器顺序
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取请求路径
        String uri = request.getRequestURI();
        // 判断是否需要放行
        for (String path : filterProp.getAllowPaths()) {
            if (uri.startsWith(path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取cookie中的token
        String token = CookieUtils.getCookieValue(request, jwtProp.getCookieName());
        try {
            // 解析token
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProp.getPublicKey());
            // TODO 权限校验
        } catch (Exception e) {
            // 解析token失败，拦截
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
        }
        return null;
    }
}
