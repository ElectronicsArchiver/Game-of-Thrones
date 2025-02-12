package got.common.util;

import got.client.model.GOTModelDragonAnimaton;

public class GOTTickFloat {

	public float min;
	public float max;
	public boolean clamp = false;
	public float current;
	public float previous;

	public GOTTickFloat() {
		current = previous = 0;
	}

	public GOTTickFloat(float value) {
		current = previous = value;
	}

	public void add(float value) {
		sync();
		current += value;
		if (clamp) {
			current = GOTModelDragonAnimaton.clamp(current, min, max);
		}
	}

	public float get() {
		return current;
	}

	public float get(float x) {
		return GOTModelDragonAnimaton.lerp(previous, current, x);
	}

	public float getMax() {
		return max;
	}

	public float getMin() {
		return min;
	}

	public float getPrevious() {
		return previous;
	}

	public void set(float value) {
		sync();
		current = clamp ? GOTModelDragonAnimaton.clamp(value, min, max) : value;
	}

	public GOTTickFloat setLimit(float min, float max) {
		clamp = true;
		setMin(min);
		setMax(max);
		set(current);
		return this;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void sync() {
		previous = current;
	}
}
