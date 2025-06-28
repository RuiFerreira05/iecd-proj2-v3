
const photo_canvas= document.getElementById("photo-frame");

document.getElementById("edit-photo-button").addEventListener("click", function(){
    document.getElementById("file-chooser").click();
});

document.getElementById("file-chooser").addEventListener("change", function(event){
    const sel_file= this.files[0];
    if(sel_file){
        const file_reader= new FileReader();
        file_reader.onload= function(){
            document.getElementById("photo").src= file_reader.result;
        }
        file_reader.readAsDataURL(sel_file);   
    }
});