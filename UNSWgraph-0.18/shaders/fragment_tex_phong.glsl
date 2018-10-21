
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightDir;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform int night;
uniform float cutoff;
uniform float attenuation;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
    float angle;
	vec3 m_unit = normalize(m);
    // Compute the s, v and r vectors
    vec3 s = normalize(lightDir).xyz;
    //vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;
    vec3 s = normalize(view_matrix*vec4(lightPos,1)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m_unit));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m_unit,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m_unit,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    angle = dot(-s, vec3(0,0,-1));
    angle = max(angle, 0);
    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);

    if (night == 1) {
//
//        if (acos(angle) < radians(cutoff))
//            outputColor = vec4(1,1,0,1);
//        else
//            outputColor = vec4(0,0,0,0);

        float spotDot = dot(-s, vec3(0,0, -1));
        float spotlight;
        if (spotDot > cutoff) {
            spotlight = pow(spotDot, attenuation);
        } else {
            spotlight = 0;
        }

        vec4 lightSource = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
        outputColor += lightSource + spotlight;
    }
}
