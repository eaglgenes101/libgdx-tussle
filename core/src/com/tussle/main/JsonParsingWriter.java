
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
        
// line 86 "JsonParsingWriter.java"
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
// line 95 "JsonParsingWriter.rl"
	{
    	        startObject(names.poll());
	    		{
        		if (top == stack.length) {
            		int[] newStack = new int[stack.length * 2];
    				System.arraycopy(stack, 0, newStack, 0, stack.length);
        			stack = newStack;
        	    }
            {stack[top++] = cs; cs = 12; _goto_targ = 2; if (true) continue _goto;}}
		    }
	break;
	case 1:
// line 99 "JsonParsingWriter.rl"
	{
            	pop();
	            {cs = stack[--top];_goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 2:
// line 103 "JsonParsingWriter.rl"
	{
        		startArray(names.poll());
	    	    {
        		if (top == stack.length) {
            		int[] newStack = new int[stack.length * 2];
    				System.arraycopy(stack, 0, newStack, 0, stack.length);
        			stack = newStack;
        	    }
            {stack[top++] = cs; cs = 70; _goto_targ = 2; if (true) continue _goto;}}
    	    }
	break;
	case 3:
// line 107 "JsonParsingWriter.rl"
	{ s = p; }
	break;
	case 4:
// line 108 "JsonParsingWriter.rl"
	{ addName(data.substring(s+1, p-1)); }
	break;
	case 5:
// line 109 "JsonParsingWriter.rl"
	{ addString(data.substring(s+1, p-1)); }
	break;
	case 6:
// line 110 "JsonParsingWriter.rl"
	{ addNull(); }
	break;
	case 7:
// line 111 "JsonParsingWriter.rl"
	{ addTrue(); }
	break;
	case 8:
// line 112 "JsonParsingWriter.rl"
	{ addFalse(); }
	break;
	case 9:
// line 114 "JsonParsingWriter.rl"
	{
	    	    try { addNumber(data.substring(s, p)); }
	    	    catch (NumberFormatException e)
	    	    {
		    	    //Empty the stack, output the errant string, move on
		    	    writ.write(data.substring(0, p));
		    	    init();
		    	    {p = (( 0))-1;}
		    	    {cs = 111; _goto_targ = 2; if (true) continue _goto;}
	    	    }
	    	}
	break;
	case 10:
// line 125 "JsonParsingWriter.rl"
	{
		    	completedValues.add(root);
    	        data.delete(0, p);
    	        {p = (( 0))-1;}
	            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
		    }
	break;
	case 12:
// line 134 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	init();
		    	{p = (( 0))-1;}
		    	{cs = 111; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
	case 13:
// line 141 "JsonParsingWriter.rl"
	{
		        if (p > 0)
		        {
		            writ.write(data.substring(0, p));
		            data.delete(0, p);
		            {p = (( 0))-1;}
		        }
		    }
	break;
// line 266 "JsonParsingWriter.java"
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
// line 125 "JsonParsingWriter.rl"
	{
		    	completedValues.add(root);
    	        data.delete(0, p);
    	        {p = (( 0))-1;}
	            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
		    }
	break;
	case 11:
// line 131 "JsonParsingWriter.rl"
	{
		    	{ p += 1; _goto_targ = 5; if (true)  continue _goto;}
		    }
	break;
	case 12:
// line 134 "JsonParsingWriter.rl"
	{
		    	//Append the errant string, unwind the stack, return to main
		    	writ.write(data.substring(0, p));
		    	init();
		    	{p = (( 0))-1;}
		    	{cs = 111; _goto_targ = 2; if (true) continue _goto;}
		    }
	break;
// line 312 "JsonParsingWriter.java"
		}
	}
	}

case 5:
	}
	break; }
	}

// line 169 "JsonParsingWriter.rl"


    }

	
// line 328 "JsonParsingWriter.java"
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
	    0,    0,    4,    6,   15,   21,   27,   33,   39,   44,   45,   47,
	   48,   56,   58,   64,   70,   72,   73,   75,   76,   91,   93,  100,
	  107,  114,  116,  117,  119,  120,  122,  123,  125,  126,  135,  141,
	  147,  153,  159,  161,  173,  184,  188,  190,  199,  201,  202,  204,
	  205,  206,  207,  208,  209,  216,  217,  218,  219,  226,  227,  228,
	  229,  236,  245,  251,  257,  263,  269,  271,  272,  274,  275,  292,
	  294,  301,  308,  324,  326,  338,  349,  351,  352,  354,  355,  359,
	  361,  370,  372,  373,  375,  376,  377,  378,  379,  380,  387,  388,
	  389,  390,  397,  398,  399,  400,  407,  416,  422,  428,  434,  440,
	  442,  443,  445,  446,  450,  454,  454
	};
}

private static final short _json_key_offsets[] = init__json_key_offsets_0();


private static char[] init__json_trans_keys_0()
{
	return new char [] {
	   34,   47,   91,  123,   34,   92,   34,   47,   92,   98,  102,  110,
	  114,  116,  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   34,   42,   47,   91,  123,   42,   42,   47,   10,
	   13,   32,   34,   44,   47,  125,    9,   10,   34,   92,   13,   32,
	   47,   58,    9,   10,   13,   32,   47,   58,    9,   10,   42,   47,
	   42,   42,   47,   10,   13,   32,   34,   43,   45,   47,   91,  102,
	  110,  116,  123,    9,   10,   48,   57,   34,   92,   13,   32,   44,
	   47,  125,    9,   10,   13,   32,   44,   47,  125,    9,   10,   13,
	   32,   34,   47,  125,    9,   10,   42,   47,   42,   42,   47,   10,
	   42,   47,   42,   42,   47,   10,   34,   47,   92,   98,  102,  110,
	  114,  116,  117,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,
	   70,   97,  102,   48,   57,   13,   32,   44,   46,   47,   69,  101,
	  125,    9,   10,   48,   57,   13,   32,   44,   47,   69,  101,  125,
	    9,   10,   48,   57,   43,   45,   48,   57,   48,   57,   13,   32,
	   44,   47,  125,    9,   10,   48,   57,   42,   47,   42,   42,   47,
	   10,   97,  108,  115,  101,   13,   32,   44,   47,  125,    9,   10,
	  117,  108,  108,   13,   32,   44,   47,  125,    9,   10,  114,  117,
	  101,   13,   32,   44,   47,  125,    9,   10,   34,   47,   92,   98,
	  102,  110,  114,  116,  117,   48,   57,   65,   70,   97,  102,   48,
	   57,   65,   70,   97,  102,   48,   57,   65,   70,   97,  102,   48,
	   57,   65,   70,   97,  102,   42,   47,   42,   42,   47,   10,   13,
	   32,   34,   44,   47,   91,   93,  102,  110,  116,  123,    9,   10,
	   43,   45,   48,   57,   34,   92,   13,   32,   44,   47,   93,    9,
	   10,   13,   32,   44,   47,   93,    9,   10,   13,   32,   34,   43,
	   45,   47,   91,   93,  102,  110,  116,  123,    9,   10,   48,   57,
	   48,   57,   13,   32,   44,   46,   47,   69,   93,  101,    9,   10,
	   48,   57,   13,   32,   44,   47,   69,   93,  101,    9,   10,   48,
	   57,   42,   47,   42,   42,   47,   10,   43,   45,   48,   57,   48,
	   57,   13,   32,   44,   47,   93,    9,   10,   48,   57,   42,   47,
	   42,   42,   47,   10,   97,  108,  115,  101,   13,   32,   44,   47,
	   93,    9,   10,  117,  108,  108,   13,   32,   44,   47,   93,    9,
	   10,  114,  117,  101,   13,   32,   44,   47,   93,    9,   10,   34,
	   47,   92,   98,  102,  110,  114,  116,  117,   48,   57,   65,   70,
	   97,  102,   48,   57,   65,   70,   97,  102,   48,   57,   65,   70,
	   97,  102,   48,   57,   65,   70,   97,  102,   42,   47,   42,   42,
	   47,   10,   34,   47,   91,  123,   34,   47,   91,  123,    0
	};
}

private static final char _json_trans_keys[] = init__json_trans_keys_0();


private static byte[] init__json_single_lengths_0()
{
	return new byte [] {
	    0,    4,    2,    9,    0,    0,    0,    0,    5,    1,    2,    1,
	    6,    2,    4,    4,    2,    1,    2,    1,   11,    2,    5,    5,
	    5,    2,    1,    2,    1,    2,    1,    2,    1,    9,    0,    0,
	    0,    0,    0,    8,    7,    2,    0,    5,    2,    1,    2,    1,
	    1,    1,    1,    1,    5,    1,    1,    1,    5,    1,    1,    1,
	    5,    9,    0,    0,    0,    0,    2,    1,    2,    1,   11,    2,
	    5,    5,   12,    0,    8,    7,    2,    1,    2,    1,    2,    0,
	    5,    2,    1,    2,    1,    1,    1,    1,    1,    5,    1,    1,
	    1,    5,    1,    1,    1,    5,    9,    0,    0,    0,    0,    2,
	    1,    2,    1,    4,    4,    0,    0
	};
}

private static final byte _json_single_lengths[] = init__json_single_lengths_0();


private static byte[] init__json_range_lengths_0()
{
	return new byte [] {
	    0,    0,    0,    0,    3,    3,    3,    3,    0,    0,    0,    0,
	    1,    0,    1,    1,    0,    0,    0,    0,    2,    0,    1,    1,
	    1,    0,    0,    0,    0,    0,    0,    0,    0,    0,    3,    3,
	    3,    3,    1,    2,    2,    1,    1,    2,    0,    0,    0,    0,
	    0,    0,    0,    0,    1,    0,    0,    0,    1,    0,    0,    0,
	    1,    0,    3,    3,    3,    3,    0,    0,    0,    0,    3,    0,
	    1,    1,    2,    1,    2,    2,    0,    0,    0,    0,    1,    1,
	    2,    0,    0,    0,    0,    0,    0,    0,    0,    1,    0,    0,
	    0,    1,    0,    0,    0,    1,    0,    3,    3,    3,    3,    0,
	    0,    0,    0,    0,    0,    0,    0
	};
}

private static final byte _json_range_lengths[] = init__json_range_lengths_0();


private static short[] init__json_index_offsets_0()
{
	return new short [] {
	    0,    0,    5,    8,   18,   22,   26,   30,   34,   40,   42,   45,
	   47,   55,   58,   64,   70,   73,   75,   78,   80,   94,   97,  104,
	  111,  118,  121,  123,  126,  128,  131,  133,  136,  138,  148,  152,
	  156,  160,  164,  166,  177,  187,  191,  193,  201,  204,  206,  209,
	  211,  213,  215,  217,  219,  226,  228,  230,  232,  239,  241,  243,
	  245,  252,  262,  266,  270,  274,  278,  281,  283,  286,  288,  303,
	  306,  313,  320,  335,  337,  348,  358,  361,  363,  366,  368,  372,
	  374,  382,  385,  387,  390,  392,  394,  396,  398,  400,  407,  409,
	  411,  413,  420,  422,  424,  426,  433,  443,  447,  451,  455,  459,
	  462,  464,  467,  469,  474,  479,  480
	};
}

private static final short _json_index_offsets[] = init__json_index_offsets_0();


private static byte[] init__json_trans_targs_0()
{
	return new byte [] {
	    2,    8,  112,  112,    1,    1,    3,    2,    2,    2,    2,    2,
	    2,    2,    2,    2,    4,    0,    5,    5,    5,    0,    6,    6,
	    6,    0,    7,    7,    7,    0,    2,    2,    2,    0,    2,    9,
	   11,  112,  112,    1,   10,    9,   10,    1,    9,    1,   11,   12,
	   12,   13,   24,   66,  113,   12,    0,   14,   61,   13,   15,   15,
	   16,   20,   15,    0,   15,   15,   16,   20,   15,    0,   17,   19,
	    0,   18,   17,   18,   15,   17,   15,   19,   20,   20,   21,   38,
	   38,   44,   23,   48,   53,   57,   23,   20,   39,    0,   22,   33,
	   21,   23,   23,   24,   29,  113,   23,    0,   23,   23,   24,   29,
	  113,   23,    0,   24,   24,   13,   25,  113,   24,    0,   26,   28,
	    0,   27,   26,   27,   24,   26,   24,   28,   30,   32,    0,   31,
	   30,   31,   23,   30,   23,   32,   21,   21,   21,   21,   21,   21,
	   21,   21,   34,    0,   35,   35,   35,    0,   36,   36,   36,    0,
	   37,   37,   37,    0,   21,   21,   21,    0,   39,    0,   23,   23,
	   24,   40,   29,   41,   41,  113,   23,   39,    0,   23,   23,   24,
	   29,   41,   41,  113,   23,   40,    0,   42,   42,   43,    0,   43,
	    0,   23,   23,   24,   29,  113,   23,   43,    0,   45,   47,    0,
	   46,   45,   46,   20,   45,   20,   47,   49,    0,   50,    0,   51,
	    0,   52,    0,   23,   23,   24,   29,  113,   23,    0,   54,    0,
	   55,    0,   56,    0,   23,   23,   24,   29,  113,   23,    0,   58,
	    0,   59,    0,   60,    0,   23,   23,   24,   29,  113,   23,    0,
	   13,   13,   13,   13,   13,   13,   13,   13,   62,    0,   63,   63,
	   63,    0,   64,   64,   64,    0,   65,   65,   65,    0,   13,   13,
	   13,    0,   67,   69,    0,   68,   67,   68,   12,   67,   12,   69,
	   70,   70,   71,   74,  107,   73,  114,   89,   94,   98,   73,   70,
	   75,   76,    0,   72,  102,   71,   73,   73,   74,   78,  114,   73,
	    0,   73,   73,   74,   78,  114,   73,    0,   74,   74,   71,   75,
	   75,   85,   73,  114,   89,   94,   98,   73,   74,   76,    0,   76,
	    0,   73,   73,   74,   77,   78,   82,  114,   82,   73,   76,    0,
	   73,   73,   74,   78,   82,  114,   82,   73,   77,    0,   79,   81,
	    0,   80,   79,   80,   73,   79,   73,   81,   83,   83,   84,    0,
	   84,    0,   73,   73,   74,   78,  114,   73,   84,    0,   86,   88,
	    0,   87,   86,   87,   74,   86,   74,   88,   90,    0,   91,    0,
	   92,    0,   93,    0,   73,   73,   74,   78,  114,   73,    0,   95,
	    0,   96,    0,   97,    0,   73,   73,   74,   78,  114,   73,    0,
	   99,    0,  100,    0,  101,    0,   73,   73,   74,   78,  114,   73,
	    0,   71,   71,   71,   71,   71,   71,   71,   71,  103,    0,  104,
	  104,  104,    0,  105,  105,  105,    0,  106,  106,  106,    0,   71,
	   71,   71,    0,  108,  110,    0,  109,  108,  109,   70,  108,   70,
	  110,    2,    8,  112,  112,    1,    2,    8,  112,  112,    1,    0,
	    0,    0
	};
}

private static final byte _json_trans_targs[] = init__json_trans_targs_0();


private static byte[] init__json_trans_actions_0()
{
	return new byte [] {
	    0,    0,   51,   48,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,   25,    0,    0,    0,   25,    0,    0,
	    0,   25,    0,    0,    0,   25,    0,    0,    0,   25,    0,    0,
	    0,   51,   48,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    7,    0,    0,    3,    0,   25,    0,    0,    0,    9,    9,
	    9,    9,    9,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    7,    7,
	    7,    0,    5,    0,    0,    0,    1,    0,    7,    0,    0,    0,
	    0,   11,   11,   11,   11,   27,   11,   25,    0,    0,    0,    0,
	    3,    0,   25,    0,    0,    7,    0,    3,    0,   25,    0,    0,
	   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,   19,   19,
	   19,    0,   19,    0,    0,   39,   19,    0,   25,   19,   19,   19,
	   19,    0,    0,   39,   19,    0,   25,    0,    0,    0,    0,    0,
	    0,   19,   19,   19,   19,   39,   19,    0,   25,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   17,   17,   17,   17,   36,   17,   25,    0,    0,
	    0,    0,    0,    0,   13,   13,   13,   13,   30,   13,   25,    0,
	    0,    0,    0,    0,    0,   15,   15,   15,   15,   33,   15,   25,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    7,    0,    0,    5,    3,    0,    0,    0,    1,    0,
	    7,    7,   25,    0,    0,    0,   11,   11,   11,   11,   27,   11,
	   25,    0,    0,    0,    0,    3,    0,   25,    0,    0,    7,    7,
	    7,    0,    5,    3,    0,    0,    0,    1,    0,    7,   25,    0,
	    0,   19,   19,   19,    0,   19,    0,   39,    0,   19,    0,   25,
	   19,   19,   19,   19,    0,   39,    0,   19,    0,   25,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,   19,   19,   19,   19,   39,   19,    0,   25,    0,    0,
	   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,   17,   17,   17,   17,   36,   17,   25,    0,
	    0,    0,    0,    0,    0,   13,   13,   13,   13,   30,   13,   25,
	    0,    0,    0,    0,    0,    0,   15,   15,   15,   15,   33,   15,
	   25,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   51,   48,    0,   21,   21,   58,   54,   21,   25,
	   25,    0
	};
}

private static final byte _json_trans_actions[] = init__json_trans_actions_0();


private static byte[] init__json_eof_actions_0()
{
	return new byte [] {
	    0,   45,   45,   45,   45,   45,   45,   45,   45,   45,   45,   45,
	   45,    0,    0,    0,    0,    0,    0,    0,    0,    0,   45,   45,
	   45,   45,   45,   45,   45,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   45,   45,    0,    0,   45,    0,    0,    0,    0,
	    0,    0,    0,    0,   45,    0,    0,    0,   45,    0,    0,    0,
	   45,    0,    0,    0,    0,    0,    0,    0,    0,    0,   45,    0,
	   45,   45,   45,    0,   45,   45,    0,    0,    0,    0,    0,    0,
	   45,   45,   45,   45,   45,    0,    0,    0,    0,   45,    0,    0,
	    0,   45,    0,    0,    0,   45,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   23,   42,   23,   23
	};
}

private static final byte _json_eof_actions[] = init__json_eof_actions_0();


static final int json_start = 111;
static final int json_first_final = 111;
static final int json_error = 0;

static final int json_en_object = 12;
static final int json_en_array = 70;
static final int json_en_main = 111;


// line 174 "JsonParsingWriter.rl"

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
        
// line 609 "JsonParsingWriter.java"
	{
	cs = json_start;
	top = 0;
	}

// line 187 "JsonParsingWriter.rl"
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