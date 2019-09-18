package com.github.tommyettinger;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Tommy Ettinger on 9/12/2019.
 */
public class ShaderUtils {
    /**
     * This is the default vertex shader from libGDX.
     */
    public static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "   v_color.a = v_color.a * (255.0/254.0);\n"
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "}\n";
    public static final String fragmentShader =
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "precision mediump float;\n" +
                    "#else\n" +
                    "#define LOWP \n" +
                    "#endif\n" +
                    "varying vec2 v_texCoords;\n" +
                    "varying LOWP vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec3 ycc = 2.0 * vec3(v_color.r * dot(tgt.rgb, bright), (v_color.g - 0.5) + v_color.a * (tgt.r - tgt.b), (v_color.b - 0.5) + v_color.a * (tgt.g - tgt.b));\n" +
                    "   tgt.rgb = clamp(vec3(dot(ycc, vec3(1.0, 0.625, -0.5)), dot(ycc, vec3(1.0, -0.375, 0.5)), dot(ycc, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = ycc.r + 1.5;\n" +
                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) - 0.5) * len;\n" +
//                    "   float len = dot(tgt.rgb, bright * 0.0625) + 1.0;\n" +
//                    "   float adj = fract(52.9829189 * fract(dot(vec4(0.06711056, 0.00583715, 0.7548776662466927, 0.5698402909980532), gl_FragCoord.xyyx))) - 0.5;\n" +
//                    "   float len = fract(dot(tgt.rgb, bright) * dot(sin(gl_FragCoord.xy * 5.6789), vec2(14.743036261279236, 13.580412143837574)));\n" +
//                    "   float adj = len * 0.4 - 0.2;\n" +
//                    "   float len = dot(tgt.rgb, bright * 0.0625) + 0.5;\n" +
//                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), sin(gl_FragCoord.xy + len) + cos(gl_FragCoord.yx)))) - 0.5) * len;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = tgt.a;\n" +
                    "}";
    public static final String fragmentShaderWarmMildLimited =
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "precision mediump float;\n" +
                    "#else\n" +
                    "#define LOWP \n" +
                    "#endif\n" +
                    "varying vec2 v_texCoords;\n" +
                    "varying LOWP vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = dot(used.rgb, bright) + 1.5;\n" +
                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy + len))) - 0.5) * len;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = tgt.a;\n" +
                    "}";

}
