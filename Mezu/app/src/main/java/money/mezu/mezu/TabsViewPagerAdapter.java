package money.mezu.mezu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsViewPagerAdapter extends FragmentStatePagerAdapter {

    private boolean mIsRtl;
    private ExpensesTabFragment mExpensesTabFragment;
    private GraphsTabFragment mGraphsTabFragment;

    public TabsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setupTabsFragments (boolean isRtl, ExpensesTabFragment expenseTabFragment, GraphsTabFragment graphsTabFragment) {
        mIsRtl = isRtl;
        this.mExpensesTabFragment = expenseTabFragment;
        this.mGraphsTabFragment = graphsTabFragment;
    }


    @Override
    public Fragment getItem(int position) {
        if (mIsRtl) {
            position = 2 - position;
        }
        switch (position) {
            case 0:
                return mExpensesTabFragment;
            case 1:
                return mGraphsTabFragment;
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