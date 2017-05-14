uniform mat4 uMVPMatrix;
attribute vec3 aPosition;
attribute vec3 aColor;
attribute vec2 aTexCoord;

varying vec3 vColor;
varying vec2 vTexCoord;

void main() {
	gl_Position = uMVPMatrix * vec4(aPosition, 1.0f);
	vColor = aColor;
//	vTexCoord = vec2(1.0-(aTexCoord.x+0.5), 1.0-aTexCoord.y-0.5);
	vTexCoord = aTexCoord;
}