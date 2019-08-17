package sun.awt.windows;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class HTMLCodec extends InputStream {
  public static final String ENCODING = "UTF-8";
  
  public static final String VERSION = "Version:";
  
  public static final String START_HTML = "StartHTML:";
  
  public static final String END_HTML = "EndHTML:";
  
  public static final String START_FRAGMENT = "StartFragment:";
  
  public static final String END_FRAGMENT = "EndFragment:";
  
  public static final String START_SELECTION = "StartSelection:";
  
  public static final String END_SELECTION = "EndSelection:";
  
  public static final String START_FRAGMENT_CMT = "<!--StartFragment-->";
  
  public static final String END_FRAGMENT_CMT = "<!--EndFragment-->";
  
  public static final String SOURCE_URL = "SourceURL:";
  
  public static final String DEF_SOURCE_URL = "about:blank";
  
  public static final String EOLN = "\r\n";
  
  private static final String VERSION_NUM = "1.0";
  
  private static final int PADDED_WIDTH = 10;
  
  private final BufferedInputStream bufferedStream;
  
  private boolean descriptionParsed = false;
  
  private boolean closed = false;
  
  public static final int BYTE_BUFFER_LEN = 8192;
  
  public static final int CHAR_BUFFER_LEN = 2730;
  
  private static final String FAILURE_MSG = "Unable to parse HTML description: ";
  
  private static final String INVALID_MSG = " invalid";
  
  private long iHTMLStart;
  
  private long iHTMLEnd;
  
  private long iFragStart;
  
  private long iFragEnd;
  
  private long iSelStart;
  
  private long iSelEnd;
  
  private String stBaseURL;
  
  private String stVersion;
  
  private long iStartOffset;
  
  private long iEndOffset;
  
  private long iReadCount;
  
  private EHTMLReadMode readMode;
  
  private static String toPaddedString(int paramInt1, int paramInt2) {
    String str = "" + paramInt1;
    int i = str.length();
    if (paramInt1 >= 0 && i < paramInt2) {
      char[] arrayOfChar = new char[paramInt2 - i];
      Arrays.fill(arrayOfChar, '0');
      StringBuffer stringBuffer = new StringBuffer(paramInt2);
      stringBuffer.append(arrayOfChar);
      stringBuffer.append(str);
      str = stringBuffer.toString();
    } 
    return str;
  }
  
  public static byte[] convertToHTMLFormat(byte[] paramArrayOfByte) {
    String str1 = "";
    String str2 = "";
    String str3 = new String(paramArrayOfByte);
    String str4 = str3.toUpperCase();
    if (-1 == str4.indexOf("<HTML")) {
      str1 = "<HTML>";
      str2 = "</HTML>";
      if (-1 == str4.indexOf("<BODY")) {
        str1 = str1 + "<BODY>";
        str2 = "</BODY>" + str2;
      } 
    } 
    str3 = "about:blank";
    int i = "Version:".length() + "1.0".length() + "\r\n".length() + "StartHTML:".length() + 10 + "\r\n".length() + "EndHTML:".length() + 10 + "\r\n".length() + "StartFragment:".length() + 10 + "\r\n".length() + "EndFragment:".length() + 10 + "\r\n".length() + "SourceURL:".length() + str3.length() + "\r\n".length();
    int j = i + str1.length();
    int k = j + paramArrayOfByte.length - 1;
    int m = k + str2.length();
    StringBuilder stringBuilder = new StringBuilder(j + "<!--StartFragment-->".length());
    stringBuilder.append("Version:");
    stringBuilder.append("1.0");
    stringBuilder.append("\r\n");
    stringBuilder.append("StartHTML:");
    stringBuilder.append(toPaddedString(i, 10));
    stringBuilder.append("\r\n");
    stringBuilder.append("EndHTML:");
    stringBuilder.append(toPaddedString(m, 10));
    stringBuilder.append("\r\n");
    stringBuilder.append("StartFragment:");
    stringBuilder.append(toPaddedString(j, 10));
    stringBuilder.append("\r\n");
    stringBuilder.append("EndFragment:");
    stringBuilder.append(toPaddedString(k, 10));
    stringBuilder.append("\r\n");
    stringBuilder.append("SourceURL:");
    stringBuilder.append(str3);
    stringBuilder.append("\r\n");
    stringBuilder.append(str1);
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    try {
      arrayOfByte1 = stringBuilder.toString().getBytes("UTF-8");
      arrayOfByte2 = str2.getBytes("UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + paramArrayOfByte.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte3, arrayOfByte1.length, paramArrayOfByte.length - 1);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length + paramArrayOfByte.length - 1, arrayOfByte2.length);
    arrayOfByte3[arrayOfByte3.length - 1] = 0;
    return arrayOfByte3;
  }
  
  public HTMLCodec(InputStream paramInputStream, EHTMLReadMode paramEHTMLReadMode) throws IOException {
    this.bufferedStream = new BufferedInputStream(paramInputStream, 8192);
    this.readMode = paramEHTMLReadMode;
  }
  
  public String getBaseURL() throws IOException {
    if (!this.descriptionParsed)
      parseDescription(); 
    return this.stBaseURL;
  }
  
  public String getVersion() throws IOException {
    if (!this.descriptionParsed)
      parseDescription(); 
    return this.stVersion;
  }
  
  private void parseDescription() throws IOException {
    this.stBaseURL = null;
    this.stVersion = null;
    this.iHTMLEnd = this.iHTMLStart = this.iFragEnd = this.iFragStart = this.iSelEnd = this.iSelStart = -1L;
    this.bufferedStream.mark(8192);
    String[] arrayOfString = { "Version:", "StartHTML:", "EndHTML:", "StartFragment:", "EndFragment:", "StartSelection:", "EndSelection:", "SourceURL:" };
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.bufferedStream, "UTF-8"), 2730);
    long l1 = 0L;
    long l2 = "\r\n".length();
    int i = arrayOfString.length;
    boolean bool = true;
    int j;
    for (j = 0; j < i; j++) {
      String str = bufferedReader.readLine();
      if (null == str)
        break; 
      while (j < i) {
        if (!str.startsWith(arrayOfString[j])) {
          j++;
          continue;
        } 
        l1 += str.length() + l2;
        String str1 = str.substring(arrayOfString[j].length()).trim();
        if (null != str1)
          try {
            switch (j) {
              case 0:
                this.stVersion = str1;
                break;
              case 1:
                this.iHTMLStart = Integer.parseInt(str1);
                break;
              case 2:
                this.iHTMLEnd = Integer.parseInt(str1);
                break;
              case 3:
                this.iFragStart = Integer.parseInt(str1);
                break;
              case 4:
                this.iFragEnd = Integer.parseInt(str1);
                break;
              case 5:
                this.iSelStart = Integer.parseInt(str1);
                break;
              case 6:
                this.iSelEnd = Integer.parseInt(str1);
                break;
              case 7:
                this.stBaseURL = str1;
                break;
            } 
            break;
          } catch (NumberFormatException numberFormatException) {
            throw new IOException("Unable to parse HTML description: " + arrayOfString[j] + " value " + numberFormatException + " invalid");
          }  
      } 
    } 
    if (-1L == this.iHTMLStart)
      this.iHTMLStart = l1; 
    if (-1L == this.iFragStart)
      this.iFragStart = this.iHTMLStart; 
    if (-1L == this.iFragEnd)
      this.iFragEnd = this.iHTMLEnd; 
    if (-1L == this.iSelStart)
      this.iSelStart = this.iFragStart; 
    if (-1L == this.iSelEnd)
      this.iSelEnd = this.iFragEnd; 
    switch (this.readMode) {
      case HTML_READ_ALL:
        this.iStartOffset = this.iHTMLStart;
        this.iEndOffset = this.iHTMLEnd;
        break;
      case HTML_READ_FRAGMENT:
        this.iStartOffset = this.iFragStart;
        this.iEndOffset = this.iFragEnd;
        break;
      default:
        this.iStartOffset = this.iSelStart;
        this.iEndOffset = this.iSelEnd;
        break;
    } 
    this.bufferedStream.reset();
    if (-1L == this.iStartOffset)
      throw new IOException("Unable to parse HTML description: invalid HTML format."); 
    for (j = 0; j < this.iStartOffset; j = (int)(j + this.bufferedStream.skip(this.iStartOffset - j)));
    this.iReadCount = j;
    if (this.iStartOffset != this.iReadCount)
      throw new IOException("Unable to parse HTML description: Byte stream ends in description."); 
    this.descriptionParsed = true;
  }
  
  public int read() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
    if (!this.descriptionParsed)
      parseDescription(); 
    if (-1L != this.iEndOffset && this.iReadCount >= this.iEndOffset)
      return -1; 
    int i = this.bufferedStream.read();
    if (i == -1)
      return -1; 
    this.iReadCount++;
    return i;
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      this.closed = true;
      this.bufferedStream.close();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\HTMLCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */