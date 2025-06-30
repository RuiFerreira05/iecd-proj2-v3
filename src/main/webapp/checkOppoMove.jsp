<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>

<%	if (session.getAttribute("client") == null) {
		response.sendRedirect("index.jsp");
    } else {
        client = (ClientBean) session.getAttribute("client");
        if (!client.getUuid().equals(session.getId())) {
        	response.sendRedirect("index.jsp");
        }
    }

	String serverResponse = client.checkOppoMove();

	if (serverResponse != null) {
		String[] parts = serverResponse.split(" ");
		switch (parts[0]) {
		case "yt":
            response.getOutputStream().println("yt");
            client.setYt(true);
            break;
        case "ge":
        	if (parts[1].equals("tie")) {
        		response.getOutputStream().println("tie");
        		request.getSession().setAttribute("tie", true);
        	} else {
        		String winner = parts[2];
        		response.getOutputStream().println("win");
        		if (winner.equals(client.getPlayerNum())) {
        			request.getSession().setAttribute("win", true);
        		} else {
        			request.getSession().setAttribute("win", false);
        		}
        	}
        	client.setPlaying(false);
        	client.setYt(false);
            break;
		}
    } else {
        response.getOutputStream().println("null");
    }
%>