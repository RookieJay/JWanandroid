package pers.jay.wanandroid.utils;

import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.widgets.PoemHeader;
import pers.zjc.commonlibs.util.ThreadUtils;
import timber.log.Timber;

public final class PoemUtils {

    public static void getPoemAsync(IPoemResult result) {
        if (result == null) {
            return;
        }
        new JinrishiciClient().getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                if (poetySentence.getData() != null) {
                    String content = poetySentence.getData().getContent();
                    Timber.e("异步请求成功，诗句是%s", content);
                    AppConfig.getInstance().setPoem(content);
                    result.onSuccess();
                }
            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                AppConfig.getInstance().setPoem("");
                result.onFail();
            }
        });
    }

    /**
     * 同步获取 需要在子线程
     */
    public static void getPoemSync() {

        if (ThreadUtils.isMainThread()) {
            Timber.e("同步方法，不能在主线程调用");
            return;
        }

        PoetySentence sentence = new JinrishiciClient().getOneSentence(
                NetWorkManager.getInstance().getOkHttpBuilder());
        PoetySentence.DataBean data = sentence.getData();
        if (data == null) {
            return;
        }
        String poem = data.getContent();
        Timber.e("同步请求成功，诗句是%s", poem);
        AppConfig.getInstance().setPoem(poem);
    }

    /**
     * 去标点
     * @param s
     * @return
     */
    public static String format(String s) {
        return s.replaceAll(
                "[`qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]", "");
    }

    public interface IPoemResult {

        void onSuccess();

        default void onFail(){};

    }

}
