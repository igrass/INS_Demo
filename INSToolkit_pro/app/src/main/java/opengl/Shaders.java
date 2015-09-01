package opengl;

public class Shaders {
    public static String m_BlendingFragmentShader = null;
    public static final String m_DefaultSphereFragmentShader = "precision mediump float;\nuniform sampler2D sTexture;\nuniform vec3 uLightPosition;\nvarying vec2 v_TextureCoord;\nvarying vec3 v_Position;\nvarying vec3 v_Normal;\nvoid main() {\n  vec3 lightVector = normalize(uLightPosition - v_Position);\n  float diffuse = max(dot(v_Normal, lightVector), 0.8);\n  vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0);\n  gl_FragColor = texture2D(sTexture, v_TextureCoord) * diffuseColor * diffuse;\n}\n";
    public static String m_DefaultSphereVertexShader = null;
    public static final String m_PanelFragmentShader = "precision mediump float;\nuniform vec4 aColor;\nvoid main() {\n  gl_FragColor = aColor;\n}\n";
    public static final String m_PanelTexFragmentShader = "varying lowp vec4 vColor;\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform sampler2D sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord)*vColor;\n}\n";
    public static final String m_PanelTexVertexShader = "attribute vec4 aPosition;\nattribute vec2 aTextureCoord;\nvarying vec4 vColor;\nvarying vec2 vTextureCoord;\nuniform vec4 aColor;\nuniform mat4 uMVPMatrix;\nvoid main() {\n  vColor = aColor;\n  vTextureCoord = aTextureCoord;\n  gl_Position = uMVPMatrix * aPosition;\n}\n";
    public static final String m_PanelVertexShader = "attribute vec4 aPosition;\nuniform mat4 uMVPMatrix;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n}\n";

    static {
        m_DefaultSphereVertexShader = "attribute vec4 aPosition;\nattribute vec3 aNormal;\nattribute vec2 aTextureCoord;\nvarying vec3 v_Position;\nvarying vec3 v_Normal;\nvarying vec2 v_TextureCoord;\nuniform vec3 uLightPosition;\nuniform mat4 uMVPMatrix;\nuniform mat4 uMVMatrix;\nvoid main() {\n  v_TextureCoord = aTextureCoord;\n  v_Position = vec3(uMVMatrix * aPosition);\n  v_Normal = vec3(uMVMatrix * vec4(aNormal, 0.0));\n  gl_Position = uMVPMatrix * aPosition;\n}\n";
        m_BlendingFragmentShader = "precision mediump float;\nvarying vec2 v_TextureCoord;\nvarying vec3 v_Position;\nvarying vec3 v_Normal;\nuniform vec3 uLightPosition;\nuniform float uAlpha;\nuniform sampler2D sTexture;\nvoid main() {\n   vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0);\n   vec4 specularColor = vec4(0.7, 0.7, 0.7, 1.0);\n   float distance = length(uLightPosition - v_Position);\n   vec3 lightVector = normalize(uLightPosition - v_Position);\n   float diffuse = max(dot(v_Normal, lightVector), 0.1);\n   diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));\n   float intensity = max(0.0, dot(v_Normal, lightVector));\n   vec4 specularLight = 0.5*pow(intensity, 7.0) * specularColor;   vec4 textureColor = texture2D(sTexture, v_TextureCoord);\n   gl_FragColor = vec4(textureColor.rgb, textureColor.a) * diffuseColor * diffuse + specularLight;\n}\n";
    }
}
