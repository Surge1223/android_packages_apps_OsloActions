package com.google.oslo.ui.glow.attributes;

import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;
import com.google.oslo.ui.glow.ShaderProgram;

public class GlowAttributes implements UniformSetter {
    private static final float DEFAULT_BLUR_INTENSITY = 0.033f;
    public static final int NUM_STOPS = 3;
    private static String UNIFORM_GLOW_BLUR = "uBlurRadius";
    private static String UNIFORM_GLOW_OPACITY = "uOpacity";
    private static String UNIFORM_GLOW_POSITION = "uGlowPosition";
    private static String UNIFORM_GLOW_PULSATE_AMP = "uPulsateAmp";
    private static String UNIFORM_GLOW_RADIUS = "uGlowRadius";
    private static String UNIFORM_GLOW_TIME = "uTime";
    private static String UNIFORM_GRADIENT_COLOR = "uGradientColor";
    private static String UNIFORM_GRADIENT_STOPS = "uGradientStops";
    private float mBlurIntensity = DEFAULT_BLUR_INTENSITY;
    private Color[] mColors = new Color[3];
    private float mOpacity;
    private PointF mPosition = new PointF();
    private float mPulsateAmp;
    private PointF mRadius = new PointF();
    private float mScale;
    private float[] mStops = new float[3];
    private float mTime;

    public GlowAttributes() {
        int i = 0;
        while (true) {
            Color[] colorArr = this.mColors;
            if (i < colorArr.length) {
                colorArr[i] = Color.valueOf(-16777216);
                i++;
            } else {
                return;
            }
        }
    }

    public void setBlurIntensity(float blurIntensity) {
        this.mBlurIntensity = blurIntensity;
    }

    public void setGlowRadius(float width, float height) {
        PointF pointF = this.mRadius;
        pointF.x = width;
        pointF.y = height;
    }

    public void setPosition(float x, float y) {
        PointF pointF = this.mPosition;
        pointF.x = x;
        pointF.y = y;
    }

    public void setColors(Color... colors) {
        int i = 0;
        while (i < colors.length && 3 > i) {
            this.mColors[i] = colors[i];
            i++;
        }
    }

    public void setStops(float... stops) {
        int i = 0;
        while (i < stops.length && 3 > i) {
            this.mStops[i] = stops[i];
            i++;
        }
    }

    public PointF getRadius() {
        return this.mRadius;
    }

    public void setOpacity(float opacity) {
        this.mOpacity = opacity;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setPulsateAmp(float amp) {
        this.mPulsateAmp = amp;
    }

    public void setTime(float time) {
        this.mTime = time;
    }

    public void setUniforms(ShaderProgram program) {
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_GLOW_BLUR), this.mBlurIntensity);
        int gradientStopsHandle = program.getUniformHandle(UNIFORM_GRADIENT_STOPS);
        float[] fArr = this.mStops;
        GLES20.glUniform3f(gradientStopsHandle, fArr[0], fArr[1], fArr[2]);
    }

    public void updateUniforms(ShaderProgram program) {
        int i = 0;
        while (true) {
            Color[] colorArr = this.mColors;
            if (i < colorArr.length) {
                Color color = colorArr[i];
                GLES20.glUniform4f(program.getUniformHandle(UNIFORM_GRADIENT_COLOR + (i + 1)), color.red(), color.green(), color.blue(), color.alpha());
                i++;
            } else {
                GLES20.glUniform1f(program.getUniformHandle(UNIFORM_GLOW_PULSATE_AMP), this.mPulsateAmp);
                GLES20.glUniform1f(program.getUniformHandle(UNIFORM_GLOW_TIME), this.mTime);
                GLES20.glUniform2f(program.getUniformHandle(UNIFORM_GLOW_POSITION), this.mPosition.x, this.mPosition.y);
                GLES20.glUniform2f(program.getUniformHandle(UNIFORM_GLOW_RADIUS), this.mRadius.x * this.mScale, this.mRadius.y * this.mScale);
                GLES20.glUniform1f(program.getUniformHandle(UNIFORM_GLOW_OPACITY), this.mOpacity);
                return;
            }
        }
    }
}

