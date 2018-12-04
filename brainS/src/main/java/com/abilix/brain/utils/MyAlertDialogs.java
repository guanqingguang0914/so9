package com.abilix.brain.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.abilix.brain.Application;
import com.abilix.brain.BrainService;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.R;

/**
 * 弹窗管理类。
 */
public class MyAlertDialogs {
    private static AlertDialog alertDialog1, alertDialog2, alertDialog3,
            alertDialog4, alertDialog10,alertDialog11;

    public static void setAlertDialog1(final Context context, final Handler mHandler) {
//		alertDialog1 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.huanjingcaiji))
//				.setMessage(context.getString(R.string.shifouhuanjingcaiji))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								setAlertDialog2(context, mHandler);
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 307, -1, 0)
//										.sendToTarget();
//							}
//						}).create();
//		alertDialog1.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog1.show();

        Utils.showTwoButtonDialog(context, context.getString(R.string.huanjingcaiji), context.getString(R.string.shifouhuanjingcaiji),
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, BrainService.HANDLER_MESSAGE_ENVIRONMENTAL_COLLECTION_BEGIN, -1, 0).sendToTarget();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAlertDialog2(context, mHandler);
                    }
                });
    }

    private static void setAlertDialog2(final Context context,
                                        final Handler mHandler) {
//		alertDialog2 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.huanjingcaiji))
//				.setMessage(
//						context.getString(R.string.huanjingcaiji_duizhunheixian))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 307, 1, 0)
//										.sendToTarget();
//								setAlertDialog3(context, mHandler);
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 307, -1, 0)
//										.sendToTarget();
//							}
//						}).create();
//		alertDialog2.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog2.show();

        Utils.showTwoButtonDialog(context, context.getString(R.string.huanjingcaiji), context.getString(R.string.huanjingcaiji_duizhunheixian),
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, BrainService.HANDLER_MESSAGE_ENVIRONMENTAL_COLLECTION_BEGIN, -1, 0).sendToTarget();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, BrainService.HANDLER_MESSAGE_ENVIRONMENTAL_COLLECTION_BEGIN, 1, 0).sendToTarget();
                        setAlertDialog3(context, mHandler);
                    }
                });
    }

    private static void setAlertDialog3(Context context, final Handler mHandler) {
//		alertDialog3 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.huanjingcaiji))
//				.setMessage(
//						context.getString(R.string.huanjingcaiji_duizhunbaise))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 307, 2, 0)
//										.sendToTarget();
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 307, -1, 0)
//										.sendToTarget();
//							}
//						}).create();
//		alertDialog3.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog3.show();

        Utils.showTwoButtonDialog(context, context.getString(R.string.huanjingcaiji), context.getString(R.string.huanjingcaiji_duizhunbaise),
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, BrainService.HANDLER_MESSAGE_ENVIRONMENTAL_COLLECTION_BEGIN, -1, 0).sendToTarget();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, BrainService.HANDLER_MESSAGE_ENVIRONMENTAL_COLLECTION_BEGIN, 2, 0).sendToTarget();
                    }
                });
    }

    public static void setAlertDialog4(String text, Context context) {
//		alertDialog4 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.huiduzhi))
//				.setMessage(text)
//				.setPositiveButton(context.getString(R.string.shi), null)
//				.setNegativeButton(context.getString(R.string.fou), null)
//				.create();
//		alertDialog4.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog4.show();

        Utils.showTwoButtonDialog(context, context.getString(R.string.huiduzhi), text,
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
    }

    /**
     * 平衡车
     *
     * @param context
     * @param text
     * @param b
     * @param mode
     * @return 窗口
     */
    public static AlertDialog setAlertDialog5(Context context, String text,
                                              boolean b, int mode) {
        AlertDialog ad = null;
        if (mode == 0) {
//			ad = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK)
//					.setTitle(context.getString(R.string.pinghengche))
//					.setMessage(
//					// "\t\t" +
//							text).create();
            ad = Utils.showNoButtonDialog(context, context.getString(R.string.pinghengche), text, true);
        } else {
//			ad = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK)
//					.setTitle(context.getString(R.string.pinghengche))
//					.setMessage("\t\t\t\t" + text).create();
            ad = Utils.showNoButtonDialog(context, context.getString(R.string.pinghengche), "\t\t\t\t" + text, true);
        }
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		ad.setCancelable(b);
        return ad;
    }

    /**
     * 指南针校准
     *
     * @param context
     * @param b
     * @param mHandler
     */
    public static void setAlertDialog6(final Context context, boolean b, final Handler mHandler, final int mode) {
        int stringId;
        if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_M) {
            stringId = R.string.zhinanzhenxuanzhuan;
        } else {
            stringId =  R.string.zhinanzhenbazi;
        }
        Utils.showTwoButtonDialog(context, context.getString(R.string.zhinanzhenjiaozhun), context.getString(stringId),
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, 314, mode, 0).sendToTarget();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_M) {
                            Message.obtain(mHandler, 314, mode, 1).sendToTarget();
                        }
                        setAlertDialog7(context, mHandler, mode);
                    }
                });
//		AlertDialog ad = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.zhinanzhenjiaozhun))
//				.setMessage(
//				// "\t\t" +
//						context.getString(R.string.zhinanzhenbazi))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								setAlertDialog7(context, mHandler, mode);
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 314, mode, 0)
//										.sendToTarget();
//							}
//						}).create();
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		ad.setCancelable(b);
//		ad.show();
    }

    /**
     * 指南针校准
     *
     * @param context
     * @param mHandler
     */
    private static void setAlertDialog7(final Context context, final Handler mHandler, final int mode) {
        Utils.showTwoButtonDialog(context, context.getString(R.string.zhinanzhenjiaozhun), context.getString(R.string.zhinanzhenwancheng),
                context.getString(R.string.fou), context.getString(R.string.shi), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, 314, mode, 0).sendToTarget();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, 314, mode, 0).sendToTarget();
                    }
                });
//		AlertDialog alertDialog7 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.zhinanzhenjiaozhun))
//				.setMessage(
//				//"\t\t" +
//						context.getString(R.string.zhinanzhenwancheng))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 314, mode, 0)
//										.sendToTarget();
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Message.obtain(mHandler, 314, mode, 0)
//										.sendToTarget();
//							}
//						}).create();
//		alertDialog7.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog7.show();
    }

    /**
     * 摄像头
     *
     * @param context
     * @param text
     * @param b
     * @return 窗口
     */
    public static AlertDialog setAlertDialog8(Context context, String text,
                                              String mss, boolean b) {
        LogMgr.d("setAlertDialog8()");
//		AlertDialog ad = null;
//		ad = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK)
//				.setTitle(text).setMessage(mss).create();
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		ad.setCanceledOnTouchOutside(b);
//		return ad;

        return Utils.showNoButtonDialog(context, text, mss, true);
    }

    /**
     * stm 升级
     *
     * @param context
     * @param b
     * @return 窗口
     */
    public static AlertDialog setAlertDialog9(Context context, boolean b, final Handler mHandler) {
//		AlertDialog ad = null;
//		ad = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.shengjigujian))
//				.setMessage(context.getString(R.string.shengjigujian_stm32))
//				.setPositiveButton(context.getString(R.string.shi),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface arg0, int arg1) {
//								Message.obtain(mHandler, 314, 10, -1)
//										.sendToTarget();
//							}
//						})
//				.setNegativeButton(context.getString(R.string.fou),
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						}).create();
//		ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		ad.setCanceledOnTouchOutside(b);
//		return ad;

        String leftBbuttonString = context.getString(R.string.fou);
        String rightBbuttonString = context.getString(R.string.shi);
        if (GlobalConfig.BRAIN_CHILD_TYPE == GlobalConfig.ROBOT_TYPE_CU) {
            leftBbuttonString = context.getString(R.string.later);
            rightBbuttonString = context.getString(R.string.update);
        }
        return Utils.showTwoButtonDialog(context, context.getString(R.string.shengjigujian), context.getString(R.string.shengjigujian_stm32),
                leftBbuttonString, rightBbuttonString, true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do nothing
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message.obtain(mHandler, 314, 10, -1).sendToTarget();
                    }
                });
    }

    /**
     * M轮子进入保护状态
     *
     * @param context
     * @param mHandler
     */
    public static void setAlertDialog10(Context context, final Handler mHandler) {
        if (alertDialog10 != null && alertDialog10.isShowing()) {
            LogMgr.w("setAlertDialog10() M轮子进入保护状态 的提示框已显示");
            return;
        }
        LogMgr.w("setAlertDialog10() 显示 M轮子进入保护状态 的提示框");
        Utils.showSingleButtonDialog(context, context.getString(R.string.tishi), context.getString(R.string.m_robot_blocked),
                context.getString(R.string.queren), true, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Application.getInstance().setMWheelProtected(false);
                        mHandler.sendEmptyMessage(BrainService.HANDLER_MESSAGE_M_BLOCKED_CONFIM);
                    }
                });
//		alertDialog10 = new AlertDialog.Builder(context,
//				AlertDialog.THEME_HOLO_DARK)
//				.setTitle(context.getString(R.string.tishi))
//				.setMessage(context.getString(R.string.m_robot_blocked))
//				.setPositiveButton(context.getString(R.string.queren),
//						new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//								Application.getInstance().setMWheelProtected(
//										false);
//								mHandler.sendEmptyMessage(500);
//							}
//						}).create();
//		alertDialog10.getWindow().setType(
//				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		alertDialog10.show();
    }
    //机器对应的温度过高 的提示框
    public static void setAlertDialog11(Context context, final Handler mHandler) {
        if (alertDialog11 != null && alertDialog11.isShowing()) {
            LogMgr.w("setAlertDialog11() 机器对应的温度过高");
            return;
        }
        LogMgr.w("setAlertDialog11() 显示 机器对应的温度过高 的提示框");
        alertDialog11 = Utils.showNoButtonDialogTempH(context, true, new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(mediaPlayer != null && mediaPlayer.isPlaying()){
//                   mediaPlayer.stop();
//                   mediaPlayer.release();
//               }
                mHandler.sendEmptyMessage(121);
                alertDialog11.dismiss();
                alertDialog11.cancel();
                alertDialog11 = null;
            }
        });
    }
    public static void setAlertDialog11close(Context context){
        if (alertDialog11 != null && alertDialog11.isShowing()) {
            LogMgr.w("setAlertDialog11() 关闭提示框");
            alertDialog11.dismiss();
            alertDialog11.cancel();
            alertDialog11 = null;
        }
    }
    public static void close() {
        alertDialog1 = null;
        alertDialog2 = null;
        alertDialog3 = null;
        alertDialog4 = null;
    }

}
