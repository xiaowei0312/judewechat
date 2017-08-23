package com.sxsram.ssm.entity;

public class User {
	private int id;
	private String username;
	private String password;
	private String province;
	private String city;
	private String area;
	private String phone;
	private String headImgUrl;
	private String openId; // 微信专用
	private Integer getCashFlag; // 提现标志
	private Integer incomFlag; // 收益标志

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public Integer getGetCashFlag() {
		return getCashFlag;
	}

	public void setGetCashFlag(Integer getCashFlag) {
		this.getCashFlag = getCashFlag;
	}

	public Integer getIncomFlag() {
		return incomFlag;
	}

	public void setIncomFlag(Integer incomFlag) {
		this.incomFlag = incomFlag;
	}

}
