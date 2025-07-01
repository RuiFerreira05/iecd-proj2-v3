<%@ page 	language="java" 
			contentType="text/html; charset=UTF-8" 
			pageEncoding="UTF-8" %>
<%@ page import="client.ClientBean" %>

<%! private String[] walloffame= new String[15]; %>
<%! private String image_url= ""; %>
<%! private String name= "Undefined"; %>
<%! private int length_wall= 0; %>
<%! private String image= " "; %>
<%! private String flag= " "; %>
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
	    
	String[] walloffame = client.getWof();
	
	length_wall= 0;
	for(int i=0; i < walloffame.length; i++){
        if(walloffame[i] != null){
            length_wall += 1;
        }
    }
	    
	%>


<!DOCTYPE html>
<html>
    
<head>
    <title>Wall of Fame</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <link rel="stylesheet" href="static/css/wall_of_fame.css" />
</head>
<body>
    <section class="title-section">
        <div class="title-container"> Wall of Fame </div>
    </section>
    <section class="wall-section">
        <div class="wall-container">
            <div class="wall">
                <div class="player-example">
                    <div class="classify-container">
                        <div class="place"> 1 </div>
                        <div class="photo-place">
                            <% 
                                if(length_wall >= 3){
                                    image= walloffame[0];
                                }
                            %>
                            <img src="data:image/png;base64,<%= image %>" alt="user photo" class="photo">
                        </div>    
                    </div>
                    <%
                        if(length_wall >= 3){
                            name= walloffame[1];
                        }
                    %>
                    <div class="username"> <%= name %></div>
                    <div class="flag-container">
                        <%
                        if(length_wall >= 3){
                            flag= walloffame[2];
                        }
                        %>
                        <img src="data:image/png;base64,<%= flag %>" alt="user nationality flag" class="flag">
                    </div>
                </div>
                <div class="player-example">
                    <div class="classify-container">
                        <div class="place"> 2 </div>
                        <div class="photo-place">
                            <% 
                                if(length_wall >= 6){
                                    image= walloffame[3];
                                }
                                else{
                                    image= " ";
                                }
                            %>
                            <img src="data:image/png;base64,<%= image %>" alt="user photo" class="photo">
                        </div>    
                    </div>
                    <%
                        if(length_wall >= 6){
                            name= walloffame[4];
                        }
                        else{
                            name= "Undefined";
                        }
                    %>
                    <div class="username"> <%= name %> </div>
                    <div class="flag-container">
                        <% 
                            if(length_wall >= 6){
                                flag= walloffame[5];
                            }
                            else{
                                flag= " ";
                            }
                        %>
                        <img src="data:image/png;base64,<%= flag %>" alt="user nationality flag" class="flag">
                    </div>
                </div>
                <div class="player-example">
                    <div class="classify-container">
                        <div class="place"> 3 </div>
                        <div class="photo-place">
                            <% 
                                if(length_wall >= 9){
                                    image= walloffame[6];
                                }
                            %>
                            <img src="data:image/png;base64,<%= image %>" alt="user photo" class="photo">
                        </div>    
                    </div>
                    <%
                        if(length_wall >= 9){
                            name= walloffame[7];
                        }
                        else{
                            name= "Undefined";
                        }
                    %>
                    <div class="username"> <%= name %> </div>
                    <div class="flag-container">
                        <% 
                            if(length_wall >= 9){
                                flag= walloffame[8];
                            }
                            else{
                                flag= " ";
                            }
                        %>
                        <img src="data:image/png;base64,<%= flag %>" alt="user nationality flag" class="flag">
                    </div>
                </div>
                <div class="player-example">
                    <div class="classify-container">
                        <div class="place"> 4 </div>
                        <div class="photo-place">
                            <% 
                                if(length_wall >= 12){
                                    image= walloffame[9];
                                }
                            %>
                            <img src="data:image/png;base64,<%= image %>" alt="user photo" class="photo">
                        </div>    
                    </div>
                    <%
                        if(length_wall >= 12){
                            name= walloffame[10];
                        }
                        else{
                            name= "Undefined";
                        }
                    %>
                    <div class="username"> <%= name %></div>
                    <div class="flag-container">
                        <% 
                            if(length_wall >= 12){
                                flag= walloffame[11];
                            }
                            else{
                                flag= " ";
                            }
                        %>
                        <img src="data:image/png;base64,<%= flag %>" alt="user nationality flag" class="flag">
                    </div>
                </div>
                <div class="player-example">
                    <div class="classify-container">
                        <div class="place"> 5 </div>
                        <div class="photo-place">
                            <% 
                                if(length_wall == 15){
                                    image= walloffame[12];
                                }
                            %>
                            <img src="data:image/png;base64,<%= image %>" alt="user photo" class="photo">
                        </div>    
                    </div>
                    <%
                        if(length_wall == 15){
                            name= walloffame[13];
                        }
                        else{
                            name= "Undefined";
                        }
                    %>
                    <div class="username"> <%= name %> </div>
                    <div class="flag-container">
                        <% 
                            if(length_wall == 15){
                                flag= walloffame[14];
                            }
                            else{
                                flag= " ";
                            }
                        %>
                        <img src="data:image/png;base64,<%= flag %>" alt="user nationality flag" class="flag">
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="exit-section">
        <div class="button-container">
            <a href="index.jsp">
                <input type="submit" class="button-input" value="Exit">
            </a>
        </div>
    </section>
</body>
</html>