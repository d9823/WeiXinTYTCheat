package liyi.com.test4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author: Seayon
 * @date: 2017/12/31
 * @time: 21:11
 */
public class Util {
    private static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private static final String WXGAME_URL = "https://mp.weixin.qq.com/wxagame/wxagame_settlement";
    private static final int TIMES = 1000;
    private static final String SESSIONID_ERROR = "SESSIONID有误，请检查";
    private static String game_data = null;
    private static final DecimalFormat decimalFormat1 = new DecimalFormat("#.###");
    private static final DecimalFormat decimalFormat2 = new DecimalFormat("#.##");
    private static final DecimalFormat decimalFormat3 = new DecimalFormat("###");

    private static String getActionData(String sessionKey, String encryptedData, String iv) {
        byte[] sessionKeyBy = sessionKey.getBytes();
        byte[] en = new byte[0];
        try {
            en = encryptedData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] ivBy = iv.getBytes();
        byte[] enc = Pkcs7Encoder.encryptOfDiyIV(en, sessionKeyBy, ivBy);
        return new String(Base64.encode(enc));
    }

    static {
        JSONArray action = new JSONArray();
        JSONArray musicList = new JSONArray();
        JSONArray touchList = new JSONArray();
        for (int i = 10000; i > 0; i--) {
            musicList.put(false);
            JSONArray actionData = new JSONArray();
            double first = Double.valueOf(decimalFormat1.format(Math.random()));
            Double second = Double.valueOf(decimalFormat2.format(Math.random() * 2));
            boolean booleFlag = i / 5000 == 0 ? true : false;
            try {
                actionData.put(first);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            actionData.put(second);
            actionData.put(booleFlag);
            action.put(actionData);
            JSONArray touchListData = new JSONArray();
            double tFirst = Double.valueOf(decimalFormat3.format(250 - (Math.random() * 10)));
            double tSecond = Double.valueOf(decimalFormat3.format(670 - (Math.random() * 20)));
            try {
                touchListData.put(tFirst);
                touchListData.put(tSecond);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            touchList.put(touchListData);
        }
        game_data = "action\":" + action.toString() + ",\"" +
                "musicList\":" + musicList.toString() + ",\"" +
                "touchList\":" + touchList.toString() + ",\"version\":1}";
    }

    public static String postData(String score, String session_id) {
        String result = null;
        JSONObject actionDataInfo = new JSONObject();
        try {
            actionDataInfo.put("score", Integer.valueOf(score));
            actionDataInfo.put("times", TIMES);
            actionDataInfo.put("game_data", "{\"seed\":" + System.currentTimeMillis() + "123" + ",\"" + game_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String AES_KEY = null;
        try {
            AES_KEY = session_id.substring(0, 16);
        } catch (Exception e) {
            return SESSIONID_ERROR;
        }

        String AES_IV = AES_KEY;
        OkHttpClient okHttpClient = new OkHttpClient();

        String actionData = Util.getActionData(AES_KEY, actionDataInfo.toString(), AES_IV);

        String json = "{\"base_req\":{\"session_id\":\"" + session_id + "\",\"fast\":1},\"action_data\":\"" + actionData + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(WXGAME_URL)
                .header("Accept", "*/*")
                .header("Accept-Language", "zh-cn")
                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Mobile/14E304 MicroMessenger/6.6.1 NetType/WIFI Language/zh_CN")
                .header("Content-Type", "application/json")
                .header("Referer", "https://servicewechat.com/wx7c8d593b2c3a7703/5/page-frame.html")
                .header("Host", "mp.weixin.qq.com")
                .header("Connection", "keep-alive")
                .post(requestBody)
                .build();
        ResponseBody responseBody = null;
        try {
            responseBody = okHttpClient.newCall(request).execute().body();
            result = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (responseBody != null) {
                responseBody.close();
            }
        }
        return result;
    }
}