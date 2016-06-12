package info.zthings.bsengine.util;

import com.badlogic.gdx.graphics.Color;


public abstract class BSEUtils {
	public static Color convertAwtColor(java.awt.Color c) {return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());}
}
