<%@page contentType="text/html; charset=utf-8" %>

<html>

	<head>
		<title>用户名-密码认证登陆</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<script src="<%=request.getContextPath() %>/res/js/jquery/jquery-1.7.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			
			jQuery.fn.center = function () 
			{
			    this.css("position","absolute");
			    this.css("top",  Math.max( 0, (($(window).height() - this.outerHeight()) / 2) + $(window).scrollTop())  + "px");
			    this.css("left", Math.max( 0, (($(window).width()  - this.outerWidth()) / 2)  + $(window).scrollLeft()) + "px");
			    return this;
			};
			 
			// To initially run the function:
			function centreContainerDiv()
			{
				// $(window).resize();
				$("#fieldContainerDiv").center();
			}
			
		</script>
		<style type="text/css">
		</style>
	</head>
	
	<body onload="centreContainerDiv();">
	
		<script type="text/javascript">
		
			function changeValue()
			{
				var authenModeFlag = document.getElementsByName("ssoAuthenModeOn")[0];
				var username       = document.getElementsByName("username")[0];
				var password       = document.getElementsByName("password")[0];
				
				var flag = authenModeFlag.checked;
				if (flag)
				{
					// 选中
					authenModeFlag.value = true; // sso认证登录
					username.disabled    = true; // 禁用用户名-密码登录
					password.disabled    = true;
					// 若果有， 清空提示信息
					// displayMsg(msg) 
					$("#message").hide();
					// 清空输入框
					$("#username").val('');
					$("#password").val('');
				}
				else
				{
					// 没有选中
					authenModeFlag.value = false;
					username.disabled    = false;
					password.disabled    = false;
				}
			}
			
			/********************
			 AJAX验证用户名 
			 *******************/
			function checkUserName()
			{
				var flag = false;
				
				var authenModeFlag = document.getElementsByName("ssoAuthenModeOn")[0];
				if (authenModeFlag.checked) 
				{
					flag = true;
					return flag;
				}
				
				// var url = "develop/loginAuth!checkUserName.jhtml";
				var url = "loginAuth!checkUserName.jhtml";
				
			    var username = $.trim($("#username").val()); 
			    if (username == '') 
			    {
			    	var msg = '用户名不能为空！';
			    	$("#message").css("display", "block");
					$("#message").css("color", "red");
					$("#message").html(msg);
			    	return;
			    }
			    else
		    	{
			    	var data = { username : $.trim($("#username").val()) };
					
					$.post
					(
						url, 
						data, 
						function(json) 
						{
							var jsonRetObj = eval("(" + json + ")");
							var ret = jsonRetObj.ret;
							var msg = jsonRetObj.msg;
							if (ret == 0) 
							{
								displayMsg(msg);
								flag = false;
							}
							
							if (ret == 1) 
							{
								flag = true;
								displayMsg(msg);
							}
							
						}, 
						"json"
					);
		    	}
			    
			    return flag;
			}
			
			function displayMsg(msg) 
			{
				// 显示信息
				$("#message").css("display", "block");
				$("#message").css("color", "red");
				$("#message").html(msg);
			}

			function checkNull(username, password) 
			{
				var msg = '';

				if (username == '' && password == '') 
				{
					msg = '用户名、密码不能为空！';
				} 
				else if (username == '') 
				{
					msg = '用户名不能为空！';
				} 
				else if (password == '') 
				{
					msg = '密码不能为空！';
				}

				if (msg == '')
					return true;
				else 
				{
					displayMsg(msg);
					return false;
				}
			}
			
			/**************************
			 AJAX执行用户名-密码认证登录
			 *************************/
			function executePwdAuth()
			{
				/* var flag = false; */
				
				// var url = "develop/loginAuth!checkSignInInfo.jhtml";
				var url = "loginAuth!checkSignInInfo.jhtml";
			    
			    var username = $.trim($("#username").val()); 
			    var password = $.trim($("#password").val());
			    var data = { username : username , password : password };
			    
			    // 异步验证
			    $.ajax({
					type : "post",
					url  : url,
					data : data,
					//contentType : "application/json; charset=utf-8",
					dataType : "json",
					beforeSend : function() {
						// $("#message").html("loading……");
					}
				}).success(function(data) {
					var jsonRetObj = eval("(" + data + ")");
					var ret = jsonRetObj.ret;
					var msg = jsonRetObj.msg;
					// 验证不通过
					if (ret == 0) 
					{
						displayMsg(msg);
						return false;
					}
					// 验证通过
					if (ret == 1) 
					{
						// 显示“”字串
						displayMsg(msg);
						/**  提交form **/
						// var url = "develop/loginAuth!signInSwitch.jhtml";
						var url = "loginAuth!signInSwitch.jhtml";
						
					    var data = 
						{
							username    : $.trim($("#username").val()),
							password    : $.trim($("#password").val()),
							requestURL  : $.trim($("input[name='requestURL']").val()),
							queryString : $.trim($("input[name='queryString']").val())
						};

						// 异步提交
						// way1
						/*
						$.post
						(
							url, 
							data, 
							function(json) 
							{
								var jsonRetObj = eval("(" + json + ")");
								var ret = jsonRetObj.ret;
								var msg = jsonRetObj.msg;
								if (ret == 0) 
								{
									displayMsg(msg);
									flag = false;
									return flag;
								}
								if (ret == 1) 
								{
									flag = true;
									return flag;
								}
							}, 
							"json"
					    );
						*/
						
						// way2
						$.ajax({
							type : "post",
							url  : url,
							data : data,
							//contentType : "application/json; charset=utf-8",
							dataType : "json",
							beforeSend : function() {
								// $("#message").html("loading……");
							}
						}).success(function(data) {
							var jsonRetObj = eval("(" + data + ")");
							var ret = jsonRetObj.ret;
							var msg = jsonRetObj.msg;
							if (ret == 0) 
							{
								displayMsg(msg);
								return false;
							}
							if (ret == 1) 
							{
								location.href = msg;
							}
						});
						
					}
				}).error(function() {
					// 
				}).complete(function(xhr, textStatus) {
					// 
				});
			}
			
			/**********************
			 AJAX执行SSO认证登录
			 *********************/
			function executeSSOAuth()
			{
				// var url = "develop/loginAuth!signInSwitch.jhtml";
				var url = "loginAuth!signInSwitch.jhtml";
				
				var ssoAuthenModeOn = document.getElementsByName("ssoAuthenModeOn")[0];
				var flag = ssoAuthenModeOn.checked;
				var dataValue = (flag == false ? 0 : 1);
				var data = 
				{ 
					ssoAuthenModeOn : dataValue,  
					requestURL  : $.trim($("input[name='requestURL']").val()), // 获取隐藏字段的值
					queryString : $.trim($("input[name='queryString']").val())
				};
				
				/*
				$.post
				(
					url, 
					data, 
					function(json) 
					{
						var jsonRetObj = eval("(" + json + ")");
						var ret = jsonRetObj.ret;
						var msg = jsonRetObj.msg;
						if (ret == 0) 
						{
							displayMsg(msg);
							return false;
						}
						if (ret == 1) 
						{
							location.href = msg;
						}
					}, 
					"json"
			    );
				*/
				
				$.ajax({
					type : "post",
					url  : url,
					data : data,
					//contentType : "application/json; charset=utf-8",
					dataType : "json",
					beforeSend : function() {
						$("#message").html("processing……");
						$("#message").css("color", "black");
					}
				}).success(function(data) {
					var jsonRetObj = eval("(" + data + ")");
					var ret = jsonRetObj.ret;
					var msg = jsonRetObj.msg;
					if (ret == 0) 
					{
						displayMsg(msg);
						return false;
					}
					if (ret == 1) 
					{
						location.href = msg;
					}
				}).complete( function(xhr, textStatus) {
					// HideLoading 
					$("#message").hide();
				}).error( function() {
					// 请求出错处理
				});
				
			}

			function submitData() 
			{
				// SSO login
				var authenModeFlag = document.getElementsByName("ssoAuthenModeOn")[0];
				var flag = authenModeFlag.checked;
				
				if (flag == true)
				{
					// 执行SSO登录
					executeSSOAuth();
					return true;
				}
					
				// username & password login
				var username = $.trim($("input[name='username']").val());
				var password = $.trim($("input[name='password']").val());

				// 前端校验是否为空
				if (!checkNull(username, password))
					return false;

				// 1. 后台校验用户名、密码是否正确
				// 2. 执行用户名-密码登录
				executePwdAuth();
			}
			
			function clearFieldText()
			{
				$("input[name='username']").attr("value", "");
				$("input[name='password']").attr("value", "");
				$("#message").hide();
				return true;
			}
			
		</script>
		
		<!--  
		<div id="message">
			<font id="msgFont" color="red">${msg}</font>
		</div> 
		-->
		<!--  使用AJAX发送请求， 不用form提交！！
		<form name="pwdLoginForm" action="authority!signInSwitch.jhtml" method="post">
		-->
		<div id="fieldContainerDiv">
			<input name="requestURL"  type="hidden" value="${requestURL}"> 	<!-- http://localhost:8080/cmpp-dev/develop/index.jhtml -->
			<input name="queryString" type="hidden" value="${queryString}"> 		<!-- node id -->
			<table>
				<tbody align="center" valign="middle"> <!-- html5标签, 在此处设置表格的样式 -->
					<!-- 显示提示信息 -->
					<tr>
						<td colspan="2" align="left" width="430" height="25">
							<div id="message">
								<font id="msgFont" color="red">${msg}</font>
							</div> 
						</td>
					</tr>
					<!-- 直接跳转到SSO -->
					<tr>
						<td width="100">sso authen</td>
						<td align="left"> 
							<input name="ssoAuthenModeOn" value="true" checked="checked" type="checkbox" onclick="changeValue();" title="选择SSO认证/不选择">
						</td>
					</tr>
					
					<tr>
						<td width="100">username</td>
						<td align="left">
							<input id="username" name="username" type="text" disabled="disabled" onblur="checkUserName();">
						</td>
					</tr> 
					<tr>
						<td width="100">password</td>
						<td align="left">
							<input id="password" name="password" type="password" disabled="disabled">
						</td>
					</tr>
					
					<tr>
						<td></td>
						<td align="left">
							<input id="submitBtn" name="submitBtn" value="submit" type="button" onclick="return submitData();"> 
							<input id="reset"     name="reset"     value="reset"  type="reset"  onclick="clearFieldText();" />
						</td>
					</tr>
					<!-- onclick="return clickCheck();"  -->
				</tbody>
			</table>
		</div>
		<!--  
		</form>
		-->
	</body>

</html>