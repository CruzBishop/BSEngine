package info.zthings.bsengine.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;


public abstract class BSEUtils {
	public static Color convertAwtColor(java.awt.Color c) {return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());}
	public static Rectangle calculateMid(int containerWidth, int containerHeight, CharSequence str, BitmapFont fnt) {
		GlyphLayout gl = new GlyphLayout(fnt, str);
		return new Rectangle((containerWidth/2)-(gl.width/2), (containerHeight/2)+(gl.height/2), gl.width, gl.height);
	}
}
