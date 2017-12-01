//database connection
var config = {
    apiKey: "AIzaSyCt6z6XTg0HFZqKyyXhMDPicSYmgu_2XwM",
    authDomain: "poosd-test.firebaseapp.com",
    databaseURL: "https://poosd-test.firebaseio.com",
    projectId: "poosd-test",
    storageBucket: "poosd-test.appspot.com",
    messagingSenderId: "1087046017322"
};
firebase.initializeApp(config);
var database = firebase.database();
var usersDb = database.ref('users/');

var uname;
var pword;

$('#loginButton').on('click', function() {
    uname = $('#username').val();
    pword = $('#password').val();
    firebase.auth().signInWithEmailAndPassword(uname, pword);
});

$('#createAccountButton').on('click', function() {
    window.location.href = 'accountCreate.html';
});

$('#facebookLoginButton').on('click', function() {
    facebookLogin();
});
$('#googleLoginButton').on('click', function() {
    googleLogin();
});

function facebookLogin() {
    var facebookProvider = new firebase.auth.FacebookAuthProvider();
    firebase.auth().signInWithRedirect(facebookProvider);
}

function googleLogin() {
    var googleProvider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithRedirect(googleProvider);
}

function logOut() {
    firebase.auth().signOut();
}

//Gets info of current user
firebase.auth().onAuthStateChanged(function(user) {
    if (user) {
        var email = user.email;
        var uid = user.uid;

        console.log(email + " " + " " + uid);
        window.location.href = 'index.html';
    } else {
        console.log("not signed in");
    }
})