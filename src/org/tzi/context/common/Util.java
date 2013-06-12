/*
   Copyright 2007-2013 Hendrik Iben, University Bremen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.tzi.context.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Util {

	public static final Pattern whiteSpace = Pattern.compile("\\s+");
	public static final Pattern equalsign = Pattern.compile("=");
	public static final Pattern newLine = Pattern.compile("[\n\r\f]+");

	public static String [] splitWS(String s) {
		return whiteSpace.split(s);
	}

	public static String [] splitEQ(String s) {
		return equalsign.split(s);
	}

	public static String [] splitNL(String s) {
		return newLine.split(s);
	}
	
	public static Set<String> listToSet(String...strs) {
		Set<String> set = new TreeSet<String>();
		for(String str : strs)
			set.add(str);
		return set;
	}
	
	public static String concat(String [] strs, String delim, int offs, int len) {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<len; i++) {
			int io = offs + i;
			if(io>=strs.length)
				break;
			sb.append(strs[io]);
			if(delim!=null && i<len-1)
				sb.append(delim);
		}
		
		return sb.toString();
	}

	public static String concat(String [] strs, String delim, int offs) {
		return concat(strs, delim, offs, strs.length-offs);
	}

	public static String concat(String [] strs, String delim) {
		return concat(strs, delim, 0, strs.length);
	}
	
	public static String urlencode(String s) {
		try {
			if(s.length()==0)
				return "@";
			return URLEncoder.encode(s, Protocol.encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return s;
		}
	}
	
	public static String urldecode(String s) {
		try {
			if(s.length()==1 && s.charAt(0)=='@')
				return "";
			
			return URLDecoder.decode(s, Protocol.encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return s;
		}
	}
	
	public static int parseIntOr(String s, int def) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException nfe) {
			return def;
		}
	}

	public static long parseLongOr(String s, long def) {
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException nfe) {
			return def;
		}
	}
	
	public static boolean isPrefix(String s) {
		return s!=null && s.length()>0 && s.charAt(0) == Protocol.PREFIX_CHAR;
	}
	
	public static int getMessageOffset(String [] messageWords, int [] len) {
		if(messageWords.length < 1) {
			if(len != null && len.length>0)
				len[0] = 0;
			return 0;
		}
		
		int o = isPrefix(messageWords[0]) ? 1 : 0;
		
		if(len!=null && len.length>0)
			len[0] = messageWords.length - o;
		
		return o;
	}
	
	public static int getMessageLength(String [] messageWords, int [] offs) {
		int [] l = { 0 };
		int o = getMessageOffset(messageWords, l);
		if(offs!=null && offs.length>0)
			offs[0] = o;
		return l[0];
	}
	
	public static boolean hasPrefix(String [] messageWords) {
		return messageWords.length > 0 && isPrefix(messageWords[0]);
	}
	
	public static Integer parseIntReply(String s) {
		String [] words = splitWS(s);
		int [] _len = { 0 };
		int offs = getMessageOffset(words, _len);
		int len = _len[0];
		if(len != 2 || !words[offs].equalsIgnoreCase(Protocol.REPLY)) {
			return null;
		}
		
		try {
			return Integer.parseInt(words[offs+1]);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}
	
	public static Integer parseIntReplyA(String [] words) {
		int [] _len = { 0 };
		int offs = getMessageOffset(words, _len);
		int len = _len[0];

		if(len != 2 || !words[offs].equalsIgnoreCase(Protocol.REPLY)) {
			return null;
		}
		
		try {
			return Integer.parseInt(words[offs+1]);
		} catch(NumberFormatException nfe) {
			return null;
		}
	}
	
	public static boolean isFailReply(String s) {
		String [] words = splitWS(s);
		int [] _len = { 0 };
		int offs = getMessageOffset(words, _len);
		int len = _len[0];

		return len > 0 && Protocol.FAIL.equalsIgnoreCase(words[offs]);
	}
	
	public static String getPrefix(String [] messageWords) {
		if(hasPrefix(messageWords))
			return messageWords[0].substring(1);
		
		return null;
	}
	
	public static String [] stripPrefix(String [] messageWords) {
		int [] _len = { 0 };
		int offs = getMessageOffset(messageWords, _len);
		int len = _len[0];
		if(offs == 0)
			return messageWords;
		
		String [] w = new String [len];
		for(int i=0; i<len; i++)
			w[i] = messageWords[i+offs];
		
		return w;
	}
	
	public static String [] splitPrefixFromMessage(String [] messageWords, String [] prefixHolder) {
		int [] _len = { 0 };
		int offs = getMessageOffset(messageWords, _len);
		int len = _len[0];
		if(offs == 0) {
			if(prefixHolder != null && prefixHolder.length>0)
				prefixHolder[0] = null;
			
			return messageWords;
		}
		
		String [] w = new String [len];
		for(int i=0; i<len; i++)
			w[i] = messageWords[i+offs];
		
		if(prefixHolder != null && prefixHolder.length>0)
			prefixHolder[0] = messageWords[0].substring(1);
		
		return w;
	}
	
	public static boolean startsWithIgnoreCase(String s, String prefix) {
		return s.toLowerCase().startsWith(prefix.toLowerCase());
	}
	
	public static final byte [] base64Table = {
		  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'
		, 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'
		, 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X'
		, 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f'
		, 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n'
		, 'o', 'p', 'q', 'r', 's', 't', 'u', 'v'
		, 'w', 'x', 'y', 'z', '0', '1', '2', '3'
		, '4', '5', '6', '7', '8', '9', '+', '/' 
	};
	
	public static final boolean isBase64Char(byte c) {
		if(c >= 'A' && c <= 'Z')
			return true;
		if(c >= 'a' && c <= 'z')
			return true;
		if(c >= '0' && c <= '9')
			return true;
		if(c == '+')
			return true;
		if(c == '/')
			return true;
		if(c == '=')
			return true;
		
		return false;
	}
	
	public static int base64CharToValue(byte c) {
		if(c >= 'A' && c <= 'Z') {
			return c - 'A';
		}
		if(c >= 'a' && c <= 'z') {
			return 26 + (c - 'a');
		}
		if(c >= '0' && c <= '9') {
			return 52 + (c - '0');
		}
		if(c == '+')
			return 62;
		if(c == '/')
			return 63;
		if(c == '=')
			return 0;
		
		return -1;
	}
	
	public static final int encodeBase64(InputStream is, OutputStream os) throws IOException {
		int written = 0;
		int avail;
		byte [] buffer = { '=', '=', '=', '=' };
		int i;
		int value = 0;
		while((avail = is.available())>0) {
			if(avail>3)
				avail = 3;
			value = 0;
			for(i = 0; i<avail; i++) { 
				value |= (is.read() << (8 * (2-i)));
			}
			if(avail<3) {
				if(avail < 3) {
					buffer[3] = '=';
				}
				if(avail < 2) {
					buffer[2] = '=';
				}
			}
			buffer[0] = base64Table[ (value >> (6*3)) & 0x3F ];
			buffer[1] = base64Table[ (value >> (6*2)) & 0x3F ];
			if(avail>1) {
				buffer[2] = base64Table[ (value >> 6) & 0x3F ];
			}
			if(avail>2) {
				buffer[3] = base64Table[ value & 0x3F ];
			}
			
			os.write(buffer);
			written+=4;
		}
		return written;
	}
	
	public static final int decodeBase64(InputStream is, OutputStream os) throws IOException {
		int written = 0;
		byte [] buffer = { '=', '=', '=', '=' };
		int bufferIndex = 0;
		byte [] tmpBuffer = {0, 0, 0, 0};
		int r, i, outWrite, value;
		byte [] outBuffer = {0, 0, 0};
		while(is.available()>0) {
			r = is.read(tmpBuffer);
			if(r > 0) {
				for(i = 0; i<r; i++) {
					if(Character.isISOControl(tmpBuffer[i]))
						continue;
					if(!isBase64Char(tmpBuffer[i]))
						throw new IOException("Invalid data in Base64 stream!");
					buffer[bufferIndex++] = tmpBuffer[i];
					
					if(bufferIndex == 4) {
						value =  base64CharToValue(buffer[0]) << 18;
						value |= base64CharToValue(buffer[1]) << 12;
						value |= base64CharToValue(buffer[2]) << 6;
						value |= base64CharToValue(buffer[3]);
						
						outBuffer[0] = (byte)((value >> 16) & 0xFF);
						outBuffer[1] = (byte)((value >> 8) & 0xFF);
						outBuffer[2] = (byte)(value & 0xFF);
						
						outWrite = 3;
						if(buffer[3]=='=')
							outWrite--;
						if(buffer[2]=='=')
							outWrite--;
						
						os.write(outBuffer, 0, outWrite);
						written += outWrite;
						
						bufferIndex = 0;
					}
				}
			}
		}
		
		return written;
	}
}
