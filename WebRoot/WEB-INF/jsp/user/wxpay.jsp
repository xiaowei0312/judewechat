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
</head>
<body>
	<form action="" method="post">
		<input type="button" value="确认支付" name="ajaxLoadId" id="test" />
	</form>
	<script type="text/javascript">
		var basePath = "${pageContext.request.contextPath}";
		$("#test")
				.one(
						"click",
						function() {
							$
									.ajax(
											{
												url : basePath
														+ "wxpay/sellerSubmitOrderPay.action" //<span style="font-family:微软雅黑;">ajax调用微信统一接口获取prepayId</span>  
											})
									.done(
											function(data) {
												var obj = eval('(' + data + ')');
												if (parseInt(obj.agent) < 5) {
													alert("您的微信版本低于5.0无法使用微信支付");
													return;
												}
												WeixinJSBridge
														.invoke(
																'getBrandWCPayRequest',
																{
																	"appId" : obj.appId, //公众号名称，由商户传入  
																	"timeStamp" : obj.timeStamp, //时间戳，自 1970 年以来的秒数  
																	"nonceStr" : obj.nonceStr, //随机串  
																	"package" : obj.packageValue, //<span style="font-family:微软雅黑;">商品包信息</span>  
																	"signType" : obj.signType, //微信签名方式:  
																	"paySign" : obj.paySign
																//微信签名  
																},
																function(res) {
																	alert(res.err_msg);
																	if (res.err_msg == "get_brand_wcpay_request:ok") {
																		window.location.href = obj.sendUrl;
																	} else {
																		alert("fail");
																		window.location.href = "http://183.45.18.197:8016/wxweb/config/oauth!execute.action";
																		//<span style="font-family:微软雅黑;">当失败后，继续跳转该支付页面让用户可以继续付款，贴别注意不能直接调转jsp，</span><span style="font-size:10.5pt">不然会报</span><span style="font-size:12.0pt"> system:access_denied。</span>  
																	}
																});

											});
						});
	</script>
</body>

</html>