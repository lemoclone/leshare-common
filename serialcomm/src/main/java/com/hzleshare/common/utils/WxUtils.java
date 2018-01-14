package com.hzleshare.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author hdf
 */
public class WxUtils {

    /**
     * 维系access_token参数名排序和拼接字符串并加密
     */
    public static String SHA1(String token) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(token.getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * {"errcode":0,"errmsg":"ok","ticket":"kgt8ON7yVITDhtdwci0qeTMM23utXN2TonGmGRPB1Fw9dXdBHNXHXmCyMLkdg6blZSg57xfvcIsd5Cf2gQyeRQ","expires_in":7200}
     */
    public static String getTicket(String accessToken) {
        String ticket = null;
        String url = String
            .format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi",
                accessToken);//这个url链接和参数不能变
        try {
            String responseBody = OkHttpUtils.get(url);
            if (StringUtils.isNotBlank(responseBody)) {
                JSONObject jsonObject = JSON.parseObject(responseBody);
                if (null == jsonObject || 0 != jsonObject.getObject("errcode", Integer.class)) {
                    return StringUtils.EMPTY;
                }
                ticket = jsonObject.getObject("ticket", String.class);
            }
        } catch (Exception e) {
            System.err.print(e.toString());
        }
        return ticket;
    }
}
