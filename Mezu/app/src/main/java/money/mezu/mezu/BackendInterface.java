package money.mezu.mezu;
import java.util.List;

/**
 * Created by asafb on 4/14/2017.
 */

// This Interface will be implemented by the Back-end team
// (may change overtime upon the needs)

public interface BackendInterface {
    // TODO: modify according to the needs

    void registerForAllUserBudgetUpdates(final BudgetsActivity activity, UserIdentifier uid);
    void deleteBudget(String bid);
    void createBudgetAndAddToUser(Budget budget, UserIdentifier uid);
    void addUserToBudget(String bid, String uid);
    void addExpenseToBudget(Budget budget, Expense expense);
    void addUserIfNeeded(UserIdentifier uid, String username, String email);
    void connectBudgetAndUserByEmail(Budget budget, String email);
}
