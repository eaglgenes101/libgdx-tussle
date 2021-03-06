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

import com.badlogic.ashley.core.ComponentMapper;
import com.tussle.collision.ECBComponent;
import com.tussle.collision.ElasticityComponent;
import com.tussle.collision.StageElementComponent;
import com.tussle.control.ControlComponent;
import com.tussle.hitbox.HitboxComponent;
import com.tussle.hitbox.HitboxLockComponent;
import com.tussle.hitbox.HurtboxComponent;
import com.tussle.motion.PositionComponent;
import com.tussle.motion.TransformComponent;
import com.tussle.motion.VelocityComponent;
import com.tussle.ownership.EntityOwnershipComponent;
import com.tussle.ownership.PlayerOwnershipComponent;
import com.tussle.script.ScriptContextComponent;
import com.tussle.sprite.SpriteComponent;
import com.tussle.stream.NameComponent;

public class Components
{
	public static final ComponentMapper<ECBComponent> ecbMapper =
			ComponentMapper.getFor(ECBComponent.class);
	public static final ComponentMapper<ElasticityComponent> elasticityMapper =
			ComponentMapper.getFor(ElasticityComponent.class);
	public static final ComponentMapper<EntityOwnershipComponent> entityOwnershipMapper =
			ComponentMapper.getFor(EntityOwnershipComponent.class);
	public static final ComponentMapper<StageElementComponent> stageElementMapper =
			ComponentMapper.getFor(StageElementComponent.class);
	public static final ComponentMapper<EntityOwnershipComponent> dependencyMapper =
			ComponentMapper.getFor(EntityOwnershipComponent.class);
	public static final ComponentMapper<HitboxComponent> hitboxMapper =
			ComponentMapper.getFor(HitboxComponent.class);
	public static final ComponentMapper<HurtboxComponent> hurtboxMapper =
			ComponentMapper.getFor(HurtboxComponent.class);
	public static final ComponentMapper<PlayerOwnershipComponent> playerOwnershipMapper =
			ComponentMapper.getFor(PlayerOwnershipComponent.class);
	public static final ComponentMapper<PositionComponent> positionMapper =
			ComponentMapper.getFor(PositionComponent.class);
	public static final ComponentMapper<VelocityComponent> velocityMapper =
			ComponentMapper.getFor(VelocityComponent.class);
	public static final ComponentMapper<ScriptContextComponent> scriptContextMapper =
			ComponentMapper.getFor(ScriptContextComponent.class);
	public static final ComponentMapper<SpriteComponent> spriteMapper =
			ComponentMapper.getFor(SpriteComponent.class);
	public static final ComponentMapper<HitboxLockComponent> hitboxLockMapper =
			ComponentMapper.getFor(HitboxLockComponent.class);
	public static final ComponentMapper<TransformComponent> transformMapper =
			ComponentMapper.getFor(TransformComponent.class);
	public static final ComponentMapper<ControlComponent> controlMapper =
			ComponentMapper.getFor(ControlComponent.class);
	public static final ComponentMapper<NameComponent> nameMapper =
			ComponentMapper.getFor(NameComponent.class);
}
