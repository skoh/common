<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>jsp</title>
<script src="/jquery.js"></script>
</head>
<body>
    테스트
</body>
</html>

<script>
$(() => {
    $.ajax({
        url: "http://localhost:8080/v1/test/templete",
        success: function(data) {
            console.log(data);
        }
    });

    $.ajax({
        url: "http://localhost:8090/v1/test/templete",
        success: function(data) {
            console.log(data);
        }
    });
});
</script>
