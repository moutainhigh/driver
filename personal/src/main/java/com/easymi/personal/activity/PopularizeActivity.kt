package com.easymi.personal.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.easymi.component.Config
import com.easymi.component.base.RxBaseActivity
import com.easymi.component.network.*
import com.easymi.component.utils.EmUtil
import com.easymi.component.utils.PhoneUtil
import com.easymi.personal.McService
import com.easymi.personal.R
import com.easymi.personal.entity.PromoteDetail
import kotlinx.android.synthetic.main.activity_popularize.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PopularizeActivity : RxBaseActivity(), View.OnClickListener {
    var promoteDetail: PromoteDetail? = null

    override fun isEnableSwipe() = false;

    override fun getLayoutId() = R.layout.activity_popularize

    override fun initToolBar() {
        popularizeCtb.setLeftIcon(R.drawable.ic_arrow_back) { finish() };
    }

    override fun initViews(savedInstanceState: Bundle?) {
        popularizeTvActionGreen.setOnClickListener(this);
        popularizeTvActionGreenLine.setOnClickListener(this);
        initData()
    }

    private fun initData() {
        val observable = ApiManager.getInstance().createApi(Config.HOST, McService::class.java)
                .getPromoteApplyDetail(EmUtil.getEmployInfo().phone, 2)
                .map(HttpResultFunc2<PromoteDetail>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MySubscriber(this, true, false, object : HaveErrSubscriberListener<PromoteDetail> {
                    override fun onNext(t: PromoteDetail?) {
                        promoteDetail = t;
                        bindInfoResult();
                    }

                    override fun onError(code: Int) {
                        finish();
                    }

                }))

        mRxManager.add(observable)
    }


    private fun bindInfoResult() {
        promoteDetail?.run {
            if (status == 0) {
                popularizeCtb.setTitle("成为推广者");
                popularizeTvPhone.visibility = View.GONE;
                with(popularizeTvActionGreen) {
                    visibility = View.VISIBLE;
                    text = "申请成为推广者";
                }
                popularizeTvActionGreenLine.visibility = View.GONE;
            } else if (status == 1) {
                popularizeTvContent.text = "您可以申请成为平台推广员，申请后经审核您可成为平台的推广员，我们将与您精密合作，互帮互助共同发展。";
                popularizeCtb.setTitle("审核中");
                popularizeTvPhone.visibility = View.GONE;
                popularizeTvActionGreen.visibility = View.GONE;
                popularizeTvActionGreenLine.visibility = View.GONE;
            } else if (status == 2) {
                popularizeTvContent.text = remark;
                popularizeCtb.setTitle("成为推广者");
                setSpan();
                with(popularizeTvActionGreen) {
                    visibility = View.VISIBLE;
                    text = "重新提交";
                }
                popularizeTvActionGreenLine.visibility = View.GONE;
            } else if (status == 3) {
                popularizeTvContent.text = "我们已经与您达成合作，您已经成为平台推广员，您可以通过系统邀请用户成为平台乘客，由此您可以获得来自平台的奖励。";
                popularizeCtb.setTitle("我的推广");
                popularizeTvPhone.visibility = View.GONE;
                with(popularizeTvActionGreen) {
                    visibility = View.VISIBLE;
                    text = "我的推广码";
                }
                with(popularizeTvActionGreenLine) {
                    visibility = View.VISIBLE;
                    text = "推广详情";
                }
            } else if (status == 4) {
                popularizeTvContent.text = "您已被管理员停权，您可以联系平台了解详细信息，也可以重新提交申请。";
                popularizeCtb.setTitle("成为推广者");
                setSpan();
                popularizeTvActionGreen.visibility = View.GONE;
                popularizeTvActionGreenLine.visibility = View.GONE;
            }
        }
    }

    private fun setSpan() {
        val spannableStringBuilder = SpannableStringBuilder("平台联系电话:")
        with(spannableStringBuilder) {
            append(if (EmUtil.getEmployInfo() != null) EmUtil.getEmployInfo().companyPhone else "")
            setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(this@PopularizeActivity, R.color.colorYellow)
                }

                override fun onClick(widget: View) {
                    if (!TextUtils.isEmpty(EmUtil.getEmployInfo().companyPhone)) {
                        PhoneUtil.call(this@PopularizeActivity, EmUtil.getEmployInfo().companyPhone)
                    }
                }
            }, 7, spannableStringBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }


        with(popularizeTvPhone) {
            visibility = View.VISIBLE
            highlightColor = Color.TRANSPARENT
            text = spannableStringBuilder
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.popularizeTvActionGreen) {
            if (promoteDetail?.status == 3) {
                startActivity(Intent(this, MyPopularizeCodeActivity::class.java));
            } else {
                bePopularizer();
            }
        } else if (v?.id == R.id.popularizeTvActionGreenLine) {
            startActivity(Intent(this, MyPopularizeActivity::class.java));
        }
    }

    private fun bePopularizer() {
        mRxManager.add(ApiManager.getInstance().createApi(Config.HOST, McService::class.java)
                .promoteApply(EmUtil.getEmployInfo().nickName, EmUtil.getEmployInfo().phone, 2)
                .filter(HttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { Observable.timer(1, TimeUnit.SECONDS); }
                .flatMap {
                    ApiManager.getInstance().createApi(Config.HOST, McService::class.java)
                            .getPromoteApplyDetail(EmUtil.getEmployInfo().phone, 2)
                            .map(HttpResultFunc2<PromoteDetail>())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MySubscriber<PromoteDetail>(this, true, false, object : HaveErrSubscriberListener<PromoteDetail> {
                    override fun onNext(t: PromoteDetail?) {
                        promoteDetail = t;
                        bindInfoResult();
                    }

                    override fun onError(code: Int) {
                        finish()
                    }
                })));
    }

}
