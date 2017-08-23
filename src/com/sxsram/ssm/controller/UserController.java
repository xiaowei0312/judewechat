package com.sxsram.ssm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.media.sound.ModelAbstractChannelMixer;
import com.sxsram.ssm.entity.AccountRecordExpand;
import com.sxsram.ssm.entity.GlobalPrams;
import com.sxsram.ssm.entity.JournalBookExpand;
import com.sxsram.ssm.entity.OnlineJournalBookItem;
import com.sxsram.ssm.entity.PlatformSyDistributedRecordExpand;
import com.sxsram.ssm.entity.PlatformYljSavedRecordExpand;
import com.sxsram.ssm.entity.RechargeAndWithDrawRecordExpand;
import com.sxsram.ssm.entity.RoleExpand;
import com.sxsram.ssm.entity.ShoppingMallExpand;
import com.sxsram.ssm.entity.ShoppingMallExpandQueryVo;
import com.sxsram.ssm.entity.ShoppingMallType;
import com.sxsram.ssm.entity.ShoppingMallTypeQueryVo;
import com.sxsram.ssm.entity.User;
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.UserExpandQueryVo;
import com.sxsram.ssm.entity.UserExtra;
import com.sxsram.ssm.entity.UserExtraQueryVo;
import com.sxsram.ssm.service.GlobalParamService;
import com.sxsram.ssm.service.MallService;
import com.sxsram.ssm.service.MemberService;
import com.sxsram.ssm.service.UserExtraService;
import com.sxsram.ssm.service.UserService;
import com.sxsram.ssm.util.AjaxResult;
import com.sxsram.ssm.util.ConfigUtil;
import com.sxsram.ssm.util.DateUtil;
import com.sxsram.ssm.util.IpAddressUtil;
import com.sxsram.ssm.util.JsonResult;
import com.sxsram.ssm.util.Pagination;
import com.sxsram.ssm.util.PreciseComputeUtil;
import com.sxsram.ssm.util.QueryCondition;
import com.sxsram.ssm.util.QueryConditionAbstractItem;
import com.sxsram.ssm.util.QueryConditionItem;
import com.sxsram.ssm.util.QueryConditionOp;
import com.sxsram.ssm.util.QueryConditionOrItems;
import com.sxsram.ssm.util.WechatJsapiSign;
import com.sxsram.ssm.util.WechatJsapiSignUtil;
import com.sxsram.ssm.util.WechatOAuth2User;
import com.sxsram.ssm.util.WechatOauth2Token;
import com.sxsram.ssm.util.WechatPosition;
import com.sxsram.ssm.util.WechatUtil;
import com.sxsram.ssm.util.WxTemplateData;
import com.sxsram.ssm.util.WxTemplateDataEntry;
import com.sxsram.ssm.util.XMLUtil;

import net.sf.json.JSON;

@Controller()
@RequestMapping(value = "/user", method = { RequestMethod.GET, RequestMethod.POST })
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private MallService mallService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	private UserExtraService userExtraService;

	@RequestMapping(value = "/error", method = { RequestMethod.GET, RequestMethod.POST })
	public String error(HttpSession session, Model model, String code, String state) throws Exception {
		return "user/error";
	}

	@RequestMapping(value = "/userCenter", method = { RequestMethod.GET, RequestMethod.POST })
	public String userCenter(HttpServletRequest request, HttpSession session, Model model, String code, String state)
			throws Exception {
		Gson gson = new Gson();
		UserExpand user = (UserExpand) session.getAttribute("user");
		WechatOAuth2User wechatOAuth2User = (WechatOAuth2User) session.getAttribute("wechatUser");
		if (wechatOAuth2User != null) {
			String headImgUrl = wechatOAuth2User.getHeadImgUrl();
			// headImgUrl = headImgUrl.replaceAll("\\", "");
			if (headImgUrl.lastIndexOf("/") >= 0) {
				headImgUrl = headImgUrl.substring(0, headImgUrl.lastIndexOf("/"));
				headImgUrl = headImgUrl + "/46";
				user.setHeadImgUrl(headImgUrl);
				session.setAttribute("user", user);
			}
		}

		RoleExpand roleExpand = userService.findRoleByUserId(user.getId());
		// 3. 如果找到，根据id查找账户信息
		UserExpand userExpand = userService.getUserAccountInfo(user.getId() + "");
		userExpand.setRole(roleExpand);
		userExpand.setTotalConsumOnline(memberService.getTotalConsumeOnline(user.getId()));
		model.addAttribute("user", userExpand);

		// 4. JsAPI 用于邀请好友
		String url = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			url += ("?" + request.getQueryString());
		String wechatJsapiTicket = WechatJsapiSignUtil.getJSApiTicket(ConfigUtil.APPID, ConfigUtil.APPSECRET);
		WechatJsapiSign wechatJsapiSign = WechatJsapiSignUtil.getSign(ConfigUtil.APPID, wechatJsapiTicket, url);
		model.addAttribute("jsApiSign", wechatJsapiSign);

		// 5.发送注册推送
		// 查询当前用户的inviterId
		UserExtra userExtra = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("e.userId", user.getId() + "", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		userExtra = userExtraService.findUserExtra(userExtraQueryVo);
		if (userExtra == null) {
			throw new RuntimeException("找不到UserExtra.");
		}

		// 判断是否已经推送过
		if (userExtra.getSendPushMsgFlag() == 0 || userExtra.getSendPushMsgFlag() == null) {
			String templateId = ConfigUtil.TEMPLATEID_INVITER_REGISTER_SUCCESS;
			if (userExtra.getInviterUser() != null) {
				// 给推荐人发送推送消息
				WxTemplateData wxTemplateData = new WxTemplateData();
				wxTemplateData.first = new WxTemplateDataEntry(ConfigUtil.INVITER_SUCCESS_MSG, "#173177");
				wxTemplateData.keyword1 = new WxTemplateDataEntry(userExtra.getInviterUser().getPhone(), "#173177");
				wxTemplateData.keyword2 = new WxTemplateDataEntry(userExtra.getUser().getPhone(), "#173177");
				wxTemplateData.remark = new WxTemplateDataEntry("感谢您使用聚德公众号", "#173177");
				String jsonStr = WechatUtil.responseTemplateMsg(ConfigUtil.APPID, ConfigUtil.APPSECRET,
						userExtra.getInviterUser().getOpenId(), templateId, null, wxTemplateData);
				JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
				if (jsonObject.get("errcode") == null)
					return gson.toJson(new JsonResult("0", "-2", "发送推送消息失败."));
				int code1 = Integer.valueOf(jsonObject.get("errcode").toString());
				if (code1 != 0) {
					return gson.toJson(new JsonResult("0", "-2", "发送推送消息失败:" + jsonObject.get("errmsg").toString()));
				}
			}

			// 给注册用户发送推送消息
			WxTemplateData wxTemplateData = new WxTemplateData();
			wxTemplateData.first = new WxTemplateDataEntry(ConfigUtil.BE_INVITERED_REGISTER_SUCCESS_MSG, "#173177");
			wxTemplateData.keyword1 = new WxTemplateDataEntry(
					((userExtra.getInviterUser() == null) ? "无" : (userExtra.getInviterUser().getPhone())), "#173177");
			wxTemplateData.keyword2 = new WxTemplateDataEntry(userExtra.getUser().getPhone(), "#173177");
			wxTemplateData.remark = new WxTemplateDataEntry("感谢您使用聚德公众号", "#173177");
			String jsonStr = WechatUtil.responseTemplateMsg(ConfigUtil.APPID, ConfigUtil.APPSECRET,
					userExtra.getUser().getOpenId(), templateId, null, wxTemplateData);
			JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
			if (jsonObject.get("errcode") == null)
				return gson.toJson(new JsonResult("0", "-2", "发送推送消息失败."));
			int code1 = Integer.valueOf(jsonObject.get("errcode").toString());
			if (code1 != 0) {
				return gson.toJson(new JsonResult("0", "-2", "发送推送消息失败:" + jsonObject.get("errmsg").toString()));
			}

			// 更新推送标志
			UserExtra tmpUserExtra = new UserExtra();
			tmpUserExtra.setId(userExtra.getId());
			tmpUserExtra.setSendPushMsgFlag(1);
			userExtraService.updateUserExtra(tmpUserExtra);
		}

		return "user/userInfo";
	}

	@RequestMapping(value = "/judgeSubscribe", method = { RequestMethod.GET, RequestMethod.POST })
	public String judgeSubscribe(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		WechatOauth2Token wechatOauth2Token = (WechatOauth2Token) session.getAttribute("wechatOauth2Token");
		if (wechatOauth2Token == null) {
			throw new RuntimeException("Can't find the wechatOauth2Token in session.");
		}
		if (WechatUtil.judgeIsFollow(wechatOauth2Token.getAccessToken(), wechatOauth2Token.getOpenId()) == false) {
			return "user/subscribe";
		}
		return "forward: userCenter.action";
	}

	@RequestMapping(value = "/inviterPage", method = { RequestMethod.GET, RequestMethod.POST })
	public String inviterPage(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		// 4. JsAPI 用于邀请好友
		String url = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			url += ("?" + request.getQueryString());
		String wechatJsapiTicket = WechatJsapiSignUtil.getJSApiTicket(ConfigUtil.APPID, ConfigUtil.APPSECRET);
		WechatJsapiSign wechatJsapiSign = WechatJsapiSignUtil.getSign(ConfigUtil.APPID, wechatJsapiTicket, url);
		model.addAttribute("jsApiSign", wechatJsapiSign);
		return "user/inviterPage";
	}

	@RequestMapping(value = "/beInviterRegistPage", method = { RequestMethod.GET, RequestMethod.POST })
	public String beInviterPage(HttpServletRequest request, HttpSession session, Model model, Integer inviterUserId)
			throws Exception {
		model.addAttribute("inviterUserId", inviterUserId);
		return "user/beInviterPage";
	}

	@RequestMapping(value = "/loginPage", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginPage() throws Exception {
		return "user/login";
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String login(HttpSession session, Model model, UserExpand inputUser, String verifyCode) throws Exception {
		Gson gson = new Gson();

		// 1.检查验证码是否正确
		String currentVerifyCode = (String) session.getAttribute("imageVerifyCode");
		if (verifyCode == null || currentVerifyCode == null || !verifyCode.equals(currentVerifyCode))
			return gson.toJson(new AjaxResult(null, "验证码不正确"));

		// 2. 获取WechatOAuth2Token
		WechatOauth2Token wechatOauth2Token = (WechatOauth2Token) session.getAttribute("wechatOauth2Token");
		if (wechatOauth2Token == null) {
			return gson.toJson(new AjaxResult(null, "无法获取到授权的accessToken，请返回到公众号，尝试再次登录"));
		}

		// 3. 获取WechatOAuth2User
		WechatOAuth2User wechatOAuth2User = (WechatOAuth2User) session.getAttribute("wechatUser");
		if (wechatOAuth2User == null) {
			return gson.toJson(new AjaxResult(null, "无法获取到用户授权信息，请返回到公众号，尝试再次登录"));
		}

		// 4.检查用户名(电话) 密码是否正确
		UserExpand user = userService.findUserWhenLogin(inputUser);
		if (user == null) {
			return gson.toJson(new AjaxResult(null, "用户名或密码错误"));
		}

		// 5. 给用户设置openId字段
		if (user.getOpenId() == null) { // 第一次登陆
			try {
				user.setOpenId(wechatOauth2Token.getOpenId());
				userService.updateUserOpenId(user);
			} catch (Exception e) {
				return gson.toJson(new AjaxResult(null, "该微信号已绑定过其他用户."));
			}
			user.getCertification().setName(wechatOAuth2User.getNickname());
			userService.updateCertification(user.getCertification());
		} else { // 不是第一次登陆
			// 2. 检查是否有openId
			if ((!user.getOpenId().equals(wechatOauth2Token.getOpenId()))) {
				return gson.toJson(new AjaxResult(null, "账户与所绑定微信不匹配，请使用初次登录所使用的微信登录本公众号."));
			}
		}

		// 7.登录成功，将user放入session
		session.setAttribute("user", user);
		
		//8. 新用户注册成功赠送600积分
		userService.registSuccessGiftJf(user);
		
		// 8.跳转到被拦截器拦截的请求页面
		String url = (String) session.getAttribute("loginInterceptedReqUrl");
		if (url != null) {
			session.removeAttribute("loginInterceptedReqUrl");
			return gson.toJson(new AjaxResult(url, null));
		}
		return gson.toJson(new AjaxResult("/wechat/user/userCenter.action", null));
	}

	@RequestMapping(value = "/quickRegist", method = { RequestMethod.GET, RequestMethod.POST })
	public String quickRegist(HttpServletRequest request, HttpSession session, Model model, String code,
			Integer inviterUserId,String from) throws Exception {
		session.setAttribute("token", (Integer) 1);
		// model.addAttribute("code", code);
		if (inviterUserId != null) {
			User inviterUser = userService.findUserById(inviterUserId);
			model.addAttribute("inviterUser", inviterUser);
		}
		model.addAttribute("from",from);
		// //JsAPI
		// String url = request.getRequestURL().toString();
		// if (request.getQueryString() != null)
		// url += ("?" + request.getQueryString());
		// String wechatJsapiTicket =
		// WechatJsapiSignUtil.getJSApiTicket(ConfigUtil.APPID,
		// ConfigUtil.APPSECRET);
		// WechatJsapiSign wechatJsapiSign =
		// WechatJsapiSignUtil.getSign(ConfigUtil.APPID, wechatJsapiTicket,
		// url);
		// model.addAttribute("jsApiSign", wechatJsapiSign);
		return "user/quickRegist";
	}

	@RequestMapping(value = "/regist", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String regist(UserExpand userExpand, HttpSession session, Model model, String smsCode, String verifyCode,
			Integer inviterId) {
		Gson gson = new Gson();
		try {
			// 1.检查验证码是否正确
			String currentVerifyCode = (String) session.getAttribute("imageVerifyCode");
			if (verifyCode == null || currentVerifyCode == null || !verifyCode.equals(currentVerifyCode))
				return gson.toJson(new JsonResult("0", "-1", "图片验证码不正确"));

			// 2.判断短信验证码是否正确
			String savedSmsCode = (String) session.getAttribute("smsCode");
			if (savedSmsCode == null || smsCode == null || !smsCode.equals(savedSmsCode)) {
				return gson.toJson(new JsonResult("0", "-1", "短信验证码不正确"));
			}

			// 3.检查提交信息是否合法
			// 3.1 用户名验证
			String username = userExpand.getUsername();
			if (username == null || username.equals("") || !username.matches("^[a-zA-z]{1}[A-Za-z0-9_]{5,15}$")) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息: 用户名必须以字母开头并且长度在6-16位之间"));
			}
			// 判断用户名是否存在
			if (userService.usernameExist(username)) {
				return gson.toJson(new JsonResult("0", "-1", "用户名已经被注册"));
			}

			// 3.2 电话号码验证
			String phone = userExpand.getPhone();
			if (phone == null || phone.equals("") || !phone.matches("^0?(13|15|18|14|17)[0-9]{9}$")) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息: 请输入正确的手机号码"));
			}
			// 判断电话号码是否存在

			if (userService.phoneExist(phone)) {
				return gson.toJson(new JsonResult("0", "-1", "手机号已被注册"));
			}

			// 3.3 密码有效性验证
			String password = userExpand.getPassword();
			if (password == null || password.equals("") || !password.matches(
					"^((?=.*[a-zA-Z])(?=.*[\\d])|(?=.*[a-zA-Z])(?=.*[\\W])|(?=.*[\\d])(?=.*[\\W]))[a-zA-Z\\d\\W]{6,16}$")) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息: 请输入正确的手机号码"));
			}

			// 3.4 城市代码验证
			String province = userExpand.getProvince();
			String city = userExpand.getCity();
			String area = userExpand.getArea();
			if (province == null || city == null || area == null || province.equals("") || city.equals("")
					|| area.equals("")) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息: 请选择常住区域"));
			}

			// 4.注册用户
			userService.registUser(userExpand, inviterId);

			return gson.toJson(new JsonResult("0", "0"));
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new JsonResult("0", "-1", "网络异常，请稍候重试."));
		}
	}

	@RequestMapping(value = "/getMoneyAccountRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getMoneyAccountRecord(Integer userId, Model model) throws Exception {
		List<AccountRecordExpand> accountRecordExpands = userService.getMoneyAccountRecord(userId);
		if (accountRecordExpands != null && accountRecordExpands.size() > 0)
			model.addAttribute("accountRecordExpands", accountRecordExpands);
		return "user/moneyAccountRecord";
	}

	@RequestMapping(value = "/getJfAccountRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getJfAccountRecord(Integer userId, Model model) throws Exception {
		List<AccountRecordExpand> accountRecordExpands = userService.getJfAccountRecord(userId);
		if (accountRecordExpands != null && accountRecordExpands.size() > 0)
			model.addAttribute("accountRecordExpands", accountRecordExpands);
		return "user/jfAccountRecord";
	}

	@RequestMapping(value = "/getDlbAccountRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getDlbAccountRecord(Integer userId, Model model) throws Exception {
		List<AccountRecordExpand> accountRecordExpands = userService.getDlbAccountRecord(userId);
		if (accountRecordExpands != null && accountRecordExpands.size() > 0)
			model.addAttribute("accountRecordExpands", accountRecordExpands);
		return "user/dlbAccountRecord";
	}

	@RequestMapping(value = "/getYljAccountRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getYljAccountRecord(Integer userId, Model model) throws Exception {
		List<PlatformYljSavedRecordExpand> accountRecordExpands = userService.getYljAccountRecord(userId);
		if (accountRecordExpands != null && accountRecordExpands.size() > 0)
			model.addAttribute("accountRecordExpands", accountRecordExpands);
		return "user/yljAccountRecord";
	}

	@RequestMapping(value = "/getSyAccountRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSyAccountRecord(Integer userId, Model model) throws Exception {
		List<PlatformSyDistributedRecordExpand> accountRecordExpands = userService.getSyAccountRecord(userId);
		if (accountRecordExpands != null && accountRecordExpands.size() > 0)
			model.addAttribute("accountRecordExpands", accountRecordExpands);
		return "user/syAccountRecord";
	}

	@RequestMapping(value = "/userNearbyStores", method = { RequestMethod.GET, RequestMethod.POST })
	public String userNearbyStores(HttpServletRequest request, Model model) throws Exception {
		String url = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			url += ("?" + request.getQueryString());
		String wechatJsapiTicket = WechatJsapiSignUtil.getJSApiTicket(ConfigUtil.APPID, ConfigUtil.APPSECRET);
		WechatJsapiSign wechatJsapiSign = WechatJsapiSignUtil.getSign(ConfigUtil.APPID, wechatJsapiTicket, url);
		model.addAttribute("jsApiSign", wechatJsapiSign);
		return "user/nearbyStores";
	}

	@RequestMapping(value = "/shopDetail", method = { RequestMethod.GET, RequestMethod.POST })
	public String shopDetail(HttpSession session, Model model, Integer mallId) throws Exception {
		ShoppingMallExpand shoppingMallExpand = userService.findAllShppingMallById(mallId);
		model.addAttribute("mall", shoppingMallExpand);
		return "user/shopDetail";
	}

	class PageObj {
		Integer totalCount;
		Object objList;

		public PageObj() {
		}

		public PageObj(Integer totalCount, Object objList) {
			this.totalCount = totalCount;
			this.objList = objList;
		}
	}

	@RequestMapping(value = "/getTypeListAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getTypeListAjax(HttpSession session, Model model, Integer pageNo, Integer pageSize, String searchKey,
			/* String orderStatusSelect, */ Integer typeSeqOrderBy, Integer orderTimeOrderBy) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");

		List<ShoppingMallType> shoppingMallTypes = null;
		Integer totalNum = 0;

		ShoppingMallTypeQueryVo shoppingMallTypeQueryVo = new ShoppingMallTypeQueryVo();
		List<QueryConditionAbstractItem> whereCondList = new ArrayList<QueryConditionAbstractItem>();
		if (!StringUtils.isEmpty(searchKey)) {
			whereCondList
					.add(new QueryConditionItem("t_shopping_mall_type.typeName", searchKey, QueryConditionOp.LIKE));
		}
		try {
			shoppingMallTypeQueryVo.setQueryCondition(new QueryCondition(whereCondList));
			totalNum = mallService.getMallTypesTotalNum(shoppingMallTypeQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> orderByMap = new HashMap<>();
		if (typeSeqOrderBy != null) {
			if (typeSeqOrderBy == 0) {
				orderByMap.put("t_shopping_mall_type.sequence", "desc");
			} else {
				orderByMap.put("t_shopping_mall_type.sequence", "asc");
			}
		}
		try {
			shoppingMallTypeQueryVo.setPagination(new Pagination(pageSize, pageNo, 0, orderByMap));
			shoppingMallTypes = mallService.getMallTypes(shoppingMallTypeQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (totalNum == null)
			totalNum = 0;
		jsonResult.resultObj = new PageObj(totalNum, shoppingMallTypes);
		return gson.toJson(jsonResult);
	}

	// @RequestMapping(value = "/userNearbyStoresResult", method = {
	// RequestMethod.GET, RequestMethod.POST })
	// public String userNearbyStoresResult(HttpSession session, Model model,
	// WechatPosition wechatPos, Integer distance,
	// Integer page, String mallType) throws Exception {
	// final int countPerPage = 20; // 每页显示20条
	//
	// List<ShoppingMallExpand> sortedMalls = new
	// ArrayList<ShoppingMallExpand>();
	// List<ShoppingMallExpand> malls = new ArrayList<ShoppingMallExpand>();
	// List<ShoppingMallExpand> allMalls = null;
	// // allMalls = userService.findAllShppingMalls();
	//
	// ShoppingMallExpandQueryVo shoppingMallExpandQueryVo = new
	// ShoppingMallExpandQueryVo();
	// List<QueryConditionAbstractItem> whereCondList = new
	// ArrayList<QueryConditionAbstractItem>();
	// if (mallType != null && !mallType.equals("-2")) {
	// whereCondList.add(new QueryConditionItem("mall.mallTypeId", mallType,
	// QueryConditionOp.EQ));
	// }
	// shoppingMallExpandQueryVo.setQueryCondition(new
	// QueryCondition(whereCondList));
	// allMalls = mallService.getMallList(shoppingMallExpandQueryVo);
	// //System.out.println("allMalls:" + allMalls);
	//
	// if (distance == null)
	// distance = 0;
	// if (page == null)
	// page = 1;
	//
	// // 所有商城
	// distance = 2000000000;
	// if (allMalls != null && allMalls.size() != 0) {
	// for (ShoppingMallExpand mall : allMalls) {
	// WechatPosition mallPos = new WechatPosition(mall.getMallPos_lat(),
	// mall.getMallPos_lnt());
	// double dis = Math.abs(WechatUtil.getDistance(wechatPos, mallPos));
	// System.out.println(dis + "\t" + distance);
	// // BigDecimal bg = new BigDecimal(dis / 1000);
	// // double f1 = bg.setScale(2,
	// // BigDecimal.ROUND_HALF_UP).doubleValue();
	// mall.setDistance(PreciseComputeUtil.round(dis / 1000, 2));
	// if (dis <= distance) {
	// sortedMalls.add(mall);
	// }
	// }
	// //System.out.println("sortedMalls-1:" + sortedMalls);
	// java.util.Collections.sort(sortedMalls);
	// //System.out.println("sortedMalls-2:" + sortedMalls);
	// int totalSize = sortedMalls.size();
	// malls.addAll(sortedMalls.subList((page - 1) * countPerPage,
	// page * countPerPage <= totalSize ? page * countPerPage : totalSize)); //
	// 每页10条记录
	// //System.out.println("malls:" + malls);
	// if (malls.size() > 0)
	// model.addAttribute("malls", malls);
	// }
	// // 总的记录数
	// model.addAttribute("mallSize", sortedMalls.size());
	// // 当前位置信息
	// model.addAttribute("wechatPos", wechatPos);
	// model.addAttribute("distance", distance);
	// return "user/nearbyStoresResult";
	// }
	@RequestMapping(value = "/userNearbyStoresResult", method = { RequestMethod.GET, RequestMethod.POST })
	public String userNearbyStoresResult(HttpSession session, Model model, WechatPosition wechatPos, Integer distance,
			Integer page, String mallType) throws Exception {
		distance = 2000000000;
		// 当前位置信息
		model.addAttribute("wechatPos", wechatPos);
		model.addAttribute("distance", distance);
		return "user/nearbyStoresResult";
	}

	class UserNearbyStoresResultJson {
		private int count;
		private List<ShoppingMallExpand> sets;
	}

	@RequestMapping(value = "/userNearbyStoresResultPage", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String userNearbyStoresResultPage(HttpSession session, Model model, WechatPosition wechatPos,
			Integer distance, Integer page, String mallType) throws Exception {
		Gson gson = new Gson();
		UserNearbyStoresResultJson jsobObj = new UserNearbyStoresResultJson();
		final int countPerPage = 20; // 每页显示20条
		Integer totalCount = 0;

		// System.out.println(wechatPos);
		// System.out.println(distance);
		// System.out.println(page);

		List<ShoppingMallExpand> sortedMalls = new ArrayList<ShoppingMallExpand>();
		List<ShoppingMallExpand> malls = new ArrayList<ShoppingMallExpand>();
		List<ShoppingMallExpand> allMalls = null;
		// allMalls = userService.findAllShppingMalls();

		ShoppingMallExpandQueryVo shoppingMallExpandQueryVo = new ShoppingMallExpandQueryVo();
		List<QueryConditionAbstractItem> whereCondList = new ArrayList<QueryConditionAbstractItem>();
		if (mallType != null && !mallType.equals("-2")) {
			whereCondList.add(new QueryConditionItem("mall.mallTypeId", mallType, QueryConditionOp.EQ));
		}
		whereCondList.add(new QueryConditionItem("mall.locked", 0 + "", QueryConditionOp.EQ));
		shoppingMallExpandQueryVo.setQueryCondition(new QueryCondition(whereCondList));
		totalCount = mallService.getMallListCount(shoppingMallExpandQueryVo);
		allMalls = mallService.getMallList(shoppingMallExpandQueryVo);
		// System.out.println("allMalls:" + allMalls);

		if (distance == null)
			distance = 0;
		if (page == null)
			page = 1;

		// 所有商城
		distance = 2000000000;
		if (allMalls != null && allMalls.size() != 0) {
			for (ShoppingMallExpand mall : allMalls) {
				if (mall.getMallPos_lat() == null || mall.getMallPos_lnt() == null) {
					continue;
				}
				WechatPosition mallPos = new WechatPosition(mall.getMallPos_lat(), mall.getMallPos_lnt());
				double dis = Math.abs(WechatUtil.getDistance(wechatPos, mallPos));
				System.out.println(dis + "\t" + distance);
				mall.setDistance(PreciseComputeUtil.round(dis / 1000, 2));
				if (dis <= distance) {
					sortedMalls.add(mall);
				}
			}
			// System.out.println("sortedMalls-1:" + sortedMalls);
			java.util.Collections.sort(sortedMalls);
			// System.out.println("sortedMalls-2:" + sortedMalls);
			int totalSize = sortedMalls.size();
			malls.addAll(sortedMalls.subList((page - 1) * countPerPage,
					page * countPerPage <= totalSize ? page * countPerPage : totalSize)); // 每页10条记录
			// System.out.println("malls:" + malls);
			jsobObj.count = totalCount;
			jsobObj.sets = malls;
		}
		return gson.toJson(jsobObj);
	}

	@RequestMapping(value = "/recharge", method = { RequestMethod.GET, RequestMethod.POST })
	public String recharge(HttpServletRequest request, HttpServletResponse response, HttpSession session, String code,
			String state, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "user/403";
		}
		RoleExpand roleExpand = userService.findRoleByUserId(user.getId());
		user.setRole(roleExpand);
		if (!user.getRole().getRoleName().equals("商家")) {// 商家
			return "user/403";
		}

		String url = request.getRequestURL().toString();
		if (request.getQueryString() != null)
			url += ("?" + request.getQueryString());
		String wechatJsapiTicket = WechatJsapiSignUtil.getJSApiTicket(ConfigUtil.APPID, ConfigUtil.APPSECRET);
		WechatJsapiSign wechatJsapiSign = WechatJsapiSignUtil.getSign(ConfigUtil.APPID, wechatJsapiTicket, url);
		model.addAttribute("jsApiSign", wechatJsapiSign);
		model.addAttribute("code", code);
		return "user/recharge";
		// request.setAttribute("code", code);
		// request.getRequestDispatcher("/paytest/recharge.jsp").forward(request,
		// response);
	}

	@RequestMapping(value = "/paySuccess", method = { RequestMethod.GET, RequestMethod.POST })
	public String paySuccess(HttpServletRequest request, Model model) throws Exception {
		return "user/paySuccess";
	}

	@RequestMapping(value = "/withdraws", method = { RequestMethod.GET, RequestMethod.POST })
	public String withdraws(HttpServletRequest request, HttpSession session, String code, String state, Model model)
			throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "userCenter";
		}
		Double balance = userService.getMoneyAccountBalance(user.getId());
		Double canMoney = balance - balance % 100;
		model.addAttribute("code", code);
		model.addAttribute("balance", balance);
		model.addAttribute("canMoney", canMoney);
		return "user/withdraws";
	}

	@RequestMapping(value = "/withdrawsSubmit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String withdrawsSubmit(HttpServletRequest request, HttpSession session, String txMoney, Model model)
			throws Exception {
		Gson gson = new Gson();
		JsonResult jsonResult = new JsonResult("0", "0", "提现成功");
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = "页面超时，请重新登录";
			return gson.toJson(jsonResult);
		}
		
		// 找到对应的UserExtra
		UserExtra userExtra = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("e.userId", user.getId() + "", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		try {
			userExtra = userExtraService.findUserExtra(userExtraQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = e.getMessage();
			return gson.toJson(jsonResult);
		}
		if (userExtra == null) {
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = "找不到对应的UserExtra";
			return gson.toJson(jsonResult);
		}
					
		System.out.println("WithdrawLimit: " + userExtra.getWithdrawLimit());

		// 判断角色，商家不允许提现
		RoleExpand roleExpand = userService.findRoleByUserId(user.getId());
		if (roleExpand != null && roleExpand.getId() == 1) { // 商家
			jsonResult.logicCode = "-9";
			jsonResult.resultMsg = "商家不允许提现";
			return gson.toJson(jsonResult);
		}

		// 判断参数设置是否允许提现
		GlobalPrams globalParams = globalParamService.findCurrentGlobalParams();
		System.out.println("current globalParams: " + globalParams.getWithdrawFlag());
		if (globalParams != null) {
			if (globalParams.getWithdrawFlag() == 0) {
				jsonResult.logicCode = "-9";
				jsonResult.resultMsg = "系统内部维护，提现功能暂停使用，恢复时间请关注微信群公告.";
				return gson.toJson(jsonResult);
			}
		}

		if (txMoney == null || !txMoney.matches("^\\d*$")) {
			jsonResult.logicCode = "-2";
			jsonResult.resultMsg = "提现金额异常";
			return gson.toJson(jsonResult);
		}

		synchronized (user) {
			Double txMoneyVal = Double.valueOf(txMoney);
			Double balance = userService.getMoneyAccountBalance(user.getId());
			Double canMoney = balance - balance % 100;
			if (txMoneyVal > canMoney || txMoneyVal <= 0 || (txMoneyVal % 100 != 0)) {
				jsonResult.logicCode = "-3";
				jsonResult.resultMsg = "提现金额不正确";
				return gson.toJson(jsonResult);
			}

			//全局控制
			/*if (txMoneyVal > globalParams.getWithdrawUpLimit()) {
				jsonResult.logicCode = "-3";
				jsonResult.resultMsg = "单次提现金额不能超过 ￥" + globalParams.getWithdrawUpLimit();
				return gson.toJson(jsonResult);
			}*/
			
			
			if (txMoneyVal > userExtra.getWithdrawLimit()) {
				jsonResult.logicCode = "-3";
				jsonResult.resultMsg = "单次提现金额不能超过 ￥" + userExtra.getWithdrawLimit();
				return gson.toJson(jsonResult);
			}

			/*
			 * // 1.提现条件判断 if (userService.getCashFlag(user.getId()) != 0) {
			 * jsonResult.logicCode = "-3"; jsonResult.resultMsg =
			 * "本月交易行为不满足提现条件，提现功能暂停使用！"; return gson.toJson(jsonResult); }
			 */

			// 3.提现成功，判断是否第一次提现
			// if (userService.getWithDrawRecordCounts(user.getId()) > 0) {
			// if (userService.getCurrentMonthConsumeRecordCounts(user.getId())
			// < 3)
			// {
			// user.setGetCashFlag(1);
			// userService.setCashFlag(user);
			// jsonResult.logicCode = "0";
			// jsonResult.resultMsg = "提现成功，但由于本月交易行为不满足提现条件，提现功能暂停使用！";
			// }
			// }

			if (userService.getWithDrawRecordCounts(user.getId()) > 0) { // 不是第一次提现
				// JournalBookExpand firstJournalBookExpand =
				// userService.findFirstJournalBookByClientId(user.getId());
				RechargeAndWithDrawRecordExpand firstWithDrawRecord = userService.findFirstWithDrawRecord(user.getId());
				if (firstWithDrawRecord == null) {
					jsonResult.logicCode = "-1";
					jsonResult.resultMsg = "提现失败：找不到任何提现记录.";
					return gson.toJson(jsonResult);
				}
				Date date = firstWithDrawRecord.getOperateTime();
				Date now = new Date();
				System.out.println("now is :" + now);
				System.out.println("date is : " + date);
				if (date.getYear() != now.getYear() || date.getMonth() != now.getMonth()) { // 非首次报单一月之内
					if (userService.getCurrentMonthConsumeRecordCounts(user.getId()) < 3) {
						jsonResult.logicCode = "-1";
						jsonResult.resultMsg = "提现失败：本月交易行为(次数)不满足提现条件.";
						return gson.toJson(jsonResult);
					}
				}
				// 获取最后一次取现记录
				RechargeAndWithDrawRecordExpand lastWithDrawRecord = userService.findLastWithDrawRecord(user.getId());
				// 判断距离现在是否小于7天
				date = lastWithDrawRecord.getOperateTime();
				System.out.println("now is :" + now);
				System.out.println("date is : " + date);
				System.out.println("-----------" + now.getYear() + "-" + now.getMonth() + "-" + now.getDate());
				System.out.println("-----------" + date.getYear() + "-" + date.getMonth() + "-" + date.getDate());
				if (DateUtil.getIntervalDays(date, now) < 7) {
					jsonResult.logicCode = "-1";
					jsonResult.resultMsg = "提现失败：距离最后一次提现时间不足7天！";
					return gson.toJson(jsonResult);
				}
			}

			/*
			 * Double serviceMoney = txMoneyVal <= 1000 ? 2 : (txMoneyVal > 1000
			 * && txMoneyVal <= 10000) ? txMoneyVal * 0.005 : txMoneyVal *
			 * 0.003;
			 */
			Double serviceMoney = txMoneyVal * 0.02;
			System.out.println(serviceMoney);

			Date date = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String time = format.format(date);
			format = new SimpleDateFormat("MMddHHmmss");
			String rand = format.format(date);
			String orderNo = ConfigUtil.MCH_ID + time + rand;
			String clientIp = IpAddressUtil.getIpAddr(request);
			RechargeAndWithDrawRecordExpand record = new RechargeAndWithDrawRecordExpand(ConfigUtil.TradeType.WITHDRAW,
					orderNo, txMoneyVal, serviceMoney, ConfigUtil.TradeState.PAYPENDING, clientIp);
			record.setUser(user);
			userService.userWithDraw(record);

			if (!withDrawToWx(user, orderNo, clientIp, (txMoneyVal - serviceMoney))) {
				jsonResult.logicCode = "-4";
				jsonResult.resultMsg = "服务器暂时无法完成提现操作";
				return gson.toJson(jsonResult);
			}
		}
		return gson.toJson(jsonResult);
	}

	public String HttpsCret(String url, String data, String cretPath, String cretPasswd) throws Exception {
		// String cretPath =
		// "/usr/local/apache-tomcat-7.0.70/webapps/wechat/WEB-INF/cert/apiclient_cert.p12";
		// String cretPawd = "";
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(cretPath));
		keyStore.load(instream, cretPasswd.toCharArray());
		instream.close();
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, cretPasswd.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		HttpPost httpost = new HttpPost(url); // 璁剧疆鍝嶅簲澶翠俊鎭?
		httpost.addHeader("Connection", "keep-alive");
		httpost.addHeader("Accept", "*/*");
		httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpost.addHeader("Host", "api.mch.weixin.qq.com");
		httpost.addHeader("X-Requested-With", "XMLHttpRequest");
		httpost.addHeader("Cache-Control", "max-age=0");
		httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
		httpost.setEntity(new StringEntity(data, "UTF-8"));
		CloseableHttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		String str = EntityUtils.toString(entity, "UTF-8");
		EntityUtils.consume(entity);
		return str;
	}

	private boolean withDrawToWx(UserExpand user, String orderNo, String clientIp, double txMoney) throws Exception {
		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
		SortedMap<Object, Object> params = new TreeMap<Object, Object>();
		parameters.put("wxappid", ConfigUtil.APPID);
		parameters.put("mch_id", ConfigUtil.MCH_ID);
		parameters.put("nonce_str", WechatJsapiSignUtil.create_nonce_str());

		parameters.put("mch_billno", orderNo);
		parameters.put("send_name", ConfigUtil.BUSINESSNAME);
		parameters.put("re_openid", user.getOpenId());
		parameters.put("total_amount", (int) txMoney * 100 + "");
		// parameters.put("re_user_name", "NO_CHECK");
		parameters.put("total_num", 1 + "");
		parameters.put("wishing", "用户提现");
		parameters.put("client_ip", clientIp);
		parameters.put("act_name", "act_name");
		parameters.put("remark", "remark");
		parameters.put("scene_id", "PRODUCT_1");
		String sign = WechatJsapiSignUtil.createPaySign(ConfigUtil.PAYKEY, "UTF-8", parameters);
		parameters.put("sign", sign);
		String data = WechatJsapiSignUtil.getRequestXml(parameters);
		System.out.println("---------requestXML: ------" + data);

		String result = HttpsCret(ConfigUtil.SEND_RED_PACKT_URL, data, ConfigUtil.CRETPATH, ConfigUtil.CRETPASSWD);
		System.out.println("----result---->" + result);
		Map<String, String> map = XMLUtil.doXMLParse(result);// 解析微信返回的信息，以Map形式存储便于取值
		// 失败，提示服务器重新发送
		if (map.get("return_code").toString().equalsIgnoreCase("FAIL")
				|| map.get("result_code").toString().equalsIgnoreCase("FAIL")) {// 通信失败
			return false;
		}

		// 获取订单状态
		RechargeAndWithDrawRecordExpand record = userService
				.findRechargeRecordByOrderNo((String) map.get("mch_billno"));
		if (record == null) {
			// 报警.
			return false;
		}

		// 查看该订单是否已经处理过
		if (record.getOperateState() != ConfigUtil.TradeState.PAYPENDING.ordinal()) {// 已经处理过了
			return false;
		}

		// 获取交易结果
		if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
			record.setOperateState(ConfigUtil.TradeState.PAYSUCCESS.ordinal());
		} else {
			record.setOperateState(ConfigUtil.TradeState.PAYERROR.ordinal());
		}
		userService.updateWithDrawRecordState(record);
		return true;
	}

	@RequestMapping(value = "/withDrawsRecords", method = { RequestMethod.GET, RequestMethod.POST })
	public String withDrawsRecords(HttpServletRequest request, HttpSession session, String txMoney, Model model)
			throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "forward: userCenter.action";
		}

		List<RechargeAndWithDrawRecordExpand> records = userService.findAllWithDrawsRecords(user.getId());
		if (records != null && records.size() > 0)
			model.addAttribute("records", records);
		return "user/withDrawsRecord";
	}

	@RequestMapping(value = "/offlineConsumeRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String offlineConsumeRecord(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "forward: userCenter.action";
		}

		List<JournalBookExpand> journalBookExpands = userService.findAllJournalBookRecordsByClientId(user.getId());
		if (journalBookExpands != null && journalBookExpands.size() > 0)
			model.addAttribute("records", journalBookExpands);
		return "user/offlineConsumeRecord";
	}

	@RequestMapping(value = "/onlineConsumeRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String onlineConsumeRecord(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "forward: userCenter.action";
		}

		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		Integer totalNum = null;
		try {
			totalNum = memberService.getOnlineConsumeRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;

		if (totalNum > 0) {
			userExpandQueryVo.setPagination(new Pagination(15, 1, null, "{\"journalTime\":\"desc\"}"));
			List<OnlineJournalBookItem> onlineJournalBookItems = null;
			try {
				onlineJournalBookItems = memberService.getOnlineConsumeRecord(userExpandQueryVo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (onlineJournalBookItems != null && onlineJournalBookItems.size() > 0)
				model.addAttribute("records", onlineJournalBookItems);
		}
		return "user/onlineConsumeRecord";
	}

	@RequestMapping(value = "/getJdbChangeRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String getJdbChangeRecord(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "forward: userCenter.action";
		}

		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		Integer totalNum = null;
		try {
			totalNum = memberService.getJdbAccountRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;

		if (totalNum > 0) {
			userExpandQueryVo.setPagination(new Pagination(15, 1, null, "{\"operateTime\":\"desc\"}"));
			List<AccountRecordExpand> jdbAccountRecordExpands = null;
			try {
				jdbAccountRecordExpands = memberService.getJdbAccountRecord(userExpandQueryVo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (jdbAccountRecordExpands != null && jdbAccountRecordExpands.size() > 0)
				model.addAttribute("accountRecordExpands", jdbAccountRecordExpands);
		}
		return "user/jdbAccountRecord";
	}
}
