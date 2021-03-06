package com.example.giggle.appmanager.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by leishifang on 2017/3/21 14:33.
 */

public class AppInfo extends BaseInfo {

    /**
     * 包名
     */
    private String packageName;
    private String versionName;

    /**
     * 应用占用空间 cace+code+data
     */
    private long size;
    /**
     * 第一次安装时间
     */
    private long firstInstallTime;
    /**
     * 最近一次更新时间
     */
    private long lastUpdateTime;

    /**
     * Full path to the base APK for this application
     */
    private String apkPath;

    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }

    /**
     * 是否是系统应用
     */
    private boolean isSys;

    public AppInfo(String lable, String packageName, String versionName, Drawable icon, long size, long
            firstInstallTime, long lastUpdateTime, String apkPath, boolean isSystem) {
        this.lable = lable;
        this.packageName = packageName;
        this.versionName = versionName;
        this.icon = icon;
        this.size = size;
        this.firstInstallTime = firstInstallTime;
        this.lastUpdateTime = lastUpdateTime;
        this.apkPath = apkPath;
        this.isSys = isSystem;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return versionName;
    }

    public void setVersion(String version) {
        this.versionName = version;
    }

    public long getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(long firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }
}
