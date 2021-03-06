package com.chair.manager.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.chair.manager.bean.EasyUIResult;
import com.chair.manager.controller.DateUtils;
import com.chair.manager.exception.ChairException;
import com.chair.manager.mapper.DeviceMapper;
import com.chair.manager.pojo.Device;
import com.chair.manager.pojo.DeviceLog;
import com.chair.manager.vo.DeviceVo;
import com.github.pagehelper.PageInfo;

import redis.clients.jedis.JedisCluster;

@Service
public class DeviceService extends BaseService<Device> {
	private static Logger logger = Logger.getLogger(DeviceService.class);
	@Autowired
	private DeviceMapper deviceMapper;

	@Autowired
	private DeviceLogService deviceLogService;
	
	/**
	 * 根据唯一约束查询设备
	 * @param device
	 * @return
	 */
	public Device findByUnique(Device device) {
		List<Device> devices = deviceMapper.select(device);
		if (devices != null && devices.size() > 0) {
			return devices.get(0);
		} else
			return null;
	}
	
	/**
	 * 根据设备编号查询设备
	 * @param d
	 * @return
	 */
	public Device queryByDeviceNO(Device d) {
		return deviceMapper.queryByDeviceNO(d);
	}
	
	/**
	 * 新增或更新设备
	 * @param device
	 * @return
	 */
	public void saveOrUpdate(Device device){
		deviceMapper.saveOrUpdate(device);
	}
	
	
	/**
	 * 查询设备列表
	 */
	public DeviceVo queryDeviceList() {
		List<Device> devices =  deviceMapper.select(null);
		if(devices.size() <= 0){
			logger.info("查询不到设备列表");
			return null;
		}
		List<DeviceVo> deviceVos = new ArrayList<DeviceVo>();
		DeviceVo vos = new DeviceVo();
		for (Device device : devices) {
			DeviceVo vo = new DeviceVo();
			vo.setDeviceID(device.getId());
			vo.setDeviceNO(device.getDeviceNo());
			vo.setDeviceModel(device.getDeviceModel());
			vo.setShopID(device.getShopId());
			vo.setShopLocation(device.getShopLocation());
			vo.setShopName(device.getShopName());
			vo.setProxyID(device.getProxyId());
			vo.setProxyName(device.getProxyName());
			vo.setFacrotyID(device.getFacrotyId());
			vo.setFactoryName(device.getFactoryName());
			vo.setStatus(device.getStatus());
			vo.setCreateTime(device.getCreateTime().toString());
			vo.setLastUpdate(device.getLastUpdate().toString());
			deviceVos.add(vo);
		}
		vos.setDeviceList(deviceVos);
		return vos;
	}

	/**
	 * 分页查询设备信息
	 * @param page 页面
	 * @param rows 页面大小
	 * @return
	 */
	public EasyUIResult queryDeviceListForPage(Device device ,Integer page, Integer rows) {
		PageInfo<Device> pageInfo= super.queryListPage(device, page, rows);
		return new EasyUIResult(pageInfo.getTotal(), pageInfo.getList());
	}
	
	
	
	/**
	 * 判断设备是否正在使用
	 * 1.在线，2.不在线，3.正在使用
	 * @param d
	 * @return
	 */
	public Device judgeDeviceIsUsed(Device d){
		List<Device> deivces = this.queryList(d);
		Device device = null;
		String str1 = DateUtils.formatString(new Date(), "yyyy-MM-dd HH:mm:ss");
		if(deivces == null || deivces.size() == 0 || (device = deivces.get(0)) == null || deivces.get(0).getStatus() == 2){
			throw new ChairException("2001", "查询不到设备信息或设备不在线");
		}

		String str2 = device.getExpTime();	//获取过期时间
		if(!StringUtils.isEmpty(str1) && !StringUtils.isEmpty(str2)){
			//当前时间 > 设备运行结束时间 && 设备状态 == 3
			if(DateUtils.compareDate(str1, str2) && device.getStatus() == 3){
				//更新状态为1
				Device updateDevice = new Device();
				updateDevice.setId(device.getId());
				updateDevice.setDeviceNo(device.getDeviceNo());
				updateDevice.setStatus(1);
				updateDevice.setLastUpdate(new Date());
				this.updateSelective(updateDevice);
				
				//设备日志跟踪
				DeviceLog deviceLog = new DeviceLog();
				deviceLog.setDeviceNo(device.getDeviceNo());
				deviceLog.setDeviceStatus(1);	//在线
				deviceLog.setDeviceStatusDesc("在线");
				deviceLog.setCreateTime(new Date());
				deviceLog.setLastUpdate(new Date());
				deviceLogService.save(deviceLog);
				
				return updateDevice;
			}else{
				if(device.getStatus() != 3){
					return device;
				}
				throw new ChairException("2002", "正在使用");
			}
		}
		return device;
	}
	
	
	/**
	 * 根据设备Token查询设备信息
	 * @param token
	 * @return
	 */
	public Device queryDeviceByToken(String token){
		Device device = new Device();
		device.setDeviceToken(token);
		return deviceMapper.queryDeviceByToken(device);
	}

	/**
	 * 根据设备号更新设备
	 * @param device
	 */
	public void updateByDeviceNO(Device device) {
		deviceMapper.updateByDeviceNO(device);
	}

	
	
	/**
	 *  定时任务2：更新“正在使用”并且 “当前时间>消费结束时间”的设备状态
	 *	@since 2017年6月15日
	 *	@author yaoym
	 */
	public void updateUsingDeviceStatus() {
		deviceMapper.updateUsingDeviceStatus();
	}
}
