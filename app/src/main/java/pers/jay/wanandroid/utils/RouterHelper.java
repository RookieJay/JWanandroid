package pers.jay.wanandroid.utils;

import android.os.Bundle;

import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.fragment.UserFragment;
import pers.zjc.commonlibs.util.StringUtils;

/**
 * 跳转辅助类
 */
public class RouterHelper {

    public static void switchToUserPage(MainActivity activity, Article article) {
        if (activity == null) {
            throw new IllegalArgumentException("param activity can not be null.");
        }
        if (article == null) {
            throw new IllegalArgumentException("param article can not be null.");
        }
        UserFragment userFragment = UserFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong(Const.Key.KEY_USER_ID, article.getUserId());
        bundle.putString(Const.Key.KEY_TITLE, StringUtils.isEmpty(
                article.getAuthor()) ? article.getShareUser() : article.getAuthor());
        userFragment.setArguments(bundle);
        activity.switchFragment(userFragment);
    }
}
