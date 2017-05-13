package io.github.changjiashuai.leanrnandroidopengl;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import android.os.Build;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/11 09:44.
 *
 * //init
 * createVAO()
 * bindVBO
 * unBindVAO
 *
 * //use
 * useProgram()
 * createVAO()
 * render()
 * unBindVAO()
 *
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VertexArrayObject {

    private int mVAOHandle = -1;

    public int getVAOHandle() {
        return mVAOHandle;
    }

    public void createVAO() {
        int[] arrays = new int[1];
        GLES30.glGenVertexArrays(1, arrays, 0);
        mVAOHandle = arrays[0];
        GLES30.glBindVertexArray(mVAOHandle);
        //bind vbo
//        VertexBufferObject vbo = new VertexBufferObject();
//        vbo.create();
    }

    public void unBindVAO() {
        GLES30.glBindVertexArray(0);
    }
}
