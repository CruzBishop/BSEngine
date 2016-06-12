package info.zthings.bsengine.abstracts;

import info.zthings.bsengine.classes.FramedStateHandler;

public abstract class AStateFSH extends AState {
	protected FramedStateHandler fsh;

	public AStateFSH(int[] map, int speed) {fsh = new FramedStateHandler(map, speed);}
}
