//variables
var numImages = 0;
var startNumImages;
var j = 0;
var imgURLs = [];
var testCount = 0;
var numLoads = 0;
var format = "";
var allowedExtension = ['jpeg', 'jpg'];
var thisRef;

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
			thisRef = data.val();
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
				if(currData.format === 'Movie')
				{
					format = 'movies/';
				}
				else
				{
					format = 'tv/';
				}
				$('#mediaCategory').val(currData.category);
				$('#mediaDescription').val(currData.description);
				$('#mediaCast').val(currData.cast);
				$('#mediaTrailerLink').val(currData.trailerLink);
				$('#mediaReleaseDate').val(releaseDateStandard);
				imgURLs = new Array()
				for(j = 0; j < currData.image +1; j++)
				{
					testCount = j;
					imageRef = storageRef.child(format + currID + "_" + j + ".jpg");
					(function(imID) {
						(imageRef.getDownloadURL().then(function(url) {
							if(imID == 0) {
								document.querySelector('#img-upload_'+imID).src = url;
								var input = $('#mediaImage_'+imID),
									label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
								input.trigger('fileselect', [label]);
								readURL($('#mediaImage_'+imID));
								$('#mediaImageName_'+imID).val(format + currID+"_"+imID+".jpg");
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
									$('#mediaImageName_'+imID).val(format + currID+"_"+imID+".jpg");
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

$('#deleteButton').on('click', function() {
	mediaRef.child(currData.title).remove().then(function() {
	for (i = 0; i < numImages + 1; i++)
    {
    	storageRef.child(format + currID + "_" + i + ".jpg").delete();
    }
    $("#submitProgressBar").animate({
	    width: "100%"
	}, 1000);
	setTimeout(function(){
		window.location.href = 'index.html';
	}, 1000);
	});
})

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
    var isValid = true;
    var element;
    var re = /(?:\.([^.]+))?$/;

    $('#fileInputErrorAlert').attr("hidden", "true");
    $('#inputErrorAlert').attr("hidden", "true");
    $('#datetErrorAlert').attr("hidden", "true");

    $(".required").each(function() {
		var element = $(this);
        element.removeClass("is-invalid");
		if (element.val() == "" || element.val() == null)
		{
			console.log(element);
			isValid = false;
			$('#inputErrorAlert').removeAttr("hidden");
			element.addClass("is-invalid");
		}
	});

	element = $("#mediaReleaseDate");
    element.removeClass("is-invalid");

	if (element.val() == null || !validateDate(element.val()))
    {
        isValid = false;
        $('#datetErrorAlert').removeAttr("hidden");
        element.addClass("is-invalid");
    }


    for (i = 0; i < numImages + 1; i++)
    {
        elementVal = $('#mediaImageName_'+i).val();
        elementVal = re.exec(elementVal)[1];
        $('#mediaImageName_'+i).removeClass("is-invalid");
        $('#mediaImage_'+i).removeClass("is-invalid");

        if (!validateJpeg(elementVal))
        {
            isValid = false;
            $('#mediaImageName_'+i).addClass("is-invalid");
            $('#mediaImage_'+i).addClass("is-invalid");
            $('#fileInputErrorAlert').removeAttr("hidden");
        }
    }

	return isValid;
}

// validate date format
function validateDate(dateString)
{
    // First check for the pattern
    if(!/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(dateString))
        return false;

    // Parse the date parts to integers
    var parts = dateString.split("/");
    var day = parseInt(parts[1], 10);
    var month = parseInt(parts[0], 10);
    var year = parseInt(parts[2], 10);

    // Check the ranges of month and year
    if(year < 1000 || year > 3000 || month == 0 || month > 12)
        return false;

    var monthLength = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

    // Adjust for leap years
    if(year % 400 == 0 || (year % 100 != 0 && year % 4 == 0))
        monthLength[1] = 29;

    // Check the range of the day
    return day > 0 && day <= monthLength[month - 1];
}

// validate image = jpeg/jpg
function validateJpeg(fileExtension)
{
    
    var isValidFile = false;

    for(var index in allowedExtension)
    {
        if(fileExtension === allowedExtension[index])
        {
            isValidFile = true;
            break;
        }
    }

    return isValidFile;
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
		storageRef.child(format + currID + "_" + thisNum + ".jpg").delete().then(function() {
			var thisStorageRef = firebase.storage().ref(format + currID + "_" + thisNum + ".jpg");
			thisStorageRef.put(document.getElementById('mediaImage_'+thisNum).files[0]);
		});
	}
	else {
		var thisStorageRef = firebase.storage().ref(format + currID + "_" + thisNum + ".jpg");
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
	$('#newImageUploader_'+numImages).remove();
	if(numImages <= startNumImages)
	{
		storageRef.child(format + currID + "_" + numImages + ".jpg").delete().then(function() {	
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
