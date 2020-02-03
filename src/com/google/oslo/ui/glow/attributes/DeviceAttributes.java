package com.google.oslo.ui.glow.attributes;

import android.graphics.Point;
import android.opengl.GLES20;
import com.google.oslo.ui.glow.ShaderProgram;

public class DeviceAttributes implements UniformSetter {
    private static String UNIFORM_ASPECT_RATIO = "uAspectRatio";
    private static String UNIFORM_CORNER_RADIUS = "uCornerRadius";
    private static String UNIFORM_SIZE = "uSize";
    private float mAspectRatio = 1.0f;
    private float mCornerRadius;
    private Point mViewSize = new Point(1, 1);

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius / ((float) this.mViewSize.x);
    }

    public void setViewSize(int width, int height) {
        Point point = this.mViewSize;
        point.x = width;
        point.y = height;
        if (height != 0) {
            this.mAspectRatio = ((float) width) / ((float) height);
        }
    }

    public void setUniforms(ShaderProgram program) {
        GLES20.glUniform2f(program.getUniformHandle(UNIFORM_SIZE), (float) this.mViewSize.x, (float) this.mViewSize.y);
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_ASPECT_RATIO), this.mAspectRatio);
        GLES20.glUniform1f(program.getUniformHandle(UNIFORM_CORNER_RADIUS), this.mCornerRadius);
    }

    public void updateUniforms(ShaderProgram program) {
    }

    public String toString() {
        return "DeviceAttributes{\n\tmViewSize{width=" + this.mViewSize.x + ", height=" + this.mViewSize.y + "}\n\tmCornerRadius=" + this.mCornerRadius + "}";
    }
}

