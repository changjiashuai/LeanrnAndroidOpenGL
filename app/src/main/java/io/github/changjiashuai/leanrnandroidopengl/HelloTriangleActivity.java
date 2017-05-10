package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//顶点数组对象: Vertex Array Object VAO
//顶点缓冲对象: Vertex Buffer Object VBO
//顶点缓冲对象: Element Buffer Object EBO Or IndexBuffer Object IBO
public class HelloTriangleActivity extends AppCompatActivity {

    private static final String TAG = "HelloTriangleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_triangle);
    }

    class MyRenderer implements GLSurfaceView.Renderer {

        private int mTextureId;

        public int getTextureId() {
            return mTextureId;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            int[] vertices = initVertices();
            initIndices();
        }

        private float[] initIndices() {
            return null;
        }

        private int[] initVertices() {
            return null;
        }

    }































}