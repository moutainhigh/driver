package com.easymi.personal.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.easymi.component.Config
import com.easymi.component.base.RxBaseActivity
import com.easymi.component.network.ApiManager
import com.easymi.component.network.HaveErrSubscriberListener
import com.easymi.component.network.HttpResultFunc2
import com.easymi.component.network.MySubscriber
import com.easymi.component.utils.CommonUtil
import com.easymi.component.utils.EmUtil
import com.easymi.component.utils.TimeUtil
import com.easymi.component.utils.ToastUtil
import com.easymi.personal.McService
import com.easymi.personal.R
import com.easymi.personal.entity.MyPopularizeBean
import com.easymi.personal.entity.MyPopularizeMainBean
import kotlinx.android.synthetic.main.activity_my_popularize.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyPopularizeActivity : RxBaseActivity(), View.OnClickListener {

    private val adapter: BaseQuickAdapter<MyPopularizeBean, BaseViewHolder> by lazy {
        object : BaseQuickAdapter<MyPopularizeBean, BaseViewHolder>(R.layout.item_my_popularize) {
            override fun convert(helper: BaseViewHolder?, item: MyPopularizeBean?) {
                helper?.run {
                    item?.run {
                        setText(R.id.itemMyPopularizeTvName, passengerName)
                        setText(R.id.itemMyPopularizeTvPhone, passengerPhone)
                        setText(R.id.itemMyPopularizeTvTime, TimeUtil.getTime("yyyy-MM-dd", created * 1000))
                        setText(R.id.itemMyPopularizeTvMoney, CommonUtil.d2s(commissionAmount, "0.00"))
                    }
                }
            }
        }
    };

    override fun isEnableSwipe() = false

    override fun getLayoutId() = R.layout.activity_my_popularize;

    override fun initToolBar() {
        myPopularizeCtb.setTitle("推广详情").setLeftIcon(R.drawable.ic_arrow_back) { finish() };
    }

    override fun initViews(savedInstanceState: Bundle?) {
        myPopularizeHeaderTvRecord.setOnClickListener(this);
        myPopularizeHeaderTvCountOn.setOnClickListener(this);
        myPopularizeRv.layoutManager = (LinearLayoutManager(this))
        adapter.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent().apply {
                component = ComponentName(this@MyPopularizeActivity, MyPopularizeFeeDetailActivity::class.java)
                putExtra("data", adapter.data[position] as? MyPopularizeBean)
            })
        }
        myPopularizeRv.setAdapter(adapter)
        getData()
    }

    private fun getData() {
        ApiManager.getInstance().createApi(Config.HOST, McService::class.java)
                .getPromoteAppList(EmUtil.getEmployInfo().phone)
                .map(HttpResultFunc2<MyPopularizeMainBean>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MySubscriber<MyPopularizeMainBean>(this, true, false, object : HaveErrSubscriberListener<MyPopularizeMainBean> {
                    override fun onNext(t: MyPopularizeMainBean?) {
                        t?.run {
                            myPopularizeHeaderTvCount.text = ("${passengerNum}人推广总计");
                            myPopularizeHeaderTvMoney.text = "￥ ${CommonUtil.d2s(commissionAmount, "0.00")}";
                            adapter.setNewData(promoterPassengerVos);
                            myPopularizeRv.visibility = if (promoterPassengerVos.size > 0) View.VISIBLE else View.GONE;
                        }
                    }

                    override fun onError(code: Int) {
                        ToastUtil.showMessage(this@MyPopularizeActivity, "数据出现错误,请重试...");
                        finish();
                    }
                }))
    }

    override fun onClick(v: View?) {
        v?.run {
            startActivity(Intent().apply {
                component = ComponentName(this@MyPopularizeActivity,
                        if (id == R.id.myPopularizeHeaderTvRecord) MyPopularizeApplyRecordActivity::class.java
                        else MyPopularizeCountOnActivity::class.java)
            })
        }
    }
}