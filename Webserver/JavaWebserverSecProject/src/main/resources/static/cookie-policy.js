// Cookie management logic

// some elements in appMain.js on load event listenner
document.getElementById('accept-all').addEventListener('click', () => {
    document.cookie = "cookiesAccepted=true; path=/; max-age=31536000"; // 1 year
    document.getElementById('cookie-banner').style.display = 'none';
    //deac backdrop when banner closed
    modalBackdrop.style.display = "none";
    activateAllButtons();
    alert("You have accepted all cookies.");
});

