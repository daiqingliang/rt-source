package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class UTF_32Coder {
  protected static final int BOM_BIG = 65279;
  
  protected static final int BOM_LITTLE = -131072;
  
  protected static final int NONE = 0;
  
  protected static final int BIG = 1;
  
  protected static final int LITTLE = 2;
  
  protected static class Decoder extends CharsetDecoder {
    private int currentBO;
    
    private int expectedBO;
    
    protected Decoder(Charset param1Charset, int param1Int) {
      super(param1Charset, 0.25F, 1.0F);
      this.expectedBO = param1Int;
      this.currentBO = 0;
    }
    
    private int getCP(ByteBuffer param1ByteBuffer) { return (this.currentBO == 1) ? ((param1ByteBuffer.get() & 0xFF) << 24 | (param1ByteBuffer.get() & 0xFF) << 16 | (param1ByteBuffer.get() & 0xFF) << 8 | param1ByteBuffer.get() & 0xFF) : (param1ByteBuffer.get() & 0xFF | (param1ByteBuffer.get() & 0xFF) << 8 | (param1ByteBuffer.get() & 0xFF) << 16 | (param1ByteBuffer.get() & 0xFF) << 24); }
    
    protected CoderResult decodeLoop(ByteBuffer param1ByteBuffer, CharBuffer param1CharBuffer) {
      if (param1ByteBuffer.remaining() < 4)
        return CoderResult.UNDERFLOW; 
      i = param1ByteBuffer.position();
      try {
        if (this.currentBO == 0) {
          byte b = (param1ByteBuffer.get() & 0xFF) << 24 | (param1ByteBuffer.get() & 0xFF) << 16 | (param1ByteBuffer.get() & 0xFF) << 8 | param1ByteBuffer.get() & 0xFF;
          if (b == 65279 && this.expectedBO != 2) {
            this.currentBO = 1;
            i += 4;
          } else if (b == -131072 && this.expectedBO != 1) {
            this.currentBO = 2;
            i += 4;
          } else {
            if (this.expectedBO == 0) {
              this.currentBO = 1;
            } else {
              this.currentBO = this.expectedBO;
            } 
            param1ByteBuffer.position(i);
          } 
        } 
        while (param1ByteBuffer.remaining() >= 4) {
          int j = getCP(param1ByteBuffer);
          if (Character.isBmpCodePoint(j)) {
            if (!param1CharBuffer.hasRemaining())
              return CoderResult.OVERFLOW; 
            i += 4;
            param1CharBuffer.put((char)j);
            continue;
          } 
          if (Character.isValidCodePoint(j)) {
            if (param1CharBuffer.remaining() < 2)
              return CoderResult.OVERFLOW; 
            i += 4;
            param1CharBuffer.put(Character.highSurrogate(j));
            param1CharBuffer.put(Character.lowSurrogate(j));
            continue;
          } 
          return CoderResult.malformedForLength(4);
        } 
        return CoderResult.UNDERFLOW;
      } finally {
        param1ByteBuffer.position(i);
      } 
    }
    
    protected void implReset() { this.currentBO = 0; }
  }
  
  protected static class Encoder extends CharsetEncoder {
    private boolean doBOM = false;
    
    private boolean doneBOM = true;
    
    private int byteOrder;
    
    protected void put(int param1Int, ByteBuffer param1ByteBuffer) {
      if (this.byteOrder == 1) {
        param1ByteBuffer.put((byte)(param1Int >> 24));
        param1ByteBuffer.put((byte)(param1Int >> 16));
        param1ByteBuffer.put((byte)(param1Int >> 8));
        param1ByteBuffer.put((byte)param1Int);
      } else {
        param1ByteBuffer.put((byte)param1Int);
        param1ByteBuffer.put((byte)(param1Int >> 8));
        param1ByteBuffer.put((byte)(param1Int >> 16));
        param1ByteBuffer.put((byte)(param1Int >> 24));
      } 
    }
    
    protected Encoder(Charset param1Charset, int param1Int, boolean param1Boolean) {
      super(param1Charset, 4.0F, param1Boolean ? 8.0F : 4.0F, (param1Int == 1) ? new byte[4] : new byte[4]);
      this.byteOrder = param1Int;
      this.doBOM = param1Boolean;
      this.doneBOM = !param1Boolean;
    }
    
    protected CoderResult encodeLoop(CharBuffer param1CharBuffer, ByteBuffer param1ByteBuffer) { // Byte code:
      //   0: aload_1
      //   1: invokevirtual position : ()I
      //   4: istore_3
      //   5: aload_0
      //   6: getfield doneBOM : Z
      //   9: ifne -> 43
      //   12: aload_1
      //   13: invokevirtual hasRemaining : ()Z
      //   16: ifeq -> 43
      //   19: aload_2
      //   20: invokevirtual remaining : ()I
      //   23: iconst_4
      //   24: if_icmpge -> 31
      //   27: getstatic java/nio/charset/CoderResult.OVERFLOW : Ljava/nio/charset/CoderResult;
      //   30: areturn
      //   31: aload_0
      //   32: ldc 65279
      //   34: aload_2
      //   35: invokevirtual put : (ILjava/nio/ByteBuffer;)V
      //   38: aload_0
      //   39: iconst_1
      //   40: putfield doneBOM : Z
      //   43: aload_1
      //   44: invokevirtual hasRemaining : ()Z
      //   47: ifeq -> 218
      //   50: aload_1
      //   51: invokevirtual get : ()C
      //   54: istore #4
      //   56: iload #4
      //   58: invokestatic isSurrogate : (C)Z
      //   61: ifne -> 99
      //   64: aload_2
      //   65: invokevirtual remaining : ()I
      //   68: iconst_4
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
      //   93: invokevirtual put : (ILjava/nio/ByteBuffer;)V
      //   96: goto -> 215
      //   99: iload #4
      //   101: invokestatic isHighSurrogate : (C)Z
      //   104: ifeq -> 200
      //   107: aload_1
      //   108: invokevirtual hasRemaining : ()Z
      //   111: ifne -> 128
      //   114: getstatic java/nio/charset/CoderResult.UNDERFLOW : Ljava/nio/charset/CoderResult;
      //   117: astore #5
      //   119: aload_1
      //   120: iload_3
      //   121: invokevirtual position : (I)Ljava/nio/Buffer;
      //   124: pop
      //   125: aload #5
      //   127: areturn
      //   128: aload_1
      //   129: invokevirtual get : ()C
      //   132: istore #5
      //   134: iload #5
      //   136: invokestatic isLowSurrogate : (C)Z
      //   139: ifeq -> 182
      //   142: aload_2
      //   143: invokevirtual remaining : ()I
      //   146: iconst_4
      //   147: if_icmpge -> 164
      //   150: getstatic java/nio/charset/CoderResult.OVERFLOW : Ljava/nio/charset/CoderResult;
      //   153: astore #6
      //   155: aload_1
      //   156: iload_3
      //   157: invokevirtual position : (I)Ljava/nio/Buffer;
      //   160: pop
      //   161: aload #6
      //   163: areturn
      //   164: iinc #3, 2
      //   167: aload_0
      //   168: iload #4
      //   170: iload #5
      //   172: invokestatic toCodePoint : (CC)I
      //   175: aload_2
      //   176: invokevirtual put : (ILjava/nio/ByteBuffer;)V
      //   179: goto -> 197
      //   182: iconst_1
      //   183: invokestatic malformedForLength : (I)Ljava/nio/charset/CoderResult;
      //   186: astore #6
      //   188: aload_1
      //   189: iload_3
      //   190: invokevirtual position : (I)Ljava/nio/Buffer;
      //   193: pop
      //   194: aload #6
      //   196: areturn
      //   197: goto -> 215
      //   200: iconst_1
      //   201: invokestatic malformedForLength : (I)Ljava/nio/charset/CoderResult;
      //   204: astore #5
      //   206: aload_1
      //   207: iload_3
      //   208: invokevirtual position : (I)Ljava/nio/Buffer;
      //   211: pop
      //   212: aload #5
      //   214: areturn
      //   215: goto -> 43
      //   218: getstatic java/nio/charset/CoderResult.UNDERFLOW : Ljava/nio/charset/CoderResult;
      //   221: astore #4
      //   223: aload_1
      //   224: iload_3
      //   225: invokevirtual position : (I)Ljava/nio/Buffer;
      //   228: pop
      //   229: aload #4
      //   231: areturn
      //   232: astore #7
      //   234: aload_1
      //   235: iload_3
      //   236: invokevirtual position : (I)Ljava/nio/Buffer;
      //   239: pop
      //   240: aload #7
      //   242: athrow
      // Exception table:
      //   from	to	target	type
      //   43	77	232	finally
      //   86	119	232	finally
      //   128	155	232	finally
      //   164	188	232	finally
      //   197	206	232	finally
      //   215	223	232	finally
      //   232	234	232	finally }
    
    protected void implReset() { this.doneBOM = !this.doBOM; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\UTF_32Coder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */