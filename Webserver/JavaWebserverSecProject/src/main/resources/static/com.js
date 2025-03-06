/*
ABOUT
- managing communication with the backend
 */



//Prevent js before page loaded
// only covers login function
window.addEventListener('load', function () {
    document.body.classList.add('loaded');


    const loginFormText = document.getElementById("loginFormOutput");
    const buttonLogout = this.document.getElementById("btn_logout");

//setting up event listener for the login form submisstion
        document.getElementById("loginForm").addEventListener("submit", async function (event) {
            event.preventDefault();
            const username = document.getElementById("userName").value;
            const password = document.getElementById("password").value;
            await loginRequestHandler(username,password)
        }); // End event listender



//setting up event listener for the logout button
    //MOVED Added to the Logout button "onckick" function in appMain.js

//function to handle the process of logging in, wh. user clicks submit in login form
        async function loginRequestHandler(username,password) {
            // send login data to server
            console.log(CON_PARAM);
            console.log(session);
            let header = new Headers({
                "Content-Type": "application/json"
            });
            headerInjector(header);

            try {
                const response = await fetch(
                CON_PARAM.PROTOCOL_TYPE.valueOf()
                    +"//"
                    +CON_PARAM.DNS_NAME.valueOf()
                    +CON_PARAM.PORT.valueOf()
                    +"/login",
                    {
                    method: "POST",
                    headers: header,

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
                        buttonLogout.onclick = buttonLogoutFunction;
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
    let header = new Headers({
        "Content-Type": "application/json"
    });
    headerInjector(header);
    const response = await fetch(
    CON_PARAM.PROTOCOL_TYPE.valueOf()
        +"//"
        +CON_PARAM.DNS_NAME.valueOf()
        +CON_PARAM.PORT.valueOf()
        +"/logout",{
        method: "POST",
        headers: header,

        body: JSON.stringify({ token:session.getToken() ,username:session.getUsername()})
    });
    switch (response.status) {
        case 200:
            console.log("Logout successful");
            session = null;
            //only do if on main page and function exists
            //if(uiOnLogout != null)
            //    uiOnLogout();
            break;
        case 500:
            console.log("Server could not perform logout, (resubmission)")
            break;
        default:
            console.log("Logout unknown Response:", response.status);
            alert("Logout unknown Response");
    }
}

//function to make a request to the server to load the Account-Management page (if valid login)
async function loadUserPageRequestHandler(){
    let header = new Headers({
        "Authorization-Token": "" + session.getToken(),
        "Authorization-Username": "" + session.getUsername(),
        "Content-Type": "application/json"
    });
    headerInjector(header);
    const response = await fetch(
        CON_PARAM.PROTOCOL_TYPE.valueOf()
        +"//"
        +CON_PARAM.DNS_NAME.valueOf()
        +CON_PARAM.PORT.valueOf()
        +"/restricted/userSpace.html",
        {
        method: "GET",
        headers: header,
    });
    switch (response.status) {
        case 200:
            console.log("Authorization successful");
            const htmlContent = await response.text(); // Read the response as text
            document.body.innerHTML = htmlContent; // Inject the HTML content into the page
            //setup the User Page after switching to it
            (session != null)
                ? uiOnLoginUserPage(session.getUsername())
                :console.log("load Account Management session == null");
            setupUserSpace();
            break;
        case 401:
            console.log("UserPage access unauthorized");
            break;
        case 500:
            console.log("UserPage Server Error");
            break;
        case 409: //Browser Fingerprint Validation Failed
            alert("Error 0x5A3, please log in again!");
            buttonLogout.click();
            break;
        default:
            console.log("UserPage unknown Response:", response.status);
    }
}


//analytics
function headerInjector(headers) {
    headers.append("Screen-Resolution", `${screen.width}x${screen.height}`);
    headers.append("Timezone", Intl.DateTimeFormat().resolvedOptions().timeZone);
    headers.append("Canvas", getCanvasData());
    let webGLInfo = getWebGLData();
    headers.append("WebGL-Vendor", webGLInfo.vendor); headers.append("WebGL-Renderer", webGLInfo.renderer);
    //According to mozilla web docks limited availability, keep in mind
    //https://developer.mozilla.org/en-US/docs/Web/API/Navigator/deviceMemory
    headers.append("Device-Memory", navigator.deviceMemory ||  "Unknown"); //only in secure context ... !!!
}

//https://stackoverflow.com/questions/25508970/canvas-fingerprinting-on-chrome
function getCanvasData() {
    try {
        let canvas = document.createElement("canvas");
        let ctx = canvas.getContext("2d");

        // Set default values for drawing (opt expand further later if not dissimilar enough)
        ctx.textBaseline = "top";
        ctx.font = "10px Arial";
        ctx.fillStyle = "#f60";
        ctx.filter = "grayscale(1)";

        ctx.fillText("Hello, fingerprint!", 2, 2);
        let dataURL = canvas.toDataURL();
        let dataURL_b64 = btoa(dataURL); //to b64 string

        //honestly, just to make it shorter for debugging
        var hash = 0;
        if (dataURL_b64.length === 0) return "Not Supported";
        for (let i = 0; i < dataURL_b64.length; i++) {
            let char = dataURL_b64.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
        }
        return hash;
    } catch (e) {
        return "Not Supported";
    }

}
// Changed becaus Firefox "WEBGL_debug_renderer_info is deprecated in Firefox and will be removed. Please use RENDERER.
function getWebGLData() {
    try {
        let canvas = document.createElement("canvas");
        let gl = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");

        if (!gl) return { vendor: "Not Supported", renderer: "Not Supported" };

        let debugInfo = gl.getExtension("WEBGL_debug_renderer_info");
        let renderer = "Unknown";
        let vendor = "Unknown";

        if (debugInfo) {
            vendor = gl.getParameter(debugInfo.UNMASKED_VENDOR_WEBGL);
            renderer = gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL);
        } else {
            renderer = gl.getParameter(gl.RENDERER) || "Unknown";
        }

        return { vendor, renderer };
    } catch (e) {
        return { vendor: "Not Supported", renderer: "Not Supported" };
    }
}
