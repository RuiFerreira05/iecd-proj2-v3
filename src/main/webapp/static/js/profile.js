
const photo_canvas= document.getElementById("photo-frame");

document.getElementById("edit-photo-button").addEventListener("click", function(){
    document.getElementById("file-chooser").click();
});

document.getElementById("file-chooser").addEventListener("change", function(event){
	console.log("hereee");
    const sel_file= this.files[0];
    if(sel_file){
        const file_reader= new FileReader();
        file_reader.onload= function(){
			console.log(file_reader.result);
            document.getElementById("photo").src= file_reader.result;
			console.log(document.getElementById("photo").src);
        }
        file_reader.readAsDataURL(sel_file);   
    }
});