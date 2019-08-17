package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public abstract class UnicodeEncoder extends CharsetEncoder {
  protected static final char BYTE_ORDER_MARK = '﻿';
  
  protected static final char REVERSED_MARK = '￾';
  
  protected static final int BIG = 0;
  
  protected static final int LITTLE = 1;
  
  private int byteOrder;
  
  private boolean usesMark;
  
  private boolean needsMark;
  
  private final Surrogate.Parser sgp = new Surrogate.Parser();
  
  protected UnicodeEncoder(Charset paramCharset, int paramInt, boolean paramBoolean) {
    super(paramCharset, 2.0F, paramBoolean ? 4.0F : 2.0F, (paramInt == 0) ? new byte[2] : new byte[2]);
    this.usesMark = this.needsMark = paramBoolean;
    this.byteOrder = paramInt;
  }
  
  private void put(char paramChar, ByteBuffer paramByteBuffer) {
    if (this.byteOrder == 0) {
      paramByteBuffer.put((byte)(paramChar >> '\b'));
      paramByteBuffer.put((byte)(paramChar & 0xFF));
    } else {
      paramByteBuffer.put((byte)(paramChar & 0xFF));
      paramByteBuffer.put((byte)(paramChar >> '\b'));
    } 
  }
  
  protected CoderResult encodeLoop(CharBuffer paramCharBuffer, ByteBuffer paramByteBuffer) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual position : ()I
    //   4: istore_3
    //   5: aload_0
    //   6: getfield needsMark : Z
    //   9: ifeq -> 43
    //   12: aload_1
    //   13: invokevirtual hasRemaining : ()Z
    //   16: ifeq -> 43
    //   19: aload_2
    //   20: invokevirtual remaining : ()I
    //   23: iconst_2
    //   24: if_icmpge -> 31
    //   27: getstatic java/nio/charset/CoderResult.OVERFLOW : Ljava/nio/charset/CoderResult;
    //   30: areturn
    //   31: aload_0
    //   32: ldc 65279
    //   34: aload_2
    //   35: invokespecial put : (CLjava/nio/ByteBuffer;)V
    //   38: aload_0
    //   39: iconst_0
    //   40: putfield needsMark : Z
    //   43: aload_1
    //   44: invokevirtual hasRemaining : ()Z
    //   47: ifeq -> 182
    //   50: aload_1
    //   51: invokevirtual get : ()C
    //   54: istore #4
    //   56: iload #4
    //   58: invokestatic isSurrogate : (C)Z
    //   61: ifne -> 99
    //   64: aload_2
    //   65: invokevirtual remaining : ()I
    //   68: iconst_2
    //   69: if_icmpge -> 86
    //   72: getstatic java/nio/charset/CoderResult.OVERFLOW : Ljava/nio/charset/CoderResult;
    //   75: astore #5
    //   77: aload_1
    //   78: iload_3
    //   79: invokevirtual position : (I)Ljava/nio/Buffer;
    //   82: pop
    //   83: aload #5
    //   85: areturn
    //   86: iinc #3, 1
    //   89: aload_0
    //   90: iload #4
    //   92: aload_2
    //   93: invokespecial put : (CLjava/nio/ByteBuffer;)V
    //   96: goto -> 43
    //   99: aload_0
    //   100: getfield sgp : Lsun/nio/cs/Surrogate$Parser;
    //   103: iload #4
    //   105: aload_1
    //   106: invokevirtual parse : (CLjava/nio/CharBuffer;)I
    //   109: istore #5
    //   111: iload #5
    //   113: ifge -> 134
    //   116: aload_0
    //   117: getfield sgp : Lsun/nio/cs/Surrogate$Parser;
    //   120: invokevirtual error : ()Ljava/nio/charset/CoderResult;
    //   123: astore #6
    //   125: aload_1
    //   126: iload_3
    //   127: invokevirtual position : (I)Ljava/nio/Buffer;
    //   130: pop
    //   131: aload #6
    //   133: areturn
    //   134: aload_2
    //   135: invokevirtual remaining : ()I
    //   138: iconst_4
    //   139: if_icmpge -> 156
    //   142: getstatic java/nio/charset/CoderResult.OVERFLOW : Ljava/nio/charset/CoderResult;
    //   145: astore #6
    //   147: aload_1
    //   148: iload_3
    //   149: invokevirtual position : (I)Ljava/nio/Buffer;
    //   152: pop
    //   153: aload #6
    //   155: areturn
    //   156: iinc #3, 2
    //   159: aload_0
    //   160: iload #5
    //   162: invokestatic highSurrogate : (I)C
    //   165: aload_2
    //   166: invokespecial put : (CLjava/nio/ByteBuffer;)V
    //   169: aload_0
    //   170: iload #5
    //   172: invokestatic lowSurrogate : (I)C
    //   175: aload_2
    //   176: invokespecial put : (CLjava/nio/ByteBuffer;)V
    //   179: goto -> 43
    //   182: getstatic java/nio/charset/CoderResult.UNDERFLOW : Ljava/nio/charset/CoderResult;
    //   185: astore #4
    //   187: aload_1
    //   188: iload_3
    //   189: invokevirtual position : (I)Ljava/nio/Buffer;
    //   192: pop
    //   193: aload #4
    //   195: areturn
    //   196: astore #7
    //   198: aload_1
    //   199: iload_3
    //   200: invokevirtual position : (I)Ljava/nio/Buffer;
    //   203: pop
    //   204: aload #7
    //   206: athrow
    // Exception table:
    //   from	to	target	type
    //   43	77	196	finally
    //   86	125	196	finally
    //   134	147	196	finally
    //   156	187	196	finally
    //   196	198	196	finally }
  
  protected void implReset() { this.needsMark = this.usesMark; }
  
  public boolean canEncode(char paramChar) { return !Character.isSurrogate(paramChar); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UnicodeEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */