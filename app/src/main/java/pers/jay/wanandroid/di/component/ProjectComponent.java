package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.ProjectModule;
import pers.jay.wanandroid.mvp.contract.ProjectContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.ProjectFragment;

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
@Component(modules = ProjectModule.class, dependencies = AppComponent.class)
public interface ProjectComponent {

    void inject(ProjectFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        ProjectComponent.Builder view(ProjectContract.View view);

        ProjectComponent.Builder appComponent(AppComponent appComponent);

        ProjectComponent build();
    }
}