uniform sampler2D uTexture1;
uniform sampler2D uTexture2;

varying vec3 vColor;
varying vec2 vTexCoord;

void main(){
//    gl_FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
	gl_FragColor = mix(texture2D(uTexture1, vTexCoord), texture2D(uTexture2, vTexCoord), 0.2);
}