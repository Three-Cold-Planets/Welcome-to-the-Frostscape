uniform sampler2D u_texture;

uniform vec2 u_resolution;
uniform vec2 u_campos;

varying vec2 v_texCoords;

vec4 colorAt(vec2 c){
    vec4 color = texture2D(u_texture, c);
    return color;
}

void main() {
    float r = 5.0;

    vec2 c = v_texCoords.xy;
    vec4 color = colorAt(c);

    for(float x = 0.0; x < r; x++){
        for(float y = 0.0; y < r; y++){
            color += colorAt(c + vec2(x, y)/u_resolution);
        }
    }

    color = color/r/r * 2.0;

    if(color.r + color.b + color.g < 0.5) gl_FragColor = vec4(0, 0, 0, 0);
    gl_FragColor = color;
}
