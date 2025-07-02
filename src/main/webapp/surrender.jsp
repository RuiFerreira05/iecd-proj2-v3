<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
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
    
	if (request.getParameter("inform_server") != null) {
		if (client.surrender()) {
			client.setPlaying(false);
		    client.setOpponentUsername(null);
		    client.setPlayerNum(null);
		    client.setYt(false);
		    client.setBoard(null);        
		    request.getSession().setAttribute("win", false);
			response.getOutputStream().print("valid");
		} else {
		    response.getOutputStream().print("error");
		}
	} else {
		client.setPlaying(false);
	    client.setOpponentUsername(null);
	    client.setPlayerNum(null);
	    client.setYt(false);
	    client.setBoard(null);
	    request.getSession().setAttribute("win", false);
	    response.getOutputStream().print("valid");
	}
	   
%>