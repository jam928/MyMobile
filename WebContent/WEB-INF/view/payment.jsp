<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Make Payment</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/main.css" type = "text/css">
		<link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
	</head>

	<body>
	<nav id = "head">
		<ul>
			<li><a href = "myAccount">Back to the MyAccount Page</a>
		</ul>
	
	</nav>
	
	<h2>Account's Balance: $${currentUser.balance}</h2>
	<h2>Saved Cards</h2>
		
		<form action = "savePaymentS" method = "POST">
		
		<table align = "left">
			<tr>
				<th>Select</th>
				<th> Credit Card Number </th>
				<th> Expiration Date </th>
				<th> CSC </th>
				<th> Vendor </th>
			</tr>
			
			<c:forEach var = "card" items = "${savedCards}">
			<tr>
				<td><input type = "radio" name = "crid" value = "${card.crid}"/></td>
				<td>${card.creditCardNumber}</td>
	    		<td>${card.expirationDate} </td>
	    		<td>${card.csc} </td>
	    		<td>${card.vendor} </td>
			</tr>
			
			</c:forEach>
		</table>
		<input type = "submit" value = "Pay" name = "pay" class = "save" />
		
		</form>
		
		<br>
		<br>
		<br>
		
		
		
	<div class = "w3-container w3-red">
		<h2>Pay with New Card</h2>
	</div>
	
		
		<form:form action = "savePayment" modelAttribute = "creditCard" method = "POST" class = "w3-container">
		
		<p>
			<label>Enter Credit Card Number: </label>
			<form:input path = "creditCardNumber" class = "w3-input"/>
			<form:errors path = "creditCardNumber" cssClass = "error" />
		</p>
		
		<p>
			<label>Enter Expiration Date: </label>
			<form:input path = "expirationDate" type = "date" class = "w3-input" />
			<form:errors path = "expirationDate" cssClass = "error" />
		</p>
		
		<p>
			<label>Enter 3 Digit Security Code: </label>
			<form:input path = "csc" class = "w3-input"/>
			<form:errors path = "csc" cssClass = "error" />
		</p>
		
		<p>
			<label>Enter Card Vendor: </label>
			<form:input path = "vendor" class = "w3-input" />
			<form:errors path = "vendor" cssClass = "error" />
		</p>
		
		<br>
		<br>
		
		<input type = "submit" value = "Pay Now!" name = "pay now!" class = "save" />
		
		</form:form>
	
	
	</body>

</html>