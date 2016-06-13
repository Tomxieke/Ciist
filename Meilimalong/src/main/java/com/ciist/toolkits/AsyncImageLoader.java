package com.ciist.toolkits;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by CIIST on 2015/11/7 0007.
 * 本程序版权归 成都中联软科智能技术有限公司 所有
 * 网站地址：www.ciist.com
 */
public class AsyncImageLoader {
    public Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler();
    private String pathRoot = "";

    public AsyncImageLoader(String floderName) {
        pathRoot = Environment.getExternalStorageDirectory()
                .getPath() + "/ciist" + "/" + floderName;
    }

    public void LoadImage(final String url, final ImageView iv) {
        if (iv.getImageMatrix() == null) {
        }
        Drawable cacheImage = loadDrawable(url, new AsyncImageLoader.ImageCallback() {
            // 请参见实现：如果第一次加载url时下面方法会执行
            public void imageLoaded(Drawable imageDrawable) {
                iv.setImageDrawable(imageDrawable);
            }
        });
        if (cacheImage != null) {
            iv.setImageDrawable(cacheImage);
        }
    }

    public Drawable loadDrawable(final String imageUrl, final ImageCallback callback) {
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            if (softReference.get() != null) {
                return softReference.get();
            }
        } else if (localSDImage(imageUrl) != null) {
            return localSDImage(imageUrl);
        }
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    final Drawable drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "image.png");
                    imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                    handler.post(new Runnable() {
                        public void run() {
                            callback.imageLoaded(drawable);
                        }
                    });
                    saveFile(drawable, imageUrl);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }

    public Drawable localSDImage(String imageUrl) {
        Bitmap bmpDefaultPic = null;
        String imageSDCardPath = pathRoot + "/image/"
                + imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length()).toLowerCase();
        File file = new File(imageSDCardPath);
        if (!file.exists()) {
            return null;
        }
        bmpDefaultPic = BitmapFactory.decodeFile(imageSDCardPath, null);
        if (bmpDefaultPic != null || bmpDefaultPic.toString().length() > 3) {
            Drawable drawable = new BitmapDrawable(bmpDefaultPic);
            return drawable;
        } else
            return null;
    }

    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable);
    }

    public void saveFile(Drawable dw, String url) {
        try {
            BitmapDrawable bd = (BitmapDrawable) dw;
            Bitmap bm = bd.getBitmap();
            final String fileNa = url.substring(url.lastIndexOf("/") + 1,
                    url.length()).toLowerCase();
            File file = new File(pathRoot + "/image/" + fileNa);
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                File maiduo = new File(pathRoot);
                File ad = new File(pathRoot + "/image");
                if (!maiduo.exists()) {
                    maiduo.mkdir();
                } else if (!ad.exists()) {
                    ad.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
        }
    }
}
