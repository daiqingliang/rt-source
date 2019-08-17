package sun.net;

import java.io.IOException;
import java.util.Vector;

public class TransferProtocolClient extends NetworkClient {
  static final boolean debug = false;
  
  protected Vector<String> serverResponse = new Vector(1);
  
  protected int lastReplyCode;
  
  public int readServerResponse() throws IOException { // Byte code:
    //   0: new java/lang/StringBuffer
    //   3: dup
    //   4: bipush #32
    //   6: invokespecial <init> : (I)V
    //   9: astore_1
    //   10: iconst_m1
    //   11: istore_3
    //   12: aload_0
    //   13: getfield serverResponse : Ljava/util/Vector;
    //   16: iconst_0
    //   17: invokevirtual setSize : (I)V
    //   20: aload_0
    //   21: getfield serverInput : Ljava/io/InputStream;
    //   24: invokevirtual read : ()I
    //   27: dup
    //   28: istore_2
    //   29: iconst_m1
    //   30: if_icmpeq -> 76
    //   33: iload_2
    //   34: bipush #13
    //   36: if_icmpne -> 60
    //   39: aload_0
    //   40: getfield serverInput : Ljava/io/InputStream;
    //   43: invokevirtual read : ()I
    //   46: dup
    //   47: istore_2
    //   48: bipush #10
    //   50: if_icmpeq -> 60
    //   53: aload_1
    //   54: bipush #13
    //   56: invokevirtual append : (C)Ljava/lang/StringBuffer;
    //   59: pop
    //   60: aload_1
    //   61: iload_2
    //   62: i2c
    //   63: invokevirtual append : (C)Ljava/lang/StringBuffer;
    //   66: pop
    //   67: iload_2
    //   68: bipush #10
    //   70: if_icmpne -> 20
    //   73: goto -> 76
    //   76: aload_1
    //   77: invokevirtual toString : ()Ljava/lang/String;
    //   80: astore #5
    //   82: aload_1
    //   83: iconst_0
    //   84: invokevirtual setLength : (I)V
    //   87: aload #5
    //   89: invokevirtual length : ()I
    //   92: ifne -> 101
    //   95: iconst_m1
    //   96: istore #4
    //   98: goto -> 129
    //   101: aload #5
    //   103: iconst_0
    //   104: iconst_3
    //   105: invokevirtual substring : (II)Ljava/lang/String;
    //   108: invokestatic parseInt : (Ljava/lang/String;)I
    //   111: istore #4
    //   113: goto -> 129
    //   116: astore #6
    //   118: iconst_m1
    //   119: istore #4
    //   121: goto -> 129
    //   124: astore #6
    //   126: goto -> 20
    //   129: aload_0
    //   130: getfield serverResponse : Ljava/util/Vector;
    //   133: aload #5
    //   135: invokevirtual addElement : (Ljava/lang/Object;)V
    //   138: iload_3
    //   139: iconst_m1
    //   140: if_icmpeq -> 177
    //   143: iload #4
    //   145: iload_3
    //   146: if_icmpne -> 20
    //   149: aload #5
    //   151: invokevirtual length : ()I
    //   154: iconst_4
    //   155: if_icmplt -> 172
    //   158: aload #5
    //   160: iconst_3
    //   161: invokevirtual charAt : (I)C
    //   164: bipush #45
    //   166: if_icmpne -> 172
    //   169: goto -> 20
    //   172: iconst_m1
    //   173: istore_3
    //   174: goto -> 203
    //   177: aload #5
    //   179: invokevirtual length : ()I
    //   182: iconst_4
    //   183: if_icmplt -> 203
    //   186: aload #5
    //   188: iconst_3
    //   189: invokevirtual charAt : (I)C
    //   192: bipush #45
    //   194: if_icmpne -> 203
    //   197: iload #4
    //   199: istore_3
    //   200: goto -> 20
    //   203: aload_0
    //   204: iload #4
    //   206: dup_x1
    //   207: putfield lastReplyCode : I
    //   210: ireturn
    // Exception table:
    //   from	to	target	type
    //   101	113	116	java/lang/NumberFormatException
    //   101	113	124	java/lang/StringIndexOutOfBoundsException }
  
  public void sendServer(String paramString) { this.serverOutput.print(paramString); }
  
  public String getResponseString() { return (String)this.serverResponse.elementAt(0); }
  
  public Vector<String> getResponseStrings() { return this.serverResponse; }
  
  public TransferProtocolClient(String paramString, int paramInt) throws IOException { super(paramString, paramInt); }
  
  public TransferProtocolClient() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\TransferProtocolClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */