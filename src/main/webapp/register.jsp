<%--
  Created by IntelliJ IDEA.
  User: ADMIN_D
  Date: 5/14/2025
  Time: 1:06 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<form action="register" method="post">
  <input type="text" name="username" placeholder="Tên đăng nhập" required />
  <input type="password" name="password" placeholder="Mật khẩu" required />
  <input type="email" name="email" placeholder="Email" required />
  <button type="submit">Đăng ký</button>
</form>

</body>
</html>
