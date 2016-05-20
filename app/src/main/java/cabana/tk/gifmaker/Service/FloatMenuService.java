package cabana.tk.gifmaker.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import cabana.tk.gifmaker.Base.Global;
import cabana.tk.gifmaker.Base.MyContext;
import cabana.tk.gifmaker.Utils.GifHelper;
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
    private HandlerThread gifMakerThread = new HandlerThread("mGifMakerTask");
    private Handler mGifMakerHandler;
    private Runnable mGifMakerTask = new Runnable() {
        @Override
        public void run() {
            mPics.add(Global.getCurrentImage());
            Log.d("kaka", "run: 截屏一张");

            if(!toggle){
                mGifMakerHandler.removeCallbacks(this);
                dealThePics();
            }else{
                mGifMakerHandler.postDelayed(this,250);
            }
        }

        private void dealThePics() {
            Log.d("kaka", "run: 开始合成,共"+mPics.size()+"张图片");
            int size = mPics.size();
            Bitmap [] pics= new Bitmap[size];
            for(int i=0;i<mPics.size();i++){
                pics[i] = mPics.get(i);
            }
            mPics.clear();
            mPics=null;
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"1.gif");
            bitmapsToGif(pics,file.getAbsolutePath(),250);
            pics = null;
            Log.d("kaka", "run: 合成完成");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        gifMakerThread.start();
        mGifMakerHandler = new Handler(gifMakerThread.getLooper());
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

    private void bitmapsToGif(Bitmap[] pic, String newPic, int duration) {
        try {
            GifHelper e = new GifHelper();
            e.setRepeat(-1);//无限轮播
            boolean start = e.start(newPic);
            Log.d("kaka", "bitmapsToGif: 图片开始生成"+start);
            // e.setFrameRate(30f);
            for (int i = 0; i < pic.length; i++) {
                e.setDelay(duration); // 设置播放的延迟时间
                e.addFrame(pic[i]); // 添加到帧中
            }
            boolean finish = e.finish();//刷新任何未决的数据，并关闭输出文件
            Log.d("kaka", "bitmapsToGif: 图片生成完毕"+finish);
            for (Bitmap bit : pic) {
                if (null != bit && !bit.isRecycled())
                    bit.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
