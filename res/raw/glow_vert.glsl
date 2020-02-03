precision lowp float;

uniform float uAspectRatio;

attribute vec4 aPosition;

varying vec2 st;

void main() {
    gl_Position = aPosition;
    st = (aPosition.xy + vec2(1.)) * .5;
    st.y = (1.0 - st.y) / uAspectRatio;
}
