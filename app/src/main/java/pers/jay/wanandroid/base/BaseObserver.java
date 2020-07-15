package pers.jay.wanandroid.base;

import com.google.gson.JsonParseException;

import android.net.ParseException;
import android.text.TextUtils;

import com.jess.arms.mvp.IView;

import org.json.JSONException;
import org.simple.eventbus.EventBus;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.observers.ResourceObserver;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.common.JApplication;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.http.ApiException;
import pers.jay.wanandroid.http.NetWorkManager;
import pers.jay.wanandroid.result.BaseBean;
import pers.zjc.commonlibs.util.StringUtils;
import retrofit2.HttpException;

public abstract class BaseObserver<T> extends ResourceObserver<T> {

    protected IView mView;
    private String mErroMessage;
    private JApplication mApp = JApplication.getInstance();

    public BaseObserver(IView mView) {
        this.mView = mView;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetWorkManager.isNetWorkAvailable()) {
            mView.showNoNetwork();
            dispose();
        }
    }

    @Override
    public void onNext(T t) {
        if (t instanceof BaseBean) {
            switch (((BaseBean)t).getCode()) {
                case Const.HttpConst.HTTP_CODE_SUCCESS:
                    onSuccess(t);
                    break;
                case Const.HttpConst.HTTP_CODE_LOGIN_EXPIRED:
                    EventBus.getDefault().post(new Event<>(Const.EventCode.LOGIN_EXPIRED, t));
                    break;
                case Const.HttpConst.HTTP_CODE_ERROR:
                default:
                    onError(new ApiException(StringUtils.isEmpty(((BaseBean)t).getMsg()) ? mApp.getString(R.string.error_unknown) : ((BaseBean)t).getMsg(), ((BaseBean)t).getCode()));
                    break;
            }
        } else {
            onSuccess(t);
        }

    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ConnectException ||  e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if ( e instanceof InterruptedIOException) {  //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof HttpException) {    // HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else if (e instanceof ApiException) {
            if (StringUtils.isEmpty(e.getMessage())) {
                onException(ExceptionReason.UNKNOWN_ERROR);
            } else {
                mErroMessage = e.getMessage();
                mView.showMessage(mErroMessage);
            }
        } else {    // 未知错误
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
        onComplete();
    }

    protected void onException(ExceptionReason reason) {
        switch (reason) {
            case PARSE_ERROR:
                mErroMessage = mApp.getString(R.string.error_parse);
                break;
            case BAD_NETWORK:
                mErroMessage = mApp.getString(R.string.network_unavailable_tip);
                break;
            case CONNECT_ERROR:
                mErroMessage = mApp.getString(R.string.error_fail_to_connect);
                break;
            case CONNECT_TIMEOUT:
                mErroMessage = mApp.getString(R.string.error_connect_time_out);
                break;
            case UNKNOWN_ERROR:
            default:
                mErroMessage = mApp.getString(R.string.error_unknown);
                break;
        }
        mView.showMessage(mErroMessage);
        onComplete();
    }

    @Override
    public void onComplete() {
        if (mView != null) {
            mView.hideLoading();
        }
    }

    /**
     * 成功回调
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 服务端返回错误回调：服务器返回数据，但响应码不为200
     * @param t
     */
    public void onError(T t) {
        if (t instanceof BaseBean) {
            String msg = ((BaseBean)t).getMsg();
            if (TextUtils.isEmpty(msg)) {
                mView.showMessage(mApp.getString(R.string.error_server_error));
            } else {
                mView.showMessage((((BaseBean)t).getMsg()));
            }
        } else {
            mView.showMessage(mApp.getString(R.string.error_unknown));
        }


    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
