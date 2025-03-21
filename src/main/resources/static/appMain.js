/*
ABOUT
managing ui interactions an visualization on the main Page
 */


//connection parameters used to easily reconfigure the application
const CON_PARAM = Object.freeze({
    //get information on if http or https communication should be used
    PROTOCOL_TYPE: location.protocol,  // ["http:" | "https:"]
    //CONFIGURABLE
    DNS_NAME: "securitytest-sh.at",
    //DNS_NAME: "localhost",
    PORT: ":"+ (location.protocol === "http:" ? "80" : "443"),
    //PORT: ":"+ (location.protocol === "http:" ? "3000" : "443"),
});

//saved instance of a session after login used throughout the application
var session = null;

const buttonLogin = document.getElementById("btn_login");
const buttonLogout = document.getElementById("btn_logout");
const buttonTogglePage = document.getElementById("btn_togglePage");
const buttonCloseLoginForm = document.getElementById("btn_closeLoginForm")

const loginModal = document.getElementById("loginModal") ;
const modalBackdrop = document.getElementById("modalBackdrop");
const body = document.body;


  /*
  Setup event listener for the loaded condition of the window when a specific flag is set.
  reason: The "uiOnLoguout" function after logging out on the user page should only be
  performed after the switch to the main page.
   */
window.addEventListener("load", () => {
    //check if on the main page
    const isMainPage = window.location.pathname === "/" || window.location.pathname === "/index.html";
    //if on main page prpare for cookie banner depending if allready accepted or not
    if (isMainPage) {
        const cookies = document.cookie.split("; ");
        let flag = false;
        for (let cookie of cookies) {
            const [name, value] = cookie.split("=");
            if (name === "cookiesAccepted") {
                if(value === "true"){
                    //if cookies are accepted activate all buttons
                    activateAllButtons();
                    flag = true;
                    modalBackdrop.style.display = "none";
                }else {
                    //if cookies are not accepted deactivate all buttons
                    deactivateAllButtons();
                    //prep Site for cookie banner
                    modalBackdrop.style.display = "block";
                    document.getElementById('cookie-banner').style.display = 'block';
                }
            }
        }
        if (!flag) {
            //if no flag is set deactivate all buttons
            deactivateAllButtons();
            //prep Site for cookie banner
            modalBackdrop.style.display = "block";
            document.getElementById('cookie-banner').style.display = 'block';
        }
    }
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

    userLoginText.textContent = username;
    userLoginText.style.color = "lightgreen";
    userIcon.style.background = "lightgreen";
}

//***************************** Logout UI ****************************************************
// Function for changing the ui after login on the main page to visualize the logged in user
function uiOnLogout(){
    const buttonLogin = document.getElementById("btn_login")
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");
    userLoginText.textContent = "none";
    userLoginText.style.color = "darkgrey";
    userIcon.style.background = "white";
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
function buttonLoginFunction(){
    if(session == null) {
        uiTurnLoginFormOn();
        //uiOnLogin function only after successful login in com.js
    }else{
        alert("You are allready Logged in!")
    }
}

function buttonLogoutFunction(){
    uiOnLogout();
    Session.cookieKillall();
    if(session != null){
        //send logout request to server
        logoutRequestHandler().then(r =>
            alert("You have been logged out!"));
    }else{
        alert("You are not logged in!")
    }
}

//set default button functions for login and logout on startup
buttonLogout.onclick = buttonLogoutFunction;
buttonLogin.onclick = buttonLoginFunction;

// Special, for deactivating login form when click outside of form
modalBackdrop.onclick = function () {
    uiTurnLoginFormOff()
};
buttonCloseLoginForm.onclick = function (){
    uiTurnLoginFormOff()
}
buttonTogglePage.onclick = async function(){
    if(session != null){
        await loadUserPageRequestHandler()
    }else{
        alert("You have to be logged in to access the user Section")
    }
}

//functions to activate or deactivate all buttons
function deactivateAllButtons(){
    buttonLogin.disabled = true;
    buttonLogout.disabled = true;
    buttonTogglePage.disabled = true;
}

function activateAllButtons(){
    buttonLogin.disabled = false;
    buttonLogout.disabled = false;
    buttonTogglePage.disabled = false;
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

//************************************* Testing **************************************************
//Used for testing purposes, logging in with different users session token
//Not used in regular code, only triggered manually during application test
function sessionInjector(username,token) {
    //inject the session class into the window object
    session = new Session(SESSION_CONSTRUCTOR_MODI.CREATE_FROM_LOGIN_REQUEST,username,token)
    session.storeSessionAsCookie(30);
    uiOnLoginMainPage(username);
    buttonLogout.onclick = buttonLogoutFunction;
}




