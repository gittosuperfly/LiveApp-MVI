package cn.cai.star.liveapp.utils.density;

public interface IDensityAdapter {

    /**
     * 是否适配dpi
     */
    default boolean adaptDpi() {
        return DensityHelper.isEnableGlobalAdaptDpi();
    }
}
