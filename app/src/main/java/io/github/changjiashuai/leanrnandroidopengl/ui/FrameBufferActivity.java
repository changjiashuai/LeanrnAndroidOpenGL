package io.github.changjiashuai.leanrnandroidopengl.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.github.changjiashuai.leanrnandroidopengl.R;
import io.github.changjiashuai.leanrnandroidopengl.ShaderProgram;
import io.github.changjiashuai.leanrnandroidopengl.utils.OpenGLUtils;

public class FrameBufferActivity extends AppCompatActivity {

    private static final String TAG = "FrameBufferActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setRenderer(new MyRenderer());
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mSurfaceView);
    }

    private class GLRenderer implements GLSurfaceView.Renderer {

        private float[] mModelMatrix = new float[16];
        private float[] mViewMatrix = new float[16];
        private float[] mProjectionMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];

        private FloatBuffer mCubePositions;
        private FloatBuffer mCubeColors;
        private FloatBuffer mCubeTextureCoordinates;

        private int muMVPMatrixHandle;
        private int muMVMatrixHandle;
        private int muTextureHandle;

        private int maPositionHandle;
        private int maColorHandle;
        private int maTexCoordHandle;

        private final int mBytesPerFloat = 4;
        private final int mPositionDataSize = 3;
        private final int mColorDataSize = 4;
        private final int mTextureCoordinateDataSize = 2;

        private int mProgramHandle;
        private int mTextureId;

        private final float[] cubePositionData = {
                // Front face
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,

                // Right face
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,

                // Back face
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                // Left face
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,

                // Bottom face
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
        };

        // R, G, B, A
        private final float[] cubeColorData = {
                // Front face (red)
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f,

                // Right face (green)
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f,

                // Back face (blue)
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                // Left face (yellow)
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                // Top face (cyan)
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                // Bottom face (magenta)
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f
        };

        // S, T (or X, Y)
        // Texture coordinate data.
        // Because images have a Y axis pointing downward (values increase as you move down the image) while
        // OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
        // What's more is that the texture coordinates are the same for every face.
        private final float[] cubeTextureCoordinateData = {
                // Front face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Right face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Back face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Left face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Top face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                // Bottom face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        public GLRenderer() {
            initVertexData();
        }

        private void initVertexData() {
            //Position
            mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mCubePositions.put(cubePositionData).position(0);

            //Color
            mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mCubeColors.put(cubeColorData).position(0);

            //TexCoord
            mCubeTextureCoordinates = ByteBuffer
                    .allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
        }

        public int loadTexture(final int resourceId) {
            final int[] textureHandle = new int[1];
            GLES20.glGenTextures(1, textureHandle, 0);

            if (textureHandle[0] != 0) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;

                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId, options);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                bitmap.recycle();
            }

            if (textureHandle[0] == 0) {
                throw new RuntimeException("failed to load texture");
            }

            return textureHandle[0];
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);

            // Position the eye in front of the origin.
            final float eyeX = 0.0f;
            final float eyeY = 0.0f;
            final float eyeZ = -0.5f;

            // We are looking toward the distance.
            final float lookX = 0.0f;
            final float lookY = 0.0f;
            final float lookZ = -0.5f;

            // Set our up vector. This is where our head would be pointing were we holding the camera.
            final float upX = 0.0f;
            final float upY = 1.0f;
            final float upZ = 0.0f;

            Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ,
                    upX, upY, upZ);
            final String vertexShader = OpenGLUtils
                    .loadFromAssetsFile("per_pixel_vertex_shader.glsl", getResources());
            final String fragmentShader = OpenGLUtils
                    .loadFromAssetsFile("per_pixel_fragment_shader.glsl", getResources());

            mProgramHandle = ShaderProgram
                    .createProgram(vertexShader, fragmentShader,
                            new String[]{"a_Position", "a_Color", "a_TexCoordinate"});
            mTextureId = loadTexture(R.drawable.awesomeface);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            final float ratio = (float) width / height;
            final float left = -ratio;
            final float right = ratio;
            final float bottom = -1.0f;
            final float top = 1.0f;
            final float near = 1.0f;
            final float far = 10.0f;
            Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            IntBuffer frameBuffer = IntBuffer.allocate(1);
            IntBuffer depthRenderBuffer = IntBuffer.allocate(1);
            IntBuffer texture = IntBuffer.allocate(1);
            int texWidth = 480, texHeight = 480;
            IntBuffer maxRenderBufferSize = IntBuffer.allocate(1);
            GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE, maxRenderBufferSize);
            if (maxRenderBufferSize.get(0) <= texWidth
                    || maxRenderBufferSize.get(0) <= texHeight) {
                //cannot use framebuffer objects as we need to create
                //a depth buffer as a renderbuffer object
                //return with appropriate error

                Log.i(TAG, "onDrawFrame: ");
            }
            GLES20.glGenFramebuffers(1, frameBuffer);
            GLES20.glGenRenderbuffers(1, depthRenderBuffer);
            GLES20.glGenTextures(1, texture);

            //bind texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
                    texWidth, texHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);

            //bind renderer buffer
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderBuffer.get(0));
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                    texWidth, texHeight);

            //bind framebuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(0));
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, texture.get(0), 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderBuffer.get(0));

            //check for framebuffer complete
            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
                // render to texture using fbo
                GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                //do a complete rotation every 10 seconds
                long time = SystemClock.uptimeMillis() % 10000L;
                float angleInDegrees = (360.0f / 10000.0f) * (2 * (time));
//                ShaderProgram.useProgram(mProgramHandle);
                GLES20.glUseProgram(mProgramHandle);

                // Set program handles for cube drawing.
                muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
                muMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
                muTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

                maPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
                maColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
                maTexCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

                // Set the active texture unit to texture unit 0.
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                // Bind the texture to this unit.
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

                // Tell the texture uniform sampler to use this texture in the shader by handing
                // to texture unit 0.
                GLES20.glUniform1i(muTextureHandle, 0);

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, -5.0f);
                Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);

                drawCube();

                // render to window system provided framebuffer
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

                // Do a complete rotation every 10 seconds.
                time = SystemClock.uptimeMillis() % 10000L;
                angleInDegrees = (360.0f / 10000.0f) * ((int) time);
                GLES20.glUseProgram(mProgramHandle);

                muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
                muMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
                muTextureHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");

                maPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
                maColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
                maTexCoordHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");

                // Set the active texture unit to texture unit 0.
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

                // Bind the texture to this unit.
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0)/*mTextureId*/);

                // Tell the texture uniform sampler to use this texture in the shader by binding
                // to texture unit 0.
                GLES20.glUniform1i(muTextureHandle, 0);

                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
                Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
                drawCube();
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }

            // cleanup
            GLES20.glDeleteRenderbuffers(1, depthRenderBuffer);
            GLES20.glDeleteFramebuffers(1, frameBuffer);
            GLES20.glDeleteTextures(1, texture);
        }

        private void drawCube() {

            // Pass in the position information
            mCubePositions.position(0);
            GLES20.glVertexAttribPointer(maPositionHandle, mPositionDataSize, GLES20.GL_FLOAT,
                    false, 0, mCubePositions);
            GLES20.glEnableVertexAttribArray(maPositionHandle);

            // Pass in the color information
            mCubeColors.position(0);
            GLES20.glVertexAttribPointer(maColorHandle, mColorDataSize, GLES20.GL_FLOAT,
                    false, 0, mCubeColors);
            GLES20.glEnableVertexAttribArray(maColorHandle);

            // Pass in the texture coordinate information
            mCubeTextureCoordinates.position(0);
            GLES20.glVertexAttribPointer(maTexCoordHandle, mTextureCoordinateDataSize,
                    GLES20.GL_FLOAT, false, 0, mCubeTextureCoordinates);
            GLES20.glEnableVertexAttribArray(maTexCoordHandle);

            // model * view
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            // Pass in the modelview matrix.
            GLES20.glUniformMatrix4fv(muMVMatrixHandle, 1, false, mMVPMatrix, 0);

            // model * view *projection
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
            // Pass in the combined matrix.
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            // Draw the cube
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        }
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
            mTextureId1 = initTexture(R.drawable.img);
//            mTextureId2 = initTexture(R.drawable.awesomeface);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            //set the camera position
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            mSquare.createFbo(mMVPMatrix, mTextureId1);
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

        private void draw(float[] mvpMatrix, int texId1) {
            GLES20.glUseProgram(mProgram);
            GLES20.glUniformMatrix4fv(muMVPMatrixIndex, 1, false, mvpMatrix, 0);
            GLES20.glVertexAttribPointer(maPositionIndex, 3, GLES20.GL_FLOAT, false,
                    0, mVertexBuffer);
            GLES20.glVertexAttribPointer(maTexCoordIndex, 2, GLES20.GL_FLOAT, false,
                    0, mTexCoordBuffer);

            GLES20.glEnableVertexAttribArray(maPositionIndex);
            GLES20.glEnableVertexAttribArray(maColorIndex);
            GLES20.glEnableVertexAttribArray(maTexCoordIndex);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId1);
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId2);
            GLES20.glUniform1i(muTexture1Index, 0);
//            GLES20.glUniform1i(muTexture2Index, 1);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndices.length,
                    GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

            GLES20.glDisableVertexAttribArray(maPositionIndex);
            GLES20.glDisableVertexAttribArray(maColorIndex);
            GLES20.glDisableVertexAttribArray(maTexCoordIndex);
        }

        private void createFbo(float[] mvpMatrix, int texId1) {
            IntBuffer framebuffer = IntBuffer.allocate(1);
            IntBuffer depthRenderbuffer = IntBuffer.allocate(1);
            IntBuffer texture = IntBuffer.allocate(1);
            int texWidth = 512, texHeight = 512;
            IntBuffer maxRenderbufferSize = IntBuffer.allocate(1);
            GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE,
                    maxRenderbufferSize);
            // check if GL_MAX_RENDERBUFFER_SIZE is >= texWidth and texHeight
            if ((maxRenderbufferSize.get(0) <= texWidth)
                    || (maxRenderbufferSize.get(0) <= texHeight)) {
                // cannot use framebuffer objects as we need to create
                // a depth buffer as a renderbuffer object
                // return with appropriate error
            }

            GLES20.glGenFramebuffers(1, framebuffer);
            // bind the framebuffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer.get(0));

            //----Texture-----//
            GLES20.glGenTextures(1, texture);
            //bindTexture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
                    texWidth, texHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            // specify texture as color attachment
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                    texture.get(0), 0);

            //----RenderBuffer-----//
            GLES20.glGenRenderbuffers(1, depthRenderbuffer);
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,
                    depthRenderbuffer.get(0));
            GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
                    GLES20.GL_DEPTH_COMPONENT16, texWidth, texHeight);
            // specify depth_renderbufer as depth attachment
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
                    GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER,
                    depthRenderbuffer.get(0));

            // check for framebuffer complete
            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
                // render to texture using FBO
                GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                        | GLES20.GL_DEPTH_BUFFER_BIT);

                // Do a complete rotation every 10 seconds.
                long time = SystemClock.uptimeMillis() % 10000L;
                float angleInDegrees = (360.0f / 10000.0f) * (2 * (int) time);

//                GLES20.glUseProgram(mProgram);
//
//                // Set the active texture unit to texture unit 0.
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                // Bind the texture to this unit.
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
//
//                // Tell the texture uniform sampler to use this texture in the
//                // shader by binding to texture unit 0.
//                GLES20.glUniform1i(muTexture1Index, 0);
//
//                drawCube();

                //绘制framebuffer中的Square
                draw(mvpMatrix, texture.get(0));

                // render to window system provided framebuffer
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
                        | GLES20.GL_DEPTH_BUFFER_BIT);

                draw(mvpMatrix, texId1);
            }

            // cleanup
            GLES20.glDeleteRenderbuffers(1, depthRenderbuffer);
            GLES20.glDeleteFramebuffers(1, framebuffer);
            GLES20.glDeleteTextures(1, texture);
        }
    }
}