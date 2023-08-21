uniform sampler2D u_texture;

uniform vec2 u_resolution;
uniform vec2 u_campos;

uniform int u_radius;

varying vec2 v_texCoords;

vec4 colorAt(vec2 c){
    vec4 color = texture2D(u_texture, c);
    return color;
}

void main() {

    vec2 c = v_texCoords.xy;
    vec4 color = colorAt(c);

    for(int x = -u_radius; x < u_radius; x++){
        for(int y = -u_radius; y < u_radius; y++){
            color += colorAt(c + vec2(x, y)/u_resolution);
        }
    }

    color = color/float(u_radius * u_radius);
    float comp = (color.r + color.g + color.b)/3.0;

    gl_FragColor = vec4(comp,comp,comp,color.a * ceil(color.a));
}
