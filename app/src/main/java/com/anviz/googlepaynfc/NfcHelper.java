package com.anviz.googlepaynfc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.List;

public class NfcHelper {
    private static volatile NfcHelper instance;
    /**
     * 设置为默认支付应用的请求码
     */
    public static final int NFC_SET_DEFAULT_REQ_CODE = 0x197;

    private Context context;

    private NfcHelper(Context context) {
        this.context = context;
    }

    public static NfcHelper getInstance(Context context) {
        // Double check locking pattern
        if (instance == null) { // Check for the first time
            synchronized (NfcHelper.class) {   // Check for the second time.
                // if there is no instance available... create new one
                if (instance == null) {
                    instance = new NfcHelper(context);
                }
            }
        }
        return instance;
    }

    private String userId = "";
    private String cardNo = "";

    public void saveInfo(@NonNull String cardNo, @NonNull String userId, @NonNull String macAddress) {
        this.cardNo = cardNo;
        this.userId = userId;

        File filesDir = context.getApplicationContext().getFilesDir();
        File file = new File(filesDir, "nfc_card");
        String content = cardNo + "," + userId;
        FileUtils.writeFile(file, content);

        Log.d("NFCHOLDER", "saveInfo: " + file.getAbsolutePath());
    }

    /**
     * @return Pair<userId, cardNo>
     */
    @Nullable
    public Pair<String, String> getInfo() {
        if (StringUtils.isNotEmpty(cardNo) && StringUtils.isNotEmpty(userId)) {
            return new Pair<>(userId, cardNo);
        }

        File filesDir = context.getApplicationContext().getFilesDir();
        File file = new File(filesDir, "nfc_card");
        Log.d("NFCHOLDER", "getInfo: " + file.getAbsolutePath());
        if (!file.exists()) {
            return null;
        }
        String content = FileUtils.readFile(file.getAbsolutePath());
        String[] split = content.split(",");
        this.cardNo = split[0];
        this.userId = split[1];

        return new Pair<>(userId, cardNo);
    }

    @NonNull
    @SuppressLint("HardwareIds")
    public static String getAndroidId(@NonNull Context context) {
        String androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Log.d("NFCHOLDER", "getAndroidId: " + androidId);
        return androidId;
    }

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
        return isDefaultNFC(context, cls, CardEmulation.CATEGORY_PAYMENT, "");
    }

    /**
     * 判断是否为默认其他应用
     *
     * @param cls NFC 模拟卡服务类
     */
    public static boolean isDefaultOther(@NonNull Context context, @NonNull Class<?> cls, String aid) {
        boolean defaultNFC = isDefaultNFC(context, cls, CardEmulation.CATEGORY_OTHER, aid);
        return defaultNFC;
    }

    /**
     * 判断是否为 NFC 默认应用
     *
     * @param cls NFC 模拟卡服务类
     */
    public static boolean isDefaultNFC(@NonNull Context context, @NonNull Class<?> cls, @NonNull String category, String aid) {
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
        ComponentName component = new ComponentName(context, cls);
        boolean defaultServiceForCategory = cardEmulation.isDefaultServiceForCategory(component, category);
        if (category.equals(CardEmulation.CATEGORY_OTHER)) {
            List<String> aidList = cardEmulation.getAidsForService(component, CardEmulation.CATEGORY_OTHER);
            boolean exist = false;
            if (aidList != null) {
                for (String existAid : aidList) {
                    if (aid.equals(existAid)) {
                        exist = true;
                        break;
                    }
                }
            }
            int selectionMode = cardEmulation.getSelectionModeForCategory(CardEmulation.CATEGORY_OTHER);
            // other 只返回 false，selectionMode 为 0 时是默认，但在 Android 系统中只有 Payment 才有默认，其他都是手动选择「实际测试即使不选择也是 true」
            // 所以 other 判断 aid 是否成功加入，如果成功加入则认为是默认，同时检查 selectionMode 是否为 ask 或 conflict
            boolean defaultOther = (selectionMode == CardEmulation.SELECTION_MODE_ALWAYS_ASK || selectionMode == CardEmulation.SELECTION_MODE_ASK_IF_CONFLICT);
            return defaultOther && exist;
        }
        return defaultServiceForCategory;
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
        setDefault(context, cls, CardEmulation.CATEGORY_PAYMENT);
    }

    public static void setDefaultOther(@NonNull Activity context, @NonNull Class<?> cls) {
        setDefault(context, cls, CardEmulation.CATEGORY_OTHER);
    }

    public static void setDefault(@NonNull Activity context, @NonNull Class<?> cls, @NonNull String category) {
        Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
        intent.putExtra(CardEmulation.EXTRA_CATEGORY, category);
        intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, new ComponentName(context, cls));
        context.startActivityForResult(intent, NFC_SET_DEFAULT_REQ_CODE);
    }

    /**
     * 动态注册服务 目前只支持 {@link CardEmulation#CATEGORY_OTHER}，如果注册两种不同的 类型，aid 重复会出现刷卡时选择应用的情况
     *
     * @param context context
     * @param cls     服务类
     * @param aids    aid 列表
     * @return
     */
    public static boolean registerDefault(@NonNull Context context, @NonNull Class<?> cls, @NonNull List<String> aids) {
        if (aids.isEmpty()) {
            return false;
        }
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
        return cardEmulation.registerAidsForService(new ComponentName(context, cls), CardEmulation.CATEGORY_OTHER, aids);
    }
}
