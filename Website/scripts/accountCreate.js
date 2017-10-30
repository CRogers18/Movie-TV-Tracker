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
	var profileImage = document.getElementById('profileImage').value;
	document.getElementById('profileImage').value="";

	console.log(username);
	addUserToDB(username, password, profileImage);
}

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
		window.location.href = 'login.html';
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