package com.sxsram.ssm.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sxsram.ssm.entity.AccountRecordExpand;
import com.sxsram.ssm.entity.Address;
import com.sxsram.ssm.entity.JournalBookExpand;
import com.sxsram.ssm.entity.OnlineJournalBookItem;
import com.sxsram.ssm.entity.PlatformSyDistributedRecordExpand;
import com.sxsram.ssm.entity.RoleExpand;
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.UserExpandQueryVo;
import com.sxsram.ssm.service.MemberService;
import com.sxsram.ssm.service.UserService;
import com.sxsram.ssm.util.ConfigUtil;
import com.sxsram.ssm.util.FileUtil;
import com.sxsram.ssm.util.JsonResult;
import com.sxsram.ssm.util.Pagination;
import com.sxsram.ssm.util.SmsUtil;

@Controller("/member")
public class MemberController {
	private String smsText = ConfigUtil.REGISTSMSTEXT;
	@Autowired
	private MemberService memberService;
	@Autowired
	private UserService userService;

	// 创建颜色
	Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	@RequestMapping(value = "/imgVerifyCodeAjax")
	public void getImageVerifyCode(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		int width = 63;
		int height = 37;
		Random random = new Random();
		// 设置response头信息
		// 禁止缓存
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		// 生成缓冲区image类
		BufferedImage image = new BufferedImage(width, height, 1);
		// 产生image类的Graphics用于绘制操作
		Graphics g = image.getGraphics();
		// Graphics类的样式
		g.setColor(this.getRandColor(200, 250));
		g.setFont(new Font("Times New Roman", 0, 28));
		g.fillRect(0, 0, width, height);
		// 绘制干扰线
		for (int i = 0; i < 40; i++) {
			g.setColor(this.getRandColor(130, 200));
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int x1 = random.nextInt(12);
			int y1 = random.nextInt(12);
			g.drawLine(x, y, x + x1, y + y1);
		}

		// 绘制字符
		String strCode = "";
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			strCode = strCode + rand;
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 13 * i + 6, 28);
		}
		// 将字符保存到session中用于前端的验证
		session.setAttribute("imageVerifyCode", strCode);
		g.dispose();

		ImageIO.write(image, "JPEG", response.getOutputStream());
		response.getOutputStream().flush();
	}

	@RequestMapping(value = "/exit", method = { RequestMethod.GET })
	public String userExit(HttpSession session, Model model) {
		session.removeAttribute("user");
		return "redirect:/main/index";
	}

	@RequestMapping(value = "/registPage", method = { RequestMethod.GET })
	public String userRegistPage(HttpSession session, Model model) {
		return "regist";
	}

	@RequestMapping(value = "/registerValidate", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String registerValidate(HttpSession session, String reqType, String lostPwdFlag, String addrId,
			String username, String phone, String verifyCode) throws Exception {
		Gson gson = new Gson();
		String json = "";
		List<Address> addresses = null;
		if (reqType.equals("loadEparchy")) {
			addresses = memberService.findEparchies(Integer.valueOf(addrId));
			json = gson.toJson(addresses);
		} else if (reqType.equals("queryYHXY")) {
			String userProtocol = FileUtil.readFileContent(ConfigUtil.PATH_USER_PROTOCOL);
			json = gson.toJson(new JsonResult("0", "0", userProtocol));
		} else if (reqType.equals("loadCity")) {
			addresses = memberService.findCities(Integer.valueOf(addrId));
			json = gson.toJson(addresses);
		} else if (reqType.equals("validAcct")) { // 验证账户是否已经存在
			if (memberService.usernameExist(username)) {
				json = gson.toJson(new JsonResult("0", "1", "用户名已被注册"));
			} else {
				json = gson.toJson(new JsonResult("0", "0"));
			}
		} else if (reqType.equals("validPhone")) { // 验证手机号是否已经存在
			if (memberService.phoneExist(phone)) {
				json = gson.toJson(new JsonResult("0", "1", "手机号已被注册"));
			} else {
				json = gson.toJson(new JsonResult("0", "0"));
			}
		} else if (reqType.equals("validMsg")) { // 发送手机验证码
			if (lostPwdFlag == null) {
				// 1.检查验证码是否正确
				String currentVerifyCode = (String) session.getAttribute("imageVerifyCode");
				if (verifyCode == null || currentVerifyCode == null || !verifyCode.equals(currentVerifyCode)) {
					json = gson.toJson(new JsonResult("0", "-2", "验证码错误"));
					return json;
				}
			}
			// 2.检查是否时间不够120s
			Long previousTimeSeconds = (Long) session.getAttribute("smsSendTime");
			long nowTimeSeconds = System.currentTimeMillis() / 1000;
			if (previousTimeSeconds != null && (nowTimeSeconds - previousTimeSeconds) < 120) {
				json = gson.toJson(new JsonResult("0", "-4"));
			} else {
				String smsCode = createRandom(true, 6);
				if (sendSMS(phone, smsText.replaceAll("SMSCODE", smsCode)) != 0) {
					json = gson.toJson(new JsonResult("0", "-3", "短信发送失败"));
				} else {
					session.setAttribute("smsCode", smsCode);
					session.setAttribute("smsSendTime", System.currentTimeMillis() / 1000);
					json = gson.toJson(new JsonResult("0", "0"));
				}
			}
		}
		return json;
	}

	private String createRandom(boolean numberFlag, int length) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}

	private int sendSMS(String phone, String smsMsg) throws Exception {
		System.out.println(phone + ":" + smsMsg);
		String account = ConfigUtil.SMSUSERNAME;// 账号
		String pswd = ConfigUtil.SMSPASSWORD;// 密码
		String mobile = phone;// 手机号码，多个号码使用","分割
		String msg = smsMsg;// 短信内容
		String returnString = SmsUtil.SubmitSms(account, pswd, mobile, msg, "1001");
		System.out.println("短发发送状态：" + returnString);

		if (returnString.matches("[0-9]*"))
			return 0;
		return -1;
	}

	@RequestMapping(value = "/registSubmitAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String regist(UserExpand userExpand, HttpServletRequest request, HttpSession session, Model model,
			String smsCode, String verifyCode) {
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
			if (memberService.usernameExist(username)) {
				return gson.toJson(new JsonResult("0", "-1", "用户名已经被注册"));
			}

			// 3.2 电话号码验证
			String phone = userExpand.getPhone();
			if (phone == null || phone.equals("") || !phone.matches("^0?(13|15|18|14|17)[0-9]{9}$")) {
				return gson.toJson(new JsonResult("0", "-1", "服务器消息: 请输入正确的手机号码"));
			}
			// 判断电话号码是否存在

			if (memberService.phoneExist(phone)) {
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
			memberService.registUser(userExpand);

			// 5.自动登录
			session.setAttribute("user", userExpand);
			return gson.toJson(new JsonResult("0", "0", "", null, request.getContextPath() + "/center"));
		} catch (Exception e) {
			e.printStackTrace();
			return gson.toJson(new JsonResult("0", "-1", "网络异常，请稍候重试."));
		}
	}

	@RequestMapping(value = "/loginPage", method = { RequestMethod.GET, RequestMethod.POST })
	public String userLoginPage(HttpSession session, Model model) {
		return "login";
	}

	@RequestMapping(value = "/loginSubmitAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String userLogin(HttpSession session, HttpServletRequest request, Model model, UserExpand inputUser,
			String verifyCode) throws Exception {
		Gson gson = new Gson();
		JsonResult jsonResult = new JsonResult("0", "0");

		// 1.检查验证码是否正确
		String currentVerifyCode = (String) session.getAttribute("imageVerifyCode");
		if (verifyCode == null || currentVerifyCode == null || !verifyCode.equals(currentVerifyCode)) {
			jsonResult.logicCode = "1";
			jsonResult.resultMsg = "验证码不正确";
			return gson.toJson(jsonResult);
		}

		// 2.检查用户名(电话) 密码是否正确
		UserExpand user = memberService.findUserWhenLogin(inputUser);
		if (user == null) {
			jsonResult.logicCode = "1";
			jsonResult.resultMsg = "用户名或者密码不正确";
			return gson.toJson(jsonResult);
		}

		// 3. 登录成功
		session.setAttribute("user", user);
		jsonResult.url = request.getContextPath() + "/center";
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/center", method = { RequestMethod.GET })
	public String userCenter(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		RoleExpand roleExpand = null;

		// 1. 如果找到，根据id查找账户信息
		UserExpand userExpand = null;
		try {
			// 获取用户角色信息
			roleExpand = memberService.findRoleByUserId(user.getId());
			// 获取用户帐户信息（当前）
			userExpand = memberService.getUserAccountInfo(user.getId() + "");
			/**
			 * 获取用户累计信息
			 */
			userExpand.setTotalConsumOffline(memberService.getTotalConsumeOffline(user.getId()));
			userExpand.setTotalConsumOnline(memberService.getTotalConsumeOnline(user.getId()));
			userExpand.setTotalRecharge(memberService.getTotalRecharge(user.getId()));
			userExpand.setTotalWithDraw(memberService.getTotalWithDraws(user.getId()));
			userExpand.setTotalRewardJf(memberService.getTotalRewardJf(user.getId()));
			userExpand.setTotalTransferDlb(memberService.getTotalTransferDlb(user.getId()));
			userExpand.setTotalRewardMoney(memberService.getTotalRewardMoney(user.getId()));
			userExpand.setTotalRewardJdb(memberService.getTotalRewardJdb(user.getId()));
			userExpand.setTotalRewardYlj(memberService.getTotalRewardYlj(user.getId()));
			userExpand.setTotalTransferYlj(memberService.getTotalTransferYlj(user.getId()));
			if (roleExpand.getRoleName().equals("商家"))
				userExpand.setTotalSumbitOrderValue(memberService.getTotalSubmitOrderValue(user.getId()));

			/**
			 * 近期收益
			 * 
			 */
			UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
			userExpandQueryVo.setId(user.getId());
			userExpandQueryVo.setPagination(new Pagination(7, 1, null, "{\"operationTime\":\"desc\"}"));
			List<PlatformSyDistributedRecordExpand> syAccountRecordExpands = memberService
					.getSyAccountRecord(userExpandQueryVo);
			if (syAccountRecordExpands != null && syAccountRecordExpands.size() > 0)
				model.addAttribute("syAccountRecordExpands", syAccountRecordExpands);

			/**
			 * 近期得利宝变更记录
			 */
			userExpandQueryVo.setPagination(new Pagination(7, 1, null, "{\"operateTime\":\"desc\"}"));
			List<AccountRecordExpand> dlbAccountRecordExpands = memberService.getDlbAccountRecord(userExpandQueryVo);
			if (dlbAccountRecordExpands != null && dlbAccountRecordExpands.size() > 0)
				model.addAttribute("dlbAccountRecordExpands", dlbAccountRecordExpands);

			/**
			 * 近期消费记录
			 */
			userExpandQueryVo.setPagination(new Pagination(7, 1, null, "{\"journalTime\":\"desc\"}"));
			List<JournalBookExpand> journalBookExpands = memberService
					.getJournalBookRecordsByClientId(userExpandQueryVo);
			if (journalBookExpands != null && journalBookExpands.size() > 0)
				model.addAttribute("journalBookExpands", journalBookExpands);

		} catch (Exception e) {
			e.printStackTrace();
		}
		userExpand.setRole(roleExpand);
		model.addAttribute("user", userExpand);
		return "memberCenter";
	}

	@RequestMapping(value = "/moneyChangeRecord", method = { RequestMethod.GET })
	public String moneyChangeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(size, page,
		// null,"{\"operateTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getMoneyAccountRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "moneyChangeRecord";
	}

	@RequestMapping(value = "/syChangeRecord", method = { RequestMethod.GET })
	public String syChangeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getSyAccountRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "syChangeRecord";
	}

	@RequestMapping(value = "/jfChangeRecord", method = { RequestMethod.GET })
	public String jfChangeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getJfAccountRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "jfChangeRecord";
	}

	@RequestMapping(value = "/dlbChangeRecord", method = { RequestMethod.GET })
	public String dlbChangeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getDlbAccountRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "dlbChangeRecord";
	}

	@RequestMapping(value = "/yljTransferRecord", method = { RequestMethod.GET })
	public String yljTransferRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getYljTransferRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "yljTransferRecord";
	}

	@RequestMapping(value = "/onlineConsumeRecord", method = { RequestMethod.GET })
	public String onlineConsumeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getOnlineConsumeRecordsTotalNum(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "user/onlineConsumeRecord";
	}

	@RequestMapping(value = "/offlineConsumeRecord", method = { RequestMethod.GET })
	public String offlineConsumeRecord(HttpSession session, Model model) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		// userExpandQueryVo.setPagination(new Pagination(7, 1, null,
		// "{\"operationTime\":\"desc\"}"));
		Integer totalNum = null;
		try {
			totalNum = memberService.getJournalBookRecordsTotalNumByClientId(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalNum == null)
			totalNum = 0;
		model.addAttribute("totalNum", totalNum);
		return "offlineConsumeRecord";
	}

	class PageObj {
		Integer totalCount;
		Object balanceDetail;

		public PageObj() {
		}

		public PageObj(Integer totalCount, Object objList) {
			this.totalCount = totalCount;
			this.balanceDetail = objList;
		}
	}

	@RequestMapping(value = "/moneyChangeRecordAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String moneyChangeRecordAjax(HttpSession session, Model model, Integer page, Integer size,
			Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"operateTime\":\"desc\"}"));
		List<AccountRecordExpand> moneyAccountRecordExpands = null;
		try {
			moneyAccountRecordExpands = memberService.getMoneyAccountRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, moneyAccountRecordExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/syChangeRecordAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syChangeRecordAjax(HttpSession session, Model model, Integer page, Integer size, Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"operationTime\":\"desc\"}"));
		List<PlatformSyDistributedRecordExpand> syAccountRecordExpands = null;
		try {
			syAccountRecordExpands = memberService.getSyAccountRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, syAccountRecordExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/jfChangeRecordAjax", method = { RequestMethod.GET })
	@ResponseBody
	public String jfChangeRecordAjax(HttpSession session, Model model, Integer page, Integer size, Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"operateTime\":\"desc\"}"));
		List<AccountRecordExpand> jfAccountRecordExpands = null;
		try {
			jfAccountRecordExpands = memberService.getJfAccountRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, jfAccountRecordExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/dlbChangeRecordAjax", method = { RequestMethod.GET })
	@ResponseBody
	public String dlbChangeRecordAjax(HttpSession session, Model model, Integer page, Integer size, Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"operateTime\":\"desc\"}"));
		List<AccountRecordExpand> dlbAccountRecordExpands = null;
		try {
			dlbAccountRecordExpands = memberService.getDlbAccountRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, dlbAccountRecordExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/yljTransferAjax", method = { RequestMethod.GET })
	@ResponseBody
	public String yljTransferRecordAjax(HttpSession session, Model model, Integer page, Integer size,
			Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"operateTime\":\"desc\"}"));
		List<AccountRecordExpand> dlbAccountRecordExpands = null;
		try {
			dlbAccountRecordExpands = memberService.getYljTransferRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, dlbAccountRecordExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/onlineConsumeRecordAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String onlineConsumeRecordAjax(HttpSession session, Model model, Integer page, Integer size,
			Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"journalTime\":\"desc\"}"));
		List<OnlineJournalBookItem> onlineJournalBookItems = null;
		try {
			onlineJournalBookItems = memberService.getOnlineConsumeRecord(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, onlineJournalBookItems);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/offlineConsumeRecordAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String offlineConsumeRecordAjax(HttpSession session, Model model, Integer page, Integer size,
			Integer totalSize) {
		UserExpand user = (UserExpand) session.getAttribute("user");
		UserExpandQueryVo userExpandQueryVo = new UserExpandQueryVo();
		userExpandQueryVo.setId(user.getId());
		userExpandQueryVo.setPagination(new Pagination(size, page, null, "{\"journalTime\":\"desc\"}"));
		List<JournalBookExpand> journalBookExpands = null;
		try {
			journalBookExpands = memberService.getJournalBookRecordsByClientId(userExpandQueryVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Gson gson = new Gson();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		JsonResult jsonResult = new JsonResult("0", "0");
		jsonResult.resultObj = new PageObj(totalSize, journalBookExpands);
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/changePwdValidate1", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String changePwdValidate1(HttpSession session, Model model, String oldPwd) {
		JsonResult jsonResult = new JsonResult("0", "0");
		Gson gson = new Gson();
		UserExpand userExpand = (UserExpand) session.getAttribute("user");
		userExpand.setPassword(oldPwd);
		UserExpand user = null;
		;
		try {
			user = memberService.findUserWhenLogin(userExpand);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			jsonResult.logicCode = "1";
			jsonResult.resultMsg = "原始密码不正确";
		}
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/changePwdValidate2", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String changePwdValidate2(HttpSession session, HttpServletRequest request, Model model, String oldPwd,
			String newPwd, String imgVerifyCode) {
		JsonResult jsonResult = new JsonResult("0", "0");
		Gson gson = new Gson();

		// 1.判断验证码是否正确
		String savedImgCode = (String) session.getAttribute("imageVerifyCode");
		if (savedImgCode == null || !savedImgCode.equals(imgVerifyCode)) {
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = "验证码不正确";
			return gson.toJson(jsonResult);
		}

		// 2.判断原始密码是否正确
		UserExpand userExpand = (UserExpand) session.getAttribute("user");
		userExpand.setPassword(oldPwd);
		UserExpand user = null;
		try {
			user = memberService.findUserWhenLogin(userExpand);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (user == null) {
			jsonResult.logicCode = "1";
			jsonResult.resultMsg = "原始密码不正确";
			return gson.toJson(jsonResult);
		}

		// 3.修改密码
		UserExpand newUser = new UserExpand();
		newUser.setId(userExpand.getId());
		newUser.setPassword(newPwd);
		try {
			memberService.updateUser(newUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		jsonResult.url = request.getContextPath() + "/loginPage";
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/changePwd", method = { RequestMethod.GET })
	public String changePwd(HttpSession session, Model model) {
		return "changePwd";
	}

	@RequestMapping(value = "/selfInfo", method = { RequestMethod.GET })
	public String selfInfo(HttpSession session, Model model) {
		return "selfInfo";
	}

	@RequestMapping(value = "/lostPwd", method = { RequestMethod.GET })
	public String forgetPwd(HttpSession session, Model model) {
		return "lostPwd";
	}

	@RequestMapping(value = "/lostPwdSubmitAjax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String lostPwdSubmitAjax(HttpSession session, Model model, String phone, String imgVerifyCode) {
		Gson gson = new Gson();
		JsonResult jsonResult = new JsonResult("0", "0");
		// 1.判断验证码是否正确
		String currentVerifyCode = (String) session.getAttribute("imageVerifyCode");
		if (imgVerifyCode == null || currentVerifyCode == null || !imgVerifyCode.equals(currentVerifyCode)) {
			jsonResult.logicCode = "-2";
			jsonResult.resultMsg = "验证码不正确";
			return gson.toJson(jsonResult);
		}

		// 2.判断该手机号是否存在
		UserExpand userExpand = null;
		try {
			userExpand = memberService.findUserByKeyWords(phone);
			if (userExpand == null) {
				jsonResult.logicCode = "-1";
				jsonResult.resultMsg = "用户不存在";
				return gson.toJson(jsonResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 3.返回成功
		jsonResult.resultObj = userExpand;
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/lostPwdSubmit", method = { RequestMethod.GET, RequestMethod.POST })
	public String lostPwdSubmit(HttpSession session, Model model, String phone) {
		model.addAttribute("phone", phone);
		return "lostPwd1";
	}

	@RequestMapping(value = "/lostPwdSubmit1Ajax", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String lostPwdSubmit1(HttpSession session, Model model, String userPhone, String newPwd, String secondPwd,
			String smsCode) {
		Gson gson = new Gson();
		JsonResult jsonResult = new JsonResult("0", "0");

		// 1.判断手机验证是否正确
		String savedSmsCode = (String) session.getAttribute("smsCode");
		if (savedSmsCode == null || smsCode == null || !smsCode.equals(savedSmsCode)) {
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = "手机验证码不正确";
			return gson.toJson(jsonResult);
		}

		// 2.判断两次密码是否一致
		if (newPwd == null || !newPwd.equals(secondPwd)) {
			jsonResult.logicCode = "-1";
			jsonResult.resultMsg = "密码两次输入不一致";
			return gson.toJson(jsonResult);
		}

		// 3.修改密码
		UserExpand newUser = null;
		try {
			newUser = memberService.findUserByKeyWords(userPhone);
			newUser.setPassword(newPwd);
			memberService.updateUser(newUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gson.toJson(jsonResult);
	}

	@RequestMapping(value = "/lostPwdSuccess", method = { RequestMethod.GET, RequestMethod.POST })
	public String lostPwdSuccess(HttpSession session, Model model, String phone) {
		return "lostPwdSuccess";
	}
}