<%@ page	language="java" 
			contentType="text/html; charset=UTF-8"
    		pageEncoding="UTF-8"%>
<%@page import="client.ClientBean"%>
<%@page import="org.w3c.dom.Document"%>

	<%! private ClientBean client = null; %>
	<%! public String username= ""; %>
	<%! public String pass= ""; %>
    <%! public String nationality= ""; %>
    <%! public String age= ""; %>
    <%! public String favcolor= ""; %>
    <%! public String photo= ""; %>
    <%! public String victories= ""; %>
    <%! public String defeats= ""; %>
    <%! public String games_played= ""; %>
    <%! public String natsxml= ""; %>
    
    <%  if (session.getAttribute("client") == null) {
    		response.sendRedirect("index.jsp");
	    } else {
	    	client = (ClientBean) session.getAttribute("client");
	    	if (!client.getUuid().equals(session.getId())) {
	    		response.sendRedirect("index.jsp");
	        }
	    }
    
	    if (!client.isConnected() || !client.isLoggedIn()) {
	    	response.sendRedirect("index.jsp");
	    }
	    
	    String[] data = client.getdata(client.getUsername());
	    //System.out.println(data);
	    username = data[0];
	    nationality = data[1];
	    age = data[2];
	    favcolor = data[3];
	    photo = data[4];
	    victories = data[5];
	    defeats = data[6];
	    games_played = data[7];
	    pass= client.getPass();
	%>

<!DOCTYPE html>
<html>
    
<head>
    <title>GoBang Profile</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" />

    <link rel="stylesheet" href="static/css/profile.css" />

    <script type="module" src="static/js/profile.js"></script>
</head>
<body>
    
    <form name="profileForm" method="post" action="profile.jsp">
        <section class="photo-section">
            <div class="photo-container">
                <div class="frame">
                    <!-- <canvas class="photo-frame" width="80" height="100"></canvas> -->
                    <img class="photo-frame" id="photo" src="data:image/png;base64,<%= photo %>" alt="profile picture"/>
                </div>
            </div>
            <button class="edit-photo-button" id="edit-photo-button">
                <i class="bi-pencil-square"></i>
            </button>
            <input accept="image/*" type="file" id="file-chooser" class="file-chooser" name="photo"/>
        </section>
        
        <section class="profile-info-section">
            <div class="profile-container">
                <div>
                    <label for="name" class="label"> <%= client.getXmlNat().length() %> </label>
                    <input class="profile-text-input" id="name" type="text" value="<%= username %>" name="username" required/>
                </div>
                <div>
                    <label for="password" class="label"> Password </label>
                    <input class="profile-text-input" id="password" type="password"  name="password" value=<%= pass %> required/>
                </div>
                <div>
                    <label for="victories" class="label"> Victories </label>
                    <input class="profile-text-input" id="victories" type="text" value="<%= victories %> " disabled required/>
                </div>
                <div>
                    <label for="age" class="label"> Age </label>
                    <input class="profile-text-input" id="age" type="text" value="<%= age %>" name="age" required/>
                </div>
                <div>
                    <label for="nationality" class="label"> Nationality </label>
                    <input class="profile-text-input" id="nationality" type="text" value="<%= nationality %>"  name="nationality" required/>
                    <%-- <label for="nationality" class="label"> Nationality </label>
                    <select class="nationality-selector" id="nationality" required>
                        <option value=""> Choose nationality </option>
                    </select> --%>
                </div>
                <div>
                    <label for="defeats" class="label"> Defeats </label>
                    <input class="profile-text-input" id="defeats" type="text" value="<%= defeats %>" disabled required/>
                </div>
                <div class="favColor-container">
                    <label for="favColor" class="label"> Favourite color</label>
                    <input type="color" id="favColor" class="profile-color-input" value="<%= favcolor %>"  name="favcolor"/>
                </div>
                <div class="game-times-container">
                    <label for="game-times-select" class="label"> Games played </label>
                    <input class="profile-text-input" id="game-times-select" type="text" value="<%= games_played %>" disabled required/>
                    <%-- <select class="game-times-select" id="game-times-select">
                        <option value=""> See times </option>
                    </select> --%>
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
    if (request.getParameter("username") != null) {
    	boolean nice = false;
    	boolean changed= false;
        String username_change = request.getParameter("username");
        String pass_change = request.getParameter("password");
        String photo_change = request.getParameter("photo");
        String age_change = request.getParameter("age");
        String nationality_change = request.getParameter("nationality");
        String color_change = request.getParameter("favcolor");
    	        
        boolean[] change= new boolean[6];
        if(!username.equals(username_change)){
        	change[0]= client.changeData("username", username_change);
        	changed= true;
        }
        if(!pass.equals(pass_change)){
        	change[1]= client.changeData("password", pass_change);
        	changed= true;
        }
        
        if(!age.equals(age_change)){
        	change[3]= client.changeData("age", age_change);
        	changed= true;
        }
        if(!nationality.equals(nationality_change)){
        	change[4]= client.changeData("nacionality", nationality_change);
        	changed= true;
        }
        if(!favcolor.equals(color_change)){
        	change[5]= client.changeData("color", color_change);
        	changed= true;
        }
        for(int i=0; i < change.length ; i++ ){
        	if(change[i]){
        		nice= true;		
        	}
        }	
        if(changed && !nice){
        	out.println("<div class='error-message'>Invalid user data.</div>");
        }
    }
    

    %> 
</body>

</html>