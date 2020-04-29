package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.TabModule;
import pers.jay.wanandroid.mvp.contract.TabContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.TabFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 11/18/2019 10:05
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = TabModule.class, dependencies = AppComponent.class)
public interface TabComponent {

    void inject(TabFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TabComponent.Builder view(TabContract.View view);

        TabComponent.Builder appComponent(AppComponent appComponent);

        TabComponent build();
    }
}