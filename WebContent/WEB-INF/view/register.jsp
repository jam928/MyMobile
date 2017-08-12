<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Register</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/register.css" type = "text/css">
		<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
	</head>

	<body>
	
	<nav id = "head">
		<ul>
			<li><a href = "login">Back to the Login Page</a></li>
		</ul>
	</nav>
	
	<div class = "w3-container w3-blue">
		<h2>Register for a MyMobile Account!</h2>
	</div>
		
		<form:form action = "addCustomer" modelAttribute = "customer" method = "POST" class = "w3-container">
		
		<p>
		<label>Name: </label>
		<form:input path = "name" type = "text" class = "w3-input"/>
		<form:errors path = "name" cssClass = "error" /></p>
		
		<p>
		<label>Email: </label>
		<form:input path = "email" type = "text" class = "w3-input"/>
		<form:errors path = "email" cssClass = "error" /></p>
		
		<p>
		<label>Password: </label>
		<form:input path = "password" type = "password" class = "w3-input"/>
		<form:errors path = "password" cssClass = "error" /></p>
		
		<p>
		<label>Birthday: </label>
		<form:input path = "birthday" type = "text" class = "w3-input" />
		<form:errors path = "birthday" cssClass = "error" /></p>
		
		<input type = "submit" value = "Save" class = "save" />
		
		</form:form>
	
	
	</body>

</html>