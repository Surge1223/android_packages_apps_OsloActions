precision highp float;

// device uniforms
uniform vec2 uSize;
uniform vec2 uGlowPosition;
uniform float uAspectRatio;
uniform float uCornerRadius;

// glow uniforms
uniform vec2 uGlowRadius;
uniform vec3 uGradientStops;
uniform vec4 uGradientColor1;
uniform vec4 uGradientColor2;
uniform vec4 uGradientColor3;
uniform float uBlurRadius;
uniform float uOpacity;

// uniforms for constant pulsating glow when in asleep mode
uniform float uPulsateAmp;
uniform float uTime;

// line uniforms
uniform float uLineThickness;
uniform float uLineWidth;
uniform float uLineAlpha;
uniform vec2 uFadeMask;
uniform vec4 uLineColor;
uniform float uLinePosX;

varying vec2 st;

float random (vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

/**
 * Ellipse functions
 */
float ellipseDist(in vec2 _st, in vec2 _ellipsePos, in vec2 _radius) {
    float dist = length((_st - vec2(0.5, 0.) - _ellipsePos) / _radius);
    return dist;
}

vec4 ellipse(in vec2 _st, in vec2 _ellipsePos, in vec2 _radius, in vec2 delta, float noiseInt,
    in vec3 stops, in vec4 color1, in vec4 color2, in vec4 color3) {

    float dist = ellipseDist(_st, _ellipsePos + delta, _radius);

    dist *= ((2. * random(_st + delta) - 1.) * noiseInt + (1. - noiseInt));
    vec4 color = mix(color1, color2, smoothstep(stops.x, stops.y, dist));
    color = mix(color, color3, smoothstep(stops.y, stops.z, dist));
    return color;
}

vec4 glowEllipse(in vec2 _st, in vec2 _ellipsePos, in vec2 _radius, in vec3 stops, in vec4 color1,
    in vec4 color2, in vec4 color3, in float blurRadius) {

    // Simulates kind of a blur.
    vec4 color = ellipse(_st, _ellipsePos, _radius * 1.3, vec2(0.), 0.005, stops, color1, color2,
        color3) * 0.25;
    color += ellipse(_st, _ellipsePos, _radius, vec2(blurRadius, 0.), 0.03, stops, color1, color2,
        color3) * 0.1875;
    color += ellipse(_st, _ellipsePos, _radius, vec2(-blurRadius, 0.), 0.001, stops, color1, color2,
        color3) * 0.1875;
    color += ellipse(_st, _ellipsePos, _radius, vec2(0., -blurRadius), 0.03, stops, color1, color2,
        color3) * 0.1875;
    color += ellipse(_st, _ellipsePos, _radius, vec2(0., blurRadius), 0.002, stops, color1, color2,
        color3) * 0.1875;

    return color;
}

/**
 * Line functions
 */
float lineMask(vec2 st, float width, float radius, vec2 maskHeight) {
    // normalized coord: x = [-1,1], y = [-1,1]
    vec2 centered = abs(st - 0.5) * 2.;
    vec2 centeredAbs = abs(centered);

    // Line Rectangle
    vec2 lines = step(1. - width, centered);
    float rect = lines.x * lines.x + lines.y * lines.y;

    // Radius corners
    float corners = step(1. - radius, min(centeredAbs.x, centeredAbs.y));
    vec2 radiusCenter = vec2(1. - radius);
    float circle = length(centeredAbs - radiusCenter);
    float r = (radius - width);
    float circleLine = smoothstep(1. - 0.001 / r, 1., circle / r);

    return (rect * (1. - corners) + circleLine * corners) * smoothstep(maskHeight.y, maskHeight.x,
        st.y);
}

float glowLine(vec2 st, float x, float mask, float lineMargin) {
    float mixer = abs(st.x - 0.5 - x) * 2.;
    float alpha = 1. - smoothstep(0.0, lineMargin, mixer);
    return alpha * mask;
}

/**
 * Main
 */
void main() {
    // ellipse that draws glow
    vec4 glow = glowEllipse(st, uGlowPosition, uGlowRadius, uGradientStops, uGradientColor1,
        uGradientColor2, uGradientColor3, uBlurRadius);

    // line
    float mask = lineMask(st, uLineThickness * 2., uCornerRadius * 2., uFadeMask);
    float line = glowLine(st, uLinePosX, mask, uLineWidth);

    // time
    float pulsateVal = 1.0 - uPulsateAmp * smoothstep(-0.7, 1.0, sin(uTime * 4.0));

    // combine
    vec4 color = glow * uOpacity * pulsateVal;
    gl_FragColor = mix(color, uLineColor, line * uLineAlpha);
}
