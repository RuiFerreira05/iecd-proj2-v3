<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Base64" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="client.ClientBean" %>
<%@ page import="jakarta.servlet.http.Part" %>
<%@ page import="java.io.IOException" %>

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
    <meta charset="UTF-8">
    <title>GoBang Registo</title>
    <link rel="stylesheet" href="static/css/register.css" />
</head>
<body>
    <form name="profileForm" method="POST" enctype="multipart/form-data">
        <section class="photo-section">
            <div class="photo-container">
                <div class="frame">
                    <img class="photo-frame" id="photoPreview" src="data:image/png;base64,default" alt="profile picture">
                </div>
            </div>
            <button type="button" class="edit-photo-button" id="edit-photo-button">
                <i class="bi-pencil-square"></i>
            </button>
            <input accept="image/*" type="file" id="file-chooser" class="file-chooser" name="photo">
        </section>
        
        <section class="profile-info-section">
            <div class="profile-container">
                <div>
                    <label for="name" class="label"> Name </label>
                    <input class="profile-text-input" id="name" type="text" name="user" required>
                </div>
                <div>
                    <label for="password" class="label"> Password </label>
                    <input class="profile-text-input" id="password" type="password" name="pass" required>
                </div>
                <div>
                    <label for="age" class="label"> Age </label>
                    <input class="profile-text-input" id="age" type="text" name="age" required>
                </div>
                <div>
                    <label for="nationality" class="label"> Nationality </label>
                    <input class="profile-text-input" id="nationality" type="text" name="nationality" required>
                </div>
                <div class="favColor-container">
                    <label for="favColor" class="label"> Favourite color</label>
                    <input type="color" id="favColor" class="profile-color-input" name="color" value="#00000">
                </div>
            </div>
        </section>
        <div class="button-container">
            <a href="index.jsp">
                <input type="submit" class="button-input" value="Submit">
            </a>
            <a href="index.jsp">
                <input type="button" class="button-input" value="Exit">
            </a>
        </div>
    </form>
    
	<%
	    if ("POST".equalsIgnoreCase(request.getMethod())) {
	        String user = request.getParameter("user");
	        String pass = request.getParameter("pass");
	        String nationality = request.getParameter("nationality");
	        String age = request.getParameter("age");
	        String favcolor = request.getParameter("color");
	
	        String profilePicture = "default";
	        Part part = request.getPart("photo");
	
	        if (part != null && part.getSize() > 0) {
	            try (InputStream in = part.getInputStream();
	                 ByteArrayOutputStream outBytes = new ByteArrayOutputStream()) {
	                byte[] buffer = new byte[4096];
	                int len;
	                while ((len = in.read(buffer)) != -1) {
	                    outBytes.write(buffer, 0, len);
	                }
	                profilePicture = Base64.getEncoder().encodeToString(outBytes.toByteArray());
	            } catch (IOException e) {
	                System.out.println("Error reading uploaded photo: " + e.getMessage());
	            }
	        }
	
	        boolean ok = client.register(user, pass, nationality, age, profilePicture, favcolor);
	        if (ok) {
	            response.sendRedirect("index.jsp");
	            return;
	        } else {
	            out.println("<p class='error-message'>Error while registering, please try again.</p>");
	        }
	    }
	%>
</body>

<script>
    document.getElementById('file-chooser').addEventListener('change', function(evt) {
        const [file] = this.files;
        if (file) {
            document.getElementById('photoPreview').src = URL.createObjectURL(file);
        }
    });
    
    document.getElementById('edit-photo-button').addEventListener('click', function() {
        document.getElementById('file-chooser').click();
    });
</script>
</html>