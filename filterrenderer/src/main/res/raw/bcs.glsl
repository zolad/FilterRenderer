precision mediump float;
 
varying mediump vec2 textureCoordinate;
 
uniform sampler2D inputImageTexture;

uniform float brightness;
uniform float contrast;
uniform float saturation;


const vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec3 BrightnessContrastSaturation(vec3 color, float brt, float con, float sat)
{
	vec3 black = vec3(0., 0., 0.);
	vec3 middle = vec3(0.5, 0.5, 0.5);
	float luminance = dot(color, W);
	vec3 gray = vec3(luminance, luminance, luminance);

	vec3 brtColor = mix(black, color, brt);
	vec3 conColor = mix(middle, brtColor, con);
	vec3 satColor = mix(gray, conColor, sat);
	return satColor;
}

void main(){

 vec3 irgb = texture2D(inputImageTexture, textureCoordinate).rgb;
 vec3 bcs_result = BrightnessContrastSaturation(irgb, brightness, contrast, saturation);


  gl_FragColor = vec4(bcs_result, 1.);

}