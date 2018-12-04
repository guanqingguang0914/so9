package com.abilix.explainer.chart;

import android.os.Handler;

import com.abilix.brain.R;
import com.abilix.explainer.AExplainer;
import com.abilix.explainer.ExplainTracker;
import com.abilix.explainer.ExplainerApplication;
import com.abilix.explainer.FunNode;
import com.abilix.explainer.helper.MExplainerHelper;
import com.abilix.explainer.utils.FileUtils;
import com.abilix.explainer.utils.LogMgr;
import com.abilix.explainer.utils.PlayerUtils;
import com.abilix.vision.utils.VisionControl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MChartExplainer extends AExplainer {
    protected MExplainerHelper explainerHelper;
    public MChartExplainer(Handler handler) {
        super(handler);
        explainerHelper = MExplainerHelper.getInstance();
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
        byte eyeRgbData[] = new byte[48];

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

                case 3: // wait
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

                case 29://void SetWeelMoto (int, int,int)	轮子电机
                    /*"第一个参数：4字节，int类型，表示电机开闭环，0闭环（默认是闭环电机），1开环(0~1)。
                    第二个参数：4字节，int类型，表示控制哪个轮子的电机，0左电机，1右电机(0~1)。
                    第三个参数：4字节，int类型，正数表示电机向前速度，负数表示电机向后速度(-100~100)。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    LogMgr.d("control SetWeelMoto-->" + "param1:" + param1 + " param2:" + param2 + " param3:" + param3);
                    explainerHelper.setWheelMoto((int) param1, (int) param2, (int) param3);
                    break;

                case 30://void SetEyeColor(int,int ,int ,int )	眼睛颜色
                    /*"第一个参数：4字节，int类型，表示眼睛几个亮灭(0~16)。
                    第二个参数：4字节，int类型，表示R颜色值(0~255)。
                    第三个参数：4字节，int类型，表示G颜色值(0~255)。
                    第四个参数：4字节，int类型，表示B颜色值(0~255)。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    param4 = explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d("control setEyeColor-->" + "param1:" + param1 + " param2:" + param2 + " param3:" + param3 + " param4:" + param4);
                    explainerHelper.setEyeColor((int) param1, (int) param2, (int) param3, (int) param4);
                    break;

                case 31://void SetColor (int,int,int ,int )	设置颜色
                    /*"第一个参数：4字节，int类型，表示代表控制哪个部位的颜色：1脖子颜色，2~3轮子颜色，4底部颜色(0~16)。
                    第二个参数：4字节，int类型，表示R颜色值(0~255)。
                    第三个参数：4字节，int类型，表示G颜色值(0~255)。
                    第四个参数：4字节，int类型，表示B颜色值(0~255)。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    param4 = explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d("control setColor-->" + "param1:" + param1 + " param2:" + param2 + " param3:" + param3 + " param4:" + param4);
                    explainerHelper.setColor((int) param1, (int) param2, (int) param3, (int) param4);
                    break;

                case 32://void SetLuminance(int,int)	设置亮度 已废弃：
                    /*"第一个参数：4字节，int类型，表示控制哪个部位的颜色亮度值:1脖子，2~3轮子，4底部。
                    第二个参数：4字节，int类型，表示亮度值(0~100)。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("control setLuminance-->" + "param1:" + param1 + " param2:" + param2 + " param3:" + param3 + " param4:" + param4);
                    explainerHelper.setLuminance((int) param1, (int) param2);
                    break;

                case 33://void SetWave (int,int)	设置波形
                    /*"第一个参数：4字节，int类型，表示控制哪个部位的波形（1~4），1脖子波形，2~3轮子波形，4底部波形。
                    第二个参数：4字节，int类型，表示波形的类型（1~4），1代表正弦波，2代表宽波，3代表高平（默认高电平），4代表低电平。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("control setWave-->" + "param1:" + param1 + " param2:" + param2);
                    explainerHelper.setWave((int) param1, (int) param2);
                    break;
                // 获取下视
                case 34:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getDownLook();
                    LogMgr.d("control 下视：--->getDownLook：" + mValue[reValue] + "param1:" + param1);
                    break;
                // 获取超声
                case 35:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getUltrasonic((int) param1);
                    LogMgr.d("control 超声：--->getUltrasonic：" + mValue[reValue] + "param1:" + param1);
                    break;
                // 获取红外
                case 36:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getInfrared();
                    LogMgr.d("control 红外：--->getInfrared:" + mValue[reValue]);
                    break;
                // 获取地面灰度
                case 37:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int id = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    mValue[reValue] = explainerHelper.getGroundGray(id);
                    LogMgr.d(String.format(Locale.US, "control 地面灰度：--->getground_gray[%d] = %f", id, mValue[reValue]));
                    break;
                // 获取碰撞
                case 38:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getCollision();
                    LogMgr.d("control 碰撞：--->getCollision: " + mValue[reValue]);
                    break;
                // 设置吸尘
                case 39:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    explainerHelper.setVacuumPower((int) param1);
                    LogMgr.d("control 吸尘：--->setVacuumPower: " + "param1:" + param1);
                    break;
                // 设置上下脖子
                case 40:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    LogMgr.d("control 上下脖子：--->SetNeckUPMotor-->" + "param1:" + param1);
                    explainerHelper.SetNeckUPMotor((int) param1);
                    break;
                // 设置左右脖子
                case 41:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    LogMgr.d("control 左右脖子：--->SetNeckLRMotor-->" + "param1:" + param1);
                    explainerHelper.SetNeckLRMotor((int) param1);
                    break;
                // 播放音乐
                case 51:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("control 播放音乐：playeretMusic-->" + "param1:" + param1 + " param2:" + param2);
                    explainerHelper.playeretMusic((int) param1, (int) param2);
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
                // 陀螺仪
                case 54:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    mValue[reValue] = (int) explainerHelper.getGyro(param1, param2);
                    LogMgr.d("getGyro(" + param1 + ", " + param2 + ") = " + mValue[reValue]);
                    break;
                // 指南针
                case 55:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getMcompass();
                    LogMgr.d("getMcompass() = " + mValue[reValue]);
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
                // 播放指定音乐
                case 71:
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("control 播放指定音乐：--->playSound:param1::" + param1 + "  " + "param2::" + param2);
                    explainerHelper.playSound((int) param1, (int) param2);
                    break;
                case 72:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchHead = explainerHelper.getTouchHead();
                    LogMgr.d("触摸isTouchHead：----" + isTouchHead + "position: " + reValue);
                    mValue[reValue] = isTouchHead;
                    break;
                case 73:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchBack = explainerHelper.getTouchBack();
                    LogMgr.d("触摸isTouchBack：----" + isTouchBack + "position: " + reValue);
                    mValue[reValue] = isTouchBack;
                    break;
                case 74:
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchChest = explainerHelper.getTouchChest();
                    LogMgr.d("触摸isTouchChest：----" + isTouchChest + "position: " + reValue);
                    mValue[reValue] = isTouchChest;
                    break;
                // 单个电机
                case 100:
                    // 第一个参数：类型(0-闭环 1-开环 2-位移)
                    // 第二个参数：模式(0-左电机 1-右电机)
                    // 第三个参数：速度
                    // 第四个参数：位移 int type,int mode,int sudu,int distance
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);
                    param4 = explainerHelper.getParam(runBuffer, mValue, 25);
                    LogMgr.d(String.format(Locale.US, "单个电机setNewOneWheelMoto(%d, %d, %d, %d)", (int) param1, (int) param2, (int) param3, (int) param4));
                    explainerHelper.setNewOneWheelMoto((int) param1, (int) param2, (int) param3, (int) param4);
                    break;
                // 所有电机
                case 101:
                    // 第一个参数：类型(0-闭环 1-位移 2-开环)
                    // 第二个参数：(顺序：由高位到低位)
                    // 第1字节：左轮子是否设置(0-不设置，1-设置，占1位)-左轮子速度是否引用变量(0-代表常量，1-引用变量，占1位)-空6位
                    // 第2字节：左轮子速度(类型为0时，速度范围为-60~60；类型为1时，速度范围为-100~100；类型为2时，速度范围为-60~60)
                    // 第3字节：右轮子是否设置(0-不设置，1-设置，占1位)-右轮子速度是否引用变量(0-代表常量，1-引用变量，占1位)-空6位
                    // 第4字节：右轮子速度(类型为0时，速度范围为-60~60；类型为1时，速度范围为-100~100；类型为2时，速度范围为-60~60)
                    // 第三个参数：左轮子位移(0-4095)单位cm
                    // 第四个参数：右轮子位移(0-4095)单位cm
                    int type = (int) explainerHelper.getParam(runBuffer, mValue, 10);// 类型。开闭位移。
                    int leftIsSet = (runBuffer[16] >> 7) & 0x01;
                    int leftIsvariable = (runBuffer[16] >> 6) & 0x01;
                    int leftSudu = runBuffer[17];
                    LogMgr.d(String.format(Locale.US, "type:%d leftIsSet:%d leftIsvariable:%d leftSudu:%d", type, leftIsSet, leftIsvariable, leftSudu));
                    int RightIsSet = (runBuffer[18] >> 7) & 0x01;
                    int RightIsvariable = (runBuffer[18] >> 6) & 0x01;
                    int ringhtSudu = runBuffer[19];
                    LogMgr.d(String.format(Locale.US, "type:%d RightIsSet:%d RightIsvariable:%d leftSudu:%d", type, RightIsSet, RightIsvariable, ringhtSudu));
                    int leftDis = (int) explainerHelper.getParam(runBuffer, mValue, 20);
                    int rightDis = (int) explainerHelper.getParam(runBuffer, mValue, 25);
                    // 这里还有两个问题，引用变量的获取。
                    if (leftIsvariable == 1) {
                        leftSudu = (int) explainerHelper.getMvalue(mValue, leftSudu);
                    }
                    if (RightIsvariable == 1) {
                        ringhtSudu = (int) explainerHelper.getMvalue(mValue, ringhtSudu);
                    }
                    LogMgr.d("leftDis: " + leftDis + " rightDis: " + rightDis);
                    LogMgr.d("leftsudu: " + leftSudu + " rightsudu: " + ringhtSudu);
                    explainerHelper.setNewAllWheelMoto(type, leftIsSet, leftSudu, RightIsSet, ringhtSudu, leftDis, rightDis);
                    break;

                case 112:// 校准指南针。
                    LogMgr.d("control 指南针：--->");
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_COMPASS);
                    pauseExplain();
                    break;

                case 113://停止播放。
                    explainerHelper.stopPlaySound();
                    break;

                //寻线模块库。
                case 114://初始化。这里需要解析参数。---------------------------------------------------------------------ok
                    int motorType = (int) runBuffer[11];//电机类型(0:闭环;1:开环;默认值:0，注：左右电机同一种类型)-第1字节
                    int lineColor = (int) runBuffer[12];//场地类型(0:黑线;1:白线;默认值:0)-第2字节
                    int speedL = (int) explainerHelper.getParam(runBuffer, mValue, 15);//第二个参数: 左电机速度(开环-100~100；闭环-60~60)
                    int speedR = (int) explainerHelper.getParam(runBuffer, mValue, 20);//第三个参数: 右电机速度(开环-100~100；闭环-60~60)
                    float Coe = explainerHelper.getParam(runBuffer, mValue, 25);//第四个参数: 阈值偏移( 白(0.00) ~ 黑(1.00) 默认值0.50)
                    LogMgr.d(String.format(Locale.US, "motorType:%d, lineColor:%d, speed[%d, %d], Coe:%f", motorType, lineColor, speedL, speedR, Coe));
                    explainerHelper.WER_InitRobot_5(1.0f, 1.0f, lineColor, Coe, motorType);
                    //这里先把这些写入配置文件里。
                    int num = 0;//地面灰度数量(0:5灰度;1:7灰度;默认值:0)
                    int IO1 = 1 - 1;
                    int IO2 = 0 - 1;
                    int IO3 = 3 - 1;
                    int IO4 = 4 - 1;
                    int IO5 = 5 - 1;
                    int IO6 = 0 - 1;
                    int IO7 = 7 - 1;
                    String io_config = num + "," + IO1 + "," + IO2 + "," + IO3 + "," + IO4 + "," + IO5 + "," + IO6 + "," + IO7 + ",";//这就是配置文件。
                    FileUtils.saveFile(io_config, FileUtils.IO_CONFIG);
                    explainerHelper.readandsend(num);
                    break;

                case 115://void EnviromentCollection(int)	环境采集
                    explainerHelper.WER_SetMotor(0, 0, 0);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_IS_GETAI);
                    //explainerHelper.environment();
                    explainerHelper.startWaiting();
                    //explainerHelper.IEnvironment(1);
                    break;

                case 116://路口寻线  ---------------------------------------------------------------------------------------------ok
                    //第一个参数：路口类型(0-左侧路口   1-右侧路口)
                    //第二个参数：第一个字节:巡线速度(10~100)；第二个字节:左转差速(0-100)；第三个字节:右转差速(0-100)；第四个字节:结束后是否停车(1-是 0-否)；
                    //第三个参数：循环次数1-4095
                    //第四个参数：冲过路口时间(0-60.000)
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);//路口类型。
                    param2 = explainerHelper.getParam(runBuffer, mValue, 20);//循环次数
                    param3 = explainerHelper.getParam(runBuffer, mValue, 25);//冲过路口时间。
                    //这里按字节解析第二个参数。
                    int speed, Lcut, Rcut, stop;  //16  17  18   19.
                    speed = runBuffer[16] & 0xff;
                    Lcut = runBuffer[17] & 0xff;
                    Rcut = runBuffer[18] & 0xff;
                    stop = runBuffer[19] & 0xff;
                    //stop = switchint(stop);
                    LogMgr.e("speed:" + speed + "," + "Lcut:" + Lcut + "," + "Rcut:" + Rcut + "," + "路口类型：" + param1 + "循环次数：" + param2 + "时间：" + param3);

                    explainerHelper.WER_LineWay_C((int) param2, (int) param1, speed, Lcut, Rcut, stop, param3);
                    //explainerHelper.waitReveice();
                    break;

                case 117://按时寻线-----------------------------------------------------------------------------------------------OK
                    //第一个参数：第一个字节:巡线速度(10~100)(默认值：10；)；第二个字节:左转差速(0-100)；第三个字节:右转差速(0-100)；第四个字节:结束后是否停车(1-是 0-否)；
                    //第二个参数：巡线时间(0-60.000)
                    //
                    int T_speed, T_Lcut, T_Rcut, T_stop; //11,12,13,14.
                    T_speed = runBuffer[11] & 0xff;
                    T_Lcut = runBuffer[12] & 0xff;
                    T_Rcut = runBuffer[13] & 0xff;
                    T_stop = runBuffer[14] & 0xff;
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);//时间。
                    LogMgr.e("time is: " + param2);
                    explainerHelper.WER_LineWay_T(T_speed, T_Lcut, T_Rcut, T_stop, param2);
                    //explainerHelper.waitReveice();
                    break;

                case 118://高级寻线
                    //第一个参数：第一个字节:巡线速度(10~100)(默认值：10；)；第二个字节:左转差速(0-100)；第三个字节:右转差速(0-100)；第四个字节:结束后是否停车(1-是 0-否)；
                    //第二个参数：第一个字节:传感器端口(0-6)；第二个字节:比较符（比较符取值范围：0:<;1:<=;2:==;3:!=;4:>=;5:>;）；
                    //第三个参数：参考值
                    int O_speed, O_Lcut, O_Rcut, O_stop, O_IO, O_operator, reference;

                    O_speed = runBuffer[11] & 0xff;
                    O_Lcut = runBuffer[12] & 0xff;
                    O_Rcut = runBuffer[13] & 0xff;
                    O_stop = runBuffer[14] & 0xff;

                    O_IO = runBuffer[16] & 0xff;
                    O_operator = runBuffer[17] & 0xff;
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);//参考值。
                    LogMgr.e("高级寻线：" + "O_IO:" + O_IO + " O_operator " + O_operator + "param3: " + param3 + " O_speed: " + O_speed + " O_Lcut: " + O_Lcut + " O_Rcut:" + O_Rcut + " O_stop:" + O_stop);
                    explainerHelper.WER_LineWay_O(O_IO, O_operator, (int) param3, O_speed, O_Lcut, O_Rcut, O_stop);
                    //explainerHelper.waitReveice();
                    break;

                case 119://转弯------------------------------------------------------------------------ok
                    //第一个参数：过线条数；(1-100))
                    //第二个参数：左马达速度；(-100-100)
                    //第三个参数：右马达速度；(-100-100)
                    //第四个参数：第一个字节:结束位置(0-偏左 1-中间 2-偏右)；第二个字节:结束后是否停车(1-是 0-否)；
                    //我传到底层的速度是0~200，也就是加100就好了。

                    int speed_L, speed_R, A_stop, N, P;
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);//过线条数N。
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);//左马达速度。
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);//右马达速度。

                    P = runBuffer[26] & 0xff;
                    A_stop = runBuffer[27] & 0xff;
                    //A_stop = switchint(A_stop);  转弯这里VJC已经改好了，不用转化。
                    LogMgr.e("Lspeed:" + param2 + "Rspeed" + param3 + "P=" + P);
                    explainerHelper.WER_Around((int) param2, (int) param3, A_stop, (int) param1, P);
                    //explainerHelper.waitReveice();
                    break;

                case 120://启动马达。
                    //第一个参数: 第一个字节：结束标志(0-延时 1-传感器)；第二个字节：端口(0-6)（仅对传感器有效）；第三个字节：比较符（比较符取值范围：0:<;1:<=;2:==;3:!=;4:>=;5:>;（仅对传感器有效））；第四个字节：结束后是否停车(1-是 0-否)。
                    //第二个参数: 结束标志数值（延时-时间  传感器触发-次数）--占4字节。
                    //第三个参数: 左马达功率(-100~100 默认值0)----------占4字节。
                    //第四个参数: 右马达功率(-100~100 默认值0)----------占4字节

                    int IO, S_operator, S_reference, S_speed_L, Rspeed, S_stop, YN;
                    YN = runBuffer[11] & 0xff; //启动方式。
                    IO = runBuffer[12] & 0xff;
                    S_operator = runBuffer[13] & 0xff;
                    S_stop = runBuffer[14] & 0xff;
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);//时间time  阈值。
                    param3 = explainerHelper.getParam(runBuffer, mValue, 20);//左马达速度。
                    param4 = explainerHelper.getParam(runBuffer, mValue, 25);//右马达速度。

                    if (YN == 0) {
                        LogMgr.e("启动马达： " + "启动方式yanshi " + "param3" + param3 + "param4: " + param4 + "time:" + param2);
                        explainerHelper.WER_SetMotor((int) param3, (int) param4, param2);
                    } else {
                        LogMgr.e("启动马达： " + "huidu " + "IO" + IO + "S_operator: " + S_operator + "param2:" + param2 + "param3: " + param3 + "param4" + param4 + "S_stop:" + S_stop);
                        explainerHelper.WER_SetMotor_L(IO, S_operator, (int) param2, (int) param3, (int) param4, S_stop);

                    }
                    //explainerHelper.waitReveice();
                    break;

                case 121: //void setSoundNew(char *) 扬声器
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

                case 122: //int GetlookdownM1(int)	下视传感器（M3s，M4s适用）
                    /*"4字节，参数：int类型，表示下视传感器类型：
                    0x00代表后左，0x01代表后中,0x02代表后右,
                    0x03代表前左,0x04代表前右；
                    0x05代表全部悬空；0x06代表全部不悬空；
                    返回值：0 否   1是"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    mValue[reValue] = explainerHelper.getDownLookM3S((int) param1);
                    LogMgr.d("control 碰撞：--->getDownLookM3S: " + mValue[reValue] + " param1:" + param1);
                    break;

                case 123://int getLeftEarTouch()	左耳触碰
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchEarLeft = explainerHelper.getTouchEarLeft();
                    LogMgr.d("触摸isTouchEarLeft：----" + isTouchEarLeft + "position: " + reValue);
                    mValue[reValue] = isTouchEarLeft;
                    break;

                case 124://int getRightEarTouch()	右耳触碰
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchEarRight = explainerHelper.getTouchEarRight();
                    LogMgr.d("触摸isTouchEarRight：----" + isTouchEarRight + "position: " + reValue);
                    mValue[reValue] = isTouchEarRight;
                    break;

                case 125://int getLeftArmTouch()	左臂触碰
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchArmLeft = explainerHelper.getTouchArmLeft();
                    LogMgr.d("触摸isTouchArmLeft：----" + isTouchArmLeft + "position: " + reValue);
                    mValue[reValue] = isTouchArmLeft;
                    break;

                case 126://int getRightArmTouch()	右臂触碰
                    reValue = explainerHelper.getU16(runBuffer, 30);
                    int isTouchArmRight = explainerHelper.getTouchArmRight();
                    LogMgr.d("触摸isTouchArmRight：----" + isTouchArmRight + "position: " + reValue);
                    mValue[reValue] = isTouchArmRight;
                    break;

                case 127: //void speechRecognition()	语音识别	"无参数，无返回值。
                    //自动显示语音识别结果。"	(此协议目前用于项目验收，不加入正式功能)
                    break;

                case 128: //int colorRecognition(int)	颜色识别	"第一个参数：4字节，int类型，表示需要识别的颜色，目前有：0红色；1黄色；2蓝色；3绿色
                    //返回值：int类型，0表未识别到该颜色；1表示识别到该颜色"""	(此协议目前用于项目验收，不加入正式功能)
                    break;

                case 129: //void MicrophoneNew(int,int)	麦克风
                    /*"第一个参数：4字节，int类型，表示录音文件（1-10）。
                    第二个参数：4字节，int类型，表示录音时间（单位s）。"*/
                    param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                    param2 = explainerHelper.getParam(runBuffer, mValue, 15);
                    LogMgr.d("129 MicrophoneNew(int,int) param1::" + param1 + "  " + "param2::" + param2);
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_RECORD, (int) param2, (int) param1);
                    pauseExplain();
                    break;

                case 130://void setDisplayImage(char*)	显示图片
                    //"第一个字节：类型 0：拍照图片；1：自定义图片(在多媒体目录下/Abilix/media/upload/image/)char*类型，表示图片名称（N<19 字节）"
                    int displayType = runBuffer[10] & 0xFF;
                    switch (displayType) {
                        case 0x00:
                            param1 = explainerHelper.getParam(runBuffer, mValue, 10);
                            LogMgr.d("case 130:显示图片[" + displayType + "]--> " + param1);
                            sendExplainMessage(ExplainTracker.MSG_EXPLAIN_DISPLAY, ExplainTracker.ARG_DISPLAY_PHOTO, String.valueOf((int) param1));
                            break;
                        case 0x01:
                            String picName = explainerHelper.getString(runBuffer, 11);
                            LogMgr.d("case 130:显示图片[" + displayType + "]--> " + picName);
                            sendExplainMessage(ExplainTracker.MSG_EXPLAIN_DISPLAY, ExplainTracker.ARG_DISPLAY_CUSTOM_IMAGE, picName);
                            break;
                    }
                    break;

                case 131: //void playCustomMusic(char*)	播放自定义音频
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

                case 132: //void closeDisplay()	关闭显示	无参数。
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                    LogMgr.d("132 closeDisplay()");
                    break;

                case 133://int setEyeSingleLedColor(int)	设置眼睛灯中单独的一个led的颜色与亮灭
                    /*"第一个参数：4字节，表示led的ID，取值1-16
                    第二个参数：4字节，表示led的亮灭，取值0或1， 0表示灭，1表示亮
                    第三个参数：4字节，其中后三位，由高至低，表示led的RGB的颜色值"*/
                    int eyeId = (int) explainerHelper.getParam(runBuffer, mValue, 10);
                    int eyeMode = (int) explainerHelper.getParam(runBuffer, mValue, 15);
                    byte[] rgb = new byte[3];
                    if (eyeMode == 1) {
                        System.arraycopy(runBuffer, 22, rgb, 0, rgb.length);
                    }
                    System.arraycopy(rgb, 0, eyeRgbData, (eyeId - 1) * 3, rgb.length);
                    LogMgr.d("133 setEyeSingleLedColor-->" + "eyeId:" + eyeId + " eyeMode:" + eyeMode + " rgb:[" + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + "]");
                    explainerHelper.setEyeColor(eyeRgbData);
                    break;
                case 139:
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_VISION, index);
                    pauseExplain();
                    break;
                case 140:
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_VISION, index);
                    pauseExplain();
                    break;
                case 141:
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_VISION, index);
                    pauseExplain();
                    break;
                case 142:
                    sendExplainMessage(ExplainTracker.MSG_EXPLAIN_VISION, index);

                    break;
                default: // 可捕获未解释执行的函数
                    strInfo = (" -- default(未处理) index: " + index + "   funPos: " + funPos);
                    LogMgr.e("error::" + strInfo);
                    break;
            }

            if (len == funPos) { // 判断程序结束
                //sendExplainMessage(ExplainTracker.MSG_EXPLAIN_NO_DISPLAY);
                LogMgr.e("explain file end");
                doexplain = false;
            }

        } while (!isStop);
    }

    @Override
    public void stopExplain() {
        super.stopExplain();
        LogMgr.d("stop explain");
        try {
            explainerHelper.stopPlaySound();
            explainerHelper.stopWaiting();
            explainerHelper.AI_stop();
            explainerHelper.startSleepStop();
            VisionControl.stopVision();
        } catch (Exception e) {
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
