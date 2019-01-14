## 对应包名及其作用

| 包名        | 释义   |  追加描述  |
| --------   | :-----:  | :----:  |
| activity      | 组件性质的activity   |   最基础多模块都常用的activity     |
| adapter        |   适配器   |      |
| app        |    Application基础类及Activity管理类    |  Application的配置和activity的管理  |
| base        |    rx基础类    |  rxActivity与rxFragment（基础base的封装）  |
| db        |    数据库    |  数据库表的操作（更改数据库记得加版本号）  |
| entity        |    实体类    |  常用和公共的实体  |
| loc        |    定位服务相关    | 位置分发用到了观察者模式   |
| network        |    网络层    | RxJava+Retrofit   |
| pay        |    支付功能封装    | 银联、微信、支付宝   |
| permission        |    Android 6.0+权限申请    | RxJava设计   |
| push        |    即时通信    | Mqtt   |
| receiver        |    系统级广播接收器    |  主要是gps和网络变化监测  |
| result        |    网络请求返回实体基础类    |  code、data、msg格式  |
| rxmvp        |    RxJava + MVP模式基础类    |  主要有添加订阅和取消订阅  |
| share        |    第三方分享    |  qq和微信相关配置和监听  |
| trace        |    轨迹纠偏工具    |  定位点位置轨迹纠偏工具  |
| update        |    应用更新组件    |  应用系统内更新模块  |
| utils        |    各种常用的工具类    |  项目中公用或常用的工具集合  |
| widget        |    自定义控件   |  公用或常用的视图控件集合  |

## 特殊或重要类使用介绍
 * Config 最常用的配置类，包含了项目所有基本配置信息。如后台地址、mqtt配置、appkey配置、七牛云上传回显地址、分享app_id、SharedPrefence常量配置等等 
 * NaviActivity 导航activity，所有需要导航的业务都是使用这个activity进行导航，只需要传入起点终点信息即可进行基本导航。
 * XApp Application实现类。包含了ARouter、DataBase、讯飞IflytekTTS、bugly以及其他三方库和常用配置的初始化
 * RxBaseActivity 所有activity的基类，包含mRxManager、ActManager的初始化，gpsReceiver、netChangeReceiver、tiredReceiver、HttpCustomReceiver等的事件接收和统一分发
 * SqliteHelper 数据库操作类，对应表的创建、更新、删除都在这个表操作。每次更改了表字段或者数据库，需要版本号加一，用于避免应用需要卸载安装。
