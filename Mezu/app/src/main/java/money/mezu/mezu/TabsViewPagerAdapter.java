package money.mezu.mezu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsViewPagerAdapter extends FragmentStatePagerAdapter {

    private boolean mIsRtl;
    private ExpensesTabFragment mExpenseTabFragment;

    public TabsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setupTabsFragments (boolean isRtl, ExpensesTabFragment expenseTabFragment) {
        mIsRtl = isRtl;
        this.mExpenseTabFragment = expenseTabFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if (mIsRtl) {
            position = 2 - position;
        }
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