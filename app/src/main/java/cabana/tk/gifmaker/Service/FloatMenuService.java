package cabana.tk.gifmaker.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cabana.tk.gifmaker.Base.Global;
import cabana.tk.gifmaker.Base.MyContext;
import cabana.tk.gifmaker.View.FloatView;

/**
 * Created by k on 2016-5-15.
 */
public class FloatMenuService extends Service{
    private FloatView floatView;
    private MediaProjectionManager mpm;
    private Context mContext;
    private DisplayImageListener mDisplayImageListener;
    private ClearImageListener mClearImageListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Context
        this.mContext = MyContext.mContext;

        floatView = FloatView.getInstance(MyContext.mContext);
        floatView.setOnclickListener(new FloatView.clickListener() {
            @Override
            public void onclick() {
                mDisplayImageListener.displayImage(Global.getCurrentImage());
            }
        });

        floatView.setOnlongpressListener(new FloatView.longPressListener() {
            @Override
            public void onLongPress() {
                mClearImageListener.ClearImage();
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

    public void setClearImage(ClearImageListener listener){
        this.mClearImageListener = listener;
    }

    public interface DisplayImageListener{
        void displayImage(Bitmap bitmap);
    }

    public interface ClearImageListener{
        void ClearImage();
    }
}
