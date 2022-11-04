uniform float u_time;
uniform vec2 u_offset;
uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform vec2 u_campos;

varying vec2 v_texCoords;

void main()
{

/*
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = gl_FragCoord.xy/u_resolution.xy;

    vec2 c = v_texCoords;

    vec2 duv = uv;
    float distortion = sqrt((0.5 - uv.x) * (0.5 - uv.x) + (0.5 - uv.y) * (0.5 - uv.y));
    float lerp = 0.4;
    duv = duv * distortion;

    vec4 tex = texture2D(u_texture, v_texCoords.xy);
    
    float intervals = mod(uv.x - u_time/8., 0.2) + mod(uv.y - u_time/8., 0.2) + mod(uv.x - u_time/4., 0.5) + mod(uv.y - u_time/4., 0.5);
    intervals = intervals;
    float darkness = (abs(uv.x - 0.5) + abs(uv.y - 0.5));
    darkness = darkness * darkness * darkness;
    darkness = darkness/3.;
    vec4 tex2 = vec4(intervals, intervals, intervals, 0.);

    float a = (abs(uv.x - 0.5) + abs(uv.y - 0.5))/((1.6 + 0.4 * -sin(u_time * 2.0)));
    a = a * a * a;

    // Output to screen
    gl_FragColor  = tex * (1.0 - a) + tex2 * a - darkness;
    */
    gl_FragColor = vec4(1, 1, 1, 1);
}