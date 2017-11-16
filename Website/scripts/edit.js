//variables
var numImages = 0;
var startNumImages;
var j = 0;
var imgURLs = [];
var testCount = 0;
var numLoads = 0;

var currData;
var url = new URL(window.location.href);
var currID = parseInt(url.searchParams.get("id"));

//navbar stuff
$('#createButton').on('click', function() 
{
	window.location.href = 'create.html';
});
$('#accountButton').on('click', function() 
{
	window.location.href = 'account.html';
});
$('#logoffButton').on('click', function() 
{
    window.location.href = 'login.html';
});

//datepicker initialize
$('#mediaReleaseDate').datepicker({
	uiLibrary: 'bootstrap4',
	iconsLibrary: 'fontawesome'
});

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
loadIn();
function loadIn()
{
	query.on("value", function(snapshot) {
		snapshot.forEach(function(data) {
			numLoads++;
			if(numLoads == 1)
			{
				currData = data.val();
				var releaseDateTemp = new Date(0);
				releaseDateTemp.setUTCSeconds(currData.releaseDate);
				var releaseDateStandard = (releaseDateTemp.getMonth() + 1) + '/' + releaseDateTemp.getDate() + '/' +  releaseDateTemp.getFullYear();
				startNumImages = currData.image;
				$('#mediaName').val(currData.title);
				$('#mediaFormat').val(currData.format);
				$('#mediaCategory').val(currData.category);
				$('#mediaDescription').val(currData.description);
				$('#mediaCast').val(currData.cast);
				$('#mediaTrailerLink').val(currData.trailerLink);
				$('#mediaReleaseDate').val(releaseDateStandard);
				imgURLs = new Array()
				for(j = 0; j < currData.image +1; j++)
				{
					testCount = j;
					imageRef = storageRef.child(currID + "_" + j);
					(function(imID) {
						(imageRef.getDownloadURL().then(function(url) {
							if(imID == 0) {
								document.querySelector('#img-upload_'+imID).src = url;
								var input = $('#mediaImage_'+imID),
									label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
								input.trigger('fileselect', [label]);
								readURL($('#mediaImage_'+imID));
								$('#mediaImageName_'+imID).val(currID+"_"+imID+".jpg");
							}
							if(imID > 0) {
								let createHolderPromise = new Promise((resolve, reject) => {
									addImageStuff();
									setTimeout(function(){
										resolve(url);
									}, 200);
								});
								createHolderPromise.then((url) => {
									document.querySelector('#img-upload_'+imID).src = url;
									var input = $('#mediaImage_'+imID),
										label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
									input.trigger('fileselect', [label]);
									readURL($('#mediaImage_'+imID));
									$('#mediaImageName_'+imID).val(currID+"_"+imID+".jpg");
								});
							}
							imgURLs[imID] = url;
							}).catch(function(error) {
								console.log(error);
						}))
					})(testCount);
				}
			}
		});
	});
}

$('#saveButton').on('click', function() {
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
				image: numImages,
				trailerLink: $('#mediaTrailerLink').val(),
				releaseDate: releaseDateUnix
			})
		});
		$("#submitProgressBar").animate({
			    width: "100%"
			}, 1000);
		setTimeout(function(){
			window.location.href = 'index.html';
		}, 1000);
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
	var thisNum = this.getAttribute("imgNum");

	if(thisNum <= currData.image) {
		storageRef.child(currID + "_" + thisNum).delete().then(function() {
			var thisStorageRef = firebase.storage().ref(currID + "_" + thisNum);
			thisStorageRef.put(document.getElementById('mediaImage_'+thisNum).files[0]);
		});
	}
	else {
		var thisStorageRef = firebase.storage().ref(currID + "_" + thisNum);
		thisStorageRef.put(document.getElementById('mediaImage_'+thisNum).files[0]);
	}
	query.once("child_added", function(snapshot) {
		snapshot.ref.update({
			image: numImages
		})
	});
});

function addImage(mediaId,image){
	addImageAttempt(mediaId,image,0);
}

function addImageAttempt(mediaId, image, imageId){
	var testRef = firebase.storage().ref(mediaId+"_"+imageId);
	testRef.getDownloadURL().then(
		function(url){
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
	numImages++;
	addImageUploader();
	addAddImageButtons();
}
function addImageUploader()
{
	$("#theForm").append("<div id='newImageUploader_"+numImages+"' class='form-row'><div class='form-group col-md-5'><label for='mediaImage'>Upload Image</label><div class='input-group'><span class='input-group-btn'><span class='btn btn-light btn-file' type='button'>Browse <input type='file' id='mediaImage_"+numImages+"' class='mediaImage' imgNum='"+numImages+"' accept='.jpg'></span></span><input id='mediaImageName_"+numImages+"' type='text' class='form-control required' id='mediaImageName' readonly></div></div><div class='col-md-1'></div><div class='col-md-3'><img id='img-upload_"+numImages+"' imgNum='"+numImages+"' class='img_upload'/></div></div>");

}
function addAddImageButtons()
{
	$('#addImageRow').remove();
	$("#theForm").append("<div id='addImageRow' class='form-row'><div class='form-group'><button id='addImageButton'  type='button' class='btn btn-outline-dark addImageButton' onclick='addImageStuff()'>Add Image</button></div></div>");
	$('#addImageRow').append("<div class='form-group col-md-3'><button id='removeImageButton' type='button' class='btn btn-outline-danger removeImageButton' onclick='removeImageButtons()'>Remove Image</button></div>")
}
function removeImageButtons()
{
	console.log(numImages);
	$('#newImageUploader_'+numImages).remove();
	if(numImages <= startNumImages)
	{
		storageRef.child(currID + "_" + numImages).delete().then(function() {	
			numImages--;
			if(numImages == 0)
			{
				$('#removeImageButton').remove();
			}
		});
	}
	else
	{

		numImages--;
		if(numImages == 0)
		{
			$('#removeImageButton').remove();
		}
	}
	query.once("child_added", function(snapshot) {
		snapshot.ref.update({
			image: numImages
		})
	});
}
