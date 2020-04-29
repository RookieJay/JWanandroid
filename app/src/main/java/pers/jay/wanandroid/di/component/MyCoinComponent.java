package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.MyCoinModule;
import pers.jay.wanandroid.mvp.contract.MyCoinContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.MyCoinFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/16/2019 17:08
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = MyCoinModule.class, dependencies = AppComponent.class)
public interface MyCoinComponent {

    void inject(MyCoinFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        MyCoinComponent.Builder view(MyCoinContract.View view);

        MyCoinComponent.Builder appComponent(AppComponent appComponent);

        MyCoinComponent build();
    }
}