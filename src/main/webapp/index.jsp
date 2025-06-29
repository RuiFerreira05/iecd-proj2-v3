<%@ page 	language="java" 
			contentType="text/html; charset=UTF-8" 
			pageEncoding="UTF-8" 
%>
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>

<%  if (session.getAttribute("client") == null) {
        client = new ClientBean(session.getId());
        session.setAttribute("client", client);
    } else {
        client = (ClientBean) session.getAttribute("client");
        if (!client.getUuid().equals(session.getId())) {
        	client = new ClientBean(session.getId());
        	session.setAttribute("client", client);
        }
    }

	if (!client.isConnected()) {
		client.connect();
	}
%>

<!DOCTYPE html>
<html>
<head>
    <title>GoBang Initial</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <link rel="stylesheet" href="static/css/initial.css" />
</head>
<body>
    <div class="initial-container">
        <h1 class="initial-title">Welcome to GoBang!</h1>
        <h2 class="initial-subtitle"><%= client.isConnected() ? "Choose an option below" : "It seems the server is currently offline, sorry!" %></h2>
        <div class="btn-group">
			<%  if (client.isConnected()) { 
				    if (client.isLoggedIn()) {
			%>
				        <a href="matchmaking.jsp"><button>Play</button></a>
				        <a href="logout.jsp"><button>Logout</button></a>
				        <a href="profile.jsp"><button>Change profile</button></a>
				        <a href="wall_of_fame.jsp"><button>Wall of fame</button></a>
				        <a href="search_player.jsp"><button>Search player</button></a>
			<%		} else {  	 %>
				        <a href="login.jsp"><button>Login</button></a>
				        <a href="wall_of_fame.jsp"><button>Wall of fame</button></a>
				        <a href="search_player.jsp"><button>Search player</button></a>
			<%		}
			   	} else { 
			%>
					<a href="index.jsp"><button>Reconnect</button></a>
			<%  } %>
        </div>
        <div>
        	<div><%= client.isConnected() ? "Connected to server" : "Not connected to server" %></div>
        	<div><%= client.getUuid() %></div>
        </div>
    </div>
    <div class="footer">
        <p>&copy; 2025 GoBang - MMR. All rights reserved.</p>
    </div>
</body>
</html>