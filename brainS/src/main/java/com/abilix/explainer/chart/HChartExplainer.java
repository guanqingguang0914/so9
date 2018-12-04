package com.abilix.explainer.chart;

import android.os.Handler;

import com.abilix.brain.GlobalConfig;
import com.abilix.brain.R;
import com.abilix.explainer.AExplainer;
import com.abilix.explainer.ControlInfo;
import com.abilix.explainer.ExplainTracker;
import com.abilix.explainer.ExplainerApplication;
import com.abilix.explainer.FunNode;
import com.abilix.explainer.helper.H56ExplainerHelper;
import com.abilix.explainer.helper.HExplainerHelper;
import com.abilix.explainer.helper.Interface.IHRobot;
import com.abilix.explainer.utils.ByteUtils;
import com.abilix.explainer.utils.LogMgr;
import com.abilix.explainer.utils.PlayerUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

public class HChartExplainer extends AExplainer {
    protected IHRobot explainerHelper;

    public HChartExplainer(Handler handler) {
        super(handler);
        if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_H) {
            explainerHelper = new H56ExplainerHelper();
        } else /*if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_H3)*/ {
            explainerHelper = new HExplainerHelper();
        }
    }

    @Override
    public void doExplain(String filePath) {
        super.doExplain(filePath);
        LogMgr.d("start explain vjc file");
        // 文件总长度
        int len = filebuf.length;
        subFunNodeJava = new FunNode();
        int funPos = 0;
        byte runBuffer[] = new byte[40];
        int index = 0;
        int reValue = 0;
        float param1 = 0;
        float param2 = 0;
        float param3 = 0;
        float param4 = 0;
        float param5 = 0;
        gloableCount = -1;

        // 添加两个时钟变量。赋初始值。
        long init_time = 0;
        long end_time = 0;
        init_time = end_time = System.currentTimeMillis();

        funPos = start;

        do {
            if (!doexplain) { // 文件解析结束后退出
                LogMgr.d("explain finish and exit");
                return;
            }
            explainerHelper.readFromFlash(filebuf, funPos, runBuffer, 40); // 获取40字节数据
            if (runBuffer[0] != 0x55 && runBuffer[1] != 0xAA) {
                LogMgr.e("explain file is not correct");
                return;
            }
            index = explainerHelper.getU16(runBuffer, 8); // 解析函数名对应的index
            funPos = explainerHelper.getU16(runBuffer, 34) * 40; // 解析跳转到哪一行
            //LogMgr.d(String.format(Locale.US, "====>index:%d funPos:%d runBuffer:%s", index, funPos, ByteUtils.bytesToString(runBuffer, runBuffer.length)));
            //LogMgr.d("====>index:" + index);

            switch (index) {
                case 0: //getName
                    byte name[] = new byte[20];
                    explainerHelper.getName(name, runBuffer);
                    //String temp = new String(name);
                    break;

                case 2: // Printf
                    int i = 0;
                    int j = 32;
                    byte[] str = new byte[200];
                    float[] valData = new float[100];// = {0};
                    for (int a = 0; a < 100; a++) {
                        valData[a] = 0.0f;
                    }
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    for (int a = 0; a < 200; a++) {
                        str[a] = 0x00;
                    }

                    for (i = 0; i < (int) param1; ++i) {
                        if (j >= 32) {
                            j = 10;
                            explainerHelper.readFromFlash(filebuf, funPos,
                                    runBuffer, 40);
                            funPos = explainerHelper.getU16(runBuffer, 34) * 40;
                        }
                        str[i] = runBuffer[j++];
                    }
                    for (int i11 = 0; i11 < 100000000; i11++) {
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    for (i = 0, j = 30; i < (int) param2; ++i) {
                        if (j > 25) {
                            j = 10;
                            explainerHelper.readFromFlash(filebuf, funPos,
                                    runBuffer, 40);
                            funPos = explainerHelper.getU16(runBuffer, 34) * 40;
                        }
                        valData[i] = explainerHelper
                                .getParam(runBuffer, mValue, j);
                        j = j + 5;
                    }
                    strInfo += (" -- printf: " + param1 + "   " + param2);
                    break;

                case 3: // wait
                case 31:
                    isSleep = true;
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    waitExplain(param1);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                    break;

                case 13:
                    // 计算时间
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    end_time = System.currentTimeMillis();
                    //这里保留一位小数。-----------------------------------------------------------------------------------------
                    Double b = ((double) (end_time - init_time) / 1000);
                    BigDecimal bd = new BigDecimal(b);
                    bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);//四舍五入。
                    b = bd.doubleValue();
                    mValue[reValue] = Float.parseFloat(b.toString());//double 转float
                    break;

                case 14:
                    // 复位时间
                    init_time = System.currentTimeMillis();
                    break;

                case 21: // my_Calc (运算符计算)
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);

                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.my_Calc(param1, param2, (int) param3);
                    break;

                case 22: // my_jump(跳转)
                    if (gloableCount == -1) {
                        gloableCount = explainerHelper.getU16(runBuffer, 4) - 1;
                    }
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    funPos = (param1 > 0.5) ? (int) param2 : (int) param3;
                    funPos = funPos * 40;
                    break;

                case 23: // return(程序结束)
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                    return;

                case 24: // stepin(调用子程序)
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    if (!addFunNodeJava(funPos)) {
                        return;
                    }
                    LogMgr.e(String.format(Locale.US, "====>case %d: mValue[0]-->tempValue[%d] length:%d", index, subFunNodeJava.valStart, mValue.length));
                    System.arraycopy(mValue, 0, tempValue, subFunNodeJava.valStart, mValue.length);
                    funPos = (int) param1 * 40;
                    break;

                case 25: // stepout(跳出子程序)
                    LogMgr.e(String.format(Locale.US, "====>case %d: tempValue[%d]-->mValue[%d] length:%d", index, subFunNodeJava.valStart + gloableCount, gloableCount, mValue.length - gloableCount));
                    System.arraycopy(tempValue, subFunNodeJava.valStart + gloableCount, mValue, gloableCount, mValue.length - gloableCount);
                    int curFunNodePos = subFunNodeJava.pos;
                    if (!delFunNodeJava()) {
                        return;
                    }
                    funPos = curFunNodePos;
                    break;

                case 28:// getRandom
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = (int) explainerHelper.getRandom((int) param1, (int) param2);
                    LogMgr.d("random response::" + mValue[reValue]);
                    break;

                case 30://void setSound(int,int)	扬声器
                    /*"第一个参数：4字节，int类型，表示声音类型。0:自我介绍、1：模拟动物、2：模拟乐器。
                    第二个参数：4字节，int类型，表示某种声音的文件下标，范围0~8。"		*/
                    LogMgr.e("setSound runBuffer::" + ByteUtils.bytesToString(runBuffer, runBuffer.length));
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.e("param1::" + param1 + "  " + "param2::" + param2);
                    explainerHelper.playSound((int) param1, (int) param2);
                    break;

                case 32: //void setLed(int,int,int)	LED
                    /*"第一个参数：4字节，int类型，表示部位，0x00代表头部，0x01代表左手，0x02代表右手，0x03代表左脚，0x04代表右脚。
                    第二个参数：4字节，部位为头部时有效。第1~3字节代表RGB，第4字节暂不用。
                    第三个参数：4字节，int类型，部位不为头部时有效。0x00代表关闭，0x01代表打开。"*/
                    int type = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    byte[] rgb = Arrays.copyOfRange(runBuffer, 16, 16 + 3);
                    int mode = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    LogMgr.d(String.format(Locale.US, "type:%d rgb[%d,%d,%d] mode:%d", type, rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF, mode));
                    explainerHelper.setLed(type, rgb, mode);
                    break;

                case 33:// int Get_Rand(int a,int b)	产生一个随机数
                    /*在a,b之间产生一个随机数(a，b取值范围0-999999)*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = (int) explainerHelper.getRandom((int) param1, (int) param2);
                    LogMgr.e("random response::" + mValue[reValue]);
                    break;

                case 34:// int GetUtrasonic ()	超声传感器
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getUtrasonic();
                    LogMgr.e("GetUtrasonic::" + mValue[reValue]);
                    break;

                case 52://麦克风。
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_RECORD, (int) param1);
                    LogMgr.d("setRecord(" + param1 + ")");
                    pauseExplain();
                    break;

                case 53: //拍照。
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param1 = param1 > 0 ? param1 : 1;
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_CAMERA, (int) param1);
                    LogMgr.d("takePicture(" + param1 + ")");
                    pauseExplain();
                    break;

                case 54: //陀螺仪
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    mValue[reValue] = (int) explainerHelper.getGyro(param1, param2);
                    LogMgr.d("getGyro(" + param1 + ", " + param2 + ") = " + mValue[reValue]);
                    break;

                case 55: //指南针
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getCompass();
                    LogMgr.d("getCompass() = " + mValue[reValue]);
                    break;

                case 70:// setDisplay
                    String content = "";
                    StringBuilder builder = new StringBuilder(content);
                    int data_lines = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    LogMgr.d("data_lines::" + data_lines);
                    for (int k = 0; k < data_lines; k++) {
                        funPos = explainerHelper.getU16(runBuffer, 34) * 40;
                        explainerHelper.readFromFlash(filebuf, funPos, runBuffer, 40);
                        int displayFlag = runBuffer[10];
                        switch (displayFlag) {
                            case 0x00://显示字符串
                                builder.append(explainerHelper.getString(runBuffer, 11));
                                break;
                            case 0x01://显示引用变量
                                float paramV = explainerHelper.getParam(runBuffer, mValue, 10);
                                LogMgr.d("paramV: " + paramV);
                                if (Float.isNaN(paramV) || Float.isInfinite(paramV)) {
                                    builder.append(ExplainerApplication.instance.getString(R.string.guoda));
                                } else {
                                    DecimalFormat df = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US));
                                    builder.append(df.format(paramV));
                                }
                                break;
                            default:
                                break;
                        }
                        if (k == 7) {
                            funPos = explainerHelper.getU16(runBuffer, 34) * 40;
                            explainerHelper.readFromFlash(filebuf, funPos, runBuffer, 40);
                        } else {
                            builder.append("\n");
                        }
                    }
                    content = builder.toString();
                    LogMgr.d("display content::" + content);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_DISPLAY, ExplainTracker.ARG_DISPLAY_TEXT, content);
                    break;

                case 72: //int getTouch()	头部触摸
                    /*返回值 0代表无触摸，1代表有触摸*/
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getHeadTouch();
                    LogMgr.e("explainerHelper.getHeadTouch() value is: " + mValue[reValue]);
                    break;

                case 112: //指南针校准。
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_COMPASS);
                    pauseExplain();
                    break;

                case 113://停止播放。-----新增停止播放模块。
                    explainerHelper.stopPlaySound();
                    break;

                case 118: //void MicrophoneNew(int,int)	麦克风
                    /*"第一个参数：4字节，int类型，表示录音文件（1-10）。
                    第二个参数：4字节，int类型，表示录音时间（单位s）。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("118 MicrophoneNew(int,int) param1::" + param1 + "  " + "param2::" + param2);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_RECORD, (int) param2, (int) param1);
                    pauseExplain();
                    break;

                case 123://void Forword()	前进
                    explainerHelper.move(0x00);
                    pauseExplain();
                    break;

                case 124://void BackOff()	后退
                    explainerHelper.move(0x01);
                    pauseExplain();
                    break;

                case 125://void TurnLeft()	左走
                    explainerHelper.move(0x02);
                    pauseExplain();
                    break;

                case 126://void TurnRight()	右走
                    explainerHelper.move(0x03);
                    pauseExplain();
                    break;

                case 127://void SwivelBody()	转体
                    explainerHelper.move(0x04);
                    pauseExplain();
                    break;

                case 128://void Dance()	跳舞
                    explainerHelper.move(0x05);
                    pauseExplain();
                    break;

                case 129://void KickBall()	踢球
                    explainerHelper.move(0x06);
                    pauseExplain();
                    break;

                case 130://void Greeting()	并步抱拳
                    explainerHelper.move(0x07);
                    pauseExplain();
                    break;

                case 131://void LeftFist()	左冲拳
                    explainerHelper.move(0x08);
                    pauseExplain();
                    break;

                case 132://void Recover()	复原
                    explainerHelper.move(0x09);
                    pauseExplain();
                    break;

                case 133://void setSoundNew(char *)	扬声器
                    /*char*类型，表示音频文件名（N<20字节）
                    附：STM32端内置音频命名规则——中文拼音_语言+下标
                    例：中文“打招呼”下的“你好”——dazhaohu_c0
                    英文“打招呼”下的“欢迎”——dazhaohu_e3
                    公共“交通”下的“赛车” —— jiaotong_p1
                    公共“动物”下的“狗” —— dongwu_p3
                    录音 —— luyin_p3（1~10）*/
                    String soundName = explainerHelper.getString(runBuffer, 11);
                    final int isNeedWaiting = runBuffer[29] & 0xFF;
                    LogMgr.d("soundName:" + soundName + " isNeedWaiting: " + isNeedWaiting);
                    explainerHelper.playSound(soundName, new PlayerUtils.OnCompletionListener() {
                        @Override
                        public void onCompletion(int state) {
                            LogMgr.d("onCompletion() state: " + state);
                            if (isNeedWaiting == 1) {
                                resumeExplain();
                            }
                        }
                    });
                    if (isNeedWaiting == 1) {
                        pauseExplain();
                    }
                    break;
                    //抓取
                case 134:
                    explainerHelper.move(0x0A);
                    pauseExplain();
                    break;
                    //左转
                case 135:
                    explainerHelper.move(0x0B);
                    pauseExplain();
                    break;
                    //右转
                case 136:
                    explainerHelper.move(0x0C);
                    pauseExplain();
                    break;
                    //右冲拳
                case 137:
                    explainerHelper.move(0x0D);
                    pauseExplain();
                    break;

                case 138://void CustomMove(char*)播放用户自定义动作文件
                    /*"char*类型，表示动作文件名（N<20字节）
                    附：动作文件命名规则——chartskill_下标
                    例：播放动作文件1——chartskill_1
                    播放动作文件2——chartskill_2
                    注：BrainB动作文件在/Abilix/media/upload/move/ 路径下"*/
                    String customName = explainerHelper.getString(runBuffer, 10);
                    explainerHelper.customMove(customName);
                    LogMgr.d("播放用户自定义动作文件: customMove(" + customName + ")");
                    pauseExplain();
                    break;

                case 139://void Gait(int, int)步态控制(H5)
                    int action = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    int speed = (int) explainerHelper.getParam(runBuffer, mValue, 15);
                    explainerHelper.gaitMotion(action, speed);
                    LogMgr.d("步态控制: gaitMotion(" + action + ", " + speed + ")");
                    break;

                case 140://void playMove(int) 动作bin文件播放
                    int moveAction = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    explainerHelper.move(moveAction);
                    LogMgr.d("步态控制: move(" + moveAction + ")");
                    pauseExplain();
                    break;

                case 141: //void playCustomMusic(char*)	播放自定义音频
                    //"第一个字节：类型 0：自定义音频(在多媒体目录下/Abilix/media/upload/audio/)。char*类型，表示音频名称（N<19 字节）"
                    int soundType = runBuffer[10] & 0xFF;
                    soundName = explainerHelper.getString(runBuffer, 11);
                    final int waitFlag = runBuffer[29] & 0xFF;
                    LogMgr.d("soundType:" + soundType + " soundName:" + soundName + " waitFlag: " + waitFlag);
                    explainerHelper.playSound(soundType, soundName, new PlayerUtils.OnCompletionListener() {
                        @Override
                        public void onCompletion(int state) {
                            LogMgr.d("onCompletion() state: " + state);
                            if (waitFlag == 1) {
                                resumeExplain();
                            }
                        }
                    });
                    if (waitFlag == 1) {
                        pauseExplain();
                    }
                    break;

                case 142://void GaitWithTime(int，int，int)	步态控制时间控制
                    /*"参数1：类型 0x00 停止步态算法；0x01 前走；0x02 后走；0x03 左走；0x04 右走；0x05 左前走；0x06 右前走；0x07 左后走；0x08 右后走；0x09 停止走动；0x0A 左转； 0x0B 右转；
                    参数2：速度，取值为0，10（慢），20（快）
                    参数3：时间 1~60 秒"*/
                    action = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    speed = (int) explainerHelper.getParam(runBuffer, mValue, 15);
                    int time = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    explainerHelper.gaitMotion(action, speed);
                    LogMgr.d("步态控制: GaitWithTime(" + action + ", " + speed + ", " + time + ")");
                    waitExplain(time);
                    explainerHelper.gaitMotion(9, speed);
                    break;

                case 143://void setMotor(int,int,int)	智能电机
                    /*"第一个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第二个参数：4字节，int类型，表示电机速度，范围：10,20,30,…,100。
                    第三个参数：4字节，表示电机角度，范围：-90~+90。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    LogMgr.d(String.format(Locale.US, "case %d: setMotor(%f, %f, %f)", index, param1, param2, param3));
                    explainerHelper.runServo((int) param1, (int) param2, (int) param3);
                    break;

                default: // 可捕获未解释执行的函数
                    strInfo += (" -- default(未处理): " + index + "   " + funPos);
                    LogMgr.e("error::" + strInfo);
                    return;
            }

            if (len == funPos) { // 判断程序结束
                sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                LogMgr.e("explain file end");
                doexplain = false;
            }

        } while (!isStop);
    }

    @Override
    public void stopExplain() {
        super.stopExplain();
        explainerHelper.stopMove(); // 停止时必须先发stopMove()停止动作播放，否则会出现卡顿；
        explainerHelper.stopPlaySound();
        explainerHelper.turnoffLed();
    }

    @Override
    public void pauseExplain() {
        super.pauseExplain();
    }

    @Override
    public void resumeExplain() {
        super.resumeExplain();
    }

}
