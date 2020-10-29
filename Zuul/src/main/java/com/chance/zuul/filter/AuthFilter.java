package com.chance.zuul.filter;

import com.chance.zuul.tools.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 权限验证 Filter 注册和登录接口不过滤
 *
 * 验证权限需要前端在 Cookie 或 Header 中（二选一即可）设置用户的 userId 和 token 因为 token 是存在 Redis
 * 中的，Redis 的键由 userId 构成，值是 token 在两个地方都没有找打 userId 或 token其中之一，就会返回 400
 * 无权限，并给与文字提示
 */
@Component
public class AuthFilter extends ZuulFilter {

	private Logger logger = LoggerFactory.getLogger(AuthFilter.class);

	// 排除过滤的 uri 地址
	private static final String LOGIN_URI = "/user/login";
	private static final String REGISTER_URI = "/user/register";

	// 无权限时的提示语
	private static final String INVALID_TOKEN = "invalid token";
	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return PRE_DECORATION_FILTER_ORDER - 1;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();

		logger.info("===uri==={}", request.getRequestURI());
		// 注册和登录接口不拦截，其他接口都要拦截校验 token
		//request.getRequestURI().indexOf("/account/") 排除请求URL的
		return !LOGIN_URI.equals(request.getRequestURI()) && !REGISTER_URI.equals(request.getRequestURI());
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();

		// 先从 cookie 中取 token，cookie中取失败再从 header 中取，两重校验
		// 通过工具类从 Cookie 中取出 token
		String token = CookieUtils.getCookieValue(request, "token");
		System.out.println(token + "===========cookie===================");

		// 不验证token时注调该代码
		if (token == null || StringUtils.isEmpty(token)) {
			readTokenFromHeader(requestContext, request);
		} else {
			verifyToken(requestContext, request, token);
		}

		return null;
	}

	/**
	 * 从 header 中读取 token 并校验
	 */
	private void readTokenFromHeader(RequestContext requestContext, HttpServletRequest request) {
		// 从 header 中读取
		String headerToken = request.getHeader("token");
		if (StringUtils.isEmpty(headerToken)) {
			setUnauthorizedResponse(requestContext, INVALID_TOKEN);
		} else {
			verifyToken(requestContext, request, headerToken);
		}
	}

	/**
	 * 从Redis中校验token
	 */
	private void verifyToken(RequestContext requestContext, HttpServletRequest request, String token) {
		//Token验证策略，可以解密Token，或者连接Redis验证
	}

	/**
	 * 设置 400 无权限状态
	 */
	private void setUnauthorizedResponse(RequestContext requestContext, String msg) {
		requestContext.setSendZuulResponse(false);
		requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
		String result = "验证失败";
		requestContext.setResponseBody(result);
	}
}