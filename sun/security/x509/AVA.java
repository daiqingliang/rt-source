package sun.security.x509;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.AccessController;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import sun.security.action.GetBooleanAction;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.Debug;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class AVA implements DerEncoder {
  private static final Debug debug = Debug.getInstance("x509", "\t[AVA]");
  
  private static final boolean PRESERVE_OLD_DC_ENCODING = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.preserveOldDCEncoding"))).booleanValue();
  
  static final int DEFAULT = 1;
  
  static final int RFC1779 = 2;
  
  static final int RFC2253 = 3;
  
  final ObjectIdentifier oid;
  
  final DerValue value;
  
  private static final String specialChars1779 = ",=\n+<>#;\\\"";
  
  private static final String specialChars2253 = ",=+<>#;\\\"";
  
  private static final String specialCharsDefault = ",=\n+<>#;\\\" ";
  
  private static final String escapedDefault = ",+<>;\"";
  
  private static final String hexDigits = "0123456789ABCDEF";
  
  public AVA(ObjectIdentifier paramObjectIdentifier, DerValue paramDerValue) {
    if (paramObjectIdentifier == null || paramDerValue == null)
      throw new NullPointerException(); 
    this.oid = paramObjectIdentifier;
    this.value = paramDerValue;
  }
  
  AVA(Reader paramReader) throws IOException { this(paramReader, 1); }
  
  AVA(Reader paramReader, Map<String, String> paramMap) throws IOException { this(paramReader, 1, paramMap); }
  
  AVA(Reader paramReader, int paramInt) throws IOException { this(paramReader, paramInt, Collections.emptyMap()); }
  
  AVA(Reader paramReader, int paramInt, Map<String, String> paramMap) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    while (true) {
      i = readChar(paramReader, "Incorrect AVA format");
      if (i == 61)
        break; 
      stringBuilder.append((char)i);
    } 
    this.oid = AVAKeyword.getOID(stringBuilder.toString(), paramInt, paramMap);
    stringBuilder.setLength(0);
    if (paramInt == 3) {
      i = paramReader.read();
      if (i == 32)
        throw new IOException("Incorrect AVA RFC2253 format - leading space must be escaped"); 
    } else {
      do {
        i = paramReader.read();
      } while (i == 32 || i == 10);
    } 
    if (i == -1) {
      this.value = new DerValue("");
      return;
    } 
    if (i == 35) {
      this.value = parseHexString(paramReader, paramInt);
    } else if (i == 34 && paramInt != 3) {
      this.value = parseQuotedString(paramReader, stringBuilder);
    } else {
      this.value = parseString(paramReader, i, paramInt, stringBuilder);
    } 
  }
  
  public ObjectIdentifier getObjectIdentifier() { return this.oid; }
  
  public DerValue getDerValue() { return this.value; }
  
  public String getValueString() {
    try {
      String str = this.value.getAsString();
      if (str == null)
        throw new RuntimeException("AVA string is null"); 
      return str;
    } catch (IOException iOException) {
      throw new RuntimeException("AVA error: " + iOException, iOException);
    } 
  }
  
  private static DerValue parseHexString(Reader paramReader, int paramInt) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte b = 0;
    byte b1;
    for (b1 = 0;; b1++) {
      int i = paramReader.read();
      if (isTerminator(i, paramInt))
        break; 
      int j = "0123456789ABCDEF".indexOf(Character.toUpperCase((char)i));
      if (j == -1)
        throw new IOException("AVA parse, invalid hex digit: " + (char)i); 
      if (b1 % 2 == 1) {
        b = (byte)(b * 16 + (byte)j);
        byteArrayOutputStream.write(b);
      } else {
        b = (byte)j;
      } 
    } 
    if (b1 == 0)
      throw new IOException("AVA parse, zero hex digits"); 
    if (b1 % 2 == 1)
      throw new IOException("AVA parse, odd number of hex digits"); 
    return new DerValue(byteArrayOutputStream.toByteArray());
  }
  
  private DerValue parseQuotedString(Reader paramReader, StringBuilder paramStringBuilder) throws IOException {
    int i = readChar(paramReader, "Quoted string did not end in quote");
    ArrayList arrayList = new ArrayList();
    boolean bool = true;
    while (i != 34) {
      if (i == 92) {
        i = readChar(paramReader, "Quoted string did not end in quote");
        Byte byte = null;
        if ((byte = getEmbeddedHexPair(i, paramReader)) != null) {
          bool = false;
          arrayList.add(byte);
          i = paramReader.read();
          continue;
        } 
        if (",=\n+<>#;\\\"".indexOf((char)i) < 0)
          throw new IOException("Invalid escaped character in AVA: " + (char)i); 
      } 
      if (arrayList.size() > 0) {
        String str = getEmbeddedHexString(arrayList);
        paramStringBuilder.append(str);
        arrayList.clear();
      } 
      bool &= DerValue.isPrintableStringChar((char)i);
      paramStringBuilder.append((char)i);
      i = readChar(paramReader, "Quoted string did not end in quote");
    } 
    if (arrayList.size() > 0) {
      String str = getEmbeddedHexString(arrayList);
      paramStringBuilder.append(str);
      arrayList.clear();
    } 
    do {
      i = paramReader.read();
    } while (i == 10 || i == 32);
    if (i != -1)
      throw new IOException("AVA had characters other than whitespace after terminating quote"); 
    return (this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID) || (this.oid.equals(X500Name.DOMAIN_COMPONENT_OID) && !PRESERVE_OLD_DC_ENCODING)) ? new DerValue((byte)22, paramStringBuilder.toString().trim()) : (bool ? new DerValue(paramStringBuilder.toString().trim()) : new DerValue((byte)12, paramStringBuilder.toString().trim()));
  }
  
  private DerValue parseString(Reader paramReader, int paramInt1, int paramInt2, StringBuilder paramStringBuilder) throws IOException { // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore #5
    //   9: iconst_1
    //   10: istore #6
    //   12: iconst_0
    //   13: istore #7
    //   15: iconst_1
    //   16: istore #8
    //   18: iconst_0
    //   19: istore #9
    //   21: iconst_0
    //   22: istore #7
    //   24: iload_2
    //   25: bipush #92
    //   27: if_icmpne -> 228
    //   30: iconst_1
    //   31: istore #7
    //   33: aload_1
    //   34: ldc 'Invalid trailing backslash'
    //   36: invokestatic readChar : (Ljava/io/Reader;Ljava/lang/String;)I
    //   39: istore_2
    //   40: aconst_null
    //   41: astore #10
    //   43: iload_2
    //   44: aload_1
    //   45: invokestatic getEmbeddedHexPair : (ILjava/io/Reader;)Ljava/lang/Byte;
    //   48: dup
    //   49: astore #10
    //   51: ifnull -> 78
    //   54: iconst_0
    //   55: istore #6
    //   57: aload #5
    //   59: aload #10
    //   61: invokeinterface add : (Ljava/lang/Object;)Z
    //   66: pop
    //   67: aload_1
    //   68: invokevirtual read : ()I
    //   71: istore_2
    //   72: iconst_0
    //   73: istore #8
    //   75: goto -> 450
    //   78: iload_3
    //   79: iconst_1
    //   80: if_icmpne -> 127
    //   83: ldc ',=\\n+<>#;\" '
    //   85: iload_2
    //   86: i2c
    //   87: invokevirtual indexOf : (I)I
    //   90: iconst_m1
    //   91: if_icmpne -> 127
    //   94: new java/io/IOException
    //   97: dup
    //   98: new java/lang/StringBuilder
    //   101: dup
    //   102: invokespecial <init> : ()V
    //   105: ldc 'Invalid escaped character in AVA: ''
    //   107: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: iload_2
    //   111: i2c
    //   112: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   115: ldc '''
    //   117: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: invokevirtual toString : ()Ljava/lang/String;
    //   123: invokespecial <init> : (Ljava/lang/String;)V
    //   126: athrow
    //   127: iload_3
    //   128: iconst_3
    //   129: if_icmpne -> 225
    //   132: iload_2
    //   133: bipush #32
    //   135: if_icmpne -> 160
    //   138: iload #8
    //   140: ifne -> 225
    //   143: aload_1
    //   144: invokestatic trailingSpace : (Ljava/io/Reader;)Z
    //   147: ifne -> 225
    //   150: new java/io/IOException
    //   153: dup
    //   154: ldc 'Invalid escaped space character in AVA.  Only a leading or trailing space character can be escaped.'
    //   156: invokespecial <init> : (Ljava/lang/String;)V
    //   159: athrow
    //   160: iload_2
    //   161: bipush #35
    //   163: if_icmpne -> 181
    //   166: iload #8
    //   168: ifne -> 225
    //   171: new java/io/IOException
    //   174: dup
    //   175: ldc 'Invalid escaped '#' character in AVA.  Only a leading '#' can be escaped.'
    //   177: invokespecial <init> : (Ljava/lang/String;)V
    //   180: athrow
    //   181: ldc ',=+<>#;\"'
    //   183: iload_2
    //   184: i2c
    //   185: invokevirtual indexOf : (I)I
    //   188: iconst_m1
    //   189: if_icmpne -> 225
    //   192: new java/io/IOException
    //   195: dup
    //   196: new java/lang/StringBuilder
    //   199: dup
    //   200: invokespecial <init> : ()V
    //   203: ldc 'Invalid escaped character in AVA: ''
    //   205: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   208: iload_2
    //   209: i2c
    //   210: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   213: ldc '''
    //   215: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: invokevirtual toString : ()Ljava/lang/String;
    //   221: invokespecial <init> : (Ljava/lang/String;)V
    //   224: athrow
    //   225: goto -> 321
    //   228: iload_3
    //   229: iconst_3
    //   230: if_icmpne -> 277
    //   233: ldc ',=+<>#;\"'
    //   235: iload_2
    //   236: i2c
    //   237: invokevirtual indexOf : (I)I
    //   240: iconst_m1
    //   241: if_icmpeq -> 321
    //   244: new java/io/IOException
    //   247: dup
    //   248: new java/lang/StringBuilder
    //   251: dup
    //   252: invokespecial <init> : ()V
    //   255: ldc 'Character ''
    //   257: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   260: iload_2
    //   261: i2c
    //   262: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   265: ldc '' in AVA appears without escape'
    //   267: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   270: invokevirtual toString : ()Ljava/lang/String;
    //   273: invokespecial <init> : (Ljava/lang/String;)V
    //   276: athrow
    //   277: ldc ',+<>;"'
    //   279: iload_2
    //   280: i2c
    //   281: invokevirtual indexOf : (I)I
    //   284: iconst_m1
    //   285: if_icmpeq -> 321
    //   288: new java/io/IOException
    //   291: dup
    //   292: new java/lang/StringBuilder
    //   295: dup
    //   296: invokespecial <init> : ()V
    //   299: ldc 'Character ''
    //   301: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   304: iload_2
    //   305: i2c
    //   306: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   309: ldc '' in AVA appears without escape'
    //   311: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   314: invokevirtual toString : ()Ljava/lang/String;
    //   317: invokespecial <init> : (Ljava/lang/String;)V
    //   320: athrow
    //   321: aload #5
    //   323: invokeinterface size : ()I
    //   328: ifle -> 380
    //   331: iconst_0
    //   332: istore #10
    //   334: iload #10
    //   336: iload #9
    //   338: if_icmpge -> 355
    //   341: aload #4
    //   343: ldc ' '
    //   345: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   348: pop
    //   349: iinc #10, 1
    //   352: goto -> 334
    //   355: iconst_0
    //   356: istore #9
    //   358: aload #5
    //   360: invokestatic getEmbeddedHexString : (Ljava/util/List;)Ljava/lang/String;
    //   363: astore #10
    //   365: aload #4
    //   367: aload #10
    //   369: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   372: pop
    //   373: aload #5
    //   375: invokeinterface clear : ()V
    //   380: iload #6
    //   382: iload_2
    //   383: i2c
    //   384: invokestatic isPrintableStringChar : (C)Z
    //   387: iand
    //   388: istore #6
    //   390: iload_2
    //   391: bipush #32
    //   393: if_icmpne -> 407
    //   396: iload #7
    //   398: ifne -> 407
    //   401: iinc #9, 1
    //   404: goto -> 442
    //   407: iconst_0
    //   408: istore #10
    //   410: iload #10
    //   412: iload #9
    //   414: if_icmpge -> 431
    //   417: aload #4
    //   419: ldc ' '
    //   421: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   424: pop
    //   425: iinc #10, 1
    //   428: goto -> 410
    //   431: iconst_0
    //   432: istore #9
    //   434: aload #4
    //   436: iload_2
    //   437: i2c
    //   438: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   441: pop
    //   442: aload_1
    //   443: invokevirtual read : ()I
    //   446: istore_2
    //   447: iconst_0
    //   448: istore #8
    //   450: iload_2
    //   451: iload_3
    //   452: invokestatic isTerminator : (II)Z
    //   455: ifeq -> 21
    //   458: iload_3
    //   459: iconst_3
    //   460: if_icmpne -> 478
    //   463: iload #9
    //   465: ifle -> 478
    //   468: new java/io/IOException
    //   471: dup
    //   472: ldc 'Incorrect AVA RFC2253 format - trailing space must be escaped'
    //   474: invokespecial <init> : (Ljava/lang/String;)V
    //   477: athrow
    //   478: aload #5
    //   480: invokeinterface size : ()I
    //   485: ifle -> 510
    //   488: aload #5
    //   490: invokestatic getEmbeddedHexString : (Ljava/util/List;)Ljava/lang/String;
    //   493: astore #10
    //   495: aload #4
    //   497: aload #10
    //   499: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   502: pop
    //   503: aload #5
    //   505: invokeinterface clear : ()V
    //   510: aload_0
    //   511: getfield oid : Lsun/security/util/ObjectIdentifier;
    //   514: getstatic sun/security/pkcs/PKCS9Attribute.EMAIL_ADDRESS_OID : Lsun/security/util/ObjectIdentifier;
    //   517: invokevirtual equals : (Ljava/lang/Object;)Z
    //   520: ifne -> 542
    //   523: aload_0
    //   524: getfield oid : Lsun/security/util/ObjectIdentifier;
    //   527: getstatic sun/security/x509/X500Name.DOMAIN_COMPONENT_OID : Lsun/security/util/ObjectIdentifier;
    //   530: invokevirtual equals : (Ljava/lang/Object;)Z
    //   533: ifeq -> 557
    //   536: getstatic sun/security/x509/AVA.PRESERVE_OLD_DC_ENCODING : Z
    //   539: ifne -> 557
    //   542: new sun/security/util/DerValue
    //   545: dup
    //   546: bipush #22
    //   548: aload #4
    //   550: invokevirtual toString : ()Ljava/lang/String;
    //   553: invokespecial <init> : (BLjava/lang/String;)V
    //   556: areturn
    //   557: iload #6
    //   559: ifeq -> 575
    //   562: new sun/security/util/DerValue
    //   565: dup
    //   566: aload #4
    //   568: invokevirtual toString : ()Ljava/lang/String;
    //   571: invokespecial <init> : (Ljava/lang/String;)V
    //   574: areturn
    //   575: new sun/security/util/DerValue
    //   578: dup
    //   579: bipush #12
    //   581: aload #4
    //   583: invokevirtual toString : ()Ljava/lang/String;
    //   586: invokespecial <init> : (BLjava/lang/String;)V
    //   589: areturn }
  
  private static Byte getEmbeddedHexPair(int paramInt, Reader paramReader) throws IOException {
    if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)paramInt)) >= 0) {
      int i = readChar(paramReader, "unexpected EOF - escaped hex value must include two valid digits");
      if ("0123456789ABCDEF".indexOf(Character.toUpperCase((char)i)) >= 0) {
        int j = Character.digit((char)paramInt, 16);
        int k = Character.digit((char)i, 16);
        return new Byte((byte)((j << 4) + k));
      } 
      throw new IOException("escaped hex value must include two valid digits");
    } 
    return null;
  }
  
  private static String getEmbeddedHexString(List<Byte> paramList) throws IOException {
    int i = paramList.size();
    byte[] arrayOfByte = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = ((Byte)paramList.get(b)).byteValue(); 
    return new String(arrayOfByte, "UTF8");
  }
  
  private static boolean isTerminator(int paramInt1, int paramInt2) {
    switch (paramInt1) {
      case -1:
      case 43:
      case 44:
        return true;
      case 59:
        return (paramInt2 != 3);
    } 
    return false;
  }
  
  private static int readChar(Reader paramReader, String paramString) throws IOException {
    int i = paramReader.read();
    if (i == -1)
      throw new IOException(paramString); 
    return i;
  }
  
  private static boolean trailingSpace(Reader paramReader) throws IOException {
    boolean bool = false;
    if (!paramReader.markSupported())
      return true; 
    paramReader.mark(9999);
    while (true) {
      int i = paramReader.read();
      if (i == -1) {
        bool = true;
        break;
      } 
      if (i == 32)
        continue; 
      if (i == 92) {
        int j = paramReader.read();
        if (j != 32) {
          bool = false;
          break;
        } 
        continue;
      } 
      bool = false;
      break;
    } 
    paramReader.reset();
    return bool;
  }
  
  AVA(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("AVA not a sequence"); 
    this.oid = X500Name.intern(paramDerValue.data.getOID());
    this.value = paramDerValue.data.getDerValue();
    if (paramDerValue.data.available() != 0)
      throw new IOException("AVA, extra bytes = " + paramDerValue.data.available()); 
  }
  
  AVA(DerInputStream paramDerInputStream) throws IOException { this(paramDerInputStream.getDerValue()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof AVA))
      return false; 
    AVA aVA = (AVA)paramObject;
    return toRFC2253CanonicalString().equals(aVA.toRFC2253CanonicalString());
  }
  
  public int hashCode() { return toRFC2253CanonicalString().hashCode(); }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException { derEncode(paramDerOutputStream); }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream1.putOID(this.oid);
    this.value.encode(derOutputStream1);
    derOutputStream2.write((byte)48, derOutputStream1);
    paramOutputStream.write(derOutputStream2.toByteArray());
  }
  
  private String toKeyword(int paramInt, Map<String, String> paramMap) { return AVAKeyword.getKeyword(this.oid, paramInt, paramMap); }
  
  public String toString() { return toKeywordValueString(toKeyword(1, Collections.emptyMap())); }
  
  public String toRFC1779String() { return toRFC1779String(Collections.emptyMap()); }
  
  public String toRFC1779String(Map<String, String> paramMap) { return toKeywordValueString(toKeyword(2, paramMap)); }
  
  public String toRFC2253String() { return toRFC2253String(Collections.emptyMap()); }
  
  public String toRFC2253String(Map<String, String> paramMap) {
    StringBuilder stringBuilder = new StringBuilder(100);
    stringBuilder.append(toKeyword(3, paramMap));
    stringBuilder.append('=');
    if ((stringBuilder.charAt(0) >= '0' && stringBuilder.charAt(0) <= '9') || !isDerString(this.value, false)) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = this.value.toByteArray();
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      stringBuilder.append('#');
      for (byte b = 0; b < arrayOfByte.length; b++) {
        byte b1 = arrayOfByte[b];
        stringBuilder.append(Character.forDigit(0xF & b1 >>> 4, 16));
        stringBuilder.append(Character.forDigit(0xF & b1, 16));
      } 
    } else {
      String str = null;
      try {
        str = new String(this.value.getDataBytes(), "UTF8");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      StringBuilder stringBuilder1 = new StringBuilder();
      for (byte b1 = 0; b1 < str.length(); b1++) {
        char c = str.charAt(b1);
        if (DerValue.isPrintableStringChar(c) || ",=+<>#;\"\\".indexOf(c) >= 0) {
          if (",=+<>#;\"\\".indexOf(c) >= 0)
            stringBuilder1.append('\\'); 
          stringBuilder1.append(c);
        } else if (c == '\000') {
          stringBuilder1.append("\\00");
        } else if (debug != null && Debug.isOn("ava")) {
          byte[] arrayOfByte = null;
          try {
            arrayOfByte = Character.toString(c).getBytes("UTF8");
          } catch (IOException iOException) {
            throw new IllegalArgumentException("DER Value conversion");
          } 
          for (byte b = 0; b < arrayOfByte.length; b++) {
            stringBuilder1.append('\\');
            char c1 = Character.forDigit(0xF & arrayOfByte[b] >>> 4, 16);
            stringBuilder1.append(Character.toUpperCase(c1));
            c1 = Character.forDigit(0xF & arrayOfByte[b], 16);
            stringBuilder1.append(Character.toUpperCase(c1));
          } 
        } else {
          stringBuilder1.append(c);
        } 
      } 
      char[] arrayOfChar = stringBuilder1.toString().toCharArray();
      stringBuilder1 = new StringBuilder();
      byte b2;
      for (b2 = 0; b2 < arrayOfChar.length && (arrayOfChar[b2] == ' ' || arrayOfChar[b2] == '\r'); b2++);
      int i;
      for (i = arrayOfChar.length - 1; i >= 0 && (arrayOfChar[i] == ' ' || arrayOfChar[i] == '\r'); i--);
      for (byte b3 = 0; b3 < arrayOfChar.length; b3++) {
        char c = arrayOfChar[b3];
        if (b3 < b2 || b3 > i)
          stringBuilder1.append('\\'); 
        stringBuilder1.append(c);
      } 
      stringBuilder.append(stringBuilder1.toString());
    } 
    return stringBuilder.toString();
  }
  
  public String toRFC2253CanonicalString() {
    StringBuilder stringBuilder = new StringBuilder(40);
    stringBuilder.append(toKeyword(3, Collections.emptyMap()));
    stringBuilder.append('=');
    if ((stringBuilder.charAt(0) >= '0' && stringBuilder.charAt(0) <= '9') || !isDerString(this.value, true)) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = this.value.toByteArray();
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      stringBuilder.append('#');
      for (byte b = 0; b < arrayOfByte.length; b++) {
        byte b1 = arrayOfByte[b];
        stringBuilder.append(Character.forDigit(0xF & b1 >>> 4, 16));
        stringBuilder.append(Character.forDigit(0xF & b1, 16));
      } 
    } else {
      String str1 = null;
      try {
        str1 = new String(this.value.getDataBytes(), "UTF8");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("DER Value conversion");
      } 
      StringBuilder stringBuilder1 = new StringBuilder();
      boolean bool = false;
      for (byte b = 0; b < str1.length(); b++) {
        char c = str1.charAt(b);
        if (DerValue.isPrintableStringChar(c) || ",+<>;\"\\".indexOf(c) >= 0 || (b == 0 && c == '#')) {
          if ((b == 0 && c == '#') || ",+<>;\"\\".indexOf(c) >= 0)
            stringBuilder1.append('\\'); 
          if (!Character.isWhitespace(c)) {
            bool = false;
            stringBuilder1.append(c);
          } else if (!bool) {
            bool = true;
            stringBuilder1.append(c);
          } 
        } else if (debug != null && Debug.isOn("ava")) {
          bool = false;
          byte[] arrayOfByte = null;
          try {
            arrayOfByte = Character.toString(c).getBytes("UTF8");
          } catch (IOException iOException) {
            throw new IllegalArgumentException("DER Value conversion");
          } 
          for (byte b1 = 0; b1 < arrayOfByte.length; b1++) {
            stringBuilder1.append('\\');
            stringBuilder1.append(Character.forDigit(0xF & arrayOfByte[b1] >>> 4, 16));
            stringBuilder1.append(Character.forDigit(0xF & arrayOfByte[b1], 16));
          } 
        } else {
          bool = false;
          stringBuilder1.append(c);
        } 
      } 
      stringBuilder.append(stringBuilder1.toString().trim());
    } 
    String str = stringBuilder.toString();
    str = str.toUpperCase(Locale.US).toLowerCase(Locale.US);
    return Normalizer.normalize(str, Normalizer.Form.NFKD);
  }
  
  private static boolean isDerString(DerValue paramDerValue, boolean paramBoolean) {
    if (paramBoolean) {
      switch (paramDerValue.tag) {
        case 12:
        case 19:
          return true;
      } 
      return false;
    } 
    switch (paramDerValue.tag) {
      case 12:
      case 19:
      case 20:
      case 22:
      case 27:
      case 30:
        return true;
    } 
    return false;
  }
  
  boolean hasRFC2253Keyword() { return AVAKeyword.hasKeyword(this.oid, 3); }
  
  private String toKeywordValueString(String paramString) {
    StringBuilder stringBuilder = new StringBuilder(40);
    stringBuilder.append(paramString);
    stringBuilder.append("=");
    try {
      String str = this.value.getAsString();
      if (str == null) {
        byte[] arrayOfByte = this.value.toByteArray();
        stringBuilder.append('#');
        for (byte b = 0; b < arrayOfByte.length; b++) {
          stringBuilder.append("0123456789ABCDEF".charAt(arrayOfByte[b] >> 4 & 0xF));
          stringBuilder.append("0123456789ABCDEF".charAt(arrayOfByte[b] & 0xF));
        } 
      } else {
        boolean bool1 = false;
        StringBuilder stringBuilder1 = new StringBuilder();
        boolean bool2 = false;
        int i = str.length();
        boolean bool3 = (i > 1 && str.charAt(0) == '"' && str.charAt(i - 1) == '"') ? 1 : 0;
        char c;
        for (c = Character.MIN_VALUE; c < i; c++) {
          char c1 = str.charAt(c);
          if (bool3 && (c == Character.MIN_VALUE || c == i - 1)) {
            stringBuilder1.append(c1);
          } else if (DerValue.isPrintableStringChar(c1) || ",+=\n<>#;\\\"".indexOf(c1) >= 0) {
            if (!bool1 && ((c == Character.MIN_VALUE && (c1 == ' ' || c1 == '\n')) || ",+=\n<>#;\\\"".indexOf(c1) >= 0))
              bool1 = true; 
            if (c1 != ' ' && c1 != '\n') {
              if (c1 == '"' || c1 == '\\')
                stringBuilder1.append('\\'); 
              bool2 = false;
            } else {
              if (!bool1 && bool2)
                bool1 = true; 
              bool2 = true;
            } 
            stringBuilder1.append(c1);
          } else if (debug != null && Debug.isOn("ava")) {
            bool2 = false;
            byte[] arrayOfByte = Character.toString(c1).getBytes("UTF8");
            for (byte b = 0; b < arrayOfByte.length; b++) {
              stringBuilder1.append('\\');
              char c2 = Character.forDigit(0xF & arrayOfByte[b] >>> 4, 16);
              stringBuilder1.append(Character.toUpperCase(c2));
              c2 = Character.forDigit(0xF & arrayOfByte[b], 16);
              stringBuilder1.append(Character.toUpperCase(c2));
            } 
          } else {
            bool2 = false;
            stringBuilder1.append(c1);
          } 
        } 
        if (stringBuilder1.length() > 0) {
          c = stringBuilder1.charAt(stringBuilder1.length() - 1);
          if (c == ' ' || c == '\n')
            bool1 = true; 
        } 
        if (!bool3 && bool1) {
          stringBuilder.append("\"" + stringBuilder1.toString() + "\"");
        } else {
          stringBuilder.append(stringBuilder1.toString());
        } 
      } 
    } catch (IOException iOException) {
      throw new IllegalArgumentException("DER Value conversion");
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\AVA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */