package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/10 22:51.
 */

public class FrameBufferObject {

    private static final String TAG = "FrameBufferObject";
    private int mFbo;
    private int mRbo;

    public FrameBufferObject() {
    }

    public int getFbo() {
        return mFbo;
    }

    public int getRbo() {
        return mRbo;
    }

    public void bindFrameBufferToRenderBuffer(int width, int height) {
        int[] renderBuffers = new int[1];
        GLES20.glGenRenderbuffers(1, renderBuffers, 0);
        mRbo = renderBuffers[0];
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRbo);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16,
                width, height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mRbo);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException(TAG + "bindFrameBufferToRenderBuffer: Failed to set up render buffer with status" + status + "and error: " + GLES20.glGetError());
        }
    }

    /**
     * @param attachment GL_COLOR_ATTACHMENT0 GL_DEPTH_ATTACHMENT GL_STENCIL_ATTACHMENT
     */
    public void bindFrameBufferToTexture(int textureId, int attachment) {
        int[] frameBuffers = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffers, 0);
        mFbo = frameBuffers[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFbo);

        //1. add attachment and at least one color attachment
        /*target：我们所创建的帧缓冲类型的目标（绘制、读取或两者都有）。
         attachment：我们所附加的附件的类型。现在我们附加的是一个颜色附件。
         需要注意，最后的那个0是暗示我们可以附加1个以上颜色的附件。我们会在后面的教程中谈到。
         textarget：你希望附加的纹理类型。
         texture：附加的实际纹理。
         level：Mipmap level。我们设置为0。*/
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, attachment,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException(TAG + "bindFrameBufferToTexture: Failed to set up texture with status" + status + "and error: " + GLES20.glGetError());
        }
    }

    public void unBindFbo() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFbo);
    }

    public void unBindRbo() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRbo);
    }

    public void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, new int[]{mFbo}, 0);
    }

    public void deleteRenderBuffer() {
        GLES20.glDeleteRenderbuffers(1, new int[]{mRbo}, 0);
    }
}