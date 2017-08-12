<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Account Info</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/profile.css" type = "text/css">
		
	</head>

	<body>
	
	<nav id = "head">
		<ul>
			<li><a href = "logout">Logout</a></li>
			<li><a href = "addALine">Add a Line ${lineError}</a></li>
			<li><a href = "selectPhonePlan">Select a Phone Plan</a></li>
			<li><a href = "pay">Pay Balance's DUE</a></li>
		</ul>
	</nav>
	
	<div style = "clear:both">
		<h2 style = "float:left">Balance DUE: $${currentCustomer.balance}</h2>
		<h3 style = "float:right">DUE DATE:  SEP 15 2017</h3>
	
	</div>
	<br>
	<br>
	<br>
	<h2>Phone Plan:</h2>
	<ul>
	
		<li>Current Monthly Rate: $${phonePlan.monthlyRate}</li>
		<li>Maximum Phone Lines: ${phonePlan.numberOfLines }</li>
		<li>Description: ${phonePlan.description}</li>
	
	</ul>
	<h2>Phone Lines:</h2> 
	<table>
	
	<tr>
		<th>Image</th>
		<th>Phone Name</th>
		<th>Color</th>
		<th>Phone Number</th>
		<th>Delete PhoneLine</th>
	
	
	</tr>
	<c:forEach var = "phoneLine" items = "${phoneLines}">
		<tr>
			<c:url var = "deleteLink" value = "/account/deletePhoneLine">
				<c:param name="phoneLineId" value = "${phoneLine.plid}" />
			</c:url>
	    	<td> <img src = "${pageContext.request.contextPath}/${phoneLine.imgSrc}" alt = "${phoneLine.alt}" width = "400" height = "400" /> </td>
			<td>${phoneLine.phoneName}</td>
			<td>${phoneLine.color}</td>
			<td>${phoneLine.phoneNumber}</td>
			<td><a href="${deleteLink}" onclick="if (!(confirm('Are you sure you want to delete this phoneLine?'))) return false" style = "float:right">Delete</a></td>
		</tr>
	</c:forEach>
	
	</table>

	
	</body>

</html>