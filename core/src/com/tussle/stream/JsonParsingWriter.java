
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

// line 1 "JsonParsingWriter.rl"
package com.tussle.stream;

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
        
// line 88 "JsonParsingWriter.java"
	{
	int _klen;
	int _trans = 0;
	int _acts;
	int _nacts;
	int _keys;
	int _goto_targ = 0;

	_goto: while (true) {
	switch ( _goto_targ ) {
	case 0:
	if ( p == ( data.length()) ) {
		_goto_targ = 4;
		continue _goto;
	}
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
case 1:
	_match: do {
	_keys = _json_key_offsets[cs];
	_trans = _json_index_offsets[cs];
	_klen = _json_single_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + _klen - 1;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + ((_upper-_lower) >> 1);
			if ( ( data.charAt(p)) < _json_trans_keys[_mid] )
				_upper = _mid - 1;
			else if ( ( data.charAt(p)) > _json_trans_keys[_mid] )
				_lower = _mid + 1;
			else {
				_trans += (_mid - _keys);
				break _match;
			}
		}
		_keys += _klen;
		_trans += _klen;
	}

	_klen = _json_range_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + (_klen<<1) - 2;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + (((_upper-_lower) >> 1) & ~1);
			if ( ( data.charAt(p)) < _json_trans_keys[_mid] )
				_upper = _mid - 2;
			else if ( ( data.charAt(p)) > _json_trans_keys[_mid+1] )
				_lower = _mid + 2;
			else {
				_trans += ((_mid - _keys)>>1);
				break _match;
			}
		}
		_trans += _klen;
	}
	} while (false);

	cs = _json_trans_targs[_trans];

	if ( _json_trans_actions[_trans] != 0 ) {
		_acts = _json_trans_actions[_trans];
		_nacts = (int) _json_actions[_acts++];
		while ( _nacts-- > 0 )
	{
			switch ( _json_actions[_acts++] )
			{
	case 0:
// line 97 "JsonParsingWriter.rl"
	{
    	        startObject(names.poll());
				reentryPoint = 8;
    	        if (doDebug) System.out.println("Starting object");
	    		{
        		if (top == stack.length) {
            		int[] newStack = new int[stack.length * 2];
    				System.arraycopy(stack, 0, newStack, 0, stack.length);
        			stack = newStack;
        	    }
            {stack[top++] = cs; cs = 8; _goto_targ = 2; if (true) continue _goto;}}
		    }
	break;
	case 1:
// line 103 "JsonParsingWriter.rl"
	{
            	reentryPoint = pop();
            	if (doDebug) System.out.println("Ending");
	            {cs = stack[--top];_goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 2:
// line 108 "JsonParsingWriter.rl"
	{
        		startArray(names.poll());
				reentryPoint = 46;
        		if (doDebug) System.out.println("Staring array");
	    	    {
        		if (top == stack.length) {
            		int[] newStack = new int[stack.length * 2];
    				System.arraycopy(stack, 0, newStack, 0, stack.length);
        			stack = newStack;
        	    }
            {stack[top++] = cs; cs = 46; _goto_targ = 2; if (true) continue _goto;}}
    	    }
	break;
	case 3:
// line 114 "JsonParsingWriter.rl"
	{ s = p; }
	break;
	case 4:
// line 116 "JsonParsingWriter.rl"
	{
	            addName(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("Name from %s%n", data.substring(s, p));
	        }
	break;
	case 5:
// line 121 "JsonParsingWriter.rl"
	{
	    	    addString(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("String from %s%n", data.substring(s, p));
	    	}
	break;
	case 6:
// line 126 "JsonParsingWriter.rl"
	{
		        addNull();
	            if (doDebug) System.out.println("Null");
		    }
	break;
	case 7:
// line 131 "JsonParsingWriter.rl"
	{
		        addTrue();
		        if (doDebug) System.out.println("True");
		    }
	break;
	case 8:
// line 136 "JsonParsingWriter.rl"
	{
	    	    addFalse();
	    	    if (doDebug) System.out.println("False");
	    	}
	break;
	case 9:
// line 141 "JsonParsingWriter.rl"
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
		    	    {p = (( 0))-1;}
		    	    {cs = 75; _goto_targ = 2; if (true) continue _goto;}
	    	    }
	    	}
	break;
	case 10:
// line 157 "JsonParsingWriter.rl"
	{
		        if (root != null)
		        {
		    	    completedValues.add(root);
		    	    if (doDebug) System.out.println("Completed JSON");
		    	    reentryPoint = -1;
    	            data.delete(0, p);
    	            p = 0;
	                { p += 1; _goto_targ = 5; if (true)  continue _goto;}
	            }
		    }
	break;
	case 12:
// line 173 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	if (doDebug) System.out.println("Error");
		    	init();
		    	{p = (( 1))-1;} //Magic constant?
		    	{cs = 75; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 13:
// line 181 "JsonParsingWriter.rl"
	{
		        if (p > 0)
		        {
		            writ.write(data.substring(0, p));
		    	    if (doDebug) System.out.printf("Slew: \"%s\"%n", data.substring(0, p));
		            data.delete(0, p);
		            p = 0;
		        }
		    }
	break;
	case 14:
// line 191 "JsonParsingWriter.rl"
	{
		        if (reentryPoint != -1)
		        {
		            p--;
		            {cs = (reentryPoint); _goto_targ = 2; if (true) continue _goto;}
		        }
		    }
	break;
// line 310 "JsonParsingWriter.java"
			}
		}
	}

case 2:
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
	if ( ++p != ( data.length()) ) {
		_goto_targ = 1;
		continue _goto;
	}
case 4:
	if ( p == ( data.length()) )
	{
	int __acts = _json_eof_actions[cs];
	int __nacts = (int) _json_actions[__acts++];
	while ( __nacts-- > 0 ) {
		switch ( _json_actions[__acts++] ) {
	case 10:
// line 157 "JsonParsingWriter.rl"
	{
		        if (root != null)
		        {
		    	    completedValues.add(root);
		    	    if (doDebug) System.out.println("Completed JSON");
		    	    reentryPoint = -1;
    	            data.delete(0, p);
    	            p = 0;
	                { p += 1; _goto_targ = 5; if (true)  continue _goto;}
	            }
		    }
	break;
	case 11:
// line 168 "JsonParsingWriter.rl"
	{
		    	if (doDebug) System.out.println("Interrupted");
		    	p--;
		    	{ p += 1; _goto_targ = 5; if (true)  continue _goto;}
		    }
	break;
	case 12:
// line 173 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	if (doDebug) System.out.println("Error");
		    	init();
		    	{p = (( 1))-1;} //Magic constant?
		    	{cs = 75; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 14:
// line 191 "JsonParsingWriter.rl"
	{
		        if (reentryPoint != -1)
		        {
		            p--;
		            {cs = (reentryPoint); _goto_targ = 2; if (true) continue _goto;}
		        }
		    }
	break;
// line 374 "JsonParsingWriter.java"
		}
	}
	}

case 5:
	}
	break; }
	}

// line 217 "JsonParsingWriter.rl"


    }

	
// line 390 "JsonParsingWriter.java"
private static byte[] init__json_actions_0()
{
	return new byte [] {
	    0,    1,    0,    1,    1,    1,    2,    1,    3,    1,    4,    1,
	    5,    1,    6,    1,    7,    1,    8,    1,    9,    1,   10,    1,
	   11,    1,   12,    1,   14,    2,    5,    1,    2,    6,    1,    2,
	    7,    1,    2,    8,    1,    2,    9,    1,    2,   10,   11,    2,
	   11,   12,    2,   13,    0,    2,   13,    2,    2,   14,   11,    3,
	   10,   13,    0,    3,   10,   13,    2,    3,   14,   13,    0,    3,
	   14,   13,    2
	};
}

private static final byte _json_actions[] = init__json_actions_0();


private static short[] init__json_key_offsets_0()
{
	return new short [] {
	    0,    0,    3,    5,   13,   19,   25,   31,   37,   44,   46,   51,
	   56,   70,   72,   78,   84,   90,   98,  104,  110,  116,  122,  124,
	  135,  145,  149,  151,  159,  160,  161,  162,  163,  169,  170,  171,
	  172,  178,  179,  180,  181,  187,  195,  201,  207,  213,  219,  235,
	  237,  243,  249,  264,  266,  277,  287,  291,  293,  301,  302,  303,
	  304,  305,  311,  312,  313,  314,  320,  321,  322,  323,  329,  337,
	  343,  349,  355,  361,  364,  367,  367
	};
}

private static final short _json_key_offsets[] = init__json_key_offsets_0();


private static char[] init__json_trans_keys_0()
{
	return new char [] {
	   34,   91,  123,   34,   92,   34,   92,   98,  102,  110,  114,  116,
	  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,   97,
	  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,   97,
	  102,   13,   32,   34,   44,  125,    9,   10,   34,   92,   13,   32,
	   58,    9,   10,   13,   32,   58,    9,   10,   13,   32,   34,   43,
	   45,   91,  102,  110,  116,  123,    9,   10,   48,   57,   34,   92,
	   13,   32,   44,  125,    9,   10,   13,   32,   44,  125,    9,   10,
	   13,   32,   34,  125,    9,   10,   34,   92,   98,  102,  110,  114,
	  116,  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,
	   97,  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,
	   97,  102,   48,   57,   13,   32,   44,   46,   69,  101,  125,    9,
	   10,   48,   57,   13,   32,   44,   69,  101,  125,    9,   10,   48,
	   57,   43,   45,   48,   57,   48,   57,   13,   32,   44,  125,    9,
	   10,   48,   57,   97,  108,  115,  101,   13,   32,   44,  125,    9,
	   10,  117,  108,  108,   13,   32,   44,  125,    9,   10,  114,  117,
	  101,   13,   32,   44,  125,    9,   10,   34,   92,   98,  102,  110,
	  114,  116,  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   13,   32,   34,   44,   91,   93,  102,  110,  116,
	  123,    9,   10,   43,   45,   48,   57,   34,   92,   13,   32,   44,
	   93,    9,   10,   13,   32,   44,   93,    9,   10,   13,   32,   34,
	   43,   45,   91,   93,  102,  110,  116,  123,    9,   10,   48,   57,
	   48,   57,   13,   32,   44,   46,   69,   93,  101,    9,   10,   48,
	   57,   13,   32,   44,   69,   93,  101,    9,   10,   48,   57,   43,
	   45,   48,   57,   48,   57,   13,   32,   44,   93,    9,   10,   48,
	   57,   97,  108,  115,  101,   13,   32,   44,   93,    9,   10,  117,
	  108,  108,   13,   32,   44,   93,    9,   10,  114,  117,  101,   13,
	   32,   44,   93,    9,   10,   34,   92,   98,  102,  110,  114,  116,
	  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,   97,
	  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,   97,
	  102,   34,   91,  123,   34,   91,  123,    0
	};
}

private static final char _json_trans_keys[] = init__json_trans_keys_0();


private static byte[] init__json_single_lengths_0()
{
	return new byte [] {
	    0,    3,    2,    8,    0,    0,    0,    0,    5,    2,    3,    3,
	   10,    2,    4,    4,    4,    8,    0,    0,    0,    0,    0,    7,
	    6,    2,    0,    4,    1,    1,    1,    1,    4,    1,    1,    1,
	    4,    1,    1,    1,    4,    8,    0,    0,    0,    0,   10,    2,
	    4,    4,   11,    0,    7,    6,    2,    0,    4,    1,    1,    1,
	    1,    4,    1,    1,    1,    4,    1,    1,    1,    4,    8,    0,
	    0,    0,    0,    3,    3,    0,    0
	};
}

private static final byte _json_single_lengths[] = init__json_single_lengths_0();


private static byte[] init__json_range_lengths_0()
{
	return new byte [] {
	    0,    0,    0,    0,    3,    3,    3,    3,    1,    0,    1,    1,
	    2,    0,    1,    1,    1,    0,    3,    3,    3,    3,    1,    2,
	    2,    1,    1,    2,    0,    0,    0,    0,    1,    0,    0,    0,
	    1,    0,    0,    0,    1,    0,    3,    3,    3,    3,    3,    0,
	    1,    1,    2,    1,    2,    2,    1,    1,    2,    0,    0,    0,
	    0,    1,    0,    0,    0,    1,    0,    0,    0,    1,    0,    3,
	    3,    3,    3,    0,    0,    0,    0
	};
}

private static final byte _json_range_lengths[] = init__json_range_lengths_0();


private static short[] init__json_index_offsets_0()
{
	return new short [] {
	    0,    0,    4,    7,   16,   20,   24,   28,   32,   39,   42,   47,
	   52,   65,   68,   74,   80,   86,   95,   99,  103,  107,  111,  113,
	  123,  132,  136,  138,  145,  147,  149,  151,  153,  159,  161,  163,
	  165,  171,  173,  175,  177,  183,  192,  196,  200,  204,  208,  222,
	  225,  231,  237,  251,  253,  263,  272,  276,  278,  285,  287,  289,
	  291,  293,  299,  301,  303,  305,  311,  313,  315,  317,  323,  332,
	  336,  340,  344,  348,  352,  356,  357
	};
}

private static final short _json_index_offsets[] = init__json_index_offsets_0();


private static byte[] init__json_trans_targs_0()
{
	return new byte [] {
	    2,   76,   76,    1,    1,    3,    2,    2,    2,    2,    2,    2,
	    2,    2,    4,    0,    5,    5,    5,    0,    6,    6,    6,    0,
	    7,    7,    7,    0,    2,    2,    2,    0,    8,    8,    9,   16,
	   77,    8,    0,   10,   41,    9,   11,   11,   12,   11,    0,   11,
	   11,   12,   11,    0,   12,   12,   13,   22,   22,   15,   28,   33,
	   37,   15,   12,   23,    0,   14,   17,   13,   15,   15,   16,   77,
	   15,    0,   15,   15,   16,   77,   15,    0,   16,   16,    9,   77,
	   16,    0,   13,   13,   13,   13,   13,   13,   13,   18,    0,   19,
	   19,   19,    0,   20,   20,   20,    0,   21,   21,   21,    0,   13,
	   13,   13,    0,   23,    0,   15,   15,   16,   24,   25,   25,   77,
	   15,   23,    0,   15,   15,   16,   25,   25,   77,   15,   24,    0,
	   26,   26,   27,    0,   27,    0,   15,   15,   16,   77,   15,   27,
	    0,   29,    0,   30,    0,   31,    0,   32,    0,   15,   15,   16,
	   77,   15,    0,   34,    0,   35,    0,   36,    0,   15,   15,   16,
	   77,   15,    0,   38,    0,   39,    0,   40,    0,   15,   15,   16,
	   77,   15,    0,    9,    9,    9,    9,    9,    9,    9,   42,    0,
	   43,   43,   43,    0,   44,   44,   44,    0,   45,   45,   45,    0,
	    9,    9,    9,    0,   46,   46,   47,   50,   49,   78,   57,   62,
	   66,   49,   46,   51,   52,    0,   48,   70,   47,   49,   49,   50,
	   78,   49,    0,   49,   49,   50,   78,   49,    0,   50,   50,   47,
	   51,   51,   49,   78,   57,   62,   66,   49,   50,   52,    0,   52,
	    0,   49,   49,   50,   53,   54,   78,   54,   49,   52,    0,   49,
	   49,   50,   54,   78,   54,   49,   53,    0,   55,   55,   56,    0,
	   56,    0,   49,   49,   50,   78,   49,   56,    0,   58,    0,   59,
	    0,   60,    0,   61,    0,   49,   49,   50,   78,   49,    0,   63,
	    0,   64,    0,   65,    0,   49,   49,   50,   78,   49,    0,   67,
	    0,   68,    0,   69,    0,   49,   49,   50,   78,   49,    0,   47,
	   47,   47,   47,   47,   47,   47,   71,    0,   72,   72,   72,    0,
	   73,   73,   73,    0,   74,   74,   74,    0,   47,   47,   47,    0,
	    2,   76,   76,    1,    2,   76,   76,    1,    0,    0,    0
	};
}

private static final byte _json_trans_targs[] = init__json_trans_targs_0();


private static byte[] init__json_trans_actions_0()
{
	return new byte [] {
	    0,   53,   50,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   25,    0,    0,    0,   25,    0,    0,    0,   25,
	    0,    0,    0,   25,    0,    0,    0,   25,    0,    0,    7,    0,
	    3,    0,   25,    0,    0,    0,    9,    9,    9,    9,    0,    0,
	    0,    0,    0,    0,    0,    0,    7,    7,    7,    5,    0,    0,
	    0,    1,    0,    7,    0,    0,    0,    0,   11,   11,   11,   29,
	   11,   25,    0,    0,    0,    3,    0,   25,    0,    0,    7,    3,
	    0,   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,   19,   19,   19,    0,    0,    0,   41,
	   19,    0,   25,   19,   19,   19,    0,    0,   41,   19,    0,   25,
	    0,    0,    0,    0,    0,    0,   19,   19,   19,   41,   19,    0,
	   25,    0,    0,    0,    0,    0,    0,    0,    0,   17,   17,   17,
	   38,   17,   25,    0,    0,    0,    0,    0,    0,   13,   13,   13,
	   32,   13,   25,    0,    0,    0,    0,    0,    0,   15,   15,   15,
	   35,   15,   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    7,    0,    5,    3,    0,    0,
	    0,    1,    0,    7,    7,   25,    0,    0,    0,   11,   11,   11,
	   29,   11,   25,    0,    0,    0,    3,    0,   25,    0,    0,    7,
	    7,    7,    5,    3,    0,    0,    0,    1,    0,    7,   25,    0,
	    0,   19,   19,   19,    0,    0,   41,    0,   19,    0,   25,   19,
	   19,   19,    0,   41,    0,   19,    0,   25,    0,    0,    0,    0,
	    0,    0,   19,   19,   19,   41,   19,    0,   25,    0,    0,    0,
	    0,    0,    0,    0,    0,   17,   17,   17,   38,   17,   25,    0,
	    0,    0,    0,    0,    0,   13,   13,   13,   32,   13,   25,    0,
	    0,    0,    0,    0,    0,   15,   15,   15,   35,   15,   25,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	   27,   71,   67,   27,   21,   63,   59,   21,   25,   25,    0
	};
}

private static final byte _json_trans_actions[] = init__json_trans_actions_0();


private static byte[] init__json_eof_actions_0()
{
	return new byte [] {
	    0,   47,   47,   47,   47,   47,   47,   47,   47,    0,    0,    0,
	    0,    0,   47,   47,   47,    0,    0,    0,    0,    0,    0,   47,
	   47,    0,    0,   47,    0,    0,    0,    0,   47,    0,    0,    0,
	   47,    0,    0,    0,   47,    0,    0,    0,    0,    0,   47,    0,
	   47,   47,   47,    0,   47,   47,    0,    0,   47,    0,    0,    0,
	    0,   47,    0,    0,    0,   47,    0,    0,    0,   47,    0,    0,
	    0,    0,    0,   56,   44,   23,   23
	};
}

private static final byte _json_eof_actions[] = init__json_eof_actions_0();


static final int json_start = 75;
static final int json_first_final = 75;
static final int json_error = 0;

static final int json_en_object = 8;
static final int json_en_array = 46;
static final int json_en_main = 75;


// line 222 "JsonParsingWriter.rl"

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
        
// line 629 "JsonParsingWriter.java"
	{
	cs = json_start;
	top = 0;
	}

// line 236 "JsonParsingWriter.rl"
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
