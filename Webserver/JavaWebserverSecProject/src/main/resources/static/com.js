/*
ABOUT
- managing communication with the backend
 */




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
            event.preventDefault();
            const username = document.getElementById("userName").value;
            const password = document.getElementById("password").value;
            await loginRequestHandler(username,password)
        }); // End event listender

//setting up event listener for the logout button
    //Added to the Logout button "onckick" function in appMain.js

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
                        //cookie which stores the session data is created
                        session.storeSessionAsCookie(30);
                            //change ui after login
                        uiOnLoginMainPage(username);
                        buttonLogout.onclick = buttonLogoutWhenLoggedInMainPage;
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
}); //event listener loaded


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
            //only do if on main page and function exists
            if(uiOnLogout != null)
                uiOnLogout();
            break;
        case 500:
            console.log("Logout Server Error")
            alert("Logout Server Error");
            break;
        default:
            console.log("Logout unknown Response:", response.status);
            alert("Logout unknown Response");
    }
}

//function to make a request to the server to load the Account-Management page (if valid login)
async function loadUserPageRequestHandler(){
    const response = await fetch("http://localhost:3000/restricted/userSpace.html", {
        method: "GET",
        headers: {
            "Authorization-Token": "" + session.getToken(),
            "Authorization-Username": "" + session.getUsername(),
            "Content-Type": "application/json",
        },
    });
    switch (response.status) {
        case 200:
            console.log("Authorization successful");
            const htmlContent = await response.text(); // Read the response as text
            document.body.innerHTML = htmlContent; // Inject the HTML content into the page
            //setup the User Page after switching to it
            (session != null) ? uiOnLoginUserPage(session.getUsername()) : console.log("load Account Management session == null");
            setupUserSpace();
            break;
        case 401:
            console.log("UserPage access unauthorized");
            break;
        case 500:
            console.log("UserPage Server Error");
            break;
        default:
            console.log("UserPage unknown Response:", response.status);
    }
}
