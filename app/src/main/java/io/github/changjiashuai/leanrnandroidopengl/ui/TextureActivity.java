package io.github.changjiashuai.leanrnandroidopengl.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.github.changjiashuai.leanrnandroidopengl.R;
import io.github.changjiashuai.leanrnandroidopengl.ShaderProgram;
import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;

public class TextureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView mGlSurfaceView = new GLSurfaceView(this);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setRenderer(new MyRenderer());
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGlSurfaceView);
    }

    private class MyRenderer implements GLSurfaceView.Renderer {

        private final float[] mMVPMatrix = new float[16];
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mViewMatrix = new float[16];
        private Square mSquare;
        private int mTextureId1;
        private int mTextureId2;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            mSquare = new Square();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);
            float ratio = (float) width / height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            mTextureId1 = initTexture(R.drawable.container);
            mTextureId2 = initTexture(R.drawable.awesomeface);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            //set the camera position
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            mSquare.draw(mMVPMatrix, mTextureId1, mTextureId2);
        }

        private int initTexture(int resId) {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            int textureId = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            return textureId;
        }
    }

    private class Square {

        private float[] mVertices = {
                //Position          //Color           //Texture Coords
                0.5f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f
        };

        private float[] mTexCoords = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        private short[] mIndices = {
                0, 1, 3,
                1, 2, 3
        };

        private FloatBuffer mVertexBuffer;
        private FloatBuffer mTexCoordBuffer;
        private ShortBuffer mIndexBuffer;

        private int muMVPMatrixIndex;

        private int mProgram;
        private int maPositionIndex;
        private int maColorIndex;
        private int maTexCoordIndex;

        private int muTexture1Index;
        private int muTexture2Index;

        public Square() {
            initVertexData();
            initShader();
        }

        private void initVertexData() {
            ByteBuffer vbb = ByteBuffer.allocateDirect(mVertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            mVertexBuffer = vbb.asFloatBuffer();
            mVertexBuffer.put(mVertices);
            mVertexBuffer.position(0);

            ByteBuffer tbb = ByteBuffer.allocateDirect(mTexCoords.length * 4);
            tbb.order(ByteOrder.nativeOrder());
            mTexCoordBuffer = tbb.asFloatBuffer();
            mTexCoordBuffer.put(mTexCoords);
            mTexCoordBuffer.position(0);

            ByteBuffer ibb = ByteBuffer.allocateDirect(mIndices.length * 2);
            ibb.order(ByteOrder.nativeOrder());
            mIndexBuffer = ibb.asShortBuffer();
            mIndexBuffer.put(mIndices);
            mIndexBuffer.position(0);
        }

        private void initShader() {
            String vertexSource = OpenGLUtils
                    .loadFromAssetsFile("vertex_texture.glsl", getResources());
            String fragmentSource = OpenGLUtils
                    .loadFromAssetsFile("fragment_texture.glsl", getResources());
            mProgram = ShaderProgram
                    .createProgram(vertexSource, fragmentSource);

            muMVPMatrixIndex = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

            maPositionIndex = GLES20.glGetAttribLocation(mProgram, "aPosition");
            maColorIndex = GLES20.glGetAttribLocation(mProgram, "aColor");
            maTexCoordIndex = GLES20.glGetAttribLocation(mProgram, "aTexCoord");

            muTexture1Index = GLES20.glGetUniformLocation(mProgram, "uTexture1");
            muTexture2Index = GLES20.glGetUniformLocation(mProgram, "uTexture2");
        }

        private void draw(float[] mvpMatrix, int texId1, int texId2) {
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(muMVPMatrixIndex, 1, false, mvpMatrix, 0);
            GLES20.glVertexAttribPointer(maPositionIndex, 3, GLES20.GL_FLOAT, false,
                    0, mVertexBuffer);
//            GLES20.glVertexAttribPointer(maColorIndex, 3, GLES20.GL_FLOAT, false,
//                    8 * 4, mVertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoordIndex, 2, GLES20.GL_FLOAT, false,
                    0, mTexCoordBuffer);

            GLES20.glEnableVertexAttribArray(maPositionIndex);
            GLES20.glEnableVertexAttribArray(maColorIndex);
            GLES20.glEnableVertexAttribArray(maTexCoordIndex);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId1);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId2);
            GLES20.glUniform1i(muTexture1Index, 0);
            GLES20.glUniform1i(muTexture2Index, 1);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length,
                    GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.length);

            GLES20.glDisableVertexAttribArray(maPositionIndex);
            GLES20.glDisableVertexAttribArray(maColorIndex);
            GLES20.glDisableVertexAttribArray(maTexCoordIndex);
        }
    }
}