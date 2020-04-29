package pers.jay.wanandroid.mvp.ui.adapter;

import com.google.android.flexbox.FlexboxLayout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.Navi;

public class NaviAdapter extends BaseQuickAdapter<Navi, BaseViewHolder> {

    private OnChildClickListener mOnChildClickListener;

    public NaviAdapter(int layoutResId, @Nullable List<Navi> data) {
        super(layoutResId, data);
    }

    public void setmOnChildClickListener(OnChildClickListener mOnChildClickListener) {
        this.mOnChildClickListener = mOnChildClickListener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Navi item) {
        helper.setText(R.id.treeTitle, item.getName());
        FlexboxLayout fbl = helper.getView(R.id.fbl);
        fbl.removeAllViews();
        List<Article> articles = item.getArticles();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            TextView childView = getChildTextView(fbl);
            childView.setText(article.getTitle());
            fbl.addView(childView);
            int position = i;
            childView.setOnClickListener(v -> {
                mOnChildClickListener.onItemChildClick(article);
            });
        }
        helper.itemView.setOnClickListener(v -> mOnChildClickListener.onItemClick(0, item));

    }

    private TextView getChildTextView(FlexboxLayout fbl) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(fbl.getContext());
        }
        return (TextView)mLayoutInflater.inflate(R.layout.item_knowledge_child, fbl, false);
    }

    public interface OnChildClickListener {

        void onItemClick(int position, Navi data);

        void onItemChildClick(Article data);
    }

}
