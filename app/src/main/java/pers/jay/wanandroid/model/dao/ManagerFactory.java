package pers.jay.wanandroid.model.dao;

import android.content.Context;

import pers.jay.wanandroid.common.JApplication;

public class ManagerFactory {

    private final Context context = JApplication.getInstance();
    /**
     * 每一个BeanManager都管理着数据库中的一个表，我将这些管理者在ManagerFactory中进行统一管理
     */
    ArticleManager articleManager;

    private static ManagerFactory mInstance = null;

    /**
     * 获取DaoFactory的实例
     */
    public static ManagerFactory getInstance() {
        if (mInstance == null) {
            synchronized (ManagerFactory.class) {
                if (mInstance == null) {
                    mInstance = new ManagerFactory();
                }
            }
        }
        return mInstance;
    }

    public synchronized ArticleManager getArticleManager() {
        if (articleManager == null) {
            articleManager = new ArticleManager(
                    DaoManager.getInstance(context).getDaoSession().getArticleDao());
        }
        return articleManager;
    }
}

