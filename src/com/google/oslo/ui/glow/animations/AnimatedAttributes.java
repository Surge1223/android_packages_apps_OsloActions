package com.google.oslo.ui.glow.animations;

import android.graphics.Color;
import android.graphics.PointF;

class AnimatedAttributes {
    static final String GLOW_OPACITY = "opacity";
    static final String GLOW_POSITION_X = "x";
    static final String GLOW_POSITION_Y = "y";
    static final String GLOW_SCALE = "glowScale";
    static final String LINE_ALPHA = "lineAlpha";
    static final String LINE_POSITION_X = "linePositionX";
    static final String LINE_WIDTH = "lineWidth";
    private Color[] mGlowColors;
    private Position mGlowPosition = new Position();
    private float mGlowScale = 1.0f;
    private float mLineAlpha = 0.0f;
    private Color mLineColor = Color.valueOf(-16777216);
    private float mLinePositionX = 0.0f;
    private float mLineWidth = 1.0f;
    private float mOpacity = 0.0f;

    static class Position {
        private PointF position;

        public Position(Position pos) {
            this(pos.getX(), pos.getY());
        }

        public Position(float x, float y) {
            this.position = new PointF(x, y);
        }

        public Position(float value) {
            this(value, value);
        }

        public Position() {
            this(0.0f);
        }

        public float getX() {
            return this.position.x;
        }

        public void setX(float value) {
            this.position.x = value;
        }

        public float getY() {
            return this.position.y;
        }

        public void setY(float value) {
            this.position.y = value;
        }

        public void set(Position value) {
            setX(value.getX());
            setY(value.getY());
        }

        public PointF get() {
            return this.position;
        }

        public String toString() {
            return "Position{x=" + getX() + ", y=" + getY() + "}";
        }
    }

    public AnimatedAttributes(int numStopsGlow) {
        this.mGlowColors = new Color[numStopsGlow];
        for (int i = 0; i < numStopsGlow; i++) {
            this.mGlowColors[i] = Color.valueOf(-16777216);
        }
    }

    public float getLinePositionX() {
        return this.mLinePositionX;
    }

    public void setLinePositionX(float linePositionX) {
        this.mLinePositionX = linePositionX;
    }

    public float getLineAlpha() {
        return this.mLineAlpha;
    }

    public void setLineAlpha(float lineAlpha) {
        this.mLineAlpha = lineAlpha;
    }

    public Color[] getGlowColors() {
        Color[] colors = new Color[this.mGlowColors.length];
        int i = 0;
        while (true) {
            Color[] colorArr = this.mGlowColors;
            if (i >= colorArr.length) {
                return colors;
            }
            colors[i] = cloneColor(colorArr[i]);
            i++;
        }
    }

    /* access modifiers changed from: protected */
    public void setGlowColors(Color[] glowColors) {
        if (glowColors != null && glowColors.length >= this.mGlowColors.length) {
            int i = 0;
            while (true) {
                Color[] colorArr = this.mGlowColors;
                if (i < colorArr.length) {
                    colorArr[i] = cloneColor(glowColors[i]);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public Color getLineColor() {
        return this.mLineColor;
    }

    /* access modifiers changed from: protected */
    public void setLineColor(Color lineColor) {
        this.mLineColor = cloneColor(lineColor);
    }

    public Position getPosition() {
        return this.mGlowPosition;
    }

    public PointF getGlowPosition() {
        return this.mGlowPosition.get();
    }

    public float getLineWidth() {
        return this.mLineWidth;
    }

    /* access modifiers changed from: protected */
    public void setGlowPosition(Position position) {
        this.mGlowPosition = new Position(position.getX(), position.getY());
    }

    public void setLineWidth(float value) {
        this.mLineWidth = value;
    }

    public float getGlowScale() {
        return this.mGlowScale;
    }

    public float getOpacity() {
        return this.mOpacity;
    }

    public void setGlowScale(float glowScale) {
        this.mGlowScale = glowScale;
    }

    public void setOpacity(float opacity) {
        this.mOpacity = opacity;
    }

    public static Color cloneColor(Color color) {
        if (color == null) {
            return null;
        }
        return Color.valueOf(color.toArgb());
    }

    public String toString() {
        return "AnimatedAttributes{mGlowPosition=" + this.mGlowPosition.toString() + ", mLineAlpha=" + this.mLineAlpha + ", mLineWidth=" + this.mLineWidth + ", mLinePositionX=" + this.mLinePositionX + ", mGlowScale=" + this.mGlowScale + ", mOpacity=" + this.mOpacity + "}";
    }
}

