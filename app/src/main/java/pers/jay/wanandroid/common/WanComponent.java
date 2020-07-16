package pers.jay.wanandroid.common;

import com.jess.arms.di.component.AppComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = WanModule.class)
public interface WanComponent {

    AppConfig appconfig();
}
