
//Prevent js before page loaded
window.addEventListener('load', function () {
    document.body.classList.add('loaded');


    const loginFormText = document.getElementById("loginFormOutput");
    const loginModal = document.getElementById("loginModal") ;
    const modalBackdrop = document.getElementById("modalBackdrop");
    const body = document.body;


//setting up event listener for the login form submisstion
        document.getElementById("loginForm").addEventListener("submit", async function (event) {

        const username = document.getElementById("userName").value;
        const password = document.getElementById("password").value;

        event.preventDefault();
        await loginRequestHandler(username,password)
        }); // End event listender


// handels the communication when
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
                        console.log(response.body.toString());
                        const data = await response.json();
                        console.log("Login successful:", data);
                        // Deactivating login form display
                        loginModal.style.display = "none";
                        modalBackdrop.style.display = "none";
                        body.classList.remove('loginForm-active');
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






    /* Login Invisible when click outside of form */






});