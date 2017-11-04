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
		$('#mediaName').val(currData.title);
		$('#mediaFormat').val(currData.format);
		$('#mediaCategory').val(currData.category);
		$('#mediaDescription').val(currData.description);
		$('#mediaCast').val(currData.cast);
		$('#mediaTrailerLink').val(currData.trailerLink);
		$('#mediaReleaseDate').val(currData.releaseDate);
		imageRef = storageRef.child(currData.title).getDownloadURL().then(function(url) {
			document.querySelector('img').src = url;
		}).catch(function(error) {
			console.log(error);
		});
	});
});

$('#saveButton').on('click', function() {
	query.once("child_added", function(snapshot) {
		snapshot.ref.update({ 
			title: $('#mediaName').val(),
			format: $('#mediaFormat').val(),
			category: $('#mediaCategory').val(),
			description: $('#mediaDescription').val(),
			cast: $('#mediaCast').val(),
			trailerLink: $('#mediaTrailerLink').val(),
			releaseDate: $('#mediaReleaseDate').val()
		})
	});
	window.location.href = 'index.html';
});
