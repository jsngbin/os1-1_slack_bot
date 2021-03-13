<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<!-- Bootstrap CSS -->
		<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BmbxuPwQa2lc/FVzBcNJ7UAyJxM6wuqIj61tLrc4wSX0szH/Ev+nYRRuWlolflfl" crossorigin="anonymous">

		<script src="resources/js/lib/jquery-3.5.1.js"></script>
		<script src="resources/js/login.js"></script>
		<script src="resources/js/rest.js?ver=20200313"></script>

		<title>${serviceName}</title>

	</head>
	<body>
		<div class="container">
			<div class="row justify-content-center">
				<div class="col text-center" >
					<img style="width: auto; height: 200px;" src="resources/img/cat.jpg" class="img-thumbnail"/>
					<h1>Hello ${serviceName} World!</h1>
				</div>
			</div>
		</div>

		<div class="container">
			<div class="row justify-content-center">
				<div class="col text-center" ><h1>오늘의 메뉴</h1></div>
			</div>
			<div class="row">
				<div class=col>
					<table class="table" name="lunch">
						<thead>
							<tr>
								<th colspan="2" scope="col">점심</th>
							</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
				<div class=col>
					<table class="table" name="dinner">
						<thead>
							<tr>
								<th colspan="2" scope="col">저녁</th>

							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div class="container">
            <form id="fileUploadForm">
                <div class="form-group">
                    <label>Tmax 메뉴 엑셀 파일을 업로드 하세요.</label>
                    <input name="file" type="file" accept=".xlsx" class="form-control"/>
                    <button type="submit" class="btn btn-primary" onclick="uploadMenuFile(); return false;">업로드</button>
                </div>
            </form>
		</div>

	</body>
</html>
<script>
	document.addEventListener("DOMContentLoaded", function(){
		loadMenu();

});
</script>