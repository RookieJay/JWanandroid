package pers.jay.wanandroid.model.dao;

import org.greenrobot.greendao.AbstractDao;

import pers.jay.wanandroid.model.Article;

public class ArticleManager extends BaseBeanManager<Article, Long> {

    public ArticleManager(AbstractDao dao) {
        super(dao);
    }
}
