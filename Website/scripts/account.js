var numMovies = 0;
var numShows = 0;
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
var mediaDbDataList = database.ref('media/');

//user stuff
firebase.auth().onAuthStateChanged(function(user){
	if(user){
		var email = user.email;
		var uid = user.uid;

		console.log(email+" "+" "+uid);
		$('#username').val(email);
		getStats(email);
	}else{
		console.log("not signed in");
	}
})

function getStats(email)
{
	mediaDbDataList.on('child_added', function(data) 
	{
	    var releaseDateTemp = new Date(0);
	    releaseDateTemp.setUTCSeconds(data.val().releaseDate);
	    var releaseDateStandard = (releaseDateTemp.getMonth() + 1) + '/' + releaseDateTemp.getDate() + '/' +  releaseDateTemp.getFullYear();
	    if (data.val().uploader === email && data.val().format === 'Movie')
	    {
		   numMovies++;
		   $('#moviesAdded').val(numMovies);
	    }
	    if (data.val().uploader === email && data.val().format === 'TV Show')
	    {
		   numShows++;
		   $('#showsAdded').val(numShows);
	    }
	});
}

