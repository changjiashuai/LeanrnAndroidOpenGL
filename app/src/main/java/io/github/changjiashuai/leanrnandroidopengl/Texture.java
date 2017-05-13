package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;

import java.nio.Buffer;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/10 23:38.
 */

public class Texture {

    private static final String TAG = "Texture";
    private int mTextureId;

    public Texture() {
    }

    public int getTextureId() {
        return mTextureId;
    }

    /**
     * @param format GL_ALPHA, GL_RGB GL_RGBA, GL_LUMINANCE, GL_LUMINANCE_ALPHA
     * @param width  指定纹理图像的宽度，必须是2的n次方。纹理图片至少要支持64个材质元素的宽度
     * @param height 指定纹理图像的高度，必须是2的m次方。纹理图片至少要支持64个材质元素的高度
     * @param type   GL_UNSIGNED_BYTE, GL_UNSIGNED_SHORT_5_6_5, GL_UNSIGNED_SHORT_4_4_4_4,
     *               GL_UNSIGNED_SHORT_5_5_5_1
     * @param pixels 指定内存中指向图像数据的指针
     */
    public void bindTexture(int format, int width, int height, int type, Buffer pixels) {
        int[] textures = new int[1];
        GLES20.glGenTextures(GLES20.GL_TEXTURE_2D, textures, 0);
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
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height,
                0, format, type, pixels);
    }

    public void unBind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }
}
