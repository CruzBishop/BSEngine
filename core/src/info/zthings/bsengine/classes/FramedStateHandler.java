package info.zthings.bsengine.classes;

import info.zthings.bsengine.abstracts.IFramedStateListener;

public class FramedStateHandler {
	private int[] map;
	private IFramedStateListener lis;
	private int speed = 1, nextTreshold = -1, infinityTreshold = 0;
	private float fr = 0;
	private int index = 0;
	
	public FramedStateHandler(int[] map, int speed) {
		if (speed <= 0) throw new IllegalArgumentException("Can't have a speed of " + speed + ", speed must be >0");
		this.map = map;
		this.speed = speed;
		
		for (float cF : map) this.infinityTreshold += cF;
		this.nextTreshold = map[0];
	}
	
	public void update(float dt) {
		if ((int)fr < infinityTreshold) {
			fr += speed * dt;
			if ((int) fr >= nextTreshold && (int) fr <= infinityTreshold) {
				index++;
				if (lis != null) lis.onStateChanged(index - 1, index, map);
				if ((int) fr < infinityTreshold) nextTreshold += map[index];
				else {
					System.out.println("Reached infinity");
				}
			}
		}
	}
	
	@Override
	public String toString() {
		float c = 0;
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("FR:"+(int)fr+"; SP:"+speed+"; IN:"+index+"; NT:"+nextTreshold+";\nFTS: ");
		for (float cmf : map) {
			sb.append(i + ": " + (int)c + ", ");
			c += cmf;
			i++;
		}
		return sb.append("oo:"+this.infinityTreshold).toString();
	}

	public void setListener(IFramedStateListener listener) {this.lis = listener;}
	public int getIndex() {return index;}
}
