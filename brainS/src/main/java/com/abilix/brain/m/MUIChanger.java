package com.abilix.brain.m;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.abilix.brain.BrainActivity;
import com.abilix.brain.R;
import com.abilix.brain.utils.LogMgr;

/**
 * creater: yhd
 * created: 2018/1/12 09:25
 * 用于刷新界面
 */

public class MUIChanger {

    //M系列一级页面长时间没有操作
    private static final int M_LONG_TIME_NO_OPERATION = 10000;

    private static MUIChanger instance;

    public static MUIChanger getInstance() {
        // 单例
        if (instance == null) {
            synchronized (MUIChanger.class) {
                if (instance == null) {
                    instance = new MUIChanger();
                }
            }
        }
        return instance;
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    public enum MPicState {
        M_PIC_STATE_GONE,
        M_PIC_STATE_LANGUAGE,
        M_PIC_STATE_WIFI,
        M_PIC_STATE_FOR_ME,
        M_PIC_STATE_HELLO,
        M_PIC_STATE_HEART,
        M_PIC_STATE_HAPPY
    }

    public enum MGifState {
        M_STATE_GONE,
        M_STATE_HEART_JUMP,
        M_STATE_ANGRY,
        M_STATE_HEART,
        M_STATE_HAPPY,
        M_STATE_SINGING,
        M_STATE_SLEEP,
        M_STATE_NERVOUS,
        M_STATE_START;
    }

    /**
     * 切换M系列开机时
     * 语言
     * wifi
     * 自我介绍
     * 心跳页面
     * 开心页面
     * 的图片状态
     */
    public void changeMPicState(final MyGiftView giftView, final MPicState state) {
        if (giftView == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case M_PIC_STATE_GONE:
                        giftView.setVisibility(View.GONE);
                        break;

                    case M_PIC_STATE_LANGUAGE:
                        giftView.setImageResource(R.drawable.m_set_language);
                        break;

                    case M_PIC_STATE_WIFI:
                        giftView.setImageResource(R.drawable.m_set_wifi);
                        break;

                    case M_PIC_STATE_FOR_ME:
                        giftView.setImageResource(R.drawable.m_science_family);
                        break;

                    case M_PIC_STATE_HELLO:
                        giftView.setImageResource(R.drawable.m_icon_instruction_learning);
                        break;

                    case M_PIC_STATE_HEART:

                        break;

                    case M_PIC_STATE_HAPPY:
                        giftView.setImageResource(R.drawable.m_gif_4);
                        break;

                    default:
                        break;
                }

                if (!MPicState.M_PIC_STATE_GONE.equals(state)) {
                    giftView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    /**
     * 切换M系列情绪状态
     */
    public void changeMGifState(final MyGiftView giftView, final MGifState state) {

        if (giftView == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case M_STATE_HEART_JUMP:
                        giftView.setMovieResource(R.drawable.m_gif_1);
                        break;

                    case M_STATE_ANGRY:
                        giftView.setMovieResource(R.drawable.m_gif_2);
                        break;

                    case M_STATE_HEART:
                        giftView.setMovieResource(R.drawable.m_gif_3);
                        break;

                    case M_STATE_HAPPY:
                        giftView.setMovieResource(R.drawable.m_gif_4);
                        break;

                    case M_STATE_SINGING:
                        giftView.setMovieResource(R.drawable.m_gif_6);
                        break;

                    case M_STATE_SLEEP:
                        giftView.setMovieResource(R.drawable.m_gif_7);
                        break;

                    case M_STATE_NERVOUS:
                        giftView.setMovieResource(R.drawable.m_gif_8);
                        break;

                    case M_STATE_GONE:
                        giftView.setVisibility(View.GONE);
                        break;

                    case M_STATE_START:
                        giftView.setMovieResource(R.drawable.m_gif_9);
                        break;

                    default:
                        break;
                }

                if (!MGifState.M_STATE_GONE.equals(state) ) {
                    giftView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private java.lang.Runnable longTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (BrainActivity.getmBrainActivity().isForegroundFirst() && !MUtils.sIsProgram) {
                changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MGifState.M_STATE_HEART_JUMP);
                MSender.getInstance().startEmotion();
            }
        }
    };

    /**
     * M开始倒计时进入自主情绪
     */
    public void startAD() {
        LogMgr.d("FZXX","== startAD() ==   " );
        mHandler.removeCallbacks(longTimeRunnable);
        mHandler.postDelayed(longTimeRunnable, M_LONG_TIME_NO_OPERATION);
    }

    /**
     * M清空倒计时并且隐藏Gif
     */
    public void stopAD() {
        LogMgr.d("FZXX","== stopAD() ==   ");
        mHandler.removeCallbacks(longTimeRunnable);
        changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MGifState.M_STATE_GONE);
    }

    /**
     * 仅停止情绪倒计时，不隐藏Gif
     */
    public void stopAdOnly() {
        LogMgr.d("FZXX","== stopAdOnly() ==   ");
        mHandler.removeCallbacks(longTimeRunnable);
    }

    /**
     * M直接进入自主情绪
     */
    public void startEmotion() {
        LogMgr.d("FZXX","== startEmotion() ==   " );
        mHandler.removeCallbacks(longTimeRunnable);
        mHandler.post(longTimeRunnable);
    }

    /**
     * M清空情绪倒计时并且隐藏Gif
     */
    public void stopEmotion() {
        LogMgr.d("FZXX","== stopEmotion() ==   ");
        mHandler.removeCallbacks(longTimeRunnable);
        changeMGifState(BrainActivity.getmBrainActivity().getMyGiftView(), MGifState.M_STATE_GONE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MSender.getInstance().stopEmotion();
            }
        });
    }

    /**
     * 设置是否可以点击
     *
     * @param enable
     */
    public void setGifViewEnable(boolean enable) {
        if (BrainActivity.getmBrainActivity() != null && BrainActivity.getmBrainActivity().getMyGiftView() != null) {
            BrainActivity.getmBrainActivity().getMyGiftView().setEnabled(enable);
        }
    }
}
