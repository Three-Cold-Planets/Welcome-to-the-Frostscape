uniform sampler2D u_texture;

uniform vec2 u_resolution;
uniform vec2 u_campos;

varying vec2 v_texCoords;

//Linier Sampling Algo from https://www.rastergrid.com/blog/2010/09/efficient-gaussian-blur-with-linear-sampling/

vec4 colorAt(vec2 c){
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);
    vec4 color = texture2D(u_texture, c);
    return color;
}

void main() {
    float offset[3];
    offset[0] = 0.0;
    offset[1] = 1.3846153846;
    offset[2] = 3.2307692308;

    float weight[3];
    weight[0] = 0.2270270270;
    weight[1] = 0.3162162162;
    weight[2] = 0.0702702703;

    vec2 c = v_texCoords.xy;
    vec4 color = colorAt(c);

    for (int i=1; i<3; i++) {
        color += colorAt(c + vec2(0.0, offset[i])) * weight[i];
        color += colorAt(c - vec2(0.0, offset[i])) * weight[i];
    }

    color = color * 2.0;
    gl_FragColor = color;
}
