package com.anviz.googlepaynfc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import androidx.annotation.NonNull;

public class NfcHelper {
    /**
     * 设置为默认支付应用的请求码
     */
    public static final int NFC_SET_DEFAULT_REQ_CODE = 0x197;

    /**
     * 是否支持NFC
     */
    public static boolean isSupportNfc(@NonNull Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
    }

    /**
     * NFC 是否打开
     */
    public static boolean isNfcEnabled(@NonNull Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        if (manager == null) {
            return false;
        }
        NfcAdapter adapter = manager.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * 打开 NFC 设置页面
     */
    public static void openNfcSetting(@NonNull Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 判断是否为默认支付应用
     *
     * @param cls NFC 模拟卡服务类
     */
    public static boolean isDefaultPayment(@NonNull Context context, @NonNull Class<?> cls) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        if (manager == null) {
            return false;
        }
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        CardEmulation cardEmulation = CardEmulation.getInstance(adapter);
        if (cardEmulation == null) {
            return false;
        }
        return cardEmulation.isDefaultServiceForCategory(new ComponentName(context, cls), CardEmulation.CATEGORY_PAYMENT);
    }

    /**
     * 设置为默认支付，会弹出一个对话框，让用户确认。设置结果可以在 onActivityResult 中再次调用 {@link #isDefaultPayment(Context, Class)} 进行检查
     * requestCode 为 {@link #NFC_SET_DEFAULT_REQ_CODE}
     * <p>
     * 若不通过 onActivityResult 获取结果，可以在 {@link Activity#onResume()} 方法中通过 {@link #isDefaultPayment(Context, Class)} 进行检查
     *
     * @param context Activity
     * @param cls     NFC 模拟卡服务类
     */
    public static void setDefaultPayment(@NonNull Activity context, @NonNull Class<?> cls) {
        Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
        intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
        intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, new ComponentName(context, cls));
        context.startActivityForResult(intent, NFC_SET_DEFAULT_REQ_CODE);
    }
}
