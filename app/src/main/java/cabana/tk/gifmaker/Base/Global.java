package cabana.tk.gifmaker.Base;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by k on 2016-5-15.
 */
public class Global {
    private static VirtualDisplay mVirtualDisplay;
    private static ImageReader mImageReader;
    private static Surface mSurface;

    public static synchronized VirtualDisplay getVirtualDisplay() {
        return mVirtualDisplay;
    }

    public static synchronized void setVirtualDisplay(VirtualDisplay display) {
        mVirtualDisplay = display;

    }

    public static Surface getSurface() {
        if (mSurface == null) {
            synchronized (Global.class) {
                if (mSurface == null) {
                    mSurface = getImageReader().getSurface();
                    return mSurface;
                }
            }
        }
        return mSurface;
    }

    public static ImageReader getImageReader() {
        if (mImageReader == null) {
            synchronized (Global.class) {
                if (mImageReader == null) {
                    mImageReader = ImageReader.newInstance(MyContext.ScreenWidth, MyContext.ScreenHeight, PixelFormat.RGBA_8888, 300);
                    return mImageReader;
                }
            }
        }
        return mImageReader;
    }

    public static Bitmap getCurrentImage(){
        Image image = mImageReader.acquireLatestImage();
        if(image == null)
            return null;
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);// 缩小为原来的一半
        Bitmap mbitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,matrix,true);
        image.close();
        return mbitmap;
    }
}
