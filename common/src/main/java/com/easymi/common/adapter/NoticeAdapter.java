package com.easymi.common.adapter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.component.app.XApp;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.SwipeLayout;

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
        addItemType(MultipleOrder.ITEM_POSTER, R.layout.home_notice_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnnAndNotice item) {

        if (item.viewType == AnnAndNotice.ITEM_HEADER) {
            if (item.type == 0) {
                helper.setText(R.id.pinned_text, XApp.getInstance().getString(R.string.home_gonggao));
            } else {
                helper.setText(R.id.pinned_text, XApp.getInstance().getString(R.string.home_notice));
            }
        } else if (item.viewType == AnnAndNotice.ITEM_POSTER) {
            ViewHolder holder = new ViewHolder(helper.itemView);
            if (item.type == 0) {
                holder.title.setVisibility(View.GONE);
                holder.content.setText(item.annMessage);
            } else {
                holder.title.setVisibility(View.VISIBLE);
                holder.content.setText(item.noticeContent);
                holder.title.setText(item.noticeTitle);
            }

            holder.time.setText(TimeUtil.getTime(XApp.getInstance().getString(R.string.date_unit), item.time * 1000));

            holder.deleteFrame.setOnClickListener(view -> {
                remove(helper.getAdapterPosition());
                notifyDataSetChanged();
            });
        }
    }

    class ViewHolder {
        SwipeLayout swipeLayout;
        TextView title;
        TextView content;
        TextView time;
        FrameLayout deleteFrame;

        public ViewHolder(View itemView) {
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            deleteFrame = itemView.findViewById(R.id.delete_frame);
        }
    }
}
