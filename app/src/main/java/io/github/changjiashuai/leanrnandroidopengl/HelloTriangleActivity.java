package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
            initTexture();
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        private void initTexture() {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            mTextureId = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);

            //target 指定目标纹理，这个值必须是GL_TEXTURE_2D。
            //level  执行细节级别。0是最基本的图像级别，你表示第N级贴图细化级别。
            //internalformat     指定纹理中的颜色组件，这个取值和后面的format取值必须相同。可选的值有
            //          GL_ALPHA,
            //          GL_RGB,
            //          GL_RGBA,
            //          GL_LUMINANCE,
            //          GL_LUMINANCE_ALPHA 等几种。
            //width  指定纹理图像的宽度，必须是2的n次方。纹理图片至少要支持64个材质元素的宽度
            //height 指定纹理图像的高度，必须是2的m次方。纹理图片至少要支持64个材质元素的高度
            //border 指定边框的宽度。必须为0。
            //format 像素数据的颜色格式，必须和internalformatt取值必须相同。可选的值有
            //          GL_ALPHA,
            //          GL_RGB,
            //          GL_RGBA,
            //          GL_LUMINANCE,
            //          GL_LUMINANCE_ALPHA 等几种。
            //type 指定像素数据的数据类型。可以使用的值有
            //          GL_UNSIGNED_BYTE,
            //          GL_UNSIGNED_SHORT_5_6_5,
            //          GL_UNSIGNED_SHORT_4_4_4_4,
            //          GL_UNSIGNED_SHORT_5_5_5_1
            //pixels      指定内存中指向图像数据的指针

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 512, 512,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
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
            GLES20.glGenFramebuffers(1, vertices, 0);
            GLES20.glGenBuffers(1, vertices, 0);
        }

        private float[] initIndices() {
            return null;
        }

        private int[] initVertices() {
            return null;
        }


        private void deleteFbo(int[] frameBuffers){
            GLES20.glDeleteFramebuffers(1, frameBuffers, 0);
        }
    }































}