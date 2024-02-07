uniform sampler2D u_texture;

uniform vec2 u_resolution;
uniform vec2 u_campos;


vec4 colorAt(vec2 c){
    vec4 color = texture2D(u_texture, c);
    return color;
}

void main() {


    gl_FragColor = vec4(comp,comp,comp,color.a * ceil(color.a));
}
