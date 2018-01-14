package com.hzleshare.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureUtil {

    public static String getSign(SortedMap<String, String> map, String secretKey) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String value = map.get(key);
            if ("sign".equals(key) || StringUtils.isEmpty(value)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        sb.append("&key=").append(secretKey);
        String signMd5Str = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        return signMd5Str;
    }

    /**
     * 生成sign HMAC-SHA256 或 MD5 签名
     *
     * @param map         map
     * @param paternerKey paternerKey
     * @return sign
     */
    public static String getSign(Map<String, String> map, String paternerKey) {
        return getSign(map, null, paternerKey);
    }

    /**
     * 生成sign HMAC-SHA256 或 MD5 签名
     *
     * @param map         map
     * @param sign_type   HMAC-SHA256 或 MD5
     * @param paternerKey paternerKey
     * @return sign
     */
    public static String getSign(Map<String, String> map, String sign_type, String paternerKey) {
        Map<String, String> tmap = MapUtil.order(map);
        if (tmap.containsKey("sign")) {
            tmap.remove("sign");
        }
        String str = MapUtil.mapJoin(tmap, false, false);
        if (sign_type == null) {
            sign_type = tmap.get("sign_type");
        }
        if ("HMAC-SHA256".equalsIgnoreCase(sign_type)) {
            try {
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(paternerKey.getBytes("UTF-8"), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                return Hex.encodeHexString(sha256_HMAC.doFinal((str + "&key=" + paternerKey).getBytes("UTF-8"))).toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {//default MD5
            return DigestUtils.md5Hex(str + "&key=" + paternerKey).toUpperCase();
        }
    }

    /**
     * 生成事件消息接收签名
     *
     * @param token     token
     * @param timestamp timestamp
     * @param nonce     nonce
     * @return str
     */
    public static String generateEventMessageSignature(String token, String timestamp, String nonce) {
        String[] array = new String[]{token, timestamp, nonce};
        Arrays.sort(array);
        //String s = StringUtils.arrayToDelimitedString(array, "");
        return DigestUtils.shaHex("s");
    }

    /**
     * mch 支付、代扣异步通知签名验证
     *
     * @param map 参与签名的参数
     * @param key mch key
     * @return boolean
     */
    public static boolean validateSign(Map<String, String> map, String key) {
        return validateSign(map, null, key);
    }

    /**
     * mch 支付、代扣API调用签名验证
     *
     * @param map       参与签名的参数
     * @param sign_type HMAC-SHA256 或 MD5
     * @param key       mch key
     * @return boolean
     */
    public static boolean validateSign(Map<String, String> map, String sign_type, String key) {
        if (map.get("sign") == null) {
            return false;
        }
        return map.get("sign").equals(getSign(map, sign_type, key));
    }


    /**
     * 随便码
     *
     * @return
     */
    public static String getNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 时间戳
     * 单位: 秒
     *
     * @return
     */
    public static String getTimeStamp() {
        Long time = System.currentTimeMillis();
        BigDecimal m = new BigDecimal(time).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
        return m.toString();
    }

    public static String mapToXml(SortedMap<String, String> params) {
        StringBuilder sb = new StringBuilder("<xml>");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            sb.append("<").append(key).append(">");
            sb.append("<![CDATA[").append(value).append("]]>");
            sb.append("</").append(key).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
