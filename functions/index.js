
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
			// send notification to user only if he is not the one that created the budget.
			const budgetPromise = admin.database().ref("/budgets/" + bid + "/budget").once('value').then(function(snapshot2)
			{
				if (snapshot2.hasChild("mOwner"))
				{
					if(snapshot2.child("mOwner").val() != uid)
					{
						sendNotification(uid, "You were added to a budget!", "", bid);				
					}
				}
			});
		}
	});
});	
//******************************************************************************************************************************************************
function addToList(dictionaryList, element)
{
	var highestIndex = 0;
	for(var index in dictionaryList)
	{
		if(highestIndex < index)
		{
			highestIndex = index;
		}
	}
	dictionaryList[highestIndex+1] = element;
}
//******************************************************************************************************************************************************
exports.handlePendingList = functions.database.ref('/budgets/{bid}/budget/mPending').onWrite(event => {
	const bid = event.params.bid;
	console.log('new pending for bid: ' + bid);
	const pendingPromise = admin.database().ref('/budgets/'+ bid).once('value').then(function(snapshot)
	{
		const pendingEmails = snapshot.val()["budget"]["mPending"];
		console.log('pending users are ' + JSON.stringify(pendingEmails));
		const usersPromise = admin.database().ref("/users").once('value').then(function(snapshot2)
		{
			for(var pendingEmailEncoded in pendingEmails)
			{
				var pendingEmail = pendingEmails[pendingEmailEncoded];
				for (var user in snapshot2.val())
				{
					if(snapshot2.val()[user]["email"] == pendingEmail)
					{
						console.log('found user ' + user);
						var budgetUsers = snapshot.val()["users"];
						budgetUsers[user] = user;
						admin.database().ref("/budgets/" + bid + "/users").set(budgetUsers);
						
						var budgetEmails = snapshot.val()["budget"]["mEmails"];
						addToList(budgetEmails, pendingEmail);
						admin.database().ref("/budgets/" + bid + "/budget/mEmails").set(budgetEmails);

						var userBudgets = snapshot2.val()[user]["budgets"];
						if(null == userBudgets)
						{
							userBudgets = {};
						}
						userBudgets[bid] = bid;
						admin.database().ref("/users/" + user +"/budgets" ).set(userBudgets);
						admin.database().ref("/budgets/" + bid + "/budget/mPending/" + pendingEmailEncoded).set(null)
						break;
					}
				}
			}
		});
	});
});
//******************************************************************************************************************************************************
exports.connectNewUserToPendingBudgets = functions.database.ref('/users/{uid}').onCreate(event => {
	const uid = event.params.uid;
	// try adding the user to all its pending budgets.
	const budgetsPromise = admin.database().ref('/budgets/').once('value').then(function(snapshot)
	{
		const userPromise = admin.database().ref('/users/' + uid).once('value').then(function(snapshot2)
		{
			var newUserEmail = snapshot2.val()["email"];
			var pendingEmailEncoded = Buffer.from(newUserEmail).toString('base64');
			console.log("connecting new user with mail: " + newUserEmail + " base64 is: " + pendingEmailEncoded);
			for(bid in snapshot.val())
			{
				if("mPending" in snapshot.val()[bid]["budget"])
				{
					if( pendingEmailEncoded in snapshot.val()[bid]["budget"]["mPending"])
					{
						console.log('adding new user to bid ' + bid + " because it was pending");
						var budgetUsers = snapshot.val()[bid]["users"];
						budgetUsers[uid] = uid;
						admin.database().ref("/budgets/" + bid + "/users").set(budgetUsers);
						
						var budgetEmails = snapshot.val()[bid]["budget"]["mEmails"];
						addToList(budgetEmails, newUserEmail);
						admin.database().ref("/budgets/" + bid + "/budget/mEmails").set(budgetEmails);

						var userBudgets = snapshot2.val()["budgets"];
						if(null == userBudgets)
						{
							userBudgets = {};
						}
						userBudgets[bid] = bid;
						admin.database().ref("/users/" + uid +"/budgets" ).set(userBudgets);
						admin.database().ref("/budgets/" + bid + "/budget/mPending/" + pendingEmailEncoded).set(null)
					}
				}
			}	
		});
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
		const newExpenseAddedBy = snapshot.child("budget").child("mExpenses").child(eid).child("mUserID").val();
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
				if(luid != newExpenseAddedBy)
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





