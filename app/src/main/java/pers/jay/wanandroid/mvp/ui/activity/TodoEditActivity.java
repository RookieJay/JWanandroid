package pers.jay.wanandroid.mvp.ui.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.EventBusManager;
import com.jess.arms.utils.ArmsUtils;

import java.util.Date;

import butterknife.BindView;
import pers.jay.wanandroid.R;
import pers.jay.wanandroid.common.CommonTextWatcher;
import pers.jay.wanandroid.common.Const;
import pers.jay.wanandroid.di.component.DaggerTodoEditComponent;
import pers.jay.wanandroid.event.Event;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.mvp.contract.TodoEditContract;
import pers.jay.wanandroid.mvp.presenter.TodoEditPresenter;
import pers.jay.wanandroid.utils.UIUtils;
import pers.zjc.commonlibs.util.StringUtils;
import pers.zjc.commonlibs.util.TimeUtils;
import pers.zjc.commonlibs.util.ToastUtils;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class TodoEditActivity extends BaseActivity<TodoEditPresenter>
        implements TodoEditContract.View {

    public static final int TYPE_ADD = 1;
    public static final int TYPE_EDIT = 2;

    @BindView(R.id.ivLeft)
    ImageView ivLeft;
    @BindView(R.id.toolbar_left)
    RelativeLayout toolbarLeft;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivRight)
    ImageView ivRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.etDate)
    EditText etDate;
    @BindView(R.id.btCommit)
    AppCompatButton btCommit;
    @BindView(R.id.switchPriority)
    SwitchCompat switchPriority;
    @BindView(R.id.rlPriority)
    RelativeLayout rlPriority;

    /*待办已办类型*/
    private int mtodoType;
    /*分组如工作、学习*/
    private int mtodoCat;
    private String mTitle;
    private Todo mData;
    private boolean important = false;
    private int mStartType = -1;

    private CommonTextWatcher textWatcher;
    private boolean hasChanged;

    private Calendar ca = Calendar.getInstance();
    private int mYear = ca.get(Calendar.YEAR);
    private int mMonth = ca.get(Calendar.MONTH);
    private int mDay = ca.get(Calendar.DAY_OF_MONTH);

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerTodoEditComponent.builder()
                               .appComponent(appComponent)
                               .view(this)
                               .build()
                               .inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                    .fitsSystemWindows(true)  //使用该属性,必须指定状态栏颜色
                    .statusBarColor(R.color.colorPrimary)
                    .init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_todo_edit;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        prepareData();
        setView();
    }

    private void setView() {
        initToolbar();
        initTodo();
    }

    private void initToolbar() {
        String titleFormat = mStartType == TYPE_ADD ? "新增TODO(%s)" : mStartType == TYPE_EDIT ? "编辑TODO(%s)" : "";
        tvTitle.setText(String.format(titleFormat, StringUtils.isEmpty(mTitle) ? "" : mTitle));
        ivLeft.setOnClickListener(v -> {
            if (hasChanged) {
                showExitDialog();
            }
            else {
                killMyself();
            }
        });
    }

    private void showExitDialog() {
        UIUtils.createConfirmDialog(this, getResources().getString(R.string.tip_unsave), true,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        killMyself();
                    }
                }, null).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initTodo() {
        requestFocus();
        configTextWatcher();
        fillData();
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> chooseDeadLine());
        rlPriority.setOnClickListener(v -> changePriority());
        //        switchPriority.setOnCheckedChangeListener((buttonView, isChecked) -> changePriority());
        btCommit.setOnClickListener(v -> commitTodo());
    }

    private void changePriority() {
        important = !important;
        switchPriority.setChecked(important);
        mData.setPriority(important ? 1 : 2);
        if (important != mData.important()) {
            hasChanged = true;
        }
    }

    private void fillData() {
        if (mStartType == TYPE_EDIT && mData != null) {
            etTitle.setText(mData.getTitle());
            etContent.setText(mData.getContent());
            etDate.setText(mData.getDateStr());
            if (null != mData) {
                important = mData.important();
            }
            switchPriority.setChecked(important);

        }
        if (null != mData) {
            mData.setPriority(important ? 1 : 2);
        }
    }

    private void requestFocus() {
        UIUtils.showSoftInputFromWindow(this, etTitle);
    }

    private void configTextWatcher() {
        CommonTextWatcher etTitleWatcher = new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.equals(s.toString(), etTitle.getText().toString())) {
                    hasChanged = true;
                }
                mData.setTitle(s.toString());
            }
        };
        CommonTextWatcher etContentWatcher = new CommonTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.equals(s.toString(), etContent.getText().toString())) {
                    hasChanged = true;
                }
                mData.setContent(s.toString());
            }
        };
        etTitle.addTextChangedListener(etTitleWatcher);
        etContent.addTextChangedListener(etContentWatcher);
    }

    private void commitTodo() {
        assert mPresenter != null;
        switch (mStartType) {
            case TYPE_ADD:
                mPresenter.addTodo(mData);
                break;
            case TYPE_EDIT:
                mPresenter.updateTodo(mData);
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chooseDeadLine() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    month++;
                    //                        showMessage(String.format("选择了%s年%s月%s日", year, month, dayOfMonth));
                    String dateStr = String.format("%s-%s-%s", year,
                            month >= 10 ? month : "0" + month,
                            dayOfMonth >= 10 ? dayOfMonth : "0" + dayOfMonth);
                    if (!StringUtils.equals(dateStr, mData.getDateStr())) {
                        hasChanged = true;
                    }
                    etDate.setText(dateStr);
                    Date date = TimeUtils.string2Date(dateStr, Const.DateFormat.WITHOUT_HMS);
                    mData.setDate(TimeUtils.date2Millis(date));
                    mData.setDateStr(dateStr);
                }, mYear, mMonth, mDay);
        datePickerDialog.setCanceledOnTouchOutside(true);
        datePickerDialog.show();
    }

    private void prepareData() {
        Intent intent = getIntent();
        mtodoType = intent.getIntExtra(Const.Key.KEY_TODO_TYPE, -1);
        mtodoCat = intent.getIntExtra(Const.Key.KEY_TODO_CAT, -1);
        mTitle = intent.getStringExtra(Const.Key.KEY_TITLE);
        mStartType = intent.getIntExtra(Const.Key.KEY_START_TYPE, -1);
        mData = intent.getParcelableExtra(Const.Key.KEY_TODO);
        if (mtodoType == -1 || mtodoCat == -1 || mStartType == -1) {
            showMessage("非法参数");
            killMyself();
            return;
        }
        switch (mStartType) {
            case TYPE_ADD:
                mData = new Todo();
                break;
            case TYPE_EDIT:
                if (mData == null) {
                    showMessage("非法参数");
                    killMyself();
                    break;
                }
            default:
                break;
        }
        mData.setType(mtodoCat);
    }

    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ToastUtils.showShort(message);
    }

    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    public void killMyself() {
        finish();
    }

    @Override
    public void commitSuccess() {
        EventBusManager.getInstance().post(new Event<>(Const.EventCode.COMMIT_SUCCESS, null));
        killMyself();
    }
}
