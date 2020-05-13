package pers.jay.wanandroid.widgets;

import android.app.Activity;
import android.content.Context;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import pers.jay.wanandroid.R;

public class ConfirmDialog extends DialogLayer {

    public ConfirmDialog(Activity activity) {
        super(activity);
    }

    public ConfirmDialog(Context context) {
        super(context);
    }

    public void show() {
//        AnyLayer.dialog().contentView(R.layout.base_ui_layout_dialog_confirm).
    }
}
