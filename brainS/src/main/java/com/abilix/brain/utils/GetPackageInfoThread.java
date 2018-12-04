package com.abilix.brain.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.abilix.brain.Application;
import com.abilix.brain.BrainActivity;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.data.ApkVersionInfo;
import com.abilix.brain.data.AppInfo;
import com.abilix.brain.data.DataProcess;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取机器上安装APP信息线程。
 */
public class GetPackageInfoThread extends Thread {

    private static final String TAG = "GetPackageInfoThread";

    private String jsonString;
    private ApkVersionInfo apkVersionInfo;
    public GetPackageInfoThread(String json) {
        jsonString = json;
        Gson gson = new Gson();
        apkVersionInfo = gson.fromJson(json, ApkVersionInfo.class);
    }

    @Override
    public void run() {
        PackageManager pm = Application.getInstance().getPackageManager();
        try {
            List<ApkVersionInfo.DataBean> beanList = apkVersionInfo.getData();
            for (ApkVersionInfo.DataBean bean : beanList) {
                if ("stm32".equals(bean.getPackageName())) {
                    bean.setVerCode(Application.getInstance().getmFirewareVersion());
                } else {
                    PackageInfo info = pm.getPackageInfo(bean.getPackageName(), PackageManager.GET_ACTIVITIES|PackageManager.GET_SERVICES);
                    bean.setVerCode(info.versionCode);
                }
            }
            Gson gson = new Gson();
            jsonString = gson.toJson(apkVersionInfo);
            LogMgr.d("当前版本信息：" + jsonString);
            int length = jsonString.length();
            byte[] data = new byte[length + 2];
            data[0] = (byte) (length >> 8 & 0xFF);
            data[1] = (byte) (length & 0xFF);
            System.arraycopy(jsonString.getBytes(), 0, data, 2, length);
            byte[] sendData = ProtocolUtil.buildProtocol(
                    (byte) ProtocolUtil.getBrainType(),
                    GlobalConfig.BRAIN_UPGRADE_OUT_CMD_1,
                    GlobalConfig.BRAIN_UPGRADE_OUT_CMD_2_QUERY_VERSIONS_OK, data);
            DataProcess.GetManger().sendMsg(sendData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
