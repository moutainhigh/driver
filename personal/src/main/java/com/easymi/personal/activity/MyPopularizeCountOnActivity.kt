package com.easymi.personal.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.easymi.common.CommApiService
import com.easymi.common.result.LoginResult
import com.easymi.component.Config
import com.easymi.component.app.XApp
import com.easymi.component.base.RxBaseActivity
import com.easymi.component.entity.Employ
import com.easymi.component.network.ApiManager
import com.easymi.component.network.HttpResultFunc
import com.easymi.component.network.MySubscriber
import com.easymi.component.network.NoErrSubscriberListener
import com.easymi.component.utils.CommonUtil
import com.easymi.component.utils.EmUtil
import com.easymi.component.utils.ToastUtil
import com.easymi.personal.McService
import com.easymi.personal.R
import kotlinx.android.synthetic.main.activity_my_popularize_count_on.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MyPopularizeCountOnActivity : RxBaseActivity(), View.OnClickListener {

    var employ: Employ? = null;

    override fun isEnableSwipe() = false;

    override fun getLayoutId() = R.layout.activity_my_popularize_count_on;

    override fun initToolBar() {
        super.initToolBar()
        myPopularizeCountOnCtb.setTitle("结算").setLeftIcon(R.drawable.ic_arrow_back) { finish(); };
    }

    override fun initViews(savedInstanceState: Bundle?) {
        myPopularizeCountOnTvAll.setOnClickListener(this)
        myPopularizeCountOnTvCountApply.setOnClickListener(this)
        myPopularizeCountOnTvRecord.setOnClickListener(this)
        myPopularizeCountOnEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                setText(s);
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    fun setText(s: Editable?) {
        if (!s.isNullOrBlank() && s.toString().toDouble() > employ?.balance ?: 0.0) {
            myPopularizeCountOnTvAll.visibility = View.GONE;
            myPopularizeCountOnTvCurrent.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
            myPopularizeCountOnTvCurrent.text = "输入金额超过剩余金额";
        } else {
            myPopularizeCountOnTvAll.visibility = View.VISIBLE;
            myPopularizeCountOnTvCurrent.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            myPopularizeCountOnTvCurrent.text = "当前剩余 ¥${CommonUtil.d2s(employ?.balance
                    ?: 0.0, "0.00")}";
        }
    }

    override fun onClick(v: View?) {
        when (v?.id ?: 0) {
            R.id.myPopularizeCountOnTvAll -> {
                employ?.run {
                    val content = CommonUtil.d2s(balance, "0.00")
                    myPopularizeCountOnEt.setText(content)
                    if (content.length <= 6) {
                        myPopularizeCountOnEt.setSelection(content.length)
                    }
                }
            }
            R.id.myPopularizeCountOnTvCountApply -> promoteApply()
            R.id.myPopularizeCountOnTvRecord ->
                startActivity(Intent(this@MyPopularizeCountOnActivity, MyPopularizeApplyRecordActivity::class.java))
        }
    }

    private fun promoteApply() {
        if (!myPopularizeCountOnEt.text.isNullOrBlank() &&
                myPopularizeCountOnEt.text.toString().toDouble() > 0
                && myPopularizeCountOnTvAll.isShown) {
            ApiManager.getInstance().createApi(Config.HOST, McService::class.java)
                    .promoteSettlementApply(myPopularizeCountOnEt.text.toString(), EmUtil.getEmployId())
                    .filter(HttpResultFunc())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MySubscriber(this, true, false, NoErrSubscriberListener {
                        ToastUtil.showMessage(this, "结算成功")
                        finish()
                    }))
        } else {
            ToastUtil.showMessage(this, "请输入正确的金额")
        }
    }

    override fun onResume() {
        super.onResume()
        getDriverInfo()
    }

    private fun getDriverInfo() {
        val observable: Observable<LoginResult> = ApiManager.getInstance().createApi(Config.HOST, CommApiService::class.java)
                .getDriverInfo(EmUtil.getEmployId(), EmUtil.getAppKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(MySubscriber<LoginResult>(this, true, false, NoErrSubscriberListener<LoginResult> { t ->
            employ = t?.data;
            employ?.saveOrUpdate();
            XApp.getEditor().putLong(Config.SP_DRIVERID, employ?.id ?: 0).apply();
            setText(myPopularizeCountOnEt.text);
        })));
    }
}
