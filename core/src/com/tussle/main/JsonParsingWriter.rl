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
        	%%{
        		machine json;
        		getkey data.charAt(p);

        		prepush {
        			if (top == stack.length) {
        				int[] newStack = new int[stack.length * 2];
        				System.arraycopy(stack, 0, newStack, 0, stack.length);
        				stack = newStack;
        			}
        		}
        		action name {
        			stringIsName = true;
        		}
        		action string {
        			String value = data.substring(s, p);
        			if (needsUnescape) value = unescape(value);
        			outer: if (stringIsName) {
        				stringIsName = false;
        				names.push(value);
        			} else {
        				String name = names.poll();
        				if (stringIsUnquoted) {
        					if (value.equals("true")) {
        						bool(name, true);
        						break outer;
        					} else if (value.equals("false")) {
        						bool(name, false);
        						break outer;
        					} else if (value.equals("null")) {
        						string(name, null);
					       		break outer;
		          			}
        					boolean couldBeDouble = false, couldBeLong = true;
			        		outer2: for (int i = s; i < p; i++) {
					       		switch (data.charAt(i)) {
					        		case '0': case '1': case '2': case '3':
					        		case '4': case '5': case '6': case '7':
					        		case '8': case '9': case '-': case '+':
					    	    		break;
				            		case '.': case 'e': case 'E':
				        	    		couldBeDouble = true;
				        		    	couldBeLong = false;
				            			break;
				            		default:
					            		couldBeDouble = false;
					        			couldBeLong = false;
					        			break outer2;
			        			}
			    			}
			    			if (couldBeDouble) {
					        	try {
				        			number(name, Double.parseDouble(value), value);
			        				break outer;
			        			} catch (NumberFormatException ignored) { }
				        	} else if (couldBeLong) {
		        				try {
			        				number(name, Long.parseLong(value), value);
				        			break outer;
				        		} catch (NumberFormatException ignored) { }
		        			}
			        	}
	        			string(name, value);
		        	}
				    stringIsUnquoted = false;
		        	s = p;
		    	}
        		action startObject {
			        startObject(names.poll());
	        		fcall object;
		        }
		    	action endObject {
        			pop();
		        	fret;
    			}
        		action startArray {
        			startArray(names.poll());
		        	fcall array;
	    		}
	        	action endArray {
        			pop();
				    fret;
			    }
		    	action comment {
		           	int start = p - 1;
		        	if (data.charAt(p++) == '/') {
		        		while (p < eof && data.charAt(p) != '\n') p++;
			        	p--;
			    	} else {
			        	while ((p+1 < eof && data.charAt(p) != '*') || data.charAt(p+1) != '/')
		    				p++;
    					p++;
			        }
			    }
			    action unquotedChars {
		    		s = p;
	        		needsUnescape = false;
	        		stringIsUnquoted = true;
		        	if (stringIsName) {
				    	outer: do {
			    			switch (data.charAt(p)) {
        			    		case '\\':
    				    			needsUnescape = true;
        				    		break;
        				    	case '/':
        				    		if (p + 1 == eof) break;
        			    			if (data.charAt(p+1)=='/' || data.charAt(p+1)=='*')
        			    			    break outer;
        			    			break;
        			    		case ':': case '\r': case '\n':
        				    		break outer;
        					}
        				} while (++p < eof);
        			} else {
				    	outer: do {
		    				switch (data.charAt(p)) {
			        	    	case '\\':
			            			needsUnescape = true;
	        		    			break;
        				    	case '/':
        				    		if (p + 1 == eof) break;
        							if (data.charAt(p+1)=='/' || data.charAt(p+1)=='*')
        			    			    break outer;
					             	break;
			        		    case '}': case ']': case ',': case '\r': case '\n':
		    				    	break outer;
    						}
	        			} while (++p < eof);
		        	}
		        	while (Character.isWhitespace(data.charAt(p--))); //Backtrack
		    	}
			    action quotedChars {
        			s = ++p;
        			needsUnescape = false;
        			do {
        			    if (data.charAt(p) == '\\')
    				    {
    				        needsUnescape = true;
        				    p++;
        				}
			        } while (data.charAt(p) != '"' && ++p < eof);
           			p--;
        		}

        		comment = ("//" | "/*") @comment;
        		ws = [\r\n\t ] | comment;
			    ws2 = [\r\t ] | comment;
		        comma = ',' | ('\n' ws* ','?);
	        	quotedString = '"' @quotedChars %string '"';
        		nameString = quotedString | ^[":,}/\r\n\t ] >unquotedChars %string;
        		valueString = quotedString | ^[":,{[\]/\r\n\t ] >unquotedChars %string;
			    value = '{' @startObject | '[' @startArray | valueString;
		        nameValue = nameString >name ws* ':' ws* value;
        		object := ws* nameValue? ws2* <: (comma ws* nameValue ws2*)** :>> (','? ws* '}' @endObject);
        		array := ws* value? ws2* <: (comma ws* value ws2*)** :>> (','? ws* ']' @endArray);
        		main := ws* value ws*;

        		write exec;
    		}%%
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

	%% write data;

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
        %% write init;
	}

	private void addChild (String name, JsonValue child) {
		child.setName(name);
		if (current == null) {
			current = child;
			root = child;
		} else if (current.isArray() || current.isObject()) {
			child.parent = current;
			if (current.size == 0)
				current.child = child;
			else {
				JsonValue last = lastChild.pop();
				last.next = child;
				child.prev = last;
			}
			lastChild.push(child);
			current.size++;
		} else
			root = current;
	}

	protected void startObject (String name) {
		JsonValue value = new JsonValue(ValueType.object);
		if (current != null) addChild(name, value);
		elements.push(value);
		current = value;
	}

	protected void startArray (String name) {
		JsonValue value = new JsonValue(ValueType.array);
		if (current != null) addChild(name, value);
		elements.push(value);
		current = value;
	}

	protected void pop () {
		root = elements.pop();
		if (current.size() > 0) lastChild.pop();
		current = elements.peek();
	}

	protected void string (String name, String value) {
		addChild(name, new JsonValue(value));
	}

	protected void number (String name, double value, String stringValue) {
		addChild(name, new JsonValue(value, stringValue));
	}

	protected void number (String name, long value, String stringValue) {
		addChild(name, new JsonValue(value, stringValue));
	}

	protected void bool (String name, boolean value) {
		addChild(name, new JsonValue(value));
	}

	private String unescape (String value)
	{
		int length = value.length();
		StringBuilder buffer = new StringBuilder(length + 16);
		for (int i = 0; i < length;) {
			char c = value.charAt(i++);
			if (c != '\\') {
				buffer.append(c);
				continue;
			}
			if (i == length) break;
			c = value.charAt(i++);
			if (c == 'u') {
				buffer.append(Character.toChars(Integer.parseInt(value.substring(i, i + 4), 16)));
				i += 4;
				continue;
			}
			switch (c) {
			    case '"': case '\\': case '/':
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