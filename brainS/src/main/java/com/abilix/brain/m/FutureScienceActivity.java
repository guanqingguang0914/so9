package com.abilix.brain.m;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.abilix.brain.Application;
import com.abilix.brain.BrainService;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.R;
import com.abilix.brain.data.AppInfo;
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
 * 未来科学站页面
 * <p>
 * 放 三个内置应用 和 com.abilix.learn.ai.****
 *
 * @author yhd
 */
public class FutureScienceActivity extends BaseFutureActivity {

    private final static int[] InfoName = {R.string.m_myfamily, R.string.m_findfruit, R.string.m_findstationery};
    private final static int[] image = {R.drawable.m_science_family, R.drawable.m_science_fruit, R.drawable.m_science_stationery};

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

        LogMgr.d("FutureScienceActivity-->onDestroy()");
    }

    @Override
    void registerBroadcastReceiver() {
        if (mInstallationBroadcastReceiver == null) {
            mInstallationBroadcastReceiver = new ScienceInstallationBroadcastReceiver();
        }
        super.registerBroadcastReceiver();
    }

    @Override
    void initData() {
        mAppInfos.clear();

        //添加三个自带应用
        //addOriginalApk();
        queryAppInfo();
        sort();
        mViewHoders = new ViewHoder[mAppInfos.size()];
        LogMgr.d("mViewHoders.length : " + mViewHoders.length);
    }

    @Override
    void initViewHolder(boolean isFirst) {
        for (int i = 0; i < mAppInfos.size(); i++) {
            final int j = i;
            ViewHoder vh = mViewHoders[i];
            if (GlobalConfig.APP_PKGNAME_MY_FAMILY.equals(mAppInfos.get(j).getPkgName())) {
                mAppInfos.get(j).setAppLabel(getResources().getString(InfoName[0]));
                mAppInfos.get(j).setAppIcon(getResources().getDrawable(image[0]));
                mAppInfos.get(j).setOrder(0);
            }
            if (GlobalConfig.APP_PKGNAME_GUESSFRUIT.equals(mAppInfos.get(j).getPkgName())) {
                mAppInfos.get(j).setAppLabel(getResources().getString(InfoName[1]));
                mAppInfos.get(j).setAppIcon(getResources().getDrawable(image[1]));
                mAppInfos.get(j).setOrder(1);
            }
            if (GlobalConfig.APP_PKGNAME_GUESSSTUDY.equals(mAppInfos.get(j).getPkgName())) {
                mAppInfos.get(j).setAppLabel(getResources().getString(InfoName[2]));
                mAppInfos.get(j).setAppIcon(getResources().getDrawable(image[2]));
                mAppInfos.get(j).setOrder(2);
            }
            vh.imageView.setImageDrawable(mAppInfos.get(j).getAppIcon());
            vh.textview.setText(mAppInfos.get(i).getAppLabel());
            vh.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isAppInstalled(FutureScienceActivity.this, mAppInfos.get(j).getPkgName())) {
                        Utils.openApp(FutureScienceActivity.this, mAppInfos.get(j).getPkgName(), null, false);
                        MUtils.sIsAppBack = true;
                        MUtils.sIsVoiceAppBack = false;
                    }
                }
            });

            vh.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //前三个是内置应用不能删除 //修改为只内置我的家人,其余可删除
                    if (j < 1) {
                        return true;
                    }

                    if (file_parent.exists()) {
                        String is = FileUtils.readFile(file_parent);
                        if (!is.contains("true")) {
                            delViewPager(j, mAppInfos.get(j));
                        } else {
                            // 家长模式下无法删除
                            Utils.showSingleButtonDialog(FutureScienceActivity.this, getString(R.string.tishi), getString(R
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
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setPageTransformer(true, new ScalePageTransformer());

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void addOriginalApk() {

        for (int i = 0; i < 3; i++) {
            AppInfo appInfo = new AppInfo();
            switch (i) {
                case 0:
                    appInfo.setPkgName(GlobalConfig.APP_PKGNAME_MY_FAMILY);
                    break;

                case 1:
                    appInfo.setPkgName(GlobalConfig.APP_PKGNAME_GUESSFRUIT);
                    break;

                case 2:
                    appInfo.setPkgName(GlobalConfig.APP_PKGNAME_GUESSSTUDY);
                    break;

                default:

                    break;
            }
            appInfo.setAppLabel(getResources().getString(InfoName[i]));
            appInfo.setAppIcon(getResources().getDrawable(image[i]));
            appInfo.setOrder(i);
            mAppInfos.add(appInfo);
        }

    }

    /**
     * 查询apk
     */
    @Override
    void queryAppInfo() {
        mPackageManager = getPackageManager();
        mMainIntent = new Intent(Intent.ACTION_MAIN, null);
        mMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mMainIntent.addCategory(GlobalConfig.APP_ABILIX_LAUNCHER);

        List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(mMainIntent, 0);

        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(mPackageManager));
        if (mAppInfos != null) {
            //ResolveInfo reInfo : resolveInfos)
            for (int i = 0; i < resolveInfos.size(); i++) {
                String activityName = resolveInfos.get(i).activityInfo.name;
                String pkgName = resolveInfos.get(i).activityInfo.packageName;
                String appLabel = (String) resolveInfos.get(i).loadLabel(mPackageManager).toString();
                Drawable icon = resolveInfos.get(i).loadIcon(mPackageManager);
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
                appInfos.setOrder(i + InfoName.length);

                appInfos.setPageType(AppInfo.PAGE_TYPE_APK);
                //如果以 com.abilix.learn.ai. 包名开头
                LogMgr.d("pkgName : " + pkgName);
                if (pkgName.startsWith(MUtils.PKG_NAME_AI)) {
                    LogMgr.d("搜索到Ai包名,加入");
                    mAppInfos.add(appInfos);
                }
                if (GlobalConfig.APP_PKGNAME_MY_FAMILY.equals(appInfos.getPkgName())) {
                    appInfos.setAppLabel(getResources().getString(InfoName[0]));
                    appInfos.setAppIcon(getResources().getDrawable(image[0]));
                    appInfos.setOrder(0);
                    mAppInfos.add(appInfos);
                }
                if (GlobalConfig.APP_PKGNAME_GUESSFRUIT.equals(appInfos.getPkgName())) {
                    appInfos.setAppLabel(getResources().getString(InfoName[1]));
                    appInfos.setAppIcon(getResources().getDrawable(image[1]));
                    appInfos.setOrder(1);
                    mAppInfos.add(appInfos);
                }
                if (GlobalConfig.APP_PKGNAME_GUESSSTUDY.equals(appInfos.getPkgName())) {
                    appInfos.setAppLabel(getResources().getString(InfoName[2]));
                    appInfos.setAppIcon(getResources().getDrawable(image[2]));
                    appInfos.setOrder(2);
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
    class ScienceInstallationBroadcastReceiver extends BaseFutureActivity.InstallationBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogMgr.d("ScienceInstallationBroadcastReceiver onReceive intent.getAction() = " + intent.getAction());
            // 安装
            if (TextUtils.equals(Intent.ACTION_PACKAGE_ADDED, intent.getAction())) {
                BrainUtils.utilisToast(getString(R.string.anzhuangchenggong), FutureScienceActivity.this);
                // 更新页面
                //如果是内置的三个应用,不更新页面
                String packageName = intent.getData().getSchemeSpecificPart();
                if (TextUtils.equals(GlobalConfig.APP_PKGNAME_MY_FAMILY, packageName)
                        //|| TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSFRUIT, packageName)
                        //|| TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSSTUDY, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_INSTANT_MESSAGING, packageName)
                        || TextUtils.equals(GlobalConfig.APP_PKGNAME_BRAINSET, packageName)
                        ) {
                    LogMgr.d("内置应用,不改变界面");
                } else {
                    if (!TextUtils.isEmpty(packageName) && packageName.startsWith(MUtils.PKG_NAME_AI)
                            ||TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSFRUIT, packageName)
                            || TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSSTUDY, packageName)
                            ) {
                        LogMgr.d("zengjia + " + packageName);
                        insertApkView(packageName);
                    }
                }
            }
        }
    }
}
