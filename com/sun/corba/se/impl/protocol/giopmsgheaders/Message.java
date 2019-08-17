package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public interface Message {
  public static final int defaultBufferSize = 1024;
  
  public static final int GIOPBigEndian = 0;
  
  public static final int GIOPLittleEndian = 1;
  
  public static final int GIOPBigMagic = 1195986768;
  
  public static final int GIOPLittleMagic = 1347373383;
  
  public static final int GIOPMessageHeaderLength = 12;
  
  public static final byte LITTLE_ENDIAN_BIT = 1;
  
  public static final byte MORE_FRAGMENTS_BIT = 2;
  
  public static final byte FLAG_NO_FRAG_BIG_ENDIAN = 0;
  
  public static final byte TRAILING_TWO_BIT_BYTE_MASK = 3;
  
  public static final byte THREAD_POOL_TO_USE_MASK = 63;
  
  public static final byte CDR_ENC_VERSION = 0;
  
  public static final byte JAVA_ENC_VERSION = 1;
  
  public static final byte GIOPRequest = 0;
  
  public static final byte GIOPReply = 1;
  
  public static final byte GIOPCancelRequest = 2;
  
  public static final byte GIOPLocateRequest = 3;
  
  public static final byte GIOPLocateReply = 4;
  
  public static final byte GIOPCloseConnection = 5;
  
  public static final byte GIOPMessageError = 6;
  
  public static final byte GIOPFragment = 7;
  
  GIOPVersion getGIOPVersion();
  
  byte getEncodingVersion();
  
  boolean isLittleEndian();
  
  boolean moreFragmentsToFollow();
  
  int getType();
  
  int getSize();
  
  ByteBuffer getByteBuffer();
  
  int getThreadPoolToUse();
  
  void read(InputStream paramInputStream);
  
  void write(OutputStream paramOutputStream);
  
  void setSize(ByteBuffer paramByteBuffer, int paramInt);
  
  FragmentMessage createFragmentMessage();
  
  void callback(MessageHandler paramMessageHandler) throws IOException;
  
  void setByteBuffer(ByteBuffer paramByteBuffer);
  
  void setEncodingVersion(byte paramByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */