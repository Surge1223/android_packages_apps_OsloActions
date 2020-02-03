package com.google.oslo.ui.glow;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import com.google.oslo.actions.R;

public class ShaderView extends GLSurfaceView {
    private  ShaderGlow mGlow = null;
    private  ShaderRenderer mRenderer;

    public ShaderView(Context pluginContext, Context sysuiContext) {
        super(pluginContext);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(1);
        setZOrderOnTop(true);
        mRenderer = new ShaderRenderer(pluginContext, sysuiContext);
        setRenderer(mRenderer);
        setRenderMode(0);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getContext().getResources().getDimensionPixelSize(R.dimen.glow_drawing_height);
        setLayoutParams(params);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        setLayerType(2, paint);
    }

    public ShaderGlow getGlow() {
        return mGlow;
    }
}

