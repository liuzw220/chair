<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <% String chair= "ch"; %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>设备管理</title>
</head>
<body>
	<div>
    <table class="easyui-datagrid" id="consumeList" title="套餐列表" 
	       data-options="singleSelect:false,collapsible:true,pagination:true,url:'/<%=chair%>/device/list',method:'post',pageSize:5,toolbar:toolbar,pageList:[2,5,10]">
	    <thead>
	        <tr>
	        	<th data-options="field:'ck',checkbox:true"></th>
	        	<th data-options="field:'id',width:60">ID</th>
	            <th data-options="field:'packageName',width:200">设备编号</th>
	            <th data-options="field:'consumedDuration',width:100">设备型号</th>
	            <th data-options="field:'status',width:100">店铺位置</th>
	            <th data-options="field:'status',width:100">店铺名称</th>
	            <th data-options="field:'status',width:100">代理名称</th>
	            <th data-options="field:'status',width:100">厂家名称</th>
	            <th data-options="field:'createTime',width:130,align:'center',formatter:formatDate">创建日期</th>
	            <th data-options="field:'lastUpdate',width:130,align:'center',formatter:formatDate">更新日期</th>
	        </tr>
	    </thead>
	</table>
	</div>
<div id="consumeAdd" class="easyui-window" title="新增设备" data-options="modal:true,closed:true,iconCls:'icon-save',href:'/<%=chair%>/page/device-add'" style="width:800px;height:600px;padding:10px;">
        The window content.
</div>
<script type="text/javascript">
function formatDate(val,row){
	var now = new Date(val);
	return now.format("yyyy-MM-dd hh:mm:ss");
}
function formatBirthday(val,row){
	var now = new Date(val);
	return now.format("yyyy-MM-dd");
}
function getSelectionsIds(){
	var consumeList = $("#consumeList");
	var sels = consumeList.datagrid("getSelections");
	var ids = [];
	for(var i in sels){
		ids.push(sels[i].id);
	}
	ids = ids.join(",");
	return ids;
}
var toolbar = [{
    text:'新增',
    iconCls:'icon-add',
    handler:function(){
    	$('#consumeAdd').window('open');
    }
}];
</script>
</body>
</html>