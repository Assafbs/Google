package money.mezu.mezu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabsViewPagerAdapter extends FragmentStatePagerAdapter {

    private boolean mIsRtl;
    private ExpensesTabFragment mExpensesTabFragment;
    private /*GraphsTabFragment*/ Fragment mGraphsTabFragment;
    private FragmentManager mFragmentManager;

    public TabsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
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

    public void onSwitchToGraph(GraphFragment graphFragment) {
        mFragmentManager.beginTransaction().remove(mGraphsTabFragment).commit();
        mGraphsTabFragment = graphFragment;
        notifyDataSetChanged();
    }

    public void onSwitchFromGraph(GraphsTabFragment graphsTabFragment) {
        mFragmentManager.beginTransaction().remove(mGraphsTabFragment).commit();
        mGraphsTabFragment = graphsTabFragment;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)
    {
        if (object instanceof GraphsTabFragment && mGraphsTabFragment instanceof GraphFragment)
            return POSITION_NONE;
        if (object instanceof GraphFragment && mGraphsTabFragment instanceof GraphsTabFragment)
            return POSITION_NONE;
        return POSITION_UNCHANGED;
    }

}