package info.zthings.bsengine.engine;

import info.zthings.bsengine.abstracts.AState;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;

public class BSEGameLoop implements Disposable {

	public BSEGameLoop(boolean debug, AState startstate) {
		BSEController.init(debug);
		System.out.println("Handing game-loop over to the GameStateManager\n\n");
		BSEStateManager.pushOnTop(startstate);
		System.out.println("Finished initialisation\n");
	}
	
	@Override
	public void dispose() {
		System.out.println("\n\n===========DISPOSING GAME===========\n");
		BSEController.getAssets().disposeSingulars();
		BSEStateManager.dispose();
		int c = BSEController.getAssets().size();
		BSEController.dispose();
		if ((c-BSEController.getAssets().size()) > 0) System.err.println("CAUGHT MEMORY LEAK OF " + (c-BSEController.getAssets().size()) + " ASSETS");
		System.out.println("===========DISPOSED GAME===========\n");
	}

	public void tick() {
		if (Gdx.input.isKeyPressed(Keys.F12)) return;
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); //clear previous frame
		BSEController.getCamera().update();
		
		BSEController.getSpriteBatch().begin();
		BSEStateManager.tick();
		BSEController.getSpriteBatch().end();
		
		BSEController.getUI().act();
		BSEController.getUI().draw();
		
		BSEController.getSpriteBatch().begin();
		if (BSEController.isDebugMode()) BSEController.renderDebug();
		BSEController.getSpriteBatch().end();
		
		if (Gdx.input.isKeyJustPressed(Keys.F9)) BSEController.decreaseDColorIndex();
		else if (Gdx.input.isKeyJustPressed(Keys.F10)) BSEController.increaseDColorIndex();
	}
}