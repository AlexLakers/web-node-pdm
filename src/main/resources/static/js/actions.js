function doLogout() {
    let isLogout = confirm("Are you sure?")
    if (isLogout) {
        window.location.href = "/logout"
    }
}

function doRegistration() {
    window.location.href = "/registration"
}


function doLogin(){
    window.location.href = "/login"
}
function updateUser() {
    var userSelect = document.getElementById("user-list");
    let selectedUserId = userSelect.value
    if (selectedUserId) {
        window.location.href = '/users/'+selectedUserId;
    } else {
        alert("Please,choose a specific user for updating.");
    }
}
function updateSpecification(){
    var specSelect=document.getElementById("spec-list")
    let selectedSpecId = specSelect.value
    if(selectedSpecId){
        window.location.href='/specifications/'+selectedSpecId;
    } else{
        alert("Please,choose a specific specification for updating")
    }
}
function updateDetail(){
    var detailSelect=document.getElementById("detail-list")
    let selectedDetailId=detailSelect.value
    if(selectedDetailId){
        window.location.href='/details/'+selectedDetailId
    }
    else{
        alert("Please,choose a specific detail for updating")
    }
}
function showDetailsBySpecId(){
    var specSelect=document.getElementById("spec-list")
    let selectedSpecId=specSelect.value
    if(selectedSpecId){
        window.location.href='/specifications/'+selectedSpecId + '/details'
    }
    else{
        alert("Please,choose a specific specification to show details")
    }
}
