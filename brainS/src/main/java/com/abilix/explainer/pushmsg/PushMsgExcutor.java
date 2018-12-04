package com.abilix.explainer.pushmsg;

import android.os.Handler;
import android.os.Looper;

import com.abilix.brain.Application;
import com.abilix.brain.GlobalConfig;
import com.abilix.explainer.ControlInfo;
import com.abilix.explainer.ExplainMessage;
import com.abilix.explainer.ExplainTracker;
import com.abilix.explainer.ExplainerApplication;
import com.abilix.explainer.ExplainerInitiator;
import com.abilix.explainer.MySensor;
import com.abilix.explainer.camera.CameraStateCallBack;
import com.abilix.explainer.camera.IRobotCamera;
import com.abilix.explainer.camera.RobotCameraStateCode;
import com.abilix.explainer.camera.systemcamera.SystemCamera;
import com.abilix.explainer.camera.usbcamera.UsbCamera;
import com.abilix.explainer.utils.ByteUtils;
import com.abilix.explainer.utils.CommonUtils;
import com.abilix.explainer.utils.FileUtils;
import com.abilix.explainer.utils.LogMgr;
import com.abilix.explainer.utils.PlayerUtils;
import com.abilix.explainer.utils.RecorderUtils;
import com.abilix.explainer.view.ExplainerAlertDialogs;
import com.abilix.explainer.view.IMainActivity;
import com.abilix.explainer.view.MainActivity;

import java.io.File;

/**
 * Created by jingh on 2017/7/17.
 */

public class PushMsgExcutor {
    private IMainActivity mIMainActivity;
    private RecorderUtils mRecorder;
    private PlayerUtils mPlayer;
    private IRobotCamera mIRobotCamera;
    private PushMsgResponse mMsgResponse;
    private MySensor mSensor;
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public PushMsgExcutor(IMainActivity mIMainActivity) {
        this.mIMainActivity = mIMainActivity;
        mRecorder = new RecorderUtils();
        mPlayer = PlayerUtils.getInstance();
        mSensor = MySensor.obtainMySensor(Application.instance);
        mMsgResponse = new PushMsgResponse();
        if (ControlInfo.getMain_robot_type() == ExplainerInitiator.ROBOT_TYPE_C
                || ControlInfo.getMain_robot_type() == ExplainerInitiator.ROBOT_TYPE_S
                || ControlInfo.getMain_robot_type() == ExplainerInitiator.ROBOT_TYPE_H3) {
            //USB摄像头
            mIRobotCamera = UsbCamera.create();
        } else {
            mIRobotCamera = SystemCamera.create();
            if (ControlInfo.getMain_robot_type() == ExplainerInitiator.ROBOT_TYPE_M && (ControlInfo.getChild_robot_type() != GlobalConfig.ROBOT_TYPE_M3S)
                    && ControlInfo.getChild_robot_type() != GlobalConfig.ROBOT_TYPE_M4S) {
                mIRobotCamera.setIsRotate(true);
            }
        }
    }

    public void takePicture(final String imagePath) {
        LogMgr.d("message 拍照");
        mIRobotCamera.takePicture(ExplainerApplication.instance, imagePath, new CameraStateCallBack() {
            @Override
            public void onState(int state) {
                LogMgr.d("拍照状态回调：" + state);
                switch (state) {
                    case RobotCameraStateCode.SAVE_PICTURE_SUCESS:
                        LogMgr.e("onSucess 回调");
                        MainActivity.getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mIMainActivity
                                                .dismissAlertDialog(ExplainerAlertDialogs.ALERTDIALOG_CAMERA_OPENING);
                                        LogMgr.d("显示照片");
                                        mIMainActivity
                                                .showPicture(imagePath);
                                    }
                                });
                        PushMsgTracker.getInstance().getPushMsgHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                mMsgResponse.TakePictureResopnse(1);
                            }
                        });
 /*                       byte[] camSucResp={(byte)0x01,(byte)0x00};
                        ProtocolSender.sendProtocol((byte)ControlInfo.getChild_robot_type(),(byte)0xA7,(byte)0x04,camSucResp);*/
                        break;
                    case RobotCameraStateCode.OPENING_CAMERA:
                        MainActivity.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mIMainActivity
                                        .showAlertDialog(
                                                ExplainerAlertDialogs.ALERTDIALOG_CAMERA_OPENING,
                                                null);
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        mIMainActivity
                                                .dismissAlertDialog(ExplainerAlertDialogs.ALERTDIALOG_CAMERA_OPENING);

                                    }
                                }, 12000);
                            }
                        });

                        break;
                    case RobotCameraStateCode.TAKE_PICTURE_USB_CAMERA_IS_NOT_CONNECTED:
                        MainActivity.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mIMainActivity
                                        .showAlertDialog(
                                                ExplainerAlertDialogs.ALERTDIALOG_CAMERA_CONNECT_ERROR,
                                                null);
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        PushMsgTracker.getInstance().getPushMsgHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mMsgResponse.TakePictureResopnse(0);
                                            }
                                        });
                                        mIMainActivity
                                                .dismissAlertDialog(ExplainerAlertDialogs.ALERTDIALOG_CAMERA_CONNECT_ERROR);

                                    }
                                }, 2000);
                            }
                        });

                        break;
                }
            }
        });
    }

    public void record(String index, int recordTime) {
        LogMgr.d("message 录音");
        final String audioPath = FileUtils.getFilePath(FileUtils.DIR_ABILIX_RECORD, String.valueOf(index), FileUtils._TYPE_3GPP);
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity.showRecordView();
            }
        });
        mRecorder.startRecord(audioPath, recordTime, new RecorderUtils.OnCompletionListener() {
            @Override
            public void onCompletion(int what) {
                if (GlobalConfig.ENABLE_RECORDING_PLAY) {
                    MainActivity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIMainActivity.showPlayRecordView();
                        }
                    });

                    mPlayer.play(audioPath, new PlayerUtils.OnCompletionListener() {
                        @Override
                        public void onCompletion(int state) {
                            MainActivity.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mIMainActivity.dimissRecordView();
                                }
                            });
                            PushMsgTracker.getInstance().getPushMsgHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    mMsgResponse.recordResponse(1);
                                }
                            });
                        }
                    });
                } else {
                    MainActivity.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIMainActivity.dimissRecordView();
                        }
                    });
                    PushMsgTracker.getInstance().getPushMsgHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mMsgResponse.recordResponse(1);
                        }
                    });
                }
            }

            @Override
            public void onMicStatusUpdate(float db) {
                LogMgr.d("onMicStatusUpdate() " + db);
            }
        }, false);
    }


    public void displayText(final String content) {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity.display(content);
            }
        });
    }

    public void displayImage(final String imgPath) {
        File file = new File(imgPath);
        if (file.exists()) {
            MainActivity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIMainActivity.showPicture(imgPath);
                }
            });

        }
    }

    public void stopDisplay() {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity.finishDisplay();
                mIMainActivity.dismissPicture();
            }
        });
    }

    public void playRecord(int index) {
        String path = FileUtils.getFilePath(FileUtils.DIR_ABILIX_RECORD, String.valueOf(index), FileUtils._TYPE_3GPP);
        mPlayer.play(path, completionListener);
    }

    public void requireGryo() {
        if (mSensor.getmO() != null && mSensor.getmG() != null && mSensor.getmS() != null) {
            byte[] data = ByteUtils.byteMerger(
                    ByteUtils.byteMerger(ByteUtils.floatsToByte(mSensor.getmO()),
                            ByteUtils.floatsToByte(mSensor.getmG())), ByteUtils.floatsToByte(mSensor.getmS()));
            LogMgr.d("反馈陀螺仪值");
            mMsgResponse.gyroResponse(data);
        }

    }

    public void stopPlaySound() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }


    public void playSound(String soundSrc, int type) {
        String path;
        switch (type) {
            case 0x01:
                path = FileUtils.getFilePath(FileUtils.DIR_MEDIA_UPLOAD_AUDIO, soundSrc);
                break;
            case 0x00:
            default:
                path = FileUtils.getFilePath(CommonUtils.getMusicDirPath(), soundSrc);
                break;
        }
        mPlayer.play(path, completionListener);
    }

    public void adjustCompass() {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity
                        .showAlertDialog(
                                ExplainerAlertDialogs.ALERTDIALOG_COMPASSADJUST_NOTIFICATION,
                                null);
            }
        });
    }

    private PlayerUtils.OnCompletionListener completionListener = new PlayerUtils.OnCompletionListener() {
        @Override
        public void onCompletion(int state) {
            LogMgr.d("音频播放结束");
            PushMsgTracker.getInstance().getPushMsgHandler().post(new Runnable() {
                @Override
                public void run() {
                    mMsgResponse.playSoundResponse(1);
                }
            });
        }
    };

    public void envCollect() {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity
                        .showAlertDialog(
                                ExplainerAlertDialogs.ALERTDIALOG_GRAY_ISCOLLECT,
                                null);
            }
        });
    }

    public void lineCollect() {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity
                        .showAlertDialog(
                                ExplainerAlertDialogs.ALERTDIALOG_GRAY_BLACKLINE,
                                null);
            }
        });
    }

    public void bgCollect() {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity
                        .showAlertDialog(
                                ExplainerAlertDialogs.ALERTDIALOG_GRAY_WHITEBACKGROUND,
                                null);
            }
        });
    }

    public void grayValues(final String values) {
        MainActivity.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIMainActivity
                        .showAlertDialog(
                                ExplainerAlertDialogs.ALERTDIALOG_GRAY_VALUE,
                                values);
            }
        });
    }

    public void detectMicVol() {
        LogMgr.d("反馈麦克风声音分贝值");
        float micVol = new RecorderUtils().getMicDbValue();
        mMsgResponse.micVolResponse(micVol);
    }

    private boolean isFaceTrackOpened = false;
    private int faceTrackedFlag, posX, posY;

    /**
     * 设置人脸追踪
     * @param mode 0：关闭 1：打开 2:关闭预览 3:开启预览
     */
    public void setFaceTrackMode(int mode) {
        switch (mode) {
            case 0x00://0：关闭
                isFaceTrackOpened = false;
                ((UsbCamera) UsbCamera.create()).stopFaceTrackAct(MainActivity.getActivity());
                break;

            case 0x01://1：打开
                posX = 0;
                posY = 0;
                faceTrackedFlag = 0;
                isFaceTrackOpened = true;
                ((UsbCamera) UsbCamera.create()).startFaceTrackAct(MainActivity.getActivity(), new UsbCamera.FaceTrackCallback() {
                    @Override
                    public void onFaceTrack(int[] position) {
                        try {
                            faceTrackedFlag = position[0];
                            posX = position[1];
                            posY = position[2];
                            LogMgr.d("onFaceTrack() faceTracked: " + faceTrackedFlag + " posXY[" + posX + ", " + posY + "]");
                        } catch (Exception e) {
                            LogMgr.d("onFaceTrack() Error:" + e.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        ExplainMessage finishExplain = new ExplainMessage();
                        finishExplain.setFuciton(ExplainMessage.EXPLAIN_STOP);
                        ExplainTracker.getInstance().doExplainCmd(finishExplain, null);
                    }
                });
                break;

            case 0x02://关闭预览
                ((UsbCamera) UsbCamera.create()).preViewFaceTrack(false);
                break;

            case 0x03://打开预览
                ((UsbCamera) UsbCamera.create()).preViewFaceTrack(true);
                break;

            default:
                break;
        }
        mMsgResponse.setFaceTrackModeResponse(0);
    }

    /**
     * 获取人脸检测结果
     * 0：无人脸；1有人脸
     */
    public void getFaceTrackedFlag() {
        mMsgResponse.faceTrackedFlagResponse(faceTrackedFlag);
    }

    /**
     * 获取人脸追踪坐标
     * @param coor
     * x,y坐标
     */
    public void getFaceTrackedPos(int coor) {
        int pos = (coor == 0) ? posX : posY;
        mMsgResponse.faceTrackedPosResponse(pos);
    }

    public void stopExcute() {
        mMsgResponse.stopExcute();
    }
}
