package pers.jay.wanandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jess.arms.utils.ArmsUtils;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.mvp.ui.activity.MainActivity;
import pers.jay.wanandroid.mvp.ui.activity.WebActivity;
import pers.jay.wanandroid.mvp.ui.activity.X5WebActivity;
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

    public static void switchToWebPageWithUrl(Context context, String url, String title) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(title)) {
            return;
        }
        Intent intent = new Intent(context, X5WebActivity.class);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TYPE, WebActivity.TYPE_URL);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_URL, url);
        intent.putExtra(Const.Key.KEY_WEB_PAGE_TITLE, title);
        ArmsUtils.startActivity(intent);
    }
}
