<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <% String chair= "ch"; %>
<div style="padding:10px 10px 10px 10px">
	<form id="shopContent" method="post">
	    <table cellpadding="5">
	    	<input type="hidden" name="id" style="width: 280px;"></input>
	    	<input type="hidden" name="type" value="3"></input> <!-- 添加商家的默认类型 -->
		    <tr>
		        <td>所属代理:</td>
		        <td><input class="easyui-combobox" name="proxyId" data-options="valueField:'proxyId',textField:'proxyName',url:'/<%=chair%>/manager/queryFactoryList',prompt:'请选择'" style="width: 280px;"></input></td>
		    </tr>
	        <tr>
	            <td>商家名称:</td>
	            <td><input class="easyui-textbox" type="text" name="shopName" data-options="required:true" style="width: 280px;"></input></td>
	        </tr>
	        <tr>
	            <td>商家地址:</td>
	            <td><input class="easyui-textbox" type="text" name="shopLocation" data-options="required:true" style="width: 280px;"></input></td>
	        </tr>
	        <tr>
	            <td>登陆用户名:</td>
	            <td><input class="easyui-textbox" type="text" name="user" data-options="required:true" style="width: 280px;"></input></td>
	        </tr>
	        <tr>
	            <td>登陆密码:</td>
	            <td><input class="easyui-textbox" type="text" name="password" data-options="required:true" style="width: 280px;"></input></td>
	        </tr>
	    </table>
	</form>
	<div style="padding:5px">
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitFormByShop()">提交</a>
	    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearFormByShop()">重置</a>
	</div>
</div>
<script type="text/javascript">
	function submitFormByShop(){
		if(!$('#shopContent').form('validate')){
			$.messager.alert('提示','商家表单还未填写完成!');
			return ;
		}
		$.post('/<%=chair%>/manager/edit',$("#shopContent").serialize(), function(data){
			if(data.status == 200){
				$.messager.alert('提示','编辑商家成功!');
				$('#shopEdit').window('close');
				$("#shopList").datagrid("reload");
//				clearFormByProxy();
			}else{
				$.messager.alert('提示','编辑商家失败!');
			}
		});
	}
	function clearFormByShop(){
		$('#shopContent').form('reset');
	}
	
</script>