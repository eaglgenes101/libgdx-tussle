
// line 1 "JsonParsingWriter.rl"
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
    public static final boolean doDebug = true;

    int cs, p, top;
    int s;
	int[] stack;

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
        
// line 87 "JsonParsingWriter.java"
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

	_trans = _json_indicies[_trans];
	cs = _json_trans_targs[_trans];

	if ( _json_trans_actions[_trans] != 0 ) {
		_acts = _json_trans_actions[_trans];
		_nacts = (int) _json_actions[_acts++];
		while ( _nacts-- > 0 )
	{
			switch ( _json_actions[_acts++] )
			{
	case 0:
// line 96 "JsonParsingWriter.rl"
	{
    	        startObject(names.poll());
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
// line 101 "JsonParsingWriter.rl"
	{
            	pop();
            	if (doDebug) System.out.println("Ending");
	            {cs = stack[--top];_goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 2:
// line 106 "JsonParsingWriter.rl"
	{
        		startArray(names.poll());
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
// line 111 "JsonParsingWriter.rl"
	{ s = p; }
	break;
	case 4:
// line 113 "JsonParsingWriter.rl"
	{
	            addName(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("Name from %s\n", data.substring(s, p));
	        }
	break;
	case 5:
// line 118 "JsonParsingWriter.rl"
	{
	    	    addString(data.substring(s+1, p-1));
	            if (doDebug) System.out.printf("String from %s\n", data.substring(s, p));
	    	}
	break;
	case 6:
// line 123 "JsonParsingWriter.rl"
	{
		        addNull();
	            if (doDebug) System.out.println("Null");
		    }
	break;
	case 7:
// line 128 "JsonParsingWriter.rl"
	{
		        addTrue();
		        if (doDebug) System.out.println("True");
		    }
	break;
	case 8:
// line 133 "JsonParsingWriter.rl"
	{
	    	    addFalse();
	    	    if (doDebug) System.out.println("False");
	    	}
	break;
	case 9:
// line 138 "JsonParsingWriter.rl"
	{
	    	    try
	    	    {
	    	        addNumber(data.substring(s, p));
	                if (doDebug) System.out.printf("Number from %s\n", data.substring(s, p));
	    	    }
	    	    catch (NumberFormatException e)
	    	    {
		    	    //Empty the stack, output the errant string, move on
		    	    writ.write(data.substring(0, p));
		    	    if (doDebug) System.out.printf("Failed number from %s\n", data.substring(s, p));
		    	    init();
		    	    {p = (( 0))-1;}
		    	    {cs = 75; _goto_targ = 2; if (true) continue _goto;}
	    	    }
	    	}
	break;
	case 10:
// line 154 "JsonParsingWriter.rl"
	{
		        if (root != null)
		        {
		    	    completedValues.add(root);
		    	    if (doDebug) System.out.println("Completed JSON");
    	            data.delete(0, p);
    	            {p = (( 0))-1;}
	                { p += 1; _goto_targ = 5; if (true)  continue _goto;}
	            }
		    }
	break;
	case 12:
// line 168 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	if (doDebug) System.out.println("Error");
		    	init();
		    	{p = (( 0))-1;}
		    	{cs = 75; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 13:
// line 176 "JsonParsingWriter.rl"
	{
		        if (p > 0)
		        {
		            writ.write(data.substring(0, p));
		    	    if (doDebug) System.out.printf("Slew: \"%s\"\n", data.substring(0, p));
		            data.delete(0, p);
		            p = 0;
		        }
		    }
	break;
// line 297 "JsonParsingWriter.java"
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
// line 154 "JsonParsingWriter.rl"
	{
		        if (root != null)
		        {
		    	    completedValues.add(root);
		    	    if (doDebug) System.out.println("Completed JSON");
    	            data.delete(0, p);
    	            {p = (( 0))-1;}
	                { p += 1; _goto_targ = 5; if (true)  continue _goto;}
	            }
		    }
	break;
	case 11:
// line 164 "JsonParsingWriter.rl"
	{
		    	if (doDebug) System.out.println("Interrupted");
		    	{ p += 1; _goto_targ = 5; if (true)  continue _goto;}
		    }
	break;
	case 12:
// line 168 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	if (doDebug) System.out.println("Error");
		    	init();
		    	{p = (( 0))-1;}
		    	{cs = 75; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
// line 349 "JsonParsingWriter.java"
		}
	}
	}

case 5:
	}
	break; }
	}

// line 204 "JsonParsingWriter.rl"


    }

	
// line 365 "JsonParsingWriter.java"
private static byte[] init__json_actions_0()
{
	return new byte [] {
	    0,    1,    0,    1,    1,    1,    2,    1,    3,    1,    4,    1,
	    5,    1,    6,    1,    7,    1,    8,    1,    9,    1,   10,    1,
	   11,    1,   12,    2,    5,    1,    2,    6,    1,    2,    7,    1,
	    2,    8,    1,    2,    9,    1,    2,   10,   11,    2,   11,   12,
	    2,   13,    0,    2,   13,    2,    3,   10,   13,    0,    3,   10,
	   13,    2
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


private static byte[] init__json_indicies_0()
{
	return new byte [] {
	    1,    2,    3,    0,    0,    4,    1,    1,    1,    1,    1,    1,
	    1,    1,    6,    5,    7,    7,    7,    5,    8,    8,    8,    5,
	    9,    9,    9,    5,    1,    1,    1,    5,   10,   10,   11,   12,
	   13,   10,    5,   15,   16,   14,   17,   17,   19,   17,   18,   20,
	   20,   21,   20,   18,   21,   21,   22,   23,   23,   25,   26,   27,
	   28,   29,   21,   24,   18,   31,   32,   30,   33,   33,   34,   35,
	   33,    5,   36,   36,   12,   13,   36,    5,   12,   12,   11,   13,
	   12,    5,   30,   30,   30,   30,   30,   30,   30,   37,   18,   38,
	   38,   38,   18,   39,   39,   39,   18,   40,   40,   40,   18,   30,
	   30,   30,   18,   41,   18,   42,   42,   43,   44,   45,   45,   46,
	   42,   41,    5,   42,   42,   43,   45,   45,   46,   42,   44,    5,
	   47,   47,   48,   18,   48,   18,   42,   42,   43,   46,   42,   48,
	    5,   49,   18,   50,   18,   51,   18,   52,   18,   53,   53,   54,
	   55,   53,    5,   56,   18,   57,   18,   58,   18,   59,   59,   60,
	   61,   59,    5,   62,   18,   63,   18,   64,   18,   65,   65,   66,
	   67,   65,    5,   14,   14,   14,   14,   14,   14,   14,   68,   18,
	   69,   69,   69,   18,   70,   70,   70,   18,   71,   71,   71,   18,
	   14,   14,   14,   18,   72,   72,   73,   75,   77,   78,   79,   80,
	   81,   82,   72,   74,   76,    5,   84,   85,   83,   86,   86,   87,
	   88,   86,    5,   89,   89,   75,   78,   89,    5,   75,   75,   73,
	   74,   74,   77,   78,   79,   80,   81,   82,   75,   76,    5,   90,
	   18,   91,   91,   92,   93,   94,   95,   94,   91,   90,    5,   91,
	   91,   92,   94,   95,   94,   91,   93,    5,   96,   96,   97,   18,
	   97,   18,   91,   91,   92,   95,   91,   97,    5,   98,   18,   99,
	   18,  100,   18,  101,   18,  102,  102,  103,  104,  102,    5,  105,
	   18,  106,   18,  107,   18,  108,  108,  109,  110,  108,    5,  111,
	   18,  112,   18,  113,   18,  114,  114,  115,  116,  114,    5,   83,
	   83,   83,   83,   83,   83,   83,  117,   18,  118,  118,  118,   18,
	  119,  119,  119,   18,  120,  120,  120,   18,   83,   83,   83,   18,
	    1,    2,    3,    0,  122,  123,  124,  121,    5,    5,    0
	};
}

private static final byte _json_indicies[] = init__json_indicies_0();


private static byte[] init__json_trans_targs_0()
{
	return new byte [] {
	    1,    2,   76,   76,    3,    0,    4,    5,    6,    7,    8,    9,
	   16,   77,    9,   10,   41,   11,    0,   12,   11,   12,   13,   22,
	   23,   15,   28,   33,   37,   15,   13,   14,   17,   15,   16,   77,
	   15,   18,   19,   20,   21,   23,   15,   16,   24,   25,   77,   26,
	   27,   29,   30,   31,   32,   15,   16,   77,   34,   35,   36,   15,
	   16,   77,   38,   39,   40,   15,   16,   77,   42,   43,   44,   45,
	   46,   47,   51,   50,   52,   49,   78,   57,   62,   66,   49,   47,
	   48,   70,   49,   50,   78,   49,   52,   49,   50,   53,   54,   78,
	   55,   56,   58,   59,   60,   61,   49,   50,   78,   63,   64,   65,
	   49,   50,   78,   67,   68,   69,   49,   50,   78,   71,   72,   73,
	   74,    1,    2,   76,   76
	};
}

private static final byte _json_trans_targs[] = init__json_trans_targs_0();


private static byte[] init__json_trans_actions_0()
{
	return new byte [] {
	    0,    0,   51,   48,    0,   25,    0,    0,    0,    0,    0,    7,
	    0,    3,    0,    0,    0,    9,    0,    9,    0,    0,    7,    7,
	    7,    5,    0,    0,    0,    1,    0,    0,    0,   11,   11,   27,
	    0,    0,    0,    0,    0,    0,   19,   19,    0,    0,   39,    0,
	    0,    0,    0,    0,    0,   17,   17,   36,    0,    0,    0,   13,
	   13,   30,    0,    0,    0,   15,   15,   33,    0,    0,    0,    0,
	    0,    7,    7,    0,    7,    5,    3,    0,    0,    0,    1,    0,
	    0,    0,   11,   11,   27,    0,    0,   19,   19,    0,    0,   39,
	    0,    0,    0,    0,    0,    0,   17,   17,   36,    0,    0,    0,
	   13,   13,   30,    0,    0,    0,   15,   15,   33,    0,    0,    0,
	    0,   21,   21,   58,   54
	};
}

private static final byte _json_trans_actions[] = init__json_trans_actions_0();


private static byte[] init__json_eof_actions_0()
{
	return new byte [] {
	    0,   45,   45,   45,   45,   45,   45,   45,   45,    0,    0,    0,
	    0,    0,   45,   45,   45,    0,    0,    0,    0,    0,    0,   45,
	   45,    0,    0,   45,    0,    0,    0,    0,   45,    0,    0,    0,
	   45,    0,    0,    0,   45,    0,    0,    0,    0,    0,   45,    0,
	   45,   45,   45,    0,   45,   45,    0,    0,   45,    0,    0,    0,
	    0,   45,    0,    0,    0,   45,    0,    0,    0,   45,    0,    0,
	    0,    0,    0,   23,   42,   23,   23
	};
}

private static final byte _json_eof_actions[] = init__json_eof_actions_0();


static final int json_start = 75;
static final int json_first_final = 75;
static final int json_error = 0;

static final int json_en_object = 8;
static final int json_en_array = 46;
static final int json_en_main = 75;


// line 209 "JsonParsingWriter.rl"

    void init()
    {
        //Flush the state
    	data.delete(0, p);
    	p = 0;
        names = new ArrayDeque<>();
        stack = new int[8];
    	root = null;
		current = null;
		elements = new ArrayDeque<>();
	    lastChild = new ArrayDeque<>();
        
// line 603 "JsonParsingWriter.java"
	{
	cs = json_start;
	top = 0;
	}

// line 222 "JsonParsingWriter.rl"
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

	protected void startArray (String name)
	{
		JsonValue value = new JsonValue(ValueType.array);
		if (current != null) addChild(name, value);
		elements.push(value);
		current = value;
	}

	protected void pop ()
	{
		root = elements.pop();
		if (current.size() > 0) lastChild.pop();
		current = elements.peek();
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