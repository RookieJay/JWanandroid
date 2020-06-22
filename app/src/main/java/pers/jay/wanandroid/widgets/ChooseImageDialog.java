package pers.jay.wanandroid.widgets;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.jess.arms.utils.PermissionUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import per.goweii.anylayer.AnimatorHelper;
import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DragLayout;
import per.goweii.anylayer.Layer;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.utils.RxPhotoTool;
import pers.zjc.commonlibs.util.ToastUtils;
import timber.log.Timber;

public class ChooseImageDialog {

    public static Layer create(Fragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("fragment can not be null");
        }
        Context context = fragment.getContext();
        if (context == null) {
            return null;
        }
        return AnyLayer.dialog(context)
                       .contentView(R.layout.layout_popup_choose_image)
                       .cancelableOnTouchOutside(true)
                       .backgroundDimAmount(0.5f)
                       .gravity(Gravity.BOTTOM)
                       .dragDismiss(DragLayout.DragStyle.Bottom)
                       .contentAnimator(new Layer.AnimatorCreator() {
                           @Override
                           public Animator createInAnimator(View target) {
                               return AnimatorHelper.createBottomInAnim(target);
                           }

                           @Override
                           public Animator createOutAnimator(View target) {
                               return AnimatorHelper.createBottomOutAnim(target);
                           }
                       })
                       .bindData(layer -> {
                           TextView tvCamera = layer.getView(R.id.tvCamera);
                           TextView tvAlbum = layer.getView(R.id.tvAlbum);
                           tvCamera.setOnClickListener(v -> {
                               requestCameraPermission(fragment, context);
                               layer.dismiss();
                           });
                           tvAlbum.setOnClickListener(v -> {
                               RxPhotoTool.openLocalImage(fragment);
                               layer.dismiss();
                           });
                       });
    }

    private static void requestCameraPermission(Fragment fragment, Context context) {
        PermissionUtil.RequestPermission requestPermission = new PermissionUtil.RequestPermission() {
            @Override
            public void onRequestPermissionSuccess() {
                Timber.e("调用 onRequestPermissionSuccess");
                RxPhotoTool.takePhoto(context, fragment);
            }

            @Override
            public void onRequestPermissionFailure(List<String> permissions) {
                ToastUtils.showShort("拒绝相机权限将无法正常使用头像功能");
            }

            @Override
            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                ToastUtils.showShort("您已拒绝相机权限，将无法正常使用app,请前往系统设置打开相机权限");
            }
        };
        RxErrorHandler handler = RxErrorHandler.builder()
                                               .with(context)
                                               .responseErrorListener((c, t) -> Timber.e(t))
                                               .build();
        PermissionUtil.requestPermission(requestPermission,
                new RxPermissions((FragmentActivity)context), handler, Manifest.permission.CAMERA);
    }

}
