package com.easymi.daijia.fragment.create;

import com.easymi.component.rxmvp.RxManager;
import com.easymi.daijia.result.BudgetResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.PassengerResult;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/27 0027.
 */

public interface CreateDJContract {
    interface View {
        void findById();

        void initPhoneEdit();

        void initPlace();

        void init();

        RxManager getManager();

        void showTypeTab(DJTypeResult result);

        void showPassenger(PassengerResult result);
    }

    interface Presenter {
        void queryDJType();

        void queryPassenger(String phone);

        void queryBudget(Long passengerId, Double distance, Integer time, Long orderTime, Long typeId);
    }

    interface Model {
        Observable<DJTypeResult> queryDJType(Long companyId);

        Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone);

        Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId);

    }
}
