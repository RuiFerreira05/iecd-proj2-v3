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

    if (!client.isConnected()) {
        response.sendRedirect("index.jsp");
    }
    
    if (client.surrender()) {
    	response.getOutputStream().print("valid");
    } else {
        response.getOutputStream().print("error");
    }
%>