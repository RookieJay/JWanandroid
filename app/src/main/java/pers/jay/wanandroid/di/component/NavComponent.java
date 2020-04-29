package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.NavModule;
import pers.jay.wanandroid.mvp.contract.NavContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.NavFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 10/18/2019 11:25
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = NavModule.class, dependencies = AppComponent.class)
public interface NavComponent {

    void inject(NavFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        NavComponent.Builder view(NavContract.View view);

        NavComponent.Builder appComponent(AppComponent appComponent);

        NavComponent build();
    }
}