<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>
<%! private boolean tie = false; %>
<%! private boolean win = false; %>

<%
    if (session.getAttribute("client") == null) {
        response.sendRedirect("index.jsp");
    } else {
        client = (ClientBean) session.getAttribute("client");
        if (!client.getUuid().equals(session.getId())) {
            response.sendRedirect("index.jsp");
        }
    }

    if (session.getAttribute("tie") != null) {
    	tie = (boolean) session.getAttribute("tie");
    	session.removeAttribute("tie");
    }
    if (session.getAttribute("win") != null) {
        win = (boolean) session.getAttribute("win");
        session.removeAttribute("win");
    }
%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Match Results</title>
</head>
<body>
	<h1>Match Results</h1>
	<% if (tie) { %>
    	<p>The match ended in a tie.</p>
    <% } else if (win) { %>
    	<p>Congratulations! You won the match!</p>
    <% } else { %>
    	<p>Sorry, you lost the match.</p>
    <% } %>
    <div>
        <a href="index.jsp">Return to Home</a>
        <a href="matchmaking.jsp">Play Again</a>
    </div>
</body>
</html>