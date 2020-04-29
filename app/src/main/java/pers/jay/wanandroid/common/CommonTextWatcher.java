package pers.jay.wanandroid.common;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class CommonTextWatcher implements TextWatcher {

    public CommonTextWatcher() {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        textChanged(s);
    }

    public abstract void textChanged(Editable s);

}
