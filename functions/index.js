
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
	var indexAsInt = 0;
	for(var index in dictionaryList)
	{
		indexAsInt = parseInt(index);
		if(highestIndex < indexAsInt)
		{
			highestIndex = indexAsInt;
		}
	}	
	dictionaryList[highestIndex+1] = element;
}
//******************************************************************************************************************************************************
function addToListFromHashSet(dictionaryList, hashSet)
{
	var highestIndex = 0;
	var indexAsInt = 0;
	for(var index in dictionaryList)
	{
		indexAsInt = parseInt(index);
		if(highestIndex < indexAsInt)
		{
			highestIndex = indexAsInt;
		}
	}	
	var indexToAdd = highestIndex + 1;
	for(var element in hashSet)
	{
		dictionaryList[indexToAdd] = element;	
		indexToAdd = indexToAdd + 1;
	}
	
}
//******************************************************************************************************************************************************
exports.handlePendingList = functions.database.ref('/budgets/{bid}/budget/mPending').onWrite(event => {
	const bid = event.params.bid;
	var emailsToAdd = {};
	var uidsToAdd = {};
	console.log('new pending for bid: ' + bid);
	const pendingPromise = admin.database().ref('/budgets/'+ bid).once('value').then(function(snapshot)
	{
		const pendingEmails = snapshot.val()["budget"]["mPending"];
		console.log('pending users are ' + JSON.stringify(pendingEmails));
		for(var pendingEmailEncoded in pendingEmails)
		{
			const lPendingEmailEncoded = pendingEmailEncoded;
			const lPendingEmail = pendingEmails[pendingEmailEncoded];
			const uidPromise = admin.database().ref('/mails/'+ pendingEmailEncoded + "/uid").once('value').then(function(snapshot2)
			{
				const uid = snapshot2.val();
				if (null == uid)
				{
					const uidPromise = admin.database().ref('/mails/'+ pendingEmailEncoded + "/pendingBudgets").once('value').then(function(snapshot3)
					{
						var pendingBudgets = snapshot3.val();
						if (null == pendingBudgets)
						{
							pendingBudgets = {};
						}
						pendingBudgets[bid] = bid;
						admin.database().ref('/mails/'+ pendingEmailEncoded + "/pendingBudgets").set(pendingBudgets);
					});
				}
				else
				{
					const uidPromise = admin.database().ref('/users/'+ uid).once('value').then(function(snapshot3)
					{
						var budgetUsers = snapshot.val()["users"];
						uidsToAdd[uid] = uid;
						for(user in uidsToAdd)
						{
							budgetUsers[user] = user;
						}
						budgetUsers[uid] = uid;
						admin.database().ref("/budgets/" + bid + "/users").set(budgetUsers);
						
						var budgetEmails = snapshot.val()["budget"]["mEmails"];
						emailsToAdd[lPendingEmail] = lPendingEmail;
						addToListFromHashSet(budgetEmails, emailsToAdd);
						admin.database().ref("/budgets/" + bid + "/budget/mEmails").set(budgetEmails);

						var userBudgets = snapshot3.val()["budgets"];
						if(null == userBudgets)
						{
							userBudgets = {};
						}
						userBudgets[bid] = bid;
						admin.database().ref("/users/" + uid + "/budgets" ).set(userBudgets);
						admin.database().ref("/budgets/" + bid + "/budget/mPending/" + lPendingEmailEncoded).set(null)
					});
				}
			});
		}
	});
});

//******************************************************************************************************************************************************
exports.connectNewUserToPendingBudgets = functions.database.ref('/users/{uid}').onCreate(event => {
	const uid = event.params.uid;
	var bidsToAddToUserBudget = {};
	// check if user has any pending budgets
	const userPromise = admin.database().ref('/users/' + uid).once('value').then(function(snapshot)
	{
		const newUserMail = snapshot.val()["email"];
		const newUserMailEncoded  = Buffer.from(newUserMail).toString('base64');
		const pendingBudgetsPromise = admin.database().ref('/mails/' +  newUserMailEncoded + '/pendingBudgets').once('value').then(function(snapshot2)
		{
			var pendingBudgets = snapshot2.val();
			if (null != pendingBudgets)
			{
				for(var bid in pendingBudgets)
				{
					const lBid = bid;
					const budgetPromise = admin.database().ref('/budgets/' + lBid).once('value').then(function(snapshot3)
					{
						var budget = snapshot3.val();
						if (null != budget)
						{
							console.log('adding new user to bid ' + lBid + " because it was pending");
							var budgetUsers = snapshot3.val()["users"];
							budgetUsers[uid] = uid;
							admin.database().ref("/budgets/" + lBid + "/users").set(budgetUsers);
							
							var budgetEmails = snapshot3.val()["budget"]["mEmails"];
							addToList(budgetEmails, newUserMail);
							admin.database().ref("/budgets/" + lBid + "/budget/mEmails").set(budgetEmails);

							// this is done in such a convoluted way we need to keep who we already added in every loop.
							var userBudgets = snapshot.val()["budgets"];
							bidsToAddToUserBudget[lBid] = lBid;
							if (null == userBudgets) 
							{
								userBudgets = {};
							}
							for(var bidToAdd in bidsToAddToUserBudget)
							{
								userBudgets[bidToAdd] = bidToAdd;
							}
							admin.database().ref("/users/" + uid +"/budgets" ).set(userBudgets);

							admin.database().ref("/budgets/" + lBid + "/budget/mPending/" + newUserMailEncoded).set(null)
						}
						admin.database().ref('/mails/' +  newUserMailEncoded + '/pendingBudgets/' + lBid).set(null);
					});				
				}
			}
		});
	});
});
//******************************************************************************************************************************************************
exports.sendExpenseNotification = functions.database.ref('/budgets/{bid}/budget/mExpenses/{eid}').onCreate(event => {
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
		const budgetThreshold = snapshot.child("budget").child("mCategoryCeilings").child("CATEGORY").val();
		const userName = snapshot.child("budget").child("mExpenses").child(eid).child("mUserName").val();
		const expenseAmount = snapshot.child("budget").child("mExpenses").child(eid).child("mAmount").val();
		const isExpense = snapshot.child("budget").child("mExpenses").child(eid).child("mIsExpense").val();
		const expenses = snapshot.child("budget").child("mExpenses").val();
		const newExpenseAddedBy = snapshot.child("budget").child("mExpenses").child(eid).child("mUserID").val();
		const mPeriod = snapshot.child("budget").child("mExpenses").child(eid).child("mPeriodic").val();
		if (null != mPeriod)
		{
			if(false == mPeriod["isFirst"])
			{
				return;
			}
				
		}
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
		var periodString = "";
		if (null != mPeriod)
		{
			periodString = " " + mPeriod["recurrenceTime"];
		}
		if (isExpense) 
		{			
			messageTitle = "New"+ periodString + " expense was added to budget: " + budgetName;
		}
		else
		{
			messageTitle = "New"+ periodString + " income was added to budget: " + budgetName;
		}

		for (var uid in users)
		{
			const luid = uid;
			const userSettingsPromise = admin.database().ref("/users/" + luid + "/settings").once('value').then(function(snapshot2)
			{
				if(luid != newExpenseAddedBy)
				{
					const shouldNotifyOnTransaction = snapshot2.child("shouldNotifyOnTransaction").val();
					if (shouldNotifyOnTransaction) 
					{
						const minimalNotificationValue = snapshot2.child("minimalNotificationValue").val();
						if (expenseAmount >= minimalNotificationValue) 
						{
							sendNotification(luid, messageTitle, messageBody, bid);
						}
					}	
				}
				
				if (snapshot2.hasChild("nofityBudgetExceeded"))
				{
					const thresholdSettingEnabled = snapshot2.child("nofityBudgetExceeded").val();
					var budgetThresholdNumber = Number(budgetThreshold);
					if (thresholdSettingEnabled && null != budgetThreshold && -1 != budgetThresholdNumber)
					{
						if (totalBudgetExpenses >= budgetThresholdNumber)
						{
						 	var messageTitle2 = "Budget " + budgetName + " has gone over threshold";
						 	var messageBody2 = "Threshold is: " + budgetThresholdNumber + " while the budget's sum of expenses is: " + totalBudgetExpenses  + " for: " + (currentExpenseDate.getMonth() + 1) + "/"+(currentExpenseDate.getYear() - 100);
						 	sendNotification(luid, messageTitle2, messageBody2, bid);
						}
					}	
				}
			});
		}
	});
});	
