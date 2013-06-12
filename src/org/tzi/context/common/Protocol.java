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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.text.Collator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Protocol {
	public static final int standardPort = 2009;

	public static final int maxDataSize = 1024*16;
	
	// space to conserve for transfer header TX messages
	public static final int transferMargin = "TX 2147483648 0 2147483648 ".length();
	public static final int txMaxData = maxDataSize - transferMargin;
	
	public static final long heartbeat = 20000;
	public static final long timeout = 60000;
	
	// 15 minutes of suspend time before removing everything 
	public static final long limboTime = 15 * 60 * 1000;
	
	public static final String encoding = "UTF-8";
	public static final String serverVersion = "1.1";
	public static final String HELLO = "SiWearContextServer v" + serverVersion;
	public static final String DROP ="DROP";
	public static final String FAIL ="FAIL";
	public static final String OK ="OK";
	public static final String CTX = "CTX";
	public static final String NEWSRC = "NEWSRC";
	public static final String NEWPRP = "NEWPRP";
	public static final String REMSRC = "REMSRC";
	public static final String REMPRP = "REMPRP";
	public static final String REPLY ="REPLY";
	public static final String PING ="PING";
	public static final String PONG ="PONG";
	public static final String LOGIN ="LOGIN";
	public static final String RELOGIN ="RELOGIN";
	public static final String ACCEPT ="ACCEPT";
	public static final String LOGOUT ="LOGOUT";
	public static final String CREATECTX = "CREATECTX";
	public static final String CREATESRC = "CREATESRC";
	public static final String CREATEPRP = "CREATEPRP";
	public static final String DELETECTX = "DELETECTX";
	public static final String DELETEPRP = "DELETEPRP";
	public static final String DELETESRC = "DELETESRC";
	public static final String LISTCLT = "LISTCLT";
	public static final String LISTCTX = "LISTCTX";
	public static final String LISTSRC = "LISTSRC";
	public static final String LISTPRP = "LISTPRP";
	public static final String SETPRP = "SETPRP";
	public static final String GETPRP = "GETPRP";
	public static final String GETCTXID = "GETCTXID";
	public static final String GETPRPID = "GETPRPID";
	public static final String GETSRCID = "GETSRCID";
	public static final String SUBSCRIBE = "SUBSCRIBE";
	public static final String SUBSCRIPT = "SUBSCRIPT";
	public static final String LISTSUB = "LISTSUB";
	public static final String CANCELSUB = "CANCELSUB";
	public static final String SHORTSUB = "SHORTSUB";
	public static final String LISTIDS = "LISTIDS";
	public static final String HISTORY = "HISTORY";
	public static final String TXPACKET = "TX";
	public static final String TXCTX = "TXCTX";
	public static final String TXACK = "TXACK";
	public static final String TXCANCEL = "TXCANCEL";
	public static final String TXRESEND = "TXRESEND";
	public static final String STARTTIME = "STARTTIME";
	public static final String SCTX = "SCTX";
	public static final String GETIDINFO = "GETIDINFO";
	public static final String SHUTDOWN = "SHUTDOWN";
	
	public static final char PREFIX_CHAR = '#';
	
	public static enum Command {
		PING,
		PONG,
		LOGIN,
		RELOGIN,
		ACCEPT,
		LOGOUT,
		CREATECTX,
		CREATESRC,
		CREATEPRP,
		DELETECTX,
		DELETEPRP,
		DELETESRC,
		LISTCTX,
		LISTSRC,
		LISTPRP,
		LISTCLT,
		SETPRP,
		GETPRP,
		GETCTXID,
		GETPRPID,
		GETSRCID,
		SUBSCRIBE,
		SUBSCRIPT,
		LISTSUB,
		CANCELSUB,
		SHORTSUB,
		LISTIDS,
		HISTORY,
		TXPACKET,
		TXACK,
		TXCANCEL,
		TXRESEND,
		STARTTIME,
		GETIDINFO,
		SHUTDOWN,
		INVALID
	};
	
	public static Map<String, Command> cmdMap;
	
	static {
		Collator c = Collator.getInstance();
		c.setStrength(Collator.PRIMARY);
		cmdMap = new TreeMap<String, Command>(c);
		cmdMap.put(PING, Command.PING);
		cmdMap.put(PONG, Command.PONG);
		cmdMap.put(LOGIN, Command.LOGIN);
		cmdMap.put(RELOGIN, Command.RELOGIN);
		cmdMap.put(ACCEPT, Command.ACCEPT);
		cmdMap.put(LOGOUT, Command.LOGOUT);
		cmdMap.put(CREATECTX, Command.CREATECTX);
		cmdMap.put(CREATESRC, Command.CREATESRC);
		cmdMap.put(CREATEPRP, Command.CREATEPRP);
		cmdMap.put(DELETECTX, Command.DELETECTX);
		cmdMap.put(DELETEPRP, Command.DELETEPRP);
		cmdMap.put(DELETESRC, Command.DELETESRC);
		cmdMap.put(LISTCTX, Command.LISTCTX);
		cmdMap.put(LISTSRC, Command.LISTSRC);
		cmdMap.put(LISTPRP, Command.LISTPRP);
		cmdMap.put(LISTCLT, Command.LISTCLT);
		cmdMap.put(SETPRP, Command.SETPRP);
		cmdMap.put(GETPRP, Command.GETPRP);
		cmdMap.put(GETCTXID, Command.GETCTXID);
		cmdMap.put(GETPRPID, Command.GETPRPID);
		cmdMap.put(GETSRCID, Command.GETSRCID);
		cmdMap.put(SUBSCRIBE, Command.SUBSCRIBE);
		cmdMap.put(SUBSCRIPT, Command.SUBSCRIPT);
		cmdMap.put(LISTSUB, Command.LISTSUB);
		cmdMap.put(CANCELSUB, Command.CANCELSUB);
		cmdMap.put(SHORTSUB, Command.SHORTSUB);
		cmdMap.put(LISTIDS, Command.LISTIDS);
		cmdMap.put(HISTORY, Command.HISTORY);
		cmdMap.put(TXPACKET, Command.TXPACKET);
		cmdMap.put(TXACK, Command.TXACK);
		cmdMap.put(TXCANCEL, Command.TXCANCEL);
		cmdMap.put(TXRESEND, Command.TXRESEND);
		cmdMap.put(STARTTIME, Command.STARTTIME);
		cmdMap.put(GETIDINFO, Command.GETIDINFO);
		cmdMap.put(SHUTDOWN, Command.SHUTDOWN);
	}
	
	public static Command getCommand(String cmdS) {
		Command cmd = cmdMap.get(cmdS);
		return (cmd==null)?Command.INVALID:cmd;
	}
	
	public static Charset protocolCharset = Charset.forName(Protocol.encoding);
	public static Charset asciiCharset = Charset.forName("ASCII");
	
	public static byte [] encodeString(String str) {
		ByteBuffer bb = protocolCharset.encode(str);
		byte [] bytes = new byte [bb.limit()];
		bb.get(bytes);
		
		return bytes;
	}
	
	public static int getPacketData(byte [] data, int n, byte [] buffer, int offs) {
		int data_offs = Protocol.txMaxData * n;
		if(data_offs >= data.length)
			return 0;
		
		int len = Protocol.txMaxData;
		if((data_offs + len) > data.length) {
			len = data.length - data_offs;
		}
		
		System.arraycopy(data, data_offs, buffer, offs, len);
		
		return len;
	}

	public static String decodeString(byte [] bytes, int offs, int len) {
		ByteBuffer bb = ByteBuffer.wrap(bytes, offs, len);
		return protocolCharset.decode(bb).toString();
	}
	
	public static final byte [] newLineBytes = "\n".getBytes(protocolCharset);
	
	public static boolean endsWithNewline(byte [] data, int size) {
		if(size < newLineBytes.length)
			return false;
		int index = size - newLineBytes.length;
		for(int i=0; i<newLineBytes.length; i++) {
			if(data[index++]!=newLineBytes[i])
				return false;
		}
		return true;
	}
	
	public static int lastNewLine(byte [] data, int size) {
		if(size < newLineBytes.length)
			return -1;

		int index = size - newLineBytes.length;
		
		while(index>=0) {
			boolean found = true;
			for(int i=0; i<newLineBytes.length; i++) {
				if(data[index+i] != newLineBytes[i]) {
					found = false;
					break;
				}
			}
			if(found)
				return index;
			
			index--;
		}
		
		return -1;
	}
	
	public static WriteMessageResult writeMessage(OutputStream os, String message) throws IOException {
		return writeMessage(os, message, false, null);
	}
	
	public static WriteMessageResult writeMessageC(ByteChannel c, String message) throws IOException {
		return writeMessageC(c, message, false, null);
	}
	
	public static class WriteMessageResult {
		private byte [] packetBytes;
		private int transferId;
		private boolean isTransfer;
		
		public WriteMessageResult() {
			isTransfer = false;
		}

		public WriteMessageResult(int transferId, byte [] packetBytes) {
			this.transferId = transferId;
			this.packetBytes = packetBytes;
			isTransfer = true;
		}
		
		public boolean isTransfer() {
			return isTransfer;
		}
		
		public int getTransferId() {
			return transferId;
		}
		
		public byte [] getPacketBytes() {
			return packetBytes;
		}
	}

	public static WriteMessageResult writeMessage(OutputStream os, String message, boolean isContext, UniqueIdProvider uid) throws IOException {
		final int reallyLarge = ((Protocol.maxDataSize>>1) + (Protocol.maxDataSize>>2));
		byte [] transferData = null;
		int transferId = -1;
		byte [] packetBytes = null;
		String txHeader = null;
		if(message.length() < reallyLarge || message.startsWith("TX")) {
			transferData = encodeString(message+"\n");
		}
		
		if(transferData == null || transferData.length > Protocol.maxDataSize) {
			if(message.startsWith("TX")) {
				throw new RuntimeException("Attempt to TX a TX!!! " + (message.length() > 100 ? message.substring(0, 100) : message) + " len = " + transferData.length + ", max = " + Protocol.maxDataSize);
			}
			
			transferId = uid.getUniqueId();
			
			packetBytes = Util.urlencode(message).getBytes(asciiCharset);
			transferData = new byte [Protocol.txMaxData+newLineBytes.length];
			int firstLen = Protocol.getPacketData(packetBytes, 0, transferData, 0);
			if(firstLen < Protocol.txMaxData) {
				byte [] tmp = transferData;
				transferData = new byte [tmp.length+newLineBytes.length];
				System.arraycopy(tmp, 0, transferData, 0, firstLen);
			}
			for(int i=0; i<newLineBytes.length; i++)
				transferData[firstLen+i] = newLineBytes[i];
			
			txHeader = String.format("%s %d 0 %d ", isContext ? Protocol.TXCTX : Protocol.TXPACKET, transferId, packetBytes.length);
		}
		
		if(txHeader != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(encodeString(txHeader));
			baos.write(transferData);
			os.write(baos.toByteArray()); // needs to be written in one call
		} else {
			os.write(transferData);
		}
		
		if(packetBytes == null)
			return new WriteMessageResult();
		
		return new WriteMessageResult(transferId, packetBytes);
	}
	
	public static WriteMessageResult writeMessageC(ByteChannel c, String message, boolean isContext, UniqueIdProvider uid) throws IOException {
		final int reallyLarge = ((Protocol.maxDataSize>>1) + (Protocol.maxDataSize>>2));
		byte [] transferData = null;
		int transferId = -1;
		byte [] packetBytes = null;
		String txHeader = null;
		if(message.length() < reallyLarge || message.startsWith("TX")) {
			transferData = encodeString(message+"\n");
		}
		
		if(transferData == null || transferData.length > Protocol.maxDataSize) {
			if(message.startsWith("TX")) {
				throw new RuntimeException("Attempt to TX a TX!!! " + (message.length() > 100 ? message.substring(0, 100) : message) + " len = " + transferData.length + ", max = " + Protocol.maxDataSize);
			}
			
			transferId = uid.getUniqueId();
			
			packetBytes = Util.urlencode(message).getBytes(asciiCharset);
			transferData = new byte [Protocol.txMaxData+newLineBytes.length];
			int firstLen = Protocol.getPacketData(packetBytes, 0, transferData, 0);
			if(firstLen < Protocol.txMaxData) {
				byte [] tmp = transferData;
				transferData = new byte [tmp.length+newLineBytes.length];
				System.arraycopy(tmp, 0, transferData, 0, firstLen);
			}
			for(int i=0; i<newLineBytes.length; i++)
				transferData[firstLen+i] = newLineBytes[i];
			
			txHeader = String.format("%s %d 0 %d ", isContext ? Protocol.TXCTX : Protocol.TXPACKET, transferId, packetBytes.length);
		}
		
		if(txHeader != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(encodeString(txHeader));
			baos.write(transferData);
			ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
			bb.rewind();
			writeByteChannel(c, bb);
		} else {
			ByteBuffer bb = ByteBuffer.wrap(transferData);
			bb.rewind();
			writeByteChannel(c, bb);
		}
		
		if(packetBytes == null)
			return new WriteMessageResult();
		
		return new WriteMessageResult(transferId, packetBytes);
	}
	
	private static void writeByteChannel(ByteChannel c, ByteBuffer bb) throws IOException {
		int l = bb.limit();
		int written = 0;
		while(written<l) {
			bb.position(written);
			written += c.write(bb);
		}
	}

	
	public static String safeSubstring(String s, int b, int e) {
		return s.substring(b>=s.length()?s.length()-1:b, e>s.length()?s.length():e); 
	}
	
	public static boolean isServerInitiatedMessage(String msg) {
		if(msg.startsWith(CTX))
			return true;
		if(msg.startsWith(NEWSRC))
			return true;
		if(msg.startsWith(NEWPRP))
			return true;
		if(msg.startsWith(PING))
			return true;
		if(msg.startsWith(DROP))
			return true;
		if(msg.startsWith(SCTX))
			return true;
		
		return false;
	}
	
	public static Map<Integer, String> parseCTXList(String ctxlist) {
		String [] words = Util.splitWS(ctxlist);
		words = Util.stripPrefix(words);
		if(words.length>1 && Protocol.REPLY.equalsIgnoreCase(words[0])) {
			Map<Integer, String> ctxMap = new TreeMap<Integer, String>();
			try {
				int numctx = Integer.parseInt(words[1]);
				 
				if(numctx>(words.length-2))
					return null;
				
				for(int i=0; i<numctx; i++) {
					String [] idname = Util.splitEQ(words[2+i]);
					if(idname.length==2) {
						try {
							int ctxId = Integer.parseInt(idname[0]);
							ctxMap.put(ctxId, Util.urldecode(idname[1]));
						} catch(NumberFormatException nfe) {
							
						}
					}
				}
			} catch(NumberFormatException nfe) {
				
			}
			
			return ctxMap;
		}
		return null;
	}
	
	public static Map<Integer, Map<Integer, String>> parseSRCList(String srclist) {
		String [] words = Util.splitWS(srclist);
		words = Util.stripPrefix(words);
		if(words.length>1 && Protocol.REPLY.equalsIgnoreCase(words[0])) {
			try {
				int windex = 1;
				int numctx = Util.parseIntOr(words[windex++], -1);
				Map<Integer, Map<Integer, String>> ctxMap = new TreeMap<Integer, Map<Integer,String>>();
				for(int cidx=0; cidx<numctx; cidx++) {
					Map<Integer, String> srcMap = new TreeMap<Integer, String>();
					int ctxid = Util.parseIntOr(words[windex++], -1);
					if(ctxid==-1)
						return null;
					int numsrc = Util.parseIntOr(words[windex++], -1);
					if(numsrc==-1)
						return null;
					for(int sidx=0; sidx<numsrc; sidx++) {
						String [] sidname = Util.splitEQ(words[windex++]);
						if(sidname.length!=2)
							return null;
						boolean isMerged = true;
						if(sidname[0].endsWith("*")) {
							isMerged = false;
							sidname[0] = sidname[0].substring(0, sidname[0].length()-1);
						}
						int srcid = Util.parseIntOr(sidname[0],-1);
						if(srcid==-1)
							return null;
						srcMap.put(srcid, Util.urldecode(sidname[1])+(isMerged?"":"*"));
					}
					if(!srcMap.isEmpty())
						ctxMap.put(ctxid, srcMap);
				}
				return ctxMap;
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				return null;
			}
		}
		return null;
	}
	
	public static  Map<Integer, Map<Integer, Map<Integer, String>>> parsePRPList(String prplist) {
		String [] words = Util.splitWS(prplist);
		words = Util.stripPrefix(words);
		if(words.length>1 && Protocol.REPLY.equalsIgnoreCase(words[0])) {
			try {
				int windex = 1;
				// get number of contexts in reply
				int numctx = Util.parseIntOr(words[windex++], -1);
				Map<Integer, Map<Integer, Map<Integer, String>>> ctxMap = new TreeMap<Integer, Map<Integer,Map<Integer,String>>>();

				// for each context
				for(int cidx=0; cidx<numctx; cidx++) {
					Map<Integer, Map<Integer, String>> srcMap = new TreeMap<Integer, Map<Integer, String>>();
					// get context id
					int ctxid = Util.parseIntOr(words[windex++], -1);
					if(ctxid==-1)
						return null;
					int numsrc = Util.parseIntOr(words[windex++], -1);
					if(numsrc==-1)
						return null;
					for(int sidx=0; sidx<numsrc; sidx++) {
						Map<Integer, String> prpMap = new TreeMap<Integer, String>();
						int srcid = Util.parseIntOr(words[windex++], -1);
						if(srcid==-1)
							return null;
						int numprp = Util.parseIntOr(words[windex++], -1);
						if(numprp==-1)
							return null;
						for(int prpidx=0; prpidx<numprp; prpidx++) {
							String [] prpidname = Util.splitEQ(words[windex++]);
							if(prpidname.length!=2)
								return null;
							boolean isMerged = true;
							if(prpidname[0].endsWith("*")) {
								isMerged = false;
								prpidname[0] = prpidname[0].substring(0, prpidname[0].length()-1);
							}
							int prpid = Util.parseIntOr(prpidname[0], -1);
							if(prpid==-1)
								return null;
							prpMap.put(prpid, Util.urldecode(prpidname[1]) + (isMerged?"":"*"));
						}
						if(!prpMap.isEmpty())
							srcMap.put(srcid, prpMap);
					}
					
					if(!srcMap.isEmpty())
						ctxMap.put(ctxid, srcMap);
				}
				return ctxMap;
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				return null;
			}
		}		
		return null;
	}
	
	public static ContextElement parseProperty(String srcName, String prpName, String prpstring) {
		String [] words = Util.splitWS(prpstring);
		words = Util.stripPrefix(words);
		if(words.length>1 && Protocol.REPLY.equalsIgnoreCase(words[0])) {
			try {
				int windex=1;
				int status = Util.parseIntOr(words[windex++], -2);
				if(status==-2)
					return null;
				
				if(status==-1) {
					return new ContextElement(srcName, prpName, "", -1L, false, (Set<String>)null);
				}
				
				String value = "";
				
				if(status==1) {
					value = Util.urldecode(words[windex++]);
				}
				
				long timestamp = Util.parseLongOr(words[windex++], -1);
				
				if(timestamp==-1)
					return null;
				
				int numtags = Util.parseIntOr(words[windex++], -1);
				
				if(numtags==-1)
					return null;
				
				Set<String> tagset = new TreeSet<String>();
				for(int ti=0; ti<numtags; ti++) {
					tagset.add(Util.urldecode(words[windex++]));
				}
				
				int p = Util.parseIntOr(words[windex++], -1);
				
				if(p==-1)
					return null;
				
				return new ContextElement(srcName, prpName, value, timestamp, p==1, tagset);
				
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				return null;
			}
		}
		return null;
	}
	
	public static List<ContextElement> parseHistory(String srcName, String prpName, String histstring) {
		String [] words = Util.splitWS(histstring);
		words = Util.stripPrefix(words);
		if(words.length>1 && Protocol.REPLY.equalsIgnoreCase(words[0])) {
			LinkedList<ContextElement> celist = new LinkedList<ContextElement>();

			int count = words.length - 2;

			try {
				if(Integer.parseInt(words[1]) != count) {
					return null;
				}
			}catch(NumberFormatException nfe) {
				return null;
			}

			for(int i=2; i<words.length; i++) {
				String ceS = Util.urldecode(words[i]);
				ContextElement ce = ContextElement.fromShortString(srcName, prpName, ceS);
				celist.add(ce);
			}

			return celist;
		}
		return null;
	}
}
