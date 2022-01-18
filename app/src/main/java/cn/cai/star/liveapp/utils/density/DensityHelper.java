package cn.cai.star.liveapp.utils.density;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import cn.cai.star.liveapp.utils.AppUtils;

import cn.cai.star.liveapp.utils.ReflexUtils;
import cn.cai.star.liveapp.utils.ui.ViewUtils;

/**
 * 基于修改density的屏幕适配方案，摇一摇支持配置基准dpi
 */
public class DensityHelper {

    private final static String TAG = "DensityHelper";

    private final static int ANDROID_STANDARD_DPI = DisplayMetrics.DENSITY_DEFAULT;
    private final static float TARGET_ADAPT_DPI = 148f;
    private final static float TARGET_ADAPT_WIDTH_DP = 414f;
    /**
     * 缩放的最大比例
     */
    private final static float DPI_SCALE_LIMIT = 1.1f;

    private static float sMockDpi = TARGET_ADAPT_DPI;
    private static float sMockDpiScaleLimit = DPI_SCALE_LIMIT;

    /**
     * 全局适配开关
     */
    private static boolean sEnableGlobalAdaptDpi = false;
    /**
     * 设备是否允许适配
     */
    private static boolean sEnableDeviceAdapt = true;
    /**
     * hook修改Bitmap densityDpi是否成功
     */
    private static boolean sHookBitmapSucceed = false;

    private static float sOriginalDensity;
    private static float sOriginalScaledDensity;
    /**
     * 当前设备适配的目标density
     */
    private static float sTargetDensity;
    private static float sTargetScaledDensity;

    private static IDensityProvider mDensityProvider;

    public static void init(Context context, boolean enableGlobalAdaptDpi,
                            @NonNull IDensityProvider provider) {
        if (!(context instanceof Application)) {
            return;
        }
        mDensityProvider = provider;
        sEnableGlobalAdaptDpi = enableGlobalAdaptDpi;
        Application application = (Application) context;
        if (!isProcessReady()) {
            DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
            sOriginalDensity = displayMetrics.density;
            sOriginalScaledDensity = displayMetrics.scaledDensity;
//      float originalDpi = displayMetrics.xdpi / displayMetrics.density;
//      sEnableDeviceAdapt = originalDpi < getTargetAdaptDpi();

            Log.i(TAG, "enableGlobalAdaptDpi=" + sEnableGlobalAdaptDpi
                    + ", enableDeviceAdapt=" + sEnableDeviceAdapt
                    + ", originalDensity=" + sOriginalDensity
                    + ", originalScaledDensity=" + sOriginalScaledDensity);

//      if (!sEnableGlobalAdaptDpi || !sEnableDeviceAdapt) {
//        return;
//      }

            computeTargetDensity(displayMetrics.widthPixels);
            sTargetScaledDensity = sTargetDensity * (displayMetrics.scaledDensity / displayMetrics.density);

            // 提前hook，如果hook不成功，不启用适配
            hookBitmapDefaultDensity();

            if (!sHookBitmapSucceed) {
                return;
            }

            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sOriginalScaledDensity = newConfig.fontScale * sOriginalDensity;
                    }
                    Activity activity = mDensityProvider.getCurrentActivity();
                    if (activity != null) {
                        DensityHelper.setActivityTargetDensity(activity, activity.getResources());
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });

            application.registerActivityLifecycleCallbacks(
                    new Application.ActivityLifecycleCallbacks() {
                        @Override
                        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                            DensityHelper.setActivityTargetDensity(activity, activity.getResources());
                        }

                        @Override
                        public void onActivityStarted(Activity activity) {
                            DensityHelper.setActivityTargetDensity(activity, activity.getResources());
                        }

                        @Override
                        public void onActivityResumed(Activity activity) {
                            DensityHelper.setActivityTargetDensity(activity, activity.getResources());
                        }

                        @Override
                        public void onActivityPaused(Activity activity) {

                        }

                        @Override
                        public void onActivityStopped(Activity activity) {

                        }

                        @Override
                        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                        }

                        @Override
                        public void onActivityDestroyed(Activity activity) {

                        }
                    });
        }

        preInitAdapt(application);

        setAppTargetDensity();
    }

    /**
     * 计算各种条件限定下的目标density
     */
    private static void computeTargetDensity(float widthPix) {
        sTargetDensity = widthPix / TARGET_ADAPT_WIDTH_DP;

//    float minTargetDensity = sOriginalDensity / getDpiScaleLimit();
//    sTargetDensity = Math.max(targetDensity, minTargetDensity);
        Log.i(TAG, "targetDensity=" + sTargetDensity);
    }

    /**
     * 获取缩放阈值
     *
     * @return
     */
    private static float getDpiScaleLimit() {
        return DPI_SCALE_LIMIT;
    }

    /**
     * 某些场景可能比较特殊，可以不进行适配，可以在这里提前初始化缓存起来
     * 例如statusBarHeight
     *
     * @param application
     */
    private static void preInitAdapt(Application application) {
        if (isEnableGlobalAdaptDpi()) {
            ViewUtils.getStatusBarHeight(application);
        }
    }

    /**
     * 修改Bitmap默认的densityDpi参数
     */
    private static void hookBitmapDefaultDensity() {
        try {
            int densityDpi = (int) (ANDROID_STANDARD_DPI * sTargetDensity);
            ReflexUtils.setStaticField(Bitmap.class, "sDefaultDensity", densityDpi);
            sHookBitmapSucceed = true;
        } catch (Throwable e) {
            Log.e(TAG, "hookBitmapDefaultDensity error:" + e.getMessage());
        }
    }

    /**
     * 是否进行全局适配
     *
     * @return
     */
    public static boolean isEnableGlobalAdaptDpi() {
        return sEnableGlobalAdaptDpi && sEnableDeviceAdapt && sHookBitmapSucceed;
    }

    /**
     * 获取原始的density
     *
     * @return
     */
    public static float getOriginalDensity() {
        return sOriginalDensity;
    }

    /**
     * 获取原始的densityDpi
     *
     * @return
     */
    public static int getOriginalDensityDpi() {
        return (int) (sOriginalDensity * ANDROID_STANDARD_DPI);
    }

    /**
     * 设置全局app的density参数
     */
    private static void setAppTargetDensity() {
        Resources appResources = AppUtils.getContext().getResources();
        DisplayMetrics displayMetrics = appResources.getDisplayMetrics();

        final float targetDensity = sTargetDensity;
        final float targetScaledDensity = sTargetScaledDensity;
//    float targetScaledDensity = targetDensity * sOriginalScaledDensity / sOriginalDensity;
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = (int) (ANDROID_STANDARD_DPI * targetDensity);

        Configuration configuration = appResources.getConfiguration();
        configuration.densityDpi = displayMetrics.densityDpi;
        int targetWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);
        int targetHeightDp = (int) (displayMetrics.heightPixels / displayMetrics.density);
        configuration.screenWidthDp = targetWidthDp;
        configuration.screenHeightDp = targetHeightDp;

        apply(Resources.getSystem(), appResources);
    }

    /**
     * 设置activity的density参数
     */
    public static void setActivityTargetDensity(@NonNull Context context) {
        setActivityTargetDensity(context, context.getResources());
    }

    /**
     * 设置activity的density参数，直接传入res参数，防止死循环
     */
    public static void setActivityTargetDensity(@NonNull Context context,
                                                @NonNull Resources activityRes) {
        if (!isProcessReady()) {
            return;
        }
        if (!isEnableGlobalAdaptDpi()) {
            return;
        }

        setAppTargetDensity();

        apply(activityRes, AppUtils.getContext().getResources());
    }

    /**
     * 检查当前进程数据是否初始化
     */
    private static boolean isProcessReady() {
        return !(sTargetDensity <= 0) && !(sOriginalDensity <= 0) && !(sOriginalScaledDensity <= 0);
    }

    private static void apply(@NonNull Resources originRes, @NonNull Resources targetRes) {
        DisplayMetrics originMetrics = originRes.getDisplayMetrics();
        DisplayMetrics targetMetrics = targetRes.getDisplayMetrics();
        originMetrics.density = targetMetrics.density;
        originMetrics.scaledDensity = targetMetrics.scaledDensity;
        originMetrics.densityDpi = targetMetrics.densityDpi;

        Configuration originCfg = originRes.getConfiguration();
        Configuration targetCfg = targetRes.getConfiguration();
        originCfg.densityDpi = targetCfg.densityDpi;
        originCfg.screenWidthDp = targetCfg.screenWidthDp;
        originCfg.screenHeightDp = targetCfg.screenHeightDp;
    }

    /**
     * 设置mock dpi参数，重启生效
     */
    public static void setMockDpi(float mockDpi) {
        sMockDpi = mockDpi;
    }

    /**
     * 设置缩放比例阈值，重启生效
     */
    public static void setMockDpiScaleLimit(float scaleLimit) {
        sMockDpiScaleLimit = scaleLimit;
    }

    public interface IDensityProvider {
        Activity getCurrentActivity();
    }
}
