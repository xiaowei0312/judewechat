package com.sxsram.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller()
@RequestMapping(value = "/mall", method = { RequestMethod.GET, RequestMethod.POST })
public class ShoppingMallController {

	@RequestMapping(value = "/intro", method = { RequestMethod.GET, RequestMethod.POST })
	public String intro() throws Exception {
		return "mall/intro";
	}
	@RequestMapping(value = "/zizhi", method = { RequestMethod.GET, RequestMethod.POST })
	public String zizhi() throws Exception {
		return "mall/zizhi";
	}
}
