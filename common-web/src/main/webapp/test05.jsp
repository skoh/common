<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
	request.setAttribute("skoh", 1);

	System.out.println(request.getAttribute("skoh"));
%>

<%-- ${skoh} --%>
<%-- <c:out value="${skoh}" /> --%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>jquery</title>

<script src="http://code.jquery.com/jquery-2.1.1.js" integrity="sha256-FA/0OOqu3gRvHOuidXnRbcmAWVcJORhz+pv3TX2+U6w="
	crossorigin="anonymous"></script>
<script>
	$(document).ready(function() {
		// 		$($("#a")[0]).text("First span");
		// 		$($("#a")[1]).text("Second span");
		// 		$($("span[name='a']")[0]).text("First span");
		// 		$($("span[name='a']")[1]).text("Second span");
		// 		$($(".demo")[0]).text("First span");
		// 		$($(".demo")[1]).text("Second span");

		// 		var a = $($("select[name='select01']")[1]);
		// 		console.log(a.prop("title"));
		// 		a.empty();

		// 		var a = $("select[name='select01']");
		// 		$.each (a, function (index, b) {
		// 			$(b).empty();
		// 		});
		// 		$(a[1]).empty();

		// 		if (",".indexOf(",") > -1) {
		// 			console.log("y");
		// 		} else {
		// 			console.log("n");
		// 		}
		// 		$("#select01 option:eq(1)").prop('selected', true);
		// 		$("#select01").empty();

		// 		var a = [];
		// 		a[0] = "0";
		// 		a[1] = "1";
		// 		a[2] = "2";
		// 		console.log(a.length);
		// 		console.log(a);

		// 		a.splice(1, 1);
		// 		console.log(a.length);
		// 		console.log(a);

		// 		for (i = a.length - 1; i >= 0; i--) {
		// 			if (a[i] == null) {
		// 				a.splice(i, 1);
		// 			}
		// 		}
		// 		console.log(a.length);
		// 		console.log(a);

		// 		$("#select02").prop("disabled", true);

		// 		$("#input01").keydown(function() {
		// 			console.log("keydown");
		// 		});

		// 		$("#input01").keyup(function() {
		// // 			console.log("keyup");
		// 	        $(this).data("old", $(this).data("new") || "");
		// 	        $(this).data("new", $(this).val());
		// 	        console.log($(this).data("old"));
		// 	        console.log($(this).data("new"));
		// 		});

		// 		$("#input01").keypress(function() {
		// 			console.log("keypress");

		// 		$("#input01").change(function() {
		// 			console.log("change");
		// 	        $(this).data("old", $(this).data("new") || "");
		// 	        $(this).data("new", $(this).val());
		// 	        console.log($(this).data("old"));
		// 	        console.log($(this).data("new"));
		// 		});

		// 		$("#input01").on("change", function() {
		// 			console.log("on");
		// 		});
// 		var json = {
// 			"a" : "1"
// 		};
// 		var json2 = {
// 			"b" : 2
// 		};
// 		$.extend(json, json2);
// 		console.log(JSON.stringify(json));
// 		json.c = 3;
// 		console.log(JSON.stringify(json));

		var dt = new Date();
		dt.setDate(dt.getDate() - 30);
		
		var year = dt.getFullYear();
		var month = dt.getMonth() + 1; // 1월(0)
		var date = dt.getDate();
		var day = dt.getDay() + 1; // 일요일(0)
		console.log(year + '.' + month + '.' + date + ' ' + day);

		dt.setDate(dt.getDate() - 30);
		console.log(dt);
	});
</script>
</head>

<body>
	<!-- 	<div> -->
	<!-- 		<span id="a" name="a" class="demo">1</span> <span id="a" name="a" class="demo">2</span> <span>3</span> -->
	<!-- 	</div> -->

	<!-- 	<select id="select01" name="select01" title="select01"> -->
	<!-- 		<option value=""></option> -->
	<!-- 		<option value="1">a</option> -->
	<!-- 		<option value="2">b</option> -->
	<!-- 	</select> -->
	<!-- 	<select id="select02" name="select01" title="select02"> -->
	<!-- 		<option value=""></option> -->
	<!-- 		<option value="3">c</option> -->
	<!-- 		<option value="4">d</option> -->
	<!-- 	</select> -->
	<!-- 	<input id="input01" name="select01" title="select02" value="1" /> -->

	/sample/select.do
	<form method="post" action="http://localhost:8050/sample/select.do">
		fields : <input name="fields" value="*"><br>
		table : <input name="table" value="sample"><br>
		condition : <input name="condition" value="id IN (1)"><br>
		<input type="submit">
	</form><br>

	/sample/insert.do
	<form method="post" action="http://localhost:8050/sample/insert.do" enctype="multipart/form-data">
		name : <input name="name" value="s"><br>
		test_id : <input name="test_id" value="3"><br>
		reg_id : <input name="reg_id" value="1"><br>
		mod_id : <input name="mod_id" value="1"><br>
		file1 : <input type="file" name="file"><br>
		file2 : <input type="file" name="file"><br>
		file3 : <input type="file" name="skoh"><br>
		<input type="submit">
	</form><br>
</body>
</html>