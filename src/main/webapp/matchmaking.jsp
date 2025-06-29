<%@ page 	language="java" 
			contentType="text/html; charset=UTF-8"
    		pageEncoding="UTF-8" %>
    		
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>

<%  if (session.getAttribute("client") == null) {
   		response.sendRedirect("index.jsp");
    } else {
    	client = (ClientBean) session.getAttribute("client");
    	if (!client.getUuid().equals(session.getId())) {
    		response.sendRedirect("index.jsp");
        }
    }
   
    if (!client.isConnected() || !client.isLoggedIn()) {
    	response.sendRedirect("index.jsp");
    }
    
   	if (!client.matchmake()) {
    	response.sendRedirect("index.jsp?matchmake_error=true");
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>GoBang Matchmaking</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    
    <link rel="stylesheet" href="static/css/matchmaking.css" />
</head>
<script type="text/javascript">
	function pollMatchmaking() {
        fetch('matchmake_poll.jsp')
            .then(response => response.text())
            .then(data => {
                console.log('Matchmaking status:', data);
                if (data === 'matched') {
                    window.location.href = 'game.jsp';
                } else if (data === 'error') {
                    window.location.href = 'index.jsp?matchmake_error=true';
                } else {
                    setTimeout(pollMatchmaking, 2000); // Poll every 2 seconds
                }
            })
            .catch(error => console.error('Error fetching matchmaking status:', error));
    }
	
    document.addEventListener('DOMContentLoaded', function() {
        pollMatchmaking();
    });
</script>
<body>
	Matchmaking...
</body>