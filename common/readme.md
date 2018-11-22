关键数据：
在本模块中，ZXOrder是后台的班次，OrderCustomer相当于后台的专线订单

在工作台获取到班次数据后应该在本地构建DymOrder数据，具体代码如下：

                            ZXOrder zxOrder = emResult2.getData().get(0);
                            zxOrder.orderType = Config.CITY_LINE;
                            zxOrder.startOutTime = zxOrder.startOutTime * 1000;
                            zxOrder.startJierenTime = zxOrder.startOutTime - zxOrder.minute * 60 * 1000;

                            if (DymOrder.findByIDType(zxOrder.orderId, Config.CITY_LINE) == null) {
                                DymOrder dymOrder = new DymOrder();
                                dymOrder.orderStatus = ZXOrderStatus.WAIT_START;
                                dymOrder.orderId = zxOrder.orderId;
                                dymOrder.orderType = Config.CITY_LINE;
                                dymOrder.saveOrUpdate();
                            }

在班次列表某一栏点击时应该调用接口查询该班次下的所有订单信息，代码如下：
                               private void getCustomers(ZXOrder zxOrder) {
                                   Observable<EmResult2<List<OrderCustomer>>> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                                           .getOrderCustomers(zxOrder.orderId)
                                           .filter(new HttpResultFunc3<>())
                                           .subscribeOn(Schedulers.io())
                                           .observeOn(AndroidSchedulers.mainThread());

                                   mRxManager.add(observable.subscribe(new MySubscriber<EmResult2<List<OrderCustomer>>>(this, false, false, new NoErrSubscriberListener<EmResult2<List<OrderCustomer>>>() {
                                       @Override
                                       public void onNext(EmResult2<List<OrderCustomer>> result2) {
                                           List<OrderCustomer> orderCustomers = result2.getData();
                                           for (int i = 0; i < orderCustomers.size(); i++) {
                                               OrderCustomer orderCustomer = orderCustomers.get(i);

                                               orderCustomer.appointTime = orderCustomer.appointTime * 1000;
                                               orderCustomer.num = i;
                                               orderCustomer.acceptSequence = i;
                                               orderCustomer.sendSequence = i;
                                               orderCustomer.status = 0;
                                               orderCustomer.subStatus = 0;

                                               orderCustomer.orderId = zxOrder.orderId;
                                               orderCustomer.orderType = zxOrder.orderType;

                                               for (OrderCustomer.OrderAddressVo orderAddressVo : orderCustomer.orderAddressVos) {
                                                   if (orderAddressVo.type == 1) { //起点
                                                       orderCustomer.startAddr = orderAddressVo.address;
                                                       orderCustomer.startLat = orderAddressVo.latitude;
                                                       orderCustomer.startLng = orderAddressVo.longitude;
                                                   } else { //终点
                                                       orderCustomer.endAddr = orderAddressVo.address;
                                                       orderCustomer.endLat = orderAddressVo.latitude;
                                                       orderCustomer.endLng = orderAddressVo.longitude;
                                                   }
                                               }
                                               orderCustomer.saveOrUpdate();
                                           }

                                            ARouter.getInstance().build("/cityline/FlowActivity").withSerializable("zxOrder",zxOrder).navigation();

                                       }
                                   })));
                               }

数据查询处理完毕即可跳转到订单执行流程页面，跨模块调用代码：
            ARouter.getInstance().build("/cityline/FlowActivity").withSerializable("zxOrder",zxOrder).navigation();



班车补单跨模块调用代码：  ARouter.getInstance().build("/cityline/CreateOrderActivity").navigation();