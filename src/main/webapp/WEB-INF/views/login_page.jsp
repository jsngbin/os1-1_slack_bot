<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<title>${serviceName}</title>
		<script src="resources/js/lib/jquery-3.5.1.js"></script>
		<script src="resources/js/login.js"></script>
	</head>
	<body>
		<% String logined = (String)session.getAttribute("login");
			if(logined != null) response.sendRedirect("/admin");
		 %>

		<div class="loginWelcome">
			<h1>로그인이 필요합니다.</h1>
		</div>
		<div class="loginDiv">
            <form name="loginForm">
                <input type="text" name="user"/>
                <input type="password" name="pass"/>
                <button type="submit" name="submitBtn" onclick="goLogin(); return false;">로그인</button>
            </form>
        </div>
	</body>
</html>