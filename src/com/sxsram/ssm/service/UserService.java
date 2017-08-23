package com.sxsram.ssm.service;

import java.util.List;

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
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.PlatformYljSavedRecordExpand;
import com.sxsram.ssm.entity.RechargeAndWithDrawRecordExpand;
import com.sxsram.ssm.entity.RoleExpand;

public interface UserService {
	public boolean registUser(UserExpand userExpand,Integer inviterId) throws Exception;

	public UserExpand findUserByOpenId(String openId) throws Exception;

	public UserExpand findUserById(int id) throws Exception;

	public List<ShoppingMallExpand> findAllShppingMalls() throws Exception;

	public List<AccountRecordExpand> getMoneyAccountRecord(Integer userId) throws Exception;

	public List<AccountRecordExpand> getJfAccountRecord(Integer userId) throws Exception;

	public List<AccountRecordExpand> getDlbAccountRecord(Integer userId) throws Exception;

	public List<PlatformYljSavedRecordExpand> getYljAccountRecord(Integer userId) throws Exception;

	public List<PlatformSyDistributedRecordExpand> getSyAccountRecord(Integer userId) throws Exception;

	public UserExpand findUserWhenLogin(UserExpand user) throws Exception;

	public void updateUserOpenId(UserExpand user) throws Exception;

	public boolean usernameExist(String username) throws Exception;

	public boolean openIdExist(String openId)throws Exception;

	public List<Address> findEparchies(Integer integer) throws Exception;

	public List<Address> findCities(Integer integer) throws Exception;

	public boolean phoneExist(String phone) throws Exception;

	public Double getMoneyAccountBalance(int userId) throws Exception;

	public Indent findIndentByIndentNo(String indentNo) throws Exception;

	public UserExpand findUserByPhone(String phone) throws Exception;

	public void saveNewJournalBookItem(JournalBookExpand journalBookExpand) throws Exception;

	public List<CommodityType> findCommodityByParentId(int parentId) throws Exception;

	public UserExpand getUserAccountInfo(String keyword) throws Exception;

	public List<JournalBookExpand> findAllJournalBookRecordsByUserId(int id) throws Exception;

	public void userRecharge(RechargeAndWithDrawRecordExpand record) throws Exception;

	public void userWithDraw(RechargeAndWithDrawRecordExpand record) throws Exception;

	public RechargeAndWithDrawRecordExpand findRechargeRecordByOrderNo(String orderNo) throws Exception;

	public RechargeAndWithDrawRecordExpand findWithDrawRecordByOrderNo(String orderNo) throws Exception;

	public void updateRechargeRecordState(RechargeAndWithDrawRecordExpand record) throws Exception;

	public void updateWithDrawRecordState(RechargeAndWithDrawRecordExpand record) throws Exception;

	public List<RechargeAndWithDrawRecordExpand> findAllWithDrawsRecords(int id) throws Exception;

	public List<JournalBookExpand> findAllJournalBookRecordsByClientId(int clientId) throws Exception;

	public JournalBookExpand findFirstJournalBookByClientId(int userId);
	
	public void updateCertification(Certification certification) throws Exception;

	public ShoppingMallExpand findAllShppingMallById(Integer mallId);

	public RoleExpand findRoleByUserId(Integer userId);

	public int getCashFlag(int id);

	public boolean setCashFlag(UserExpand userExpand);

	public int getWithDrawRecordCounts(int id);

	public int getCurrentMonthConsumeRecordCounts(int id);

	public double findSubmitOrderCheckLimit();

	public GlobalPrams findGlobalParams();

	public RechargeAndWithDrawRecordExpand findLastWithDrawRecord(int userId);

	public RechargeAndWithDrawRecordExpand findFirstWithDrawRecord(int id);

	public void registSuccessGiftJf(UserExpand user)throws Exception;

}
