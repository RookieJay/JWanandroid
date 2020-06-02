package pers.jay.wanandroid.model.dbconverter;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

import pers.jay.wanandroid.model.Tag;
import pers.zjc.commonlibs.util.GsonUtils;

public class TagConverter implements PropertyConverter<List<Tag>, String> {

    @Override
    public List<Tag> convertToEntityProperty(String databaseValue) {
        return GsonUtils.fromJson(databaseValue, new TypeToken<List<Tag>>() { }.getType());
    }

    @Override
    public String convertToDatabaseValue(List<Tag> entityProperty) {
        return GsonUtils.toJson(entityProperty);
    }
}
