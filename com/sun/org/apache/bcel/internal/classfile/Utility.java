package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class Utility {
  private static int consumed_chars;
  
  private static boolean wide = false;
  
  private static final int FREE_CHARS = 48;
  
  private static int[] CHAR_MAP = new int[48];
  
  private static int[] MAP_CHAR = new int[256];
  
  private static final char ESCAPE_CHAR = '$';
  
  public static final String accessToString(int paramInt) { return accessToString(paramInt, false); }
  
  public static final String accessToString(int paramInt, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = 0;
    for (byte b = 0; i < 2048; b++) {
      i = pow2(b);
      if ((paramInt & i) != 0 && (!paramBoolean || (i != 32 && i != 512)))
        stringBuffer.append(Constants.ACCESS_NAMES[b] + " "); 
    } 
    return stringBuffer.toString().trim();
  }
  
  public static final String classOrInterface(int paramInt) { return ((paramInt & 0x200) != 0) ? "interface" : "class"; }
  
  public static final String codeToString(byte[] paramArrayOfByte, ConstantPool paramConstantPool, int paramInt1, int paramInt2, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer(paramArrayOfByte.length * 20);
    ByteSequence byteSequence = new ByteSequence(paramArrayOfByte);
    try {
      byte b;
      for (b = 0; b < paramInt1; b++)
        codeToString(byteSequence, paramConstantPool, paramBoolean); 
      for (b = 0; byteSequence.available() > 0; b++) {
        if (paramInt2 < 0 || b < paramInt2) {
          String str = fillup(byteSequence.getIndex() + ":", 6, true, ' ');
          stringBuffer.append(str + codeToString(byteSequence, paramConstantPool, paramBoolean) + '\n');
        } 
      } 
    } catch (IOException iOException) {
      System.out.println(stringBuffer.toString());
      iOException.printStackTrace();
      throw new ClassFormatException("Byte code error: " + iOException);
    } 
    return stringBuffer.toString();
  }
  
  public static final String codeToString(byte[] paramArrayOfByte, ConstantPool paramConstantPool, int paramInt1, int paramInt2) { return codeToString(paramArrayOfByte, paramConstantPool, paramInt1, paramInt2, true); }
  
  public static final String codeToString(ByteSequence paramByteSequence, ConstantPool paramConstantPool, boolean paramBoolean) throws IOException {
    int i5;
    int i4;
    int i3;
    int[] arrayOfInt2;
    int[] arrayOfInt1;
    byte b;
    int i1;
    int n;
    int m;
    int k;
    int j;
    short s = (short)paramByteSequence.readUnsignedByte();
    int i = 0;
    int i2 = 0;
    StringBuffer stringBuffer = new StringBuffer(Constants.OPCODE_NAMES[s]);
    if (s == 170 || s == 171) {
      int i6 = paramByteSequence.getIndex() % 4;
      i2 = (i6 == 0) ? 0 : (4 - i6);
      for (byte b1 = 0; b1 < i2; b1++) {
        byte b2;
        if ((b2 = paramByteSequence.readByte()) != 0)
          System.err.println("Warning: Padding byte != 0 in " + Constants.OPCODE_NAMES[s] + ":" + b2); 
      } 
      i = paramByteSequence.readInt();
    } 
    switch (s) {
      case 170:
        j = paramByteSequence.readInt();
        k = paramByteSequence.readInt();
        i3 = paramByteSequence.getIndex() - 12 - i2 - 1;
        i += i3;
        stringBuffer.append("\tdefault = " + i + ", low = " + j + ", high = " + k + "(");
        arrayOfInt2 = new int[k - j + 1];
        for (i4 = 0; i4 < arrayOfInt2.length; i4++) {
          arrayOfInt2[i4] = i3 + paramByteSequence.readInt();
          stringBuffer.append(arrayOfInt2[i4]);
          if (i4 < arrayOfInt2.length - 1)
            stringBuffer.append(", "); 
        } 
        stringBuffer.append(")");
        return stringBuffer.toString();
      case 171:
        m = paramByteSequence.readInt();
        i3 = paramByteSequence.getIndex() - 8 - i2 - 1;
        arrayOfInt1 = new int[m];
        arrayOfInt2 = new int[m];
        i += i3;
        stringBuffer.append("\tdefault = " + i + ", npairs = " + m + " (");
        for (i4 = 0; i4 < m; i4++) {
          arrayOfInt1[i4] = paramByteSequence.readInt();
          arrayOfInt2[i4] = i3 + paramByteSequence.readInt();
          stringBuffer.append("(" + arrayOfInt1[i4] + ", " + arrayOfInt2[i4] + ")");
          if (i4 < m - 1)
            stringBuffer.append(", "); 
        } 
        stringBuffer.append(")");
        return stringBuffer.toString();
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 167:
      case 168:
      case 198:
      case 199:
        stringBuffer.append("\t\t#" + (paramByteSequence.getIndex() - 1 + paramByteSequence.readShort()));
        return stringBuffer.toString();
      case 200:
      case 201:
        stringBuffer.append("\t\t#" + (paramByteSequence.getIndex() - 1 + paramByteSequence.readInt()));
        return stringBuffer.toString();
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 169:
        if (wide) {
          i1 = paramByteSequence.readUnsignedShort();
          wide = false;
        } else {
          i1 = paramByteSequence.readUnsignedByte();
        } 
        stringBuffer.append("\t\t%" + i1);
        return stringBuffer.toString();
      case 196:
        wide = true;
        stringBuffer.append("\t(wide)");
        return stringBuffer.toString();
      case 188:
        stringBuffer.append("\t\t<" + Constants.TYPE_NAMES[paramByteSequence.readByte()] + ">");
        return stringBuffer.toString();
      case 178:
      case 179:
      case 180:
      case 181:
        n = paramByteSequence.readUnsignedShort();
        stringBuffer.append("\t\t" + paramConstantPool.constantToString(n, (byte)9) + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 187:
      case 192:
        stringBuffer.append("\t");
      case 193:
        n = paramByteSequence.readUnsignedShort();
        stringBuffer.append("\t<" + paramConstantPool.constantToString(n, (byte)7) + ">" + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 182:
      case 183:
      case 184:
        n = paramByteSequence.readUnsignedShort();
        stringBuffer.append("\t" + paramConstantPool.constantToString(n, (byte)10) + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 185:
        n = paramByteSequence.readUnsignedShort();
        i4 = paramByteSequence.readUnsignedByte();
        stringBuffer.append("\t" + paramConstantPool.constantToString(n, (byte)11) + (paramBoolean ? (" (" + n + ")\t") : "") + i4 + "\t" + paramByteSequence.readUnsignedByte());
        return stringBuffer.toString();
      case 19:
      case 20:
        n = paramByteSequence.readUnsignedShort();
        stringBuffer.append("\t\t" + paramConstantPool.constantToString(n, paramConstantPool.getConstant(n).getTag()) + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 18:
        n = paramByteSequence.readUnsignedByte();
        stringBuffer.append("\t\t" + paramConstantPool.constantToString(n, paramConstantPool.getConstant(n).getTag()) + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 189:
        n = paramByteSequence.readUnsignedShort();
        stringBuffer.append("\t\t<" + compactClassName(paramConstantPool.getConstantString(n, (byte)7), false) + ">" + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 197:
        n = paramByteSequence.readUnsignedShort();
        i5 = paramByteSequence.readUnsignedByte();
        stringBuffer.append("\t<" + compactClassName(paramConstantPool.getConstantString(n, (byte)7), false) + ">\t" + i5 + (paramBoolean ? (" (" + n + ")") : ""));
        return stringBuffer.toString();
      case 132:
        if (wide) {
          i1 = paramByteSequence.readUnsignedShort();
          b = paramByteSequence.readShort();
          wide = false;
        } else {
          i1 = paramByteSequence.readUnsignedByte();
          b = paramByteSequence.readByte();
        } 
        stringBuffer.append("\t\t%" + i1 + "\t" + b);
        return stringBuffer.toString();
    } 
    if (Constants.NO_OF_OPERANDS[s] > 0)
      for (i5 = 0; i5 < Constants.TYPE_OF_OPERANDS[s].length; i5++) {
        stringBuffer.append("\t\t");
        switch (Constants.TYPE_OF_OPERANDS[s][i5]) {
          case 8:
            stringBuffer.append(paramByteSequence.readByte());
            break;
          case 9:
            stringBuffer.append(paramByteSequence.readShort());
            break;
          case 10:
            stringBuffer.append(paramByteSequence.readInt());
            break;
          default:
            System.err.println("Unreachable default case reached!");
            stringBuffer.setLength(0);
            break;
        } 
      }  
    return stringBuffer.toString();
  }
  
  public static final String codeToString(ByteSequence paramByteSequence, ConstantPool paramConstantPool) throws IOException { return codeToString(paramByteSequence, paramConstantPool, true); }
  
  public static final String compactClassName(String paramString) { return compactClassName(paramString, true); }
  
  public static final String compactClassName(String paramString1, String paramString2, boolean paramBoolean) {
    int i = paramString2.length();
    paramString1 = paramString1.replace('/', '.');
    if (paramBoolean && paramString1.startsWith(paramString2) && paramString1.substring(i).indexOf('.') == -1)
      paramString1 = paramString1.substring(i); 
    return paramString1;
  }
  
  public static final String compactClassName(String paramString, boolean paramBoolean) { return compactClassName(paramString, "java.lang.", paramBoolean); }
  
  private static final boolean is_digit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private static final boolean is_space(char paramChar) { return (paramChar == ' ' || paramChar == '\t' || paramChar == '\r' || paramChar == '\n'); }
  
  public static final int setBit(int paramInt1, int paramInt2) { return paramInt1 | pow2(paramInt2); }
  
  public static final int clearBit(int paramInt1, int paramInt2) {
    int i = pow2(paramInt2);
    return ((paramInt1 & i) == 0) ? paramInt1 : (paramInt1 ^ i);
  }
  
  public static final boolean isSet(int paramInt1, int paramInt2) { return ((paramInt1 & pow2(paramInt2)) != 0); }
  
  public static final String methodTypeToSignature(String paramString, String[] paramArrayOfString) throws ClassFormatException {
    StringBuffer stringBuffer = new StringBuffer("(");
    if (paramArrayOfString != null)
      for (byte b = 0; b < paramArrayOfString.length; b++) {
        String str1 = getSignature(paramArrayOfString[b]);
        if (str1.endsWith("V"))
          throw new ClassFormatException("Invalid type: " + paramArrayOfString[b]); 
        stringBuffer.append(str1);
      }  
    String str = getSignature(paramString);
    stringBuffer.append(")" + str);
    return stringBuffer.toString();
  }
  
  public static final String[] methodSignatureArgumentTypes(String paramString) throws ClassFormatException { return methodSignatureArgumentTypes(paramString, true); }
  
  public static final String[] methodSignatureArgumentTypes(String paramString, boolean paramBoolean) throws ClassFormatException {
    ArrayList arrayList = new ArrayList();
    try {
      if (paramString.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + paramString); 
      for (int i = 1; paramString.charAt(i) != ')'; i += consumed_chars)
        arrayList.add(signatureToString(paramString.substring(i), paramBoolean)); 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
    String[] arrayOfString = new String[arrayList.size()];
    arrayList.toArray(arrayOfString);
    return arrayOfString;
  }
  
  public static final String methodSignatureReturnType(String paramString) { return methodSignatureReturnType(paramString, true); }
  
  public static final String methodSignatureReturnType(String paramString, boolean paramBoolean) {
    String str;
    try {
      int i = paramString.lastIndexOf(')') + 1;
      str = signatureToString(paramString.substring(i), paramBoolean);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
    return str;
  }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3) { return methodSignatureToString(paramString1, paramString2, paramString3, true); }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3, boolean paramBoolean) { return methodSignatureToString(paramString1, paramString2, paramString3, paramBoolean, null); }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3, boolean paramBoolean, LocalVariableTable paramLocalVariableTable) throws ClassFormatException {
    String str;
    StringBuffer stringBuffer = new StringBuffer("(");
    byte b = (paramString3.indexOf("static") >= 0) ? 0 : 1;
    try {
      if (paramString1.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + paramString1); 
      int i;
      for (i = 1; paramString1.charAt(i) != ')'; i += consumed_chars) {
        String str1 = signatureToString(paramString1.substring(i), paramBoolean);
        stringBuffer.append(str1);
        if (paramLocalVariableTable != null) {
          LocalVariable localVariable = paramLocalVariableTable.getLocalVariable(b);
          if (localVariable != null)
            stringBuffer.append(" " + localVariable.getName()); 
        } else {
          stringBuffer.append(" arg" + b);
        } 
        if ("double".equals(str1) || "long".equals(str1)) {
          b += 2;
        } else {
          b++;
        } 
        stringBuffer.append(", ");
      } 
      str = signatureToString(paramString1.substring(++i), paramBoolean);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString1);
    } 
    if (stringBuffer.length() > 1)
      stringBuffer.setLength(stringBuffer.length() - 2); 
    stringBuffer.append(")");
    return paramString3 + ((paramString3.length() > 0) ? " " : "") + str + " " + paramString2 + stringBuffer.toString();
  }
  
  private static final int pow2(int paramInt) { return 1 << paramInt; }
  
  public static final String replace(String paramString1, String paramString2, String paramString3) {
    StringBuffer stringBuffer = new StringBuffer();
    try {
      int i;
      if ((i = paramString1.indexOf(paramString2)) != -1) {
        int j;
        for (j = 0; (i = paramString1.indexOf(paramString2, j)) != -1; j = i + paramString2.length()) {
          stringBuffer.append(paramString1.substring(j, i));
          stringBuffer.append(paramString3);
        } 
        stringBuffer.append(paramString1.substring(j));
        paramString1 = stringBuffer.toString();
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      System.err.println(stringIndexOutOfBoundsException);
    } 
    return paramString1;
  }
  
  public static final String signatureToString(String paramString) { return signatureToString(paramString, true); }
  
  public static final String signatureToString(String paramString, boolean paramBoolean) {
    consumed_chars = 1;
    try {
      int j;
      String str;
      StringBuffer stringBuffer;
      int i;
      switch (paramString.charAt(0)) {
        case 'B':
          return "byte";
        case 'C':
          return "char";
        case 'D':
          return "double";
        case 'F':
          return "float";
        case 'I':
          return "int";
        case 'J':
          return "long";
        case 'L':
          i = paramString.indexOf(';');
          if (i < 0)
            throw new ClassFormatException("Invalid signature: " + paramString); 
          consumed_chars = i + 1;
          return compactClassName(paramString.substring(1, i), paramBoolean);
        case 'S':
          return "short";
        case 'Z':
          return "boolean";
        case '[':
          stringBuffer = new StringBuffer();
          for (i = 0; paramString.charAt(i) == '['; i++)
            stringBuffer.append("[]"); 
          j = i;
          str = signatureToString(paramString.substring(i), paramBoolean);
          consumed_chars += j;
          return str + stringBuffer.toString();
        case 'V':
          return "void";
      } 
      throw new ClassFormatException("Invalid signature: `" + paramString + "'");
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid signature: " + stringIndexOutOfBoundsException + ":" + paramString);
    } 
  }
  
  public static String getSignature(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    char[] arrayOfChar = paramString.toCharArray();
    boolean bool1 = false;
    boolean bool2 = false;
    byte b = -1;
    int i;
    for (i = 0; i < arrayOfChar.length; i++) {
      switch (arrayOfChar[i]) {
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case ' ':
          if (bool1)
            bool2 = true; 
          break;
        case '[':
          if (!bool1)
            throw new RuntimeException("Illegal type: " + paramString); 
          b = i;
          break;
        default:
          bool1 = true;
          if (!bool2)
            stringBuffer.append(arrayOfChar[i]); 
          break;
      } 
    } 
    i = 0;
    if (b > 0)
      i = countBrackets(paramString.substring(b)); 
    paramString = stringBuffer.toString();
    stringBuffer.setLength(0);
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      stringBuffer.append('['); 
    b1 = 0;
    for (byte b2 = 4; b2 <= 12 && b1 == 0; b2++) {
      if (Constants.TYPE_NAMES[b2].equals(paramString)) {
        b1 = 1;
        stringBuffer.append(Constants.SHORT_TYPE_NAMES[b2]);
      } 
    } 
    if (b1 == 0)
      stringBuffer.append('L' + paramString.replace('.', '/') + ';'); 
    return stringBuffer.toString();
  }
  
  private static int countBrackets(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    byte b1 = 0;
    boolean bool = false;
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      switch (arrayOfChar[b2]) {
        case '[':
          if (bool)
            throw new RuntimeException("Illegally nested brackets:" + paramString); 
          bool = true;
          break;
        case ']':
          if (!bool)
            throw new RuntimeException("Illegally nested brackets:" + paramString); 
          bool = false;
          b1++;
          break;
      } 
    } 
    if (bool)
      throw new RuntimeException("Illegally nested brackets:" + paramString); 
    return b1;
  }
  
  public static final byte typeOfMethodSignature(String paramString) throws ClassFormatException {
    try {
      if (paramString.charAt(0) != '(')
        throw new ClassFormatException("Invalid method signature: " + paramString); 
      int i = paramString.lastIndexOf(')') + 1;
      return typeOfSignature(paramString.substring(i));
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
  }
  
  public static final byte typeOfSignature(String paramString) throws ClassFormatException {
    try {
      switch (paramString.charAt(0)) {
        case 'B':
          return 8;
        case 'C':
          return 5;
        case 'D':
          return 7;
        case 'F':
          return 6;
        case 'I':
          return 10;
        case 'J':
          return 11;
        case 'L':
          return 14;
        case '[':
          return 13;
        case 'V':
          return 12;
        case 'Z':
          return 4;
        case 'S':
          return 9;
      } 
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    } 
  }
  
  public static short searchOpcode(String paramString) {
    paramString = paramString.toLowerCase();
    for (short s = 0; s < Constants.OPCODE_NAMES.length; s = (short)(s + 1)) {
      if (Constants.OPCODE_NAMES[s].equals(paramString))
        return s; 
    } 
    return -1;
  }
  
  private static final short byteToShort(byte paramByte) { return (paramByte < 0) ? (short)('Ā' + paramByte) : (short)paramByte; }
  
  public static final String toHexString(byte[] paramArrayOfByte) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      short s = byteToShort(paramArrayOfByte[b]);
      String str = Integer.toString(s, 16);
      if (s < 16)
        stringBuffer.append('0'); 
      stringBuffer.append(str);
      if (b < paramArrayOfByte.length - 1)
        stringBuffer.append(' '); 
    } 
    return stringBuffer.toString();
  }
  
  public static final String format(int paramInt1, int paramInt2, boolean paramBoolean, char paramChar) { return fillup(Integer.toString(paramInt1), paramInt2, paramBoolean, paramChar); }
  
  public static final String fillup(String paramString, int paramInt, boolean paramBoolean, char paramChar) {
    int i = paramInt - paramString.length();
    char[] arrayOfChar = new char[(i < 0) ? 0 : i];
    for (byte b = 0; b < arrayOfChar.length; b++)
      arrayOfChar[b] = paramChar; 
    return paramBoolean ? (paramString + new String(arrayOfChar)) : (new String(arrayOfChar) + paramString);
  }
  
  static final boolean equals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    int i;
    if ((i = paramArrayOfByte1.length) != paramArrayOfByte2.length)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfByte1[b] != paramArrayOfByte2[b])
        return false; 
    } 
    return true;
  }
  
  public static final void printArray(PrintStream paramPrintStream, Object[] paramArrayOfObject) { paramPrintStream.println(printArray(paramArrayOfObject, true)); }
  
  public static final void printArray(PrintWriter paramPrintWriter, Object[] paramArrayOfObject) { paramPrintWriter.println(printArray(paramArrayOfObject, true)); }
  
  public static final String printArray(Object[] paramArrayOfObject) { return printArray(paramArrayOfObject, true); }
  
  public static final String printArray(Object[] paramArrayOfObject, boolean paramBoolean) { return printArray(paramArrayOfObject, paramBoolean, false); }
  
  public static final String printArray(Object[] paramArrayOfObject, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramArrayOfObject == null)
      return null; 
    StringBuffer stringBuffer = new StringBuffer();
    if (paramBoolean1)
      stringBuffer.append('{'); 
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] != null) {
        stringBuffer.append((paramBoolean2 ? "\"" : "") + paramArrayOfObject[b].toString() + (paramBoolean2 ? "\"" : ""));
      } else {
        stringBuffer.append("null");
      } 
      if (b < paramArrayOfObject.length - 1)
        stringBuffer.append(", "); 
    } 
    if (paramBoolean1)
      stringBuffer.append('}'); 
    return stringBuffer.toString();
  }
  
  public static boolean isJavaIdentifierPart(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z') || (paramChar >= '0' && paramChar <= '9') || paramChar == '_'); }
  
  public static String encode(byte[] paramArrayOfByte, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      gZIPOutputStream.write(paramArrayOfByte, 0, paramArrayOfByte.length);
      gZIPOutputStream.close();
      byteArrayOutputStream.close();
      paramArrayOfByte = byteArrayOutputStream.toByteArray();
    } 
    CharArrayWriter charArrayWriter = new CharArrayWriter();
    JavaWriter javaWriter = new JavaWriter(charArrayWriter);
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      byte b1 = paramArrayOfByte[b] & 0xFF;
      javaWriter.write(b1);
    } 
    return charArrayWriter.toString();
  }
  
  public static byte[] decode(String paramString, boolean paramBoolean) throws IOException {
    char[] arrayOfChar = paramString.toCharArray();
    CharArrayReader charArrayReader = new CharArrayReader(arrayOfChar);
    JavaReader javaReader = new JavaReader(charArrayReader);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int i;
    while ((i = javaReader.read()) >= 0)
      byteArrayOutputStream.write(i); 
    byteArrayOutputStream.close();
    charArrayReader.close();
    javaReader.close();
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    if (paramBoolean) {
      GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte));
      byte[] arrayOfByte1 = new byte[arrayOfByte.length * 3];
      byte b = 0;
      int j;
      while ((j = gZIPInputStream.read()) >= 0)
        arrayOfByte1[b++] = (byte)j; 
      arrayOfByte = new byte[b];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte, 0, b);
    } 
    return arrayOfByte;
  }
  
  public static final String convertString(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      switch (arrayOfChar[b]) {
        case '\n':
          stringBuffer.append("\\n");
          break;
        case '\r':
          stringBuffer.append("\\r");
          break;
        case '"':
          stringBuffer.append("\\\"");
          break;
        case '\'':
          stringBuffer.append("\\'");
          break;
        case '\\':
          stringBuffer.append("\\\\");
          break;
        default:
          stringBuffer.append(arrayOfChar[b]);
          break;
      } 
    } 
    return stringBuffer.toString();
  }
  
  static  {
    byte b1 = 0;
    boolean bool = false;
    byte b2;
    for (b2 = 65; b2 <= 90; b2++) {
      CHAR_MAP[b1] = b2;
      MAP_CHAR[b2] = b1;
      b1++;
    } 
    for (b2 = 103; b2 <= 122; b2++) {
      CHAR_MAP[b1] = b2;
      MAP_CHAR[b2] = b1;
      b1++;
    } 
    CHAR_MAP[b1] = 36;
    MAP_CHAR[36] = b1;
    CHAR_MAP[++b1] = 95;
    MAP_CHAR[95] = b1;
  }
  
  private static class JavaReader extends FilterReader {
    public JavaReader(Reader param1Reader) { super(param1Reader); }
    
    public int read() throws IOException {
      int i = this.in.read();
      if (i != 36)
        return i; 
      int j = this.in.read();
      if (j < 0)
        return -1; 
      if ((j >= 48 && j <= 57) || (j >= 97 && j <= 102)) {
        int k = this.in.read();
        if (k < 0)
          return -1; 
        char[] arrayOfChar = { (char)j, (char)k };
        return Integer.parseInt(new String(arrayOfChar), 16);
      } 
      return MAP_CHAR[j];
    }
    
    public int read(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      for (int i = 0; i < param1Int2; i++)
        param1ArrayOfChar[param1Int1 + i] = (char)read(); 
      return param1Int2;
    }
  }
  
  private static class JavaWriter extends FilterWriter {
    public JavaWriter(Writer param1Writer) { super(param1Writer); }
    
    public void write(int param1Int) throws IOException {
      if (Utility.isJavaIdentifierPart((char)param1Int) && param1Int != 36) {
        this.out.write(param1Int);
      } else {
        this.out.write(36);
        if (param1Int >= 0 && param1Int < 48) {
          this.out.write(CHAR_MAP[param1Int]);
        } else {
          char[] arrayOfChar = Integer.toHexString(param1Int).toCharArray();
          if (arrayOfChar.length == 1) {
            this.out.write(48);
            this.out.write(arrayOfChar[0]);
          } else {
            this.out.write(arrayOfChar[0]);
            this.out.write(arrayOfChar[1]);
          } 
        } 
      } 
    }
    
    public void write(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws IOException {
      for (int i = 0; i < param1Int2; i++)
        write(param1ArrayOfChar[param1Int1 + i]); 
    }
    
    public void write(String param1String, int param1Int1, int param1Int2) throws IOException { write(param1String.toCharArray(), param1Int1, param1Int2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */