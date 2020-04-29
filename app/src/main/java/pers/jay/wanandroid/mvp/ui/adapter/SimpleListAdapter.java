package pers.jay.wanandroid.mvp.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import pers.jay.wanandroid.R;

public class SimpleListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SimpleListAdapter(@Nullable List<String> data) {
        super(R.layout.base_popup_item_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.tvItem, item);
    }
}
