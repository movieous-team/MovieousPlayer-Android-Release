package video.movieous.droid.player.demo.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.util.Random;

public class Utils {

    public static int getRandomNum(int min, int max) {
        return new Random().nextInt(max) % (max - min + 1) + min;
    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));

            final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
            StringBuilder ret = new StringBuilder(bytes.length * 2);
            for (int i = 0; i < bytes.length; i++) {
                ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
                ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
            }
            return ret.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 分秒
     *
     * @param time ms
     */
    public static String fromMMss(long time) {
        if (time < 0) {
            return "00:00";
        }

        int ss = (int) (time / 1000);
        int mm = ss / 60;
        int s = ss % 60;
        int m = mm % 60;
        String strM = String.valueOf(m);
        String strS = String.valueOf(s);
        if (m < 10) {
            strM = "0" + strM;
        }
        if (s < 10) {
            strS = "0" + strS;
        }
        return strM + ":" + strS;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static String getFilePath(Context context, Uri uri) {
        String filepath = "";
        String uriPath = uri.toString();

        // Handle local file and remove url encoding
        if (uriPath.startsWith("file://")) {
            filepath = uriPath.replace("file://", "");
            try {
                return URLDecoder.decode(filepath, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection,
                    null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int column_index = cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filepath = cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e("Path Error", e.toString());
        }
        return filepath;
    }

}
