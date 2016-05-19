package cabana.tk.gifmaker.Base;

import android.app.Application;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by k on 2016-5-15.
 */
public class MyContext extends Application{
    public static android.content.Context mContext;
    public static Handler mHandler;
    public static int ScreenWidth;
    public static int ScreenHeight;
    public static int ScreenDPI;
    public static WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        initScreen();
    }

    private void initContext() {
        mContext = getApplicationContext();
        mHandler = new Handler();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    private void initScreen() {
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        ScreenWidth = metrics.widthPixels;
        ScreenHeight = metrics.heightPixels;
        ScreenDPI = metrics.densityDpi;
    }

}
