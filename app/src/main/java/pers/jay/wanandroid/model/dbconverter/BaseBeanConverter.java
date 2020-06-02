package pers.jay.wanandroid.model.dbconverter;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

import pers.zjc.commonlibs.util.GsonUtils;

public class BaseBeanConverter<T> implements PropertyConverter<List<T>, String> {

    @Override
    public List<T> convertToEntityProperty(String databaseValue) {
        return GsonUtils.fromJson(databaseValue, new TypeToken<T>() { }.getType());
    }

    @Override
    public String convertToDatabaseValue(List<T> entityProperty) {
        return GsonUtils.toJson(entityProperty);
    }

}
