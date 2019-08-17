package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.ASCIIUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QPEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUDecoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.UUEncoderStream;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;

public class MimeUtility {
  public static final int ALL = -1;
  
  private static final int BUFFER_SIZE = 1024;
  
  private static boolean decodeStrict = true;
  
  private static boolean encodeEolStrict = false;
  
  private static boolean foldEncodedWords = false;
  
  private static boolean foldText = true;
  
  private static String defaultJavaCharset;
  
  private static String defaultMIMECharset;
  
  private static Hashtable mime2java;
  
  private static Hashtable java2mime;
  
  static final int ALL_ASCII = 1;
  
  static final int MOSTLY_ASCII = 2;
  
  static final int MOSTLY_NONASCII = 3;
  
  public static String getEncoding(DataSource paramDataSource) {
    ContentType contentType = null;
    InputStream inputStream = null;
    String str = null;
    try {
      contentType = new ContentType(paramDataSource.getContentType());
      inputStream = paramDataSource.getInputStream();
    } catch (Exception exception) {
      return "base64";
    } 
    boolean bool = contentType.match("text/*");
    int i = checkAscii(inputStream, -1, !bool);
    switch (i) {
      case 1:
        str = "7bit";
        break;
      case 2:
        str = "quoted-printable";
        break;
      default:
        str = "base64";
        break;
    } 
    try {
      inputStream.close();
    } catch (IOException iOException) {}
    return str;
  }
  
  public static String getEncoding(DataHandler paramDataHandler) {
    ContentType contentType = null;
    String str = null;
    if (paramDataHandler.getName() != null)
      return getEncoding(paramDataHandler.getDataSource()); 
    try {
      contentType = new ContentType(paramDataHandler.getContentType());
    } catch (Exception exception) {
      return "base64";
    } 
    if (contentType.match("text/*")) {
      AsciiOutputStream asciiOutputStream = new AsciiOutputStream(false, false);
      try {
        paramDataHandler.writeTo(asciiOutputStream);
      } catch (IOException iOException) {}
      switch (asciiOutputStream.getAscii()) {
        case 1:
          return "7bit";
        case 2:
          return "quoted-printable";
      } 
      str = "base64";
    } else {
      AsciiOutputStream asciiOutputStream = new AsciiOutputStream(true, encodeEolStrict);
      try {
        paramDataHandler.writeTo(asciiOutputStream);
      } catch (IOException iOException) {}
      if (asciiOutputStream.getAscii() == 1) {
        str = "7bit";
      } else {
        str = "base64";
      } 
    } 
    return str;
  }
  
  public static InputStream decode(InputStream paramInputStream, String paramString) throws MessagingException {
    if (paramString.equalsIgnoreCase("base64"))
      return new BASE64DecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("quoted-printable"))
      return new QPDecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("uuencode") || paramString.equalsIgnoreCase("x-uuencode") || paramString.equalsIgnoreCase("x-uue"))
      return new UUDecoderStream(paramInputStream); 
    if (paramString.equalsIgnoreCase("binary") || paramString.equalsIgnoreCase("7bit") || paramString.equalsIgnoreCase("8bit"))
      return paramInputStream; 
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  
  public static OutputStream encode(OutputStream paramOutputStream, String paramString) throws MessagingException {
    if (paramString == null)
      return paramOutputStream; 
    if (paramString.equalsIgnoreCase("base64"))
      return new BASE64EncoderStream(paramOutputStream); 
    if (paramString.equalsIgnoreCase("quoted-printable"))
      return new QPEncoderStream(paramOutputStream); 
    if (paramString.equalsIgnoreCase("uuencode") || paramString.equalsIgnoreCase("x-uuencode") || paramString.equalsIgnoreCase("x-uue"))
      return new UUEncoderStream(paramOutputStream); 
    if (paramString.equalsIgnoreCase("binary") || paramString.equalsIgnoreCase("7bit") || paramString.equalsIgnoreCase("8bit"))
      return paramOutputStream; 
    throw new MessagingException("Unknown encoding: " + paramString);
  }
  
  public static OutputStream encode(OutputStream paramOutputStream, String paramString1, String paramString2) throws MessagingException {
    if (paramString1 == null)
      return paramOutputStream; 
    if (paramString1.equalsIgnoreCase("base64"))
      return new BASE64EncoderStream(paramOutputStream); 
    if (paramString1.equalsIgnoreCase("quoted-printable"))
      return new QPEncoderStream(paramOutputStream); 
    if (paramString1.equalsIgnoreCase("uuencode") || paramString1.equalsIgnoreCase("x-uuencode") || paramString1.equalsIgnoreCase("x-uue"))
      return new UUEncoderStream(paramOutputStream, paramString2); 
    if (paramString1.equalsIgnoreCase("binary") || paramString1.equalsIgnoreCase("7bit") || paramString1.equalsIgnoreCase("8bit"))
      return paramOutputStream; 
    throw new MessagingException("Unknown encoding: " + paramString1);
  }
  
  public static String encodeText(String paramString) throws UnsupportedEncodingException { return encodeText(paramString, null, null); }
  
  public static String encodeText(String paramString1, String paramString2, String paramString3) throws UnsupportedEncodingException { return encodeWord(paramString1, paramString2, paramString3, false); }
  
  public static String decodeText(String paramString) throws UnsupportedEncodingException {
    String str = " \t\n\r";
    if (paramString.indexOf("=?") == -1)
      return paramString; 
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, str, true);
    StringBuffer stringBuffer1 = new StringBuffer();
    StringBuffer stringBuffer2 = new StringBuffer();
    boolean bool = false;
    while (stringTokenizer.hasMoreTokens()) {
      String str2;
      String str1 = stringTokenizer.nextToken();
      char c;
      if ((c = str1.charAt(0)) == ' ' || c == '\t' || c == '\r' || c == '\n') {
        stringBuffer2.append(c);
        continue;
      } 
      try {
        str2 = decodeWord(str1);
        if (!bool && stringBuffer2.length() > 0)
          stringBuffer1.append(stringBuffer2); 
        bool = true;
      } catch (ParseException parseException) {
        str2 = str1;
        if (!decodeStrict)
          str2 = decodeInnerWords(str2); 
        if (stringBuffer2.length() > 0)
          stringBuffer1.append(stringBuffer2); 
        bool = false;
      } 
      stringBuffer1.append(str2);
      stringBuffer2.setLength(0);
    } 
    return stringBuffer1.toString();
  }
  
  public static String encodeWord(String paramString) throws UnsupportedEncodingException { return encodeWord(paramString, null, null); }
  
  public static String encodeWord(String paramString1, String paramString2, String paramString3) throws UnsupportedEncodingException { return encodeWord(paramString1, paramString2, paramString3, true); }
  
  private static String encodeWord(String paramString1, String paramString2, String paramString3, boolean paramBoolean) throws UnsupportedEncodingException {
    boolean bool;
    String str;
    int i = checkAscii(paramString1);
    if (i == 1)
      return paramString1; 
    if (paramString2 == null) {
      str = getDefaultJavaCharset();
      paramString2 = getDefaultMIMECharset();
    } else {
      str = javaCharset(paramString2);
    } 
    if (paramString3 == null)
      if (i != 3) {
        paramString3 = "Q";
      } else {
        paramString3 = "B";
      }  
    if (paramString3.equalsIgnoreCase("B")) {
      bool = true;
    } else if (paramString3.equalsIgnoreCase("Q")) {
      bool = false;
    } else {
      throw new UnsupportedEncodingException("Unknown transfer encoding: " + paramString3);
    } 
    StringBuffer stringBuffer = new StringBuffer();
    doEncode(paramString1, bool, str, 68 - paramString2.length(), "=?" + paramString2 + "?" + paramString3 + "?", true, paramBoolean, stringBuffer);
    return stringBuffer.toString();
  }
  
  private static void doEncode(String paramString1, boolean paramBoolean1, String paramString2, int paramInt, String paramString3, boolean paramBoolean2, boolean paramBoolean3, StringBuffer paramStringBuffer) throws UnsupportedEncodingException {
    int i;
    byte[] arrayOfByte = paramString1.getBytes(paramString2);
    if (paramBoolean1) {
      i = BEncoderStream.encodedLength(arrayOfByte);
    } else {
      i = QEncoderStream.encodedLength(arrayOfByte, paramBoolean3);
    } 
    int j;
    if (i > paramInt && (j = paramString1.length()) > 1) {
      doEncode(paramString1.substring(0, j / 2), paramBoolean1, paramString2, paramInt, paramString3, paramBoolean2, paramBoolean3, paramStringBuffer);
      doEncode(paramString1.substring(j / 2, j), paramBoolean1, paramString2, paramInt, paramString3, false, paramBoolean3, paramStringBuffer);
    } else {
      QEncoderStream qEncoderStream;
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
      if (paramBoolean1) {
        qEncoderStream = new BEncoderStream(byteArrayOutputStream);
      } else {
        qEncoderStream = new QEncoderStream(byteArrayOutputStream, paramBoolean3);
      } 
      try {
        qEncoderStream.write(arrayOfByte);
        qEncoderStream.close();
      } catch (IOException iOException) {}
      byte[] arrayOfByte1 = byteArrayOutputStream.toByteArray();
      if (!paramBoolean2)
        if (foldEncodedWords) {
          paramStringBuffer.append("\r\n ");
        } else {
          paramStringBuffer.append(" ");
        }  
      paramStringBuffer.append(paramString3);
      for (byte b = 0; b < arrayOfByte1.length; b++)
        paramStringBuffer.append((char)arrayOfByte1[b]); 
      paramStringBuffer.append("?=");
    } 
  }
  
  public static String decodeWord(String paramString) throws UnsupportedEncodingException {
    if (!paramString.startsWith("=?"))
      throw new ParseException(); 
    int i = 2;
    int j;
    if ((j = paramString.indexOf('?', i)) == -1)
      throw new ParseException(); 
    String str1 = javaCharset(paramString.substring(i, j));
    i = j + 1;
    if ((j = paramString.indexOf('?', i)) == -1)
      throw new ParseException(); 
    String str2 = paramString.substring(i, j);
    i = j + 1;
    if ((j = paramString.indexOf("?=", i)) == -1)
      throw new ParseException(); 
    String str3 = paramString.substring(i, j);
    try {
      QDecoderStream qDecoderStream;
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ASCIIUtility.getBytes(str3));
      if (str2.equalsIgnoreCase("B")) {
        qDecoderStream = new BASE64DecoderStream(byteArrayInputStream);
      } else if (str2.equalsIgnoreCase("Q")) {
        qDecoderStream = new QDecoderStream(byteArrayInputStream);
      } else {
        throw new UnsupportedEncodingException("unknown encoding: " + str2);
      } 
      int k = byteArrayInputStream.available();
      byte[] arrayOfByte = new byte[k];
      k = qDecoderStream.read(arrayOfByte, 0, k);
      String str = new String(arrayOfByte, 0, k, str1);
      if (j + 2 < paramString.length()) {
        String str4 = paramString.substring(j + 2);
        if (!decodeStrict)
          str4 = decodeInnerWords(str4); 
        str = str + str4;
      } 
      return str;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw unsupportedEncodingException;
    } catch (IOException iOException) {
      throw new ParseException();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new UnsupportedEncodingException();
    } 
  }
  
  private static String decodeInnerWords(String paramString) throws UnsupportedEncodingException {
    int i = 0;
    StringBuffer stringBuffer = new StringBuffer();
    int j;
    while ((j = paramString.indexOf("=?", i)) >= 0) {
      stringBuffer.append(paramString.substring(i, j));
      int k = paramString.indexOf("?=", j);
      if (k < 0)
        break; 
      String str = paramString.substring(j, k + 2);
      try {
        str = decodeWord(str);
      } catch (ParseException parseException) {}
      stringBuffer.append(str);
      i = k + 2;
    } 
    if (i == 0)
      return paramString; 
    if (i < paramString.length())
      stringBuffer.append(paramString.substring(i)); 
    return stringBuffer.toString();
  }
  
  public static String quote(String paramString1, String paramString2) {
    int i = paramString1.length();
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      char c = paramString1.charAt(b);
      if (c == '"' || c == '\\' || c == '\r' || c == '\n') {
        StringBuffer stringBuffer = new StringBuffer(i + 3);
        stringBuffer.append('"');
        stringBuffer.append(paramString1.substring(0, b));
        char c1 = Character.MIN_VALUE;
        for (byte b1 = b; b1 < i; b1++) {
          char c2 = paramString1.charAt(b1);
          if ((c2 == '"' || c2 == '\\' || c2 == '\r' || c2 == '\n') && (c2 != '\n' || c1 != '\r'))
            stringBuffer.append('\\'); 
          stringBuffer.append(c2);
          c1 = c2;
        } 
        stringBuffer.append('"');
        return stringBuffer.toString();
      } 
      if (c < ' ' || c >= '' || paramString2.indexOf(c) >= 0)
        bool = true; 
    } 
    if (bool) {
      StringBuffer stringBuffer = new StringBuffer(i + 2);
      stringBuffer.append('"').append(paramString1).append('"');
      return stringBuffer.toString();
    } 
    return paramString1;
  }
  
  static String fold(int paramInt, String paramString) {
    if (!foldText)
      return paramString; 
    int i;
    for (i = paramString.length() - 1; i >= 0; i--) {
      char c1 = paramString.charAt(i);
      if (c1 != ' ' && c1 != '\t')
        break; 
    } 
    if (i != paramString.length() - 1)
      paramString = paramString.substring(0, i + 1); 
    if (paramInt + paramString.length() <= 76)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer(paramString.length() + 4);
    char c = Character.MIN_VALUE;
    while (paramInt + paramString.length() > 76) {
      int j = -1;
      for (int k = 0; k < paramString.length() && (j == -1 || paramInt + k <= 76); k++) {
        char c1 = paramString.charAt(k);
        if ((c1 == ' ' || c1 == '\t') && c != ' ' && c != '\t')
          j = k; 
        c = c1;
      } 
      if (j == -1) {
        stringBuffer.append(paramString);
        paramString = "";
        paramInt = 0;
        break;
      } 
      stringBuffer.append(paramString.substring(0, j));
      stringBuffer.append("\r\n");
      c = paramString.charAt(j);
      stringBuffer.append(c);
      paramString = paramString.substring(j + 1);
      paramInt = 1;
    } 
    stringBuffer.append(paramString);
    return stringBuffer.toString();
  }
  
  static String unfold(String paramString) throws UnsupportedEncodingException {
    if (!foldText)
      return paramString; 
    StringBuffer stringBuffer = null;
    int i;
    while ((i = indexOfAny(paramString, "\r\n")) >= 0) {
      int j = i;
      int k = paramString.length();
      if (++i < k && paramString.charAt(i - 1) == '\r' && paramString.charAt(i) == '\n')
        i++; 
      if (j == 0 || paramString.charAt(j - 1) != '\\') {
        char c;
        if (i < k && ((c = paramString.charAt(i)) == ' ' || c == '\t')) {
          while (++i < k && ((c = paramString.charAt(i)) == ' ' || c == '\t'))
            i++; 
          if (stringBuffer == null)
            stringBuffer = new StringBuffer(paramString.length()); 
          if (j != 0) {
            stringBuffer.append(paramString.substring(0, j));
            stringBuffer.append(' ');
          } 
          paramString = paramString.substring(i);
          continue;
        } 
        if (stringBuffer == null)
          stringBuffer = new StringBuffer(paramString.length()); 
        stringBuffer.append(paramString.substring(0, i));
        paramString = paramString.substring(i);
        continue;
      } 
      if (stringBuffer == null)
        stringBuffer = new StringBuffer(paramString.length()); 
      stringBuffer.append(paramString.substring(0, j - 1));
      stringBuffer.append(paramString.substring(j, i));
      paramString = paramString.substring(i);
    } 
    if (stringBuffer != null) {
      stringBuffer.append(paramString);
      return stringBuffer.toString();
    } 
    return paramString;
  }
  
  private static int indexOfAny(String paramString1, String paramString2) { return indexOfAny(paramString1, paramString2, 0); }
  
  private static int indexOfAny(String paramString1, String paramString2, int paramInt) {
    try {
      int i = paramString1.length();
      for (int j = paramInt; j < i; j++) {
        if (paramString2.indexOf(paramString1.charAt(j)) >= 0)
          return j; 
      } 
      return -1;
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      return -1;
    } 
  }
  
  public static String javaCharset(String paramString) throws UnsupportedEncodingException {
    if (mime2java == null || paramString == null)
      return paramString; 
    String str = (String)mime2java.get(paramString.toLowerCase());
    return (str == null) ? paramString : str;
  }
  
  public static String mimeCharset(String paramString) throws UnsupportedEncodingException {
    if (java2mime == null || paramString == null)
      return paramString; 
    String str = (String)java2mime.get(paramString.toLowerCase());
    return (str == null) ? paramString : str;
  }
  
  public static String getDefaultJavaCharset() {
    if (defaultJavaCharset == null) {
      String str = null;
      str = SAAJUtil.getSystemProperty("mail.mime.charset");
      if (str != null && str.length() > 0) {
        defaultJavaCharset = javaCharset(str);
        return defaultJavaCharset;
      } 
      try {
        defaultJavaCharset = System.getProperty("file.encoding", "8859_1");
      } catch (SecurityException securityException) {
        InputStreamReader inputStreamReader = new InputStreamReader(new NullInputStream());
        defaultJavaCharset = inputStreamReader.getEncoding();
        if (defaultJavaCharset == null)
          defaultJavaCharset = "8859_1"; 
      } 
    } 
    return defaultJavaCharset;
  }
  
  static String getDefaultMIMECharset() {
    if (defaultMIMECharset == null)
      defaultMIMECharset = SAAJUtil.getSystemProperty("mail.mime.charset"); 
    if (defaultMIMECharset == null)
      defaultMIMECharset = mimeCharset(getDefaultJavaCharset()); 
    return defaultMIMECharset;
  }
  
  private static void loadMappings(LineInputStream paramLineInputStream, Hashtable paramHashtable) {
    while (true) {
      String str;
      try {
        str = paramLineInputStream.readLine();
      } catch (IOException iOException) {
        break;
      } 
      if (str == null || (str.startsWith("--") && str.endsWith("--")))
        break; 
      if (str.trim().length() == 0 || str.startsWith("#"))
        continue; 
      StringTokenizer stringTokenizer = new StringTokenizer(str, " \t");
      try {
        String str1 = stringTokenizer.nextToken();
        String str2 = stringTokenizer.nextToken();
        paramHashtable.put(str1.toLowerCase(), str2);
      } catch (NoSuchElementException noSuchElementException) {}
    } 
  }
  
  static int checkAscii(String paramString) {
    byte b1 = 0;
    byte b2 = 0;
    int i = paramString.length();
    for (byte b3 = 0; b3 < i; b3++) {
      if (nonascii(paramString.charAt(b3))) {
        b2++;
      } else {
        b1++;
      } 
    } 
    return (b2 == 0) ? 1 : ((b1 > b2) ? 2 : 3);
  }
  
  static int checkAscii(byte[] paramArrayOfByte) {
    byte b1 = 0;
    byte b2 = 0;
    for (byte b3 = 0; b3 < paramArrayOfByte.length; b3++) {
      if (nonascii(paramArrayOfByte[b3] & 0xFF)) {
        b2++;
      } else {
        b1++;
      } 
    } 
    return (b2 == 0) ? 1 : ((b1 > b2) ? 2 : 3);
  }
  
  static int checkAscii(InputStream paramInputStream, int paramInt, boolean paramBoolean) {
    byte b1 = 0;
    byte b2 = 0;
    char c = 'က';
    byte b3 = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = (encodeEolStrict && paramBoolean) ? 1 : 0;
    byte[] arrayOfByte = null;
    if (paramInt != 0) {
      c = (paramInt == -1) ? 4096 : Math.min(paramInt, 4096);
      arrayOfByte = new byte[c];
    } 
    while (paramInt != 0) {
      int i;
      try {
        if ((i = paramInputStream.read(arrayOfByte, 0, c)) == -1)
          break; 
        byte b = 0;
        for (byte b4 = 0; b4 < i; b4++) {
          byte b5 = arrayOfByte[b4] & 0xFF;
          if (bool3 && ((b == 13 && b5 != 10) || (b != 13 && b5 == 10)))
            bool2 = true; 
          if (b5 == 13 || b5 == 10) {
            b3 = 0;
          } else if (++b3 > 'Ϧ') {
            bool1 = true;
          } 
          if (nonascii(b5)) {
            if (paramBoolean)
              return 3; 
            b2++;
          } else {
            b1++;
          } 
          b = b5;
        } 
      } catch (IOException iOException) {
        break;
      } 
      if (paramInt != -1)
        paramInt -= i; 
    } 
    return (paramInt == 0 && paramBoolean) ? 3 : ((b2 == 0) ? (bool2 ? 3 : (bool1 ? 2 : 1)) : ((b1 > b2) ? 2 : 3));
  }
  
  static final boolean nonascii(int paramInt) { return (paramInt >= 127 || (paramInt < 32 && paramInt != 13 && paramInt != 10 && paramInt != 9)); }
  
  static  {
    try {
      String str = SAAJUtil.getSystemProperty("mail.mime.decodetext.strict");
      decodeStrict = (str == null || !str.equalsIgnoreCase("false"));
      str = SAAJUtil.getSystemProperty("mail.mime.encodeeol.strict");
      encodeEolStrict = (str != null && str.equalsIgnoreCase("true"));
      str = SAAJUtil.getSystemProperty("mail.mime.foldencodedwords");
      foldEncodedWords = (str != null && str.equalsIgnoreCase("true"));
      str = SAAJUtil.getSystemProperty("mail.mime.foldtext");
      foldText = (str == null || !str.equalsIgnoreCase("false"));
    } catch (SecurityException securityException) {}
    java2mime = new Hashtable(40);
    mime2java = new Hashtable(10);
    try {
      InputStream inputStream = MimeUtility.class.getResourceAsStream("/META-INF/javamail.charset.map");
      if (inputStream != null) {
        inputStream = new LineInputStream(inputStream);
        loadMappings((LineInputStream)inputStream, java2mime);
        loadMappings((LineInputStream)inputStream, mime2java);
      } 
    } catch (Exception exception) {}
    if (java2mime.isEmpty()) {
      java2mime.put("8859_1", "ISO-8859-1");
      java2mime.put("iso8859_1", "ISO-8859-1");
      java2mime.put("ISO8859-1", "ISO-8859-1");
      java2mime.put("8859_2", "ISO-8859-2");
      java2mime.put("iso8859_2", "ISO-8859-2");
      java2mime.put("ISO8859-2", "ISO-8859-2");
      java2mime.put("8859_3", "ISO-8859-3");
      java2mime.put("iso8859_3", "ISO-8859-3");
      java2mime.put("ISO8859-3", "ISO-8859-3");
      java2mime.put("8859_4", "ISO-8859-4");
      java2mime.put("iso8859_4", "ISO-8859-4");
      java2mime.put("ISO8859-4", "ISO-8859-4");
      java2mime.put("8859_5", "ISO-8859-5");
      java2mime.put("iso8859_5", "ISO-8859-5");
      java2mime.put("ISO8859-5", "ISO-8859-5");
      java2mime.put("8859_6", "ISO-8859-6");
      java2mime.put("iso8859_6", "ISO-8859-6");
      java2mime.put("ISO8859-6", "ISO-8859-6");
      java2mime.put("8859_7", "ISO-8859-7");
      java2mime.put("iso8859_7", "ISO-8859-7");
      java2mime.put("ISO8859-7", "ISO-8859-7");
      java2mime.put("8859_8", "ISO-8859-8");
      java2mime.put("iso8859_8", "ISO-8859-8");
      java2mime.put("ISO8859-8", "ISO-8859-8");
      java2mime.put("8859_9", "ISO-8859-9");
      java2mime.put("iso8859_9", "ISO-8859-9");
      java2mime.put("ISO8859-9", "ISO-8859-9");
      java2mime.put("SJIS", "Shift_JIS");
      java2mime.put("MS932", "Shift_JIS");
      java2mime.put("JIS", "ISO-2022-JP");
      java2mime.put("ISO2022JP", "ISO-2022-JP");
      java2mime.put("EUC_JP", "euc-jp");
      java2mime.put("KOI8_R", "koi8-r");
      java2mime.put("EUC_CN", "euc-cn");
      java2mime.put("EUC_TW", "euc-tw");
      java2mime.put("EUC_KR", "euc-kr");
    } 
    if (mime2java.isEmpty()) {
      mime2java.put("iso-2022-cn", "ISO2022CN");
      mime2java.put("iso-2022-kr", "ISO2022KR");
      mime2java.put("utf-8", "UTF8");
      mime2java.put("utf8", "UTF8");
      mime2java.put("ja_jp.iso2022-7", "ISO2022JP");
      mime2java.put("ja_jp.eucjp", "EUCJIS");
      mime2java.put("euc-kr", "KSC5601");
      mime2java.put("euckr", "KSC5601");
      mime2java.put("us-ascii", "ISO-8859-1");
      mime2java.put("x-us-ascii", "ISO-8859-1");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimeUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */