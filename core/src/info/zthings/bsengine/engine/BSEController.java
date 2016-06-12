package info.zthings.bsengine.engine;

import info.zthings.bsengine.abstracts.AState;
import info.zthings.bsengine.classes.BSEAssetManager;
import info.zthings.bsengine.util.BSEUtils;
import info.zthings.utils.MiscUtil;
import info.zthings.utils.exceptions.UnintendedBehaviorException;

import java.awt.Dimension;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BSEController {
	public static final String VERSION = "0.0.0";
	private static int dci;
	private static boolean isDebugMode;

	private static HashMap<String, HashMap<String, Object>> debugInfo = new HashMap<String, HashMap<String, Object>>();
	private static SpriteBatch batch;
	private static Stage ui;
	private static BSEAssetManager assets;
	private static OrthographicCamera cam;
	private static ShapeRenderer sr;
	private static Viewport port;
	private static Sprite loading;
	private static Texture loadingTex;
	private static TmxMapLoader mapLoader;
	private static BitmapFont debugFont;
	private static Dimension dim;
	
	public static void init(boolean debugMode) {
		System.out.println("Initialising BSEngine");
		cam = new OrthographicCamera(BSEController.getHeight(), BSEController.getHeight());
		cam.position.set(BSEController.getWidth() / 2, BSEController.getHeight() / 2, 0);
		port = new FitViewport(BSEController.getWidth(), BSEController.getHeight(), BSEController.getCamera());
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(cam.combined);
		
		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);
		
		debugFont = new BitmapFont();
		assets = new BSEAssetManager();
		
		loadingTex = new Texture(Gdx.files.internal("images/loading.png"));
		loading = new Sprite(loadingTex);
		
		ui = new Stage(port, batch);
		Gdx.input.setInputProcessor(ui);

		mapLoader = new TmxMapLoader();
		
		isDebugMode = debugMode;
	}
	
	public static void dispose() {
		batch.dispose();
		assets.dispose();
		loadingTex.dispose();
		ui.dispose();
		sr.dispose();
	}
	
	public static boolean isDebugMode() {return isDebugMode;}
	public static BSEAssetManager getAssets() {return assets;}
	public static SpriteBatch getSpriteBatch() {return batch;}
	public static Sprite getLoadingTexture() {return loading;}
	public static Stage getUI() {return ui;}
	public static OrthographicCamera getCamera() {return cam;}
	public static TmxMapLoader getMapLoader() {return mapLoader;}
	public static ShapeRenderer getShapeRenderer() {return sr;}
	public static BitmapFont getDebugFont() {return debugFont;}
	public static void setDimensions(int w, int h) {dim = new Dimension(w, h);}
	public static int getWidth() {return (int)dim.getWidth();}
	public static int getHeight() {return (int)dim.getHeight();}
	
	public static void setDebugInfoEntry(String name, Object obj) {
		if (debugInfo.containsKey(BSEStateManager.peek().getClass().getSimpleName())) debugInfo.get(BSEStateManager.peek().getClass().getSimpleName()).put(name, obj);
	}
	public static HashMap<String, Object> getDebugInfo(String state) {return debugInfo.get(state);}
	public static void createStateDebugData(AState state) {debugInfo.put(state.getClass().getSimpleName(), new HashMap<String, Object>());}

	
	public static Color getDebugColor() {return BSEUtils.convertAwtColor(MiscUtil.rainbow[dci]);}
	public static void decreaseDColorIndex() {
		dci--;
		if (dci < 0) dci = MiscUtil.rainbow.length-1;
		debugFont.setColor(BSEUtils.convertAwtColor(MiscUtil.rainbow[dci]));
	}
	public static void increaseDColorIndex() {
		dci++;
		if (dci == MiscUtil.rainbow.length) dci = 0;
		else if (dci > MiscUtil.rainbow.length) throw new UnintendedBehaviorException("Color wrap failed");
		debugFont.setColor(BSEUtils.convertAwtColor(MiscUtil.rainbow[dci]));
	}
	
	//CHECK
	public static void renderDebug() {
		debugFont.draw(BSEController.getSpriteBatch(), "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 0, BSEController.getHeight());
		debugFont.draw(BSEController.getSpriteBatch(), "Delta: " + Gdx.graphics.getDeltaTime(), 0, BSEController.getHeight()-15);
		debugFont.draw(BSEController.getSpriteBatch(), "State: " + BSEStateManager.peek().getClass().getSimpleName().substring(5), 0, BSEController.getHeight()-30);
		debugFont.draw(BSEController.getSpriteBatch(), "Mouse: (" + Gdx.input.getX() + "," + ((Gdx.input.getY()-BSEController.getHeight())*-1) +")", 0, BSEController.getHeight()-45);
		
		int y = BSEController.getHeight()-65;
		for (String str : BSEController.getAssets()) {
			debugFont.draw(BSEController.getSpriteBatch(), str, 0, y);
			y -= 15;
		}
		y -= 15;
		try {
			Class<?> c = BSEStateManager.peek().getClass();
			while(c.getSuperclass() != null) {
				c = c.getSuperclass();
				for (Field field : ClassReflection.getDeclaredFields(c)) {
				    field.setAccessible(true);
				    Object value = field.get(BSEStateManager.peek());
				    if (value != null && !field.isFinal()) {
				    	debugFont.draw(BSEController.getSpriteBatch(), field.getName() + " = " + MiscUtil.formatObjectString(value), 0, y);
				        y -= 15;
				        if (MiscUtil.formatObjectString(value).contains("\n")) y -= 15;
				    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		y = 15;
		for (Object obj : BSEController.getDebugInfo(BSEStateManager.peek().getClass().getSimpleName()).values()) {
			debugFont.draw(BSEController.getSpriteBatch(), obj.toString(), 0, y);
			y += 15;
		}
		
		if (Gdx.input.isKeyPressed(Keys.F8) && BSEController.isDebugMode()) {
			BSEController.getShapeRenderer().setColor(BSEController.getDebugColor());
			BSEController.getShapeRenderer().begin();
			BSEStateManager.peek().debugRender(BSEController.getShapeRenderer());
			BSEController.getShapeRenderer().end();
		}
	}
}
