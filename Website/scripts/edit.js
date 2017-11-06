//variables
var numImages = 0;
var j = 0;

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

var currData;
var url = new URL(window.location.href);
var currID = parseInt(url.searchParams.get("id"));

// Initialize Firebase
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
var mediaRef = database.ref('media/');
var storageRef = firebase.storage().ref();
var imageRef;

var query = mediaRef.orderByChild('id').equalTo(currID);

query.on("value", function(snapshot) {
	snapshot.forEach(function(data) {
		currData = data.val();
		var releaseDateTemp = new Date(0);
		releaseDateTemp.setUTCSeconds(currData.releaseDate);
		var releaseDateStandard = (releaseDateTemp.getMonth() + 1) + '/' + releaseDateTemp.getDate() + '/' +  releaseDateTemp.getFullYear();
		$('#mediaName').val(currData.title);
		$('#mediaFormat').val(currData.format);
		$('#mediaCategory').val(currData.category);
		$('#mediaDescription').val(currData.description);
		$('#mediaCast').val(currData.cast);
		$('#mediaTrailerLink').val(currData.trailerLink);
		$('#mediaReleaseDate').val(releaseDateStandard);
		$('#mediaImageName').val(currData.image);
		//TODO fix image retrieval
		for(j = 0; j < currData.image; j++)
		{
			imageRef = storageRef.child(currID + "_" + numImages).getDownloadURL().then(function(url) {
				if(j > 0) {
					addImageStuff();
				}
				document.querySelector('#img-upload_'+j).src = url;
				}).catch(function(error) {
					console.log(error);
			});

		}
	});
});

//image upload 
$(document).on('change', '.btn-file :file', function() 
{
	var input = $(this),
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
	input.trigger('fileselect', [label]);
});
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

$('#saveButton').on('click', function() {
	//TODO fix validation once image upload is fixed
	if(validateData())
	{
		//format release date in unix timestamp
		var releaseDateObject = new Date(document.getElementById('mediaReleaseDate').value);
		var releaseDateUnix = Math.round(releaseDateObject.getTime()/1000);
	
		query.once("child_added", function(snapshot) {
			snapshot.ref.update({ 
				title: $('#mediaName').val(),
				format: $('#mediaFormat').val(),
				category: $('#mediaCategory').val(),
				description: $('#mediaDescription').val(),
				cast: $('#mediaCast').val(),
				trailerLink: $('#mediaTrailerLink').val(),
				releaseDate: releaseDateUnix
			})
		});
		for(i = 0; i < numImages + 1; i++)
		{
			addImage(mediaId, document.getElementById('mediaImage_'+i).files[0], i);
		}
		// $("#submitProgressBar").animate({
		// 	    width: "100%"
		// 	}, 1000);
		// setTimeout(function(){
    		window.location.href = 'index.html';
		// }, 1000);
	}
	else
	{
		document.body.scrollTop = document.documentElement.scrollTop = 0;
	}
});

function validateData()
{
	$('#inputErrorAlert').attr("hidden");
	var isValid = true;
	$(".required").each(function() {
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

function addImage(mediaId,image){
	addImageAttempt(mediaId,image,0);
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
			console.log("FAILED")
			testRef.put(image);
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
	console.log("numimages in addimagestuff: " + numImages);
	numImages++;
	addImageUploader();
	addAddImageButtons();
}
function addImageUploader()
{
 	$("#theForm").append("<div id='newImageUploader_"+numImages+"' class='form-row'><div class='form-group col-md-5'><label for='mediaImage'>Upload Image</label><div class='input-group'><span class='input-group-btn'><span class='btn btn-light btn-file' type='button'>Browse <input type='file' id='mediaImage_"+numImages+"' class='mediaImage' imgNum='"+numImages+"'></span></span><input type='text' class='form-control' id='mediaImageName' readonly></div></div><div class='col-md-1'></div><div class='col-md-3'><img id='img-upload_"+numImages+"' imgNum='"+numImages+"' class='img_upload'/></div></div>");

}
function addAddImageButtons()
{
	$('#addImageRow').remove();
	$("#theForm").append("<div id='addImageRow' class='form-row'><div class='form-group'><button id='addImageButton'  type='button' class='btn btn-outline-dark addImageButton' onclick='addImageStuff()'>Add Image</button></div></div>");
	$('#addImageRow').append("<div class='form-group col-md-3'><button id='removeImageButton' type='button' class='btn btn-outline-danger removeImageButton' onclick='removeImageButtons()'>Remove Image</button></div>")
}
function removeImageButtons()
{
	console.log('why');
	$('#newImageUploader_'+numImages).remove();
	numImages--;
	if(numImages == 0)
	{
		$('#removeImageButton').remove();
	}
}
