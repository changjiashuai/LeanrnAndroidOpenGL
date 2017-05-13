package io.github.changjiashuai.leanrnandroidopengl.ui;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;
import io.github.changjiashuai.leanrnandroidopengl.R;
import io.github.changjiashuai.leanrnandroidopengl.ShaderProgram;
import io.github.changjiashuai.leanrnandroidopengl.VertexBufferObject;

//顶点数组对象: Vertex Array Object VAO
//顶点缓冲对象: Vertex Buffer Object VBO
//索引缓冲对象: Element Buffer Object EBO Or IndexBuffer Object IBO
public class HelloTriangleActivity extends AppCompatActivity {

    private static final String TAG = "HelloTriangleActivity";
    private GLSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_triangle);
        mSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(new MyRenderer());
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    class MyRenderer implements GLSurfaceView.Renderer {

        private int mProgram;
        private VertexBufferObject mVbo;
        private VertexBufferObject mVboForIndex;

        private float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f, 0.5f, 0.0f
        };

        private float[] vertices2 = {
                0.5f, 0.5f, 0.0f,   // 右上角
                0.5f, -0.5f, 0.0f,  // 右下角
                -0.5f, -0.5f, 0.0f, // 左下角
                -0.5f, 0.5f, 0.0f   // 左上角
        };

        private short[] indices = { // 注意索引从0开始!
                0, 1, 3, // 第一个三角形
                1, 2, 3  // 第二个三角形
        };

        public MyRenderer() {
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

            String vertexShader = OpenGLUtils
                    .loadFromAssetsFile("vertex.glsl", getResources());
            String fragmentShader = OpenGLUtils
                    .loadFromAssetsFile("fragment.glsl", getResources());

            mProgram = ShaderProgram
                    .createProgram(vertexShader, fragmentShader);
            ShaderProgram.useProgram(mProgram);

//            mVbo = new VertexBufferObject(vertices);
            mVboForIndex = new VertexBufferObject(vertices2, indices);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

//            drawByVertex();
            drawByIndex();


//            GLES20.glUseProgram(mProgram);
//            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "position");
//            GLES20.glEnableVertexAttribArray(mPositionHandle);
//            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0,
//                    mVboForIndex.getVertexBuffer());
//
//            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
//                    GLES20.GL_UNSIGNED_SHORT, mVboForIndex.getIndexBuffer());
        }

        private void drawByVertex() {
            mVbo.create();
            mVbo.setAttrLocations(mProgram, "position", 3);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }

        private void drawByIndex() {
//            mVboForIndex.createIndices();
            mVboForIndex.uploadVerticesBuffer(mProgram, "position", 3);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length,
                    GLES20.GL_UNSIGNED_SHORT, mVboForIndex.getIndexBuffer());
        }
    }
}