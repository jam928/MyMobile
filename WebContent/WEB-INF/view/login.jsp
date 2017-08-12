<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html>
	<head>
		<title>Login</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/login.css" type = "text/css">
		<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
		
	</head>

	<body>
	
	<nav id = "head">
		<ul>
			<li><a href = "register">Don't have a MyMobile Account? Click to register</a></li>
			<li><a href = "forgotPW">Forgot Password?</a></li>
			<li><a href = "viewPhones">View Phones</a>
			
		</ul>
	
	</nav>
	
	<div class = "w3-container w3-blue">
		<h2>Login to Your MyMobile Account!</h2>
	</div>
	<form:form action = "loggedIn" method = "POST" modelAttribute = "theCustomer" class = "w3-container" >
		<p>
			<label>Email:</label>
			<form:input path = "email" type = "email" class = "w3-input" />
			<form:errors path = "email" cssClass = "error" /></p>
		<p>
			<label>Password:</label>
			<form:input path = "password" type = "password" class = "w3-input" />
			<form:errors path = "password" cssClass = "error" />
		</p>
			<input type = "submit" value = "Login">
		
	<div style = "color: red">${error}</div>
	
	</form:form>
	</body>

</html>