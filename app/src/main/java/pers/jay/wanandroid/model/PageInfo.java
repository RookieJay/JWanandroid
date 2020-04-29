package pers.jay.wanandroid.model;

import java.util.List;

public class PageInfo<T> {

    /**
     * curPage : 1
     * datas : [{"coinCount":4958,"level":50,"rank":1,"userId":20382,"username":"g**eii"},{"coinCount":4896,"level":49,"rank":2,"userId":27535,"username":"1**08491840"},{"coinCount":4624,"level":47,"rank":3,"userId":3559,"username":"A**ilEyon"},{"coinCount":3869,"level":39,"rank":4,"userId":1260,"username":"于**家的吴蜀黍"},{"coinCount":3433,"level":35,"rank":5,"userId":28694,"username":"c**ng0218"},{"coinCount":3428,"level":35,"rank":6,"userId":2,"username":"x**oyang"},{"coinCount":3266,"level":33,"rank":7,"userId":1534,"username":"j**gbin"},{"coinCount":3216,"level":33,"rank":8,"userId":9621,"username":"S**24n"},{"coinCount":3166,"level":32,"rank":9,"userId":2068,"username":"i**Cola"},{"coinCount":3142,"level":32,"rank":10,"userId":863,"username":"m**qitian"},{"coinCount":3088,"level":31,"rank":11,"userId":3753,"username":"S**phenCurry"},{"coinCount":3051,"level":31,"rank":12,"userId":29303,"username":"深**士"},{"coinCount":3047,"level":31,"rank":13,"userId":7590,"username":"陈**啦啦啦"},{"coinCount":3033,"level":31,"rank":14,"userId":7710,"username":"i**Cola7"},{"coinCount":3030,"level":31,"rank":15,"userId":1871,"username":"l**shifu"},{"coinCount":3025,"level":31,"rank":16,"userId":833,"username":"w**lwaywang6"},{"coinCount":2998,"level":30,"rank":17,"userId":15603,"username":"r**eryxx"},{"coinCount":2957,"level":30,"rank":18,"userId":7541,"username":"l**64301766"},{"coinCount":2930,"level":30,"rank":19,"userId":23244,"username":"a**ian"},{"coinCount":2922,"level":30,"rank":20,"userId":7809,"username":"1**23822235"},{"coinCount":2883,"level":29,"rank":21,"userId":10010,"username":"c**01220122"},{"coinCount":2872,"level":29,"rank":22,"userId":22832,"username":"7**502274@qq.com"},{"coinCount":2847,"level":29,"rank":23,"userId":25793,"username":"F**_2014"},{"coinCount":2847,"level":29,"rank":24,"userId":6142,"username":"c**huah"},{"coinCount":2847,"level":29,"rank":25,"userId":14032,"username":"M**eor"},{"coinCount":2847,"level":29,"rank":26,"userId":28607,"username":"S**Brother"},{"coinCount":2833,"level":29,"rank":27,"userId":28454,"username":"c**xzxzc"},{"coinCount":2812,"level":29,"rank":28,"userId":27596,"username":"1**5915093@qq.com"},{"coinCount":2812,"level":29,"rank":29,"userId":8152,"username":"t**g111"},{"coinCount":2807,"level":29,"rank":30,"userId":2199,"username":"M**459"}]
     * offset : 0
     * over : false
     * pageCount : 416
     * size : 30
     * total : 12461
     */

    private int curPage;
    private int offset;
    private boolean over;
    private int pageCount;
    private int size;
    private int total;
    private List<T> datas;

    public int getCurPage() { return curPage;}

    public void setCurPage(int curPage) { this.curPage = curPage;}

    public int getOffset() { return offset;}

    public void setOffset(int offset) { this.offset = offset;}

    public boolean isOver() { return over;}

    public void setOver(boolean over) { this.over = over;}

    public int getPageCount() { return pageCount;}

    public void setPageCount(int pageCount) { this.pageCount = pageCount;}

    public int getSize() { return size;}

    public void setSize(int size) { this.size = size;}

    public int getTotal() { return total;}

    public void setTotal(int total) { this.total = total;}

    public List<T> getDatas() { return datas;}

    public void setDatas(List<T> datas) { this.datas = datas;}

}
