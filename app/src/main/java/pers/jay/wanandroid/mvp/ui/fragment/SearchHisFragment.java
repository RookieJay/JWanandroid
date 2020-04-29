package pers.jay.wanandroid.mvp.ui.fragment;

import com.google.android.flexbox.FlexboxLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.SearchHelper;
import pers.jay.wanandroid.di.component.DaggerSearchComponent;
import pers.jay.wanandroid.model.HotKey;
import pers.jay.wanandroid.mvp.contract.SearchContract;
import pers.jay.wanandroid.mvp.presenter.SearchPresenter;
import pers.jay.wanandroid.mvp.ui.activity.SearchActivity;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class SearchHisFragment extends BaseFragment<SearchPresenter> implements SearchContract.View {

    @BindView(R.id.tvSearchHot)
    TextView tvSearchHot;
    @BindView(R.id.tvSearchHis)
    TextView tvSearchHis;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rvHot)
    RecyclerView rvHot;
    @BindView(R.id.rvHis)
    RecyclerView rvHis;

    BaseQuickAdapter<HotKey, BaseViewHolder> hotKeyAdapter;
    BaseQuickAdapter<String, BaseViewHolder> hisAdapter;
    @BindView(R.id.ivClear)
    ImageView ivClear;
    @BindView(R.id.flHis)
    FrameLayout flHis;

    private SearchActivity activity;
    private SearchHelper searchHelper;

    public static SearchHisFragment newInstance() {
        return new SearchHisFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchActivity) {
            this.activity = (SearchActivity)context;
        }
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerSearchComponent //如找不到该类,请编译一下项目
                              .builder().appComponent(appComponent).view(this).build().inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        searchHelper = SearchHelper.getInstance();
        setupView();
        initHistory();
        mPresenter.loadHotKeys();
    }

    private void setupView() {
        rvHot.setHasFixedSize(true);
        rvHot.setLayoutManager(new FlexboxLayoutManager(mContext));
        hotKeyAdapter = new BaseQuickAdapter<HotKey, BaseViewHolder>(
                R.layout.item_knowledge_child) {
            @Override
            protected void convert(@NonNull BaseViewHolder helper, HotKey item) {
                helper.setText(R.id.tvTreeChild, item.getName());
                helper.itemView.setOnClickListener(v -> search(item.getName()));
            }
        };
        rvHot.setAdapter(hotKeyAdapter);
        rvHis.setHasFixedSize(true);
        rvHis.setLayoutManager(new FlexboxLayoutManager(mContext));
        hisAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_search_history) {

            private boolean showDel;

            @Override
            protected void convert(@NonNull BaseViewHolder helper, String item) {
                helper.setText(R.id.tvSearchHis, item);
                helper.itemView.setOnClickListener(v -> search(item));
                helper.itemView.findViewById(R.id.tvSearchHis)
                               .setOnClickListener(v -> search(item));
                ImageView ivDel = helper.itemView.findViewById(R.id.ivDel);
                if (showDel) {
                    ivDel.startAnimation(AnimationUtils.loadAnimation(mContext,
                            R.anim.anim_scale_from_center_out));
                }
                else {
                    ivDel.startAnimation(
                            AnimationUtils.loadAnimation(mContext, R.anim.anim_scale_to_center_in));
                }
                ivDel.setVisibility(showDel ? View.VISIBLE : View.GONE);
                helper.itemView.setOnLongClickListener(v -> {
                    ivDel.setVisibility(showDel ? View.VISIBLE : View.GONE);
                    showDel = !showDel;
                    notifyDataSetChanged();
                    return true;
                });
                helper.itemView.findViewById(R.id.tvSearchHis).setOnLongClickListener(v -> {
                    showDel = !showDel;
                    notifyDataSetChanged();
                    return true;
                });
                ivDel.setOnClickListener(v -> {
                    remove(helper.getLayoutPosition());
                    removeSearchKey(item);
                });
            }
        };
        rvHis.setAdapter(hisAdapter);
        ivClear.setOnClickListener(v -> {
            UIUtils.createConfirmDialog(mContext, mContext.getResources().getString(R.string.dialog_clear_history), true,
                    (dialog, which) -> {
                        dialog.dismiss();
                        clearAllHis();
                    }, null).show();
//            AnyLayer.dialog(mContext)
//                    .contentView(R.layout.dk_dialog_common)
//                    .cancelableOnTouchOutside(true)
//                    .gravity(Gravity.CENTER)
//                    .dragDismiss(DragLayout.DragStyle.Bottom)
//                    .contentAnimator(new Layer.AnimatorCreator() {
        });


    }

    private void clearAllHis() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1F, 0F);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flHis.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        alphaAnimation.setDuration(300L);
        flHis.startAnimation(alphaAnimation);
        hisAdapter.setNewData(new ArrayList<>());
        searchHelper.clear();
    }

    private void removeSearchKey(String item) {
        searchHelper.removeSearchKey(item);
    }

    private void initHistory() {
        List<String> his = searchHelper.getSearchKeys();
        if (his == null || his.isEmpty()) {
            flHis.setVisibility(View.GONE);
            return;
        }
        hisAdapter.addData(his);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        // 由于解绑时机发生在onComplete()之后，容易引起空指针
        if (progressBar == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {

    }

    private void search(String key) {
        if (flHis.getVisibility() != View.VISIBLE) {
            flHis.setVisibility(View.VISIBLE);
        }
        activity.doSearch(key);
        updateHisData(key);
    }

    private void updateHisData(String key) {
        List<String> data = hisAdapter.getData();
        data.remove(key);
        data.add(0, key);
        hisAdapter.setNewData(data);
        flHis.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showHotKeys(List<HotKey> data) {
        hotKeyAdapter.setNewData(data);
    }

    @Override
    public void showEmpty() {
        showMessage("暂无数据");
    }

    public void addHistory(String key) {
        updateHisData(key);
    }



}
