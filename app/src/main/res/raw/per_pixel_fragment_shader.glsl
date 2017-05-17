precision mediump float;           // Set the default precision to medium. We don't need as high of a 
                                // precision in the fragment shader.
uniform sampler2D u_Texture;      // The input texture.
uniform float u_use_texture ;     // 设置是否使用纹理

varying vec3 v_Position;        // Interpolated position for this fragment.
varying vec4 v_Color;              // This is the color from the vertex shader interpolated across the 
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment.
  
// The entry point for our fragment shader.
void main()                            
{        
	if (u_use_texture < 0.5) {
	 	gl_FragColor = v_Color * v_Color[3];
	} else {
		gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate) * v_Color[3];
	}  
}