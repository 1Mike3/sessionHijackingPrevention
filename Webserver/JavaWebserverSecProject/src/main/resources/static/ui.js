//Wait for load because css loading problem
window.addEventListener('load', function () {
    document.body.classList.add('loaded'); 

    /* input */
 const buttonLogin = this.document.getElementById("btn_login");
 const buttonLogout = this.document.getElementById("btn_logout");
 const buttonTogglePage = this.document.getElementById("btn_userPage");
 const buttonCloseLoginForm = this.document.getElementById("btn_closeLoginForm")
    /* output */
  loginModal = this.document.getElementById("loginModal") ;
  const modalBackdrop = document.getElementById("modalBackdrop");
  const body = document.body;

  /* Initally deactivate the modalBackdrop on Startup */
  modalBackdrop.style.display = "none";

   /* button funcs */
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
/* Login Invisible when click outside of form */






});