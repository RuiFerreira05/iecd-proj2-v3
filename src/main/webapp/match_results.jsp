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
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
	<title>Match Results</title>
    <link rel="stylesheet" href="static/css/match_results.css" />
</head>
<body>
	<h1>Match Results</h1>
	<div class="game-end-information">
		<% if (tie) { %>
    		<p>🤝 The match ended in a tie. 🤝</p>
    	<% } else if (win) { %>
    		<p>🏆 Congratulations! You won the match! 🏆</p>
    	<% } else { %>
    		<p>😢 Sorry, you lost the match. 😢</p>
    	<% } %>
    </div>
    <div class="button-container">
        <a href="index.jsp">
        	<input class="button-input" value="Return to Home" type="button">
        </a>
        <a href="matchmaking.jsp">
        	<input class="button-input" value="Play Again" type="button">
        </a>
    </div>
</body>
</html>