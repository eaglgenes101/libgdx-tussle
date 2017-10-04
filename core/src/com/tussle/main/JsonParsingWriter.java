
// line 1 "JsonParsingWriter.rl"
package com.tussle.main;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.SerializationException;

import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

// Please refer to the Ragel file, rather than the java file it helps generate.
// ragel -G2 -J -o JsonParsingWriter.java JsonParsingWriter.rl

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

public class JsonParsingWriter extends Writer implements JsonSource
{
	StringBuilder data;

	int cs, p, pe, eof, top, s;
	boolean needsUnescape, stringIsName, stringIsUnquoted;
	int[] stack;

	Deque<JsonValue> completedValues;
	Deque<String> names;

	private Deque<JsonValue> elements;
	private Deque<JsonValue> lastChild;
	private JsonValue root, current;

	public JsonParsingWriter()
	{
		data = new StringBuilder();
		completedValues = new LinkedList<>();
		init();
	}

	public void write(char[] cbuf, int off, int len)
	{
		data.append(cbuf, off, len);
	}

	public JsonValue read()
	{
		flush();
		return completedValues.poll();
	}

	public boolean ready()
	{
		flush();
		return !completedValues.isEmpty();
	}

	public void close()
	{
		flush();
	}

	public void flush()
	{
		if (!completedValues.isEmpty()) return;
		//Now try to parse it
		pe = data.length();
		eof = data.length();
		try
		{

// line 91 "JsonParsingWriter.java"
			{
				int _klen;
				int _trans = 0;
				int _acts;
				int _nacts;
				int _keys;
				int _goto_targ = 0;

				_goto:
				while (true)
				{
					switch (_goto_targ)
					{
						case 0:
							if (p == pe)
							{
								_goto_targ = 4;
								continue _goto;
							}
							if (cs == 0)
							{
								_goto_targ = 5;
								continue _goto;
							}
						case 1:
							_match:
							do
							{
								_keys = _json_key_offsets[cs];
								_trans = _json_index_offsets[cs];
								_klen = _json_single_lengths[cs];
								if (_klen > 0)
								{
									int _lower = _keys;
									int _mid;
									int _upper = _keys + _klen - 1;
									while (true)
									{
										if (_upper < _lower)
											break;

										_mid = _lower + ((_upper - _lower) >> 1);
										if ((data.charAt(p)) < _json_trans_keys[_mid])
											_upper = _mid - 1;
										else if ((data.charAt(p)) > _json_trans_keys[_mid])
											_lower = _mid + 1;
										else
										{
											_trans += (_mid - _keys);
											break _match;
										}
									}
									_keys += _klen;
									_trans += _klen;
								}

								_klen = _json_range_lengths[cs];
								if (_klen > 0)
								{
									int _lower = _keys;
									int _mid;
									int _upper = _keys + (_klen << 1) - 2;
									while (true)
									{
										if (_upper < _lower)
											break;

										_mid = _lower + (((_upper - _lower) >> 1) & ~1);
										if ((data.charAt(p)) < _json_trans_keys[_mid])
											_upper = _mid - 2;
										else if ((data.charAt(p)) > _json_trans_keys[_mid + 1])
											_lower = _mid + 2;
										else
										{
											_trans += ((_mid - _keys) >> 1);
											break _match;
										}
									}
									_trans += _klen;
								}
							}
							while (false);

							_trans = _json_indicies[_trans];
							cs = _json_trans_targs[_trans];

							if (_json_trans_actions[_trans] != 0)
							{
								_acts = _json_trans_actions[_trans];
								_nacts = (int)_json_actions[_acts++];
								while (_nacts-- > 0)
								{
									switch (_json_actions[_acts++])
									{
										case 0:
// line 98 "JsonParsingWriter.rl"
										{
											stringIsName = true;
										}
										break;
										case 1:
// line 101 "JsonParsingWriter.rl"
										{
											String value = data.substring(s, p);
											if (needsUnescape) value = unescape(value);
											outer:
											if (stringIsName)
											{
												stringIsName = false;
												names.push(value);
											}
											else
											{
												String name = names.poll();
												if (stringIsUnquoted)
												{
													if (value.equals("true"))
													{
														bool(name, true);
														break outer;
													}
													else if (value.equals("false"))
													{
														bool(name, false);
														break outer;
													}
													else if (value.equals("null"))
													{
														string(name, null);
														break outer;
													}
													boolean couldBeDouble = false, couldBeLong = true;
													outer2:
													for (int i = s; i < p; i++)
													{
														switch (data.charAt(i))
														{
															case '0':
															case '1':
															case '2':
															case '3':
															case '4':
															case '5':
															case '6':
															case '7':
															case '8':
															case '9':
															case '-':
															case '+':
																break;
															case '.':
															case 'e':
															case 'E':
																couldBeDouble = true;
																couldBeLong = false;
																break;
															default:
																couldBeDouble = false;
																couldBeLong = false;
																break outer2;
														}
													}
													if (couldBeDouble)
													{
														try
														{
															number(name, Double.parseDouble(value), value);
															break outer;
														}
														catch (NumberFormatException ignored) { }
													}
													else if (couldBeLong)
													{
														try
														{
															number(name, Long.parseLong(value), value);
															break outer;
														}
														catch (NumberFormatException ignored) { }
													}
												}
												string(name, value);
											}
											stringIsUnquoted = false;
											s = p;
										}
										break;
										case 2:
// line 154 "JsonParsingWriter.rl"
										{
											startObject(names.poll());
											{
												if (top == stack.length)
												{
													int[] newStack = new int[stack.length * 2];
													System.arraycopy(stack, 0, newStack, 0, stack.length);
													stack = newStack;
												}
												{
													stack[top++] = cs;
													cs = 5;
													_goto_targ = 2;
													if (true) continue _goto;
												}
											}
										}
										break;
										case 3:
// line 158 "JsonParsingWriter.rl"
										{
											pop();
											{
												cs = stack[--top];
												_goto_targ = 2;
												if (true) continue _goto;
											}
										}
										break;
										case 4:
// line 162 "JsonParsingWriter.rl"
										{
											startArray(names.poll());
											{
												if (top == stack.length)
												{
													int[] newStack = new int[stack.length * 2];
													System.arraycopy(stack, 0, newStack, 0, stack.length);
													stack = newStack;
												}
												{
													stack[top++] = cs;
													cs = 23;
													_goto_targ = 2;
													if (true) continue _goto;
												}
											}
										}
										break;
										case 5:
// line 166 "JsonParsingWriter.rl"
										{
											pop();
											{
												cs = stack[--top];
												_goto_targ = 2;
												if (true) continue _goto;
											}
										}
										break;
										case 6:
// line 170 "JsonParsingWriter.rl"
										{
											int start = p - 1;
											if (data.charAt(p++) == '/')
											{
												while (p < eof && data.charAt(p) != '\n') p++;
												p--;
											}
											else
											{
												while ((p + 1 < eof && data.charAt(p) != '*') || data.charAt(p + 1) != '/')
													p++;
												p++;
											}
										}
										break;
										case 7:
// line 181 "JsonParsingWriter.rl"
										{
											s = p;
											needsUnescape = false;
											stringIsUnquoted = true;
											if (stringIsName)
											{
												outer:
												do
												{
													switch (data.charAt(p))
													{
														case '\\':
															needsUnescape = true;
															break;
														case '/':
															if (p + 1 == eof) break;
															if (data.charAt(p + 1) == '/' || data.charAt(p + 1) == '*')
																break outer;
															break;
														case ':':
														case '\r':
														case '\n':
															break outer;
													}
												}
												while (++p < eof);
											}
											else
											{
												outer:
												do
												{
													switch (data.charAt(p))
													{
														case '\\':
															needsUnescape = true;
															break;
														case '/':
															if (p + 1 == eof) break;
															if (data.charAt(p + 1) == '/' || data.charAt(p + 1) == '*')
																break outer;
															break;
														case '}':
														case ']':
														case ',':
														case '\r':
														case '\n':
															break outer;
													}
												}
												while (++p < eof);
											}
											while (Character.isWhitespace(data.charAt(p--))) ; //Backtrack
										}
										break;
										case 8:
// line 218 "JsonParsingWriter.rl"
										{
											s = ++p;
											needsUnescape = false;
											do
											{
												if (data.charAt(p) == '\\')
												{
													needsUnescape = true;
													p++;
												}
											}
											while (data.charAt(p) != '"' && ++p < eof);
											p--;
										}
										break;
// line 342 "JsonParsingWriter.java"
									}
								}
							}

						case 2:
							if (cs == 0)
							{
								_goto_targ = 5;
								continue _goto;
							}
							if (++p != pe)
							{
								_goto_targ = 1;
								continue _goto;
							}
						case 4:
							if (p == eof)
							{
								int __acts = _json_eof_actions[cs];
								int __nacts = (int)_json_actions[__acts++];
								while (__nacts-- > 0)
								{
									switch (_json_actions[__acts++])
									{
										case 1:
// line 101 "JsonParsingWriter.rl"
										{
											String value = data.substring(s, p);
											if (needsUnescape) value = unescape(value);
											outer:
											if (stringIsName)
											{
												stringIsName = false;
												names.push(value);
											}
											else
											{
												String name = names.poll();
												if (stringIsUnquoted)
												{
													if (value.equals("true"))
													{
														bool(name, true);
														break outer;
													}
													else if (value.equals("false"))
													{
														bool(name, false);
														break outer;
													}
													else if (value.equals("null"))
													{
														string(name, null);
														break outer;
													}
													boolean couldBeDouble = false, couldBeLong = true;
													outer2:
													for (int i = s; i < p; i++)
													{
														switch (data.charAt(i))
														{
															case '0':
															case '1':
															case '2':
															case '3':
															case '4':
															case '5':
															case '6':
															case '7':
															case '8':
															case '9':
															case '-':
															case '+':
																break;
															case '.':
															case 'e':
															case 'E':
																couldBeDouble = true;
																couldBeLong = false;
																break;
															default:
																couldBeDouble = false;
																couldBeLong = false;
																break outer2;
														}
													}
													if (couldBeDouble)
													{
														try
														{
															number(name, Double.parseDouble(value), value);
															break outer;
														}
														catch (NumberFormatException ignored) { }
													}
													else if (couldBeLong)
													{
														try
														{
															number(name, Long.parseLong(value), value);
															break outer;
														}
														catch (NumberFormatException ignored) { }
													}
												}
												string(name, value);
											}
											stringIsUnquoted = false;
											s = p;
										}
										break;
// line 419 "JsonParsingWriter.java"
									}
								}
							}

						case 5:
					}
					break;
				}
			}

// line 245 "JsonParsingWriter.rl"

			if (elements.size() == 0)
			{
				completedValues.push(this.root);
				init();
			}
		}
		catch (RuntimeException ex)
		{
			JsonValue exValue = Utility.exceptionToJson(ex);
			exValue.addChild("Invalid String", new JsonValue(data.substring(0, p)));
			completedValues.push(exValue);
			init();
		}
	}


	// line 447 "JsonParsingWriter.java"
	private static byte[] init__json_actions_0()
	{
		return new byte[]{
				0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1,
				6, 1, 7, 1, 8, 2, 0, 7, 2, 0, 8, 2,
				1, 3, 2, 1, 5
		};
	}

	private static final byte _json_actions[] = init__json_actions_0();


	private static short[] init__json_key_offsets_0()
	{
		return new short[]{
				0, 0, 11, 13, 14, 16, 25, 31, 37, 39, 50, 57,
				64, 73, 74, 83, 85, 87, 96, 98, 100, 101, 103, 105,
				116, 123, 130, 141, 142, 153, 155, 157, 168, 170, 172, 174,
				179, 184, 184
		};
	}

	private static final short _json_key_offsets[] = init__json_key_offsets_0();


	private static char[] init__json_trans_keys_0()
	{
		return new char[]{
				13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10, 42,
				47, 34, 42, 47, 13, 32, 34, 44, 47, 58, 125, 9,
				10, 13, 32, 47, 58, 9, 10, 13, 32, 47, 58, 9,
				10, 42, 47, 13, 32, 34, 44, 47, 58, 91, 93, 123,
				9, 10, 9, 10, 13, 32, 44, 47, 125, 9, 10, 13,
				32, 44, 47, 125, 13, 32, 34, 44, 47, 58, 125, 9,
				10, 34, 13, 32, 34, 44, 47, 58, 125, 9, 10, 42,
				47, 42, 47, 13, 32, 34, 44, 47, 58, 125, 9, 10,
				42, 47, 42, 47, 34, 42, 47, 42, 47, 13, 32, 34,
				44, 47, 58, 91, 93, 123, 9, 10, 9, 10, 13, 32,
				44, 47, 93, 9, 10, 13, 32, 44, 47, 93, 13, 32,
				34, 44, 47, 58, 91, 93, 123, 9, 10, 34, 13, 32,
				34, 44, 47, 58, 91, 93, 123, 9, 10, 42, 47, 42,
				47, 13, 32, 34, 44, 47, 58, 91, 93, 123, 9, 10,
				42, 47, 42, 47, 42, 47, 13, 32, 47, 9, 10, 13,
				32, 47, 9, 10, 0
		};
	}

	private static final char _json_trans_keys[] = init__json_trans_keys_0();


	private static byte[] init__json_single_lengths_0()
	{
		return new byte[]{
				0, 9, 2, 1, 2, 7, 4, 4, 2, 9, 7, 7,
				7, 1, 7, 2, 2, 7, 2, 2, 1, 2, 2, 9,
				7, 7, 9, 1, 9, 2, 2, 9, 2, 2, 2, 3,
				3, 0, 0
		};
	}

	private static final byte _json_single_lengths[] = init__json_single_lengths_0();


	private static byte[] init__json_range_lengths_0()
	{
		return new byte[]{
				0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0,
				1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1,
				0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1,
				1, 0, 0
		};
	}

	private static final byte _json_range_lengths[] = init__json_range_lengths_0();


	private static short[] init__json_index_offsets_0()
	{
		return new short[]{
				0, 0, 11, 14, 16, 19, 28, 34, 40, 43, 54, 62,
				70, 79, 81, 90, 93, 96, 105, 108, 111, 113, 116, 119,
				130, 138, 146, 157, 159, 170, 173, 176, 187, 190, 193, 196,
				201, 206, 207
		};
	}

	private static final short _json_index_offsets[] = init__json_index_offsets_0();


	private static byte[] init__json_indicies_0()
	{
		return new byte[]{
				1, 1, 2, 3, 4, 3, 5, 3, 6, 1, 0, 7,
				7, 3, 8, 3, 9, 9, 3, 11, 11, 12, 13, 14,
				3, 15, 11, 10, 16, 16, 17, 18, 16, 3, 19, 19,
				20, 21, 19, 3, 22, 22, 3, 21, 21, 24, 3, 25,
				3, 26, 3, 27, 21, 23, 28, 29, 28, 28, 30, 31,
				32, 3, 33, 34, 33, 33, 13, 35, 15, 3, 34, 34,
				12, 36, 37, 3, 15, 34, 10, 16, 3, 36, 36, 12,
				3, 38, 3, 3, 36, 10, 39, 39, 3, 40, 40, 3,
				13, 13, 12, 3, 41, 3, 15, 13, 10, 42, 42, 3,
				43, 43, 3, 28, 3, 44, 44, 3, 45, 45, 3, 47,
				47, 48, 49, 50, 3, 51, 52, 53, 47, 46, 54, 55,
				54, 54, 56, 57, 58, 3, 59, 60, 59, 59, 49, 61,
				52, 3, 60, 60, 48, 62, 63, 3, 51, 52, 53, 60,
				46, 54, 3, 62, 62, 48, 3, 64, 3, 51, 3, 53,
				62, 46, 65, 65, 3, 66, 66, 3, 49, 49, 48, 3,
				67, 3, 51, 52, 53, 49, 46, 68, 68, 3, 69, 69,
				3, 70, 70, 3, 8, 8, 71, 8, 3, 72, 72, 73,
				72, 3, 3, 3, 0
		};
	}

	private static final byte _json_indicies[] = init__json_indicies_0();


	private static byte[] init__json_trans_targs_0()
	{
		return new byte[]{
				35, 1, 3, 0, 4, 36, 36, 36, 36, 1, 6, 5,
				13, 17, 22, 37, 7, 8, 9, 7, 8, 9, 7, 10,
				20, 21, 11, 11, 11, 12, 17, 19, 37, 11, 12, 19,
				14, 16, 15, 14, 12, 18, 17, 11, 9, 5, 24, 23,
				27, 31, 34, 25, 38, 25, 25, 26, 31, 33, 38, 25,
				26, 33, 28, 30, 29, 28, 26, 32, 31, 25, 23, 2,
				36, 2
		};
	}

	private static final byte _json_trans_targs[] = init__json_trans_targs_0();


	private static byte[] init__json_trans_actions_0()
	{
		return new byte[]{
				13, 0, 15, 0, 0, 7, 3, 11, 1, 11, 17, 0,
				20, 0, 0, 5, 1, 1, 1, 0, 0, 0, 11, 13,
				15, 0, 7, 3, 1, 1, 1, 1, 23, 0, 0, 0,
				0, 0, 0, 11, 11, 0, 11, 11, 11, 11, 13, 0,
				15, 0, 0, 7, 9, 3, 1, 1, 1, 1, 26, 0,
				0, 0, 0, 0, 0, 11, 11, 0, 11, 11, 11, 1,
				0, 0
		};
	}

	private static final byte _json_trans_actions[] = init__json_trans_actions_0();


	private static byte[] init__json_eof_actions_0()
	{
		return new byte[]{
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				0, 0, 0
		};
	}

	private static final byte _json_eof_actions[] = init__json_eof_actions_0();


	static final int json_start = 1;
	static final int json_first_final = 35;
	static final int json_error = 0;

	static final int json_en_object = 5;
	static final int json_en_array = 23;
	static final int json_en_main = 1;


// line 262 "JsonParsingWriter.rl"

	void init()
	{
		//Flush the state
		needsUnescape = false;
		stringIsName = false;
		stringIsUnquoted = false;
		names = new ArrayDeque<>();
		data.delete(0, p);
		stack = new int[8];
		p = 0;
		s = 0;
		this.root = null;
		current = null;
		elements = new ArrayDeque<>();
		lastChild = new ArrayDeque<>();

// line 636 "JsonParsingWriter.java"
		{
			cs = json_start;
			top = 0;
		}

// line 279 "JsonParsingWriter.rl"
	}

	private void addChild(String name, JsonValue child)
	{
		child.setName(name);
		if (current == null)
		{
			current = child;
			root = child;
		}
		else if (current.isArray() || current.isObject())
		{
			child.parent = current;
			if (current.size == 0)
				current.child = child;
			else
			{
				JsonValue last = lastChild.pop();
				last.next = child;
				child.prev = last;
			}
			lastChild.push(child);
			current.size++;
		}
		else
			root = current;
	}

	protected void startObject(String name)
	{
		JsonValue value = new JsonValue(ValueType.object);
		if (current != null) addChild(name, value);
		elements.push(value);
		current = value;
	}

	protected void startArray(String name)
	{
		JsonValue value = new JsonValue(ValueType.array);
		if (current != null) addChild(name, value);
		elements.push(value);
		current = value;
	}

	protected void pop()
	{
		root = elements.pop();
		if (current.size() > 0) lastChild.pop();
		current = elements.peek();
	}

	protected void string(String name, String value)
	{
		addChild(name, new JsonValue(value));
	}

	protected void number(String name, double value, String stringValue)
	{
		addChild(name, new JsonValue(value, stringValue));
	}

	protected void number(String name, long value, String stringValue)
	{
		addChild(name, new JsonValue(value, stringValue));
	}

	protected void bool(String name, boolean value)
	{
		addChild(name, new JsonValue(value));
	}

	private String unescape(String value)
	{
		int length = value.length();
		StringBuilder buffer = new StringBuilder(length + 16);
		for (int i = 0; i < length; )
		{
			char c = value.charAt(i++);
			if (c != '\\')
			{
				buffer.append(c);
				continue;
			}
			if (i == length) break;
			c = value.charAt(i++);
			if (c == 'u')
			{
				buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
				i += 4;
				continue;
			}
			switch (c)
			{
				case '"':
				case '\\':
				case '/':
					break;
				case 'b':
					c = '\b';
					break;
				case 'f':
					c = '\f';
					break;
				case 'n':
					c = '\n';
					break;
				case 'r':
					c = '\r';
					break;
				case 't':
					c = '\t';
					break;
				default:
					throw new SerializationException("Illegal escaped character: \\" + c);
			}
			buffer.append(c);
		}
		return buffer.toString();
	}
}