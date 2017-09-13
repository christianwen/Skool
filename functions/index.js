

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
/*
 exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
 });
*/
// The Firebase Admin SDK to access the Firebase Realtime Database.

var functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.deleteTask=functions.database.ref("tasks/{task_id}").onDelete(event=>{
    var task_id=event.params.task_id;
    console.log("task deleted in "+task_id);
    let prev_class_id=event.data.previous.val().class_id;
       
    return admin.database().ref('classes/'+prev_class_id+'/tasks/'+task_id).set(null);
});

exports.pushTask=functions.database.ref("tasks/{task_id}").onCreate(event =>{
    var task_id=event.params.task_id;
    var data=event.data.val();
    console.log("data",data);
    var class_id=data.class_id;
    
    
    console.log("class id",class_id);
    var promise1 = admin.database().ref('classes/'+class_id+'/tasks/'+task_id).set(true);
    title=data.title;
    content=data.content;
    var promise2 = loadStudents(class_id).then(tokens => {
            console.log('tokens: ',tokens);

            var payload = {
                notification: {
                    title: title,
                    body: content,
                    sound: 'default',
                    badge: '1'
                }
            };
            var pr1=admin.messaging().sendToDevice(tokens, payload);

            var payload_data ={
                data: {
                    task_id: task_id
                }
            };

            var pr2=admin.messaging().sendToDevice(tokens,payload_data);

            return Promise.all([ pr1 , pr2 ]);
        });
    return Promise.all([ promise1 , promise2 ]);

});

function loadStudents(class_id) {
    let dbRef = admin.database().ref("classes/"+class_id+"/students");
    return dbRef.once('value').then(snap => {
        var tokens = [];
        var ref=admin.database().ref('students');
        var promises=[];
        snap.forEach(student =>{
            var promise=ref.child(student.key+"/token").once('value');
            promises.push(promise);

        });

        return Promise.all(promises).then(results => {
            results.forEach(result=>{
                tokens.push(result.val());
            });
            console.log("tokens",tokens);
            return tokens;
        });


    });
}

exports.deleteStudent = functions.database.ref("students/{student_id}").onDelete(event=>{
    var student_id = event.params.student_id;
    var data=event.data.previous.val();
    return admin.database().ref("classes/"+data.class_id+"/students/"+student_id).set(null);
});

exports.pushStudent = functions.database.ref("students/{student_id}").onCreate(event=>{
    var student_id = event.params.student_id;  
    var data = event.data.val();          
    var class_id = data.class_id;
    console.log(class_id,student_id);       
    return admin.database().ref("classes/"+class_id+"/students/"+student_id).set(true);        
});

exports.deleteRating = functions.database.ref("ratings/{rating_id}").onDelete(event=>{
    console.log("rating deleted");
    var rating_id=event.params.rating_id;
    var data=event.data.previous.val();
    var score=parseInt(data.score);
    var ref = admin.database().ref("teachers/"+data.to);
    var promise1 = ref.child("ratings/"+rating_id).set(null);
    var promise2 = ref.child("total_ratings").once('value').then(result=>{
        var total_ratings = parseInt(result.val())-1;
        ref.child("total_ratings").set(total_ratings);
        return ref.child("average_rating").once('value').then(result=>{
            var average_rating = (parseFloat(result.val())*(total_ratings+1)-score)/total_ratings;
            console.log(average_rating);
            return ref.child("average_rating").set(average_rating);
        });
    });
    return Promise.all([promise1,promise2]);
});

exports.pushRating = functions.database.ref("ratings/{rating_id}").onCreate(event=>{

    var rating_id=event.params.rating_id;
    var data=event.data.val();
    var score=parseInt(data.score);
    var ref = admin.database().ref("teachers/"+data.to);
    var promise1 = ref.child("ratings/"+rating_id).set(true);
    var promise2 = ref.child("total_ratings").once('value').then(result=>{
        var total_ratings = parseInt(result.val())+1;
        ref.child("total_ratings").set(total_ratings);
        return ref.child("average_rating").once('value').then(result=>{
            var average_rating = (parseFloat(result.val())*(total_ratings-1)+score)/total_ratings;
            return ref.child("average_rating").set(average_rating);
        });
    });

    return Promise.all([promise1,promise2]);
   
});

exports.deleteConfirmation = functions.database.ref("tasks/{task_id}/confirmations/{user_id}").onDelete(event=>{
    var task_id = event.params.task_id;
    var user_id = event.params.user_id;
    var ref=admin.database().ref("tasks/"+task_id+"/confirmations_count");
    
    var promise1 = admin.database().ref("students/"+user_id+"/confirmations/"+task_id).set(null);
       
        var promise2 = ref.once('value').then(result=>{
            var val;
          
            val=parseInt(result.val())-1;
            return ref.set(val);
        });
    return Promise.all([promise1,promise2]);
});

exports.pushConfirmation = functions.database.ref("tasks/{task_id}/confirmations/{user_id}").onCreate(event=>{
 
    var task_id = event.params.task_id;
    var user_id = event.params.user_id;
    var ref=admin.database().ref("tasks/"+task_id+"/confirmations_count");           
    var promise1 = admin.database().ref("students/"+user_id+"/confirmations/"+task_id).set(true);

    var promise2 = ref.once('value').then(result=>{
        var val;
        console.log(result);
        if(result.val()===null){
            val = 1;
        }
        else{
            val = parseInt(result.val())+1;
        };
        return ref.set(val);
    });
    
    return Promise.all([promise1,promise2]);
});

exports.pushComment = functions.database.ref("comments/{comment_id}").onCreate(event=>{
    var comment_id = event.params.comment_id;
    var data = event.data.val();
    var task_id = data.task_id;
    var ref =  admin.database().ref("tasks/"+task_id+"/comments_count");
    var promise1 = admin.database().ref("tasks/"+task_id+"/comments/"+comment_id).set(true);
    var promise2 = ref.once('value').then(result=>{
        var val = result.val() === null ? 1 : result.val() + 1;
        return ref.set(val);
    });
    
    return Promise.all([promise1,promise2]);
});

exports.deleteComment = functions.database.ref("comments/{comment_id}").onDelete(event=>{
    var comment_id = event.params.comment_id;
    var data = event.data.previous.val();
    var task_id = data.task_id;
    
    var ref =  admin.database().ref("tasks/"+task_id+"/comments_count");
    var promise1 = admin.database().ref("tasks/"+task_id+"/comments/"+comment_id).set(null);
    var promise2 = ref.once('value').then(result=>{
        var val = result.val() === null ? 0 : result.val() - 1;
        return ref.set(val);
    });
    
    return Promise.all([promise1,promise2]);
});