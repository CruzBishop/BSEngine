package info.zthings.bsengine.engine;

import info.zthings.bsengine.abstracts.AState;
import info.zthings.bsengine.abstracts.annotations.NoLoadingAnimation;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class BSEStateManager {
	private static Stack<AState> states = new Stack<AState>();
	
	public static void pushOnTop(AState state) {pushOnTop(state, true);}
	public static void pushOnTop(AState state, boolean clearUI) {
		if (clearUI) BSEController.getUI().clear();
		if (states.size() > 0) System.out.println("====STATE " + state.getClass().getSimpleName() + " WAS PUSHED OVER " + states.peek().getClass().getSimpleName() + "====");
		else System.out.println("====STATE " + state.getClass().getSimpleName() + " WAS PUSHED ON TOP OF NOTHING====");
		state.init(BSEController.getAssets());
		states.push(state);
		BSEController.createStateDebugData(state);
		BSEController.getSpriteBatch().setColor(Color.WHITE);
	}
	
	//FUTURE all singulars are disposed, but if the underlaying state uses singular assets that can/will break stuff
	public static void popTop() {popTop(true);}
	public static void popTop(boolean clearUI) {
		if (clearUI) BSEController.getUI().clear();
		System.out.println("====STATE " + states.peek().getClass().getSimpleName() + " WAS POPPED, NEW STATE: " + states.get(states.size()-1) +"====");
		BSEController.getAssets().disposeSingulars();
		states.pop().dispose();
		BSEController.getSpriteBatch().setColor(Color.WHITE);
	}
	
	/** Short for:
	 * <pre>
	 * popTop();
	 * pushOnTop(state);
	 * </pre>
	 * @param state
	 */
	public static void set(AState state) {set(state, true);}
	public static void set(AState state, boolean clearUI) {
		System.out.println("====STATE WAS SET TO " + state.getClass().getSimpleName() + "====");
		BSEController.getAssets().disposeSingulars();
		states.pop().dispose();
		if (clearUI) BSEController.getUI().clear();
		state.init(BSEController.getAssets());
		states.push(state);
		BSEController.createStateDebugData(state);
		BSEController.getSpriteBatch().setColor(Color.WHITE);
	}
	
	public static void tick() {
		if (BSEController.getAssets().getProgress() == 1f) {
			AState prevState = states.peek();
			states.peek().update(Gdx.graphics.getDeltaTime());
			//If the state hasn't changed it's safe to render
			if (states.peek().equals(prevState)) states.peek().render(BSEController.getSpriteBatch(), BSEController.getAssets());
		} else { //Update & Render loading animation
			System.out.println("Loading");
			
			if (!states.peek().getClass().isAnnotationPresent(NoLoadingAnimation.class)) {
				BSEController.getLoadingTexture().setRotation(BSEController.getLoadingTexture().getRotation()-200*Gdx.graphics.getDeltaTime());
				BSEController.getLoadingTexture().setPosition((BSEController.getWidth()/2)-32, (BSEController.getHeight()/2)-(32));
		    	BSEController.getLoadingTexture().draw(BSEController.getSpriteBatch());
			}
		    
		    if (BSEController.getAssets().update()) states.peek().postInit(BSEController.getAssets());
		}
	}
	
	public static Stack<AState> getStack() {return states;}
	public static AState peek() {return states.peek();}
	public static void dispose() {for (AState s : states) s.dispose();}
}
