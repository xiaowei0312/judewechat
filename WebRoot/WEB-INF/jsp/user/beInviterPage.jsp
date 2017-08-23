<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<meta charset="utf-8" />
<title></title>
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<script type="text/javascript">
	document.querySelector('html').style.fontSize = document.documentElement.clientWidth
			/ 10 + 'px';
</script>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

.box {
	position: relative;
}

.bg {
	display: block;
	width: 100%;
	height: auto;
}

.btn-box {
	display: block;
	position: absolute;
	top: 20.117rem;
	left: 2.5rem;
	width: 5rem;
}

.btn {
	width: 100%;
	height: auto;
}

.btn-box:active {
	opacity: .7;
}
</style>
</head>
<body>
	<div class="box">
		<img class="bg" src="${pageContext.request.contextPath}/img/bg12345.jpg" /> <a class="btn-box" href="http://www.0352jdw.com/wechat/user/quickRegist.action?inviterUserId=${inviterUserId}&from=app"> <img
			class="btn" src="${pageContext.request.contextPath}/img/btn.gif" />
		</a>
	</div>
</body>
</html>