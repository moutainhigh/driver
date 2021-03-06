package com.easymi.taxi.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.taxi.R;
/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: InputRemarkDialog
 * @Author: shine
 * Date: 2018/11/16 上午11:23
 * Description: 备注输入框
 * History:
 */
public class InputRemarkDialog extends Dialog {

    public InputRemarkDialog(Context context) {
        super(context);
    }

    public InputRemarkDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * 构建弹窗
     */
    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private EditText editText;
        private String editStr;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setEditStr(String editStr) {
            this.editStr = editStr;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public InputRemarkDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final InputRemarkDialog dialog = new InputRemarkDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.taxi_dialog_remark_layout, null);
            editText = (EditText) layout.findViewById(R.id.edit_money);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (StringUtils.isBlank(getEditStr())) {
                                        ToastUtil.showMessage(context, context.getResources().getString(R.string.refuse_reason));
                                        return;
                                    }
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
//            if (negativeButtonText != null) {
//                ((Button) layout.findViewById(R.id.negativeButton))
//                        .setText(negativeButtonText);
//                if (negativeButtonClickListener != null) {
//                    ((Button) layout.findViewById(R.id.negativeButton))
//                            .setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//                                    negativeButtonClickListener.onClick(dialog,
//                                            DialogInterface.BUTTON_NEGATIVE);
//                                }
//                            });
//                }
//            } else {
//                // if no confirm button just set the visibility to GONE
//                layout.findViewById(R.id.negativeButton).setVisibility(
//                        View.GONE);
//            }
            // set the content message
            if (message != null) {
                editText.setHint(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }
            if (editStr != null) {
                editText.setText(editStr);
            }
            dialog.setContentView(layout);
            return dialog;
        }

        public String getEditStr() {
            return editText.getText().toString();
        }
    }
}
