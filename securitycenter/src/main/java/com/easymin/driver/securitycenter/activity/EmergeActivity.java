package com.easymin.driver.securitycenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Arrays;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.adapter.RecyclerViewAdapter;
import com.easymin.driver.securitycenter.dialog.TimeDialog;
import com.easymin.driver.securitycenter.entity.Contact;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.ContactResult;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;
import com.easymin.driver.securitycenter.utils.PhoneUtil;
import com.easymin.driver.securitycenter.utils.ToastUtil;
import com.easymin.driver.securitycenter.widget.CusToolbar;
import com.easymin.driver.securitycenter.widget.SwitchButton;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: EmergeActivity
 * @Author: shine
 * Date: 2018/11/26 下午6:47
 * Description:
 * History:
 */
public class EmergeActivity extends CheckPermissionsActivity {

    private RecyclerViewAdapter mAdapter;
    private ArrayList<Contact> mDataSet = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayout lin_hint;
    private TextView tv_notice;
    private LinearLayout lin_time;
    private SwitchButton share_able_btn;
    private TextView tv_time;
    private TextView tv_lianxiren_hint;
    private EditText et_phone;
    private TextView tv_tongxunlu;
    private EditText et_name;
    private TextView tv_zengjia;
    private LinearLayout lin_list;
    private TextView tv_add;
    private LinearLayout lin_add;

    private CusToolbar toolbar;
    private String startTime;
    private String endTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emerge);

        initView();
        initToolbar();
        initData();
        initAdapter();
        initListener();
    }

    public void initData() {
        if (getIntent().getIntExtra("emergeContackCheck", 0) == 0) {
            showFirst();
            tv_tongxunlu.setOnClickListener(v -> {
                PhoneUtil.getContacts(this, 0X00);
            });
            tv_zengjia.setOnClickListener(v -> {
                if (TextUtils.isEmpty(et_name.getText().toString())) {
                    ToastUtil.showMessage(this, "请输入联系人姓名");
                    return;
                }
                if (TextUtils.isEmpty(et_phone.getText().toString())) {
                    ToastUtil.showMessage(this, "请输入联系人电话");
                    return;
                }
                firstAdd();
            });
        } else {
            hintFirst();
            getContactList();
        }
    }

    public void initToolbar() {
        toolbar = findViewById(R.id.cus_toolbar);
        toolbar.setLeftIcon(R.mipmap.ic_back, view -> {
            finish();
        });
        toolbar.setTitle("紧急联系人");
    }

    public void initView() {
        recyclerView = findViewById(R.id.recycler);
        lin_hint = findViewById(R.id.lin_hint);
        tv_notice = findViewById(R.id.tv_notice);
        lin_time = findViewById(R.id.lin_time);
        share_able_btn = findViewById(R.id.share_able_btn);
        tv_time = findViewById(R.id.tv_time);
        tv_lianxiren_hint = findViewById(R.id.tv_lianxiren_hint);
        et_phone = findViewById(R.id.et_phone);
        tv_tongxunlu = findViewById(R.id.tv_tongxunlu);
        et_name = findViewById(R.id.et_name);
        tv_zengjia = findViewById(R.id.tv_zengjia);
        lin_list = findViewById(R.id.lin_list);
        tv_add = findViewById(R.id.tv_add);
        lin_add = findViewById(R.id.lin_add);
    }

    public void initAdapter() {
        // Layout Managers:
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mAdapter = new RecyclerViewAdapter(this);
        mAdapter.setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener((view, contact) -> {
            int i = view.getId();
            if (i == R.id.tv_delete) {
                deletContact(contact.id);
            } else if (i == R.id.tv_edit) {
                Intent intent = new Intent(this, AddEmergeActivity.class);
                intent.putExtra("contact", contact);
                intent.putExtra("type", 1);
                startActivityForResult(intent, 0X01);
            } else if (i == R.id.lin_item) {
                if (contact.emerg_check == 1) {
                    tripEmergeChoose(contact, 0);
                } else {
                    tripEmergeChoose(contact, 1);
                }
            }
        });
    }

    public void initListener() {
        tv_add.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEmergeActivity.class);
            intent.putExtra("type", 0);
            startActivityForResult(intent, 0X01);
        });
        tv_time.setOnClickListener(v -> {
            TimeDialog timeDialog = new TimeDialog(this);
            timeDialog.setOnSelectListener(new TimeDialog.OnSelectListener() {
                @Override
                public void onSelect(String timeStr) {
                    tv_time.setText(timeStr);
                    startTime = timeStr.split("-")[0];
                    endTime = timeStr.split("-")[1];
                    shareAutoTime();
                }
            });
            timeDialog.show();
        });
    }

    /**
     * 显示第一次添加
     */
    public void showFirst() {
        lin_time.setVisibility(View.GONE);
        lin_list.setVisibility(View.GONE);
        lin_add.setVisibility(View.VISIBLE);
        lin_hint.setVisibility(View.VISIBLE);
        setAutoShatre();
    }

    /**
     * 已经添加过的显示
     */
    public void hintFirst() {
        lin_time.setVisibility(View.VISIBLE);
        lin_list.setVisibility(View.VISIBLE);
        lin_add.setVisibility(View.GONE);
        lin_hint.setVisibility(View.GONE);
    }

    /**
     * 显示开关和时间段
     */
    public void showSwitch(Contact contact) {
        if (contact.share_auto == 1) {
            share_able_btn.setChecked(true);
        } else {
            share_able_btn.setChecked(false);
        }
        startTime = contact.share_start;
        endTime = contact.share_end;
        if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
            tv_time.setText(startTime + "-" + endTime + "");
        } else {
            tv_time.setText("未设置");
        }
        setAutoShatre();
    }

    //除了第一次加载 其余的根据开关状态调用接口
    public void setAutoShatre(){
        share_able_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                shareAutoAment(1);
            } else {
                shareAutoAment(0);
            }
        });
    }

    //获取联系人列表
    public void getContactList() {
        Observable<ContactResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .contactList(CenterConfig.PASSENGERID, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                hintFirst();
                if (emResult.data != null && emResult.data.size() > 0) {
                    mDataSet.clear();
                    mDataSet.addAll(emResult.data);
                    mAdapter.setList(mDataSet);
                    showSwitch(emResult.data.get(0));
                } else {
                    showFirst();
                }
            } else {
                ToastUtil.showMessage(this, emResult.getMessage(), Toast.LENGTH_LONG);
            }
        })));
    }

    //添加紧急联系人
    public void firstAdd() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .tripEmergeContact(et_name.getText().toString(), et_phone.getText().toString(),
                        CenterConfig.PASSENGERID, CenterConfig.PASSENGERPHONE, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                getContactList();
            } else {
                ToastUtil.showMessage(this, emResult.getMessage(), Toast.LENGTH_LONG);
            }
        })));
    }

    //自动分享的开关
    public void shareAutoAment(int isOpen) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .shareAutoAment(CenterConfig.PASSENGERID, isOpen, startTime, endTime, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                getContactList();
            } else {
                ToastUtil.showMessage(this, emResult.getMessage());
            }
        })));
    }

    //修改分享时间
    public void shareAutoTime() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .shareAutoTime(CenterConfig.PASSENGERID, startTime, endTime, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                ToastUtil.showMessage(this, "时间设置成功");
            } else {
                ToastUtil.showMessage(this, emResult.getMessage());
            }
        })));
    }

    //删除单个订单
    public void deletContact(long id) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .deleteContact(id, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                getContactList();
                mAdapter.closeAllItems();
            } else {
                ToastUtil.showMessage(this, emResult.getMessage());
            }
        })));
    }

    //选中分享人和取消分享人
    public void tripEmergeChoose(Contact contact, int isSelect) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .tripEmergeChoose(contact.id, isSelect, CenterConfig.APPKEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(this, false,
                true, emResult -> {
            if (emResult.getCode() == 1) {
                getContactList();
            } else {
                ToastUtil.showMessage(this, emResult.getMessage());
            }
        })));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0X00) {
                PhoneUtil.UserPhone userPhone = PhoneUtil.handleResult(this, resultCode, data);
                et_name.setText(userPhone.name);
                et_phone.setText(userPhone.phoneNo);
            } else if (requestCode == 0X01) {
                getContactList();
            }
        }
    }
}
