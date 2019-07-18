package com.zhanghaochen.smalldemos.widget.floatview;

/**
 * @author created by zhanghaochen
 * @date 2019-07-16 5:03 PM
 * 描述：
 */
public class FloatViewUtil {
    private static MonitorFloatView mMonitorFloatView;

    public static MonitorFloatView getTestFloatIns() {
        if (mMonitorFloatView == null) {
            MonitorFloatView.IMonitorFloat.sBinding = new MonitorFloatImpl();
            mMonitorFloatView = new MonitorFloatView(new MonitorFloatView.OnRemoveFromWindow() {
                @Override
                public void onRemove() {
                    mMonitorFloatView = null;
                }
            });
        }
        return mMonitorFloatView;
    }
}
