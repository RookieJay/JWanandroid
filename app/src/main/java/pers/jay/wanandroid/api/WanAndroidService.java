package pers.jay.wanandroid.api;

import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import pers.jay.wanandroid.model.Coin;
import pers.jay.wanandroid.model.CoinHistory;
import pers.jay.wanandroid.model.HotKey;
import pers.jay.wanandroid.model.Navi;
import pers.jay.wanandroid.model.PageInfo;
import pers.jay.wanandroid.model.ShareUserArticles;
import pers.jay.wanandroid.model.Tab;
import pers.jay.wanandroid.model.Todo;
import pers.jay.wanandroid.model.User;
import pers.jay.wanandroid.model.Article;
import pers.jay.wanandroid.model.ArticleInfo;
import pers.jay.wanandroid.model.BannerImg;
import pers.jay.wanandroid.model.Website;
import pers.jay.wanandroid.result.WanAndroidResponse;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface WanAndroidService {

    /**
     * 登录
     * @param  username 账号 password 密码
     * @return
     */
    @POST("user/login")
    @FormUrlEncoded
    Observable<WanAndroidResponse<User>> login(@Field("username") String username, @Field("password") String password); //@QueryMap LinkedHashMap<String, String> map

    /**
     * 退出登录
     * http://www.wanandroid.com/user/logout/json
     */
    @GET("user/logout/json")
    Observable<WanAndroidResponse> logout();

    /**
     * 注册
     * @param username 用户名
     * @param password 密码
     * @param repassword 确认密码
     * @return
     */
    @POST("user/register")
    @FormUrlEncoded
    Observable<WanAndroidResponse<User>> register(@Field("username") String username, @Field("password") String password, @Query("repassword") String repassword);

    /**
     * 轮播图
     */
    @GET("banner/json")
    Observable<WanAndroidResponse<List<BannerImg>>> banner();

    /**
     * 首页文章列表
     * @param page 页码
     * @return
     */
    @GET("article/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> homeArticles(@Path("page") int page);

    /**
     * 置顶文章
     */
    @GET("/article/top/json")
    Observable<WanAndroidResponse<List<Article>>> topArticles();

    /**
     * 收藏文章列表
     * @param page
     * @return
     */
    @GET("lg/collect/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> colArticles(@Path("page") int page);

    /**
     * 收藏站内文章
     * @param articleId 文章id
     */
    @POST("/lg/collect/{id}/json")
    Observable<WanAndroidResponse> collectInside(@Path("id") int articleId);

    /**
     * 收藏站外文章
     * @param title
     * @param author
     * @param link
     * @return
     */
    @POST("/lg/collect/add/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> collectOutside(@Field("title") String title, @Field("author") String author, @Field("link") String link);

    /**
     * 取消收藏文章(普通文章)
     * @param articleId 文章id
     */
    @POST("lg/uncollect_originId/{id}/json")
    Observable<WanAndroidResponse> unCollect(@Path("id") int articleId);

    /**
     * 取消收藏文章(我的收藏页面（该页面包含自己录入的内容）)
     * @param articleId
     * @return
     */
    @POST("/lg/uncollect/{id}/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> unCollectMine(@Path("id") int articleId, @Field("originId") int originId);

    /**
     * 收藏网站列表
     * @return
     */
    @GET("/lg/unCollect/usertools/json")
    Observable<WanAndroidResponse<List<Website>>> collectSites();

    /**
     * 收藏网址
     * @return
     */
    @POST("/lg/unCollect/addtool/json")
    Observable<WanAndroidResponse<List<Website>>> collectSites(@Field("name") String name, @Field("link") String link);

    /**
     *  编辑收藏网站
     * @return
     */
    @POST("/lg/unCollect/updatetool/json")
    Observable<WanAndroidResponse<List<Website>>> editSites(@Field("id") int id, @Field("name") String name, @Field("link") String link);

    /**
     *  删除收藏网站
     * @return
     */
    @POST("/lg/unCollect/deletetool/json")
    Observable<WanAndroidResponse<List<Website>>> deleteSites(@Field("id") int id);

    /**
     * 搜索
     */
    @POST("/article/query/{page}/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse<ArticleInfo>> search(@Path("page") int page, @Field("k") String key);

    /**
     * 搜索热词
     */
    @GET("/hotkey/json")
    Observable<WanAndroidResponse<List<HotKey>>> hotKeys();

    /**
     * 体系数据
     */
    @GET("/tree/json")
    Observable<WanAndroidResponse<List<Tab>>> tree();

    /**
     * 知识体系下的文章
     * @param page 页码
     * @param cid 分类的id
     */
    @GET("/article/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> treeArticles(@Path("page") int page, @Query("cid") int cid);

    /**
     * 按照作者昵称搜索文章
     * @param page
     * @param author
     * @return
     */
    @POST("/article/list/{page}/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse<ArticleInfo>> searchActiclesByAuthor(@Path("page") int page, @Field("author") String author);

    /**
     * 导航数据
     * @return
     */
    @GET("/navi/json")
    Observable<WanAndroidResponse<List<Navi>>> naviData();

    /**
     * 项目分类
     */
    @GET("/project/tree/json")
    Observable<WanAndroidResponse<List<Tab>>> projectTab();

    /**
     * 项目列表数据
     * @param page
     * @param cid
     * @return
     */
    @GET("/project/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> projects(@Path("page") int page, @Query("cid") int cid);

    /**
     * 获取公众号列表
     * https://wanandroid.com/wxarticle/chapters/json
     */
    @GET("wxarticle/chapters/json")
    Observable<WanAndroidResponse<List<Tab>>> wxList();

    /**
     * 查看某个公众号历史数据
     * https://wanandroid.com/wxarticle/list/408/1/json
     */
    @GET("wxarticle/list/{id}/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> wxArticleList(@Path("id") int id, @Path("page") int page);

    /**
     * 在某个公众号中搜索历史文章
     * https://wanandroid.com/wxarticle/list/405/1/json?k=Java
     */
    @GET("wxarticle/list/{id}/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> wxArticleSearch(@Path("id") int id, @Path("page") int page, @Field("k") int key);

    /**
     * 积分排行榜接口
     * https://www.wanandroid.com/coin/rank/1/json
     */
    @GET("coin/rank/{page}/json")
    Observable<WanAndroidResponse<PageInfo<Coin>>> allRank(@Path("page") int page);

    /**
     * 获取个人积分，需要登录后访问
     * https://www.wanandroid.com/lg/coin/userinfo/json
     */
    @GET("lg/coin/userinfo/json")
    Observable<WanAndroidResponse<Coin>> personalCoin();

    /**
     * 获取个人积分获取列表(历史记录)，需要登录后访问
     * https://www.wanandroid.com//lg/coin/list/1/json
     */
    @GET("lg/coin/list/{page}/json")
    Observable<WanAndroidResponse<PageInfo<CoinHistory>>> coinHistory(@Path("page") int page);

    /**
     * 10.1 广场列表数据
     * https://wanandroid.com/user_article/list/0/json
     * GET请求
     * 页码拼接在url上从0开始
     * 可能出现返回列表数据<每页数据，因为有自见的文章被过滤掉了。
     */
    @GET("/user_article/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> squareArticles(@Path("page") int page);

    /**
     * 10.2 分享人对应列表数据
     * https://www.wanandroid.com/user/2/share_articles/1/json
     */
    @GET("/user/2/share_articles/{page}/json")
    Observable<WanAndroidResponse<ShareUserArticles>> shareUserArticles(@Path("page") int page);

    /**
     * 10.3 自己的分享的文章列表
     * 如果你登陆了，可以直接点击查看自己分享的列表：
     * https://wanandroid.com/user/lg/private_articles/1/json
     */
    @GET("user/lg/private_articles/{page}/json")
    Observable<WanAndroidResponse<ShareUserArticles>> privateArticles(@Path("page") int page);

    /**
     * 10.4 删除自己分享的文章
     * https://wanandroid.com/lg/user_article/delete/9475/json
     * 请求:POST
     * 参数：文章id，拼接在链接上
     */
    @POST("lg/user_article/delete/{id}/json")
    Observable<WanAndroidResponse> deleteArticle(@Path("id") int id);

    /**
     * 10.5 分享文章
     * https://www.wanandroid.com/lg/user_article/add/json
     * 请求：POST
     * 参数：title:link
     * 	注意需要登录后查看，如果为CSDN，简书等链接会直接通过审核，在对外的分享文章列表中展示。
     * 否则只能在自己的分享文章列表查看，见10.3。
     */
    @POST("lg/user_article/add/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> shareArticle(@Field("title") String title, @Field("link") String link);

    /**
     * 玩 Android TODO Open API v2 地址：https://www.wanandroid.com/blog/show/2442
     * 务必使用 https
     */
    /**
     *1. 新增一个 TODO
     * https://www.wanandroid.com/lg/todo/add/json
     *
     * 方法：POST
     * 参数：
     * 	title: 新增标题（必须）
     * 	content: 新增详情（必须）
     * 	date: 2018-08-01 预定完成时间（不传默认当天，建议传）
     * 	type: 大于0的整数（可选）；
     * 	priority 大于0的整数（可选）；
     *  type 可以用于，在app 中预定义几个类别：例如 工作1；生活2；娱乐3；
     *  新增的时候传入1，2，3，查询的时候，传入type 进行筛选；
     *  如果不设置type则为 0，未来无法做 type=0的筛选，会显示全部（筛选 type 必须为大于 0 的整数）
     *  priority 大于0的整数（可选）；重要（1）一般（2）等
     *
     */
    @POST("lg/todo/add/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> addTodo(@FieldMap LinkedHashMap<String, Object> map);

    /**
     *2. 更新一个 Todo
     * https://www.wanandroid.com/lg/todo/update/83/json
     *
     * 方法：POST
     * 参数：
     * 	id: 拼接在链接上，为唯一标识，列表数据返回时，每个todo 都会有个id标识 （必须）
     * 	title: 更新标题 （必须）
     * 	content: 新增详情（必须）
     * 	date: 2018-08-01（必须）
     * 	status: 0 // 0为未完成，1为完成
     * 	type: ；
     * 	priority: ；
     * 	如果有当前状态没有携带，会被默认值更新，比如当前 todo status=1，更新时没有带上，会认为被重置。
     *  注意：当更新 status=1时，会自动设置服务器当前时间为完成时间。
     */
    @POST("lg/todo/update/{id}/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> updateTodo(@Path("id") int id, @FieldMap LinkedHashMap<String, Object> map);

    /**
     *
     * 3. 删除一个 Todo
     * https://www.wanandroid.com/lg/todo/delete/83/json
     *
     * 方法：POST
     * 参数：
     * 	id: 拼接在链接上，为唯一标识
     */
    @POST("lg/todo/delete/{id}/json")
    Observable<WanAndroidResponse> deleteTodo(@Path("id") int id);

    /**
     * 4. 仅更新完成状态Todo
     * https://www.wanandroid.com/lg/todo/done/80/json
     *
     * 方法：POST
     * 参数：
     * 	id: 拼接在链接上，为唯一标识
     * 	status: 0或1，传1代表未完成到已完成，反之则反之。
     * 只会变更status，未完成->已经完成 or 已经完成->未完成。
     */
    @POST("lg/todo/done/{id}/json")
    @FormUrlEncoded
    Observable<WanAndroidResponse> doneTodo(@Path("id") int id, @Field("status") int status);

    /**
     *
     * 5. TODO 列表
     * https://www.wanandroid.com~/lg/todo/v2/list/页码/json
     * 	页码从1开始，拼接在url 上
     * 	status 状态， 1-完成；0未完成; 默认全部展示；
     * 	type 创建时传入的类型, 默认全部展示
     * 	priority 创建时传入的优先级；默认全部展示
     * 	orderby 1:完成日期顺序；2.完成日期逆序；3.创建日期顺序；4.创建日期逆序(默认)；
     *
     * 注意：page 从1开始
     */
    @GET("lg/todo/v2/list/{page}/json")
    Observable<WanAndroidResponse<PageInfo<Todo>>> todoList(@Path("page") int page, @QueryMap LinkedHashMap<String, Object> map);

    /**
     * 问答列表
     * @param page 页码从1开始
     * @return
     */
    @GET("wenda/list/{page}/json")
    Observable<WanAndroidResponse<ArticleInfo>> qaList(@Path("page") int page);

}
