package pers.jay.wanandroid.model.enums;

public enum ArticleInfoType {

    HOME(1), QA(2), SEARCH(3), SQUARE(4), COLLECTION(5), KNOWLEDGE(6), WEIXIN(7), PROJECT(8);

    private int type;

    ArticleInfoType(int type) {
        this.type = type;
    }

    public int value() {
        return type;
    }

}
