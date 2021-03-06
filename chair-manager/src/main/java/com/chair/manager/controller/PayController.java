package com.chair.manager.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chair.manager.bean.ReqParam;
import com.chair.manager.bean.ResponseResult;
import com.chair.manager.service.UserAccountService;


@Controller
@RequestMapping("pay")
public class PayController {
	private static Logger logger = Logger.getLogger(PayController.class);

	@Autowired
	private UserAccountService  userAccountService;
	
	
	@ResponseBody
	@RequestMapping(value="callback",method=RequestMethod.POST)
	private ResponseResult callback(@RequestBody ReqParam reqParam){
		logger.info("------【支付成功回调】参数------"+reqParam);
		return new ResponseResult(userAccountService.updateRechargeStatus(reqParam.getPhoneNumber(), reqParam.getBatchNO(), reqParam.getTransactionID()));
	}
	
	
}
