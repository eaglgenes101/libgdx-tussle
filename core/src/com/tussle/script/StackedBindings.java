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

import org.apache.commons.collections4.IteratorUtils;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.*;

//Used to implement the binding call stack in tussle scripts
public class StackedBindings implements Bindings
{
	Deque<SimpleBindings> bindingStack;
	Map<String, Deque<Object>> bindingMap;
	
	public StackedBindings()
	{
		bindingStack = new ArrayDeque<>();
		bindingStack.push(new SimpleBindings());
		bindingMap = new HashMap<>();
	}
	
	public StackedBindings(Map<String, Object> startValues)
	{
		bindingStack = new ArrayDeque<>();
		bindingMap = new HashMap<>();
		bindingStack.push(new SimpleBindings(startValues));
		for (Map.Entry<String, Object> entry : startValues.entrySet())
		{
			ArrayDeque<Object> toPut = new ArrayDeque<>();
			toPut.push(entry.getValue());
			bindingMap.put(entry.getKey(), toPut);
		}
	}
	
	public void clear()
	{
		//Complete remove the stack
		bindingStack.clear();
		bindingStack.push(new SimpleBindings());
		bindingMap.clear();
	}
	
	private transient Collection<Object> values = null;
	private transient Set<String> keySet = null;
	private transient Set<Entry<String, Object>> entrySet = null;
	
	class StackedBindingsEntry implements Map.Entry<String, Object>
	{
		Map.Entry<String, Deque<Object>> base;
		
		StackedBindingsEntry(Map.Entry<String, Deque<Object>> start)
		{
			base = start;
		}
		
		public int hashCode()
		{
			String key = getKey();
			Object value = getValue();
			return (key==null?0:key.hashCode())^(value==null?0:value.hashCode());
		}
		
		public String getKey()
		{
			return base.getKey();
		}
		
		public Object getValue()
		{
			return base.getValue().peek();
		}
		
		public Object setValue(Object o)
		{
			Object removed = base.getValue().pop();
			base.getValue().push(o);
			return removed;
		}
		
		public boolean equals(Object other)
		{
			if (other instanceof Map.Entry)
			{
				return (getKey().equals(((Map.Entry)other).getKey()) &&
				        (getValue().equals(((Map.Entry)other).getValue())));
			}
			else return false;
		}
	}
	
	public Set<Map.Entry<String, Object>> entrySet()
	{
		if (entrySet == null)
			entrySet = new AbstractSet<Map.Entry<String, Object>>()
			{
				public Iterator<Map.Entry<String, Object>> iterator()
				{
					return IteratorUtils.transformedIterator(
							bindingMap.entrySet().iterator(),
							entry -> new StackedBindingsEntry(entry)
					);
				}
				
				public int size()
				{
					return bindingMap.size();
				}
			};
		return entrySet;
	}
	
	public Set<String> keySet()
	{
		if (keySet == null)
			keySet = new AbstractSet<String>()
			{
				public Iterator<String> iterator()
				{
					return IteratorUtils.transformedIterator(
							bindingMap.entrySet().iterator(),
							entry -> entry.getKey()
					);
				}
				
				public int size()
				{
					return bindingMap.size();
				}
			};
		return keySet;
	}
	
	public Collection<Object> values()
	{
		if (values == null)
			values = new AbstractCollection<Object>()
			{
				public Iterator<Object> iterator()
				{
					return IteratorUtils.transformedIterator(
							bindingMap.values().iterator(),
							(Deque<Object> stack) -> stack.peek()
					);
				}
				
				public int size()
				{
					return bindingMap.size();
				}
			};
		return values;
	}
	
	public boolean containsKey(Object o)
	{
		return bindingMap.containsKey(o);
	}
	
	public Object get(Object o)
	{
		if (containsKey(o)) return bindingMap.get(o).peek();
		else return null;
	}
	
	public boolean containsValue(Object o)
	{
		for (Deque<Object> stack : bindingMap.values())
			if (stack.peek().equals(o)) return true;
		return false;
	}
	
	public void putAll(Map<? extends String, ? extends Object> toPut)
	{
		for (Map.Entry<? extends String, ? extends Object> entry : toPut.entrySet())
			put(entry.getKey(), entry.getValue());
	}
	
	public int size()
	{
		return bindingMap.size();
	}
	
	public boolean isEmpty()
	{
		return bindingMap.isEmpty();
	}
	
	public Object remove(Object o)
	{
		//Remove the map entry
		if (!bindingMap.containsKey(o)) return null;
		Object previousVal = bindingMap.get(o).pop();
		if (bindingMap.get(o).isEmpty()) bindingMap.remove(o);
		for (Map<String, Object> val : bindingStack)
		{
			if (val.containsKey(o))
			{
				Object checkVal = val.remove(o);
				assert previousVal == checkVal;
				break;
			}
		}
		return previousVal;
	}
	
	public Object put(String key, Object value)
	{
		//If it already exists, find where it is in the stack, and replace
		if (bindingMap.containsKey(key))
		{
			for (Map<String, Object> val : bindingStack)
				if (val.containsKey(key))
				{
					Object fromMapVal = bindingMap.get(key).pop();
					Object fromStackVal = val.put(key, value);
					bindingMap.get(key).push(value);
					assert fromMapVal == fromStackVal;
					return fromMapVal;
				}
			throw new AssertionError("Key found in map but not in stack");
		}
		else
		{
			//Put it on the topmost map
			bindingStack.peek().put(key, value);
			bindingMap.put(key, new ArrayDeque<>());
			bindingMap.get(key).push(value);
			return null;
		}
	}
	
	public void push()
	{
		bindingStack.push(new SimpleBindings());
	}
	
	public void push(Bindings values)
	{
		bindingStack.push(new SimpleBindings(values));
		for (Map.Entry<? extends String, ? extends Object> entry : values.entrySet())
		{
			putOver(entry.getKey(), entry.getValue());
		}
	}
	
	public void putOver(String key, Object value)
	{
		if (bindingStack.peek().containsKey(key))
		{
			Object fromStackEntry = bindingStack.peek().put(key, value);
			Object fromMapEntry = bindingMap.get(key).pop();
			bindingMap.get(key).push(value);
			assert fromStackEntry == fromMapEntry;
		}
		else
		{
			bindingStack.peek().put(key, value);
			if (!bindingMap.containsKey(key))
				bindingMap.put(key, new ArrayDeque<>());
			bindingMap.get(key).push(value);
		}
	}
	
	public void putOverAll(Bindings values)
	{
		for (Map.Entry<? extends String, ? extends Object> entry : values.entrySet())
		{
			putOver(entry.getKey(), entry.getValue());
		}
	}
	
	public Bindings pop()
	{
		//Get the top map
		SimpleBindings topValue = bindingStack.pop();
		if (bindingStack.isEmpty()) bindingStack.push(new SimpleBindings());
		for (Map.Entry<String, Object> entry : topValue.entrySet())
		{
			Object val = bindingMap.get(entry.getKey()).pop();
			if (bindingMap.get(entry.getKey()).isEmpty()) bindingMap.remove(entry.getKey());
			assert val == entry.getValue();
		}
		return topValue;
	}
}
