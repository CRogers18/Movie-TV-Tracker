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
		imageRef = storageRef.child(currData.title).getDownloadURL().then(function(url) {
			document.querySelector('img').src = url;
		}).catch(function(error) {
			console.log(error);
		});
	});
});

$('#saveButton').on('click', function() {
	//TODO fix validation once image upload is fixed
	//if(validateData())
	//{
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
		window.location.href = 'index.html';
	//}
	//else 
	//{
	//	document.body.scrollTop = document.documentElement.scrollTop = 0;
	//}
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
			console.log(element);
			isValid = false;
			$('#inputErrorAlert').removeAttr("hidden");
			element.addClass("is-invalid");
		}
	});

	return isValid;
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
