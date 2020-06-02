package pers.jay.wanandroid.model.dbconverter;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

import pers.jay.wanandroid.model.Article;
import pers.zjc.commonlibs.util.GsonUtils;

public class ArticleConverter implements PropertyConverter<List<Article>, String> {

    @Override
    public List<Article> convertToEntityProperty(String databaseValue) {
        return GsonUtils.fromJson(databaseValue, new TypeToken<List<Article>>() { }.getType());
    }

    @Override
    public String convertToDatabaseValue(List<Article> entityProperty) {
        return GsonUtils.toJson(entityProperty);
    }
}
