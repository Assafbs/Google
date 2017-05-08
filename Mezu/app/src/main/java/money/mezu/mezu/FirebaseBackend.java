package money.mezu.mezu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;
import android.util.Pair;

/**
 * Created by Or on 4/27/2017.
 */

public class FirebaseBackend implements BackendInterface {
    private DatabaseReference mDatabase;
    private static boolean mInitialized;
    private static FirebaseBackend mInstance;
    private static BudgetsActivity mBudgetsActivity;
    private static HashSet<Pair<String, ValueEventListener>> mPathsIListenTo = new HashSet<Pair<String, ValueEventListener>>();
    private FirebaseBackend() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    //************************************************************************************************************************************************
    public static FirebaseBackend getInstance() {
        if (!mInitialized) {
            mInstance = new FirebaseBackend();
            mInitialized = true;
        }
        return mInstance;
    }
    //************************************************************************************************************************************************
    public void registerForAllUserBudgetUpdates(BudgetsActivity budgetsActivity, UserIdentifier uid) {
        FirebaseBackend.mBudgetsActivity = budgetsActivity;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + uid.getId().toString() + "/budgets");

        ValueEventListener listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: budgets have changed:%s", dataSnapshot.toString()));
                HashMap<String,String> budgets = (HashMap<String,String>) dataSnapshot.getValue();
                if (null == budgets)
                {
                    return;
                }
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: budgets have changed:%s", budgets.toString()));
                for(String key : budgets.keySet())
                {
                    if (!mPathsIListenTo.contains("budgets/" + key + "/budget"))
                    {
                        registerForBudgetUpdates(key);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mPathsIListenTo.add(Pair.create("users/" + uid.getId().toString() + "/budgets", listener));
    }
    //************************************************************************************************************************************************
    private void registerForBudgetUpdates(String bid)
    {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("budgets/" + bid + "/budget");
        ValueEventListener listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: budget has changed: hip hip horay got the following shit: %s", dataSnapshot.toString()));
                Budget newBudget = new Budget((HashMap<String, Object>)dataSnapshot.getValue());
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: deserialized budget is: %s", newBudget.toString()));
                FirebaseBackend.mBudgetsActivity.updateBudgetsCallback(newBudget);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mPathsIListenTo.add(Pair.create("budgets/" + bid + "/budget", listener));
    }
    //************************************************************************************************************************************************
    public void deleteBudget(String bid) {
        final String bidToRemove = bid;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("budgets/" + bid + "/users");
        // TODO - maybe take care of listeners hash map...
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                HashMap<String, Object> uidDict =(HashMap<String, Object>)dataSnapshot.getValue();
                for (String uidAsString : uidDict.keySet()) {
                    mDatabase.child("users").child(uidAsString).child("budgets").child(bidToRemove).removeValue();
                }
                mDatabase.child("budgets").child(bidToRemove).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    //************************************************************************************************************************************************
    public void createBudgetAndAddToUser(Budget budget, UserIdentifier uid) {
        Log.d("","FirebaseBackend:addBudgetToUser: adding budget to user");
        String newBid = createBudget(budget);
        connectBudgetAndUser(newBid, uid.getId().toString());
    }
    //************************************************************************************************************************************************
    private String createBudget(Budget budget)
    {
        Log.d("","FirebaseBackend:createBudget: creating budget");
        DatabaseReference budgetRef = mDatabase.child("budgets").push();
        String bid = budgetRef.getKey();
        budget.setId(bid);
        budgetRef.child("budget").setValue(budget.serializeNoExpenses());
        Log.d("",String.format("FirebaseBackend:createBudget: created budget with id:%s", budget.getId().toString()));
        return bid;
    }
    //************************************************************************************************************************************************
    private void addBudgetToUser(String bid, String uid)
    {
        Log.d("",String.format("FirebaseBackend:addBudgetToUser: adding budget with id: %s", bid));
        mDatabase.child("users").child(uid).child("budgets").child(bid).setValue(bid);
        Log.d("", "FirebaseBackend:addBudgetToUser: added budget");
    }
    //************************************************************************************************************************************************
    public void addUserToBudget(String bid, String uid)
    {
        // TODO - make sure user entry always exists in DB in this stage
        //mDatabase.child("users").push().setValue(uid.getId().toString());
        mDatabase.child("budgets").child(bid).child("users").child(uid).setValue(uid);
    }
    //************************************************************************************************************************************************
    public void addExpenseToBudget(Budget budget, Expense expense)
    {
        DatabaseReference expenseRef = mDatabase.child("budgets").child(budget.getId()).child("budget").child("mExpenses").push();
        String eid = expenseRef.getKey();
        expense.setId(eid);
        HashMap<String, Object> serializedExpense = expense.serialize();
        expenseRef.setValue(serializedExpense);
    }
    //************************************************************************************************************************************************
    public void stopListeningOnEvents()
    {
        Log.d("", "FirebaseBackend:stopListeningOnEvents: stopping");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (Pair<String,ValueEventListener > pathListener: mPathsIListenTo)
        {
            DatabaseReference ref = database.getReference(pathListener.first);
            ref.removeEventListener(pathListener.second);
        }
        mPathsIListenTo = new HashSet<Pair<String, ValueEventListener>>();
    }
    //************************************************************************************************************************************************
    public void addUserIfNeeded(UserIdentifier uid, String username, String email)
    {
        final String uidToAdd = uid.getId().toString();
        final String usernameToAdd = username;
        final String emailToAdd = email;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild(uidToAdd)) {
                    mDatabase.child("users").child(uidToAdd).child("username").setValue(usernameToAdd);
                    mDatabase.child("users").child(uidToAdd).child("email").setValue(emailToAdd);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    //************************************************************************************************************************************************
    private void connectBudgetAndUser(String bid, String uid)
    {
        addBudgetToUser(bid, uid);
        addUserToBudget(bid, uid);
    }
    //************************************************************************************************************************************************
    public void connectBudgetAndUserByEmail(Budget budget, String email)
    {
        //TODO - maybe change DB representation in the future...
        final String innerEmail = email;
        final String bid = budget.getId();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                HashMap<String, Object> uidDict =(HashMap<String, Object>)dataSnapshot.getValue();
                for (final String uidAsString : uidDict.keySet()) {
                    DatabaseReference ref2 = database.getReference("users/" + uidAsString + "/email/");
                    if (ref2 == null)
                        continue;
                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null && (dataSnapshot.getValue()).equals(innerEmail)) {
                                Log.d("", String.format("FirebaseBackend:connectBudgetAndUserByEmail: value: %s, address: %s", dataSnapshot.getValue(), innerEmail));
                                connectBudgetAndUser(bid, uidAsString);
                            }
                            else {
                                Log.d("", String.format("FirebaseBackend:connectBudgetAndUserByEmail: uid is: %s, value: is null, address: %s", uidAsString, innerEmail));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
