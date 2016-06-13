package info.zthings.bsengine.abstracts;

public interface IFramedStateListener {
	public abstract void onStateChanged(int oldstate, int newstate, int[] map);
}
