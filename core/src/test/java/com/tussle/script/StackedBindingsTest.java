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

package com.tussle.script;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.script.Bindings;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StackedBindingsTest
{
	public StackedBindings bindings;
	
	@BeforeEach
	public void init()
	{
		bindings = new StackedBindings();
	}
	
	@Test
	@DisplayName("Check the contractual fulfillment of the Bindings interface")
	public void bindingsImplTest()
	{
		assertTrue(bindings.isEmpty());
		assertEquals(bindings.size(), 0);
		assertEquals(bindings.put("Hello", "Goodbye"), null);
		assertFalse(bindings.isEmpty());
		assertEquals(bindings.size(), 1);
		assertEquals(bindings.get("Hello"), "Goodbye");
		assertEquals(bindings.get("Invalid"), null);
		assertEquals(bindings.put("Hello", "What"), "Goodbye");
		assertEquals(bindings.size(), 1);
		assertEquals(bindings.get("Hello"), "What");
		assertEquals(bindings.remove("Hello"), "What");
		assertTrue(bindings.isEmpty());
		assertEquals(bindings.get("Hello"), null);
	}
	
	@Test
	@DisplayName("Check the soundness of the iterable StackedBindings views")
	public void bindingsViewTest()
	{
		bindings.put("Hello", "H");
		bindings.put("Goodbye", "G");
		Set<String> keyView = bindings.keySet();
		Collection<Object> valueView = bindings.values();
		Set<Map.Entry<String, Object>> entryView = bindings.entrySet();
		assertTrue(keyView.contains("Hello"));
		assertTrue(keyView.contains("Goodbye"));
		assertTrue(valueView.contains("H"));
		assertTrue(valueView.contains("G"));
		assertEquals(keyView.size(), 2);
		assertEquals(valueView.size(), 2);
		assertEquals(entryView.size(), 2);
		
	}
	
	@Test
	@DisplayName("Check that the iterators for each view are all sound")
	public void bindingsIteratorTest()
	{
		bindings.put("Hello", "H");
		bindings.put("Goodbye", "G");
		Iterator<String> keyIterator = bindings.keySet().iterator();
		Iterator<Object> valueIterator = bindings.values().iterator();
		Iterator<Map.Entry<String, Object>> entryIterator = bindings.entrySet().iterator();
		keyIterator.next();
		assertTrue(keyIterator.hasNext());
		keyIterator.next();
		assertFalse(keyIterator.hasNext());
		valueIterator.next();
		assertTrue(valueIterator.hasNext());
		valueIterator.next();
		assertFalse(valueIterator.hasNext());
		Map.Entry<String, Object> ent1 = entryIterator.next();
		assertTrue(entryIterator.hasNext());
		assertEquals(bindings.get(ent1.getKey()), ent1.getValue());
		Map.Entry<String, Object> ent2 = entryIterator.next();
		assertFalse(entryIterator.hasNext());
		assertEquals(bindings.get(ent2.getKey()), ent2.getValue());
		assertNotEquals(ent1.getKey(), ent2.getKey());
		assertNotEquals(ent1.getValue(), ent2.getValue());
	}
	
	@Test
	@DisplayName("Check that exceptional conditions don't throw inappropriate exceptions")
	public void noExceptionsTest()
	{
		try
		{
			bindings.pop();
			bindings.remove("Nonexistent");
			bindings.push();
			bindings.pop();
			bindings.pop();
		}
		catch (Exception e)
		{
			fail(e);
		}
	}
	
	@Test
	@DisplayName("Check that overlaying scopes works correctly")
	public void bindingLayersTest()
	{
		bindings.put("H", "Hello");
		bindings.putOver("H", "Replacement");
		bindings.put("G", "Goodbye");
		bindings.put("R", "Real");
		assertEquals(bindings.get("H"), "Replacement");
		bindings.push();
		bindings.put("I", "Internal");
		bindings.putOver("J", "Jocular");
		bindings.put("H", "Hello");
		bindings.putOver("G", "Great");
		assertEquals(bindings.get("H"), "Hello");
		assertEquals(bindings.get("G"), "Great");
		Bindings b = bindings.pop();
		assertFalse(b.containsKey("H"));
		assertTrue(b.containsKey("G"));
		assertTrue(b.containsKey("I"));
		assertTrue(b.containsKey("J"));
		assertFalse(b.containsKey("R"));
		assertEquals(b.get("G"), "Great");
		assertEquals(bindings.get("H"), "Hello");
		assertEquals(bindings.get("G"), "Goodbye");
	}
}
