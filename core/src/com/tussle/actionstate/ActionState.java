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

import com.tussle.collision.HitboxLock;
import com.tussle.collision.Hurtbox;
import com.tussle.fighter.Fighter;
import com.tussle.fighter.Terminable;
import com.tussle.main.PhysicalBody;
import com.tussle.main.Utility;

import java.util.LinkedList;
import java.util.List;

public abstract class ActionState extends Terminable
{
    public boolean act(float delta)
    {
        ActionState newAction = (ActionState)eachFrame();
        if (newAction != null && newAction != this)
        {
            ((Fighter)getActor()).setActionState(newAction);
            return true;
        }
        return false;
    }

    public void onClanked(PhysicalBody other)
    {
        //Nothing by default
    }

    public Fighter getOwner()
    {
        return (Fighter)getActor();
    }

    public PhysicalBody getBody()
    {
        return (PhysicalBody)getActor();
    }

    public List<HitboxLock> getHitboxLocks()
    {
        return Utility.emptyLockList;
    }

    public List<Hurtbox> getHurtboxes()
    {
        return Utility.emptyHurtboxList;
    }
}
