package com.abilix.brain.m;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abilix.brain.Application;
import com.abilix.brain.GlobalConfig;
import com.abilix.brain.R;
import com.abilix.brain.TypefaceBaseActivity;
import com.abilix.brain.control.ServerHeartBeatProcesser;
import com.abilix.brain.data.AppInfo;
import com.abilix.brain.ui.ClipViewPager;
import com.abilix.brain.utils.BrainUtils;
import com.abilix.brain.utils.LogMgr;
import com.abilix.brain.utils.Utils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * creater: yhd
 * created: 2018/2/8 10:09
 * 可做删除的页面(子页面 未来科学站,未来酷乐园)
 */

public abstract class BaseFutureActivity extends TypefaceBaseActivity {
    ClipViewPager mViewPager;
    ImageView mIv;
    PackageManager mPackageManager;

    ViewPagerAdapter mAdapter;
    ViewHoder[] mViewHoders;
    List<AppInfo> mAppInfos = new LinkedList<AppInfo>();

    File file_parent = null;
    Intent mMainIntent = null;
    InstallationBroadcastReceiver mInstallationBroadcastReceiver;


    class ViewHoder {
        View view;
        LinearLayout ll;
        ImageView imageView;
        TextView textview;
        ImageView imageViewFrame;
    }

    class ViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mAppInfos.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            ((ViewPager) container).removeView(view);
            view = null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViewHoders[position].view;
            try {
                if (view.getParent() != null) {
                    container.removeView(view);
                }
                view.setTag(position);
                container.addView(view, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        //如果不重写,刷新不会立即刷新页面,POSITION_NONE 代表 Item消失
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    /**
     * 监听apk 安装卸载
     *
     * @author luox
     */
    abstract class InstallationBroadcastReceiver extends BroadcastReceiver{}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application.getInstance().setOrientation(this);
        setContentView(R.layout.activity_m_future);
        mViewPager = (ClipViewPager) findViewById(R.id.m_activity_viewpager);
        findViewById(R.id.page_container).setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return mViewPager.dispatchTouchEvent(event);
                    }
                });

        mIv = (ImageView) findViewById(R.id.m_activity_back);
        mIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });

        file_parent = new File(BrainUtils.ROBOTINFO);

        initData();
        initView(true);
        registerBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mInstallationBroadcastReceiver != null) {
            unregisterReceiver(mInstallationBroadcastReceiver);
        }
    }

    /**
     * 插入apk页面
     */
    void insertApkView(String apkPakName) {
        if (apkPakName != null) {
            if (mMainIntent == null) {
                mMainIntent = new Intent(Intent.ACTION_MAIN, null);
                mMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                mMainIntent.addCategory(GlobalConfig.APP_ABILIX_LAUNCHER);
            }
            List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(mMainIntent, 0);

            Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(mPackageManager));
            if (mAppInfos != null) {
                for (ResolveInfo reInfo : resolveInfos) {
                    String activityName = reInfo.activityInfo.name;
                    String pkgName = reInfo.activityInfo.packageName;
                    if (apkPakName.equals(pkgName)) {
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
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pkgName);
                        AppInfo appInfos = new AppInfo();
                        appInfos.setAppName(activityName);
                        appInfos.setAppLabel(appLabel);
                        appInfos.setPkgName(pkgName);
                        appInfos.setAppIcon(icon);
                        appInfos.setIntent(launchIntent);
                        appInfos.setInstallTime(installed);
                        // appInfos.setApk(true);
                        appInfos.setPageType(AppInfo.PAGE_TYPE_APK);
                        int position = mViewHoders.length;
                        if (GlobalConfig.APP_PKGNAME_MY_FAMILY.equals(appInfos.getPkgName())) {
                            position = 0;
                        } else if (GlobalConfig.APP_PKGNAME_GUESSFRUIT.equals(appInfos.getPkgName())) {
                            position = 1;
                        } else if (GlobalConfig.APP_PKGNAME_GUESSSTUDY.equals(appInfos.getPkgName())) {
                            position = 1;
                            for (AppInfo app : mAppInfos) {
                                if (TextUtils.equals(GlobalConfig.APP_PKGNAME_GUESSFRUIT, app.getPkgName())) {
                                    position = 2;
                                    break;
                                }
                            }
                        }
                        insertPage(position, appInfos);
                        return;
                    } else {
                        continue;
                    }
                }
            }
        }
    }

    /**
     * 插入页面：将appInfo viewHolder添加到列表中 并刷新页面 跳至该页面
     */
    void insertPage(int i, AppInfo appInfo) {
        if (appInfo == null) {
            LogMgr.e("insertPage() appInfo == null");
            return;
        }
        mAppInfos.add(i, appInfo);

        //刷新UI
        mViewHoders = new ViewHoder[mAppInfos.size()];
        initView(false);
        mViewPager.setCurrentItem(i);
    }

    /**
     * 删除页面
     *
     * @param appInfo
     */
    public void delViewPager(final int i, final AppInfo appInfo) {
        Utils.showTwoButtonDialog(this, getString(R.string.tishi), getString(R.string.delete),
                getString(R.string.cancel),
                getString(R.string.determine), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (appInfo != null) {
                            String pageName = null;
                            if (appInfo.getPageType() == AppInfo.PAGE_TYPE_FILE) {
                                String fileName = appInfo.getPathName();
                                pageName = fileName;
                                if (!TextUtils.isEmpty(fileName)) {
                                    File file = new File(fileName);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    file = null;
                                }
                            } else if (appInfo.getPageType() == AppInfo.PAGE_TYPE_APK) {
                                pageName = appInfo.getPkgName();
                                ServerHeartBeatProcesser.getInstance().feedbackToAppStore(appInfo.getPkgName(), true);
                            } else if (appInfo.getPageType() == AppInfo.PAGE_TYPE_RECORD) {
                            } else if (appInfo.getPageType() == AppInfo.PAGE_TYPE_IMAGE) {
                            }

                            int realIndex = mAppInfos.indexOf(appInfo);
                            LogMgr.i("删除对象的原index = " + i + " 真实index = " + realIndex);
//                            mViewHoders.remove(realIndex);
                            mAppInfos.remove(realIndex);
                            //刷新界面
                            mViewHoders = new ViewHoder[mAppInfos.size()];
                            LogMgr.d("mAppInfos.size : " + mAppInfos.size());
                            LogMgr.d("mViewHoders.size : " + mViewHoders.length);
                            initView(false);

                        }
                    }
                });
    }

    void registerBroadcastReceiver() {

        IntentFilter filter1 = new IntentFilter();
        // 安装
        filter1.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter1.addDataScheme("package");
        filter1.setPriority(1000);
        registerReceiver(mInstallationBroadcastReceiver, filter1);
    }

    abstract void initData();

    void initView(boolean isFirst) {
        for (int i = 0; i < mAppInfos.size(); i++) {
            View childview = LayoutInflater.from(this).inflate(
                    R.layout.m_activity_item, null);
            ViewHoder vh = new ViewHoder();
            vh.view = childview;
            vh.ll = (LinearLayout) childview
                    .findViewById(R.id.m_item_ll);

            vh.imageView = (ImageView) childview
                    .findViewById(R.id.m_item_imageview);

            vh.textview = (TextView) childview
                    .findViewById(R.id.m_item_textview);

            vh.imageViewFrame = (ImageView) childview
                    .findViewById(R.id.m_item_iv_frame);

            mViewHoders[i] = vh;
        }
        initViewHolder(isFirst);
    }


    /**
     * 查询apk
     */
     abstract void queryAppInfo();


    void sort() {
        Collections.sort(mAppInfos, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo lhs, AppInfo rhs) {
                if (lhs.getOrder() < rhs.getOrder()) {
                    return -1;
                } else if (lhs.getOrder() > rhs.getOrder()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    abstract void initViewHolder(boolean isFirst);


}
