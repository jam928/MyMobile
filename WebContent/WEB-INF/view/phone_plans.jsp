<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Phone Plans</title>
		<link rel = "stylesheet" href = "${pageContext.request.contextPath}/resources/css/profile.css" type = "text/css">
		
	</head>

	<body>
	
	<nav id = "head">
		<ul>
			<li><a href = "myAccount">Back to the MyAccount Page</a>
		</ul>
	
	</nav>
	
	<h2>Select a phone plan: </h2>
	<form:form method = "POST" modelAttribute = "plan" action = "savePhonePlan">
	<table>
		<tr>
			<th>Select</th>
			<th>Monthly Rate</th>
			<th>Number Of Lines</th>
			<th>Plan's Description</th>
		</tr>
		
		<c:forEach var = "temp" items = "${phonePlans}">
		<tr>
			
				<td><form:radiobutton path = "planId" value = "${temp.planId}"/>${temp.planId}</td>
				<td>$${temp.monthlyRate}</td>
				<td>${temp.numberOfLines}</td>
				<td>${temp.description}</td>
				
		</tr>
		</c:forEach>
		
		<tr>
			<td><input type = "submit" value = "Get Plan" /></td>
		<tr>
		
	</table>
	</form:form>
	
	
	
	
	</body>

</html>