<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="client.ClientBean" %>

<%! private ClientBean client = null; %>

<%	int x = request.getParameter("x") != null ? Integer.parseInt(request.getParameter("x")) : -1;
    int y = request.getParameter("y") != null ? Integer.parseInt(request.getParameter("y")) : -1;
    
    client = (ClientBean) session.getAttribute("client");
    
    if (client == null) {
    	response.sendRedirect("index.jsp");
    } else if (!client.getUuid().equals(session.getId())) {
    	response.sendRedirect("index.jsp");
    }
    
    if (x != -1 && y != -1) {
    	String result = client.move(x, y);
        if (result != null) {
        	if (result.equals("valid")) {
        		response.getOutputStream().println("vm");
        	} else {
        		String [] parts = result.split(" ");
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
        	}
        } else {
        	response.getOutputStream().println("im");
        }
    }
	
%>