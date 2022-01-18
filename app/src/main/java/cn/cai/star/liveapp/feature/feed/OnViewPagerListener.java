package cn.cai.star.liveapp.feature.feed;

public interface OnViewPagerListener {
    void onPageRelease(boolean isNext, int position);
    void onPageSelected(int position, boolean isBottom);
}
