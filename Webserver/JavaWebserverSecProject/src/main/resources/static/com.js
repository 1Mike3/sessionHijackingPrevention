
//Prevent js before page loaded
window.addEventListener('load', function () {
    document.body.classList.add('loaded');

// elements
    //functional
        document.getElementById("loginForm").addEventListener("submit", async function (event) {
        event.preventDefault();
        const username = document.getElementById("userName").value;
        const password = document.getElementById("password").value;
    //For Style and User Information
        const loginFormText = document.getElementById("loginFormOutput");
        const loginModal = document.getElementById("loginModal") ;
        const modalBackdrop = document.getElementById("modalBackdrop");
        const body = document.body;

// com
    // send login data to server
        try {
            const response = await fetch("http://localhost:3000/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });


        // react "send login data to server"

            switch (response.status) {
                case 200: //OK
                    const data = await response.json();
                    console.log("Login successful:", data);
                    // Deactivating login form display
                    loginModal.style.display = "none";
                    modalBackdrop.style.display = "none";
                    body.classList.remove('loginForm-active');
                    break;
                case 204: //Missing Credentials
                    console.log("Missing credentials");
                    loginFormText.innerText = "Missing credentials!";
                    break;
                case 401: //Unauthorized
                    console.log("Username and/or password incorrect!");
                    loginFormText.innerText = "Unauthorized!";
                    break;
                default:
                    console.log("Login failed:", response.status);
            }

        } catch (error) {
            console.error("Error:", error);
            alert("Login Error: " + error);
        }

    });



    /* Login Invisible when click outside of form */






});