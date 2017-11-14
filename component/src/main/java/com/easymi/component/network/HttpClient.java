package com.easymi.component.network;

import android.content.Context;

import com.easymi.component.R;
import com.easymi.component.app.XApp;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xyin on 2016/10/11.
 * 网络请求client.
 */

public class HttpClient {

    private OkHttpClient mOkHttpClient;

    /**
     * 内部静态类实现单例,且在第一次使用时才加载.
     */
    private static class SingletonHolder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    /**
     * 获取单例实例.
     *
     * @return Api对象
     */
    public static HttpClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 私有化构造方法,配置okhttpClient.
     */
    private HttpClient() {
        File cacheFile = new File(XApp.context().getCacheDir(), "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 2); //2M
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();   //拦截器用来输出请求日志方便调试
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); //日志输出等级为BODY(打印请求和返回值的头部和body信息)

        //创建okhttp客户端
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(16000, TimeUnit.MILLISECONDS)
                .connectTimeout(16000, TimeUnit.MILLISECONDS)
                .addInterceptor(logInterceptor) //添加日志拦截器,进行输出日志
                .retryOnConnectionFailure(true)    //失败重连
                .cache(cache)
                .build();
    }

    /**
     * 创建一个网络访问的api.
     *
     * @param hostUrl 该api的host地址
     * @param service api的class类型
     * @param <T>     实际需要返回类型
     * @return 实际返回的api实例
     */
    public <T> T createApi(String hostUrl, Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //添加一个Gson转化
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //添加一个rxjava转换
                .baseUrl(hostUrl)
                .build();

        return retrofit.create(service);
    }

    /**
     * 获取错编码
     *
     * @param code： 错编码
     * @return :
     */
    public static String codeString(Context context, int code) {
        String str = null;
        switch (code) {
            case 0:
                str = context.getString(R.string.succeed);
                break;
            case -1:
                str = context.getString(R.string.code1);
                break;
            case -2:
                str = context.getString(R.string.code2);
                break;
            case -3:
                str = context.getString(R.string.code3);
                break;
            case -4:
                str = context.getString(R.string.code4);
                break;
            case -5:
                str = context.getString(R.string.code5);
                break;
            case -6:
                str = context.getString(R.string.code6);
                break;
            case -7:
                str = context.getString(R.string.code7);
                break;
            case -8:
                str = context.getString(R.string.code8);
                break;
            case -9:
                str = context.getString(R.string.code9);
                break;
            case -10:
                str = context.getString(R.string.code10);
                break;
            case -11:
                str = context.getString(R.string.code11);
                break;
            case -12:
                str = context.getString(R.string.code12);
                break;
            case -13:
                str = context.getString(R.string.code13);
                break;
            case -14:
                str = context.getString(R.string.code14);
                break;
            case -15:
                str = context.getString(R.string.code15);
                break;
            case -16:
                str = context.getString(R.string.code16);
                break;
            case -17:
                str = context.getString(R.string.code17);
                break;
            case -18:
                str = context.getString(R.string.code18);
                break;
            case -19:
                str = context.getString(R.string.code19);
                break;
            case -20:
                str = context.getString(R.string.code20);
                break;
            case -21:
                str = context.getString(R.string.code21);
                break;
            case -22:
                str = context.getString(R.string.code22);
                break;
            case -23:
                str = context.getString(R.string.code23);
                break;
            case -24:
                str = context.getString(R.string.code24);
                break;
            case -25:
                str = context.getString(R.string.code25);
                break;
            case -26:
                str = context.getString(R.string.code26);
                break;
            case -27:
                str = context.getString(R.string.code27);
                break;
            case -28:
                str = context.getString(R.string.code28);
                break;
            case -29:
                str = context.getString(R.string.code29);
                break;
            case -30:
                str = context.getString(R.string.code30);
                break;
            case -31:
                str = context.getString(R.string.code31);
                break;
            case -32:
                str = context.getString(R.string.code32);
                break;
            case -33:
                str = context.getString(R.string.code33);
                break;
            case -34:
                str = context.getString(R.string.code34);
                break;
            case -35:
                str = context.getString(R.string.code35);
                break;
            case -36:
                str = context.getString(R.string.code36);
                break;
            case -37:
                str = context.getString(R.string.code37);
                break;
            case -38:
                str = context.getString(R.string.code38);
                break;
            case -39:
                str = context.getString(R.string.code39);
                break;
            case -40:
                str = context.getString(R.string.code40);
                break;
            case -41:
                str = context.getString(R.string.code41);
                break;
            case -42:
                str = context.getString(R.string.code42);
                break;
            case -43:
                str = context.getString(R.string.code43);
                break;
            case -44:
                str = context.getString(R.string.code44);
                break;
            case -45:
                str = context.getString(R.string.code45);
                break;
            case -46:
                str = context.getString(R.string.code46);
                break;
            case -47:
                str = context.getString(R.string.code47);
                break;
            case -48:
                str = context.getString(R.string.code48);
                break;
            case -49:
                str = context.getString(R.string.code49);
                break;
            case -50:
                str = context.getString(R.string.code50);
                break;
            case -51:
                str = context.getString(R.string.code51);
                break;
            case -52:
                str = context.getString(R.string.code52);
                break;
            case -53:
                str = context.getString(R.string.code53);
                break;
            case -54:
                str = context.getString(R.string.code54);
                break;
            case -55:
                str = context.getString(R.string.code55);
                break;
            case -56:
                str = context.getString(R.string.code56);
                break;
            case -57:
                str = context.getString(R.string.code57);
                break;
            case -58:
                str = context.getString(R.string.code58);
                break;
            case -59:
                str = context.getString(R.string.code59);
                break;
            case -60:
                str = context.getString(R.string.code60);
                break;
            case -61:
                str = context.getString(R.string.code61);
                break;
            case -62:
                str = context.getString(R.string.code62);
                break;
            case -63:
                str = context.getString(R.string.code63);
                break;
            case -64:
                str = context.getString(R.string.code64);
                break;
            case -65:
                str = context.getString(R.string.code65);
                break;
            case -66:
                str = context.getString(R.string.code66);
                break;
            case -67:
                str = context.getString(R.string.code67);
                break;
            case -68:
                str = context.getString(R.string.code68);
                break;
            case -69:
                str = context.getString(R.string.code69);
                break;
            case -70:
                str = context.getString(R.string.code70);
                break;
            case -71:
                str = context.getString(R.string.code71);
                break;
            case -72:
                str = context.getString(R.string.code72);
                break;
            case -73:
                str = context.getString(R.string.code73);
                break;
            case -74:
                str = context.getString(R.string.code74);
                break;
            case -75:
                str = context.getString(R.string.code75);
                break;
            case -76:
                str = context.getString(R.string.code76);
                break;
            case -77:
                str = context.getString(R.string.code77);
                break;
            case -78:
                str = context.getString(R.string.code78);
                break;
            case -79:
                str = context.getString(R.string.code79);
                break;
            case -80:
                str = context.getString(R.string.code80);
                break;
            case -81:
                str = context.getString(R.string.code81);
                break;
            case -82:
                str = context.getString(R.string.code82);
                break;
            case -83:
                str = context.getString(R.string.code83);
                break;
            case -84:
                str = context.getString(R.string.code84);
                break;
            case -85:
                str = context.getString(R.string.code85);
                break;
            case -100:
                str = context.getString(R.string.code100);
                break;
        }
        return str;
    }

}
