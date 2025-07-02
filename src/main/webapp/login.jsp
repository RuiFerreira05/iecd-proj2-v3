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
		response.sendRedirect("index.jsp");
	}
%>

<!DOCTYPE html>
<html>
    
<head>
    <title>Welcome to GoBang!</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />

    <link rel="stylesheet" href="static/css/login.css" />
</head>

<body>
    <section class="title-section">
        <div class="title-container"> GoBang </div>
    </section>
    <section class="session-section">
        <div class="session-container">
            <form class="login" method="post" action="login.jsp">
                <div class="session-title"> Login </div>
                <div class="login-element-container">
                    <label for="username" class="label"> Username </label>
                    <input class="session-text-input" id="username" type="text" name="user" required>
                </div>
                <div class="login-element-container">
                    <label for="password" class="label"> Password </label>
                    <input class="session-text-input" id="password" type="password" name="pass" required>
                </div>
                <div class="login-button-container">
                    <input type="submit" value="Login" class="login-button">
                </div>
            </form>
            <% if (request.getParameter("user") != null && request.getParameter("pass") != null) {
	               String username = request.getParameter("user");
	               String password = request.getParameter("pass");
	               if (client.login(username, password)) {
	            	   String favColor = client.getdata(username)[3];
	            	   client.setFavColor(favColor);
	                   response.sendRedirect("index.jsp");
	               } else {
	                   out.println("<div class='error-message'>Invalid username or password.</div>");
	               }
               }
            %>
            <div>
                <div class="session-title"> Create account </div>
                <div class="login-button-container">
                    <a href="register.jsp"><button class="login-button">Register</button></a>
                </div>
            </div>
        </div>
    </section>
    <section class="exit-section">
        <div class="exit-button-container">
            <a href="index.jsp">
                <button class="button-input" type="button"> Exit </button>
            </a>
        </div>
    </section>
</body>

</html>