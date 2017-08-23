package com.sxsram.ssm.mapper;

import java.util.List;

import com.sxsram.ssm.entity.AccountExpand;
import com.sxsram.ssm.entity.AccountRecordExpand;
import com.sxsram.ssm.entity.Address;
import com.sxsram.ssm.entity.Certification;
import com.sxsram.ssm.entity.CommodityType;
import com.sxsram.ssm.entity.GlobalPrams;
import com.sxsram.ssm.entity.Indent;
import com.sxsram.ssm.entity.JournalBookExpand;
import com.sxsram.ssm.entity.PlatformSyDistributedRecord;
import com.sxsram.ssm.entity.PlatformSyDistributedRecordExpand;
import com.sxsram.ssm.entity.ShoppingMallExpand;
import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.PlatformYljSavedRecordExpand;
import com.sxsram.ssm.entity.RechargeAndWithDrawRecordExpand;
import com.sxsram.ssm.entity.Role;
import com.sxsram.ssm.entity.RoleExpand;

public interface UserMapper {
	public void insertNewJfAccount(AccountExpand accountExpand) throws Exception;

	public void insertNewDlbAccount(AccountExpand accountExpand) throws Exception;

	public void insertNewMoneyAccount(AccountExpand accountExpand) throws Exception;

	public void insertNewYljAccount(AccountExpand accountExpand) throws Exception;
	
	public void insertNewJdbAccount(AccountExpand accountExpand) throws Exception;

	public List<AccountRecordExpand> queryAllDlbRecords(Integer userId) throws Exception;

	public List<AccountRecordExpand> queryAllJfRecords(Integer userId) throws Exception;

	public List<PlatformYljSavedRecordExpand> queryAllYljRecords(Integer userId) throws Exception;

	public List<PlatformSyDistributedRecordExpand> queryAllSyRecords(Integer userId) throws Exception;

	public List<AccountRecordExpand> queryAllMoneyRecords(Integer userId) throws Exception;

	public void insertNewUser(UserExpand userExpand) throws Exception;

	public void updateUserAccountInformation(UserExpand userExpand) throws Exception;

	public UserExpand queryUserAccountInfo(String keyWord) throws Exception;

	public UserExpand queryUserByOpenId(String openId) throws Exception;

	public UserExpand queryUserById(int id) throws Exception;

	public List<ShoppingMallExpand> queryAllShoppingMalls() throws Exception;

	public UserExpand queryUserWhenLogin(UserExpand user) throws Exception;

	public void updateUserOpenId(UserExpand user) throws Exception;

	public int countUsername(String username) throws Exception;

	public int countOpenId(String openId) throws Exception;

	public List<Address> queryEparchies(Integer addrId) throws Exception;

	public List<Address> queryCities(Integer addrId) throws Exception;

	public int countPhone(String phone) throws Exception;

	public Double getMoneyAccountBalance(int userId) throws Exception;

	public Indent findIndentByIndentNo(String indentNo) throws Exception;

	public UserExpand queryUserByPhone(String phone) throws Exception;

	public void insertNewJournalBookItem(JournalBookExpand journalBookExpand) throws Exception;

	public List<CommodityType> queryCommodityByParentId(int parentId) throws Exception;

	public void updateIndentFlag(int id) throws Exception;

	public void insertNewCertification(Certification certification) throws Exception;

	public List<JournalBookExpand> queryAllJournalBookRecordsByUserId(int id) throws Exception;

	public AccountExpand findUserMoneyAccountById(int userId) throws Exception;

	public void updateUserMoneyBalance(AccountExpand moneyAccount) throws Exception;

	public void updateUserMoneyTotalPlatformOutgoings(AccountExpand moneyAccount) throws Exception;

	public void updateUserMoneyTotalUserIncomings(AccountExpand moneyAccount) throws Exception;

	public void updateUserMoneyTotalUserOutgoings(AccountExpand moneyAccount) throws Exception;

	public void insertNewMoneyChangeRecord(AccountRecordExpand accountRecordExpand) throws Exception;

	public AccountExpand findUserJfAccountById(int userId) throws Exception;

	public void updateUserJfBalance(AccountExpand jfAccount) throws Exception;

	public void insertNewJfChangeRecord(AccountRecordExpand accountRecordExpand) throws Exception;

	public void insertNewRechargeOrWithDrawRecord(RechargeAndWithDrawRecordExpand record) throws Exception;

	public RechargeAndWithDrawRecordExpand queryRechargeOrWithDarwRecord(String orderNo) throws Exception;

	public void updateRechageOrWithDarwRecordState(RechargeAndWithDrawRecordExpand record) throws Exception;

	public List<RechargeAndWithDrawRecordExpand> queryAllWithDrawsRecords(int id) throws Exception;

	public List<RechargeAndWithDrawRecordExpand> queryAllRechargeRecords(int id) throws Exception;

	public List<JournalBookExpand> queryAllJournalBookRecordsByClientId(int clientId) throws Exception;

	public void updateCertification(Certification certification) throws Exception;

	public ShoppingMallExpand queryShoppingMallById(Integer mallId) throws Exception;

	public RoleExpand queryRoleByUserId(Integer userId) throws Exception;

	public int queryCashFlag(int id) throws Exception;

	public void updateCashFlag(UserExpand userExpand) throws Exception;

	public int queryWithDrawsRecordsByUserId(int id) throws Exception;

	public int queryCurrentMonthConsumeRecordCounts(int id) throws Exception;

	public GlobalPrams findGlobalParams() throws Exception;

	public JournalBookExpand queryFirstJournalBookByClientId(int userId) throws Exception;

	public RechargeAndWithDrawRecordExpand queryLastWithDrawRecord(int userId) throws Exception;

	public RechargeAndWithDrawRecordExpand queryFirstWithDrawRecord(int userId) throws Exception;

}