package com.easymi.common.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.MultipleOrder;

import java.util.List;

/**
 * Created by liuzihao on 2018/4/20.
 */

public class NoticeAdapter extends BaseMultiItemQuickAdapter<AnnAndNotice, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public NoticeAdapter(List<AnnAndNotice> data) {
        super(data);
        addItemType(MultipleOrder.ITEM_HEADER, R.layout.order_pinned_layout);
        addItemType(MultipleOrder.ITEM_POSTER, R.layout.order_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnnAndNotice item) {

    }
}
