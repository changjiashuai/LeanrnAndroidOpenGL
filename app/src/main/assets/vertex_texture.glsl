attribute vec3 aPosition;
attribute vec3 aColor;
attribute vec2 aTexCoord;

varying vec3 vColor;
varying vec2 vTexCoord;

void main() {
	gl_Position = vec4(aPosition, 1.0f);
	vColor = aColor;
	vTexCoord = vec2(aTexCoord.x+0.5, 1.0f-aTexCoord.y-0.5);
}