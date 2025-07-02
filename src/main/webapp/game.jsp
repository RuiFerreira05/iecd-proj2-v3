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
            background: <%= client.getFavColor() == null ? "#FFFFFF" : client.getFavColor() %>
        }
    </style>
</head>
<body>
    <section class="title-section">
        <div id="turn-display" class="title-container">
            Your Turn!
        </div>
        <div id="timer-display"></div>
    </section>
    <section class="board-section">
        <div class="board-container">
            <canvas id="board-canvas" width="400" height="400"></canvas>
        </div>
    </section>
    <section class="options-section">
        <div class="button-container">
            <button type="button" id="exit-button" class="button-exit">Surrender</button>
        </div>
    </section>
</body>
<script src="static/js/game.js"></script>
<script>
	new Game(<%=board%>, <%=yt%>)
</script>
</html>
