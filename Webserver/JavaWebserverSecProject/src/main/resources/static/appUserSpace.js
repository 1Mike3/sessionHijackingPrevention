/*
ABOUT
managing ui interactions an visualization
 */



function setupUserSpace() {

    const buttonLogoutUserSpace = document.getElementById("btn_logout");
    const buttonToggleUserSpace = document.getElementById("btn_togglePage");


//has to be implemented differently because it redirects to the main page after logout
    async function buttonLogoutWhenLoggedInUserPage() {
        // Setting flag to indicate logout on the main page (onload)
        //
        localStorage.setItem("loggedOut", "true");
        // Send the logout request to the server and wait for it to complete
        try {
            await logoutRequestHandler();
            Session.cookieKillall();
            // Redirect to the main page
            window.location.href = "http://localhost:3000/";
        } catch (error) {
            console.error("Logout failed:", error);
            alert("Failed to log out. Please try again.");
        }
    }

    //set default button functions for login and logout on startup
    buttonLogoutUserSpace.onclick = buttonLogoutWhenLoggedInUserPage;


    buttonToggleUserSpace.onclick = function () {
        window.location.href = "http://localhost:3000";

    }

}



