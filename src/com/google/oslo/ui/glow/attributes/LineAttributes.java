package com.google.oslo.ui.glow.attributes;

import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;
import com.google.oslo.ui.glow.ShaderProgram;

public class LineAttributes implements UniformSetter {
    private static final float DEFAULT_THICKNESS = 2.0f;
    private static final String UNIFORM_LINE_ALPHA = "uLineAlpha";
    private static final String UNIFORM_LINE_COLOR = "uLineColor";
    private static final String UNIFORM_LINE_FADE_MASK = "uFadeMask";
    private static final String UNIFORM_LINE_POSITION_X = "uLinePosX";
    private static final String UNIFORM_LINE_THICKNESS = "uLineThickness";
    private static final String UNIFORM_LINE_WIDTH = "uLineWidth";
    private float mAlpha;
    private Color mColor = Color.valueOf(-16777216);
    private PointF mFadeStops = new PointF();
    private float mPositionX = 0.0f;
    private float mThickness = DEFAULT_THICKNESS;
    private float mWidth;

    public void setThickness(float thickness) {
        this.mThickness = thickness;
    }

    public void setWidth(float width) {
        this.mWidth = width;
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
    }

    public void setFadeStops(float stop1, float stop2) {
        PointF pointF = this.mFadeStops;
        pointF.x = stop1;
        pointF.y = stop2;
    }

    public void setColor(Color color) {
        this.mColor = color;
    }

    public float getPositionX() {
        return this.mPositionX;
    }

    public void setPositionX(float positionX) {
        this.mPositionX = positionX;
    }

    public void setUniforms(ShaderProgram program) {
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_LINE_THICKNESS), this.mThickness);
        GLES20.glUniform2f(program.getUniformHandle(UNIFORM_LINE_FADE_MASK), this.mFadeStops.x, this.mFadeStops.y);
    }

    public void updateUniforms(ShaderProgram program) {
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_LINE_WIDTH), this.mWidth);
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_LINE_ALPHA), this.mAlpha);
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_LINE_POSITION_X), this.mPositionX);
        GLES20.glUniform4f(program.getUniformHandle(UNIFORM_LINE_COLOR), this.mColor.red(), this.mColor.green(), this.mColor.blue(), this.mColor.alpha());
    }
}

