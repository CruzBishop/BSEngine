package info.zthings.bsengine.abstracts;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public abstract class AButtonListener extends InputListener {
	 @Override
	public boolean touchDown (InputEvent ev, float x, float y, int pointer, int button) {
		 pressed(ev, x, y);
		 return true;
	 }
	@Override
	public void touchUp(InputEvent ev, float x, float y, int pointer, int button) {
		if (x > 0 && y > 0 && x < ev.getTarget().getWidth() && y < ev.getTarget().getHeight()) released(ev, x, y);
	}

	public abstract void released(InputEvent ev, float x, float y);
	public abstract void pressed(InputEvent ev, float x, float y);
}
