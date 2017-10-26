//navbar stuff
$('#createButton').on('click', function() 
{
	window.location.href = 'create.html';
});
$('#accountButton').on('click', function() 
{
	window.location.href = 'account.html';
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
//retrieve data
//TODO - retrieve only curr user's data
mediaDbDataList.on('child_added', function(data) 
{
	table.row.add([data.val().id, data.val().title, data.val().format, data.val().category, data.val().releaseDate]).draw(false);
});

//create datatable
var table = $('#mediaTable').DataTable({
	responsive: true,
	columns: [
            { title: "ID", width: "5%" },
            { title: "Title", width: "34%" },
            { title: "Format", width: "10%" },
            { title: "Category", width: "10%" },
            { title: "Release Date", width: "21%" }
        ]
});

