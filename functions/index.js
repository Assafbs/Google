
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


/**
 Triggers when a user's budget assosiation has changed.
 */
exports.sendAddedToBudgetNotification = functions.database.ref('/users/{uid}/budgets/{bid}').onWrite(event => {
	const uid = event.params.uid;
	const bid = event.params.bid;
	// If un-follow we exit the function.
	if (!event.data.val()) 
	{
		return console.log('User ', uid, ' has left budget ', bid);
	}
	console.log('User ', uid, ' was added to budget ', bid);

	// Get notification token for user.
	const deviceNotificationTokenPromise = admin.database().ref('/users/'+uid + '/notificationToken').once('value').then(function(snapshot) {
		const deviceNotificationToken = snapshot.val(); 
	    console.log('Got device Token: ', deviceNotificationToken);

	    // Notification details.
		const payload = 
		{
			notification: 
			{
				title: 'You were added to a budget!',
				//body: `${follower.displayName} is now following you.`,
				//icon: follower.photoURL
			}
		};

		// Send notifications to all tokens.
		return admin.messaging().sendToDevice(deviceNotificationToken, payload);
	});	
});	






