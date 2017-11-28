package com.easymi.daijia.fragment.create;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.daijia.result.BudgetResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.PassengerResult;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public class CreateDJPresenter implements CreateDJContract.Presenter {

    private CreateDJContract.View view;
    private CreateDJContract.Model model;

    private Context context;

    public CreateDJPresenter(CreateDJContract.Model model, Context context) {
        this.model = model;
        this.context = context;
        this.model = new CreateDJMoldel();
    }

    @Override
    public void queryDJType() {
        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
        view.getManager().add(model.queryDJType(employ.company_id).subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJTypeResult>() {
            @Override
            public void onNext(DJTypeResult djTypeResult) {
                view.showTypeTab(djTypeResult);
            }

            @Override
            public void onError(int code) {

            }
        })));
    }

    @Override
    public void queryPassenger(String phone) {
        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
        view.getManager().add(model.queryPassenger(employ.company_id, employ.company_name, phone).subscribe(new MySubscriber<>(context, true, true, new NoErrSubscriberListener<PassengerResult>() {
            @Override
            public void onNext(PassengerResult result) {
                view.showPassenger(result);
            }
        })));
    }

    @Override
    public void queryBudget(Long passengerId, Double distance, Integer time, Long orderTime, Long typeId) {
        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
        view.getManager().add(model.getBudgetPrice(passengerId,employ.company_id,distance,time,orderTime,typeId).subscribe(new MySubscriber<BudgetResult>(context, false, false, new HaveErrSubscriberListener<BudgetResult>() {
            @Override
            public void onNext(BudgetResult budgetResult) {

            }

            @Override
            public void onError(int code) {

            }
        })));
    }
}
