package cabana.tk.gifmaker.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import cabana.tk.gifmaker.Base.MyContext;
import cabana.tk.gifmaker.R;

/**
 * Created by k on 2015/12/8.
 */
public class FloatView implements View.OnTouchListener {
    private static final String TAG = "FloatView";
    private static final float MOVE_LIMIT = 20;
    private static boolean isShow;
    private static FloatView mFloatView;
    private Context context;
    private WindowManager mWM;
    private View mView;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    private float down_X;
    private float down_Y;
    private long down_Time;
    private float up_X;
    private float up_Y;
    private long up_Time;

    private float down_x;
    private float down_y;
    private float move_x;
    private float move_y;
    private clickListener listener;
    private longPressListener longlistener;

    private FloatView(Context context) {
        this.context = context;
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
    }

    public static FloatView getInstance(Context context) {
        if (mFloatView == null) {
            synchronized (FloatView.class) {
                if (mFloatView == null) {
                    return new FloatView(context);
                }
            }
        }
        return mFloatView;
    }

    public void setOnclickListener(clickListener listener) {
        this.listener = listener;
    }

    public void setOnlongpressListener(longPressListener listener) {
        this.longlistener = listener;
    }

    public void show(String text) {
        if (!isShow) {
            if (mView == null) {
                mView = View.inflate(context, R.layout.layout_floatview, null);
            }
            TextView textView = (TextView) mView.findViewById(R.id.flaotview_text);
            if (text != null)
                textView.setText(text);
            mView.setOnTouchListener(this);
            mWM.addView(mView, mParams);
        }
        isShow = true;
    }

    public void show() {
        if (!isShow) {
            if (mView == null) {
                mView = View.inflate(context, R.layout.layout_floatview, null);
            }
            TextView textView = (TextView) mView.findViewById(R.id.flaotview_text);
            mView.setOnTouchListener(this);
            mWM.addView(mView, mParams);
            isShow = true;
        }
    }

    public void reveal(){
        if (!isShow) {
            if (mView != null) {
                mView.setVisibility(View.VISIBLE);
            }
            isShow = true;
        }
    }


    public void hide() {
        if (isShow) {
            if (mView != null) {
                mView.setVisibility(View.INVISIBLE);
                MyContext.windowManager.updateViewLayout(mView,mParams);
            }
            isShow = false;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down_X = event.getRawX();
                down_Y = event.getRawY();
                down_x = event.getRawX();
                down_y = event.getRawY();
                down_Time = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                up_X = event.getRawX();
                up_Y = event.getRawY();
                up_Time = System.currentTimeMillis();

                float diffx = up_X - down_X;
                float diffy = up_Y - down_Y;
                long diffTime = up_Time - down_Time;
                if (Math.abs(diffx) < 20 && Math.abs(diffy) < 20) {
                    if (diffTime > 2000) {
                        // do the long press event
                        longlistener.onLongPress();
                        Toast.makeText(MyContext.mContext, "Touch event --->long press", Toast.LENGTH_SHORT).show();
                    } else {
                        // do the click event
                        listener.onclick();
                        Toast.makeText(MyContext.mContext, "Touch event --->click", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                move_x = event.getRawX();
                move_y = event.getRawY();

                float diffX = move_x - down_x;
                float diffY = move_y - down_y;
                mParams.x += diffX;
                mParams.y += diffY;
                MyContext.windowManager.updateViewLayout(mView, mParams);
                down_x = move_x;
                down_y = move_y;
                break;
        }
        return false;
    }

    public interface clickListener{
        void onclick();
    }

    public interface longPressListener{
        void onLongPress();
    }
}
