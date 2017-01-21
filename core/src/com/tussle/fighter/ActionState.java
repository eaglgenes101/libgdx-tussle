package com.tussle.fighter;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by eaglgenes101 on 1/20/17.
 */
public abstract class ActionState extends Action
{
    public abstract void onStart(); //After construction and initialization

    public boolean act(float delta)
    {
        ActionState newAction = eachFrame();
        if (newAction != null)
        {
            ((Fighter)getActor()).setActionState(newAction);
            return true;
        }
        return false;
    }

    public abstract ActionState eachFrame();

    public abstract void onEnd(ActionState nextState);
}
