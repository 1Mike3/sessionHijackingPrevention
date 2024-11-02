/*
ABOUT
managing ui interactions an visualization
 */



 const buttonLogin = document.getElementById("btn_login");
 const buttonLogout = document.getElementById("btn_logout");
 const buttonTogglePage = document.getElementById("btn_togglePage");
 const buttonCloseLoginForm = document.getElementById("btn_closeLoginForm")

  const loginModal = document.getElementById("loginModal") ;
  const modalBackdrop = document.getElementById("modalBackdrop");
  const body = document.body;

  /* Initally deactivate the modalBackdrop on Startup */
  modalBackdrop.style.display = "none";

  /*
  Setup event listener for the loaded condition of the window when a specific flag is set.
  reason: The "uiOnLoguout" function after logging out on the user page should only be
  performed after the switch to the main page.
   */
window.addEventListener("load", () => {
    //check if on the main page
    const isMainPage = window.location.pathname === "/" || window.location.pathname === "/index.html";
    //if on main page and logged out flag present (set in buttonLogoutWhenLoggedInUserPage) finish logout
    if (isMainPage && localStorage.getItem("loggedOut") === "true") {
        // Only run this on the main page
        uiOnLogout();
        alert("You have been logged out!");
        // Clear the flag after running the function
        localStorage.removeItem("loggedOut");
    }
});

//***************************** Login UI ****************************************************
//function used in com to change the ui after a user has been logged in
function uiOnLoginMainPage(username){
    const buttonLogin = document.getElementById("btn_login")
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");

    buttonLogin.onclick = buttonLoginWhenAlreadyLoggedIn;
    userLoginText.textContent = username;
    userLoginText.style.color = "lightgreen";
    userIcon.style.background = "lightgreen";
}
//Modified version for user page with removed login button because it does not exist there
function uiOnLoginUserPage(username){
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");
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

function buttonLogoutWhenLoggedInMainPage(){
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
buttonLogout.onclick = buttonLogoutWhenLoggedInMainPage;
buttonLogin.onclick = buttonLoginWhenNotLoggedIn;

// Special, for deactivating login form when click outside of form
modalBackdrop.onclick = function () {
    uiTurnLoginFormOff()
};
buttonCloseLoginForm.onclick = function (){
    uiTurnLoginFormOff()
}
buttonTogglePage.onclick = async function(){
    await loadUserPageRequestHandler()
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



