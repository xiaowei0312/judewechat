package com.sxsram.ssm.service;

import java.util.List;

import com.sxsram.ssm.entity.UserExtra;
import com.sxsram.ssm.entity.UserExtraQueryVo;

public interface UserExtraService {
	public void addNewUserExtra(UserExtra userExtra) throws Exception;

	public void updateUserExtra(UserExtra userExtra) throws Exception;

	public void delUserExtra(UserExtra userExtra) throws Exception;

	public UserExtra findUserExtra(UserExtraQueryVo userExtraQueryVo)throws Exception;

	public List<UserExtra> findUserExtras(UserExtraQueryVo userExtraQueryVo)throws Exception;
}
