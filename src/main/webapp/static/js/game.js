function main() {
    
    const canvas= document.getElementById("board-canvas");
    const ctx = canvas.getContext('2d');
    let click= false;

    drawBoardLines(canvas);
    
    canvas.addEventListener("click", function(ev){
            console.log("heyyyy");
            checkMoveClick(canvas, ev);
            click= true;
            console.log("yooo");
            
        });
    if(click){
        const [mousex, mousey] = getMouseCoord(canvas);
            let mx_cnv= ev.x - mousex;
            let my_cnv= ev.y - mousey;
            fetch("webapp/gameHandler.jsp?movex=" + mx_cnv + "&movey=" + my_cnv)
                .then(response => response.text())
                .then(status => {
                    if(status.includes("invalid")){
                        console.error("invalid move");
                        window.location.reload();
                    } else {
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error("Error fetching game data:", error);
                });
        click= false;
    }
    
}

function drawBoardLines (canvas){
    const ctx = canvas.getContext('2d');
    ctx.fillStyle= "#8b402f";
    
    let line_interval= canvas.width / 15;
    let posx= 3;
    let posy= 0;
    for(let line=0; line < 15; line++){
        posy += line_interval;
        ctx.fillRect(posx, posy, canvas.width - 6, 4);
    }
    posx= 0;
    posy= 5;
    line_interval= canvas.height / 15;
    for(let col=0; col < 15; col++){
        posx += line_interval;
        ctx.fillRect(posx, posy, 4, canvas.height - 6);
    }
}

function drawBoardfromData(canvas, data){
    const ctx = canvas.getContext('2d');
    ctx.fillStyle= "#8b402f";
    
    let posx= 0;
    let posy= 0;
    let width= 27;
    let height= 27; 
    let total_spaces= 15 * 15;
    for (let i= 0; i < total_spaces; i++){
        if(posx + width >= canvas.width + 10){
            posx= 0;
            posy += height ;
        }
        if(data[i] === "B"){
            ctx.beginPath();
            ctx.arc(posx + width/2, posy + height/2, 7, 0, 2 * Math.PI);
            ctx.fillStyle = "black";
            ctx.fill();
            ctx.closePath();
        } else if(data[i] === "W"){
            ctx.beginPath();
            ctx.arc(posx + width/2, posy + height/2, 7, 0, 2 * Math.PI);
            ctx.fillStyle = "white";
            ctx.fill();
            ctx.closePath();
        }
        posx += width ;
    }
}

function checkMoveClick(canvas, ev){
    const ctx = canvas.getContext('2d');

    let mousex= 0;
    let mousey= 0;
    [mousex, mousey]= getMouseCoord(canvas);

    let mx_cnv= ev.x - mousex;
    let my_cnv= ev.y - mousey;

    let posx= 0;
    let posy= 0;
    let width= 27;
    let height= 27; 
    let total_spaces= 15 * 15;
    for (let i= 0; i < total_spaces; i++){
        if(posx + width >= canvas.width + 10){
            posx= 0;
            posy += height ;
        }

        let check= mx_cnv >= posx && mx_cnv <= posx + width && my_cnv >= posy && my_cnv <= posy + height;
        if(check){
            ctx.beginPath();
            ctx.arc(posx + width/2, posy + height/2, 7, 0, 2 * Math.PI);
            ctx.fillStyle = "red";
            ctx.fill();
            ctx.closePath();
        }
        posx += width ;
    }

}

function getMouseCoord(el) {
    let xPos = 0;  // Initialize the x-coordinate
    let yPos = 0;  // Initialize the y-coordinate

    // Traverse the DOM hierarchy to calculate the coordinates
    while (el) {
         if (el.tagName === 'BODY') {
            // Deal with browser quirks with body/window/document and page scroll
            let xScroll = el.scrollLeft || document.documentElement.scrollLeft;
            let yScroll = el.scrollTop || document.documentElement.scrollTop;

            xPos += (el.offsetLeft - xScroll + el.clientLeft);
            yPos += (el.offsetTop - yScroll + el.clientTop);
        } else {
            // For all other non-BODY elements
            xPos += (el.offsetLeft - el.scrollLeft + el.clientLeft);
            yPos += (el.offsetTop - el.scrollTop + el.clientTop);
        }

        el = el.offsetParent;
    }
    return [xPos, yPos];
}

main();