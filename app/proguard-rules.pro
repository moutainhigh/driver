# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 小米通道
-keep class com.xiaomi.** {*;}
-dontwarn com.xiaomi.**
# 华为通道
-keep class com.huawei.** {*;}
-dontwarn com.huawei.**

#eventBus
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


# hf add
#---------------------------基本配置---------------------------

# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*

# 避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 避免混淆反射
-keepattributes EnclosingMethod

# 避免混淆异常
-keepattributes Exceptions

#抛出异常时保留代码行号，在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable

#用于告诉ProGuard，不要跳过对非公开类的处理。默认情况下是跳过的，因为程序中不会引用它们，有些情况下人们编写的代码与类库中的类在同一个包下，并且对包中内容加以引用，此时需要加入此条声明。
-dontskipnonpubliclibraryclasses

#这个是给Microsoft Windows用户的，因为ProGuard假定使用的操作系统是能区分两个只是大小写不同的文件名，但是Microsoft Windows不是这样的操作系统，所以必须为ProGuard指定-dontusemixedcaseclassnames选项
-dontusemixedcaseclassnames

# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#声明在处理过程中输出更多信息。添加这项配置之后，如果处理过程中出现异常，会输出整个StackTrace而不是一条简单的异常说明。
-verbose

#-------------------------默认保留的区域----------------------------------------

# 保留了继承自Activity、Application这些类的子类
# 因为这些子类，都有可能被外部调用
# 比如说，第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
# 如果有引用android-support-v4.jar包，可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
-keep class android.support.** {*;}## 保留support下的所有类及其内部类

##如果引用了v4或者v7包
-dontwarn android.support.**
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**


# 保留在Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 枚举类不能被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# 保留自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化的类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
#Parcelable实现类除了不能混淆本身之外，为了确保类成员也能够被访问，类成员也不能被混淆
-keepclassmembers class * implements android.os.Parcelable {
 public <fields>;
 private <fields>;
}


# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 对于R（资源）下的所有类及其方法，都不能被混淆
-keep class **.R$* {
    *;
}

# 对于带有回调函数onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}


#移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用，另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
-assumenosideeffects class com.easymi.component.utils.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

#但是如果使用Gson进行数据解析的时候要注意Gson自身的混淆
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

#使用GSON、fastjson等框架时，所写的JSON对象类不混淆，否则无法将JSON解析成对应的对象
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

#---------------------------------webview------------------------------------
# webView处理，项目中没有使用到webView忽略即可  替换成自己你自己定义的那个类名
#关于我们
-keepclassmembers class com.easymi.personal.activity.AboutUsActivity {
    public *;
}
#安全中心webview使用类
-keepclassmembers class com.easymin.driver.securitycenter.activity.WebActivity {
    public *;
}
#公共webview使用类
-keepclassmembers class com.easymi.component.activity.WebActivity {
    public *;
}
#文章相关
-keepclassmembers class com.easymi.personal.activity.ArticleActivity {
    public *;
}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}

#---------------------------------本app内的混淆-------------------------------
-keep public class com.easymi.component.entity.**{*;}
-keep public class com.easymi.component.widget.**{*;}
-keep public class com.easymi.component.result.**{*;}

-keep public class com.easymi.common.entity.**{*;}
-keep public class com.easymi.common.result.**{*;}

-keep public class com.easymin.carpooling.entity.**{*;}
-keep public class com.easymin.carpooling.result.**{*;}

-keep public class com.easymin.chartered.entity.**{*;}
-keep public class com.easymin.chartered.result.**{*;}

-keep public class com.easymi.cityline.entity.**{*;}
-keep public class com.easymi.common.result.**{*;}

-keep public class com.easymin.custombus.entity.**{*;}
-keep public class com.easymin.custombus.result.**{*;}

-keep public class com.easymin.passengerbus.entity.**{*;}
-keep public class com.easymin.passengerbus.result.**{*;}

-keep public class com.easymi.personal.entity.**{*;}
-keep public class com.easymi.personal.result.**{*;}

-keep public class com.easymin.rental.entity.**{*;}
-keep public class com.easymin.rental.result.**{*;}

-keep public class com.easymin.driver.securitycenter.entity.**{*;}
-keep public class com.easymin.driver.securitycenter.result.**{*;}

-keep public class com.easymi.taxi.entity.**{*;}
-keep public class com.easymi.taxi.result.**{*;}

-keep public class com.easymi.zhuanche.entity.**{*;}
-keep public class com.easymi.zhuanche.result.**{*;}

-keep public class com.easymin.official.entity.**{*;}
-keep public class com.easymin.official.result.**{*;}

-keep public class com.easymi.component.app.**{*;}
-keep public class com.easymi.component.db.**{*;}

-keep public class com.easymi.component.utils.AesUtil{*;}
-keep public class com.easymi.component.cat.Cat{*;}


#不混淆所有的接口的某个字段：
-keep class com._65.sdk.Constants{
        int PLUGIN_TYPE_USER;
        int PLUGIN_TYPE_PAY;
      }

#不混淆所以的接口：
-keep interface *{
         <methods>;
          <fields>;
     }

#---------------------------------三方jar-------------------------------
#支付宝
-dontwarn android.net.**
-dontwarn com.alipay.sdk.sys.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.a.**{*;}
-keep class com.alipay.sdk.sys.**{*;}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}
-keep class android.net.SSLCertificateSocketFactory{*;}


#zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.**{*;}
-keep interface com.google.zxing.**{*;}


#tencent
-dontwarn com.tencent.**
-keep class com.tencent.**{ *; }
-keep interface com.tencent.**{ *; }


#rxlifecycle
-dontwarn com.trello.rxlifecycle.**
-keep class com.trello.rxlifecycle.**{*;}
-keep interface com.trello.rxlifecycle.**{*;}


#union银联
-keep class com.unionpay.**{*;}
-keep interface com.unionpay.**{*;}
-keep interface cn.gov.pbc.tsm.client.mobile.android.bank.service.**{*;}
-keep class UCMobile.PayPlugin.**{*;}
-keep class unionpay.**{*;}
-keep interface unionpay.**{*;}
-dontwarn com.unionpay.**


#arouter
-keep public class com.alibaba.android.arouter.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
-dontwarn com.alibaba.android.arouter.**



#retrofit2混淆
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }


#okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**


#高德地图map
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

-keep class com.amap.api.**{*;}
-keep class com.autonavi.**{*;}
-dontwarn com.amap.api.**


#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder


#科大讯飞语音
-keep class com.iflytek.**{*;}
-keepattributes Signature







