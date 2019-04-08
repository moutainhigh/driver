# 定制包车
主要流程介绍

* 订单所有状态
同专车状态基本一样。 只是在司机端没有新单和未接单的状态，司机端显示的单默认是已接单状态。
到达目的地确认订单后即为订单结束。线上不涉及资金操作。

```java
/**
     * 新单
     */
    /**
     * 已接单
     */
    public static final int TAKE_ORDER = 10;
    /**
     * 前往预约地
     */
    public static final int GOTO_BOOKPALCE_ORDER = 15;
    /**
     * 到达预约地
     */
    public static final int ARRIVAL_BOOKPLACE_ORDER = 20;
    /**
     * 前往目的地
     */
    public static final int GOTO_DESTINATION_ORDER = 25;
    /**
     * 到达目的地
     */
    public static final int ARRIVAL_DESTINATION_ORDER = 30;
    /**
     * 已完成
     */
    public static final int FINISH_ORDER = 35;
```

* 跑单流程
    * 行程未开始 （排班会有出发时间，界面会展示出发时间，底部也会有出发前的倒计时。尤为注意的是这个是客户预约的出发时间，司机可提前出发前往预约地。）
    * 前往预约地 （开始行程后即为前往预约地，此状态会展示客户的电话，以及到客户预约地的线路规划地图）
    * 到达预约地 （到达预约地后需要司机等待客户上车，在确认无误后滑动开始出发）
    * 前往目的地 （前往目的地中界面规划当前位置到客户预约的目的地规划，司机可按照导航前往目的地。）
    * 到达目的地 （达到客户目的地后需要司机滑动确认到达目的地）
    * 确认结束订单 （此界面展示订单信息，司机点击确认订单，此订单标志结束）
    
* 跑单流程图

![定制包车流程图](./chartered.ipg)
    
    