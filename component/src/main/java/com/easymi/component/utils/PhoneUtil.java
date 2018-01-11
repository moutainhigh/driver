package com.easymi.component.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.easymi.component.R;
import com.easymi.component.permission.RxPermissions;

import java.util.List;

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

    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void call(final Activity context, final String phoneNumber) {
        try {
            RxPermissions rxPermissions = new RxPermissions(context);
            rxPermissions.request(Manifest.permission.CALL_PHONE)
                    .subscribe(granted -> {
                        if (granted) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phoneNumber));
                            context.startActivity(intent);
                        } else {

                        }
                    });
        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }
    }

    /**
     * 判断服务是否处于运行状态.
     *
     * @param servicename
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String servicename, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            if (servicename.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查GPS是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkGps(final Context context) {
        if (!PhoneFunc.hasGps(context)) {
            ToastUtil.showMessage(context, context.getResources().getString(R.string.no_gps), Toast.LENGTH_LONG);
        }
        if (!PhoneFunc.checkWifi(context)) {
            ToastUtil.showMessage(context, context.getResources().getString(R.string.closed_wifi), Toast.LENGTH_LONG);
        }
        if (!PhoneFunc.isGPSEnable(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(context.getResources().getString(R.string.please_open_gps));
            builder.setNegativeButton(context.getResources().getString(R.string.ok),
                    (dialog, which) -> {
                        if (!PhoneFunc.isGPSEnable(context)) {
                            try {
                                if ("ZTE".equalsIgnoreCase(Build.MANUFACTURER)) {
                                    ToastUtil.showMessage(context, context.getResources().getString(R.string.please_open_gps));
                                } else {
                                    context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            } catch (Exception e) {
                                ToastUtil.showMessage(context, context.getResources().getString(R.string.please_open_gps));
                            }
                        } else {
                            dialog.dismiss();
                        }
                    });
            builder.show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 键盘keyCode转为英文字母
     *
     * @param code
     * @return
     */
    public static String code2Str(int code) {
        String str = "";
        switch (code) {
            case 29:
                str = "A";
                break;
            case 30:
                str = "B";
                break;
            case 31:
                str = "C";
                break;
            case 32:
                str = "D";
                break;
            case 33:
                str = "E";
                break;
            case 34:
                str = "F";
                break;
            case 35:
                str = "G";
                break;
            case 36:
                str = "H";
                break;
            case 37:
                str = "I";
                break;
            case 38:
                str = "J";
                break;
            case 39:
                str = "K";
                break;
            case 40:
                str = "L";
                break;
            case 41:
                str = "M";
                break;
            case 42:
                str = "N";
                break;
            case 43:
                str = "O";
                break;
            case 44:
                str = "P";
                break;
            case 45:
                str = "Q";
                break;
            case 46:
                str = "R";
                break;
            case 47:
                str = "S";
                break;
            case 48:
                str = "T";
                break;
            case 49:
                str = "U";
                break;
            case 50:
                str = "V";
                break;
            case 51:
                str = "W";
                break;
            case 52:
                str = "X";
                break;
            case 53:
                str = "Y";
                break;
            case 54:
                str = "Z";
                break;


        }
        return str;
    }

}
