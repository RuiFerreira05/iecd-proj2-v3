<%@ page 	language="java" 
			contentType="text/html; charset=UTF-8" 
			pageEncoding="UTF-8" %>
<%@ page import="client.ClientBean" %>

<%! private String image_url= ""; %>
<%! private String name= "Undefined"; %>
<%! private int length_wall= 0; %>
<%! private String image= " "; %>
<%! private String flag= " "; %>
<%! private ClientBean client = null; %>

<%  if (session.getAttribute("client") == null) {
    		response.sendRedirect("index.jsp");
	} else {
	    client = (ClientBean) session.getAttribute("client");
	    if (!client.getUuid().equals(session.getId())) {
	    	response.sendRedirect("index.jsp");
	    }
	}
    
	if (!client.isConnected()) {
	    response.sendRedirect("index.jsp");
	}
	    
%>


<!DOCTYPE html>
<html>
    
<head>
    <title>Search Player</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <link rel="stylesheet" href="static/css/search_player.css" />
</head>
<body>
    <section class="title-section">
        <div class="title-container"> Search player </div>
    </section>
    <section class="search-section">
    	<form class="search" method="post" action="search_player.jsp">
	    	<div class="instructions">Insert user name of player</div>
	        <div class="input-container">
	            <input class="text-input" id="username" type="text" name="user">
	            <input type="submit" class="button-input" value="Search">
	        </div>
    	</form>
    	<% if (request.getParameter("user") != null) {
    		String username = request.getParameter("user");
    		String[] data= client.getdata(username);
	        if (data != null) {
	        	session.setAttribute("player_search", data);
	        	response.sendRedirect("search_result.jsp");
	        } 
	        else {
	        	out.println("<div class='error-message'>Invalid username.</div>");
	        }
	       }
         %>
    </section>
    <section class="exit-section">
        <div class="button-container">
            <a href="index.jsp">
                <input type="submit" class="button-input" value="Exit">
            </a>
        </div>
    </section>
</body>
</html>