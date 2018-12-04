package com.abilix.brain.m;

import android.os.RemoteException;

import com.abilix.brain.Application;
import com.abilix.brain.BrainActivity;
import com.abilix.brain.BrainInfo;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.utils.LogMgr;
import com.abilix.brain.utils.SharedPreferenceTools;
import com.abilix.brain.utils.Utils;
import com.abilix.learn.oculus.distributorservice.ProtocolBean;

import java.util.Locale;

/**
 * creater: yhd
 * created: 2018/1/12 09:24
 */

public class MReceiver {

    private static MReceiver instance;
    private MUIChanger mMUIChanger;

    public MReceiver() {
        mMUIChanger = MUIChanger.getInstance();
    }

    public static MReceiver getInstance() {
        // 单例
        if (instance == null) {
            synchronized (MReceiver.class) {
                if (instance == null) {
                    instance = new MReceiver();
                }
            }
        }
        return instance;
    }

    public void handleStatus(ProtocolBean protocolBean) throws RemoteException {

        int orderId = protocolBean.getOrderId();
        String content = protocolBean.getContent();
        byte[] protocolData = protocolBean.getProtocolData();

        LogMgr.d("FZXX", String.format(Locale.US, "=== 处理状态 handleStatus() === orderId: %d", orderId));
        switch (orderId) {

            /*case MUtils.B_UI_A:
                //mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_LANGUAGE);
                break;

            case MUtils.B_UI_B:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_WIFI);
                break;

            case MUtils.B_UI_C:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_FOR_ME);
                break;

            case MUtils.B_UI_D:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_LANGUAGE);
                break;

            case MUtils.B_UI_E:
               mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_HEART);
                break;*/
            //显示引导图标
            case MUtils.B_UI_WIFI:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_WIFI);
                break;

            case MUtils.B_UI_FACE:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_FOR_ME);
                break;

            case MUtils.B_UI_HELLO:
                mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_HELLO);
                break;

            case MUtils.B_UI_ACTION:
            case MUtils.B_UI_A:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_HEART_JUMP);

                break;
            case MUtils.B_UI_B:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_ANGRY);
                break;
            case MUtils.B_UI_C:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_HEART);
                break;
            case MUtils.B_UI_F:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_SINGING);
                break;
            case MUtils.B_UI_G:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_SLEEP);
                break;
            case MUtils.B_UI_H:
                mMUIChanger.changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MGifState.M_STATE_NERVOUS);
                break;

            case MUtils.B_GAME_A:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), MUtils.APP_GANYAZI)) {
                    Utils.openApp(BrainActivity.getmBrainActivity(), MUtils.APP_GANYAZI, null, false);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = true;
                } else {
                    //发送错误
                    MSender.getInstance().handActionForBean(MUtils.B_GAMEA_ERROR);
                }
                break;
            case MUtils.B_GAME_B:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), MUtils.APP_ECHOPIANO)) {
                    Utils.openApp(BrainActivity.getmBrainActivity(), MUtils.APP_ECHOPIANO, null, false);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = true;
                } else {
                    MSender.getInstance().handActionForBean(MUtils.B_GAMEB_ERROR);
                }
                break;
            case MUtils.B_GAME_C:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), MUtils.APP_LEARNEN)) {
                    Utils.openApp(BrainActivity.getmBrainActivity(), MUtils.APP_LEARNEN, null, false);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = true;
                } else {
                    MSender.getInstance().handActionForBean(MUtils.B_GAMEC_ERROR);
                }
                break;
            case MUtils.B_GAME_D:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), MUtils.APP_YUEGAN)) {
                    MUtils.openApp(BrainActivity.getmBrainActivity(), MUtils.APP_YUEGAN, null, false, MUtils.OCULUS_APP, false, false);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = true;
                } else {
                    MSender.getInstance().handActionForBean(MUtils.B_GAMED_ERROR);
                }
            case MUtils.B_GAME_E:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), MUtils.APP_YUEGAN)) {
                    MUtils.openApp(BrainActivity.getmBrainActivity(), MUtils.APP_YUEGAN, null, false, MUtils.OCULUS_APP, true, false);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = true;
                } else {
                    MSender.getInstance().handActionForBean(MUtils.B_GAMED_ERROR);
                }
                break;
            case MUtils.B_SHOW_BATTERY:
                MUtils.startBrainSet(MUtils.FRAGMENT_ID_BATTERY, false, false);
                break;
            case MUtils.B_HIDE_BATTERY:
                //发送广播停止BrainSet
                MUtils.sIsYuyinControl = true;
                MUtils.closeBrainSet();
                MUtils.startBrainActivity(MUtils.BRAIN_PKG_NAME, MUtils.BRAIN_ACTIVITY_NAME);
                break;
            case MUtils.B_SHOW_VOICE_PICKER_CANTTOUCH:
                MUtils.startBrainSet(MUtils.FRAGMENT_ID_VOLUME, false, false);
                break;
            case MUtils.B_HIDE_VOICE_PICKER_CANTTOUCH:
                //发送广播停止BrainSet
                MUtils.sIsYuyinControl = true;
                MUtils.closeBrainSet();
                MUtils.startBrainActivity(MUtils.BRAIN_PKG_NAME, MUtils.BRAIN_ACTIVITY_NAME);
                break;

            case MUtils.B_BACK_TO_BRAIN_MAINACTIVITY:
                //发送广播停止BrainSet
                MUtils.sIsYuyinControl = true;
                //MUtils.closeBrainSet();
                //MUtils.startBrainActivity(MUtils.BRAIN_PKG_NAME, MUtils.BRAIN_ACTIVITY_NAME);
                break;


            case MUtils.B_TOAPP:
                MUtils.sIsYuyinControl = true;
                break;

            case MUtils.B_BACKAPP:
                MUtils.sIsYuyinControl = true;
                break;

            case MUtils.B_INIT_COMPLETE:
                if (!MUtils.sIsBootspeakingFinish && !MUtils.isBrainFirstStart()) {
                    mMUIChanger.startEmotion();
                }
                break;

            case MUtils.B_WIFI:
                MUtils.startBrainSet(MUtils.FRAGMENT_ID_WIFI, true, false);
                break;

            case MUtils.B_GUIDE_LANGUAGE:
                MUtils.startBrainSet(MUtils.FRAGMENT_ID_LANGUAGE, true, true);
                break;

            case MUtils.B_GUIDE_WIFI:
                MUtils.startBrainSet(MUtils.FRAGMENT_ID_WIFI, true, true);
                break;

            case MUtils.B_GUIDE_FACE:
                if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), GlobalConfig.APP_PKGNAME_MY_FAMILY)) {
                    MUtils.openApp(BrainActivity.getmBrainActivity(), GlobalConfig.APP_PKGNAME_MY_FAMILY, null, false, null, false, true);
                    MUtils.sIsAppBack = true;
                    MUtils.sIsVoiceAppBack = false;
                } else {
                    mMUIChanger.changeMPicState(BrainActivity.getmBrainActivity().getMyGiftView(), MUIChanger.MPicState.M_PIC_STATE_GONE);
                }
                break;

            case MUtils.B_GUIDE_DONE:
                //引导程序结束
                MUtils.setIsFirstStart(false);
                mMUIChanger.setGifViewEnable(true);
                break;

            case MUtils.M_RECEIVCE_PROTOCOL:

                //接收协议,转发给PAD
                if (BrainInfo.Builder.getBrainInfo() != null  && protocolData != null) {
                    BrainInfo.Builder.getBrainInfo().returnDataToClient(protocolData);
                    LogMgr.d("FZXX","== 接收我叫奥科流思2.0协议,转发给PAD ==");
                    LogMgr.d("FZXX","== data :  " + Utils.bytesToString(protocolData) + "  ==");
                }
                break;

            //图灵激活状态处理
            case MUtils.TULING_INIT_NONET:
            case MUtils.TULING_INIT_ERRORSN:
            case MUtils.TULING_ERROR_CODE:
            case MUtils.TULING_INIT_SUCCES:
                MUtils.errCode = orderId;
                MUtils.errMsg = content;
                if (orderId == MUtils.TULING_INIT_SUCCES) {
                    SharedPreferenceTools.saveBoolean(Application.getInstance(),
                            SharedPreferenceTools.KEY_TURING_REGISTER_STATE,
                            true);
                }
                break;

            default:

                break;
        }
    }


}
