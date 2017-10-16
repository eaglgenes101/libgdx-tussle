package com.tussle.main;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.SerializationException;

import java.io.IOException;
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
    public static final boolean doDebug = false;

    int cs, p, top;
    int s;
	int[] stack;
	int reentryPoint;

    Deque<JsonValue> completedValues;
	Deque<String> names;

	private Deque<JsonValue> elements;
	private Deque<JsonValue> lastChild;
	private JsonValue root, current;

	Writer writ;

	public JsonParsingWriter(Writer pip)
	{
	    data = new StringBuilder();
	    completedValues = new LinkedList<>();
	    init();
	    writ = pip;
	}

	public void write(char[] cbuf, int off, int len)
	{
	    data.append(cbuf, off, len);
	}

	public JsonValue read() throws IOException
	{
	    flush();
	    return completedValues.poll();
	}

	public boolean ready() throws IOException
	{
	    flush();
	    return !completedValues.isEmpty();
	}

	public void close() throws IOException
	{
	    flush();
	}

	public void flush() throws IOException
	{
	    if (!completedValues.isEmpty()) return;
	    if (p >= data.length()) return;
        %%{
        	machine json;
            getkey data.charAt(p);
            variable pe data.length();
            variable eof data.length();

            prepush {
        		if (top == stack.length) {
            		int[] newStack = new int[stack.length * 2];
    				System.arraycopy(stack, 0, newStack, 0, stack.length);
        			stack = newStack;
        	    }
            }
            action startObject {
    	        startObject(names.poll());
				reentryPoint = fentry(object);
    	        if (doDebug) System.out.println("Starting object");
	    		fcall object;
		    }
		    action end {
            	reentryPoint = pop();
            	if (doDebug) System.out.println("Ending");
	            fret;
		    }
    	    action startArray {
        		startArray(names.poll());
				reentryPoint = fentry(array);
        		if (doDebug) System.out.println("Staring array");
	    	    fcall array;
    	    }
			action start { s = p; }
	        action name
	        {
	            addName(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("Name from %s%n", data.substring(s, p));
	        }
	    	action string
	    	{
	    	    addString(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("String from %s%n", data.substring(s, p));
	    	}
		    action null
		    {
		        addNull();
	            if (doDebug) System.out.println("Null");
		    }
		    action true
		    {
		        addTrue();
		        if (doDebug) System.out.println("True");
		    }
	    	action false
	    	{
	    	    addFalse();
	    	    if (doDebug) System.out.println("False");
	    	}
	    	action number
	    	{
	    	    try
	    	    {
	    	        addNumber(data.substring(s, p));
	                if (doDebug) System.out.printf("Number from %s%n", data.substring(s, p));
	    	    }
	    	    catch (NumberFormatException e)
	    	    {
		    	    //Empty the stack, output the errant string, move on
		    	    writ.write(data.substring(0, p));
		    	    if (doDebug) System.out.printf("Failed number from %s%n", data.substring(s, p));
		    	    init();
		    	    fexec 0;
		    	    fgoto main;
	    	    }
	    	}
		    action output {
		        if (root != null)
		        {
		    	    completedValues.add(root);
		    	    if (doDebug) System.out.println("Completed JSON");
		    	    reentryPoint = -1;
    	            data.delete(0, p);
    	            p = 0;
	                fbreak;
	            }
		    }
		    action exit {
		    	if (doDebug) System.out.println("Interrupted");
		    	fhold;
		    	fbreak;
		    }
		    action error {
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	if (doDebug) System.out.println("Error");
		    	init();
		    	fexec 1; //Magic constant?
		    	fgoto main;
		    }
		    action slew {
		        if (p > 0)
		        {
		            writ.write(data.substring(0, p));
		    	    if (doDebug) System.out.printf("Slew: \"%s\"%n", data.substring(0, p));
		            data.delete(0, p);
		            p = 0;
		        }
		    }
		    action reenter
		    {
		        if (reentryPoint != -1)
		        {
		            fhold;
		            fgoto *reentryPoint;
		        }
		    }

        	ws = [\r\n\t ];
	    	escapeChar = "\\" [\"\\bfnrt];
	    	escapeUnicode = "\\u" xdigit{4};
	    	strForm = '"' (escapeChar | escapeUnicode | ^[\\\"])** '"';
    	    intForm = ('+'|'-')? ('0'..'9') ('0'..'9')**;
            outside = strForm | (any - zlen - [\"[{]);

    	    str = strForm >start %string;
    	    bool = "null" %null | "true" %true | "false" %false;
    	    number = (intForm ("." ('0'..'9')**)? ([Ee] intForm)?) >start %number;

		    value = '{' @startObject | '[' @startArray | str | bool | number;
	        nameValue = strForm >start %name ws* ':' ws* value;
    	    object := ws* nameValue? ws* <: (',' ws* nameValue ws*)** :>> (','? ws* '}' @end) $/exit $!error;
            array := ws* value? ws* <: (',' ws* value ws*)** :>> (','? ws* ']' @end) $/exit $!error;
    	    main := ((outside**) %slew :> ('{' @startObject | '[' @startArray) %output )* >reenter $/exit $!error;

        	write exec;
    	}%%

    }

	%% write data;

    void init()
    {
        //Flush the state
    	data.delete(0, p);
    	p = 0;
        names = new ArrayDeque<>();
        stack = new int[8];
    	root = null;
		current = null;
		reentryPoint = -1;
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

	protected void startObject (String name)
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

	protected int pop()
	{
		root = elements.pop();
		if (current.size() > 0) lastChild.pop();
		current = elements.peek();
		if (top == 0) return -1;
		else return stack[top-1];
	}

	protected void addName(String name)
	{
	    names.add(unescape(name));
	}

	protected void addString(String value)
	{
	    addChild(names.poll(), new JsonValue(unescape(value)));
	}

	protected void addNumber (String value) throws NumberFormatException
	{
	    JsonValue toAddVal;
	    try
	    {
	        long val = Long.parseLong(value);
	        toAddVal = new JsonValue(val, value);
	    }
	    catch (NumberFormatException n)
	    {
	        double val = Double.parseDouble(value);
	        toAddVal = new JsonValue(val, value);
	    }
	    addChild(names.poll(), toAddVal);
	}

	protected void addNull()
	{
	    addChild(names.poll(), new JsonValue(JsonValue.ValueType.nullValue));
	}

	protected void addTrue()
	{
	    addChild(names.poll(), new JsonValue(true));
	}

	protected void addFalse()
	{
	    addChild(names.poll(), new JsonValue(false));
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
