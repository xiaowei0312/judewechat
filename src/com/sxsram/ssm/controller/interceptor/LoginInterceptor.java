package com.sxsram.ssm.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.service.UserService;
import com.sxsram.ssm.util.ConfigUtil;
import com.sxsram.ssm.util.WechatOAuth2User;
import com.sxsram.ssm.util.WechatOauth2Token;
import com.sxsram.ssm.util.WechatUtil;

@Controller
public class LoginInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 获取请求的url
		String url = request.getRequestURI().toString();
		if (request.getQueryString() != null)
			url += ("?" + request.getQueryString());

		// 不需要登录验证，且code字段在控制器内部处理的路径
		if (url.indexOf("userNearbyStores") > 0) {
			return true;
		}

		// 获取session
		HttpSession session = request.getSession();

		// 判断是否有code
		String code = request.getParameter("code");
		String codeHasUsed = request.getParameter("codeHasUsed");
		String openId = null;

		if (code != null && codeHasUsed == null) {
			WechatOauth2Token wechatOauth2Token = null;
			wechatOauth2Token = WechatUtil.getOauth2AccessToken(ConfigUtil.APPID, ConfigUtil.APPSECRET, code);
			if (wechatOauth2Token != null && wechatOauth2Token.getOpenId() != null) {
				openId = wechatOauth2Token.getOpenId();
				WechatOAuth2User wechatUser = WechatUtil.getOauth2UserInfo(wechatOauth2Token.getAccessToken(),
						wechatOauth2Token.getOpenId());
				if (wechatUser != null)
					session.setAttribute("wechatUser", wechatUser);
				session.setAttribute("openId", wechatOauth2Token.getOpenId());
				session.setAttribute("wechatOauth2Token", wechatOauth2Token);
				if (url.indexOf("&codeHasUsed=1") < 0)
					url += "&codeHasUsed=1";
			}
		}

		// 判断url是否是公开 地址（实际使用时将公开 地址配置配置文件中）
		// 这里公开地址是登陆提交的地址
		if (url.indexOf("login") > 0 || url.indexOf("regist") > 0 || url.indexOf("Regist") > 0
				|| url.indexOf("error") > 0 || url.indexOf("imageVerifyCode") > 0 || url.indexOf("userNearbyStores") > 0
				|| url.indexOf("getTypeListAjax") > 0 || url.indexOf("shopDetail") > 0)
			return true;

		// 从session中取出用户身份信息
		UserExpand sessionUser = (UserExpand) session.getAttribute("user");
		if (sessionUser != null) {
			return true;
		}

		// 根据openid获取
		if (openId != null) {
			sessionUser = userService.findUserByOpenId(openId);
		}
		if (sessionUser != null) {
			session.setAttribute("user", sessionUser);
			return true;
		}

		// 执行这里表示用户身份需要认证，跳转登陆页面
		session.setAttribute("loginInterceptedReqUrl", url);
		request.getRequestDispatcher("/user/loginPage.action").forward(request, response);
		return false;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}
}