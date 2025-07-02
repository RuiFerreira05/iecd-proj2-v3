class Game {
	
	constructor(board, yt) {
        this.board = board;
		this.yt = yt;
		this.player = yt ? 1 : 2;
        this.canvas = document.getElementById("board-canvas");
		this.exitButton = document.getElementById("exit-button");
		this.turnDisplay = document.getElementById("turn-display");
        this.ctx = this.canvas.getContext('2d');
        this.init();
    }
	
	init() {
		this.drawBoardLines();
		this.updateBoard();
		
		if (!this.yt) {
			this.turnDisplay.innerHTML = "Opponent's turn";
			this.checkOppoMove();
		}
		
		this.canvas.addEventListener("click", (ev) => {
			this.handleClick(ev);
        });
		this.exitButton.addEventListener("click", () => {
            this.surrender();
        });
	}
	
	paintCell(x, y, color) {
		const ctx = this.canvas.getContext('2d');
		let posx = x * 27 + 13;
		let posy = y * 27 + 13;
		ctx.beginPath();
		ctx.arc(posx, posy, 7, 0, 2 * Math.PI);
		ctx.fillStyle = color;
		ctx.fill();
		ctx.closePath();
		console.log(`Painted cell at (${x}, ${y}) with color ${color}`);
	}

	drawBoardLines() {
	    const ctx = this.canvas.getContext('2d');
	    ctx.fillStyle= "#8b402f";
	    
	    let line_interval= this.canvas.width / 15;
	    let posx= 3;
	    let posy= 0;
	    for(let line=0; line < 15; line++){
	        posy += line_interval;
	        ctx.fillRect(posx, posy, this.canvas.width - 6, 4);
	    }
	    posx= 0;
	    posy= 5;
	    line_interval= this.canvas.height / 15;
	    for(let col=0; col < 15; col++){
	        posx += line_interval;
	        ctx.fillRect(posx, posy, 4, this.canvas.height - 6);
	    }
	}
	
	surrender() {
		fetch("surrender.jsp")
		    .then(response => response.text())
	        .then(data => {
				console.log("Surrender response: ", data);
				if (data.includes("valid")) {
					window.location.href = "index.jsp";
				} else if (data.includes("error")) {
					alert("Error surrendering the match.");
	            }
	        })
	        .catch(error => console.error("Error surrendering:", error));
	}
	
	updateBoard() {
	    fetch("getBoard.jsp")
            .then(response => response.text())
            .then(data => {
				console.log("Board data received: ", data);
                if (data) {
                    this.drawBoardfromData(data);
                } else {
                    alert("Error retrieving board data.");
                }
            })
            .catch(error => console.error("Error fetching board data:", error));
    }
	
	handleClick(ev) {
		if (!this.yt) {
			return;
		}
		let [x, y] = this.getCellsFromMouse(ev);
		console.log("Clicked cell: ", x, y);
		fetch(`move.jsp?x=${y}&y=${x}`) // yes. x and y are swapped in the request, for some reason this is the only way to make it work
		    .then(response => response.text())
            .then(data => {
                console.log("Move response: ", data);
                if (data.includes("vm")) {
					this.yt = false;
					this.turnDisplay.innerHTML = "Opponent's turn";
					this.paintCell(x, y, this.player === 1 ? "red" : "blue");
                    this.updateBoard();
					console.log("board updated: ", this.board);
					this.checkOppoMove();
				} else if (data.includes("tie") || data.includes("win")) {
					window.location.href = "match_results.jsp";
                } else if (data.includes("im")) {
                    alert("Invalid move");
                }
            })
            .catch(error => console.error("Error making move:", error));
	}
	
	async checkOppoMove() {
		fetch("checkOppoMove.jsp")
			.then(response => response.text())
			.then(data => {
				console.log("Opponent move check response: ", data);
				if (data.includes("yt")) {
                    this.yt = true;
					this.turnDisplay.innerHTML = "Your turn";
                    this.updateBoard();
				} else if (data.includes("tie") || data.includes("win")) {
					window.location.href = "match_results.jsp";
                } else {
					setTimeout(() => {
					    this.checkOppoMove();
                    }, 2000);
				} 
			});
	}
	
	checkMoveClick(ev) {
	    const ctx = this.canvas.getContext('2d');

	    let mousex= 0;
	    let mousey= 0;
	    [mousex, mousey]= this.getMouseCoord(this.canvas);

	    let mx_cnv= ev.x - mousex;
	    let my_cnv= ev.y - mousey;

	    let posx= 0;
	    let posy= 0;
	    let width= 27;
	    let height= 27; 
	    let total_spaces= 15 * 15;
	    for (let i= 0; i < total_spaces; i++){
	        if(posx + width >= this.canvas.width + 10){
	            posx= 0;
	            posy += height;
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
	
	drawBoardfromData(data) {
	    const ctx = this.canvas.getContext('2d');
	    ctx.fillStyle= "#8b402f";
	    
	    let posx= 0;
	    let posy= 0;
	    let width= 27;
	    let height= 27; 
	    let total_spaces= 15 * 15;
	    for (let i= 0; i < total_spaces; i++){
	        if(posx + width >= this.canvas.width + 10){
	            posx= 0;
	            posy += height ;
	        }
	        if(data[i] === "1") {
	            ctx.beginPath();
	            ctx.arc(posx + width/2, posy + height/2, 7, 0, 2 * Math.PI);
	            ctx.fillStyle = "red";
	            ctx.fill();
	            ctx.closePath();
	        } else if(data[i] === "2") {
	            ctx.beginPath();
	            ctx.arc(posx + width/2, posy + height/2, 7, 0, 2 * Math.PI);
	            ctx.fillStyle = "blue";
	            ctx.fill();
	            ctx.closePath();
	        }
	        posx += width ;
	    }
	}
	
	getCellsFromMouse(ev) {

	    let mousex= 0;
	    let mousey= 0;
	    [mousex, mousey]= this.getMouseCoord(this.canvas);

	    let mx_cnv= ev.x - mousex;
	    let my_cnv= ev.y - mousey;
	    let width= 27;
	    let height= 27; 
		
		return [Math.floor(mx_cnv / width), Math.floor(my_cnv / height)];
		
	}
	
	getMouseCoord(el) {
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
}