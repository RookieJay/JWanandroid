package pers.jay.wanandroid.common;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class WanModule {

    @Provides
    @Singleton
    public AppConfig provideAppConfig() {
        return AppConfig.getInstance();
    }
}
