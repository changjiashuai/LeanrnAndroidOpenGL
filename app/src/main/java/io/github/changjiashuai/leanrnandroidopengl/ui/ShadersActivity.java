package io.github.changjiashuai.leanrnandroidopengl.ui;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.github.changjiashuai.leanrnandroidopengl.ShaderProgram;
import io.github.changjiashuai.leanrnandroidopengl.VertexBufferObject;
import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;

public class ShadersActivity extends AppCompatActivity {

    private static final String TAG = "ShadersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(new MyRenderer());
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mSurfaceView);
    }

    private class MyRenderer implements GLSurfaceView.Renderer {

        private int mProgram;
        private VertexBufferObject mVbo;

        private float[] mVertices = {
                //Position
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f,
        };
        private float[] mVerticesWithColors = {
                //Position          //Color
                -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f
        };


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
//            initShaderProgramWithColor();
            initShaderProgramWithoutColor();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//            vertexWithColor();
            vertexWithoutColor();

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }

        private void initShaderProgramWithColor() {
            String vertexSource = OpenGLUtils
                    .loadFromAssetsFile("vertex_basic.glsl", getResources());
            String fragmentSource = OpenGLUtils
                    .loadFromAssetsFile("fragment_basic.glsl", getResources());
            mProgram = ShaderProgram
                    .createProgram(vertexSource, fragmentSource);
            ShaderProgram.useProgram(mProgram);
        }

        private void vertexWithColor() {
            mVbo = new VertexBufferObject(mVerticesWithColors);
            mVbo.create();

            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    6 * 4, 0);
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            int mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
            GLES20.glVertexAttribPointer(mColorHandle, 3, GLES20.GL_FLOAT, false,
                    6 * 4, 3 * 4);
            GLES20.glEnableVertexAttribArray(mColorHandle);
        }

        private void initShaderProgramWithoutColor() {
            String vertexSource = OpenGLUtils
                    .loadFromAssetsFile("vertex_basic_without_color.glsl", getResources());
            String fragmentSource = OpenGLUtils
                    .loadFromAssetsFile("fragment_basic_without_varying_color.glsl",
                            getResources());
            mProgram = ShaderProgram
                    .createProgram(vertexSource, fragmentSource);
            ShaderProgram.useProgram(mProgram);
        }

        private void vertexWithoutColor() {
            //uniform
            mVbo = new VertexBufferObject(mVertices);
            mVbo.create();

            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    3 * 4, 0);
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            int muColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
            float mGreenColor = new Random().nextFloat();
            Log.i(TAG, "vertexWithoutColor: " + mGreenColor);
            GLES20.glUniform3f(muColorHandle, 0.0f, mGreenColor, 0.0f);
        }
    }
}