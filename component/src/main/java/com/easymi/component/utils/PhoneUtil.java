package com.easymi.component.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.easymi.component.permission.RxPermissions;

import rx.functions.Action1;

/**
 * Created by xyin on 2017/4/10.
 * 电话号码帮助类.
 */

public class PhoneUtil {

    private static final String TAG = "PhoneUtil";

    private PhoneUtil() {
    }

    /**
     * 获取联系人.
     *
     * @param activity    activity
     * @param requestCode requestCode
     */
    public static void getContacts(Activity activity, int requestCode) {
        if (activity != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 获取联系人.
     *
     * @param fragment    fragment
     * @param requestCode requestCode
     */
    public static void getContacts(Fragment fragment, int requestCode) {
        if (fragment != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 处理查询结果.
     *
     * @param resultCode resultCode
     * @param data       data
     */
    public static UserPhone handleResult(Context context, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null || context == null) {
            LogUtil.e(TAG, "getContacts fail, context is null or intent data is null");
            return null;
        }

        UserPhone userPhone = null;

        //开始查询
        Uri uri = data.getData();
        if (uri != null) {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null);
                if (phoneCursor != null) {
//                    while (phoneCursor.moveToNext()) {  //遍历该联系人所有号码
//                        String phoneNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        LogUtil.d(TAG, phoneNo + " (" + userName + ")");
//                    }

                    //获取第一个电话
                    if (phoneCursor.moveToNext()) {
                        String phoneNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        userPhone = new UserPhone();
                        userPhone.name = userName;
                        if (!TextUtils.isEmpty(phoneNo)) {
                            try {
                                // 对手机号码进行预处理（去掉号码前的+86、首尾空格、“-”号等）
                                userPhone.phoneNo = phoneNo.replaceAll("^(\\+86)", "")
                                        .replaceAll("^(86)", "")
                                        .replaceAll("-", "")
                                        .replaceAll(" ", "")
                                        .trim();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                userPhone.phoneNo = phoneNo;
                            }
                        } else {
                            userPhone.phoneNo = phoneNo;
                        }
                    }

                    phoneCursor.close();
                }
                cursor.close();
            }
        }
        return userPhone;
    }

    /**
     * 电话号码实例对象.
     */
    public static class UserPhone {
        public String name; //名字
        public String phoneNo; //电话号码
    }

    public static void hideKeyboard(Activity paramActivity) {

        InputMethodManager localInputMethodManager = (InputMethodManager) paramActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View localView = paramActivity.getCurrentFocus();
        if (localView != null) {
            IBinder localIBinder = localView.getWindowToken();
            if (localIBinder != null)
                localInputMethodManager
                        .hideSoftInputFromWindow(localIBinder, 0);
        }
    }

    public static void call(final Activity context, final String phoneNumber) {
        try {
            RxPermissions rxPermissions = new RxPermissions(context);
            rxPermissions.request(Manifest.permission.CALL_PHONE)
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean granted) {
                            if (granted) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + phoneNumber));
                                context.startActivity(intent);
                            } else {

                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

}
