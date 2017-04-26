package com.chair.manager.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.JedisCluster;

import com.chair.manager.bean.ReqParam;
import com.chair.manager.bean.ResponseResult;
import com.chair.manager.pojo.ConsumedDetails;
import com.chair.manager.pojo.RechargePackage;
import com.chair.manager.pojo.RechargeRecord;
import com.chair.manager.service.ConsumedDetailsService;
import com.chair.manager.service.RechargePackageService;
import com.chair.manager.service.RechargeRecordService;
import com.chair.manager.service.UsersService;
import com.chair.manager.vo.ConsumedDetailsVo;
import com.chair.manager.vo.RechargePackageVo;
import com.chair.manager.vo.RechargeRecordVo;


@RequestMapping("/user")
@Controller
public class UsersController {
	private Logger logger = Logger.getLogger(UsersController.class);

	@Autowired
	private RechargeRecordService rechargeRecordService;
	@Autowired
	private ConsumedDetailsService consumedDetailsService;
	@Autowired
	private UsersService  usersService;

	@Autowired
	private JedisCluster jedisCluster;
	
	@Autowired
	private RechargePackageService rechargePackageService;
	
	

	/**
	 * 查看用户充值明细
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryRechargeDetails",method=RequestMethod.POST)
	private ResponseResult queryRechargeDetails(@RequestBody ReqParam param){
		RechargeRecord rr=new RechargeRecord();
		rr.setPhoneNumbe(param.getPhoneNumber());
		List<RechargeRecord>  rechargeRecordList = rechargeRecordService.queryList(rr);
		List<RechargeRecordVo> voList = new ArrayList<RechargeRecordVo>();
		for(int i=0; i<rechargeRecordList.size(); i++){
			RechargeRecordVo rrv = new RechargeRecordVo();
			rrv.setRechargeAmount(rechargeRecordList.get(i).getRechargeAmount().toString());
			rrv.setRechargeDuration(rechargeRecordList.get(i).getRechargeDuration());
			rrv.setRechargeTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rechargeRecordList.get(i).getCreateTime()));
			voList.add(rrv);
		}
		RechargeRecordVo res = new RechargeRecordVo();
		res.setOpenID(param.getOpenID());
		res.setPhoneNumber(param.getPhoneNumber());
		res.setRechargeList(voList);
		res.setOpenID(param.getOpenID());
		res.setPhoneNumber(param.getPhoneNumber());
		return new ResponseResult(res);
	}
	
	/**
	 * 查看用户消费明细
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryConsumedDetails",method=RequestMethod.POST)
	private ResponseResult queryConsumedDetails(@RequestBody ReqParam param){
		ConsumedDetails cd=new ConsumedDetails();
		cd.setPhoneNumber(param.getPhoneNumber());
		List<ConsumedDetails> rs=consumedDetailsService.queryList(cd);
		ConsumedDetailsVo vo=new ConsumedDetailsVo();
		List<ConsumedDetailsVo> vos=new ArrayList<ConsumedDetailsVo>();
		for(ConsumedDetails consumed:rs){
			ConsumedDetailsVo c=new ConsumedDetailsVo();
			c.setConsumedDuration(consumed.getConsumedDuration());
			c.setConsumedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(consumed.getCreateTime()));
			vos.add(c);
		}
		vo.setOpenID(param.getOpenID());
		vo.setPhoneNumber(param.getPhoneNumber());
		vo.setConsumedList(vos);
		return new ResponseResult(vo);
	}
	
	/**
	 * 查询充值套餐列表
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryPackageList",method=RequestMethod.POST)
	private ResponseResult queryPackageList(@RequestBody ReqParam param){
		List<RechargePackage> rechargePackages = rechargePackageService.queryRechargeListByLimit(new RechargePackage());
		List<RechargePackageVo> vos = new ArrayList<RechargePackageVo>();
		RechargePackageVo vors = new RechargePackageVo();
		System.err.println("----rechargePackages---"+rechargePackages.size());
//		for (RechargePackage rechargePackage : rechargePackages) {
		for(int i=0;i<rechargePackages.size();i++){
			System.err.println("---rechargePackages.get(i)---"+rechargePackages.get(i).toString());
			RechargePackage rechargePackage = rechargePackages.get(i);
			RechargePackageVo vo = new  RechargePackageVo();
			vo.setPackageID(rechargePackage.getId());
			vo.setPackageName(rechargePackage.getPackageName());
			vo.setPackageName(rechargePackage.getPackageName());
			vo.setRechargeAmount(rechargePackage.getRechargeAmoun().toString());
			vo.setRechargeDuration(rechargePackage.getRechargeDuration().toString());
			vos.add(vo);
		}
		vors.setPackageList(vos);
		return new ResponseResult(vors);
	}
	
	/**
	 * 发送用户验证码
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="queryUserRegStatus",method=RequestMethod.POST)
	private ResponseResult queryUserStatus(@RequestBody ReqParam param){
		return new ResponseResult(usersService.queryUserRegStatus(param.getOpenID())); 
	}
	

	/**
	 * 用户登录
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="login",method=RequestMethod.POST)
	private ResponseResult userLogin(@RequestBody ReqParam param){
		return new ResponseResult(usersService.login(param.getOpenID(), param.getPhoneNumber(), param.getIdentCode())); 
	}
	

	/**
	 * 发送用户验证码
	 * @param userID 用户ID
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="sendCode",method=RequestMethod.POST)
	private ResponseResult sendCode(@RequestBody ReqParam param){
		usersService.sendCode(param.getPhoneNumber());
		return new ResponseResult(null); 
	}
	
	

	/**
	 * 测试Redis集群
	 * @param phoneNumber 用户手机号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="testRedis",method=RequestMethod.POST)
	private ResponseResult TestRedis(@RequestBody ReqParam param){
		System.out.println("----"+param);
		String rs1 = jedisCluster.setex("bbb", 30, "12345555");
		String rs2 = jedisCluster.get("bbb");
//		String rs2 = redisService.set("13", "4321", 10);
		System.err.println("---rs1---"+rs1);
		System.err.println("---rs2---"+rs2);
		return new ResponseResult(null); 
	}
	
	
}
