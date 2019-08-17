package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class Base64 {
  public static final int BASE64DEFAULTLENGTH = 76;
  
  private static final int BASELENGTH = 255;
  
  private static final int LOOKUPLENGTH = 64;
  
  private static final int TWENTYFOURBITGROUP = 24;
  
  private static final int EIGHTBIT = 8;
  
  private static final int SIXTEENBIT = 16;
  
  private static final int FOURBYTE = 4;
  
  private static final int SIGN = -128;
  
  private static final char PAD = '=';
  
  private static final byte[] base64Alphabet = new byte[255];
  
  private static final char[] lookUpBase64Alphabet = new char[64];
  
  static final byte[] getBytes(BigInteger paramBigInteger, int paramInt) {
    paramInt = paramInt + 7 >> 3 << 3;
    if (paramInt < paramBigInteger.bitLength())
      throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength")); 
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (paramBigInteger.bitLength() % 8 != 0 && paramBigInteger.bitLength() / 8 + 1 == paramInt / 8)
      return arrayOfByte1; 
    byte b = 0;
    int i = arrayOfByte1.length;
    if (paramBigInteger.bitLength() % 8 == 0) {
      b = 1;
      i--;
    } 
    int j = paramInt / 8 - i;
    byte[] arrayOfByte2 = new byte[paramInt / 8];
    System.arraycopy(arrayOfByte1, b, arrayOfByte2, j, i);
    return arrayOfByte2;
  }
  
  public static final String encode(BigInteger paramBigInteger) { return encode(getBytes(paramBigInteger, paramBigInteger.bitLength())); }
  
  public static final byte[] encode(BigInteger paramBigInteger, int paramInt) {
    paramInt = paramInt + 7 >> 3 << 3;
    if (paramInt < paramBigInteger.bitLength())
      throw new IllegalArgumentException(I18n.translate("utils.Base64.IllegalBitlength")); 
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    if (paramBigInteger.bitLength() % 8 != 0 && paramBigInteger.bitLength() / 8 + 1 == paramInt / 8)
      return arrayOfByte1; 
    byte b = 0;
    int i = arrayOfByte1.length;
    if (paramBigInteger.bitLength() % 8 == 0) {
      b = 1;
      i--;
    } 
    int j = paramInt / 8 - i;
    byte[] arrayOfByte2 = new byte[paramInt / 8];
    System.arraycopy(arrayOfByte1, b, arrayOfByte2, j, i);
    return arrayOfByte2;
  }
  
  public static final BigInteger decodeBigIntegerFromElement(Element paramElement) throws Base64DecodingException { return new BigInteger(1, decode(paramElement)); }
  
  public static final BigInteger decodeBigIntegerFromText(Text paramText) throws Base64DecodingException { return new BigInteger(1, decode(paramText.getData())); }
  
  public static final void fillElementWithBigInteger(Element paramElement, BigInteger paramBigInteger) {
    String str = encode(paramBigInteger);
    if (!XMLUtils.ignoreLineBreaks() && str.length() > 76)
      str = "\n" + str + "\n"; 
    Document document = paramElement.getOwnerDocument();
    Text text = document.createTextNode(str);
    paramElement.appendChild(text);
  }
  
  public static final byte[] decode(Element paramElement) throws Base64DecodingException {
    Node node = paramElement.getFirstChild();
    StringBuffer stringBuffer = new StringBuffer();
    while (node != null) {
      if (node.getNodeType() == 3) {
        Text text = (Text)node;
        stringBuffer.append(text.getData());
      } 
      node = node.getNextSibling();
    } 
    return decode(stringBuffer.toString());
  }
  
  public static final Element encodeToElement(Document paramDocument, String paramString, byte[] paramArrayOfByte) {
    Element element = XMLUtils.createElementInSignatureSpace(paramDocument, paramString);
    Text text = paramDocument.createTextNode(encode(paramArrayOfByte));
    element.appendChild(text);
    return element;
  }
  
  public static final byte[] decode(byte[] paramArrayOfByte) throws Base64DecodingException { return decodeInternal(paramArrayOfByte, -1); }
  
  public static final String encode(byte[] paramArrayOfByte) { return XMLUtils.ignoreLineBreaks() ? encode(paramArrayOfByte, 2147483647) : encode(paramArrayOfByte, 76); }
  
  public static final byte[] decode(BufferedReader paramBufferedReader) throws IOException, Base64DecodingException {
    byte[] arrayOfByte = null;
    unsyncByteArrayOutputStream = null;
    try {
      unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
      String str;
      while (null != (str = paramBufferedReader.readLine())) {
        byte[] arrayOfByte1 = decode(str);
        unsyncByteArrayOutputStream.write(arrayOfByte1);
      } 
      arrayOfByte = unsyncByteArrayOutputStream.toByteArray();
    } finally {
      unsyncByteArrayOutputStream.close();
    } 
    return arrayOfByte;
  }
  
  protected static final boolean isWhiteSpace(byte paramByte) { return (paramByte == 32 || paramByte == 13 || paramByte == 10 || paramByte == 9); }
  
  protected static final boolean isPad(byte paramByte) { return (paramByte == 61); }
  
  public static final String encode(byte[] paramArrayOfByte, int paramInt) {
    if (paramInt < 4)
      paramInt = Integer.MAX_VALUE; 
    if (paramArrayOfByte == null)
      return null; 
    int i = paramArrayOfByte.length * 8;
    if (i == 0)
      return ""; 
    int j = i % 24;
    int k = i / 24;
    int m = (j != 0) ? (k + 1) : k;
    int n = paramInt / 4;
    int i1 = (m - 1) / n;
    char[] arrayOfChar = null;
    arrayOfChar = new char[m * 4 + i1];
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    byte b5 = 0;
    byte b6 = 0;
    byte b7 = 0;
    byte b8 = 0;
    byte b9;
    for (b9 = 0; b9 < i1; b9++) {
      for (byte b = 0; b < 19; b++) {
        b3 = paramArrayOfByte[b7++];
        b4 = paramArrayOfByte[b7++];
        b5 = paramArrayOfByte[b7++];
        b2 = (byte)(b4 & 0xF);
        b1 = (byte)(b3 & 0x3);
        byte b10 = ((b3 & 0xFFFFFF80) == 0) ? (byte)(b3 >> 2) : (byte)(b3 >> 2 ^ 0xC0);
        byte b11 = ((b4 & 0xFFFFFF80) == 0) ? (byte)(b4 >> 4) : (byte)(b4 >> 4 ^ 0xF0);
        byte b12 = ((b5 & 0xFFFFFF80) == 0) ? (byte)(b5 >> 6) : (byte)(b5 >> 6 ^ 0xFC);
        arrayOfChar[b6++] = lookUpBase64Alphabet[b10];
        arrayOfChar[b6++] = lookUpBase64Alphabet[b11 | b1 << 4];
        arrayOfChar[b6++] = lookUpBase64Alphabet[b2 << 2 | b12];
        arrayOfChar[b6++] = lookUpBase64Alphabet[b5 & 0x3F];
        b8++;
      } 
      arrayOfChar[b6++] = '\n';
    } 
    while (b8 < k) {
      b3 = paramArrayOfByte[b7++];
      b4 = paramArrayOfByte[b7++];
      b5 = paramArrayOfByte[b7++];
      b2 = (byte)(b4 & 0xF);
      b1 = (byte)(b3 & 0x3);
      b9 = ((b3 & 0xFFFFFF80) == 0) ? (byte)(b3 >> 2) : (byte)(b3 >> 2 ^ 0xC0);
      byte b10 = ((b4 & 0xFFFFFF80) == 0) ? (byte)(b4 >> 4) : (byte)(b4 >> 4 ^ 0xF0);
      byte b11 = ((b5 & 0xFFFFFF80) == 0) ? (byte)(b5 >> 6) : (byte)(b5 >> 6 ^ 0xFC);
      arrayOfChar[b6++] = lookUpBase64Alphabet[b9];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b10 | b1 << 4];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b2 << 2 | b11];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b5 & 0x3F];
      b8++;
    } 
    if (j == 8) {
      b3 = paramArrayOfByte[b7];
      b1 = (byte)(b3 & 0x3);
      b9 = ((b3 & 0xFFFFFF80) == 0) ? (byte)(b3 >> 2) : (byte)(b3 >> 2 ^ 0xC0);
      arrayOfChar[b6++] = lookUpBase64Alphabet[b9];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b1 << 4];
      arrayOfChar[b6++] = '=';
      arrayOfChar[b6++] = '=';
    } else if (j == 16) {
      b3 = paramArrayOfByte[b7];
      b4 = paramArrayOfByte[b7 + 1];
      b2 = (byte)(b4 & 0xF);
      b1 = (byte)(b3 & 0x3);
      b9 = ((b3 & 0xFFFFFF80) == 0) ? (byte)(b3 >> 2) : (byte)(b3 >> 2 ^ 0xC0);
      byte b = ((b4 & 0xFFFFFF80) == 0) ? (byte)(b4 >> 4) : (byte)(b4 >> 4 ^ 0xF0);
      arrayOfChar[b6++] = lookUpBase64Alphabet[b9];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b | b1 << 4];
      arrayOfChar[b6++] = lookUpBase64Alphabet[b2 << 2];
      arrayOfChar[b6++] = '=';
    } 
    return new String(arrayOfChar);
  }
  
  public static final byte[] decode(String paramString) throws Base64DecodingException {
    if (paramString == null)
      return null; 
    byte[] arrayOfByte = new byte[paramString.length()];
    int i = getBytesInternal(paramString, arrayOfByte);
    return decodeInternal(arrayOfByte, i);
  }
  
  protected static final int getBytesInternal(String paramString, byte[] paramArrayOfByte) {
    int i = paramString.length();
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      byte b = (byte)paramString.charAt(b2);
      if (!isWhiteSpace(b))
        paramArrayOfByte[b1++] = b; 
    } 
    return b1;
  }
  
  protected static final byte[] decodeInternal(byte[] paramArrayOfByte, int paramInt) throws Base64DecodingException {
    if (paramInt == -1)
      paramInt = removeWhiteSpace(paramArrayOfByte); 
    if (paramInt % 4 != 0)
      throw new Base64DecodingException("decoding.divisible.four"); 
    int i = paramInt / 4;
    if (i == 0)
      return new byte[0]; 
    byte[] arrayOfByte = null;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    m = (i - 1) * 4;
    k = (i - 1) * 3;
    b1 = base64Alphabet[paramArrayOfByte[m++]];
    b2 = base64Alphabet[paramArrayOfByte[m++]];
    if (b1 == -1 || b2 == -1)
      throw new Base64DecodingException("decoding.general"); 
    byte b5;
    b3 = base64Alphabet[b5 = paramArrayOfByte[m++]];
    byte b6;
    b4 = base64Alphabet[b6 = paramArrayOfByte[m++]];
    if (b3 == -1 || b4 == -1) {
      if (isPad(b5) && isPad(b6)) {
        if ((b2 & 0xF) != 0)
          throw new Base64DecodingException("decoding.general"); 
        arrayOfByte = new byte[k + 1];
        arrayOfByte[k] = (byte)(b1 << 2 | b2 >> 4);
      } else if (!isPad(b5) && isPad(b6)) {
        if ((b3 & 0x3) != 0)
          throw new Base64DecodingException("decoding.general"); 
        arrayOfByte = new byte[k + 2];
        arrayOfByte[k++] = (byte)(b1 << 2 | b2 >> 4);
        arrayOfByte[k] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
      } else {
        throw new Base64DecodingException("decoding.general");
      } 
    } else {
      arrayOfByte = new byte[k + 3];
      arrayOfByte[k++] = (byte)(b1 << 2 | b2 >> 4);
      arrayOfByte[k++] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
      arrayOfByte[k++] = (byte)(b3 << 6 | b4);
    } 
    k = 0;
    m = 0;
    for (j = i - 1; j > 0; j--) {
      b1 = base64Alphabet[paramArrayOfByte[m++]];
      b2 = base64Alphabet[paramArrayOfByte[m++]];
      b3 = base64Alphabet[paramArrayOfByte[m++]];
      b4 = base64Alphabet[paramArrayOfByte[m++]];
      if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1)
        throw new Base64DecodingException("decoding.general"); 
      arrayOfByte[k++] = (byte)(b1 << 2 | b2 >> 4);
      arrayOfByte[k++] = (byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF);
      arrayOfByte[k++] = (byte)(b3 << 6 | b4);
    } 
    return arrayOfByte;
  }
  
  public static final void decode(String paramString, OutputStream paramOutputStream) throws Base64DecodingException, IOException {
    byte[] arrayOfByte = new byte[paramString.length()];
    int i = getBytesInternal(paramString, arrayOfByte);
    decode(arrayOfByte, paramOutputStream, i);
  }
  
  public static final void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream) throws Base64DecodingException, IOException { decode(paramArrayOfByte, paramOutputStream, -1); }
  
  protected static final void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream, int paramInt) throws Base64DecodingException, IOException {
    if (paramInt == -1)
      paramInt = removeWhiteSpace(paramArrayOfByte); 
    if (paramInt % 4 != 0)
      throw new Base64DecodingException("decoding.divisible.four"); 
    int i = paramInt / 4;
    if (i == 0)
      return; 
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    int j = 0;
    byte b = 0;
    for (j = i - 1; j > 0; j--) {
      b1 = base64Alphabet[paramArrayOfByte[b++]];
      b2 = base64Alphabet[paramArrayOfByte[b++]];
      b3 = base64Alphabet[paramArrayOfByte[b++]];
      b4 = base64Alphabet[paramArrayOfByte[b++]];
      if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1)
        throw new Base64DecodingException("decoding.general"); 
      paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      paramOutputStream.write((byte)(b3 << 6 | b4));
    } 
    b1 = base64Alphabet[paramArrayOfByte[b++]];
    b2 = base64Alphabet[paramArrayOfByte[b++]];
    if (b1 == -1 || b2 == -1)
      throw new Base64DecodingException("decoding.general"); 
    byte b5;
    b3 = base64Alphabet[b5 = paramArrayOfByte[b++]];
    byte b6;
    b4 = base64Alphabet[b6 = paramArrayOfByte[b++]];
    if (b3 == -1 || b4 == -1) {
      if (isPad(b5) && isPad(b6)) {
        if ((b2 & 0xF) != 0)
          throw new Base64DecodingException("decoding.general"); 
        paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      } else if (!isPad(b5) && isPad(b6)) {
        if ((b3 & 0x3) != 0)
          throw new Base64DecodingException("decoding.general"); 
        paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
        paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      } else {
        throw new Base64DecodingException("decoding.general");
      } 
    } else {
      paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      paramOutputStream.write((byte)(b3 << 6 | b4));
    } 
  }
  
  public static final void decode(InputStream paramInputStream, OutputStream paramOutputStream) throws Base64DecodingException, IOException {
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    byte b = 0;
    byte[] arrayOfByte = new byte[4];
    int i;
    while ((i = paramInputStream.read()) > 0) {
      byte b9 = (byte)i;
      if (isWhiteSpace(b9))
        continue; 
      if (isPad(b9)) {
        arrayOfByte[b++] = b9;
        if (b == 3)
          arrayOfByte[b++] = (byte)paramInputStream.read(); 
        break;
      } 
      arrayOfByte[b++] = b9;
      if (b9 == -1)
        throw new Base64DecodingException("decoding.general"); 
      if (b != 4)
        continue; 
      b = 0;
      b1 = base64Alphabet[arrayOfByte[0]];
      b2 = base64Alphabet[arrayOfByte[1]];
      b3 = base64Alphabet[arrayOfByte[2]];
      b4 = base64Alphabet[arrayOfByte[3]];
      paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      paramOutputStream.write((byte)(b3 << 6 | b4));
    } 
    byte b5 = arrayOfByte[0];
    byte b6 = arrayOfByte[1];
    byte b7 = arrayOfByte[2];
    byte b8 = arrayOfByte[3];
    b1 = base64Alphabet[b5];
    b2 = base64Alphabet[b6];
    b3 = base64Alphabet[b7];
    b4 = base64Alphabet[b8];
    if (b3 == -1 || b4 == -1) {
      if (isPad(b7) && isPad(b8)) {
        if ((b2 & 0xF) != 0)
          throw new Base64DecodingException("decoding.general"); 
        paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      } else if (!isPad(b7) && isPad(b8)) {
        b3 = base64Alphabet[b7];
        if ((b3 & 0x3) != 0)
          throw new Base64DecodingException("decoding.general"); 
        paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
        paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      } else {
        throw new Base64DecodingException("decoding.general");
      } 
    } else {
      paramOutputStream.write((byte)(b1 << 2 | b2 >> 4));
      paramOutputStream.write((byte)((b2 & 0xF) << 4 | b3 >> 2 & 0xF));
      paramOutputStream.write((byte)(b3 << 6 | b4));
    } 
  }
  
  protected static final int removeWhiteSpace(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return 0; 
    byte b1 = 0;
    int i = paramArrayOfByte.length;
    for (byte b2 = 0; b2 < i; b2++) {
      byte b = paramArrayOfByte[b2];
      if (!isWhiteSpace(b))
        paramArrayOfByte[b1++] = b; 
    } 
    return b1;
  }
  
  static  {
    byte b1;
    for (b1 = 0; b1 < 'ÿ'; b1++)
      base64Alphabet[b1] = -1; 
    for (b1 = 90; b1 >= 65; b1--)
      base64Alphabet[b1] = (byte)(b1 - 65); 
    for (b1 = 122; b1 >= 97; b1--)
      base64Alphabet[b1] = (byte)(b1 - 97 + 26); 
    for (b1 = 57; b1 >= 48; b1--)
      base64Alphabet[b1] = (byte)(b1 - 48 + 52); 
    base64Alphabet[43] = 62;
    base64Alphabet[47] = 63;
    for (b1 = 0; b1 <= 25; b1++)
      lookUpBase64Alphabet[b1] = (char)(65 + b1); 
    b1 = 26;
    byte b2;
    for (b2 = 0; b1 <= 51; b2++) {
      lookUpBase64Alphabet[b1] = (char)(97 + b2);
      b1++;
    } 
    b1 = 52;
    for (b2 = 0; b1 <= 61; b2++) {
      lookUpBase64Alphabet[b1] = (char)(48 + b2);
      b1++;
    } 
    lookUpBase64Alphabet[62] = '+';
    lookUpBase64Alphabet[63] = '/';
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\Base64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */