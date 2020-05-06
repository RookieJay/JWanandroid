package pers.jay.wanandroid.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.model.BingDailyImage;
import pers.jay.wanandroid.result.WanAndroidResponse;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 08/01/2019 11:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
public interface HomeContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        void showMoreArticles(ArticleInfo articleInfo);

        void showBanner(List<BannerImg> bannerImgs);

        void refresh(List<Article> articleList);

        void updateCollectStatus(boolean isCollect, Article article, int position);

        void showLoadMoreFail();

        void restoreLikeButton(int position);

        void addDailyPic(String url);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse<ArticleInfo>> getArticle(int page);

        Observable<WanAndroidResponse<List<BannerImg>>> getBanner();

        Observable<WanAndroidResponse<List<Article>>> getTopArticles();

        Observable<WanAndroidResponse> collect(int id);

        Observable<WanAndroidResponse> unCollect(int id);

        Observable<ResponseBody> getBingImg();
    }
}
