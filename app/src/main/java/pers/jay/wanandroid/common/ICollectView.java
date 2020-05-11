package pers.jay.wanandroid.common;

import com.jess.arms.mvp.IView;

import pers.jay.wanandroid.model.Article;

/**
 * 需要实现收藏功能的界面实现此接口
 */
public interface ICollectView extends IView {

    /**
     * 收藏成功 使用第三方库时无需回调，立即展示点击动画
     */
    void onCollectSuccess(Article article, int position);

    /**
     * 收藏失败
     */
    void onCollectFail(Article article, int position);
}
