package com.abilix.brain.m;

import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.abilix.brain.BrainService;
import com.abilix.brain.utils.LogMgr;
import com.abilix.brain.utils.Utils;
import com.abilix.learn.oculus.distributorservice.ProtocolBean;

import java.util.Locale;

/**
 * creater: yhd
 * created: 2018/1/12 09:24
 */

public class MSender {

    private static MSender instance;

    public static MSender getInstance() {
        // 单例
        if (instance == null) {
            synchronized (MSender.class) {
                if (instance == null) {
                    instance = new MSender();
                }
            }
        }
        return instance;
    }

    /**
     * 传递给FZXX一个开始情绪的状态
     */
    public synchronized void startEmotion() {
        LogMgr.d("FZXX", "=== 开始情绪 ===");
        handActionForBean(MUtils.START_EMOTION);
    }

    /**
     * 传递给FZXX一个关闭情绪的状态
     */
    public void stopEmotion() {
        LogMgr.d("FZXX", "=== 關閉情绪 ===");
        handActionForBean(MUtils.EXIT_EMOTION);
    }


    /**
     * @param orderId 传入OrderId就可以发送消息给我叫奥科流思服务
     */
    public void handActionForBean(int orderId) {
        ProtocolBean protocolBean = new ProtocolBean();
        protocolBean.setOrderId(orderId);
        handActionForBean(protocolBean);
    }

    /**
     * @param orderId
     */
    public void handActionForBean(int orderId, String content, byte[] protocolData) {
        ProtocolBean protocolBean = new ProtocolBean();
        protocolBean.setOrderId(orderId);
        protocolBean.setContent(content);
        protocolBean.setProtocolData(protocolData);

        handActionForBean(protocolBean);
    }

    private void handActionForBean(@NonNull ProtocolBean protocolBean) {
        try {
            LogMgr.d("FZXX", String.format(Locale.US, "=== 处理发送状态 handActionForBean() === orderId:0x%x content:%s data:%s",
                    protocolBean.getOrderId(), protocolBean.getContent(), Utils.bytesToString(protocolBean.getProtocolData())));
            if (BrainService.getmBrainService() != null && BrainService.getmBrainService().getMService() != null) {
                BrainService.getmBrainService().getMService().handAction(protocolBean);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            LogMgr.e("FZXX", "== Error ==" + e.toString());
        }
    }
}
