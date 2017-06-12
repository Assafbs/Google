
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

function sendNotification(uid, title, body)
{
	// Get notification token for user.
	const deviceNotificationTokenPromise = admin.database().ref('/users/'+uid + '/notificationToken').once('value').then(function(snapshot) {
		const deviceNotificationToken = snapshot.val(); 
	    console.log('Got device Token: ', deviceNotificationToken);

	    // Notification details.
		const payload = 
		{
			notification: 
			{
				title: title,
				body: body,
				//icon: follower.photoURL
			}
		};

		// Send notifications to all tokens.
		return admin.messaging().sendToDevice(deviceNotificationToken, payload);
	});	
}
/**
 Triggers when a user's budget assosiation has changed.
 */
 //******************************************************************************************************************************************************
exports.sendAddedToBudgetNotification = functions.database.ref('/users/{uid}/budgets/{bid}').onWrite(event => {
	const uid = event.params.uid;
	const bid = event.params.bid;
	// If un-follow we exit the function.
	if (!event.data.val()) 
	{
		return console.log('User ', uid, ' has left budget ', bid);
	}
	console.log('User ', uid, ' was added to budget ', bid);
	sendNotification(uid, "You were added to a budget!", "");
	

	
});	
//******************************************************************************************************************************************************
exports.sendExpenseNotification = functions.database.ref('/budgets/{bid}/budget/mExpenses/{eid}').onWrite(event => {
	const eid = event.params.eid;
	const bid = event.params.bid;
	// If un-follow we exit the function.
	if (!event.data.val()) 
	{
		return console.log('Expense ', eid ,' deleted from budget ', bid);
	}
	console.log('Expense ', eid, ' was added to budget ', bid);

	const uidsPromise = admin.database().ref("/budgets/" + bid).once('value').then(function(snapshot)
	{
		const users = snapshot.child("users").val();
		const budgetName = snapshot.child("budget").child("mName").val();
		const userName = snapshot.child("budget").child("mExpenses").child(eid).child("mUserName").val();
		const expenseAmount = snapshot.child("budget").child("mExpenses").child(eid).child("mAmount").val();
		const isExpense = snapshot.child("budget").child("mExpenses").child(eid).child("mIsExpense").val();
		var messageTitle = "";
		const messageBody = "By: " + userName + "\nFor: " + expenseAmount;
		if (isExpense) 
		{
			
			messageTitle = "New expense was added to budget: " + budgetName;
		}
		else
		{
			messageTitle = "New income was added to budget: " + budgetName;
		}

		for (var uid in users) 
		{
			sendNotification(uid, messageTitle, messageBody);
		}
		
	});

});	





