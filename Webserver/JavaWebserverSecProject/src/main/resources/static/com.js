/*
ABOUT
- managing communication with the backend
 */


//saved instance of an session after login
let session = null;


//Prevent js before page loaded
window.addEventListener('load', function () {
    document.body.classList.add('loaded');



    const loginFormText = document.getElementById("loginFormOutput");
    const loginModal = document.getElementById("loginModal") ;
    const modalBackdrop = document.getElementById("modalBackdrop");
    const buttonLogin = this.document.getElementById("btn_login");
    const buttonLogout = this.document.getElementById("btn_logout");
    const body = document.body;


//setting up event listener for the login form submisstion
        document.getElementById("loginForm").addEventListener("submit", async function (event) {
        const username = document.getElementById("userName").value;
        const password = document.getElementById("password").value;
        event.preventDefault();
        await loginRequestHandler(username,password)
        }); // End event listender


//function to handle the process of logging in
        async function loginRequestHandler(username,password) {
            // send login data to server
            try {
                const response = await fetch("http://localhost:3000/login", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({username, password}),
                });

                // react "send login data to server"
                switch (response.status) {
                    case 200: //OK
                            //fetching json
                        console.log(response.body.toString());
                        const data = await response.json();
                        console.log("Login successful:", data);
                            // Deactivating login form display
                        uiTurnLoginFormOff();
                            //obtaining needed data from resp
                        const username = data.username;
                        const token = data.token;
                        console.log("recived:: usn: "+ username +",token: " + token);
                            //creating new session object from login data
                        session = new Session(SESSION_CONSTRUCTOR_MODI.CREATE_FROM_LOGIN_REQUEST,username,token);
                            //change ui after login
                        uiOnLogin(username);
                        buttonLogout.onclick = buttonLogoutWhenLoggedIn;
                        break;
                    case 204: //Missing Credentials
                        console.log("Login Missing credentials");
                        loginFormText.innerText = "Missing credentials!";
                        break;
                    case 401: //Unauthorized
                        console.log("Login unauthorized");
                        loginFormText.innerText = "Username and/or password incorrect!";
                        break;
                    case 500: //internal Server Error
                        console.log("Login Server Error")
                        loginFormText.innerText = "Something went wrong"; // :)
                        break;
                    default:
                        loginFormText.innerText = "Unknown Exception occurred";
                        console.log("Login unknown Response:", response.status);
                }
            } catch (error) {
                console.error("Error:", error);
                alert("Login Error: " + error);
            }
        }
//function to handle communication for logging out to the backend
    async function logoutRequestHandler(){
        const response = await fetch("http://localhost:3000/logout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ token:session.getToken() ,username:session.getUsername()})
        });
        switch (response.status) {
            case 200:
                console.log("Logout successful");
                session = null;
                uiOnLogout();
                break;
            case 500:
                console.log("Logout Server Error")
                break;
            default:
                console.log("Logout unknown Response:", response.status);
        }
    }





    /* Login Invisible when click outside of form */






});