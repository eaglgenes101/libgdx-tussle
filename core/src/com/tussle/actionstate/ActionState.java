/*
 * Copyright (c) 2017 eaglgenes101
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tussle.actionstate;

import com.badlogic.ashley.core.Entity;
import com.tussle.main.Terminable;

import javax.script.Bindings;
import javax.script.SimpleBindings;

public abstract class ActionState implements Terminable
{
    private Entity owner;
    Bindings variables;

    public ActionState()
    {
        variables = new SimpleBindings();
    }

    //Each frame
    public abstract void act();

    public Entity getBody()
    {
        return owner;
    }

    public Entity getOwner()
    {
        return owner;
    }

    /*
    public void onClank(Hitbox ourBox, Hitbox otherBox)
    {
        //Nothing by default
    }

    public List<HitboxLock> getHitboxLocks()
    {
        return Utility.emptyLockList;
    }

    public List<Hurtbox> getHurtboxes()
    {
        return Utility.emptyHurtboxList;
    }
    */
}
