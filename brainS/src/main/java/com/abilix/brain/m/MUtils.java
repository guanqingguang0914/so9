package com.abilix.brain.m;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.text.TextUtils;

import com.abilix.brain.BrainActivity;
import com.abilix.brain.BrainService;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.utils.LogMgr;
import com.abilix.brain.utils.ProtocolUtil;
import com.abilix.brain.utils.SharedPreferenceTools;
import com.abilix.brain.utils.Utils;

import java.util.Iterator;
import java.util.List;

/**
 * creater: yhd
 * created: 2017/12/1 13:43
 */

public class MUtils {
    public static final String FILTER_BRAINSET_BACK = "filter.brainset.back";//从Brainset返回

    public static final String PKG_NAME_AI = "com.abilix.learn.ai.";  //未来科学站需要放的包

    public static final String FILTER_OPEN_LEADSET = "com.abilix.openleadset";//打开开机引导

/*=========================================== 改版后状态 ===================================================================*/

/*
    public static final int B_UI_A = 3;//语言
    public static final int B_UI_B = 4;//wifi
    public static final int B_UI_C = 5;//自我介绍
*/

    public static final int M_RECEIVCE_PROTOCOL = 0x99001000;//接收M协议,并发送给PAD
    public static final int M_SEND_PROTOCOL = 0x99001100;//接收PAD协议,并发送给M

    //图灵激活状态处理
    public static final int TULING_INIT_NONET=71;//图灵激活没有网络
    public static final int TULING_INIT_ERRORSN=72;//sn码错误
    public static final int TULING_ERROR_CODE=73;//图灵错误  （有错误信息会附加错误信息）
    public static final int TULING_INIT_SUCCES=74;//图灵激活成功

    /*=========================================== 改版后状态 ===================================================================*/


/* ========================================= 改版前状态 ======================================================== */

    //收到的状态码
    public static final int B_UI_A = 3;//心跳页面
    public static final int B_UI_B = 4;//生气页面
    public static final int B_UI_C = 5;//爱心页面
//    public static final int B_UI_D = 6;//心跳页面
//    public static final int B_UI_E = 7;//开心页面
    public static final int B_UI_F = 8;//唱歌页面
    public static final int B_UI_G = 9;//休眠页面
    public static final int B_UI_H = 10;//紧张页面
    public static final int B_GAME_A = 11;//进入赶鸭子小应用
    public static final int B_GAME_B = 12;//进入超声琴小应用
    public static final int B_GAME_C = 13;//进入学英语小应用
    public static final int B_GAME_D = 14;//进入乐感达人小应用 false
    public static final int B_GAME_E = 15;//跳舞启动的与感达人  true
    public static final int B_SHOW_BATTERY = 16;//展示电池UI
    public static final int B_HIDE_BATTERY = 17;//隐藏电池UI
    public static final int B_SHOW_VOICE_PICKER_CANTTOUCH = 18;//展示一个不能点击触摸的声音UI
    public static final int B_HIDE_VOICE_PICKER_CANTTOUCH = 19;//隐藏这个不能点击触摸的声音UI
    public static final int B_BACK_TO_BRAIN_MAINACTIVITY = 20;//回到brain的MainActivity
    public static final int B_TOAPP = 21;//进入拍照漫游activity
    public static final int B_BACKAPP = 22;//退出拍照漫游activity
    public static final int B_INIT_COMPLETE = 23;//初始化完成
    public static final int B_GUIDE_LANGUAGE = 24;//进入语言设置
    public static final int B_GUIDE_WIFI = 25;//wifi设置
    public static final int B_GUIDE_FACE = 26;//人脸录入
    public static final int B_GUIDE_DONE = 27;//引导完成
    public static final int B_UI_WIFI=30;//设置wifi的ui
    public static final int B_UI_FACE=31;//设置人脸录入的ui
    public static final int B_UI_HELLO=32;//say hello
    public static final int B_UI_ACTION=33;//语音动作的ui
    public static final int B_WIFI = 200;//进入WIfi
    public static final int B_OTHER_APP = 201;  //进入其他APP

    //发送的状态码
    public static final int S_GUIDE = 0x05001100;//系统状态-- 开机引导
    public static final int S_LANGUAGE_DONE = 0x05001300;//开机引导-- 语言设置完成
    public static final String S_LANGUAGE_DONE_FILTER = "filter.language.back";//开机引导-- 语言设置完成
    public static final int S_WIFI_SUCCESS = 0x05001500;//开机引导-- wifi设置完成
    public static final String S_WIFI_DONE_FILTER = "filter.wifi.back";//开机引导-- wifi设置完成
    public static final int S_FACEINPUTDONE = 0x05001800;//开机引导--人脸录入完成
    public static final String S_FACE_DONE_FILTER = "filter.face.back";//开机引导-- 人脸录入完成

    public static final int START_EMOTION = 0x05000300;  //開始情緒
    public static final int EXIT_EMOTION = 0x05000400;  //關閉情緒
    public static final int B_GAMEA_ERROR = 0x05000600;//进入赶鸭子小应用失败
    public static final int B_GAMEB_ERROR = 0x05000700;//进入超声琴小应用失败
    public static final int B_GAMEC_ERROR = 0x05000800;//进入学英语小应用失败
    public static final int B_GAMED_ERROR = 0x05000900;//进入乐感达人小应用失败
    public static final int B_GO_TO_APP = 0x05000A00;//brain进入其他应用
    public static final int B_BACK_TO_BRAIN = 0x05000B00;//返回brain
    public static final int B_CLICK_GO_TO_APP = 0x0500C00;//brain进入其他应用(手点)
    public static final int B_CLICK_BACK_TO_BRAIN = 0x05000D00;//返回brain(手点)
    public static final int B_BRAIN_ENTER_PROGRAMMING_MODE = 0x05002000;//brain进入编程模式
    public static final int B_BRAIN_QUIT_PROGRAMMING_MODE = 0x05002100;//brain退出编程模式

    public static final int S_PROGRAM = 0x05002000;//编程连接
    public static final int S_PROGRAM_DONE = 0x05002100;//编程断开
    public static final int S_PROGRAM_STOP = 0x05002200;//编程暂停
    public static final int S_PROGRAM_START = 0x05002300;//编程继续


    //内置应用
    public static final String APP_GANYAZI = "com.abilix.ganyazi";
    public static final String APP_ECHOPIANO = "com.abilix.learn.echopiano";
    public static final String APP_LEARNEN = "com.abilix.learnenglish";
    public static final String APP_YUEGAN = "com.abilix.learn.m_musicking";
    public static final String OCULUS_APP = "oculus_app";  //乐感达人附加的信息

    public static final String BRAIN_PKG_NAME = "com.abilix.brain";
    public static final String BRAIN_ACTIVITY_NAME = "com.abilix.brain.BrainActivity";
    public static final String BRAINSET_MANAGEFRAGMENT = "com.abilix.brainset.ManageFragment";

    //进入BrainSet的ID
    public static final int FRAGMENT_ID_WIFI = 0x00;
    public static final int FRAGMENT_ID_LANGUAGE = 0x01;
    public static final int FRAGMENT_ID_ABOUT = 0x02;
    public static final int FRAGMENT_ID_BATTERY = 0x03;
    public static final int FRAGMENT_ID_VOLUME = 0x04;
    public static final int FRAGMENT_ID_CLEAR = 0x05;
    public static final int FRAGMENT_ID_UPDATE = 0x06;

/* =============================================== 改版前状态 ============================================================ */

    //第一次启动Brain
    public static final String SP_FIRST_START_BRAIN = "SP_FIRST_START_BRAIN";

    //
    public static boolean sIsAppBack = false;
    public static boolean sIsVoiceAppBack = false;  //是否是语音开启APP  ,true代表语音开启,false代表点击开启
    public static boolean sIsFirstResume = true;  //是否是第一次走BrainActivity的Resume方法
    public static boolean sIsYuyinControl; //是语音控制返回
    public static boolean sIsBingdingFinish = false;
    public static boolean sIsBootspeakingFinish = false;
    public static boolean sIsProgram = false; //是否是编程模式

    public static int errCode = 0;
    public static String errMsg;

    public static void closeBrainSet() {
        Intent intent = new Intent();
        intent.setAction("com.abilix.brainset.closeapp");
        intent.putExtra(Utils.INTENT_PACKAGE_NAME, GlobalConfig.APP_PKGNAME_BRAINSET);
        BrainActivity.getmBrainActivity().sendBroadcast(intent);
    }


    public static void startBrainActivity(String pkgName, String activityName) {
        ComponentName componentName = new ComponentName(pkgName, activityName);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        BrainActivity.getmBrainActivity().startActivity(intent);
    }

    public static void startBrainSet(int id, boolean canClick, boolean isGuide) {
        ComponentName componentName = new ComponentName(GlobalConfig.APP_PKGNAME_BRAINSET, BRAINSET_MANAGEFRAGMENT);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.putExtra("id", id);
        intent.putExtra("type", GlobalConfig.ROBOT_TYPE_M);
        intent.putExtra("canClick", canClick);
        intent.putExtra("isGuide", isGuide);
        BrainActivity.getmBrainActivity().startActivity(intent);
    }


    /**
     * @return Brain是否是第一次启动
     */
    public static boolean isBrainFirstStart() {
        return SharedPreferenceTools.getBoolean(BrainActivity.getmBrainActivity(), MUtils.SP_FIRST_START_BRAIN, true);
    }

    /**
     * @return Brain是否是第一次启动
     */
    public static void setIsFirstStart(boolean isFirst) {
        SharedPreferenceTools.saveBoolean(BrainActivity.getmBrainActivity(), MUtils.SP_FIRST_START_BRAIN, isFirst);
    }

    /**
     * @return BrainService 和 M绑定的aidl是否不为空
     */
    public static boolean isBrainServiceAndMAidlNotNull() {
        return BrainService.getmBrainService() != null && BrainService.getmBrainService().getMService() != null;
    }

    /**
     * @return 是否已经说完开机语音并且已经绑定M的aidl
     */
    public static boolean isSpeakAndBindingFinish() {
        return MUtils.sIsBootspeakingFinish && MUtils.sIsBingdingFinish;
    }

    /**
     * 打开app 使用Intent.FLAG_ACTIVITY_NEW_TASK
     * 用于从Brain页面点击打开应用
     *
     * @param packageName
     * @param gotoWifiPage 打开设置时，是否打开wifi界面
     */
    public static void openApp(Context context, String packageName, String activityName, boolean gotoWifiPage, String extraName, boolean add, boolean isGuide) {
        LogMgr.i("Brain Utils openApp() packageName = " + packageName);
        if (!TextUtils.equals(packageName, GlobalConfig.APP_PKGNAME_BRAINSET)
                && !TextUtils.equals(packageName, GlobalConfig.APP_PKGNAME_UPDATEONLINETEST)
                && !TextUtils.equals(packageName, GlobalConfig.APP_PACKAGE_NAME_UPDATE_STM32_APK)) {
            //打开赵辉那边应用前 先关闭掉串口
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (GlobalConfig.BRAIN_CHILD_TYPE == GlobalConfig.ROBOT_TYPE_CU) {
                            long time = System.currentTimeMillis();
                            while (System.currentTimeMillis() - time < 1500) {
                            }
                        }
                        LogMgr.e("关闭串口");
                        if (BrainService.getmBrainService() != null & BrainService.getmBrainService().getControlService() != null) {
                            BrainService.getmBrainService().getControlService().destorySP();
                            //启动M系列小应用的时候需要发送状态值给他们
                            if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_M
                                    && BrainService.getmBrainService().getMService() != null) {
                                MSender.getInstance().handActionForBean(MUtils.B_GO_TO_APP);
                                LogMgr.d("FZXX", "handAction(MUtils.B_GO_TO_APP)");
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            //打开设置应用 如果Control循环读取串口的功能被停止，发送一条命令打开
            BrainService.getmBrainService().sendMessageToControl(GlobalConfig.CONTROL_CALLBACKMODE_OLD_PROTOCOL,
                    ProtocolUtil.buildProtocol((byte) GlobalConfig.BRAIN_TYPE, (byte) 0x11, (byte) 0x09, null), null, 0, 0);
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            PackageManager pm = context.getApplicationContext().getPackageManager();

            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);

            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = null;
            Iterator<ResolveInfo> it = apps.iterator();
            while (it.hasNext()) {
                ri = it.next();
                LogMgr.d("==Utils==", ri.activityInfo.name);
                if (ri.activityInfo.packageName.equals(packageName) &&
                        activityName != null && ri.activityInfo.name.equals(activityName)) {
                    break;
                }
            }
            if (ri != null) {
                String pkName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (packageName.equals(GlobalConfig.APP_PKGNAME_BRAINSET) && gotoWifiPage) {
//                    forceStopApp(context, packageName);
                    intent.putExtra(GlobalConfig.BRAIN_SET_GOTO_WIFI_PAGE, gotoWifiPage);
                }

                ComponentName cn = new ComponentName(pkName, className);

//                if (!TextUtils.isEmpty(filePath)) {
//                    intent.putExtra(GlobalConfig.CHART_APP_FILE_PATH_EXTRA_NAME, filePath);
//                }
//
//                if (!TextUtils.isEmpty(pageName)) {
//                    intent.putExtra(GlobalConfig.CHART_APP_PAGE_NAME_EXTRA, pageName);
//                }

                intent.setComponent(cn);
                intent.putExtra(extraName, add);
                intent.putExtra("IS_GUIDE", isGuide);


//                if (GlobalConfig.CHART_AND_PROJECT_PROGRAM_APP_PACKAGE_NAME.equals(packageName)) {
//                    LogMgr.d("start explainer");
//                    ((BrainActivity) context).startActivityForResult(intent, BrainActivity
// .REQUEST_CODE_FOR_VJC_AND_PROGRAM_JROJECT);
//                } else {
                context.startActivity(intent);
//                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogMgr.e("getPackInfo failed for package " + packageName);
        }
    }



}

