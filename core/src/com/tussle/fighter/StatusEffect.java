package com.tussle.fighter;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by eaglgenes101 on 1/26/17.
 */
public abstract class StatusEffect extends Action implements Terminable
{
	public abstract void onStart(); //After construction and initialization

	public boolean act(float delta)
	{
		StatusEffect newEffect = eachFrame();
		if (newEffect == this)
		{
			return false;
		}
		else
		{
			onEnd(newEffect);
			if (newEffect != null)
				((Fighter)actor).addStatusEffect(newEffect);
			return true;
		}
	}

	public abstract StatusEffect eachFrame(); //Each frame

	public abstract void onEnd(StatusEffect nextEffect); //Before disposal
}
