<%@ page	language="java" 
			contentType="text/html; charset=UTF-8"
    		pageEncoding="UTF-8"%>
<%@page import="client.ClientBean"%>

<%! private ClientBean client = null; %>

<%  if (session.getAttribute("client") != null) {
		client = (ClientBean) session.getAttribute("client");
    	client.logout();
    }

    response.sendRedirect("index.jsp");
%>