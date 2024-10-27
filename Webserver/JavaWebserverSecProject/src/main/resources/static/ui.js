/*
ABOUT
managing ui interactions an visualization
 */


//Wait for load because css loading problem
window.addEventListener('load', function () {
    document.body.classList.add('loaded'); 

    /* input */
 const buttonLogin = this.document.getElementById("btn_login");
 const buttonLogout = this.document.getElementById("btn_logout");
 const buttonTogglePage = this.document.getElementById("btn_userPage");
 const buttonCloseLoginForm = this.document.getElementById("btn_closeLoginForm")
    /* output */
  const loginModal = this.document.getElementById("loginModal") ;
  const modalBackdrop = document.getElementById("modalBackdrop");
  const body = document.body;

  /* Initally deactivate the modalBackdrop on Startup */
  modalBackdrop.style.display = "none";


  //************************************* button functions **************************************************
buttonLogin.onclick = function(){
    /* Visibility Login Form */
    loginModal.style.display = "block"; 
    modalBackdrop.style.display = "block"; 
    body.classList.add('loginForm-active'); 
    
};
// Special, for deactivating login form when click outside of form
modalBackdrop.onclick = function () {
     /* Visibility Login Form */
    loginModal.style.display = "none"; 
    modalBackdrop.style.display = "none";
    body.classList.remove('loginForm-active');

};
buttonCloseLoginForm.onclick = function(){
     /* Visibility Login Form */
    loginModal.style.display = "none"; 
    modalBackdrop.style.display = "none"; 
    body.classList.remove('loginForm-active');
}
buttonTogglePage.onclick = function(){

}
buttonLogout.onclick = function(){

}

}); //end load on startup

//***************************** Login UI ****************************************************
function uiOnLogin(username){
    const buttonLogin = document.getElementById("btn_login")
    const userLoginText = document.getElementById("txt_loggedInUser");
    const userIcon = document.getElementById("icn_user");

    buttonLogin.disabled = true;
    userLoginText.textContent = username;
    userLoginText.style.color = "lightgreen";
    userIcon.style.background = "lightgreen";
}





