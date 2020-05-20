package pers.jay.wanandroid.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.PoetySentence;
import com.jinrishici.sdk.android.view.JinrishiciTextViewConfig;

import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.JApplication;

public class PoemTextView extends android.support.v7.widget.AppCompatTextView {

    private JinrishiciTextViewConfig config = new JinrishiciTextViewConfig();
    private PoemTextView.DataFormatListener dataFormatListener = null;//格式化方法
    private PoetySentence nowPoetySentence = null;//现在正在展示的诗词数据的对象

    public PoemTextView(Context context) {
        this(context, null);
    }

    public PoemTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PoemTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setTypeface(genTypeface());
        initAttrs(context, attrs, defStyleAttr);
        if (!JinrishiciFactory.isInit())
            JinrishiciFactory.init(context);
        request();
    }

    /**
     * 获取自定义字体
     * @return
     */
    private Typeface genTypeface() {
        return Typeface.createFromAsset(JApplication.getInstance().getAssets(), "fonts/FZZJ-ZHZHXKJW.TTF");
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JinrishiciTextView, defStyleAttr, 0);
        if (typedArray.hasValue(R.styleable.JinrishiciTextView_jrsc_refresh_on_click))
            config.isRefreshWhenClick = typedArray.getBoolean(R.styleable.JinrishiciTextView_jrsc_refresh_on_click, config.isRefreshWhenClick);
        if (typedArray.hasValue(R.styleable.JinrishiciTextView_jrsc_show_error))
            config.isShowErrorOnTextView = typedArray.getBoolean(R.styleable.JinrishiciTextView_jrsc_show_error, config.isShowErrorOnTextView);
        if (typedArray.hasValue(R.styleable.JinrishiciTextView_jrsc_show_loading_text))
            config.isShowLoadingText = typedArray.getBoolean(R.styleable.JinrishiciTextView_jrsc_show_loading_text, config.isShowLoadingText);
        if (typedArray.hasValue(R.styleable.JinrishiciTextView_jrsc_text_loading))
            config.loadingText = typedArray.getString(R.styleable.JinrishiciTextView_jrsc_text_loading);
        if (typedArray.hasValue(R.styleable.JinrishiciTextView_jrsc_text_error))
            config.customErrorText = typedArray.getString(R.styleable.JinrishiciTextView_jrsc_text_error);
        typedArray.recycle();
        initConfig();
    }

    private void initConfig() {
        if (config.isRefreshWhenClick)
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    request();
                }
            });
        else
            setOnClickListener(null);
    }

    public void setConfig(JinrishiciTextViewConfig config) {
        this.config.copy(config);
        initConfig();
    }

    /**
     * 发起请求
     * 异步方法，请求成功后将诗词数据显示到TextView上
     */
    private void request() {
        if (config.isShowLoadingText)
            setText(config.loadingText);
        new JinrishiciClient().getOneSentenceBackground(new JinrishiciCallback() {
            @Override
            public void done(PoetySentence poetySentence) {
                setText(formatPoetySentence(poetySentence));
            }

            @Override
            public void error(JinrishiciRuntimeException e) {
                setText(config.isShowErrorOnTextView ? e.getMessage() : config.customErrorText);
            }
        });
    }

    public void setDataFormat(PoemTextView.DataFormatListener listener) {
        this.dataFormatListener = listener;//存储格式化的操作，便于在设置的时候回调
        formatPoetySentence(nowPoetySentence);//刷新当前已有的数据
    }

    public interface DataFormatListener {
        String set(PoetySentence poetySentence);
    }

    private String formatPoetySentence(PoetySentence poetySentence) {
        if (poetySentence == null)
            //如果存储数据为空，第一次请求还未完成，这个时候跳过这次刷新数据的请求，
            // TextView显示的文本交由开发者定义
            return config.loadingText;
        nowPoetySentence = poetySentence;//存储
        if (dataFormatListener == null)//如果没有设置格式化操作，则生成默认的操作————只显示诗词内容
            dataFormatListener = new PoemTextView.DataFormatListener() {
                @Override
                public String set(PoetySentence poetySentence) {
                    return poetySentence.getData().getContent();
                }
            };
        return dataFormatListener.set(poetySentence);
    }
}
