package io.github.changjiashuai.leanrnandroidopengl.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/11 08:43.
 */

public class OpenGLUtils {

    private static final String TAG = "OpenGLUtils";

    public static String loadFromAssetsFile(String fname, Resources resources) {
        String result = null;
        try {
            InputStream is = resources.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            is.close();
            result = new String(buff, "UTF-8");
            result.replaceAll("\\r\\n", "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String loadFromRawFile(Resources resources, int resId) {
        InputStream inputStream = resources.openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "checkError op: " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}