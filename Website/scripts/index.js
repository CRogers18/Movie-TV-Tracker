//navbar stuff
$('#createButton').on('click', function() 
{
	window.location.href = 'create.html';
});
$('#accountButton').on('click', function() 
{
	window.location.href = 'account.html';
});

var email;

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
    var releaseDateTemp = new Date(0);
    releaseDateTemp.setUTCSeconds(data.val().releaseDate);
    var releaseDateStandard = (releaseDateTemp.getMonth() + 1) + '/' + releaseDateTemp.getDate() + '/' +  releaseDateTemp.getFullYear();
    // if(data.val().uploader === email)
    // {
	   table.row.add([data.val().id, data.val().title, data.val().format, data.val().category, releaseDateStandard]).draw(false);
    // }
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

$('#mediaTable tbody').on('click', 'tr', function() {
    var selectedID = table.rows(this).data()[0][0];
    window.location.href = 'edit.html?id='+selectedID;
});

//Gets info of current user
firebase.auth().onAuthStateChanged(function(user){
    if(user){
        email = user.email;
        var uid = user.uid;

        console.log(email+" "+" "+uid);
    }else{
        console.log("not signed in");
    }
})
