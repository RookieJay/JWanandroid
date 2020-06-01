package pers.jay.wanandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidQUtils {

    private static final String[] IMAGE_PROJECTION = { MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

    /**
     * 扫描系统相册、视频等，图片、视频选择器都是通过ContentResolver来提供
     */
    public static String getPathWithQ(Context context) {
        Cursor imageCursor = context.getContentResolver()
                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            IMAGE_PROJECTION, null, null,
                                            IMAGE_PROJECTION[4] + " DESC");
        if (imageCursor == null) {
            return null;
        }
        String path = imageCursor.getString(imageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
        String name = imageCursor.getString(imageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
        int id = imageCursor.getInt(imageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
        String folderPath = imageCursor.getString(
                imageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
        String folderName = imageCursor.getString(
                imageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));

        //Android Q 公有目录只能通过Content Uri + id的方式访问，以前的File路径全部无效，如果是Video，记得换成MediaStore.Videos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon()
                                                               .appendPath(String.valueOf(id))
                                                               .build()
                                                               .toString();
        }
        return null;
    }

    /**
     * 判断公有目录文件是否存在，自Android Q开始，公有目录File API都失效，不能直接通过new File(path).exists();判断公有目录文件是否存在，正确方式如下
     */
    public static boolean isAndroidQFileExists(Context context, String path) {
        AssetFileDescriptor afd = null;
        ContentResolver cr = context.getContentResolver();
        try {
            Uri uri = Uri.parse(path);
            afd = cr.openAssetFileDescriptor(uri, "r");
            if (afd == null) {
                return false;
            }
            else {
                close(afd);
            }
        }
        catch (FileNotFoundException e) {
            return false;
        }
        finally {
            close(afd);
        }
        return true;
    }

    private static void close(AssetFileDescriptor afd) {
        try {
            if (afd == null) {
                return;
            }
            afd.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查是否外部存储可用于读取和写入
     * @return
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
