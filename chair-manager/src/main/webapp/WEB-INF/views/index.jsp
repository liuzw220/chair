<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <% String chair= "ch"; %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>管理台系统</title>
<jsp:include page="/commons/common-js.jsp"></jsp:include>

<style type="text/css">
	.content {
		padding: 10px 10px 10px 10px;
	}
</style>
</head>
<body class="easyui-layout">
    <div data-options="region:'west',title:'菜单',split:true" style="width:180px;">
    	<ul id="menu" class="easyui-tree" style="margin-top: 10px;margin-left: 5px;">
         	<li>
         		<span>充值套餐管理</span>
         		<ul>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/recharge-add'}">新增充值套餐</li>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/recharge-list'}">查询充值套餐</li>
	         	</ul>
         	</li>
         	<li>
         		<span>消费套餐管理</span>
         		<ul>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/consume-add'}">新增消费套餐</li>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/consume-list'}">查询消费套餐</li>
	         	</ul>
         	</li>
         	<li>
         		<span>数据管理</span>
         		<ul>
	         	<%-- 	<li data-options="attributes:{'url':'/<%=chair%>/page/factory-add'}">新增厂家</li>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/consume-list'}">新增代理</li>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/consume-list'}">新增商家</li> --%>
	         		<li data-options="attributes:{'url':'/<%=chair%>/page/device-list'}">新增设备</li>
	         	</ul>
         	</li>
         </ul>
    </div>
    <div data-options="region:'center',title:''">
    	<div id="tabs" class="easyui-tabs">
		    <div title="首页" style="padding:20px;">
		        	
		    </div>
		</div>
    </div>
    
<script type="text/javascript">
$(function(){
	$('#menu').tree({
		onClick: function(node){
			if($('#menu').tree("isLeaf",node.target)){
				var tabs = $("#tabs");
				var tab = tabs.tabs("getTab",node.text);
				if(tab){
					tabs.tabs("select",node.text);
				}else{
					tabs.tabs('add',{
					    title:node.text,
					    href: node.attributes.url,
					    closable:true,
					    bodyCls:"content"
					});
				}
			}
		}
	});
});
</script>
</body>
</html>