package com.abilix.explainer.utils;

import com.abilix.brain.BrainService;
import com.abilix.brain.GlobalConfig;

import java.util.Locale;

public class ProtocolUtils {

    /**
     * 数据头 不需要返回
     */
    public final static int DATA_HEAD = 0x55;

    /**
     * 数据头 返回
     */
    public final static int DATA_HEAD_ = 0x56;

    /**
     * 1 ONE
     */
    public final static int DATA_ONE = 0x01;

    /**
     * 2 TWO
     */
    public final static int DATA_TWO = 0x02;

    /**
     * 3 THREE
     */
    public final static int DATA_THREE = 0x03;

    /**
     * 4 FOUR
     */
    public final static int DATA_FOUR = 0x04;

    /**
     * 5 FIVE
     */
    public final static int DATA_FIVE = 0x05;

    /**
     * 6 SIX
     */
    public final static int DATA_SIX = 0X06;

    /**
     * 0 ZERO
     */
    public final static int DATA_ZERO = 0x00;
    /**
     * 轮子协议
     */
    public final static byte A = 'A';

    public final static byte B = 'B';

    public final static byte C = 'C';

    public final static byte D = 'D';

    public final static byte E = 'E';

    public final static byte F = 'F';

    public final static byte G = 'G';

    public final static byte H = 'H';

    public final static byte I = 'I';

    public final static byte J = 'J';

    public final static byte K = 'K';

    public final static byte L = 'L';

    public final static byte M = 'M';

    public final static byte N = 'N';

    public final static byte O = 'O';

    public final static byte P = 'P';

    public final static byte Q = 'Q';

    public final static byte DR = 'R';

    public final static byte S = 'S';

    public final static byte T = 'T';

    public final static byte U = 'U';

    public final static byte V = 'V';

    public final static byte W = 'W';

    public final static byte X = 'X';

    public final static byte Y = 'Y';

    public final static byte Z = 'Z';

    /**
     * 轮子协议
     */
    public static byte[] WHEELBYTE = {DATA_HEAD, B, L, E, S, E, T, 0X03, 100,
            100, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};

    /**
     * 头部
     */
    public static byte[] HEADBYTE = {DATA_HEAD, N, E, C, K, M, O, T, 0X00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /**
     * 设置眼睛颜色
     */
    public static byte[] EYE_COLOR = {DATA_HEAD, E, Y, 1, 0X00, 0X00, 0X00,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
            0X00, 0X00};

    /**
     * 设置眼睛数量
     */
    public static byte[] EYE_COUNT = {DATA_HEAD, E, Y, 2, 0X00, 0X00, 0X00,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00,
            0X00, 0X00};

    /**
     * 设置颜色
     */
    public static byte[] COLOR = {DATA_HEAD, C, O, L, E, D, 0X00, 0X00, 0X00,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};

    /**
     * 设置亮度
     */
    public static byte[] LUMINANCE = {DATA_HEAD, L, U, M, I, N, A, N, C, E,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};

    /**
     * 设置波形
     */
    public static byte[] WAVEMODE = {DATA_HEAD, W, A, V, E, F, O, DR, M, 0X00,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};

    /**
     * 设置吸尘
     */
    public static byte[] VACUUM = {DATA_HEAD, B, L, E, S, E, T, 0X04, 0X00,
            0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};
    /**
     * 超声波 协议
     */
    public static final byte[] mUltrasonicByte = {DATA_HEAD_, A, I, DR, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    /**
     * 下视 协议
     */
    public static final byte[] mDown_WatchByte = {DATA_HEAD_, L, O, O, K, D,
            O, W, N, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00};

    /**
     * 后端红外测距
     */
    public static final byte[] mRear_End_InfraredByte = {DATA_HEAD_, I, N, F,
            DR, A, 0x00, 0x00, 0X00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    /**
     * 碰撞测试
     */
    public static final byte[] mCrashInfraredByte = {DATA_HEAD_, C, O, L, L,
            I, S, I, O, N, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00};
    /**
     * 地面灰度 打开
     */
    public static final byte[] mGround_Grayscale_OpenByte = {DATA_HEAD, G, DR,
            A, Y, O, P, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00};
    /**
     * 地面灰度 发送
     */
    public static final byte[] mGround_Grayscale_SendByte = {DATA_HEAD_, G,
            DR, A, Y, DR, D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    /**
     * read缓冲区长度
     */
    private static final int BUFF_LENGTH = 20;

    /**
     * request超时时间 ms
     */
    private static final int REQUEST_TIMEOUT = 100;

    /**
     * request超时重发次数
     */
    private static final int RETRY_TIMES = 3;

    /**
     * 构造传输命令
     *
     * @param type 1个字节，代表不同的上层(机器人)型号
     * @param cmd1 1个字节，代表主命令
     * @param cmd2 1个字节，代表子命令
     * @param data 可变长度 null表示不带数据
     * @return
     */
    public static byte[] buildProtocol(byte type, byte cmd1, byte cmd2, byte[] data) {
        byte[] sendBuff;
        if (data == null) {
            byte[] buff = new byte[8];
            buff[0] = type;
            buff[1] = cmd1;
            buff[2] = cmd2;
            sendBuff = addProtocol(buff);
        } else {
            byte[] buff = new byte[8 + data.length];
            buff[0] = type;
            buff[1] = cmd1;
            buff[2] = cmd2;
            System.arraycopy(data, 0, buff, 7, data.length);
            sendBuff = addProtocol(buff);
        }

        return sendBuff;
    }

    /**
     * 增加协议头前两个字节为AA 55，三，四字节为长度，最后一个字节为校验位
     * 协议封装： AA 55 len1 len2 type cmd1 cmd2 00 00 00 00 (data) check
     * @param buff 类型，命令字1，命令字2，保留字，数据位
     * @return
     */
    private static byte[] addProtocol(byte[] buff) {
        short len = (short) (buff.length);
        byte[] sendbuff = new byte[len + 4];
        sendbuff[0] = (byte) 0xAA; // 头
        sendbuff[1] = (byte) 0x55;
        sendbuff[3] = (byte) (len & 0x00FF); // 长度: 从type到check
        sendbuff[2] = (byte) ((len >> 8) & 0x00FF);
        System.arraycopy(buff, 0, sendbuff, 4, buff.length); // type - data

        byte check = 0x00; // 校验位
        for (int n = 0; n <= len + 2; n++) {
            check += sendbuff[n];
        }
        sendbuff[len + 3] = (byte) (check & 0x00FF);
        return sendbuff;
    }

    //向STM32发送指令，并读取返回值
    public synchronized static byte[] sendProtocol(byte[] sendBuff) {
        byte[] readBuff = null;
        try {
            LogMgr.d(String.format(Locale.US, "sendProtocol() write:%s", ByteUtils.bytesToString(sendBuff, sendBuff.length)));
            for (int i = 0; i < RETRY_TIMES && readBuff == null; i++) {
                LogMgr.i("sendProtocol() RETRY_TIMES: " + i);
                readBuff = SPUtils.request(sendBuff, REQUEST_TIMEOUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (readBuff == null) {
            readBuff = new byte[BUFF_LENGTH];
        }
        LogMgr.d(String.format(Locale.US, "sendProtocol()  read:%s", ByteUtils.bytesToString(readBuff, readBuff.length)));
        return readBuff;
    }

    public synchronized static byte[] sendProtocol(byte[] sendBuff, int timeout) {
        byte[] readBuff = null;
        try {
            LogMgr.d(String.format(Locale.US, "sendProtocol() write:%s", ByteUtils.bytesToString(sendBuff, sendBuff.length)));
            for (int i = 0; i < RETRY_TIMES && readBuff == null; i++) {
                LogMgr.i("sendProtocol() RETRY_TIMES: " + i);
                readBuff = SPUtils.request(sendBuff, timeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (readBuff == null) {
            readBuff = new byte[BUFF_LENGTH];
        }
        LogMgr.d(String.format(Locale.US, "sendProtocol()  read:%s", ByteUtils.bytesToString(readBuff, readBuff.length)));
        return readBuff;
    }

    public synchronized static byte[] sendProtocol(byte type, byte cmd1, byte cmd2, byte[] data) {
        byte[] sendBuff = buildProtocol(type, cmd1, cmd2, data);
        return sendProtocol(sendBuff);
    }

    public synchronized static byte[] sendProtocol(byte type, byte cmd1, byte cmd2, byte[] data, int timeout) {
        byte[] sendBuff = buildProtocol(type, cmd1, cmd2, data);
        return sendProtocol(sendBuff, timeout);
    }

    public synchronized static void cancelRequestTimeout() {
        try {
            SPUtils.cancelRequestTimeout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //向STM32发送指令，不读取返回值
    public synchronized static void write(byte[] sendBuff) {
        try {
            LogMgr.d(String.format(Locale.US, "write:%s", ByteUtils.bytesToString(sendBuff, sendBuff.length)));
            SPUtils.write(sendBuff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void write(byte type, byte cmd1, byte cmd2, byte[] data) {
        byte[] sendBuff = buildProtocol(type, cmd1, cmd2, data);
        write(sendBuff);
    }

    public synchronized static void controlSkillPlayer(int state,String filePath) {
        try {
            LogMgr.d(String.format(Locale.US, "controlSkillPlayer: state:%d, filePath:%s", state, filePath));
            //SPUtils.controlSkillPlayer(state, filePath);
            BrainService.getmBrainService().sendMessageToControl(GlobalConfig.ACTION_SERVICE_MODE_LEARN_LETTER, null, filePath, state, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void sendCmdToControl(byte type, byte cmd1, byte cmd2, byte[] data) {
        try {
            byte[] sendBuff = buildProtocol(type, cmd1, cmd2, data);
            LogMgr.d(String.format(Locale.US, "sendCmdToControl() sendBuff: %s", ByteUtils.bytesToString(sendBuff, sendBuff.length)));
            BrainService.getmBrainService().sendMessageToControl(GlobalConfig.ACTION_SERVICE_MODE_OLD_PROTOCOL, sendBuff, null, 1, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static void sendPatchCmdToControl(int modeState, byte[] cmdData) {
        try {
            LogMgr.d(String.format(Locale.US, "sendCmdToControl() modeState: %d cmdData: %s", modeState, ByteUtils.bytesToString(cmdData, cmdData.length)));
            BrainService.getmBrainService().sendMessageToControl(GlobalConfig.CONTROL_CALLBACKMODE_PATCH_CMD, cmdData, null, modeState, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
