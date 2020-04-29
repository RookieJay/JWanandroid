package pers.jay.wanandroid.mvp.ui.adapter;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.model.Coin;

public class RankAdapter extends BaseQuickAdapter<Coin, BaseViewHolder> {

    public RankAdapter(int layoutResId, @Nullable List<Coin> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Coin item) {
        helper.setText(R.id.tvRank, item.getRealRank())
              .setText(R.id.tvUser, item.getUsername())
              .setText(R.id.tvScore, String.valueOf(item.getCoinCount()));
        int position = helper.getAdapterPosition();
        setFirstThreeImg(helper, position);
    }

    private void setFirstThreeImg(BaseViewHolder helper, int position) {
        ImageView ivRank = helper.itemView.findViewById(R.id.ivRank);
        switch (position) {
            case 0:
                helper.setImageDrawable(R.id.ivRank, mContext.getDrawable(R.drawable.ic_first));
                helper.setGone(R.id.tvRank, false).setVisible(R.id.ivRank, true);
                ivRank.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.yellow)));
                break;
            case 1:
                helper.setImageDrawable(R.id.ivRank, mContext.getDrawable(R.drawable.ic_second));
                helper.setGone(R.id.tvRank, false).setVisible(R.id.ivRank, true);
                ivRank.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.orange_red)));
                break;
            case 2:
                helper.setImageDrawable(R.id.ivRank, mContext.getDrawable(R.drawable.ic_third));
                helper.setGone(R.id.tvRank, false).setVisible(R.id.ivRank, true);
                ivRank.setImageTintList(ColorStateList.valueOf(mContext.getColor(R.color.blue_light)));
                break;
            default:
                helper.setGone(R.id.ivRank, false).setVisible(R.id.tvRank, true);
                break;
        }
    }

}
