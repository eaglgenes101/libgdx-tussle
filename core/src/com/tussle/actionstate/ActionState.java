package com.tussle.actionstate;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
public abstract class ActionState extends Action implements Terminable
{
    public boolean act(float delta)
    {
        ActionState newAction = eachFrame();
        if (newAction != null && newAction != this)
        {
            ((Fighter)getActor()).setActionState(newAction);
            return true;
        }
        return false;
    }

    public abstract ActionState eachFrame(); //Each frame
}
