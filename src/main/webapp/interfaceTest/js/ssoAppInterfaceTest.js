//sso-app接口校验
//请求防盗刷的机制是根据请求参数和header属性加密校验。首先在客户端设置请求header里devid等五个属性，这五个属性字母表顺序排序拼接MD5加密一次，
//然后作为一个secret参数拼到请求参数之后字母表顺序排序后MD5加密，形成header里一个program-sign属性。请求参数名字列表字典排序构成program-params参数。
//服务器端根据program-params获取参数值，并根据header属性按同样的方法运算一遍，和header里参数program-sign做对比，一致认为是合法的，不一致认为是非法请求。
var test={
	devid: "41C3AB17-824D-4073-8121-53CA3122B0E4",
	version: "1.0.0",	
	timestamp: 1473219040809,
	token: "token",
    random: 3264720759,
	contextUrl:"",
    innerApiUrl:"",

	//自动初始化contextUrl
	initContextUrl:function(){
		var url = window.location.host;
		url="http://"+url+"/sso-app/";
		$("#contextUrlInit").val(url);
		//test.contextUrl = url;
	},
	
	//重设header中属性初始化值
	resetHeaderProperty:function(){
        test.contextUrl = $("#contextUrlInit").val();
        test.innerApiUrl = $("#innerApiUrl").val();
		test.devid=$("#devidInit").val();
		test.version=$("#versionInit").val();
		test.timestamp=$("#timestampInit").val();
		test.token=$("#tokenInit").val();
		test.random=$("#randomInit").val();
		alert("初始化设置完成，请看console日志！");
		console.log("init test:"+JSON.stringify(test));
	},
	//注册
	register:function(){
		//alert();
		var username_=$('#usernameRegister').val();
		var nickname_=$('#nicknameRegister').val();
		var email_=$('#emailRegister').val();
		var password_=$('#passwordRegister').val();
		var password_2=$.md5(password_);
		var siteid_=$('#siteidRegister').val();
		if(username_==""||email_==""||password_==""){
			alert("注册，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/register",
			type:'post',
			data:{devid:test.devid,email:email_,password:password_2,username:username_,nickname:nickname_,siteid:siteid_},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignReg());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,password,email,siteid,username");
            },
			success:function(data){
				//alert();
				console.log("注册接口-结果： "+JSON.stringify(data));
				$("#registerResult").text(JSON.stringify(data, null, 4));
				$('#emailLogin').val(email);
				$('#passwordLogin').val(password_);
				
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到注册的program-sign
	getProgramSignReg:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var password_=$('#passwordRegister').val();
		var password_2=$.md5(password_);
		var email_=$('#emailRegister').val();
		var username_=$('#usernameRegister').val();
		var siteid_=$('#siteidRegister').val();
		var sBuffer="devid="+test.devid+"&password="+password_2+"&email="+email_+"&siteid="+siteid_+"&username="+username_+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("注册接口-sBuffer： "+sBuffer);
		var programSign = $.md5(sBuffer);
		console.log("注册接口-program-sign： "+programSign);
		return programSign;
	},
	
	//手机登录
	login:function(){
		//alert();
		var email_=$('#emailLogin').val();
		var password_=$('#passwordLogin').val();
		var password_2=$.md5(password_);
		if(email==""||password_==""){
			alert("登录，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/login",
			type:'post',
			data:{devid:test.devid,email:email,password:password_2},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignLog());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,password,email");
            },
			success:function(data){
				//alert();
				console.log("登录接口-结果： "+JSON.stringify(data,null,"\t"));
				if(data.msg=="success"){
					test.token=data.value.token;
					$("[name='token']").val(test.token);
					$("#uidLogout").val(data.value.uid);
					$("#uidUpload").val(data.value.uid);
				}
				$("#loginResult").html(JSON.stringify(data,null,"\t"));
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到登录的program-sign
	getProgramSignLog:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var password_=$('#passwordLogin').val();
		var password_2=$.md5(password_);
		var email=$('#emailLogin').val();
		var sBuffer="devid="+test.devid+"&password="+password_2+"&email"+email+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("登录接口-sBuffer： "+sBuffer);
		console.log("登录接口-program-sign： "+$.md5(sBuffer));
		return $.md5(sBuffer);
	},
	
	//邮箱登录
	loginByEmail:function(){
		//alert();
		var email_=$('#emailLogin').val();
		var password_=$('#passwordLoginByEamil').val();
		var password_2=$.md5(password_);
		if(email_==""||password_==""){
			alert("登录，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/login",
			type:'post',
			data:{devid:test.devid,email:email_,password:password_2},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignLogByEmail());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,password,email");
            },
			success:function(data){
				//alert();
				console.log("登录接口-结果： "+JSON.stringify(data,null,"\t"));
				if(data.msg=="success"){
					test.token=data.value.token;
					$("[name='token']").val(test.token);
					$("#uidLogout").val(data.value.uid);
					$("#uidUpload").val(data.value.uid);
				}
				$("#loginByEmailResult").html(JSON.stringify(data,null,"\t"));
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到登录的program-sign
	getProgramSignLogByEmail:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var password_=$('#passwordLoginByEamil').val();
		var password_2=$.md5(password_);
		var email_=$('#emailLogin').val();
		var sBuffer="devid="+test.devid+"&password="+password_2+"&email="+email_+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("登录接口-sBuffer： "+sBuffer);
		console.log("登录接口-program-sign： "+$.md5(sBuffer));
		return $.md5(sBuffer);
	},
	
	//用户名登录
	loginByUsername:function(){
		//alert();
		var username_=$('#usernameLogin').val();
		var password_=$('#passwordLoginByUsername').val();
		var password_2=$.md5(password_);
		if(username_==""||password_==""){
			alert("登录，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/login",
			type:'post',
			data:{devid:test.devid,username:username_,password:password_2},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignLogByUsername());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,password,username");
            },
			success:function(data){
				//alert();
				console.log("登录接口-结果： "+JSON.stringify(data,null,"\t"));
				if(data.msg=="success"){
					test.token=data.value.token;
					$("[name='token']").val(test.token);
					$("#uidLogout").val(data.value.uid);
					$("#uidUpload").val(data.value.uid);
				}
				$("#loginByUsernameResult").html(JSON.stringify(data,null,"\t"));
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到登录的program-sign
	getProgramSignLogByUsername:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var password_=$('#passwordLoginByUsername').val();
		var password_2=$.md5(password_);
		var username_=$('#usernameLogin').val();
		var sBuffer="devid="+test.devid+"&password="+password_2+"&username="+username_+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("登录接口-sBuffer： "+sBuffer);
		console.log("登录接口-program-sign： "+$.md5(sBuffer));
		return $.md5(sBuffer);
	},
	
	//第三方登录
	loginByOther:function(){
		//alert();
		var provider_=$('#provider').val();
		var oid_=$('#oid').val();
		var nickname_=$('#nickname1').val();
		if(provider_==""||oid_==""||nickname_==""){
			alert("第三方登录，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/loginByOther",
			type:'post',
			data:{devid:test.devid,provider:provider_,oid:oid_,nickname:nickname_},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignLogByOther());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,provider,oid,nickname");
            },
			success:function(data){
				//alert();
				console.log("第三方登录接口-结果： "+JSON.stringify(data,null,"\t"));
				if(data.msg=="success"){
					test.token=data.value.token;
					$("[name='token']").val(test.token);
					$("#uidLogout").val(data.value.uid);
					$("#uidUpload").val(data.value.uid);
				}
				$("#loginByOtherResult").html(JSON.stringify(data,null,"\t"));
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到第三方登录的program-sign
	getProgramSignLogByOther:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var provider_=$('#provider').val();
		var oid_=$('#oid').val();
		var nickname_=$('#nickname1').val();
		var sBuffer="devid="+test.devid+"&provider="+provider_+"&oid="+oid_+"&nickname="+nickname_+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("第三方登录接口-sBuffer： "+sBuffer);
		console.log("第三方登录接口-program-sign： "+$.md5(sBuffer));
		return $.md5(sBuffer);
	},
	
	//上传头像和显示
	uploadHeadImg:function(){
		if(!test.innerApiUrl) {
			alert("请填写内网API地址，并重新初始化！");
			return;
		}
		var form = new FormData(document.getElementById("uploadForm"));
		/*$.ajax({
			//url:test.contextUrl+"api/uploadPortrait/"+$("#uidUpload").val(),
			// http://59.108.92.233:8088/app_if/amuc/api/member/uploadImage
			url:test.innerApiUrl+"amuc/api/member/uploadImage",
			type:"get",
			data:form,
			processData:false,
			contentType:false,
			success:function(data){
				console.log("上传头像成功！"+JSON.stringify(data,null,"\t"));
				if(data.code==1){
					$("#uploadResult").html(JSON.stringify(data,null,"\t")+"<br/><img style='width:100px;height:100px;' src='"+data.value.head+"' />");
				}else{
					$("#uploadResult").html(JSON.stringify(data,null,"\t"));
				}
			},
			error:function(e){
				alert("请求错误！！");
			}
		});*/


	},
	
	//退出
	logout:function(){
		//alert();
		var uid_=$('#uidLogout').val();
		var token_=$('#tokenLogout').val();
		if(uid_==""||token_==""){
			alert("退出，一个或多个参数为空！");
			return;
		}
		$.ajax({
			url:test.contextUrl+"api/logout",
			type:'post',
			data:{devid:test.devid,uid:uid_,token:token_},
			dataType:"json",
			beforeSend: function(request) {
				request.setRequestHeader("program-sign", test.getProgramSignLogout());
            	request.setRequestHeader("devid", test.devid);
				request.setRequestHeader("version", test.version);
				request.setRequestHeader("timestamp", test.timestamp);
				request.setRequestHeader("token", test.token);
				request.setRequestHeader("random", test.random);
				request.setRequestHeader("program-params","devid,token,uid");
            },
			success:function(data){
				//alert();
				console.log("退出接口-结果： "+JSON.stringify(data, null, 4));
				$("#exitResult").text(JSON.stringify(data, null, 4));
			},
			complete: function(XMLHttpRequest, textStatus){
		        this;  // 调用本次AJAX请求时传递的options参数
		    },
		    error: function (XMLHttpRequest, textStatus, errorThrown) {
		    	if(textStatus == 'abort'){
		        	XMLHttpRequest.abort();
		        }else{
		        	
		        }
		    }
		});
	},
	//得到退出的program-sign
	getProgramSignLogout:function(){
		var buffer="devid="+test.devid+"&random="+test.random+"&timestamp="+test.timestamp+"&token="+test.token+"&version="+test.version;
		var uid_=$('#uidLogout').val();
		var token_=$('#tokenLogout').val();
		var sBuffer="devid="+test.devid+"&token="+token_+"&uid="+uid_+"&";
		
		sBuffer=sBuffer+"secret="+$.md5(buffer);
		console.log("退出接口-sBuffer： "+sBuffer);
		console.log("退出接口-program-sign： "+$.md5(sBuffer));
		return $.md5(sBuffer);
	},
}


$(function(){
	//初始化后台地址
	test.initContextUrl();
	//各按钮点击事件绑定
	$("#init").click(test.resetHeaderProperty);
	$("#register").click(test.register);
	$("#login").click(test.login);
	$("#loginByEmail").click(test.loginByEmail);
	$("#loginByUsername").click(test.loginByUsername);
	$("#loginByOther").click(test.loginByOther);
	//$("#upload").click(test.uploadHeadImg);
	$("#exit").click(test.logout);
	//双击清空结果值
	$("pre").dblclick(function(){
  		$("pre").html("结果在这里...");
	});
});