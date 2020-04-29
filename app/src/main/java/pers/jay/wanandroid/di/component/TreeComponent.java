package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.TreeModule;
import pers.jay.wanandroid.mvp.contract.TreeContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.KnowledgeFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 10/17/2019 16:17
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = TreeModule.class, dependencies = AppComponent.class)
public interface TreeComponent {

    void inject(KnowledgeFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        TreeComponent.Builder view(TreeContract.View view);

        TreeComponent.Builder appComponent(AppComponent appComponent);

        TreeComponent build();
    }
}