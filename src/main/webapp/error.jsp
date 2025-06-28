<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
    
<%-- Página de erro, preciso de alguém para tratar do front-end aqui --%>
    
<html>
<head>
<meta charset="UTF-8">
<title>Error</title>
</head>
<body>
	<%= exception.getMessage() %>
</body>
</html>