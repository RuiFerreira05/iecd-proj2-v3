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

	response.getOutputStream().println(client.getBoard());
%>