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

package com.tussle.main;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.assets.AssetManager;
import com.tussle.motion.CollisionSystem;
import com.tussle.motion.MotionSystem;
import com.tussle.postprocess.PostprocessSystem;
import com.tussle.script.SubactionCleanerSystem;
import com.tussle.script.SubactionScriptSystem;
import com.tussle.sprite.CameraSystem;
import com.tussle.sprite.HitboxDrawingSystem;
import com.tussle.sprite.SpriteSystem;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class TussleEngine extends Engine
{
	AssetManager manager;

	//Give it an array of entities
	public TussleEngine()
	{
		super();
		manager = new AssetManager();
		
		//Add entity systems
		addSystem(new MotionSystem(0));
		addSystem(new CollisionSystem(1));
		addSystem(new CameraSystem(2));
		addSystem(new SpriteSystem(manager, getSystem(CameraSystem.class).getCamera(),3));
		addSystem(new HitboxDrawingSystem(getSystem(CameraSystem.class).getCamera(), 4));
		addSystem(new SubactionScriptSystem(5));
		
		//Must be the very, very last few systems
		addSystem(new SubactionCleanerSystem(Integer.MAX_VALUE-1));
		addSystem(new PostprocessSystem(Integer.MAX_VALUE));
	}

	public void update(float delta)
	{
		super.update(delta);
	}
	
}
