package pers.jay.wanandroid.mvp.contract;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;

import java.util.function.DoubleUnaryOperator;

import io.reactivex.Observable;
import pers.jay.wanandroid.common.ICollectView;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.ShareUserArticles;
import pers.jay.wanandroid.result.WanAndroidResponse;

public interface MySharesContract {

    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends ICollectView {

        void showData(ArticleInfo articleInfo);

        void deleteSuccess(int position);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {

        Observable<WanAndroidResponse<ShareUserArticles>> getMyShares(int page);

        Observable<WanAndroidResponse> deleteShare(int id);
    }
}
