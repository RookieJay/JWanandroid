package pers.jay.wanandroid.common;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

import pers.zjc.commonlibs.util.SPUtils;

public class TodoTypeHelper {

    public static final String TODO_TYPE = "todo_type";

    private static SPUtils spUtils;
    private static Gson gson;

    private LinkedHashMap<Integer, String> mTypeMap = new LinkedHashMap<>();

    private static class TodoTypeHolder {
        private static final TodoTypeHelper INSTANCE = new TodoTypeHelper();
    }

    public static TodoTypeHelper getInstance() {
        if (spUtils == null) {
            spUtils = SPUtils.getInstance(TODO_TYPE);
        }
        if (gson == null) {
            gson = JApplication.getInstance().getAppComponent().gson();
        }
        return TodoTypeHolder.INSTANCE;
    }

    public LinkedHashMap<Integer, String> getmTypeMap() {

        return mTypeMap;
    }

    public void setmTypeMap(LinkedHashMap<Integer, String> mTypeMap) {
        this.mTypeMap = mTypeMap;
        spUtils.put(TODO_TYPE, gson.toJson(mTypeMap));
    }
}
