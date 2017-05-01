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
    public static UserIdentifier mUid;
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
    // TODO: make a better way for the uid to be avalable for everyone and
    // don't cuple the FirebaseBackend with a single uid.
    public void setUid(UserIdentifier uid)
    {
        mUid = uid;
    }
    //************************************************************************************************************************************************
    public void registerForAllUserBudgetUpdates(BudgetsActivity budgetsActivity) {
        FirebaseBackend.mBudgetsActivity = budgetsActivity;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/" + mUid.getId().toString() + "/budgets");

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
        mPathsIListenTo.add(Pair.create("users/" + mUid.getId().toString() + "/budgets", listener));
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
//    public List<Expense> getExpensesOfBudget(BudgetIdentifier bid) {
//        final String[][] eids = new String[1][];
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("budgets/" + bid.getId().toString() + "/expenses");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                eids[0] = (String[]) dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });
//
//        final List<Expense> expenses = new ArrayList<Expense>();
//        for (String eid : eids[0]) {
//            DatabaseReference ref2 = mDatabase.child("expenses").child(eid);
//            ref2.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    expenses.add((Expense)dataSnapshot.getValue());
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                }
//            });
//        }
//        return expenses;
//    }
    //************************************************************************************************************************************************
    public void deleteBudget(BudgetIdentifier bid) {
        mDatabase.child("budgets").child(bid.getId().toString()).removeValue();
        // TODO - go all over users and remove budgets from there?
    }
    //************************************************************************************************************************************************
    public void createBudgetAndAddToUser(Budget budget) {
        Log.d("","FirebaseBackend:addBudgetToUser: adding budget to user");
        String newBid = createBudget(budget);
        addBudgetToUser(newBid);
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
    private void addBudgetToUser(String bid)
    {
        Log.d("",String.format("FirebaseBackend:addBudgetToUser: adding budget with id: %s", bid));
        mDatabase.child("users").child(mUid.getId().toString()).child("budgets").child(bid).setValue(bid);
        Log.d("", "FirebaseBackend:addBudgetToUser: added budget");
    }

    //************************************************************************************************************************************************
    public void addUserToBudget(Budget budget) {

        mDatabase.child("users").push().setValue(mUid.getId().toString());
        mDatabase.child("budgets").child(budget.getId().toString()).child("users").push().setValue(mUid.getId().toString());
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
}
