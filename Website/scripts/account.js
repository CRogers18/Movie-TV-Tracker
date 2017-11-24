var numMovies = 0, numShows = 0;
var action = 1, adventure = 0, comedy = 0, drama = 0, fantasy = 0, horror = 0, mystery = 0, romance = 0, scifi = 0, thriller = 0;
var actionhold = 0;
var months = [0,0,0,0,0,0,0,0,0,0,0,0];
var imageRef;
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
var storageRef = firebase.storage().ref();

//user stuff
firebase.auth().onAuthStateChanged(function(user){
	if(user){
		var email = user.email;
		var uid = user.uid;

		console.log(email+" "+" "+uid);
		$('#username').val(email);
		imageRef = storageRef.child("profile_images/"+uid);
		(imageRef.getDownloadURL().then(function(url) {
			document.querySelector('#prof_img').src = url;
		}))
		var complete = getStats(email);
		initMetrics(complete);
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
	    var releaseMonth = releaseDateTemp.getMonth()+1;
	    if (data.val().uploader === email)
	    {
	    	months[releaseMonth-1]++;
	    	monthData[0].y = months;
	    	Plotly.newPlot('monthGraph', monthData, monthLayout);
	    	switch (data.val().format) {
	    		case "Movie":
	    			numMovies++;
	    			initMetrics();
	    			break;
	    		case "TV Show":
	    			numShows++;
	    			initMetrics();
	    			break;
	    	}
	    	switch (data.val().category) {
	    		case "Action":
	    			if (actionhold > 0)
	    				action++;
	    			else actionhold++;
	    			graphData[0].values[0] = action;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Adventure":
	    			adventure++;
	    			if(adventure > 0)
	    				graphData[0].labels[1] = 'Adventure';
	    			graphData[0].values[1] = adventure;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Comedy":
	    			comedy++;
	    			graphData[0].values[2] = comedy;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Drama":
	    			drama++;
	    			graphData[0].values[3] = drama;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Fantasy":
	    			fantasy++;
	    			graphData[0].values[4] = fantasy;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Horror":
	    			horror++;
	    			graphData[0].values[5] = horror;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Mystery":
	    			mystery++;
	    			graphData[0].values[6] = mystery;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Romance":
	    			romance++;
	    			graphData[0].values[7] = romance;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Sci-Fi":
	    			scifi++;
	    			graphData[0].values[8] = scifi;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    		case "Thriller":
	    			thriller++;
	    			graphData[0].values[8] = thriller;
	    			Plotly.plot('genreGraph', graphData, layout);
	    			break;
	    	}
	    	if(actionhold == 0)
	    	{
	    		graphData[0].values[0] = 0;
	    		Plotly.plot('genreGraph', graphData, layout);
	    	}
	    }
	});
}

function initMetrics(gotStats)
{
	$('#moviesAdded').val(numMovies);
	$('#showsAdded').val(numShows);
}

//genre graph init
var graphData = [{
	values: [action, adventure, comedy, drama, fantasy, horror, mystery, romance, scifi, thriller],
	labels: ['Action', 'Adventure', 'Comedy', 'Drama', 'Fantasy', 'Horror', 'Mystery', 'Romance', 'Sci-Fi', 'Thriller'],
	type: 'pie',
	name: '',
	insidetextfont: {
		color: 'rgb(255,255,255)'
	}
}];
var layout = {
	title: "Genre Distribution"
};
Plotly.newPlot('genreGraph', graphData, layout);

//month graph init
var monthData = [
  {
    x: ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'],
    y: months,
    type: 'bar'
  }
];
var monthLayout =
{
	title: 'Release Month Distribution'
};
Plotly.newPlot('monthGraph', monthData, monthLayout);



