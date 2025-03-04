function doLogout() {
    let isLogout = confirm("Are you sure?")
    if (isLogout) {
        window.location.href = "/logout"
    }
}

function doRegistration() {
    window.location.href = "/admin//users/registration"
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
