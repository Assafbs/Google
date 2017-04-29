package money.mezu.mezu;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Or on 4/27/2017.
 */

public class FirebaseBackend implements BackendInterface {
    private DatabaseReference mDatabase;
    private static boolean mInitialized;
    private static FirebaseBackend mInstance;

    private FirebaseBackend() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseBackend getInstance() {
        if (!mInitialized) {
            mInstance = new FirebaseBackend();
            mInitialized = true;
        }
        return mInstance;
    }

    public ArrayList<Budget> getUsersBudgets(UserIdentifier uid) {
        final String[][] bids = new String[1][];
        DatabaseReference ref = mDatabase.child("users").child(uid.toString()).child("budgets");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bids[0] = (String[]) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        final ArrayList<Budget> budgets = new ArrayList<Budget>();
        for (String bid : bids[0]) {
            DatabaseReference ref2 = mDatabase.child("expenses").child(bid);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    budgets.add((Budget)dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
        return budgets;
    }

    public List<Expense> getExpensesOfBudget(BudgetIdentifier bid) {
        final String[][] eids = new String[1][];
        DatabaseReference ref = mDatabase.child("budgets").child(bid.toString()).child("expenses");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eids[0] = (String[]) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        final List<Expense> expenses = new ArrayList<Expense>();
        for (String eid : eids[0]) {
            DatabaseReference ref2 = mDatabase.child("expenses").child(eid);
            ref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    expenses.add((Expense)dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
        return expenses;
    }

    public void deleteBudget(BudgetIdentifier bid) {
        mDatabase.child("budgets").child(bid.toString()).removeValue();
        // TODO - go all over users and remove budgets from there?
    }

    public void addBudgetToUser(UserIdentifier uid, Budget budget) {
        String bid = budget.getId().toString();
        mDatabase.child("budgets").child(bid).child("budget").setValue(budget);
        mDatabase.child("users").child(uid.toString()).child("budgets").push().setValue(bid);
    }

    public void addUserToBudget(Budget budget, UserIdentifier uid) {
        mDatabase.child("users").push().setValue(uid);
        mDatabase.child("budgets").child(budget.getId().toString()).child("users").push().setValue(uid);
    }

    public void addExpenseToBudget(Budget budget, Expense expense) {
        String eid = expense.getId().toString();
        mDatabase.child("expenses").child(eid).child("expense").setValue(expense);
        mDatabase.child("budgets").child(budget.getId().toString()).child("expenses").push().setValue(eid);
    }
}
