
//Prevent js before page loaded
window.addEventListener('load', function () {
    document.body.classList.add('loaded');

// elements
    //login form
    document.getElementById("loginForm").addEventListener("submit", async function (event) {
        event.preventDefault();
        const username = document.getElementById("userName").value;
        const password = document.getElementById("password").value;

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
        // react send login data to server
            //little helper for debugging, set to true to get alerts on success/failure
            const metaActivateAlerts = true;
            if (response.ok) {
                const data = await response.json();
                console.log("Login successful:", data);
                if (metaActivateAlerts)
                    alert("Login successful: " + data.message);
            } else {
                console.log("Login failed:", response.status);
                if (metaActivateAlerts)
                     alert("Login failed: " + response.status);
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Login Error: " + error);
        }
    });



    /* Login Invisible when click outside of form */






});