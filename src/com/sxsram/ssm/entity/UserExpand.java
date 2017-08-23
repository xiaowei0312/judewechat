package com.sxsram.ssm.entity;

import java.util.List;

public class UserExpand extends User {
	private RoleExpand role;
	private Certification certification;
	private List<UserAccount> userAccounts;

	// 账户基本信息
	private AccountExpand jfAccount;
	private AccountExpand dlbAccount;
	private AccountExpand moneyAccount;
	private AccountExpand yljAccount;
	private AccountExpand jdbAccount;

	// 账户统计信息
	private Double totalConsumOffline; // 累计线下消费
	private Double totalConsumOnline; // 累计线上消费
	private Double totalRewardJf; // 累计奖励积分
	private Double totalTransferDlb; // 累计转换聚财宝
	private Double totalRewardMoney; // 累计奖励（money）
	private Double totalRewardJdb; // 累计奖励（jdb）
	private Double totalRewardYlj; // 累计奖励（ylj）
	private Double totalTransferYlj;	//累计转存养老金
	private Double totalRecharge; // 累计充值
	private Double totalWithDraw; // 累计提现
	private Double totalSumbitOrderValue;	//累计报单上交总金额

	// Other,我也不记得这些是干什么用的了
	private double changeValue; // 账户余额改变
	private Address provinceObj;
	private Address cityObj;
	private Address areaObj;
	private String keywords;
	private UserExpand proxyUser;

	
	
	public Double getTotalTransferYlj() {
		return totalTransferYlj;
	}

	public void setTotalTransferYlj(Double totalTransferYlj) {
		this.totalTransferYlj = totalTransferYlj;
	}

	public Double getTotalSumbitOrderValue() {
		return totalSumbitOrderValue;
	}

	public void setTotalSumbitOrderValue(Double totalSumbitOrderValue) {
		this.totalSumbitOrderValue = totalSumbitOrderValue;
	}

	public Double getTotalConsumOffline() {
		return totalConsumOffline;
	}

	public void setTotalConsumOffline(Double totalConsumOffline) {
		this.totalConsumOffline = totalConsumOffline;
	}

	public Double getTotalConsumOnline() {
		return totalConsumOnline;
	}

	public void setTotalConsumOnline(Double totalConsumOnline) {
		this.totalConsumOnline = totalConsumOnline;
	}

	public Double getTotalRewardJf() {
		return totalRewardJf;
	}

	public void setTotalRewardJf(Double totalRewardJf) {
		this.totalRewardJf = totalRewardJf;
	}

	public Double getTotalTransferDlb() {
		return totalTransferDlb;
	}

	public void setTotalTransferDlb(Double totalTransferDlb) {
		this.totalTransferDlb = totalTransferDlb;
	}

	public Double getTotalRewardMoney() {
		return totalRewardMoney;
	}

	public void setTotalRewardMoney(Double totalRewardMoney) {
		this.totalRewardMoney = totalRewardMoney;
	}

	public Double getTotalRewardJdb() {
		return totalRewardJdb;
	}

	public void setTotalRewardJdb(Double totalRewardJdb) {
		this.totalRewardJdb = totalRewardJdb;
	}

	public Double getTotalRewardYlj() {
		return totalRewardYlj;
	}

	public void setTotalRewardYlj(Double totalRewardYlj) {
		this.totalRewardYlj = totalRewardYlj;
	}

	public Double getTotalRecharge() {
		return totalRecharge;
	}

	public void setTotalRecharge(Double totalRecharge) {
		this.totalRecharge = totalRecharge;
	}

	public Double getTotalWithDraw() {
		return totalWithDraw;
	}

	public void setTotalWithDraw(Double totalWithDraw) {
		this.totalWithDraw = totalWithDraw;
	}

	public AccountExpand getJdbAccount() {
		return jdbAccount;
	}

	public void setJdbAccount(AccountExpand jdbAccount) {
		this.jdbAccount = jdbAccount;
	}

	public UserExpand getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(UserExpand proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Address getProvinceObj() {
		return provinceObj;
	}

	public void setProvinceObj(Address provinceObj) {
		this.provinceObj = provinceObj;
	}

	public Address getCityObj() {
		return cityObj;
	}

	public void setCityObj(Address cityObj) {
		this.cityObj = cityObj;
	}

	public Address getAreaObj() {
		return areaObj;
	}

	public void setAreaObj(Address areaObj) {
		this.areaObj = areaObj;
	}

	public double getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(double changeValue) {
		this.changeValue = changeValue;
	}

	public Certification getCertification() {
		return certification;
	}

	public void setCertification(Certification certification) {
		this.certification = certification;
	}

	public RoleExpand getRole() {
		return role;
	}

	public void setRole(RoleExpand role) {
		this.role = role;
	}

	public List<UserAccount> getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(List<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}

	public AccountExpand getJfAccount() {
		return jfAccount;
	}

	public void setJfAccount(AccountExpand jfAccount) {
		this.jfAccount = jfAccount;
	}

	public AccountExpand getDlbAccount() {
		return dlbAccount;
	}

	public void setDlbAccount(AccountExpand dlbAccount) {
		this.dlbAccount = dlbAccount;
	}

	public AccountExpand getMoneyAccount() {
		return moneyAccount;
	}

	public void setMoneyAccount(AccountExpand moneyAccount) {
		this.moneyAccount = moneyAccount;
	}

	public AccountExpand getYljAccount() {
		return yljAccount;
	}

	public void setYljAccount(AccountExpand yljAccount) {
		this.yljAccount = yljAccount;
	}

	@Override
	public String toString() {
		return "UserExpand [role=" + role + ", certification=" + certification + ", userAccounts=" + userAccounts
				+ ", jfAccount=" + jfAccount + ", dlbAccount=" + dlbAccount + ", moneyAccount=" + moneyAccount
				+ ", yljAccount=" + yljAccount + ", jdbAccount=" + jdbAccount + ", changeValue=" + changeValue
				+ ", provinceObj=" + provinceObj + ", cityObj=" + cityObj + ", areaObj=" + areaObj + ", keywords="
				+ keywords + ", proxyUser=" + proxyUser + "]";
	}
}