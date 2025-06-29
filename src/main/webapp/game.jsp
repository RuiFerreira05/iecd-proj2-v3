<%@ page 	language="java" 
			contentType="text/html; charset=UTF-8" 
			pageEncoding="UTF-8" 
%>
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>
<%! private String playerNum = null; %>
<%! private String opponentUsername = null; %>
<%! private boolean yt = false; %>
<%! private String board = null; %>

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
	
	if (!client.isPlaying()) {
		response.sendRedirect("index.jsp");
	}
	
	playerNum = client.getPlayerNum();
	opponentUsername = client.getOpponentUsername();
	yt = client.isYt();
	board = client.getBoard();
%>

<!DOCTYPE html>
<html>
<head>
    <title>GoBang Game</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />

    <link rel="stylesheet" href="static/css/game.css" />
    <style>
        body {
            <%--background: <%= client.getdata(client.getUsername())[3] %>;--%>
            background: #ffffff;
        }
    </style>
    <script type="module" src="static/js/game.js"></script>
</head>
<body>
    <section class="title-section">
        <div class="title-container">
            Your Turn!
        </div>
    </section>
    <section class="board-section">
        <div class="board-container">
            <canvas id="board-canvas" width="400" height="400"></canvas>
        </div>
    </section>
    <section class="options-section">
        <div class="button-container">
            <a href="index.jsp"><button type="button" class="button-exit">Exit</button></a>
        </div>
        <div>
            <%= playerNum %>
            <%= opponentUsername %>
            <%= yt %>
            <%= board %>
        </div>
    </section>
</body>
</html>
