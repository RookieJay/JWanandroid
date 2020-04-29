package pers.jay.wanandroid.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import io.reactivex.Observable;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.result.WanAndroidResponse;

public interface TabContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void showData(ArticleInfo data);

        void updateCollectStatus(boolean isCollect, Article item, android.view.View view,
                                 int position);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse<ArticleInfo>> getKnowledgeArticles(int childId, int page);

        Observable<WanAndroidResponse> collect(int id);

        Observable<WanAndroidResponse> unCollect(int id);

        Observable<WanAndroidResponse<ArticleInfo>> getWxArticles(int cid, int page);

        Observable<WanAndroidResponse<ArticleInfo>> getProjectArticles(int page, int childId);
    }
}
