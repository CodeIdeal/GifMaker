package cabana.tk.gifmaker.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import cabana.tk.gifmaker.Base.Global;
import cabana.tk.gifmaker.Base.MyContext;
import cabana.tk.gifmaker.View.FloatView;

/**
 * Created by k on 2016-5-15.
 */
public class FloatMenuService extends Service{
    private FloatView floatView;
    private MediaProjectionManager mpm;
    private DisplayImageListener mDisplayImageListener;
    private ArrayList<Bitmap> mPics= new ArrayList<>();
    private boolean toggle;
    private HandlerThread gifMakerThread = new HandlerThread("gifMaker",Thread.NORM_PRIORITY);
    private Handler mGifMakerHandler = new Handler(gifMakerThread.getLooper());
    private Runnable mGifMakerTask = new Runnable() {
        @Override
        public void run() {
            if(!toggle){
                mGifMakerHandler.removeCallbacks(this);
                dealThePics();
            }
            mPics.add(Global.getCurrentImage());
            mGifMakerHandler.postDelayed(this,250);
        }

        private void dealThePics() {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatView = FloatView.getInstance(MyContext.mContext);
        floatView.setOnclickListener(new FloatView.clickListener() {
            @Override
            public void onclick() {
                if(!toggle){
                    mDisplayImageListener.displayImage(Global.getCurrentImage());
                }else{
                    toggle = false;
                }
            }
        });

        floatView.setOnlongpressListener(new FloatView.longPressListener() {
            @Override
            public void onLongPress() {
                toggle = true;
                mGifMakerHandler.post(mGifMakerTask);
            }
        });

        floatView.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public FloatMenuService getService(){
            return FloatMenuService.this;
        }
    }

    public void setDisplayImage(DisplayImageListener listener){
        this.mDisplayImageListener = listener;
    }

    public interface DisplayImageListener{
        void displayImage(Bitmap bitmap);
    }

}
