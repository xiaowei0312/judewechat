<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta id="viewport" name="viewport"
	content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>聚德购物商城</title>
<meta name="Keywords" content="聚德购物|聚德购物商城|聚德养老购物">
<meta name="Description" content="聚德购物是社会新型消费养老电子商务平台！">
<meta content="telephone=no" name="format-detection">
<meta content="email=no" name="format-detection">
<meta http-equiv="Cache-Control"
	content="no-cache, no-store, must-revalidate">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath }/common/base.css">
<script src="${pageContext.request.contextPath }/common/hm.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/common/jquery.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/common/layer.min.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath }/common/layer.css"
	id="layui_layer_skinlayercss">
<script type="text/javascript"
	src="${pageContext.request.contextPath }/common/template.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/common/base.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath }/common/login.js"></script>
<link type="text/css" href="${pageContext.request.contextPath }/css/style_inviter.css" rel="stylesheet">
<link type="text/css" href="${pageContext.request.contextPath }/css/inviter.css" rel="stylesheet">
<script>
	document.querySelector("html").style.fontSize=document.documentElement.clientWidth/10+"px";
	/*屏幕的宽度=10rem  移动端用*/
</script>

<script src="http://res.wx.qq.com/open/js/jweixin-1.2.0.js"></script>
<script type="text/javascript">
	wx.config({
		debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。  
		appId : '${jsApiSign.appId}', // 必填，公众号的唯一标识  
		timestamp : '${ jsApiSign.timestamp}', // 必填，生成签名的时间戳  
		nonceStr : '${ jsApiSign.nonceStr}', // 必填，生成签名的随机串  
		signature : '${ jsApiSign.signature}',// 必填，签名，见附录1  
		jsApiList : [ 'checkJsApi', 'chooseImage', 'previewImage',
		        'onMenuShareAppMessage','onMenuShareTimeline','onMenuShareQQ','onMenuShareWeibo','onMenuShareQZone',
				'uploadImage', 'downloadImage', 'getNetworkType',//网络状态接口
				'openLocation',//使用微信内置地图查看地理位置接口
				'getLocation' //获取地理位置接口
		]
	// 必填，需要使用的JS接口列表，所有JS接口列表见附录2  
	});

	wx.ready(function() {
				//alert(window.location.href);
		wx.onMenuShareAppMessage({
			title : '省钱就找聚德网，注册就送600元',
			desc: '您的朋友邀请您注册聚德网', // 分享描述
		    link: 'http://www.0352jdw.com/wechat/user/beInviterRegistPage.action?inviterUserId=${sessionScope.user.id}', 
		    			// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		   	//link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx20be3e329920dba3&redirect_uri=' + 
		   	//		encodeURI("http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}") + 
		   	//		'&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect',
		    imgUrl: 'http://www.0352jdw.com/wechat/img/getheadimg.png', // 分享图标
		    type: 'link', // 分享类型,music、video或link，不填默认为link
		    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			success : function(res) {
				//alert('my success.');
			},
			cancel : function(res) {
				//alert('用户拒绝授权获取地理位置');
			}
		});
		
		wx.onMenuShareTimeline({
			title : '省钱就找聚德网，注册就送600元',
			//desc: '您的朋友邀请您注册聚德网', // 分享描述
		    link: 'http://www.0352jdw.com/wechat/user/beInviterRegistPage.action?inviterUserId=${sessionScope.user.id}', 
		    			// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		   	//link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx20be3e329920dba3&redirect_uri=' + 
		   	//		encodeURI("http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}") + 
		   	//		'&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect',
		    imgUrl: 'http://www.0352jdw.com/wechat/img/getheadimg.png', // 分享图标
		    //type: 'link', // 分享类型,music、video或link，不填默认为link
		    //dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			success : function(res) {
				//alert('my success.');
			},
			cancel : function(res) {
				//alert('用户拒绝授权获取地理位置');
			}
		});
		
		wx.onMenuShareQQ({
			title : '省钱就找聚德网，注册就送600元',
			desc: '您的朋友邀请您注册聚德网', // 分享描述
		    link: 'http://www.0352jdw.com/wechat/user/beInviterRegistPage.action?inviterUserId=${sessionScope.user.id}', 
		    			// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		   	//link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx20be3e329920dba3&redirect_uri=' + 
		   	//		encodeURI("http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}") + 
		   	//		'&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect',
		    imgUrl: 'http://www.0352jdw.com/wechat/img/getheadimg.png', // 分享图标
		    //type: 'link', // 分享类型,music、video或link，不填默认为link
		    //dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			success : function(res) {
				//alert('my success.');
			},
			cancel : function(res) {
				//alert('用户拒绝授权获取地理位置');
			}
		});
		
		wx.onMenuShareWeibo({
			title : '省钱就找聚德网，注册就送600元',
			desc: '您的朋友邀请您注册聚德网', // 分享描述
		    link: 'http://www.0352jdw.com/wechat/user/beInviterRegistPage.action?inviterUserId=${sessionScope.user.id}', 
		    			// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		   	//link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx20be3e329920dba3&redirect_uri=' + 
		   	//		encodeURI("http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}") + 
		   	//		'&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect',
		    imgUrl: 'http://www.0352jdw.com/wechat/img/getheadimg.png', // 分享图标
		    //type: 'link', // 分享类型,music、video或link，不填默认为link
		    //dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			success : function(res) {
				//alert('my success.');
			},
			cancel : function(res) {
				//alert('用户拒绝授权获取地理位置');
			}
		});
		
		wx.onMenuShareQZone({
			title : '省钱就找聚德网，注册就送600元',
			desc: '您的朋友邀请您注册聚德网', // 分享描述
		    link: 'http://www.0352jdw.com/wechat/user/beInviterRegistPage.action?inviterUserId=${sessionScope.user.id}', 
		    			// 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
		   	//link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx20be3e329920dba3&redirect_uri=' + 
		   	//		encodeURI("http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}") + 
		   	//		'&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect',
		    imgUrl: 'http://www.0352jdw.com/wechat/img/getheadimg.png', // 分享图标
		    //type: 'link', // 分享类型,music、video或link，不填默认为link
		    //dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			success : function(res) {
				//alert('my success.');
			},
			cancel : function(res) {
				//alert('用户拒绝授权获取地理位置');
			}
		});
	});
	//初始化jsapi接口 状态
	wx.error(function(res) {
		alert("调用微信jsapi返回的状态:" + res.errMsg);
	});
</script>
</head>

<body>
	<div class="containter">
		<div class="innerhead">
			<div class="headImg">
				<img src="${pageContext.request.contextPath }/img/head.png">
			</div>
		</div>
		<div class="inner">
			<div class="font_16" >
				<h4></h4>
				<p style="font-size:0.4rem;line-height:30px">聚德网成功上线离不开各位亲爱会员的鼓励和支持，
				老会员通过转发朋友圈，邀请好友注册成为聚德网会员，好友直接获赠600积分，妥妥的福利。新会员注册成功并产生第一笔消费，老会员获赠10积分，共享时代，分享是种潮流，转发领奖。</p>
			 	<p>&nbsp;</p>
			</div>
			<div class="btn">
				<button onclick="submitMess();">我要推荐</button>
			</div>
			<!-- 
			<div class="ewm">
				<p>想了解更多，长按二维码</p>
				<img src="${pageContext.request.contextPath }/img/ewm.png">
			</div>
			 -->
		</div>
	</div>
	<div id="messageDiv">
		<div class="blackBg"></div>
		<div id="messageShow">
			<img src="${pageContext.request.contextPath }/img/pic-4.png"/>
			<a class="colsedBtn" href="javascript:;" onclick="closedMess()">
				<img src="${pageContext.request.contextPath }/img/pic-5.png"/></a>
		</div>
	</div>
	<script>
		function submitMess(){
			$("#messageDiv").show();
		}
		function closedMess(){
			$("#messageDiv").hide();
		}
	</script>
</body>
</html>