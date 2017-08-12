<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Add a Line</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/main.css" type = "text/css">
		<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
	</head>

	<body>
	
	<nav id = "head">
		<ul>
			<li><a href = "myAccount">Back to the MyAccount Page</a>
		</ul>
	
	</nav>
	
	<div class = "w3-container w3-green">
		<h2>Add a Line to your mobile account</h2>
		
	</div>
	
	<h4 style = "text-align:left" >Select Phone: </h4>
		
		<form:form action = "saveLine" modelAttribute = "phoneLine" method = "POST" class = "w3-container">
		
		<table align = "left">
			<tr>
				<th>Select</th>
				<th> Image </th>
				<th> Condition </th>
				<th> Name </th>
				<th> Rating </th>
				<th> Price </th>
				<th> Color </th>
			</tr>
			
			<c:forEach var = "tempPhone" items = "${phones}">
			<tr>
				<td><form:radiobutton path="pid" value = "${tempPhone.pid}"/></td>
				<td> <img src = "${pageContext.request.contextPath}/${tempPhone.imgSrc}" alt = "${tempPhone.alt}" width = "400" height = "400" /> </td>
				<td>${tempPhone.condition}</td>
	    		<td>${tempPhone.name} </td>
	    		<td>${tempPhone.rating} </td>
	    		<td>$${tempPhone.price} </td>
	    		<td>${tempPhone.color} </td>
			</tr>
			
			</c:forEach>
		</table>
		<br>
		<br>
		<label>Enter Phone Number: </label>
		<form:input type = "tel" path = "phoneNumber" class = "w3-input" />
		
		<br>
		<br>
		
		<input type = "submit" value = "Add Line!" class = "save" class ="w3-input" />
		
		</form:form>
	
	
	</body>

</html>