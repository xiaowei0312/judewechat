package com.sxsram.ssm.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxsram.ssm.entity.CommodityType;
import com.sxsram.ssm.entity.GlobalPrams;
import com.sxsram.ssm.entity.Indent;
import com.sxsram.ssm.entity.JournalBookExpand;
import com.sxsram.ssm.entity.RoleExpand;
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.service.UserService;
import com.sxsram.ssm.util.ConfigUtil;
import com.sxsram.ssm.util.WechatUtil;
import com.sxsram.ssm.util.WxTemplateData;
import com.sxsram.ssm.util.WxTemplateDataEntry;
import com.sxsram.ssm.util.WxTemplateEntity;

@Controller()
@RequestMapping(value = "/user", method = { RequestMethod.GET, RequestMethod.POST })
public class SellerController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = "/sellerReportOrder", method = { RequestMethod.GET, RequestMethod.POST })
	public String sellerReportOrder(HttpSession session, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (user == null) {
			return "user/403";
		}
		RoleExpand roleExpand = userService.findRoleByUserId(user.getId());
		user.setRole(roleExpand);
		if (!user.getRole().getRoleName().equals("商家")) {// 商家
			return "user/403";
		}
		GlobalPrams globalParams = userService.findGlobalParams();
		if (globalParams == null) {
			System.out.println("服务器无法找到参数配置.");
			return "user/403";
		}
		System.out.println("++++++ " + globalParams.getVipJfRatio() + "\t" + globalParams.getBusJfRatio());
		model.addAttribute("globalParams",globalParams);
		return "user/sellerReportOrder";
	}

	class JsonResult {
		public String resultCode;
		public String logicCode;
		public String resultMsg;
		public double balance;
		public boolean valid;
		public String username;
		public String name;
		public Object resultObj;

		public JsonResult(String resultCode, String logicCode, String resultMsg) {
			super();
			this.resultCode = resultCode;
			this.logicCode = logicCode;
			this.resultMsg = resultMsg;
		}

		public JsonResult(String resultCode, String logicCode) {
			super();
			this.resultCode = resultCode;
			this.logicCode = logicCode;
		}

		public JsonResult() {
			super();
		}
	}

	@RequestMapping(value = "/offLineOrderSubmit", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String offLineOrderSubmit(HttpSession session, String reqType, String buyerSmsCode, String indentNo,
			String buyerAcct, String rewardAcct, String subType, JournalBookExpand journalBookExpand) throws Exception {
		Gson gson = new Gson();
		String json = "";
		UserExpand user = (UserExpand) session.getAttribute("user");
		if (reqType.equals("getBalance")) { // 获取账户余额
			Double balance = userService.getMoneyAccountBalance(user.getId());
			JsonResult jsonResult = new JsonResult("0", "0", "");
			jsonResult.balance = balance;
			return gson.toJson(jsonResult);
		} else if (reqType.equals("validOrderId")) { // 请正确输入订单号码
			Indent indent = userService.findIndentByIndentNo(indentNo);
			if (indent == null) {
				return gson.toJson(new JsonResult("0", "-1", "无效的订单号"));
			} else if (indent.getFlag() == 1) {
				return gson.toJson(new JsonResult("0", "-1", "该订单号已经被使用"));
			} else {
				return gson.toJson(new JsonResult("0", "0"));
			}
		} else if (reqType.equals("checkBuyer")) { // buyerAcct
			UserExpand user2 = userService.findUserByPhone(buyerAcct);
			if (user2 == null) {
				return gson.toJson(new JsonResult("0", "-1", "账户不存在"));
			} else if (user2.getCertification() == null) {
				return gson.toJson(new JsonResult("0", "-1", "该用户未进行实名认证"));
			} else {
				JsonResult jsonResult = new JsonResult("0", "0", "");
				jsonResult.username = user2.getUsername();
				jsonResult.name = user2.getCertification().getName();
				return gson.toJson(jsonResult);
			}
		} else if (reqType.equals("checkReward")) { // checkReward
			UserExpand user2 = userService.findUserByPhone(rewardAcct);
			if (user2 == null) {
				return gson.toJson(new JsonResult("0", "-1", "账户不存在"));
			} else if (user2.getCertification() == null) {
				return gson.toJson(new JsonResult("0", "-1", "该用户未进行实名认证"));
			} else {
				JsonResult jsonResult = new JsonResult("0", "0", "");
				jsonResult.username = user2.getUsername();
				jsonResult.name = user2.getCertification().getName();
				return gson.toJson(jsonResult);
			}
		} else if (reqType.equals("loadSubTypes")) { // typeid 获取商品子类型
			List<CommodityType> types = userService
					.findCommodityByParentId(journalBookExpand.getCommodityType().getId());
			JsonResult jsonResult = new JsonResult("0", "0");
			jsonResult.resultObj = types;
			return gson.toJson(jsonResult);
		} else if (reqType.equals("submit")) { // 提交表单
			UserExpand buyer = null, reward = null;

			// 0.journalBook验证
			if (journalBookExpand == null) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：请正确填写表单信息"));
			}

			// 1.indent验证
			if (indentNo == null || indentNo.length() != 7) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：请输入正确订单号"));
			}
			Indent indent = userService.findIndentByIndentNo(indentNo);
			if (indent == null || indent.getFlag() != 0) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：订单号已经被使用"));
			}

			// 2.buyer验证
			if (buyerAcct == null || buyerAcct.length() == 0) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：消费者账号不能为空"));
			}
			buyer = userService.findUserByPhone(buyerAcct);
			if (buyer == null) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：消费者账户不存在"));
			}

			// 3.商品类型验证
			if (journalBookExpand.getCommodityType() == null || subType == null || subType.length() == 0) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：请选择商品类型"));
			}

			// 4.商品名称验证
			String commodityName = journalBookExpand.getCommodityName();
			if (commodityName == null || commodityName.length() == 0 || commodityName.length() > 50) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：请正确填写商品名称"));
			}

			// 5.交易额验证
			String amount = journalBookExpand.getAmount() + "";
			if (!amount.matches("^[1-9]{1}([0-9])*(.[0-9]{1})?$")) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：交易额必须大于0并且只能有一位小数"));
			}

			// 6.商家优惠验证
			String premiumRates = journalBookExpand.getPremiumRates() + "";
			if (!premiumRates.matches("^[0-9]*\\.[0-9]$")) {
				return gson.toJson(new JsonResult("0", "-2", "服务器消息：请正确选择优惠信息"));
			}

			// 7.余额验证
			Double balance = userService.getMoneyAccountBalance(user.getId());
			double money = journalBookExpand.getAmount() * journalBookExpand.getPremiumRates() / 100;
			if (balance < money) {
				return gson.toJson(new JsonResult("0", "-2", "余额不足"));
			}

			// 8.商品类型处理
			if (subType != null && !subType.equals("")) {
				journalBookExpand.getCommodityType().setId(Integer.valueOf(subType));
			}

			// 9.奖励用户处理
			if (rewardAcct != null && rewardAcct.length() > 0) {
				reward = userService.findUserByPhone(rewardAcct);
				if (reward == null) {
					return gson.toJson(new JsonResult("0", "-2", "服务器消息：奖励用户账户不存在"));
				}
			} else {
				reward = user;
			}
			journalBookExpand.setClient(buyer);
			journalBookExpand.setBusiness(user);
			if (reward != null)
				journalBookExpand.setReward(reward);
			journalBookExpand.setIndent(indent);

			journalBookExpand.setJournalTime(new Date());

			GlobalPrams globalParams = userService.findGlobalParams();
			if (globalParams == null || globalParams.getCheckLimitAmount() == null) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息：找不到参数配置."));
			}
			if (journalBookExpand.getAmount() < globalParams.getCheckLimitAmount()) {
				journalBookExpand.setFlag(1);// 不需要审核
			} else {
				journalBookExpand.setFlag(0);// 审核
			}
			try {
				// 报单
				userService.saveNewJournalBookItem(journalBookExpand);
				// 发送推送消息
				// 发送推送消息
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				String templateId = ConfigUtil.TEMPLATEID;
				WxTemplateData wxTemplateData = new WxTemplateData();
				wxTemplateData.first = new WxTemplateDataEntry("您在聚德商城提交订单如下：", "#173177");
				wxTemplateData.keyword1 = new WxTemplateDataEntry(journalBookExpand.getAmount() + "", "#173177");
				wxTemplateData.keyword2 = new WxTemplateDataEntry("1", "#173177");
				wxTemplateData.keyword3 = new WxTemplateDataEntry(dateFormat.format(journalBookExpand.getJournalTime()),
						"#173177");
				wxTemplateData.keyword4 = new WxTemplateDataEntry(journalBookExpand.getIndent().getIndentNo(),
						"#173177");
				wxTemplateData.keyword5 = new WxTemplateDataEntry(journalBookExpand.getBusiness().getUsername(),
						"#173177");
				wxTemplateData.remark = new WxTemplateDataEntry("感谢您使用聚德公众号，积分会在平台审核通过后进行赠送", "#173177");

				if (buyer.getOpenId() != null) {
					String jsonStr = WechatUtil.responseTemplateMsg(ConfigUtil.APPID, ConfigUtil.APPSECRET,
							buyer.getOpenId(), templateId, null, wxTemplateData);
					JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
					if (jsonObject.get("errcode") == null)
						return gson.toJson(new JsonResult("0", "-2", "发送推送消息失败."));
					int code = Integer.valueOf(jsonObject.get("errcode").toString());
					if (code != 0) {
						return gson
								.toJson(new JsonResult("0", "-2", "发送推送消息失败:" + jsonObject.get("errmsg").toString()));
					}
				}
				return gson.toJson(new JsonResult("0", "0"));
			} catch (Exception e) {
				e.printStackTrace();
				return gson
						.toJson(new JsonResult("0", "-2", "报单失败：网络异常，请稍候重试."));
			}
		}
		return json;
	}

	@RequestMapping(value = "/submitOrderRecord", method = { RequestMethod.GET, RequestMethod.POST })
	public String submitOrderRecord(HttpSession session, Model model) throws Exception {
		UserExpand user = (UserExpand) session.getAttribute("user");
		List<JournalBookExpand> journalBookExpands = userService.findAllJournalBookRecordsByUserId(user.getId());
		if (journalBookExpands != null && journalBookExpands.size() > 0)
			model.addAttribute("records", journalBookExpands);
		return "user/sellerReportOrderRecord";
	}
}
