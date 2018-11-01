package com.kehua.energy.monitor.app.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import me.walten.fastgo.base.fragment.SimpleFragment;

public class CommonViewPagerAdapter extends FragmentPagerAdapter {
    private List<String> titleList;
    private List<SimpleFragment> fragmentList;

    public CommonViewPagerAdapter(FragmentManager fm,
                                  List<String> titleList,
                                  List<SimpleFragment> fragmentList) {
        super(fm);
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }

    @Override
    public SimpleFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
