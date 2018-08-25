precision mediump float;
 
varying mediump vec2 textureCoordinate;
 
uniform sampler2D inputImageTexture;

uniform sampler2D inputImageTexture2;

uniform float strength;
const vec3 W = vec3(0.2125, 0.7154, 0.0721);

vec3 ovelayBlender(vec3 Color, vec3 filter){
	vec3 filter_result;
	float luminance = dot(filter, W);

	if(luminance < 0.5)
		filter_result = 2. * filter * Color;
	else
		filter_result = 1. - (1. - (2. *(filter - 0.5)))*(1. - Color);

	return filter_result;
}

void main(){

 vec3 irgb = texture2D(inputImageTexture, textureCoordinate).rgb;
 vec3 filter = texture2D(inputImageTexture2, textureCoordinate).rgb;

  vec3 after_filter = mix(irgb, ovelayBlender(irgb, filter), strength);

  gl_FragColor = vec4(after_filter, 1.);

}