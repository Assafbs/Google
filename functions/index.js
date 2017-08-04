
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database. 
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

function sendNotification(uid, title, body, pBid)
{
	// Get notification token for user.
	const deviceNotificationTokenPromise = admin.database().ref('/users/'+uid + '/notificationToken').once('value').then(function(snapshot) {
		const deviceNotificationToken = snapshot.val(); 
	    console.log('Got device Token: ', deviceNotificationToken);

	    // Notification details.
		const payload = 
		{
			data:
			{
				bid: pBid
			},

			notification: 
			{
				title: title,
				body: body,
				click_action: "ACTIVITY_OPEN_BUDGET_WHEN_READY",
				icon: "mezu_logo"
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

	// if user left budget.
	if (!event.data.val()) 
	{
		return console.log('User ', uid, ' has left budget ', bid);
	}
	console.log('User ', uid, ' was added to budget ', bid);
	const userSettingsPromise = admin.database().ref("/users/" + uid + "/settings/notifyWhenAddedToBudget").once('value').then(function(snapshot)
	{
		const shouldNotify = snapshot.val();
		if (shouldNotify)
		{
			sendNotification(uid, "You were added to a budget!", "", bid);	
		}
	});
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
		const expenses = snapshot.child("budget").child("mExpenses").val();
		var totalBudgetExpenses = 0;
		const currentExpenseDate = new Date(expenses[eid]["mTime"]);
		for (var curreid in expenses)
		{
			if ((new Date(expenses[curreid]["mTime"])).getMonth() == currentExpenseDate.getMonth())
			{
				if (expenses[curreid]["mIsExpense"]) 
				{
					totalBudgetExpenses += expenses[curreid]["mAmount"];
				}
				else
				{	
					totalBudgetExpenses -= expenses[curreid]["mAmount"];	
				}
			}
		}
		console.log('Expense sum is ' + totalBudgetExpenses);

		var messageTitle = "";
		var messageBody = "By: " + userName + "\nFor: " + expenseAmount;
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
			const luid = uid;
			const userSettingsPromise = admin.database().ref("/users/" + luid + "/settings").once('value').then(function(snapshot)
			{
				const shouldNotifyOnTransaction = snapshot.child("shouldNotifyOnTransaction").val();
				if (shouldNotifyOnTransaction) 
				{
					const minimalNotificationValue = snapshot.child("minimalNotificationValue").val();
					if (expenseAmount >= minimalNotificationValue) 
					{
						sendNotification(luid, messageTitle, messageBody, bid);
					}
				}

				if (snapshot.hasChild(bid))
				{
					const thresholdSettingEnabled = snapshot.child(bid).child("thresholdSettings").child("shouldNotify").val();
					if (thresholdSettingEnabled)
					{
						const thresholdVal = snapshot.child(bid).child("thresholdSettings").child("threshold").val();
						if (totalBudgetExpenses >= thresholdVal)
						{
						 	messageTitle = "Budget " + budgetName + " has gone over threshold";
						 	messageBody = "Threshold is: " + thresholdVal + " while the budget's sum of expenses is: " + totalBudgetExpenses  + " for: " + (currentExpenseDate.getMonth() + 1) + "/"+(currentExpenseDate.getYear() - 100);
						 	sendNotification(luid, messageTitle, messageBody, bid);
						}
					}	
				}
			});
		}
	});
});	





