/*
ABOUT
- managing user login sessions
 */

class Session{
    constructor(username,token) {
        this.username = username;
        this.token = token;
    }

        static onStartup(){
        //TODO check if cookie token present if so load and make request to backend
        //TODO if so create new session object with response from backend
        }

        getUsername(){
        return this.username
        }
        getToken(){
        return this.token
        }


}


