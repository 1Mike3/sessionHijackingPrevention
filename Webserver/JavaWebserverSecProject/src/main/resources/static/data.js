
//saved instance of a session after login used throughout the application
let session = null;

//constants
// ... poor man's enum
// used to chhange the behaviour of the Session constructor
const SESSION_CONSTRUCTOR_MODI = Object.freeze({
    CREATE_FROM_LOGIN_REQUEST: 1,
    CREATE_FROM_STORED_TOKEN: 2,
});

//for changing the code for testing during development
const DEBUG = Object.freeze({
    ACTIVE: false
});

//connection parameters used to easiely reconfigure the application
const CON_PARAM = Object.freeze([{
    PROTOCOL_TYPE: "http",  // ["http" | "https"]
    DNS_NAME: "localhost:3000"
}])