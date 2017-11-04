//variables
var maxRecordId = 0;

//navbar stuff
$('#createButton').on('click', function() 
{
	window.location.href = 'create.html';
});
$('#accountButton').on('click', function() 
{
	window.location.href = 'account.html';
});

//datepicker initialize
$('#mediaReleaseDate').datepicker({
    uiLibrary: 'bootstrap4',
    iconsLibrary: 'fontawesome'
});

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

var mediaDbDataList = database.ref('media/');
mediaDbDataList.on('child_added', function(data) 
{
	if(data.val().id > maxRecordId)
	{
		maxRecordId = data.val().id;
	}
});

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
$("#mediaImage").change(function()
{
	readURL(this);
});

$('#submitButton').on('click', function() 
{
	if(validateData())
	{
		addMedia();
		$("#submitProgressBar").animate({
			    width: "100%"
			}, 1000);
		setTimeout(function(){
    		window.location.href = 'index.html';
		}, 1000);
	}
});

function validateData()
{
	$('#inputErrorAlert').attr("hidden");
	var isValid = true;
	$("input, select, textarea").each(function() {
		var element = $(this);
		element.removeClass("is-invalid");
		if (element.val() == "" || element.val() == null)
		{
			isValid = false;
			$('#inputErrorAlert').removeAttr("hidden");
			element.addClass("is-invalid");
		}
	});

	return isValid;
}


function addMedia()
{
	var mediaId = maxRecordId+1;
	var mediaName = document.getElementById('mediaName').value;
	var mediaDescription = document.getElementById('mediaDescription').value;
	var mediaReleaseDate = document.getElementById('mediaReleaseDate').value;
	var mediaImage = document.getElementById('mediaImage').value;
	var actualImageFile = document.getElementById('mediaImage').files[0];
	var mediaCategory = document.getElementById('mediaCategory').value;
	var mediaFormat = document.getElementById('mediaFormat').value;
	var mediaUploader= "TODO"; //need to get user system figured out first
	var mediaCast = document.getElementById('mediaCast').value;
	var mediaTrailerLink = document.getElementById('mediaTrailerLink').value;
	var storageRef = firebase.storage().ref(mediaName);
	storageRef.put(actualImageFile);
	addMediaToDB(mediaId, mediaName, mediaDescription, mediaReleaseDate, mediaImage, mediaCategory, mediaFormat, mediaUploader, mediaCast, mediaTrailerLink);
}

function addMediaToDB(mediaId, name, mediaDescription, mediaReleaseDate, mediaImage, mediaCategory, mediaFormat, mediaUploader, mediaCast, mediaTrailerLink){
	var mediaRef = firebase.database().ref('media/'+name);
	mediaRef.once('value')
	.then(function(snapshot){
		var exists = snapshot.exists();
		if(!exists){
			mediaRef.set({
			   id: mediaId,
			   title: name,
			   description: mediaDescription,
			   releaseDate: mediaReleaseDate,
			   image: mediaImage,
			   category: mediaCategory,
			   format: mediaFormat,
			   uploader: mediaUploader,
			   cast: mediaCast,
			   trailerLink: mediaTrailerLink
		   });
		}
	});
}