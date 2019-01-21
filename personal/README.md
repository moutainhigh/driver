# 个人中心
### 使用中的单页面简述
* 个人中心中主要是放的司机的非业务相关的界面，除登陆注册，其余都是一些关联性较小的单页面。下面对各个界面做一个简述：

| activity     | 界面描述   |  主要功能  |
| --------     | :-----:  | :----:  |
| AboutUsActivity     | 关于我们   |   通过接口获取alias为DriverAboutUs的相关文章信息，以html的形式用webview加载显示在界面中|
| AnnouncementActivity      |   公告列表   |  查询司机相关公告进行展示    |
| ArticleActivity |   文章管理   |  根据tag、articleId、annId加载对应的文章或者公告显示在webview中  |
| ChangeActivity |   修改密码   |  司机登陆中用旧密码修改新密码（注意点：密码都是SHA256加密后传输的）  |
| DetailActivity |   账户明细  |  司机账户明细列表展示  |
| EvaActivity  |    乘客评价    | 乘客对司机服务的评价统计  |
| LanguageActivity |  语言设置  |  切换界面繁体简体文字 |
| LoginActivity |  登陆界面  | 司机账号密码登陆界面 |
| MoreActivity  |  更多功能界面  |  附近厕所、帮助中心、天气预报、系统检查等  |
| MsgActivity  |  消息通知界面 |  主要公告、通知等消息类界面  |
| NaviSetActivity | 导航设置界面  | 主要对导航规划的规划方式进行一个配置   |
| NearWcActivity | 附近厕所  |  高德请求附近厕所点，加载到地图展示  |
| NotifityActivity | 通知列表  |  通知列表数据集合展示 |
| PersonalActivity | 个人中心界面 | 个人中心相关信息展示，功能列表等 |
| PocketActivity |  我的钱包 | 我的钱包界面、包含余额、提现、充值、明细等功能 |
| RechargeActivity | 充值界面 | 余额充值界面，tag根据后台配置显示，默认三个。目前支持支付宝和微信充值 |
| SetActivity | 设置界面 |   |
| utils        |    各种常用的工具类    |  项目中公用或常用的工具集合  |
| widget       |    自定义控件   |  公用或常用的视图控件集合  |
