package com.anviz.googlepaynfc;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Pair;
import androidx.annotation.NonNull;
import com.anviz.logger.ConsoleLogger;
import com.anviz.logger.Logger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
//todo 正式版需要替换所有的log日志
public class NfcEmulatorService extends HostApduService {
    private static final String TAG = "NfcEmulatorService";

    private static final String HAC_KEY = "r#g#W#9THs9Y7@Sk";
    public static final String HAC_EXTRA = "UTec";

    private static final String DEFAULT_CMD = "00a4040005f123422221";

    private NfcHelper nfcHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        nfcHelper = NfcHelper.getInstance(getApplicationContext());
        if (BuildConfig.DEBUG) {
            Logger.getInstance().addLogger(new ConsoleLogger());
        }
        Logger.logD(TAG, "onCreate: ");
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Logger.logD(TAG, "process commadn apdu");
        long start = System.nanoTime();
        Pair<String, String> userIdCardNo = nfcHelper.getInfo();
        long spend = System.nanoTime() - start;
        Logger.logD(TAG, "getInfo spend: " + TimeUnit.NANOSECONDS.toMillis(spend) + "ms");

        if (userIdCardNo == null) {
            Logger.logD(TAG, "processCommandApdu: userIdCardNo is null");
            return new byte[]{0x00};
        }
        String receive = util_byte.byte2hex(commandApdu).replace(" ", "");
        Logger.logD(TAG, "processCommandApdu: " + receive);

        String uid = userIdCardNo.first; // 用于测试
        String cardNo = userIdCardNo.second;
        String command = "";

        Logger.logD(TAG, "userId: " + uid + " cardNo: " + cardNo);
        if (receive.equals(DEFAULT_CMD)) {
            byte[] bytes = util_byte.toByteArray(uid);
            Logger.logD(TAG, "send user id " + util_byte.byte2hex(bytes));
            return bytes;
        } else if (receive.startsWith("5369676e")) {
            byte[] randomByte = Arrays.copyOfRange(commandApdu, 4, 8);
            Logger.logD(TAG, "randomCount hex " + util_byte.byte2hex(randomByte));

            byte[] payloadBytesArray = generatePayload(commandApdu, uid, cardNo);
            Logger.logD(TAG, "payload " + util_byte.byte2hex(payloadBytesArray));
            long s1 = System.nanoTime();
            byte[] hmac = Codec.hmacSha256(HAC_KEY.getBytes(StandardCharsets.US_ASCII), payloadBytesArray);
            long s2 = System.nanoTime();
            Logger.logD(TAG, "hmac spend: " + TimeUnit.NANOSECONDS.toMillis(s2 - s1) + "ms");
            Logger.logD(TAG, "send: hmac" + util_byte.byte2hex(hmac));
            return hmac;
        }

        byte[] response = command.getBytes();
        Logger.logD(TAG, "send: " + util_byte.byte2hex(response));
        return response;
    }

    @NonNull
    private static byte[] generatePayload(byte[] commandApdu, String uid, String cardNo) {
        List<byte[]> payloadBytes = new ArrayList<>();
        payloadBytes.add(cardNo.getBytes());
        payloadBytes.add(Arrays.copyOfRange(commandApdu, 4, 8));
        payloadBytes.add(HAC_EXTRA.getBytes(StandardCharsets.US_ASCII));
        payloadBytes.add(util_byte.toByteArray(uid));

        int size = 0;
        for (byte[] bytes : payloadBytes) {
            size += bytes.length;
        }
        byte[] payloadBytesArray = new byte[size];
        int index = 0;
        for (byte[] bytes : payloadBytes) {
            System.arraycopy(bytes, 0, payloadBytesArray, index, bytes.length);
            index += bytes.length;
        }
        return payloadBytesArray;
    }

    @Override
    public void onDeactivated(int reason) {
        Logger.logD(TAG, "onDeactivated: " + reason);
    }
}
