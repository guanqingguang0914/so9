package com.abilix.brain.m;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.abilix.brain.Application;
import com.abilix.brain.BrainActivity;
import com.abilix.brain.BrainService;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.R;
import com.abilix.brain.data.AppInfo;
import com.abilix.brain.data.BrainDatabaseHelper;
import com.abilix.brain.ui.ScalePageTransformer;
import com.abilix.brain.utils.BrainUtils;
import com.abilix.brain.utils.FileUtils;
import com.abilix.brain.utils.LogMgr;
import com.abilix.brain.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 未来酷乐园页面
 *
 * @author yhd
 */
public class FutureCoolPlayActivity extends BaseFutureActivity {

    //排序相关
    private BrainDatabaseHelper mBrainDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        if (GlobalConfig.BRAIN_TYPE == GlobalConfig.ROBOT_TYPE_M) {
            if (BrainService.getmBrainService() != null
                    && BrainService.getmBrainService().getMService() != null
                    && !Application.getInstance().isFirstStartApplication()
                    && MUtils.sIsAppBack
                    ) {

                if (MUtils.sIsVoiceAppBack) {
                    MSender.getInstance().handActionForBean(MUtils.B_BACK_TO_BRAIN);
                    LogMgr.d("FZXX", "handAction(MUtils.B_BACK_TO_BRAIN)");
                } else {
                    MSender.getInstance().handActionForBean(MUtils.B_CLICK_BACK_TO_BRAIN);
                    LogMgr.d("FZXX", "handAction(MUtils.B_CLICK_BACK_TO_BRAIN)");
                }

            }
        }

        MUtils.sIsAppBack = false;
        MUtils.sIsVoiceAppBack = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogMgr.d("FutureCoolActivity-->onDestroy()");
    }

    @Override
    void registerBroadcastReceiver() {
        if (mInstallationBroadcastReceiver == null) {
            mInstallationBroadcastReceiver = new CoolInstallationBroadcastReceiver();
        }
        super.registerBroadcastReceiver();
    }

    @Override
    void initData() {
        mAppInfos.clear();
        queryAppInfo();
        sort();
        mViewHoders = new ViewHoder[mAppInfos.size()];
    }

    @Override
    void initViewHolder(boolean isFirst) {
        for (int i = 0; i < mAppInfos.size(); i++) {
            final int j = i;
            ViewHoder vh = mViewHoders[i];
            vh.imageView.setImageDrawable(mAppInfos.get(i).getAppIcon());
            vh.textview.setText(mAppInfos.get(i).getAppLabel());
            vh.imageViewFrame.setVisibility(View.VISIBLE);
            vh.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isAppInstalled(BrainActivity.getmBrainActivity(), mAppInfos.get(j).getPkgName())) {
                        Utils.openApp(FutureCoolPlayActivity.this, mAppInfos.get(j).getPkgName(), null, false);
                        MUtils.sIsAppBack = true;
                        MUtils.sIsVoiceAppBack = false;
                    }
                }
            });

            vh.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String pkgName = mAppInfos.get(j).getPkgName();
                    if (TextUtils.equals(MUtils.APP_GANYAZI, pkgName)
                            || TextUtils.equals(MUtils.APP_ECHOPIANO, pkgName)
                            || TextUtils.equals(MUtils.APP_YUEGAN, pkgName)
                            || TextUtils.equals(MUtils.APP_LEARNEN, pkgName)
                            ) {
                        LogMgr.d("内置apk 不可删除：" + pkgName);
                        return true;
                    }
                    if (file_parent.exists()) {
                        String is = FileUtils.readFile(file_parent);
                        if (!is.contains("true")) {
                            delViewPager(j, mAppInfos.get(j));
                        } else {
                            // 家长模式下无法删除
                            Utils.showSingleButtonDialog(FutureCoolPlayActivity.this, getString(R.string.tishi), getString(R
                                            .string.shezhibrain),
                                    getString(R.string.queren), false, new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //TODO yhd
                                        }
                                    });
                        }
                    } else {
                        delViewPager(j, mAppInfos.get(j));
                    }

                    return true;
                }
            });
        }

        if (isFirst) {
            mAdapter = new ViewPagerAdapter();
            // mViewPager.setCurrentItem(Integer.MAX_VALUE / 2 + 1);
            mViewPager.setOffscreenPageLimit(3);
            // changeListener = new MyChangeListener();
            // mViewPager.setOnPageChangeListener(changeListener);
            mViewPager.setAdapter(mAdapter);
            // mViewPager.setCurrentItem(1);
            mViewPager.setPageTransformer(true, new ScalePageTransformer());
            // mViewPager.setPageMargin(-10);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 查询apk
     */
    @Override
    void queryAppInfo() {
        mPackageManager = getPackageManager();
        //排序相关
        mBrainDatabaseHelper = new BrainDatabaseHelper(this);
        mSqLiteDatabase = mBrainDatabaseHelper.getWritableDatabase();

        mMainIntent = new Intent(Intent.ACTION_MAIN, null);
        mMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mMainIntent.addCategory(GlobalConfig.APP_ABILIX_LAUNCHER);

        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(mMainIntent, 0);

        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(mPackageManager));
        if (mAppInfos != null) {
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name;
                String pkgName = reInfo.activityInfo.packageName;
                String appLabel = (String) reInfo.loadLabel(mPackageManager).toString();
                Drawable icon = reInfo.loadIcon(mPackageManager);
                ApplicationInfo appInfo = null;
                try {
                    appInfo = mPackageManager.getApplicationInfo(pkgName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String appFile = appInfo.sourceDir;
                long installed = new File(appFile).lastModified();
                Intent launchIntent = mPackageManager.getLaunchIntentForPackage(pkgName);
                AppInfo appInfos = new AppInfo();
                appInfos.setAppName(activityName);
                appInfos.setAppLabel(appLabel);
                appInfos.setPkgName(pkgName);
                appInfos.setAppIcon(icon);
                appInfos.setIntent(launchIntent);
                appInfos.setInstallTime(installed);

                appInfos.setPageType(AppInfo.PAGE_TYPE_APK);
                if (TextUtils.equals(GlobalConfig.APP_PKGNAME_BRAINSET, pkgName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSFRUIT, pkgName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSSTUDY, pkgName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_MY_FAMILY, pkgName)
                        || TextUtils.equals(BrainActivity.OCULUSNAME, pkgName)
                        || TextUtils.equals(BrainActivity.OCULUSNAME_OLD, pkgName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_INSTANT_MESSAGING, pkgName)
                        || pkgName.startsWith(MUtils.PKG_NAME_AI)
                        ) {
                    continue;
                } else {
                    mAppInfos.add(appInfos);
                }
            }
        }
    }


    /**
     * 监听apk 安装卸载
     *
     * @author luox
     */
    class CoolInstallationBroadcastReceiver extends BaseFutureActivity.InstallationBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogMgr.d("CoolInstallationBroadcastReceiver onReceive intent.getAction() = " + intent.getAction());
            // 安装
            if (TextUtils.equals(Intent.ACTION_PACKAGE_ADDED, intent.getAction())) {
                BrainUtils.utilisToast(getString(R.string.anzhuangchenggong), FutureCoolPlayActivity.this);
                // 更新页面
                //如果是内置的三个应用,不更新页面
                String packageName = intent.getData().getSchemeSpecificPart();
                if (TextUtils.equals(GlobalConfig.APP_PKGNAME_MY_FAMILY, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSFRUIT, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSSTUDY, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_INSTANT_MESSAGING, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_BRAINSET, packageName)
                        || packageName.startsWith(MUtils.PKG_NAME_AI)
                        ) {
                    LogMgr.d("内置应用,不改变界面");
                } else {
                    insertApkView(packageName);
                }
            }
        }
    }

}
