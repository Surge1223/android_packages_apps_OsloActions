package com.google.oslo.ui.glow.attributes;

import com.google.oslo.ui.glow.ShaderProgram;

public interface UniformSetter {
    void setUniforms(ShaderProgram shaderProgram);

    void updateUniforms(ShaderProgram shaderProgram);
}

