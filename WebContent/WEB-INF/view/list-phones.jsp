<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<!DOCTYPE html>

<html>
	<head>
		<title>MyMobile</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/main.css" type = "text/css">
	</head>

<body>
	
	<nav id  = "head">
		<ul>
			<li><a href = "login">View Account</a></li>
			<li><a href = "admin">Admin</a>
		</ul>
	
	</nav>
	
	<h2>MyMobile Phones</h2>

	<div id = "container">		
	
		<table>
		
			<!-- add out html table here -->
			<tr>
				<th> Image </th>
				<th> Condition </th>
				<th> Name </th>
				<th> Rating </th>
				<th> Price </th>
				<th> Color </th>
				<th> Quantity</th>
			</tr>
			
	    	<!-- loop over and print the list of phones -->
	    	<c:forEach var = "tempPhone" items = "${phones}" varStatus = "iter" >
	    		<tr>
	
	    			<td> <img src = "${pageContext.request.contextPath}/${tempPhone.imgSrc}" alt = "${tempPhone.alt}" width = "400" height = "400" /> </td>
	    			<td>${tempPhone.condition}</td>
	    			<td>${tempPhone.name} </td>
	    			<td>${tempPhone.rating} </td>
	    			<td>$${tempPhone.price} </td>
	    			<td>${tempPhone.color} </td>
	    			<td>${tempPhone.quantity}</td>
    			</tr>
	    	</c:forEach>
		
		</table>
	</div>
</body>

</html>