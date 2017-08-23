<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<!DOCTYPE html>
<!-- saved from url=(0034)http://m.58jude.com/member/home.do -->
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
	href="${pageContext.request.contextPath}/common/base.css">
<script src="${pageContext.request.contextPath}/common/hm.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/jquery.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/layer.min.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/common/layer.css"
	id="layui_layer_skinlayercss">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/template.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/base.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/common/home.js"></script>
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
		    link: 'http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}', 
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
		    link: 'http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}', 
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
		    link: 'http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}', 
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
		    link: 'http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}', 
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
		    link: 'http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${sessionScope.user.id}', 
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

<body ontouchstart="">
	<header class="header">
		<h2>会员中心</h2>
	</header>
	<div class="main">
		<div class="jui_cells" style="margin-top: 10px;">
			<div class="member_info clearfix">
				<div class="headArea">
					<em class="headImg"> <a> <img
							src="${sessionScope.user.headImgUrl}">
					</a>
					</em>
				</div>
				<div class="acctInfo">
					<p>${sessionScope.user.username }</p>
					<p>
						剩余积分：<span>${user.jfAccount.accountBalance }</span>分
					</p>
					<p class="userType">
						<span> 
							${sessionScope.user.role.roleName }
						</span>
					</p>
				</div>
			</div>
		</div>
		<div class="acctStatInfo">
			<div class="acctTitle">账户余额</div>
			<div class="acctNum balance">${user.moneyAccount.accountBalance }</div>
		</div>
		<div class="acctStatInfo">
			<table style="width: 100%;">
				<tbody>
					<tr>
						<td>
							<div class="acctTitle">当前聚财宝</div>
							<div class="acctNum">${user.dlbAccount.accountBalance }</div>
						</td>
						<td style="border-left: 1px solid #ddd;">
							<div class="acctTitle">当前养老金</div>
							<div class="acctNum">${user.yljAccount.accountBalance }</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="acctStatInfo">
			<table style="width: 100%;">
				<tbody>
					<tr>
						<td>
							<div class="acctTitle">当前聚德币</div>
							<div class="acctNum">${user.jdbAccount.accountBalance }</div>
						</td>
						<td>
							<div class="acctTitle">累计赠送</div>
							<div class="acctNum">${user.moneyAccount.totalPlatformIncomings }</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="acctStatInfo">
			<table style="width: 100%;">
				<tbody>
					<tr>
						<td>
							<div class="acctTitle">累计线下消费</div>
							<div class="acctNum">${user.moneyAccount.totalPlatformOutgoings }</div>
						</td>
						<td>
							<div class="acctTitle">累计线上消费</div>
							<div class="acctNum">${user.totalConsumOnline }</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="jui_cells_title">账户管理</div>
		<div class="jui_cells jui_cells_access">
			<a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getMoneyAccountRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">余额变更记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getSyAccountRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">收益变更记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getJfAccountRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">积分变更记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getDlbAccountRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">聚财宝变更记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getJdbChangeRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">聚德币变更记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/getYljAccountRecord.action?userId=${user.id}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">养老金转存记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a>
		</div>
		<div class="jui_cells_title">资金管理</div>
		<div class="jui_cells jui_cells_access">
			<a class="jui_cell"
				href="${pageContext.request.contextPath }/user/recharge.action?openId=${openId}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">余额充值</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/withdraws.action?openId=${openId}">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">提现申请</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <!-- <a class="jui_cell"
				href="javascript:void(0);">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">我的银行卡</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> -->
		</div>
		<div class="jui_cells_title">交易中心</div>
		<div class="jui_cells jui_cells_access">
			<a class="jui_cell"
				href="${pageContext.request.contextPath }/user/offlineConsumeRecord.action">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">线下消费记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a>
			<a class="jui_cell"
				href="${pageContext.request.contextPath }/user/onlineConsumeRecord.action">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">线上消费记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a>
		</div>
		<div class="jui_cells_title">商家管理</div>
		<div class="jui_cells jui_cells_access">
			<a class="jui_cell"
				href="${pageContext.request.contextPath }/user/sellerReportOrder.action">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">线下报单</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="${pageContext.request.contextPath }/user/submitOrderRecord.action">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">报单记录</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a>
		</div>
		<!-- <div class="jui_cells_title">安全管理</div>
		<div class="jui_cells jui_cells_access">
			<a class="jui_cell"
				href="javascript:void(0);">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">修改密码</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a> <a class="jui_cell"
				href="javascript:void(0);">
				<div class="jui_cell_bd jui_cell_primary">
					<lable class="jui_lable">实名认证</lable>
				</div>
				<div class="jui_cell_ft"></div>
			</a>
		</div> -->
		<footer class="footer">
			<p class="copy">
				©<span>2016</span>聚德购物商城
			</p>
			<p class="line">晋ICP备16009896号</p>
			<p class="line">微信公众账号：聚德购物商城</p>
		</footer>

	</div>
</body>
</html>