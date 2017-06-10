package money.mezu.mezu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.Charset;
import java.math.BigInteger;

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

public class FirebaseBackend {
    private DatabaseReference mDatabase;
    private static FirebaseBackend mInstance = null;
    private static HashSet<Pair<String, ValueEventListener>> mPathsIListenTo = new HashSet<Pair<String, ValueEventListener>>();
    private FirebaseBackend() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    //************************************************************************************************************************************************
    public static FirebaseBackend getInstance()
    {
        if (mInstance == null)
            mInstance = new FirebaseBackend();

        return mInstance;
    }
    //************************************************************************************************************************************************
    public void startListeningForAllUserBudgetUpdates(UserIdentifier uid)
    {
        Log.d("","FirebaseBackend:registerForBudgetUpdates: start");
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
                    EventDispatcher.getInstance().notifyLocalCacheReady();
                    return;
                }
                else
                {
                    BudgetsDownloadedNotifier.handleIfFirstExecution(budgets.keySet());
                }
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: budgets have changed:%s", budgets.toString()));
                for(String key : budgets.keySet())
                {
                    boolean pathFound = false;
                    for (Pair<String, ValueEventListener> currentPair: mPathsIListenTo)
                    {
                        if (currentPair.first.equals("budgets/" + key + "/budget"))
                        {
                            pathFound = true;
                            break;
                        }
                    }
                    if (!pathFound)
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
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: budget has changed: hip hip horay got the following z: %s", dataSnapshot.toString()));
                Budget newBudget = new Budget((HashMap<String, Object>)dataSnapshot.getValue());
                Log.d("",String.format("FirebaseBackend:registerForBudgetUpdates: deserialized budget is: %s", newBudget.toString()));
                EventDispatcher.getInstance().notifyBudgetUpdatedListeners(newBudget);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mPathsIListenTo.add(Pair.create("budgets/" + bid + "/budget", listener));
    }
    //************************************************************************************************************************************************
    public void leaveBudget(String bid, UserIdentifier uid) {
        final String bidToLeave = bid;
        final String uidToUpdate = uid.getId().toString();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        stopListeningOnPath("budgets/" + bid + "/budget");
        EventDispatcher.getInstance().notifyUserLeftBudgetListeners(bid);
        DatabaseReference ref = database.getReference("budgets/" + bid + "/users");
        final ValueEventListener newListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> uidDict = (HashMap<String, Object>) dataSnapshot.getValue();
                mDatabase.child("users").child(uidToUpdate).child("budgets").child(bidToLeave).removeValue();
                mDatabase.child("budgets").child(bidToLeave).child("users").child(uidToUpdate).removeValue();
                // second condition verifies that we were a member of the budget to begin with.
                if (1 == uidDict.size() && uidDict.containsKey(uidToUpdate)) {
                    mDatabase.child("budgets").child(bidToLeave).removeValue();
                }
                stopListeningOnPath("budgets/" + bidToLeave + "/users");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mPathsIListenTo.add(new Pair<String, ValueEventListener>("budgets/" + bidToLeave + "/users", newListener));
    }
    //************************************************************************************************************************************************
    public void editBudget(Budget budget)
    {
        final Budget budgetToEdit = budget;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("budgets/" + budget.getId() + "/budget");
        // TODO - maybe take care of listeners hash map...
        mDatabase.child("budgets").child(budgetToEdit.getId()).child("budget").
                setValue(budgetToEdit.serializeNoExpenses());
        for (Expense expense : budgetToEdit.getExpenses()) {
            mDatabase.child("budgets").child(budgetToEdit.getId()).child("budget").child("mExpenses").
                    child(expense.getId()).setValue(expense.serialize());
        }
    }
    //************************************************************************************************************************************************
    public void editBudgetUsers(String bid, List<UserIdentifier> uids)
    {
        final String bidToEdit = bid;
        final List<UserIdentifier> uidsToEdit = uids;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("budgets/" + bid);
        // TODO - maybe take care of listeners hash map...
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                DatabaseReference ref2 = mDatabase.child("budgets").child(bidToEdit).child("users");
                ref2.addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        HashMap<String, Object> uidDict =(HashMap<String, Object>)dataSnapshot.getValue();
                        for (String uidAsString : uidDict.keySet()) {
                            mDatabase.child("users").child(uidAsString).child("budgets").child(bidToEdit).removeValue();
                            mDatabase.child("budgets").child(bidToEdit).child("users").child(uidAsString).removeValue();
                        }
                        for (UserIdentifier uid : uidsToEdit) {
                            String uidAsString = uid.getId().toString();
                            mDatabase.child("users").child(uidAsString).child("budgets").child(bidToEdit).setValue(bidToEdit);
                            mDatabase.child("budgets").child(bidToEdit).child("users").child(uidAsString).setValue(uidAsString);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
    //************************************************************************************************************************************************
    public void deleteExpense(String bid, String eid)
    {
        mDatabase.child("budgets").child(bid).child("budget").child("mExpenses").child(eid).removeValue();
    }
    //************************************************************************************************************************************************
    public void editExpense(String bid, Expense expense)
    {
        HashMap<String, Object> serializedExpense = expense.serialize();
        mDatabase.child("budgets").child(bid).child("budget").child("mExpenses").
                child(expense.getId()).setValue(serializedExpense);
    }
    //************************************************************************************************************************************************
    public void createBudgetAndAddToUser(Budget budget, UserIdentifier uid)
    {
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
    public void stopListeningOnAllPaths()
    {
        Log.d("", "FirebaseBackend:stopListeningOnEvents: stopping");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (Pair<String,ValueEventListener > pathListener: mPathsIListenTo)
        {
            DatabaseReference ref = database.getReference(pathListener.first);
            Log.d("", String.format("FirebaseBackend:stopListeningOnEvents: will not listen on:%s",  pathListener.first));
            ref.removeEventListener(pathListener.second);
        }
        mPathsIListenTo = new HashSet<Pair<String, ValueEventListener>>();
    }
    //************************************************************************************************************************************************
    public void stopListeningOnPath(String path)
    {
        HashSet<Pair<String, ValueEventListener>> pairsToDelete = new HashSet<Pair<String, ValueEventListener>>();
        for (Pair<String, ValueEventListener> currentPair: mPathsIListenTo)
        {
            if (currentPair.first.equals(path))
            {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(currentPair.first);
                Log.d("", String.format("FirebaseBackend:stopListeningOnEvents: will not listen on:%s",  currentPair.first));
                ref.removeEventListener(currentPair.second);
                pairsToDelete.add(currentPair);
            }
        }
        for (Pair<String, ValueEventListener> pairToDelete : pairsToDelete)
        {
            mPathsIListenTo.remove(pairToDelete);
        }
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
                    mDatabase.child("users").child(uidToAdd).child("username").setValue(hash(usernameToAdd));
                    mDatabase.child("users").child(uidToAdd).child("email").setValue(hash(emailToAdd));
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
        final String emailHash = hash(email);
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
                            if (dataSnapshot.getValue() != null && (dataSnapshot.getValue()).equals(emailHash)) {
                                Log.d("", String.format("FirebaseBackend:connectBudgetAndUserByEmail: value: %s, address: %s", dataSnapshot.getValue(), emailHash));
                                connectBudgetAndUser(bid, uidAsString);
                            }
                            else {
                                Log.d("", String.format("FirebaseBackend:connectBudgetAndUserByEmail: uid is: %s, value: is null, address: %s", uidAsString, emailHash));
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
    //************************************************************************************************************************************************
    private static String hash(String s)
    {
        //return md5(s);
        return s;
    }

    private static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
}
