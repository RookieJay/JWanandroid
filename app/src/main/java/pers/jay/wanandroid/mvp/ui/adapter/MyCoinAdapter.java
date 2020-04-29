package pers.jay.wanandroid.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.model.CoinHistory;

public class MyCoinAdapter extends BaseQuickAdapter<CoinHistory, BaseViewHolder> {

    public MyCoinAdapter(int layoutResId, @Nullable List<CoinHistory> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CoinHistory item) {
        helper.setText(R.id.tvReason, item.getReason())
              .setText(R.id.tvDesc, item.getDesc())
              .setText(R.id.tvCoinCount, String.format("+%s", item.getCoinCount()));
    }
}
