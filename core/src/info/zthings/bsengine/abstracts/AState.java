package info.zthings.bsengine.abstracts;

import java.util.ArrayList;

import info.zthings.bsengine.classes.BSEAssetManager;
import info.zthings.bsengine.engine.BSEController;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

@SuppressWarnings("unused")
public abstract class AState implements Disposable {
	//protected ArrayList<IEntity> entities = new ArrayList<IEntity>();
	
	/** Called on the first update frame, should be used to load assets, as further frames will be suspended until all assets are loaded */
	public abstract void init(BSEAssetManager assets);
	/** Called every frame */
	public abstract void update(float dt);
	/** Called every frame after update(dt) */
	public abstract void render(SpriteBatch batch, BSEAssetManager assets);
	
	/** Called when the state is removed from the manager and can't be accesed anymore without a new constructor-call */
	@Override
	public void dispose() {}
	/** Called once at the first frame where all assets are loaded (can be used to define stuff like Skins wich needs certain assets to be loaded */
	public void postInit(BSEAssetManager assets) {}
	/** Used to debug-render shapes when debug mode is enabled AND key F8 is pressed */
	public void debugRender(ShapeRenderer sr) {}
}
