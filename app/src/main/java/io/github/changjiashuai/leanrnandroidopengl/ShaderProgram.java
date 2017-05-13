package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;
import android.util.Log;

import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/11 08:35.
 */

public class ShaderProgram {

    private static final String TAG = "ShaderProgram";

    public static int createProgram(String vertexSource,
                                    String fragmentSource) {
        int vertexShader = Shader.load(GLES20.GL_VERTEX_SHADER, vertexSource);

        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = Shader.load(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        OpenGLUtils.checkError("glCreateProgram");
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            OpenGLUtils.checkError("glAttachShader vertexShader");
            GLES20.glAttachShader(program, fragmentShader);
            OpenGLUtils.checkError("glAttachShader fragmentShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "createProgram: Could not link program error: " +
                        GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static void useProgram(int program) {
        GLES20.glUseProgram(program);
    }

    public static void deleteProgram(int program) {
        GLES20.glDeleteProgram(program);
    }
}