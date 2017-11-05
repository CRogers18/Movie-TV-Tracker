//variables
var maxRecordId = 0;
var numImages = 0;

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
//$('.btn-file :file').on('fileselect', function(event, label) 
$(document).on('fileselect', '.btn-file :file', function(event, label)
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
			$('#img-upload_'+input.getAttribute("imgNum")).attr('src', e.target.result);
		}
	reader.readAsDataURL(input.files[0]);
	}
}
$(document).on('change', '.mediaImage', function()
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
			}, 1000*numImages);
		setTimeout(function(){
    		window.location.href = 'index.html';
		}, 1000*numImages);
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
	//format release date in unix timestamp
	var releaseDateObject = new Date(document.getElementById('mediaReleaseDate').value);
	var releaseDateUnix = Math.round(releaseDateObject.getTime()/1000);
	
	//put all values in variables
	var mediaId = maxRecordId+1;
	var mediaName = document.getElementById('mediaName').value;
	var mediaDescription = document.getElementById('mediaDescription').value;
	var mediaReleaseDate = releaseDateUnix;
	var mediaImage = document.getElementById('mediaImage_0').value;
	var mediaCategory = document.getElementById('mediaCategory').value;
	var mediaFormat = document.getElementById('mediaFormat').value;
	var currentUser = firebase.auth().currentUser;
	if(currentUser){
		var mediaUploader= currentUser.email; 
	} else {
		mediaUploader = "N/A";
		console.log("failed to find current user");
	}
	var mediaCast = document.getElementById('mediaCast').value;
	var mediaTrailerLink = document.getElementById('mediaTrailerLink').value;

	for(i = 0; i < numImages + 1; i++)
	{
		console.log(i);
		addImage(mediaId, document.getElementById('mediaImage_'+i).files[0], i);
	}
	
	addMediaToDB(mediaId, mediaName, mediaDescription, mediaReleaseDate, mediaImage, mediaCategory, mediaFormat, mediaUploader, mediaCast, mediaTrailerLink);
}

function addImage(mediaId,image,imgNum){
	addImageAttempt(mediaId,image,imgNum);
}

function addImageAttempt(mediaId, image, imageId){
	console.log(imageId);
	var testRef = firebase.storage().ref(mediaId+"_"+imageId);
	testRef.getDownloadURL().then(
		function(url){
			console.log(url)
			addImageAttempt(mediaId,image,imageId+1);
		}).catch(
		function(error){
			console.log("FAILED");
			testRef.put(image);
	});
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

//Add/remove multiple images ui elements
$('#addImageButton').on('click', function() {
	addImageStuff();
});
function addImageStuff()
{
	numImages++;
	addImageUploader();
	addAddImageButtons();
}
function addImageUploader()
{
 	$("#theForm").append("<div id='newImageUploader' class='form-row'><div class='form-group col-md-5'><label for='mediaImage'>Upload Image</label><div class='input-group'><span class='input-group-btn'><span class='btn btn-light btn-file' type='button'>Browse <input type='file' id='mediaImage_"+numImages+"' class='mediaImage' imgNum='"+numImages+"'></span></span><input type='text' class='form-control' id='mediaImageName' readonly></div></div><div class='col-md-1'></div><div class='col-md-3'><img id='img-upload_"+numImages+"' imgNum='"+numImages+"' class='img_upload'/></div></div>");

}
function addAddImageButtons()
{
	$('#addImageRow').remove();
	$("#theForm").append("<div id='addImageRow' class='form-row'><div class='form-group'><button id='addImageButton'  type='button' class='btn btn-outline-dark addImageButton' onclick='addImageStuff()'>Add Image</button></div></div>");
	$('#addImageRow').append("<div class='form-group col-md-3'><button id='removeImageButton' type='button' class='btn btn-outline-danger removeImageButton' onclick='removeImageButtons()'>Remove Image</button></div>")
}
function removeImageButtons()
{
	numImages--;
	if(numImages == 0)
	{
		$('#removeImageButton').remove();
	}
	$('#newImageUploader').remove();
}