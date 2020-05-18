package pers.jay.wanandroid.utils;

import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;
import com.scwang.smartrefresh.header.StoreHouseHeader;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import pers.jay.wanandroid.common.AppConfig;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.zjc.commonlibs.util.ThreadUtils;
import timber.log.Timber;

public final class PoemUtils {

    public static void setHeaderWords(RefreshHeader header) {
        if (header == null) {
            return;
        }
        if (ThreadUtils.isMainThread()) {
            Timber.e("同步方法，不能在主线程调用");
            return;
        }
        String poem = AppConfig.getInstance().getPoem();
        if (header instanceof StoreHouseHeader) {
            ((StoreHouseHeader)header).initWithString("WANANDROID");
        }
        else if (header instanceof ClassicsHeader) {
            ((ClassicsHeader)header).setLastUpdateText(poem);
        }

    }

    public static void getPoemAsync(RefreshLayout refreshLayout) {
        if (refreshLayout == null) {
            return;
        }
        new JinrishiciClient().getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                if (poetySentence.getData() != null) {
                    String content = poetySentence.getData().getContent();
                    Timber.e("请求成功，诗句是%s", content);
                    AppConfig.getInstance().setPoem(format(content));
                    setHeaderWords(refreshLayout.getRefreshHeader());
                }
            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                AppConfig.getInstance().setPoem("");
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
        AppConfig.getInstance().setPoem(format(poem));
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

}
