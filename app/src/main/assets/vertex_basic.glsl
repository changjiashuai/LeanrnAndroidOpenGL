
attribute vec3 aPosition;
attribute vec3 aColor;

varying vec3 vColor;

void main() {
	gl_Position = vec4(aPosition, 1.0f);
	vColor = aColor;
}