/*
ABOUT
managing ui interactions an visualization
 */


//Wait for load because css loading problem
//window.addEventListener('load', function () {
 //   document.body.classList.add('loaded');

//saved instance of a session after login used throughout the application
let session = null;


    /* input */
 const buttonLogin = document.getElementById("btn_login");
 const buttonLogout = document.getElementById("btn_logout");
 const buttonTogglePage = document.getElementById("btn_userPage");
 const buttonCloseLoginForm = document.getElementById("btn_closeLoginForm")
    /* output */
  const loginModal = document.getElementById("loginModal") ;
  const modalBackdrop = document.getElementById("modalBackdrop");
  const body = document.body;

  /* Initally deactivate the modalBackdrop on Startup */
  modalBackdrop.style.display = "none";


//}); //end load on startup

//***************************** Login UI ****************************************************
//function used in com to change the ui after a user has been logged in
function uiOnLogin(username){
    const buttonLogin = document.getElementById("btn_login")
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");

    buttonLogin.onclick = buttonLoginWhenAlreadyLoggedIn;
    userLoginText.textContent = username;
    userLoginText.style.color = "lightgreen";
    userIcon.style.background = "lightgreen";
}


//************************************* button functions **************************************************
function buttonLoginWhenNotLoggedIn(){
    uiTurnLoginFormOn()
    //uiOnLogin function only after successful login in com.js
}
function buttonLoginWhenAlreadyLoggedIn(){
    /* Visibility Login Form */
    alert("You are allready Logged in!")
}

function buttonLogoutWhenLoggedIn(){
    uiOnLogout();
    Session.cookieKillall();
    //reset the function again to the default
    buttonLogout.onclick = buttonLogoutWhenNotLoggedIn;
    //send logout request to server
    logoutRequestHandler().then(r =>
         alert("You have been logged out!"));
}
function buttonLogoutWhenNotLoggedIn(){
    alert("You are not logged in!")
}
//set default button functions for login and logout on startup
buttonLogout.onclick = buttonLogoutWhenLoggedIn;
buttonLogin.onclick = buttonLoginWhenNotLoggedIn;

// Special, for deactivating login form when click outside of form
modalBackdrop.onclick = function () {
    uiTurnLoginFormOff()
};
buttonCloseLoginForm.onclick = function (){
    uiTurnLoginFormOff()
}
buttonTogglePage.onclick = function(){

}
//************************************* MISC UI Functions **************************************************
function uiTurnLoginFormOff(){
    loginModal.style.display = "none";
    modalBackdrop.style.display = "none";
    body.classList.remove('loginForm-active');
}
function uiTurnLoginFormOn(){
    loginModal.style.display = "block";
    modalBackdrop.style.display = "block";
    body.classList.add('loginForm-active');
}
//***************************** Logout UI ****************************************************
function uiOnLogout(){
    const buttonLogin = document.getElementById("btn_login")
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");
    buttonLogin.onclick = buttonLoginWhenNotLoggedIn;
    userLoginText.textContent = "none";
    userLoginText.style.color = "darkgrey";
    userIcon.style.background = "white";
}

