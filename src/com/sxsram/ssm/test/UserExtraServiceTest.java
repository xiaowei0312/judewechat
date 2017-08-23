package com.sxsram.ssm.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sxsram.ssm.entity.UserExpand;
import com.sxsram.ssm.entity.UserExpandQueryVo;
import com.sxsram.ssm.entity.UserExtra;
import com.sxsram.ssm.entity.UserExtraQueryVo;
import com.sxsram.ssm.entity.UserQueryVo;
import com.sxsram.ssm.service.UserExtraService;
import com.sxsram.ssm.util.QueryCondition;
import com.sxsram.ssm.util.QueryConditionAbstractItem;
import com.sxsram.ssm.util.QueryConditionItem;
import com.sxsram.ssm.util.QueryConditionOp;

public class UserExtraServiceTest {
	private ApplicationContext ctx = null;
	private SqlSessionFactory sqlSessionFactory;
	private UserExtraService userExtraService;
	{
		ctx = new ClassPathXmlApplicationContext("classpath:spring/springmvc-junit.xml");
		sqlSessionFactory = ctx.getBean(SqlSessionFactory.class);
		userExtraService = ctx.getBean(UserExtraService.class);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInsertNewUser() throws Exception {
		UserExtra userExtra = new UserExtra();
		userExtra.setUserId(11);
		userExtra.setInviterUserId(28);
		userExtra.setGiveMoneyFlag(0);
		userExtraService.addNewUserExtra(userExtra);
	}

	@Test
	public void testDeleteUser() throws Exception {
		UserExtra userExtra = new UserExtra();
		userExtra.setId(1);
		userExtraService.delUserExtra(userExtra);
	}

	@Test
	public void testUpdateUser() throws Exception {
		UserExtra userExtra = new UserExtra();
		userExtra.setId(1);
		userExtra.setGiveMoneyFlag(1);
		userExtraService.updateUserExtra(userExtra);
	}

	@Test
	public void testFindUser() throws Exception {
		UserExtra userExtra = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("u.phone", "13546712419", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		userExtra = userExtraService.findUserExtra(userExtraQueryVo);
		System.out.println(userExtra);
		System.out.println(userExtra.getUser().getUsername()+" , " +userExtra.getUser().getPhone());
		System.out.println(userExtra.getInviterUser().getUsername()+" , " +userExtra.getInviterUser().getPhone());
	}
	
	@Test
	public void testFindUserList() throws Exception {
		List<UserExtra> userExtraList = null;
		UserExtraQueryVo userExtraQueryVo = new UserExtraQueryVo();
		List<QueryConditionAbstractItem> items = new ArrayList<QueryConditionAbstractItem>();
		items.add(new QueryConditionItem("e.delFlag", "0", QueryConditionOp.EQ));
		items.add(new QueryConditionItem("u.phone", "18135100171", QueryConditionOp.EQ));
		userExtraQueryVo.setQueryCondition(new QueryCondition(items));
		userExtraList = userExtraService.findUserExtras(userExtraQueryVo);
		System.out.println(userExtraList);
	}
}
