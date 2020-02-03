package com.google.oslo.ui.glow;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShaderProgram {
    private static final String TAG = ShaderProgram.class.getSimpleName();
    private Context mContext;
    private int mProgramHandle;

    public ShaderProgram(Context context) {
        this.mContext = context;
    }

    public boolean useGLProgram(int vertexResId, int fragmentResId) {
        this.mProgramHandle = loadShaderProgram(vertexResId, fragmentResId);
        GLES20.glUseProgram(this.mProgramHandle);
        return true;
    }

    public int getAttributeHandle(String name) {
        return GLES20.glGetAttribLocation(this.mProgramHandle, name);
    }

    public int getUniformHandle(String name) {
        return GLES20.glGetUniformLocation(this.mProgramHandle, name);
    }

    private int loadShaderProgram(int vertexId, int fragmentId) {
        return getProgramHandle(getShaderHandle(35633, getShaderResource(vertexId)), getShaderHandle(35632, getShaderResource(fragmentId)));
    }
    
    private String getShaderResource(final int n) {
        final Resources resources = this.mContext.getResources();
        final StringBuilder sb = new StringBuilder();
        Throwable t = null;
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resources.openRawResource(n)));
            try {
                while (true) {
                    final String line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                    sb.append("\n");
                }
                bufferedReader.close();
            }
            finally {
                try {}
                finally {
                    try {
                        bufferedReader.close();
                    }
                    finally {
                        final Throwable t2 = null;
                        t.addSuppressed(t2);
                    }
                }
            }
        }
        catch (IOException | Resources.NotFoundException ex) {
             Throwable t3 = null;
            Log.d(ShaderProgram.TAG, "Can not read the shader source", t3);
            t = null;
        }
        String string;
        if (t == null) {
            string = "";
        }
        else string = t.toString();
        return string;
    }

    private int getShaderHandle(int type, String src) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            String str = TAG;
            Log.d(str, "Create shader failed, type=" + type);
            return 0;
        }
        GLES20.glShaderSource(shader, src);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private int getProgramHandle(int vertexHandle, int fragmentHandle) {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            Log.d(TAG, "Can not create OpenGL ES program");
            return 0;
        }
        GLES20.glAttachShader(program, vertexHandle);
        GLES20.glAttachShader(program, fragmentHandle);
        GLES20.glLinkProgram(program);
        return program;
    }
}

