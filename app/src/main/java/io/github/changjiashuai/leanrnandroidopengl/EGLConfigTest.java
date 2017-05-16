package io.github.changjiashuai.leanrnandroidopengl;

import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Email: changjiashuai@gmail.com
 *
 * Created by CJS on 2017/5/16 11:06.
 */

public class EGLConfigTest {

    private static final String TAG = "EGLConfigTest";

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config,
                                 int attribute, int defaultValue) {
        int[] val = new int[1];
        if (egl.eglGetConfigAttrib(display, config, attribute, val)) {
            return val[0];
        }
        return defaultValue;
    }

    private void printEGLConfigAttribs(EGL10 egl, EGLDisplay display, EGLConfig config) {
        int value = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_RED_SIZE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_GREEN_SIZE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_BLUE_SIZE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_ALPHA_SIZE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_DEPTH_SIZE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_RENDERABLE_TYPE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_RENDERABLE_TYPE: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLE_BUFFERS, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_SAMPLE_BUFFERS: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_SAMPLES, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_SAMPLES: " + value);

        value = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, -1);
        Log.i(TAG, "printEGLConfigAttribs: EGL_STENCIL_SIZE: " + value);
    }

    private void init(Object nativeWindow) {
        //1.获取EGLDisplay对象
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        //2.初始化与EGLDisplay 之间的连接。
        int[] version = new int[2];
        egl10.eglInitialize(display, version);
        String vendor = egl10.eglQueryString(display, EGL10.EGL_VENDOR);
        Log.i(TAG, "init: vendor: " + vendor);//打印此版本EGL的实现厂商
        String versionStr = egl10.eglQueryString(display, EGL10.EGL_VERSION);
        Log.i(TAG, "init: version: " + versionStr);//打印EGL版本号
        String extension = egl10.eglQueryString(display, EGL10.EGL_EXTENSIONS);
        Log.i(TAG, "init: extension: " + extension);//EGL支持的扩展

        //3.获取EGLConfig对象
        int[] attributes = new int[]{
                EGL10.EGL_RED_SIZE, 8, //指定RGB中R大小（bits）
                EGL10.EGL_GREEN_SIZE, 8,//指定G大小
                EGL10.EGL_BLUE_SIZE, 8,//指定B大小
                EGL10.EGL_ALPHA_SIZE, 8,//指定Alpha大小
                EGL10.EGL_DEPTH_SIZE, 16,//指定深度缓存（Z Buffer）大小
                EGL10.EGL_RENDERABLE_TYPE, 4,//指定渲染api类别
                EGL10.EGL_NONE//总是以EGL10.EGL_NONE结尾
        };//构造需要的特性列表
        EGLConfig config = null;
        int[] configNum = new int[1];
        egl10.eglChooseConfig(display, attributes, null, 0, configNum);
        int num = configNum[0];
        if (num != 0) {
            EGLConfig[] configs = new EGLConfig[num];
            //获取所有满足attributes的configs
            egl10.eglChooseConfig(display, attributes, configs, num, configNum);
            //以某种规则选择一个config，这里使用最简单的规则
            config = configs[0];
        }
        printEGLConfigAttribs(egl10, display, config);

        //4.创建EGLSurface实例
        EGLSurface surface = egl10.eglCreateWindowSurface(display, config, nativeWindow, null);

        //5.创建EGLContext 实例
        int attrs[] = {
                0x3098, 2,
                EGL10.EGL_NONE
        };
        EGLContext context = egl10.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrs);

        //6.连接EGLContext和EGLSurface.
        egl10.eglMakeCurrent(display, surface, surface, context);

        //7.使用GL指令绘制图形
        //onDrawFrame(egl)...

        //8.断开并释放与EGLSurface关联的EGLContext对象
        egl10.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);

        //9.删除EGLSurface对象
        egl10.eglDestroySurface(display, surface);

        //10.删除EGLContext对象
        egl10.eglDestroyContext(display, context);

        //11.终止与EGLDisplay之间的连接。
        egl10.eglTerminate(display);
    }
}