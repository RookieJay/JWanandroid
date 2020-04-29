package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.SearchModule;
import pers.jay.wanandroid.mvp.contract.SearchContract;

import com.jess.arms.di.scope.ActivityScope;

import pers.jay.wanandroid.mvp.ui.fragment.SearchHisFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 12/04/2019 17:42
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@ActivityScope
@Component(modules = SearchModule.class, dependencies = AppComponent.class)
public interface SearchComponent {

    void inject(SearchHisFragment activity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        SearchComponent.Builder view(SearchContract.View view);

        SearchComponent.Builder appComponent(AppComponent appComponent);

        SearchComponent build();
    }
}