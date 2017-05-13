package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;
import android.util.Log;

import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/11 08:21.
 */

public class Shader {

    private static final String TAG = "Shader";

    public static int load(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        OpenGLUtils.checkError("glCreateShader");
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            OpenGLUtils.checkError("glShaderSource");
            GLES20.glCompileShader(shader);
            OpenGLUtils.checkError("glCompileShader");
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            OpenGLUtils.checkError("glGetShaderiv");
            if (compiled[0] == 0) {
                Log.e(TAG, "load: Could not compile shader " + shaderType + " error: " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                OpenGLUtils.checkError("glDeleteShader");
                shader = 0;
            }
        }
        return shader;
    }
}
