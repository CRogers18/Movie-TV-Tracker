var numUsers = 0;
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

var userDbDataList = database.ref('user/');
function addUser()
{
	var username = document.getElementById('username').value;
	document.getElementById('username').value="";
	var password = document.getElementById('password').value;
	document.getElementById('password').value="";
	createUser(username,password);
	// addUserToDB(username, password, profileImage);
}

//Creates a new user
function createUser(email, password){
	firebase.auth().setPersistence(firebase.auth.Auth.Persistence.SESSION).then(function (user){
		firebase.auth().createUserWithEmailAndPassword(email,password).catch(function(error){
			console.log(error.code+": "+error.message);
		});
	});
}

function addUserImageWithRedirect(){
	var testRef = firebase.storage().ref("profile_images/" + firebase.auth().currentUser.uid);
	var profileImage = document.getElementById('profileImage').files[0];
	document.getElementById('profileImage').value="";
	.then(function(){
		window.location.href = 'login.html';
	});
}

function addUserImage(image){
	var testRef = firebase.storage().ref("profile_images/" + firebase.auth().currentUser.uid);
	testRef.put(image);
}

//Logs user in
function logInUser(email, password){
	firebase.auth().setPersistence(firebase.auth.Auth.Persistence.SESSION).then(function (){
		firebase.auth().signInWithEmailAndPassword(email, password).catch(function(error){
			console.log(error.code+": "+error.message);
		});
	});
}

//Gets info of current user
firebase.auth().onAuthStateChanged(function(user){
	if(user){
		var email = user.email;
		var uid = user.uid;

		console.log(email+" "+" "+uid);
		addUserImageWithRedirect();

	}else{
		console.log("not signed in");
	}
})


function addUserToDB(username, password, profileImage)
{
	var showRef = firebase.database().ref('user/'+username);
	showRef.once('value')
	.then(function(snapshot){
		var exists = snapshot.exists();
		if(!exists){
			showRef.set({
			   username: username,
			   password: password,
			   profileImage: profileImage
		   });
		}
	});
}


//image upload 
$(document).on('change', '.btn-file :file', function() 
{
	var input = $(this),
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
	input.trigger('fileselect', [label]);
});
$('.btn-file :file').on('fileselect', function(event, label) 
{
	var input = $(this).parents('.input-group').find(':text'),
	log = label;
	if( input.length ) 
	{
		input.val(log);
	} 
	else 
	{
		if( log ) alert(log);
	}
	
});
function readURL(input)
{
	if (input.files && input.files[0]) 
	{
		var reader = new FileReader();
		reader.onload = function (e) 
		{
			$('#img-upload').attr('src', e.target.result);
		}
	reader.readAsDataURL(input.files[0]);
	}
}
$("#profileImage").change(function()
{
	readURL(this);
});

$('#backButton').on('click', function()
{
	window.location.href = 'login.html';
});

$('#submitNewAccountButton').on('click', function()
{
	if(validate())
	{
		addUser();
		
	}
});

function validate()
{
	$('#inputErrorAlert').attr("hidden");

	var isValid = true;
	$("input").each(function() {
		var element = $(this);
		element.removeClass("is-invalid");
		if (element.val() == "" || element.val() == null)
		{
			isValid = false;
			$('#inputErrorAlert').removeAttr("hidden");
			element.addClass("is-invalid");
		}
	});
	if ($('#password').val() !== $('#passwordConfirm').val())
	{
		isValid = false;
		$('#inputErrorAlert').removeAttr("hidden");
		$('#password, #passwordConfirm').addClass("is-invalid");

	}
	return isValid;
}