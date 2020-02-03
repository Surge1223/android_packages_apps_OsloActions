package com.google.oslo.ui.glow;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShaderRenderer implements GLSurfaceView.Renderer {
    private final ShaderGlow mGlow;

    public ShaderRenderer(Context pluginContext, Context sysuiContext) {
        mGlow = new ShaderGlow(pluginContext, sysuiContext);
    }

    public ShaderGlow getGlow() {
        return mGlow;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mGlow.init();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mGlow.onSizeChanged(width, height);
    }

    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        GLES20.glEnable(3042);
        GLES20.glBlendFunc(770, 771);
        mGlow.draw();
        GLES20.glDisable(3042);
    }
}

