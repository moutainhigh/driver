package com.easymin.driver.securitycenter.network;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by xyin on 2016/10/11.
 * 网络请求client.
 */

public class ApiManager {

    private OkHttpClient mOkHttpClient;

    /**
     * 内部静态类实现单例,且在第一次使用时才加载.
     */
    private static class SingletonHolder {
        private static final ApiManager INSTANCE = new ApiManager();
    }

    /**
     * 获取单例实例.
     *
     * @return Api对象
     */
    public static ApiManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 私有化构造方法,配置okhttpClient.
     */
    private ApiManager() {
//        File cacheFile = new File(CApp.getInstance().getCacheDir(), "cache");
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 2); //2M

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();   //拦截器用来输出请求日志方便调试
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //日志输出等级为BODY(打印请求和返回值的头部和body信息)

//        Ssl ssl = new Ssl(CApp.getInstance(), "");

        EncryptInterceptor encryptInterceptor = new EncryptInterceptor();

        //创建okhttp客户端
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(16000, TimeUnit.MILLISECONDS)
                .connectTimeout(16000, TimeUnit.MILLISECONDS)
                .addInterceptor(encryptInterceptor)
                .addInterceptor(new TokenInterceptor())//token拦截器
                .addInterceptor(logInterceptor) //添加日志拦截器,进行输出日志
                .retryOnConnectionFailure(true) //失败重连
                ;
//                .cache(cache);

//        if (CenterConfig.HOST.contains("https://")) {
//            builder.sslSocketFactory(ssl.getSslSocketFactory(), ssl.getTrustManager());
//        }

        mOkHttpClient = builder.build();
    }

    /**
     * 创建一个使用网络访问的api.
     *
     * @param hostUrl 该api的host地址
     * @param service api的class类型
     * @param <T>     实际需要返回类型
     * @return 实际返回的api实例
     */
    public <T> T createApi(String hostUrl, Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(KeyGsonConverterFactory.create()) //添加一个Gson转化
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加一个rxjava转换
                .baseUrl(hostUrl)
                .build();

        return retrofit.create(service);
    }

}
