package pers.jay.wanandroid.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pers.zjc.commonlibs.util.SPUtils;

public class SearchHelper {

    public static final String SEARCH_KEY = "search_key";

    private static SearchHelper mInstance;
    private static SPUtils spUtils;
    private static Gson gson;
    private static final int MAX_SEARCH_KEY_COUNT = 20;

    private SearchHelper() {
        if (mInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SearchHelper getInstance() {
        if (mInstance == null) {
            synchronized (SearchHelper.class) {
                if (mInstance == null) {
                    mInstance = new SearchHelper();
                }
            }
        }
        spUtils = SPUtils.getInstance(SEARCH_KEY);
        gson = JApplication.getInstance().getAppComponent().gson();
        return mInstance;
    }

    private List<String> searchKeys;

    public List<String> getSearchKeys() {
        String json = spUtils.getString(SEARCH_KEY);
        searchKeys = gson.fromJson(json, new TypeToken<List<String>>() { }.getType());
        return searchKeys;
    }

    public void addSearchKey(String searchKey) {
        if (searchKeys != null) {
            if (searchKeys.size() == MAX_SEARCH_KEY_COUNT) {
                searchKeys.remove(searchKeys.size() - 1);
            }
            searchKeys.remove(searchKey);
        } else {
            searchKeys = new ArrayList<>();
        }
        searchKeys.add(0, searchKey);
        spUtils.put(SEARCH_KEY, gson.toJson(searchKeys));
    }

    public void removeSearchKey(String searchKey) {
        searchKeys.remove(searchKey);
        spUtils.put(SEARCH_KEY, gson.toJson(searchKeys));
    }

    public void clear() {
        spUtils.clear();
    }

}
