package cabana.tk.gifmaker.Utils;

import android.graphics.BitmapFactory;

/**
 * Created by k on 2016-5-20.
 */
public class BitmapUtils {
    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;//获取图片的高
        final int width = options.outWidth;//获取图片的框
        int inSampleSize = 4;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;//求出缩放值
    }
}
