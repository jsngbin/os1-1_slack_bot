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
			if(logined == null) response.sendRedirect("/login_page");
		 %>
		 <form name="logoutForm">
		 <input type="hidden" name="user" value="<%= logined %>"></input>
		 <button type="submit" onclick="goLogout(); return false;">로그아웃</button>
		</form>
		<h1>Hello ${serviceName} Admin Page!</h1>
	</body>
</html>