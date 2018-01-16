package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RatingBar;
import com.easymi.personal.R;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by developerLzh on 2017/11/15 0015.
 */

public class EvaActivity extends RxBaseActivity {

    RatingBar ratingBar;
    TagContainerLayout tagContainerLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_eva;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ratingBar = findViewById(R.id.rating_bar);
        tagContainerLayout = findViewById(R.id.tag_container);

        ratingBar.setStarMark(4.4f);
        tagContainerLayout.setTagBackgroundColor(R.color.b_f0f0f0);
        tagContainerLayout.setTheme(ColorFactory.NONE);
        tagContainerLayout.addTag("长得帅");
        tagContainerLayout.addTag("说话平和");
        tagContainerLayout.addTag("很长的tag很长的tag很长的tag很长的tag");
        tagContainerLayout.addTag("财大气粗");
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setTitle(R.string.person_pingjia);
        cusToolbar.setLeftBack(view -> onBackPressed());
    }
}
