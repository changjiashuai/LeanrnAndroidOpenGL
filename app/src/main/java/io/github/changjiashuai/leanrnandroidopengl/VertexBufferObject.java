package io.github.changjiashuai.leanrnandroidopengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/11 08:11.
 */

public class VertexBufferObject {

    private float[] mVertices;
    private short[] mIndices;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mIndexBuffer;

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public VertexBufferObject(float[] vertices) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        mVertices = vertices;
    }

    public VertexBufferObject(float[] vertices, short[] indices) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        mVertices = vertices;

        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
        mIndices = indices;
    }

    public void create() {
        int[] buffers = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertices.length * 4,
                mVertexBuffer, GLES20.GL_STATIC_DRAW);
    }

    public void createIndices() {
        int[] buffers = new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndices.length * 2,
                mIndexBuffer, GLES20.GL_STATIC_DRAW);
    }

    /**
     * indx: 第一个参数指定我们要配置的顶点属性。还记得我们在顶点着色器中使用layout(location = 0)
     * 定义了position顶点属性的位置值(Location)吗？它可以把顶点属性的位置值设置为0。
     * 因为我们希望把数据传递到这一个顶点属性中，所以这里我们传入0。
     *
     * size: 第二个参数指定顶点属性的大小。顶点属性是一个vec3，它由3个值组成，所以大小是3。
     *
     * type: 第三个参数指定数据的类型，这里是GL_FLOAT(GLSL中vec*都是由浮点数值组成的)。
     *
     * normalized: 下个参数定义我们是否希望数据被标准化(Normalize)。如果我们设置为GL_TRUE，
     * 所有数据都会被映射到0（对于有符号型signed数据是-1）到1之间。我们把它设置为GL_FALSE。
     *
     * stride: 第五个参数叫做步长(Stride)，它告诉我们在连续的顶点属性组之间的间隔。
     * 由于下个组位置数据在3个GLfloat之后， 我们把步长设置为3 *sizeof(GLfloat)。
     * 要注意的是由于我们知道这个数组是紧密排列的（在两个顶点属性之间没有空隙）
     * 我们也可以设置为0来让OpenGL决定具体步长是多少（只有当数值是紧密排列时才可用）。一旦我们有更多的顶点属性，
     * 我们就必须更小心地定义每个顶点属性之间的间隔，
     * 我们在后面会看到更多的例子(译注:这个参数的意思简单说就是从这个属性第二次出现的地方到整个数组0位置之间
     * 有多少字节)。
     *
     * offset: 它表示位置数据在缓冲中起始位置的偏移量(Offset)。由于位置数据在数组的开头，所以这里是0。
     * 我们会在后面详细解释这个参数。
     */
    public void setAttrLocations(int attrHandle) {
        GLES20.glVertexAttribPointer(attrHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, 0);
        GLES20.glEnableVertexAttribArray(attrHandle);
    }

    public int getAttribLocation(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    public void setAttrLocations(int program, String name, int size) {
        int index = GLES20.glGetAttribLocation(program, name);
        GLES20.glVertexAttribPointer(index, size, GLES20.GL_FLOAT, false, size * 4, 0);
        GLES20.glEnableVertexAttribArray(index);
    }

    public void uploadVerticesBuffer(int program, String name, int size) {
        int index = GLES20.glGetAttribLocation(program, name);
        GLES20.glVertexAttribPointer(index, size, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(index);
    }
}