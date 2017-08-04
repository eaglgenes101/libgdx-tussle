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

package com.tussle.sprite;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.HashMap;

/**
 * Created by eaglgenes101 on 4/13/17.
 */
public class SpriteComponent implements Component
{
	String intendedSpritePath;
	Sprite currentSprite;
	HashMap<Texture, Sprite> knownSprites;

	public SpriteComponent()
	{
		intendedSpritePath = null;
		currentSprite = null;
		knownSprites = new HashMap<>();
	}

	public Sprite loadSprite(Texture texture)
	{
		if (!knownSprites.containsKey(texture))
			knownSprites.put(texture, new Sprite(texture));
		return knownSprites.get(texture);
	}

	public void setPath(String path)
	{
		intendedSpritePath = path;
	}
}
