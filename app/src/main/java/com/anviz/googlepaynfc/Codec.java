package com.anviz.googlepaynfc;

import androidx.annotation.NonNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * byte数组 字符串 hex
 * Hash hMac 等操作
 */
public class Codec {
    public static final String H_MAC_SHA_1 = "HmacSHA1";
    public static final String H_MAC_SHA_256 = "HmacSHA256";

    @NonNull
    public static byte[] hmacSha1(byte[] key, byte[] value) {
        return hMac(key, value, H_MAC_SHA_1);
    }

    @NonNull
    public static byte[] hmacSha256(byte[] key, byte[] value) {
        return hMac(key, value, H_MAC_SHA_256);
    }

    /**
     * 根据 key 和 value 计算 HmacSHA1 的值。
     * 与硬件端校验的时候，需要注意，硬件端的编码方式 charset 可能是 ASCII。
     *
     * @param KEY      密钥
     * @param VALUE    值
     * @param SHA_TYPE 具体算法，如 {@link  #H_MAC_SHA_1} {@link #H_MAC_SHA_256}
     */
    @NonNull
    private static byte[] hMac(byte[] KEY, byte[] VALUE, String SHA_TYPE) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(KEY, SHA_TYPE);
            Mac mac = Mac.getInstance(SHA_TYPE);
            mac.init(signingKey);
            return mac.doFinal(VALUE);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
