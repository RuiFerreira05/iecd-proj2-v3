<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Base64" %>
<%@ page import="client.ClientBean" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="jakarta.servlet.http.Part" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.Iterator" %>

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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" />
    <link rel="stylesheet" href="static/css/register.css" />
</head>
<body>
    <form name="registerForm" action="register.jsp" method="post">
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
                    <input type="color" id="favColor" class="profile-color-input" name="color" value="#000000" required>
                </div>
            </div>
        </section>
        <div class="button-container">
            <input type="submit" class="button-input" value="Submit">
            <a href="index.jsp">
                <input type="button" class="button-input" value="Exit">
            </a>
        </div>
    </form>
    
	<%
        if (request.getParameter("user") != null) {
        	
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            String nationality = request.getParameter("nationality");
            String age = request.getParameter("age");
            String favcolor = request.getParameter("color");
            
            String profilePicture = "default";
            //Part photoPart = request.getPart("photo");
            //profilePicture  = request.getParameter("file");
            profilePicture  = request.getParameter("file");
            
            System.out.println("user: " + user);
    		System.out.println("pass: " + pass);
    		System.out.println("nat: " + nationality);
    		System.out.println("age: " + age);
    		System.out.println("color: " + favcolor);
    		System.out.println("photo: " + profilePicture);
            
            /*if (photoPart != null && photoPart.getSize() > 0) {
                try (InputStream is = photoPart.getInputStream()) {
                    byte[] photoData = is.readAllBytes();
                    profilePicture = Base64.getEncoder().encodeToString(photoData);
                } catch (IOException e) {
                    out.println("<p class='error-message'>Error reading photo: " + e.getMessage() + "</p>");
                }
            }*/
            boolean ok = client.register(user, pass, nationality, age, favcolor, profilePicture);
            if (ok) {
            	System.out.println("ok");
            	session.setAttribute("user", user);
                response.sendRedirect("index.jsp");
                return;
            } else {
                out.println("<p class='error-message'>Error while registing, please try again.</p>");
            }
        }
    %>
</body>

<script>
    document.getElementById('file-chooser').addEventListener('change', function(evt) {
        const [file] = this.files;
        if (file) {
            document.getElementById('photoPreview').src = URL.createObjectURL(file);
            let help= URL.createObjectURL(file).toString();
            //httpPostAsync(URL.createObjectURL(file).toString());
            console.log("this: " + URL.createObjectURL(file));
            console.log(file);
        }
    });
    
    document.getElementById('edit-photo-button').addEventListener('click', function() {
        document.getElementById('file-chooser').click();
    });
    
    /*function httpPostAsync(file) {
    	var xmlHttp;
    	if (window.XMLHttpRequest)
    		xmlHttp = new XMLHttpRequest();
    	else
    		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    	console.log("register.jsp?file=" + file );
    	xmlHttp.open("GET", "register.jsp?file=" + file , true); 
    	console.log("post");
    	xmlHttp.send();
    	console.log("sent");
    }*/
</script>
</html>