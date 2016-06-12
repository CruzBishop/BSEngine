package info.zthings.bsengine.classes;

import info.zthings.bsengine.abstracts.AButtonListener;
import info.zthings.utils.MiscUtil;
import info.zthings.utils.exceptions.UnintendedBehaviorException;
import info.zthings.utils.exceptions.UnknownCaseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class BSEAssetManager implements Disposable, Iterable<String> {
	private final AssetManager ass;
	/** Fullpath -> Garbage */
	private final HashMap<String, Disposable> garbage = new HashMap<String, Disposable>();
	/** Alias -> fullpath */
	private final HashMap<String, String> aliases = new HashMap<String, String>();
	/** Fullpath */
	private final ArrayList<String> singulars = new ArrayList<String>();
	
	public BSEAssetManager() {
		ass = new AssetManager();
		
		FileHandleResolver resolver = new InternalFileHandleResolver();
		ass.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		ass.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}
	

	public void load(String filepath, String alias, Class<?> type, boolean singular) {
		filepath = parsePrefix(filepath, type);
		
		if (type.equals(Sprite.class)) type = Texture.class;
		else if (type.equals(ImageButton.class)) type = Texture.class;
		
		if (singular) this.singulars.add(filepath);
		this.aliases.put(alias, filepath);
		
		System.out.println("Mapped alias " + alias + "->" + filepath);
		
		if (ass.getLoader(type) != null) this.ass.load(filepath, type);
		else throw new UnsupportedOperationException("No loader available for type " + type.getSimpleName());
	}
	
	public void loadFont(String filepath, String alias, FreeTypeFontParameter ftfp, boolean singular) {
		FreeTypeFontLoaderParameter params = new FreeTypeFontLoaderParameter();
		filepath = "fonts/"+filepath+".ttf";
		params.fontFileName = filepath;
		params.fontParameters.size = ftfp.size;
		params.fontParameters = ftfp;
		ass.load(alias+".ttf", BitmapFont.class, params);
		
		if (singular) this.singulars.add(alias+".ttf");
		this.aliases.put(alias+".ttf", filepath);
		System.out.println("Mapped alias " + alias + "->" + filepath);
	}
	public void loadFont(String filepath, String alias, int size, boolean singular) {
		FreeTypeFontParameter ftfp = new FreeTypeFontParameter();
		ftfp.size = size;
		loadFont(filepath, alias, ftfp, singular);
	}
	
	
	public void markGarbage(Disposable dis, String alias, boolean singular) {
		if (singular) this.singulars.add(alias);
		this.garbage.put(alias, dis);
	}
	
	
	/** Creates an ImageButton from the given file */
	public ImageButton createImageButton(String filealias, AButtonListener inputListener) {
		Skin skin = new Skin();
		
		Texture tex = getTexture(filealias);
		skin.add("normal", new TextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight()/3));
		skin.add("hover", new TextureRegion(tex, 0, tex.getHeight()/3, tex.getWidth(), tex.getHeight()/3));
		skin.add("clicked", new TextureRegion(tex, 0, (tex.getHeight()/3)*2, tex.getWidth(), tex.getHeight()/3));
		
		ImageButtonStyle buttStyle = new ImageButtonStyle();
		buttStyle.up = skin.newDrawable("normal");
		buttStyle.over = skin.newDrawable("hover");
		buttStyle.down = skin.newDrawable("clicked");
		
		skin.add("default", buttStyle);
		ImageButton butt = new ImageButton(skin);
		butt.addListener(inputListener);
		markGarbage(skin, "button_"+filealias, true);
		return butt;
	}
	
	
	public Texture getTexture(String filealias) {return getFromAlias(filealias, Texture.class);}
	public Sound getSound(String filealias) {return getFromAlias(filealias, Sound.class);}
	public Music getMusic(String filealias) {return getFromAlias(filealias, Music.class);}
	public BitmapFont getFont(String filealias) {return ass.get(filealias+".ttf", BitmapFont.class);}
	public AssetManager getManager() {return ass;}

	
	public boolean update() {return ass.update();}
	public int size() {return ass.getLoadedAssets();}
	public float getProgress() {return ass.getProgress();}
	public boolean isLoaded(String filealias) {
		if (!this.aliases.containsKey(filealias)) unresolvedAlias(filealias);
		return this.ass.isLoaded(this.aliases.get(filealias));
	}
	
	
	public void unload(String filealias) {
		if (!this.aliases.containsKey(filealias)) unresolvedAlias(filealias);
		this.ass.unload(aliases.get(filealias));
		if (this.singulars.contains(aliases.get(filealias))) throw new UnintendedBehaviorException("Resource '" + filealias + "' has a singular-lifetime and will get automatically disposed");
	}
	
	
	@Override
	public void dispose() {
		for (String a : ass.getAssetNames()) {
			System.out.println("Disposing " + a);
			ass.unload(a);
		}
		
		HashMap<String, Disposable> copy = new HashMap<String, Disposable>();
		copy.putAll(this.garbage);
		for (Entry<String, Disposable> en : copy.entrySet()) {
			System.out.println("Disposing " + MiscUtil.formatObjectString(en.getValue()));
			en.getValue().dispose();
			this.garbage.remove(en.getKey());
		}
	}
	public void disposeSingulars() {
		System.out.println("Disposing singular-assets");
		for (String file : this.singulars) {
			if (this.ass.isLoaded(file)) {
				System.out.println("Disposing " + file);
				this.ass.unload(file);
			} else if (this.garbage.containsKey(file)) {
				System.out.println("Disposing " + MiscUtil.formatObjectString(this.garbage.get(file)));
				this.garbage.get(file).dispose();
				this.garbage.remove(file);
			} else throw new UnintendedBehaviorException("Singular '" + file + "' cannot be resolved");
		}
		this.singulars.clear();
		System.out.println();
	}
	
	
	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private Iterator<String> itAss = ass.getAssetNames().iterator();
			private Iterator<Disposable> itGarb = garbage.values().iterator();			
			
			@Override
			public boolean hasNext() {return (itAss.hasNext() || itGarb.hasNext());}

			@Override
			public String next() {
				if (itAss.hasNext()) return itAss.next();
				else if (itGarb.hasNext()) return MiscUtil.formatObjectString(itGarb.next());
				else throw new UnintendedBehaviorException("next() is called but hasNext() returns false");
			}
			
			@Override
			public void remove() {throw new UnsupportedOperationException();}
		};
	}
	
	
	private String parsePrefix(String filename, Class<?> type) {
		if (filename.startsWith("./")) return filename.substring(2);
		if (type.equals(Texture.class)) filename = "images/"+filename;
		else if (type.equals(Music.class) || type.equals(Sound.class)) filename = "sound/"+filename;
		else if (type.equals(BitmapFont.class)) filename = "fonts/"+filename;
		else if (type.equals(Sprite.class)) filename = "images/sprites/"+filename;
		else if (type.equals(TiledMap.class)) filename = "maps/"+filename;
		else if (type.equals(ImageButton.class)) filename = "images/butt/"+filename;
		else throw new UnknownCaseException();
		return filename;
	}
	private <T> T getFromAlias(String filealias, Class<T> type) {
		if (!this.aliases.containsKey(filealias)) unresolvedAlias(filealias);
		return this.ass.get(aliases.get(filealias), type);
	}
	private void unresolvedAlias(String filealias) {throw new IllegalArgumentException("Can't resolve alias " + filealias);}
}
