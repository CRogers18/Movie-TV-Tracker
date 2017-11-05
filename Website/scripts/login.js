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

$('#loginButton').on('click', function() 
{
	uname = $('#username').val();
	pword = $('#password').val();
});

$('#createAccountButton').on('click', function()
{
	window.location.href = 'accountCreate.html';
});

//Gets info of current user
firebase.auth().onAuthStateChanged(function(user){
    if(user){
        var email = user.email;
        var uid = user.uid;

        console.log(email+" "+" "+uid);
    }else{
        console.log("not signed in");
    }
})