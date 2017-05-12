package money.mezu.mezu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsViewPagerAdapter extends FragmentStatePagerAdapter {

    private ExpensesTabFragment mExpenseTabFragment;

    public void setupTabsFragments (ExpensesTabFragment expenseTabFragment) {
        this.mExpenseTabFragment = expenseTabFragment;
    }

    public TabsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mExpenseTabFragment;
            case 1:
                return new Tab2_stub();
            case 2:
                return new Tab3_stub();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}