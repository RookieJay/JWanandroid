package pers.jay.wanandroid.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.mvp.BasePresenter;

import pers.jay.wanandroid.R;

public abstract class BaseTabActivity<P extends BasePresenter> extends BaseActivity<P> {

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_base_tab;
    }

}
