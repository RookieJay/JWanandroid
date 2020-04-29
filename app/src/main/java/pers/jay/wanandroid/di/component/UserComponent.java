package pers.jay.wanandroid.di.component;

import dagger.BindsInstance;
import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import pers.jay.wanandroid.di.module.UserModule;
import pers.jay.wanandroid.mvp.contract.UserContract;

import com.jess.arms.di.scope.FragmentScope;

import pers.jay.wanandroid.mvp.ui.fragment.UserFragment;

/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 01/28/2020 18:33
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@FragmentScope
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface UserComponent {

    void inject(UserFragment fragment);

    @Component.Builder
    interface Builder {

        @BindsInstance
        UserComponent.Builder view(UserContract.View view);

        UserComponent.Builder appComponent(AppComponent appComponent);

        UserComponent build();
    }
}