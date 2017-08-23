package com.sxsram.ssm.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.ReplaceOverride;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sxsram.ssm.entity.AccountExpand;
import com.sxsram.ssm.entity.AccountRecordExpand;
import com.sxsram.ssm.entity.Address;
import com.sxsram.ssm.entity.Certification;
import com.sxsram.ssm.entity.CommodityType;
import com.sxsram.ssm.entity.GlobalPrams;
import com.sxsram.ssm.entity.Indent;
import com.sxsram.ssm.entity.JournalBookExpand;
import com.sxsram.ssm.entity.PlatformSyDistributedRecordExpand;
import com.sxsram.ssm.entity.ShoppingMallExpand;
import com.sxsram.ssm.entity.User;
import com.sxsram.ssm.entity.UserAccountExpand;
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.UserExtra;
import com.sxsram.ssm.entity.UserExtraQueryVo;
import com.sxsram.ssm.entity.PlatformYljSavedRecordExpand;
import com.sxsram.ssm.entity.RechargeAndWithDrawRecordExpand;
import com.sxsram.ssm.entity.RoleExpand;
import com.sxsram.ssm.mapper.UserMapper;
import com.sxsram.ssm.service.GlobalParamService;
import com.sxsram.ssm.service.UserExtraService;
import com.sxsram.ssm.service.UserService;
import com.sxsram.ssm.util.AjaxResult;
import com.sxsram.ssm.util.ConfigUtil;
import com.sxsram.ssm.util.QueryCondition;
import com.sxsram.ssm.util.QueryConditionAbstractItem;
import com.sxsram.ssm.util.QueryConditionItem;
import com.sxsram.ssm.util.QueryConditionOp;

@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;
	@Resource
	private UserExtraService userExtraService;
	@Resource
	private GlobalParamService globalParamService;

	@Override
	public UserExpand findUserWhenLogin(UserExpand user) throws Exception {
		return userMapper.queryUserWhenLogin(user);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void updateUserOpenId(UserExpand user) throws Exception {
		userMapper.updateUserOpenId(user);
	}

	@Override
	public void updateCertification(Certification certification) throws Exception {
		userMapper.updateCertification(certification);
	}

	@Override
	public UserExpand getUserAccountInfo(String keyWord) throws Exception {
		return userMapper.queryUserAccountInfo(keyWord);
	}

	@Override
	public boolean usernameExist(String username) throws Exception {
		return userMapper.countUsername(username) != 0 ? true : false;
	}

	@Override
	public boolean openIdExist(String openId) throws Exception {
		return userMapper.countOpenId(openId) != 0 ? true : false;
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3, rollbackFor = {
			java.lang.Exception.class })
	@Override
	public boolean registUser(UserExpand userExpand, Integer inviterId) throws Exception {
		Certification certification = new Certification();
		userMapper.insertNewCertification(certification);
		userExpand.setCertification(certification);
		userMapper.insertNewUser(userExpand);

		UserExtra userExtra = new UserExtra();
		AccountExpand jfAccountExpand = new AccountExpand("plateform-jf-account");
		AccountExpand dlbAccountExpand = new AccountExpand("plateform-dlb-account");
		AccountExpand moneyAccountExpand = new AccountExpand("plateform-money-account");
		AccountExpand yljAccountExpand = new AccountExpand("plateform-ylj-account");
		AccountExpand jdbAccountExpand = new AccountExpand("plateform-jdb-account");

		jfAccountExpand.setUser(userExpand);
		dlbAccountExpand.setUser(userExpand);
		moneyAccountExpand.setUser(userExpand);
		yljAccountExpand.setUser(userExpand);
		jdbAccountExpand.setUser(userExpand);

		userExtra.setUserId(userExpand.getId());
		if (inviterId != null) {
			userExtra.setInviterUserId(inviterId);
		}
		userExtra.setGiveMoneyFlag(0);

		userMapper.insertNewJfAccount(jfAccountExpand);
		userMapper.insertNewDlbAccount(dlbAccountExpand);
		userMapper.insertNewMoneyAccount(moneyAccountExpand);
		userMapper.insertNewYljAccount(yljAccountExpand);
		userMapper.insertNewJdbAccount(jdbAccountExpand);
		userExtraService.addNewUserExtra(userExtra);

		userExpand.setJfAccount(jfAccountExpand);
		userExpand.setDlbAccount(dlbAccountExpand);
		userExpand.setMoneyAccount(moneyAccountExpand);
		userExpand.setYljAccount(yljAccountExpand);
		userExpand.setJdbAccount(jdbAccountExpand);

		userMapper.updateUserAccountInformation(userExpand);
		return true;
	}

	@Override
	public UserExpand findUserByOpenId(String openId) throws Exception {
		return userMapper.queryUserByOpenId(openId);
	}

	@Override
	public UserExpand findUserById(int id) throws Exception {
		return userMapper.queryUserById(id);
	}

	@Override
	public List<ShoppingMallExpand> findAllShppingMalls() throws Exception {
		return userMapper.queryAllShoppingMalls();
	}

	@Override
	public List<AccountRecordExpand> getMoneyAccountRecord(Integer userId) throws Exception {
		return userMapper.queryAllMoneyRecords(userId);
	}

	@Override
	public List<AccountRecordExpand> getJfAccountRecord(Integer userId) throws Exception {
		return userMapper.queryAllJfRecords(userId);
	}

	@Override
	public List<AccountRecordExpand> getDlbAccountRecord(Integer userId) throws Exception {
		return userMapper.queryAllDlbRecords(userId);
	}

	@Override
	public List<PlatformYljSavedRecordExpand> getYljAccountRecord(Integer userId) throws Exception {
		return userMapper.queryAllYljRecords(userId);
	}

	@Override
	public List<PlatformSyDistributedRecordExpand> getSyAccountRecord(Integer userId) throws Exception {
		return userMapper.queryAllSyRecords(userId);
	}

	@Override
	public List<Address> findEparchies(Integer addrId) throws Exception {
		return userMapper.queryEparchies(addrId);
	}

	@Override
	public List<Address> findCities(Integer addrId) throws Exception {
		return userMapper.queryCities(addrId);
	}

	@Override
	public boolean phoneExist(String phone) throws Exception {
		return userMapper.countPhone(phone) != 0 ? true : false;
	}

	@Override
	public Double getMoneyAccountBalance(int userId) throws Exception {
		// TODO Auto-generated method stub
		return userMapper.getMoneyAccountBalance(userId);
	}

	@Override
	public Indent findIndentByIndentNo(String indentNo) throws Exception {
		// TODO Auto-generated method stub
		return userMapper.findIndentByIndentNo(indentNo);
	}

	@Override
	public UserExpand findUserByPhone(String phone) throws Exception {
		// TODO Auto-generated method stub
		return userMapper.queryUserByPhone(phone);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	public void updateUserMoneyBalance(int userId, double money, String desc) throws Exception {

		AccountExpand moneyAccount = userMapper.findUserMoneyAccountById(userId);

		moneyAccount.setAccountBalance(moneyAccount.getAccountBalance() + money);
		userMapper.updateUserMoneyBalance(moneyAccount);

		AccountRecordExpand accountRecordExpand = new AccountRecordExpand();
		accountRecordExpand.setOperateDesc(desc);
		accountRecordExpand.setOperateNum(Math.abs(money));
		accountRecordExpand.setOperation(money > 0 ? 0 : 1);// 0收入，1支出
		accountRecordExpand.setAccount(moneyAccount);
		userMapper.insertNewMoneyChangeRecord(accountRecordExpand);
	}

	/**
	 * 累计消息
	 * 
	 * @param userId
	 * @param amount
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	private void updateUserMoneyTotalPlatformOutgoings(int userId, double amount) throws Exception {
		AccountExpand moneyAccount = userMapper.findUserMoneyAccountById(userId);
		// moneyAccount.setTotalPlatformOutgoings(moneyAccount.getTotalPlatformIncomings()
		// + amount);
		moneyAccount.setTotalPlatformOutgoings(moneyAccount.getTotalPlatformOutgoings() + amount);
		userMapper.updateUserMoneyTotalPlatformOutgoings(moneyAccount);
	}

	/**
	 * 提现总额
	 * 
	 * @param userId
	 * @param amount
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	private void updateUserMoneyTotalUserIncomings(int userId, double amount) throws Exception {
		AccountExpand moneyAccount = userMapper.findUserMoneyAccountById(userId);
		// moneyAccount.setTotalPlatformOutgoings(moneyAccount.getTotalPlatformIncomings()
		// + amount);
		moneyAccount.setTotalUserIncomings(moneyAccount.getTotalUserIncomings() + amount);
		userMapper.updateUserMoneyTotalUserIncomings(moneyAccount);
	}

	/**
	 * 充值总额
	 * 
	 * @param userId
	 * @param amount
	 * @throws Exception
	 */
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	private void updateUserMoneyTotalUserOutgoings(int userId, double amount) throws Exception {
		AccountExpand moneyAccount = userMapper.findUserMoneyAccountById(userId);
		// moneyAccount.setTotalPlatformOutgoings(moneyAccount.getTotalPlatformIncomings()
		// + amount);
		moneyAccount.setTotalUserOutgoings(moneyAccount.getTotalUserOutgoings() + amount);
		userMapper.updateUserMoneyTotalUserOutgoings(moneyAccount);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	public void updateUserJfBalance(int userId, double rewardJf, String desc, int opeation) throws Exception {
		AccountExpand jfAccount = userMapper.findUserJfAccountById(userId);

		AccountRecordExpand accountRecordExpand = new AccountRecordExpand();
		accountRecordExpand.setOperateDesc(desc);
		accountRecordExpand.setOperateNum(rewardJf);
		accountRecordExpand.setOperation(opeation);
		accountRecordExpand.setAccount(jfAccount);
		accountRecordExpand.setBalance(jfAccount.getAccountBalance());
		userMapper.insertNewJfChangeRecord(accountRecordExpand);

		jfAccount.setAccountBalance(jfAccount.getAccountBalance() + rewardJf);
		userMapper.updateUserJfBalance(jfAccount);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void saveNewJournalBookItem(JournalBookExpand journalBookExpand) throws Exception {
		// TODO Auto-generated method stub
		userMapper.insertNewJournalBookItem(journalBookExpand);
		userMapper.updateIndentFlag(journalBookExpand.getIndent().getId());
		// UserExpand client =journalBookExpand.getClient();
		// client.setGetCashFlag(0); //只要报单，就恢复收益
		// userMapper.updateCashFlag(client);

		double money = journalBookExpand.getAmount() * journalBookExpand.getPremiumRates() / 100;
		// 更新商家现金账户
		updateUserMoneyBalance(journalBookExpand.getBusiness().getId(), -1 * money, "商家报单");
		// 更新平台现金账户
		updateUserMoneyBalance(1, money, "商家报单");// 1代表平台账户
		// 更新买家累计消费
		updateUserMoneyTotalPlatformOutgoings(journalBookExpand.getClient().getId(), journalBookExpand.getAmount());

		// if (journalBookExpand.getClient()!=null &&
		// journalBookExpand.getGiftJf() > 0) {
		// // 更新商家积分账户
		// updateUserJfBalance(journalBookExpand.getClient().getId(),
		// journalBookExpand.getGiftJf(), "商家报单");
		// // 更新平台积分账户
		// updateUserJfBalance(1, -1 * journalBookExpand.getGiftJf(), "商家报单");
		// }

		// 如果是第一次消费，给推荐人赠送10积分
		UserExtra userExtra = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("e.userId", journalBookExpand.getClient().getId() + "", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		userExtra = userExtraService.findUserExtra(userExtraQueryVo);
		if (userExtra == null || userExtra.getInviterUser() == null || userExtra.getInviterRewardFlag() == 1) {
			return;
		}
		GlobalPrams globalParams = globalParamService.findCurrentGlobalParams();
		if (globalParams == null) {
			System.out.println("PROGRAMERROR: " + "Can't find Global Params");
			return;
		}
		int giftJf = globalParams.getInviterSuccessGiftJf() == null ? 0 : globalParams.getInviterSuccessGiftJf();
		updateUserJfBalance(userExtra.getInviterUserId(), giftJf, "推荐新用户", 0);
		// updateUserJfBalance(userExtra.getUserId(), 600, "首次消费", 0);
		// updateUserJfBalance(1, 10 * -1, "推荐新用户");

		// 更新赠送标志
		UserExtra tmpUserExtra = new UserExtra();
		tmpUserExtra.setId(userExtra.getId());
		tmpUserExtra.setInviterRewardFlag(1);
		userExtraService.updateUserExtra(tmpUserExtra);
	}

	@Override
	public List<CommodityType> findCommodityByParentId(int parentId) throws Exception {
		return userMapper.queryCommodityByParentId(parentId);
	}

	@Override
	public List<JournalBookExpand> findAllJournalBookRecordsByUserId(int id) throws Exception {
		return userMapper.queryAllJournalBookRecordsByUserId(id);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void userRecharge(RechargeAndWithDrawRecordExpand record) throws Exception {
		userMapper.insertNewRechargeOrWithDrawRecord(record);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void userWithDraw(RechargeAndWithDrawRecordExpand record) throws Exception {
		userMapper.insertNewRechargeOrWithDrawRecord(record);
	}

	@Override
	public RechargeAndWithDrawRecordExpand findRechargeRecordByOrderNo(String orderNo) throws Exception {
		return userMapper.queryRechargeOrWithDarwRecord(orderNo);
	}

	@Override
	public RechargeAndWithDrawRecordExpand findWithDrawRecordByOrderNo(String orderNo) throws Exception {
		return userMapper.queryRechargeOrWithDarwRecord(orderNo);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void updateRechargeRecordState(RechargeAndWithDrawRecordExpand record) throws Exception {
		userMapper.updateRechageOrWithDarwRecordState(record);
		if (record.getOperateState() == ConfigUtil.TradeState.PAYSUCCESS.ordinal()) {
			if (record.getOperateType() == ConfigUtil.TradeType.RECHARGE.ordinal()) {
				updateUserMoneyBalance(record.getUser().getId(), record.getOperateNum(), "账户充值");
				updateUserMoneyTotalUserIncomings(record.getUser().getId(), record.getOperateNum());
			} else {
				updateUserMoneyBalance(record.getUser().getId(), -1 * record.getOperateNum(), "账户提现");
				updateUserMoneyTotalUserOutgoings(record.getUser().getId(), record.getOperateNum());
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	@Override
	public void updateWithDrawRecordState(RechargeAndWithDrawRecordExpand record) throws Exception {
		userMapper.updateRechageOrWithDarwRecordState(record);
		if (record.getOperateState() == ConfigUtil.TradeState.PAYSUCCESS.ordinal()) {
			if (record.getOperateType() == ConfigUtil.TradeType.RECHARGE.ordinal()) {
				updateUserMoneyBalance(record.getUser().getId(), record.getOperateNum(), "账户充值");
				updateUserMoneyTotalUserIncomings(record.getUser().getId(), record.getOperateNum());
			} else {
				updateUserMoneyBalance(record.getUser().getId(), -1 * record.getOperateNum(), "账户提现");
				updateUserMoneyTotalUserOutgoings(record.getUser().getId(), record.getOperateNum());
			}
		}
	}

	@Override
	public List<RechargeAndWithDrawRecordExpand> findAllWithDrawsRecords(int id) throws Exception {
		// TODO Auto-generated method stub
		return userMapper.queryAllWithDrawsRecords(id);
	}

	@Override
	public List<JournalBookExpand> findAllJournalBookRecordsByClientId(int clientId) throws Exception {
		return userMapper.queryAllJournalBookRecordsByClientId(clientId);
	}

	@Override
	public JournalBookExpand findFirstJournalBookByClientId(int userId) {
		try {
			return userMapper.queryFirstJournalBookByClientId(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RechargeAndWithDrawRecordExpand findLastWithDrawRecord(int userId) {
		try {
			return userMapper.queryLastWithDrawRecord(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RechargeAndWithDrawRecordExpand findFirstWithDrawRecord(int userId) {
		try {
			return userMapper.queryFirstWithDrawRecord(userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ShoppingMallExpand findAllShppingMallById(Integer mallId) {
		try {
			return userMapper.queryShoppingMallById(mallId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RoleExpand findRoleByUserId(Integer userId) {
		try {
			return userMapper.queryRoleByUserId(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getCashFlag(int id) {
		try {
			return userMapper.queryCashFlag(id);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public boolean setCashFlag(UserExpand userExpand) {
		try {
			userMapper.updateCashFlag(userExpand);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int getWithDrawRecordCounts(int id) {
		try {
			return userMapper.queryWithDrawsRecordsByUserId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public int getCurrentMonthConsumeRecordCounts(int id) {
		try {
			return userMapper.queryCurrentMonthConsumeRecordCounts(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public double findSubmitOrderCheckLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GlobalPrams findGlobalParams() {
		try {
			return userMapper.findGlobalParams();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = false, timeout = 3)
	public void registSuccessGiftJf(UserExpand user) throws Exception {
		UserExtra userExtra = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("e.userId", user.getId() + "", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		userExtra = userExtraService.findUserExtra(userExtraQueryVo);
		if (userExtra == null) {
			throw new RuntimeException("ProgramException: Can't find UserExtra");
		}

		GlobalPrams globalParams = globalParamService.findCurrentGlobalParams();
		if (globalParams == null) {
			throw new RuntimeException("ProgramException: Can't find Global params in loginSubmit function.");
		}

		//没有有邀请人 || 已经赠送过
		if (userExtra.getInviterUser() == null || userExtra.getBeInviterRewardFlag() == 1) {
			return;
		}
		
		//赠送积分
		int giftJf = globalParams.getBeInviterSuccessGiftJf() == null ? 0
				: globalParams.getBeInviterSuccessGiftJf();
		updateUserJfBalance(user.getId(), giftJf, "新用户注册", 0);

		// 更新赠送标志
		UserExtra tmpUserExtra = new UserExtra();
		tmpUserExtra.setId(userExtra.getId());
		tmpUserExtra.setBeInviterRewardFlag(1);
		userExtraService.updateUserExtra(tmpUserExtra);
	}

}
