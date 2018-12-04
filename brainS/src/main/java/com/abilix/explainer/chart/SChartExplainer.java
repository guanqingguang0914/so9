package com.abilix.explainer.chart;

import android.os.Handler;

import com.abilix.explainer.AExplainer;
import com.abilix.explainer.ExplainTracker;
import com.abilix.explainer.ExplainerApplication;
import com.abilix.explainer.FunNode;
import com.abilix.brain.R;
import com.abilix.explainer.camera.CameraStateCallBack;
import com.abilix.explainer.camera.usbcamera.UsbCamera;
import com.abilix.explainer.helper.SExplainerHelper;
import com.abilix.explainer.utils.ByteUtils;
import com.abilix.explainer.utils.LogMgr;
import com.abilix.explainer.utils.PlayerUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class SChartExplainer extends AExplainer {
    protected SExplainerHelper explainerHelper;

    private int mLedType = -1;
    private boolean isDisplay = false;

    public SChartExplainer(Handler handler) {
        super(handler);
        explainerHelper = new SExplainerHelper();
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
                    //sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
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
                    //sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
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

                case 29: //void setMotor(int,int,int)	智能电机
                    /*"第一个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第二个参数：4字节，int类型，表示电机速度，范围：1~1023。
                    第三个参数：4字节，表示电机角度，范围：-90~+90。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    LogMgr.d(String.format(Locale.US, "case %d: setMotor(%f, %f, %f)", index, param1, param2, param3));
                    explainerHelper.setSingleMotorS((int) param1, (int) param2, (int) param3);
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

                case 32: //void setHeadLed(int,int,int,int)	头部LED
                    /*"第一个参数：4字节，int类型，表示状态，0x00代表关闭，0x01代表开启。
                    第二个参数：4字节:(单色蓝光 就只用第1个字节 )
                    第1个字节:0x00代表单色蓝光； 0x01代表多色光；
                    第2个字节:红色：0-代表不设置，255-代表设置(单色蓝光：0-代表不设置，255-代表设置);
                    第3个字节:绿色：0-代表不设置，255-代表设置;
                    第4个字节:蓝色：0-代表不设置，255-代表设置;
                    第三个参数：4字节，int类型，表示频率，范围1~10(次/s)。
                    第四个参数：
                    第1字节：代表亮度值，范围0~100
                    第2字节：代表红色亮度，范围0~8
                    第3字节：代表绿色亮度，范围0~8
                    第4字节：代表蓝色亮度，范围0~8"		*/
                    int onOff = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    int type = runBuffer[16] & 0xFF;
                    int rSet = runBuffer[17] & 0xFF;
                    int gSet = runBuffer[18] & 0xFF;
                    int bSet = runBuffer[19] & 0xFF;
                    int frequency = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    int luminance = runBuffer[26] & 0xFF;
                    int rLevel = runBuffer[27] & 0xFF;
                    int gLevel = runBuffer[28] & 0xFF;
                    int bLevel = runBuffer[29] & 0xFF;
                    LogMgr.d(String.format(Locale.US, "type:%d rgbSet[%d,%d,%d] freq:%d lum:%d, rgbLevel[%d,%d,%d]",
                            type, rSet, gSet, bSet, frequency, luminance, rLevel, gLevel, bLevel));
                    byte[] data;
                    mLedType = type;
                    if (type == 0x00) { //0x00代表单色蓝光 S0--S2
                        data = new byte[]{0x00};
                        if (onOff == 0) {//0x00代表关闭
                            data[0] = 0x00;
                        } else {//0x01代表开启
                            data[0] = (byte) ((frequency & 0x0F) << 4 | (luminance &0x0F));
                        }
                    } else { //0x01代表多色光 S3--S8
                        data = new byte[]{0x00, 0x00};
                        if (onOff == 0) {//0x00代表关闭
                            data[0] = 0x00;
                            data[1] = 0x00;
                        } else {//0x01代表开启
                            rLevel = rSet > 0 ? rLevel : 0;
                            gLevel = gSet > 0 ? gLevel : 0;
                            bLevel = bSet > 0 ? bLevel : 0;
                            data[0] = (byte) (((rLevel & 0x0F) << 4) | (gLevel &0x0F));
                            data[1] = (byte) (((bLevel & 0x0F) << 4) | (frequency &0x0F));
                        }
                    }
                    LogMgr.d("LED DATA: " + ByteUtils.bytesToString(data, data.length));
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_LED, -1, data);
                    waitExplain(1.0f);
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
                    mValue[reValue] = explainerHelper.GetUtrasonic();
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

                case 54:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    mValue[reValue] = (int) explainerHelper.getGyro(param1, param2);
                    LogMgr.d("getGyro(" + param1 + ", " + param2 + ") = " + mValue[reValue]);
                    break;

                case 55:
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

                case 119: //void setSoundNew(char *) 扬声器
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

                case 120: //void setWheelModeMotor(int,int)	设置车轮模式电机
                    /*"第一个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第二个参数：4字节，int类型，表示电机速度，范围：-1023~-200，0，200~1023。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("120 setWheelModeMotor(int,int) param1::" + param1 + "  " + "param2::" + param2);
                    explainerHelper.setSingleMotorInWheelMode((int) param1, (int) param2);
                    break;

                case 121: //void setMode(int,int,int)	设置电机模式
                    /*"第一个参数：4字节，int类型，表示模式，0：关节模式；1：车轮模式
                    第二个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第三个参数：4字节，int类型，等待时间。默认为3秒。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    LogMgr.d("121 setMode(int,int) param1::" + param1 + "  " + "param2::" + param2 + "param3::" + param3);
                    explainerHelper.setSingleMotorMode((int) param2, (int) param1);
                    waitExplain(param3);
                    break;

                case 122: //int getEngineAngle(int)	获取舵机角度
                    /*"第一个参数：4字节，int类型，表示舵机ID（1~16）
                    返回值范围（-90~90）"*/
                    int servoId = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int[] angles = explainerHelper.getServoAngles();
                    mValue[reValue] = angles[servoId - 1];
                    LogMgr.d("122 getEngineAngle() = " + mValue[reValue]);
                    break;

                case 123: //void setDoubleWheelModeMotor(int,int)	设置双车轮模式电机
                    /*"第一个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第二个参数：4字节，int类型，表示电机速度，范围：-1023~-200，0，200~1023。
                    第三个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第四个参数：4字节，int类型，表示电机速度，范围：-1023~-200，0，200~1023。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    param4 = explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d("123 setWheelModeMotor(int,int, int, int) param1::" + param1 + " param2::" + param2 + " param3::" + param3 + " param4::" + param4);
                    explainerHelper.setDoubleMotorInWheelMode((int) param1, (int) param2, (int) param3, (int) param4);
                    break;

                case 124: //void setWheelModeMotor(int,int)	设置车轮模式电机
                    /*"第一个参数：4字节，int类型，表示电机ID，范围：1~22。
                    第二个参数：4字节，int类型，表示电机速度，范围：-1023~-200，0，200~1023。
                    第三个参数：4字节，int类型，表示执行模式：0表示速度模式，1表示时间模式；
                    第三个参数：4字节，int类型，表示执行时间1~600s（时间模式有效）*/
                    int id = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    int speed = (int) explainerHelper.getParam(runBuffer, mValue, 15);
                    int runMode = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    float runTime =  explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d("124 setWheelModeMotor() id::" + id + " speed::" + speed + " mode::" + runMode + " time::" + runTime);
                    explainerHelper.setSingleMotorInWheelMode(id, speed);
                    if (runMode == 1) {
                        waitExplain(runTime);
                        explainerHelper.setSingleMotorInWheelMode(id, 0);
                    }
                    break;

                case 125: //void setDoubleWheelModeMotor(int,int)	设置双车轮模式电机
                    /*"第一个参数：4字节，第一字节表示电机ID，范围：1~22;第二三字节表示电机速度，范围：-1023~-200，0，200~1023。
                    第二个参数：4字节，第一字节表示电机ID，范围：1~22;第二三字节表示电机速度，范围：-1023~-200，0，200~1023。
                    第三个参数：4字节，int类型，表示执行时间1~600s*/
                    int id01 = runBuffer[11] & 0xFF;
                    int speed01 = (runBuffer[12] << 8) | (runBuffer[13] & 0xFF);
                    int id02 = runBuffer[16] & 0xFF;
                    int speed02 = runBuffer[17] << 8 | (runBuffer[18] & 0xFF);
                    runMode = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    runTime = (int) explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d("125 setWheelModeMotor() id01::" + id01 + " speed01::" + speed01 + " id02::" + id02 + " speed02::" + speed02 + " mode::" + runMode + " time::" + runTime);
                    explainerHelper.setDoubleMotorInWheelMode(id01, speed01, id02, speed02);
                    if (runMode == 1) {
                        waitExplain(runTime);
                        if(speed01 + speed02 != 0){
                            explainerHelper.setSingleMotorInWheelMode(id01, 0);
                            explainerHelper.setSingleMotorInWheelMode(id02, 0);
                        }else{
                            explainerHelper.setDoubleMotorInWheelMode(id01, 0, id02, 0);
                        }
                        waitExplain((float) 0.15);
                    }
                    break;

                case 126: //void initGyroscope  陀螺仪校准
                    //用于车轮模式走直线校准
                    LogMgr.d("126 initGyroscope()");
                    explainerHelper.initGyroscope();
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_CALIBRATE_GYRO);
                    pauseExplain();
                    break;

                case 136: //void playCustomMusic(char*)	播放自定义音频
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

                case 137: //void closeDisplay()	关闭显示	无参数。
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                    LogMgr.d("137 closeDisplay()");
                    break;

                default: // 可捕获未解释执行的函数
                    strInfo += (" -- default(未处理): " + index + "   " + funPos);
                    LogMgr.e("error::" + strInfo);
                    return;
            }

            if (len == funPos) { // 判断程序结束
                //sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                LogMgr.e("explain file end");
                doexplain = false;
            }

        } while (!isStop);
    }

    private CameraStateCallBack mCameraStateCallBack=new CameraStateCallBack() {
        @Override
        public void onState(int state) {
            LogMgr.d("相机状态回调："+state);
        }
    };


    @Override
    public void stopExplain() {
        super.stopExplain();//关闭在前。退出循环用。
        explainerHelper.setServoWalkStop();
        explainerHelper.stopPlaySound();
        explainerHelper.setCompassCheck(false);//退出指南针校准等待。
        //setLed(new byte[]{0x00, 0x00});
        try{
            //停止车轮模式舵机
            for (int i = 0; i < 16; i++) {
                explainerHelper.setSingleMotorInWheelMode(i + 1, 0);
                Thread.sleep(10);
            }
            if (mLedType != -1) {
                int dataLength = mLedType == 0 ? 1 : 2;
                for (int i = 0; i < 1; i++) {
                    if (dataLength == 2) {
                        UsbCamera.create().setBrightnessS(ExplainerApplication.instance,0,mCameraStateCallBack);
                        UsbCamera.create().setBrightnessS(ExplainerApplication.instance,0,mCameraStateCallBack);
                    } else {
                        UsbCamera.create().setBrightnessS(ExplainerApplication.instance,0,mCameraStateCallBack);
                    }
                    Thread.sleep(5);
                }
                mLedType = -1;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
