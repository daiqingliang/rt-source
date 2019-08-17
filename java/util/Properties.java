package java.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import jdk.internal.util.xml.BasicXmlPropertiesProvider;
import sun.util.spi.XmlPropertiesProvider;

public class Properties extends Hashtable<Object, Object> {
  private static final long serialVersionUID = 4112578634029874840L;
  
  protected Properties defaults;
  
  private static final char[] hexDigit = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  public Properties() { this(null); }
  
  public Properties(Properties paramProperties) { this.defaults = paramProperties; }
  
  public Object setProperty(String paramString1, String paramString2) { return put(paramString1, paramString2); }
  
  public void load(Reader paramReader) throws IOException { load0(new LineReader(paramReader)); }
  
  public void load(InputStream paramInputStream) throws IOException { load0(new LineReader(paramInputStream)); }
  
  private void load0(LineReader paramLineReader) throws IOException {
    char[] arrayOfChar = new char[1024];
    int i;
    while ((i = paramLineReader.readLine()) >= 0) {
      char c = Character.MIN_VALUE;
      byte b = 0;
      int j = i;
      boolean bool1 = false;
      boolean bool2 = false;
      while (b < i) {
        c = paramLineReader.lineBuf[b];
        if ((c == '=' || c == ':') && !bool2) {
          j = b + true;
          bool1 = true;
          break;
        } 
        if ((c == ' ' || c == '\t' || c == '\f') && !bool2) {
          j = b + true;
          break;
        } 
        if (c == '\\') {
          bool2 = !bool2 ? 1 : 0;
        } else {
          bool2 = false;
        } 
        b++;
      } 
      while (j < i) {
        c = paramLineReader.lineBuf[j];
        if (c != ' ' && c != '\t' && c != '\f')
          if (!bool1 && (c == '=' || c == ':')) {
            bool1 = true;
          } else {
            break;
          }  
        j++;
      } 
      String str1 = loadConvert(paramLineReader.lineBuf, 0, b, arrayOfChar);
      String str2 = loadConvert(paramLineReader.lineBuf, j, i - j, arrayOfChar);
      put(str1, str2);
    } 
  }
  
  private String loadConvert(char[] paramArrayOfChar1, int paramInt1, int paramInt2, char[] paramArrayOfChar2) {
    if (paramArrayOfChar2.length < paramInt2) {
      int j = paramInt2 * 2;
      if (j < 0)
        j = Integer.MAX_VALUE; 
      paramArrayOfChar2 = new char[j];
    } 
    char[] arrayOfChar = paramArrayOfChar2;
    byte b = 0;
    int i = paramInt1 + paramInt2;
    while (paramInt1 < i) {
      char c = paramArrayOfChar1[paramInt1++];
      if (c == '\\') {
        c = paramArrayOfChar1[paramInt1++];
        if (c == 'u') {
          char c1 = Character.MIN_VALUE;
          for (byte b1 = 0; b1 < 4; b1++) {
            c = paramArrayOfChar1[paramInt1++];
            switch (c) {
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                c1 = (c1 << 4) + c - '0';
                break;
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
                c1 = (c1 << '\004') + '\n' + c - 'a';
                break;
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
                c1 = (c1 << '\004') + '\n' + c - 'A';
                break;
              default:
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            } 
          } 
          arrayOfChar[b++] = (char)c1;
          continue;
        } 
        if (c == 't') {
          c = '\t';
        } else if (c == 'r') {
          c = '\r';
        } else if (c == 'n') {
          c = '\n';
        } else if (c == 'f') {
          c = '\f';
        } 
        arrayOfChar[b++] = c;
        continue;
      } 
      arrayOfChar[b++] = c;
    } 
    return new String(arrayOfChar, 0, b);
  }
  
  private String saveConvert(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramString.length();
    int j = i * 2;
    if (j < 0)
      j = Integer.MAX_VALUE; 
    StringBuffer stringBuffer = new StringBuffer(j);
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c > '=' && c < '') {
        if (c == '\\') {
          stringBuffer.append('\\');
          stringBuffer.append('\\');
        } else {
          stringBuffer.append(c);
        } 
      } else {
        switch (c) {
          case ' ':
            if (b == 0 || paramBoolean1)
              stringBuffer.append('\\'); 
            stringBuffer.append(' ');
            break;
          case '\t':
            stringBuffer.append('\\');
            stringBuffer.append('t');
            break;
          case '\n':
            stringBuffer.append('\\');
            stringBuffer.append('n');
            break;
          case '\r':
            stringBuffer.append('\\');
            stringBuffer.append('r');
            break;
          case '\f':
            stringBuffer.append('\\');
            stringBuffer.append('f');
            break;
          case '!':
          case '#':
          case ':':
          case '=':
            stringBuffer.append('\\');
            stringBuffer.append(c);
            break;
          default:
            if (((c < ' ' || c > '~') ? 1 : 0) & paramBoolean2) {
              stringBuffer.append('\\');
              stringBuffer.append('u');
              stringBuffer.append(toHex(c >> '\f' & 0xF));
              stringBuffer.append(toHex(c >> '\b' & 0xF));
              stringBuffer.append(toHex(c >> '\004' & 0xF));
              stringBuffer.append(toHex(c & 0xF));
              break;
            } 
            stringBuffer.append(c);
            break;
        } 
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static void writeComments(BufferedWriter paramBufferedWriter, String paramString) throws IOException {
    paramBufferedWriter.write("#");
    int i = paramString.length();
    byte b1 = 0;
    byte b2 = 0;
    char[] arrayOfChar = new char[6];
    arrayOfChar[0] = '\\';
    arrayOfChar[1] = 'u';
    while (b1 < i) {
      char c = paramString.charAt(b1);
      if (c > 'ÿ' || c == '\n' || c == '\r') {
        if (b2 != b1)
          paramBufferedWriter.write(paramString.substring(b2, b1)); 
        if (c > 'ÿ') {
          arrayOfChar[2] = toHex(c >> '\f' & 0xF);
          arrayOfChar[3] = toHex(c >> '\b' & 0xF);
          arrayOfChar[4] = toHex(c >> '\004' & 0xF);
          arrayOfChar[5] = toHex(c & 0xF);
          paramBufferedWriter.write(new String(arrayOfChar));
        } else {
          paramBufferedWriter.newLine();
          if (c == '\r' && b1 != i - 1 && paramString.charAt(b1 + 1) == '\n')
            b1++; 
          if (b1 == i - 1 || (paramString.charAt(b1 + 1) != '#' && paramString.charAt(b1 + 1) != '!'))
            paramBufferedWriter.write("#"); 
        } 
        b2 = b1 + 1;
      } 
      b1++;
    } 
    if (b2 != b1)
      paramBufferedWriter.write(paramString.substring(b2, b1)); 
    paramBufferedWriter.newLine();
  }
  
  @Deprecated
  public void save(OutputStream paramOutputStream, String paramString) {
    try {
      store(paramOutputStream, paramString);
    } catch (IOException iOException) {}
  }
  
  public void store(Writer paramWriter, String paramString) throws IOException { store0((paramWriter instanceof BufferedWriter) ? (BufferedWriter)paramWriter : new BufferedWriter(paramWriter), paramString, false); }
  
  public void store(OutputStream paramOutputStream, String paramString) { store0(new BufferedWriter(new OutputStreamWriter(paramOutputStream, "8859_1")), paramString, true); }
  
  private void store0(BufferedWriter paramBufferedWriter, String paramString, boolean paramBoolean) throws IOException {
    if (paramString != null)
      writeComments(paramBufferedWriter, paramString); 
    paramBufferedWriter.write("#" + (new Date()).toString());
    paramBufferedWriter.newLine();
    synchronized (this) {
      Enumeration enumeration = keys();
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        String str2 = (String)get(str1);
        str1 = saveConvert(str1, true, paramBoolean);
        str2 = saveConvert(str2, false, paramBoolean);
        paramBufferedWriter.write(str1 + "=" + str2);
        paramBufferedWriter.newLine();
      } 
    } 
    paramBufferedWriter.flush();
  }
  
  public void loadFromXML(InputStream paramInputStream) throws IOException {
    XmlSupport.load(this, (InputStream)Objects.requireNonNull(paramInputStream));
    paramInputStream.close();
  }
  
  public void storeToXML(OutputStream paramOutputStream, String paramString) { storeToXML(paramOutputStream, paramString, "UTF-8"); }
  
  public void storeToXML(OutputStream paramOutputStream, String paramString1, String paramString2) throws IOException { XmlSupport.save(this, (OutputStream)Objects.requireNonNull(paramOutputStream), paramString1, (String)Objects.requireNonNull(paramString2)); }
  
  public String getProperty(String paramString) {
    Object object = get(paramString);
    String str = (object instanceof String) ? (String)object : null;
    return (str == null && this.defaults != null) ? this.defaults.getProperty(paramString) : str;
  }
  
  public String getProperty(String paramString1, String paramString2) {
    String str = getProperty(paramString1);
    return (str == null) ? paramString2 : str;
  }
  
  public Enumeration<?> propertyNames() {
    Hashtable hashtable = new Hashtable();
    enumerate(hashtable);
    return hashtable.keys();
  }
  
  public Set<String> stringPropertyNames() {
    Hashtable hashtable = new Hashtable();
    enumerateStringProperties(hashtable);
    return hashtable.keySet();
  }
  
  public void list(PrintStream paramPrintStream) {
    paramPrintStream.println("-- listing properties --");
    Hashtable hashtable = new Hashtable();
    enumerate(hashtable);
    Enumeration enumeration = hashtable.keys();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = (String)hashtable.get(str1);
      if (str2.length() > 40)
        str2 = str2.substring(0, 37) + "..."; 
      paramPrintStream.println(str1 + "=" + str2);
    } 
  }
  
  public void list(PrintWriter paramPrintWriter) {
    paramPrintWriter.println("-- listing properties --");
    Hashtable hashtable = new Hashtable();
    enumerate(hashtable);
    Enumeration enumeration = hashtable.keys();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = (String)hashtable.get(str1);
      if (str2.length() > 40)
        str2 = str2.substring(0, 37) + "..."; 
      paramPrintWriter.println(str1 + "=" + str2);
    } 
  }
  
  private void enumerate(Hashtable<String, Object> paramHashtable) {
    if (this.defaults != null)
      this.defaults.enumerate(paramHashtable); 
    Enumeration enumeration = keys();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      paramHashtable.put(str, get(str));
    } 
  }
  
  private void enumerateStringProperties(Hashtable<String, String> paramHashtable) {
    if (this.defaults != null)
      this.defaults.enumerateStringProperties(paramHashtable); 
    Enumeration enumeration = keys();
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      Object object2 = get(object1);
      if (object1 instanceof String && object2 instanceof String)
        paramHashtable.put((String)object1, (String)object2); 
    } 
  }
  
  private static char toHex(int paramInt) { return hexDigit[paramInt & 0xF]; }
  
  class LineReader {
    byte[] inByteBuf;
    
    char[] inCharBuf;
    
    char[] lineBuf = new char[1024];
    
    int inLimit = 0;
    
    int inOff = 0;
    
    InputStream inStream;
    
    Reader reader;
    
    public LineReader(InputStream param1InputStream) {
      this.inStream = param1InputStream;
      this.inByteBuf = new byte[8192];
    }
    
    public LineReader(Reader param1Reader) {
      this.reader = param1Reader;
      this.inCharBuf = new char[8192];
    }
    
    int readLine() throws IOException {
      byte b = 0;
      char c = Character.MIN_VALUE;
      boolean bool1 = true;
      boolean bool2 = false;
      boolean bool3 = true;
      boolean bool4 = false;
      boolean bool5 = false;
      boolean bool6 = false;
      while (true) {
        if (this.inOff >= this.inLimit) {
          this.inLimit = (this.inStream == null) ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf);
          this.inOff = 0;
          if (this.inLimit <= 0) {
            if (!b || bool2)
              return -1; 
            if (bool5)
              b--; 
            return b;
          } 
        } 
        if (this.inStream != null) {
          c = (char)(0xFF & this.inByteBuf[this.inOff++]);
        } else {
          c = this.inCharBuf[this.inOff++];
        } 
        if (bool6) {
          bool6 = false;
          if (c == '\n')
            continue; 
        } 
        if (bool1) {
          if (c == ' ' || c == '\t' || c == '\f' || (!bool4 && (c == '\r' || c == '\n')))
            continue; 
          bool1 = false;
          bool4 = false;
        } 
        if (bool3) {
          bool3 = false;
          if (c == '#' || c == '!') {
            bool2 = true;
            continue;
          } 
        } 
        if (c != '\n' && c != '\r') {
          this.lineBuf[b++] = c;
          if (b == this.lineBuf.length) {
            int i = this.lineBuf.length * 2;
            if (i < 0)
              i = Integer.MAX_VALUE; 
            char[] arrayOfChar = new char[i];
            System.arraycopy(this.lineBuf, 0, arrayOfChar, 0, this.lineBuf.length);
            this.lineBuf = arrayOfChar;
          } 
          if (c == '\\') {
            bool5 = !bool5 ? 1 : 0;
            continue;
          } 
          bool5 = false;
          continue;
        } 
        if (bool2 || b == 0) {
          bool2 = false;
          bool3 = true;
          bool1 = true;
          b = 0;
          continue;
        } 
        if (this.inOff >= this.inLimit) {
          this.inLimit = (this.inStream == null) ? this.reader.read(this.inCharBuf) : this.inStream.read(this.inByteBuf);
          this.inOff = 0;
          if (this.inLimit <= 0) {
            if (bool5)
              b--; 
            return b;
          } 
        } 
        if (bool5) {
          b--;
          bool1 = true;
          bool4 = true;
          bool5 = false;
          if (c == '\r')
            bool6 = true; 
          continue;
        } 
        break;
      } 
      return b;
    }
  }
  
  private static class XmlSupport {
    private static final XmlPropertiesProvider PROVIDER = loadProvider();
    
    private static XmlPropertiesProvider loadProviderFromProperty(ClassLoader param1ClassLoader) {
      String str = System.getProperty("sun.util.spi.XmlPropertiesProvider");
      if (str == null)
        return null; 
      try {
        Class clazz = Class.forName(str, true, param1ClassLoader);
        return (XmlPropertiesProvider)clazz.newInstance();
      } catch (ClassNotFoundException|IllegalAccessException|InstantiationException classNotFoundException) {
        throw new ServiceConfigurationError(null, classNotFoundException);
      } 
    }
    
    private static XmlPropertiesProvider loadProviderAsService(ClassLoader param1ClassLoader) {
      Iterator iterator = ServiceLoader.load(XmlPropertiesProvider.class, param1ClassLoader).iterator();
      return iterator.hasNext() ? (XmlPropertiesProvider)iterator.next() : null;
    }
    
    private static XmlPropertiesProvider loadProvider() { return (XmlPropertiesProvider)AccessController.doPrivileged(new PrivilegedAction<XmlPropertiesProvider>() {
            public XmlPropertiesProvider run() {
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              XmlPropertiesProvider xmlPropertiesProvider = Properties.XmlSupport.loadProviderFromProperty(classLoader);
              if (xmlPropertiesProvider != null)
                return xmlPropertiesProvider; 
              xmlPropertiesProvider = Properties.XmlSupport.loadProviderAsService(classLoader);
              return (xmlPropertiesProvider != null) ? xmlPropertiesProvider : new BasicXmlPropertiesProvider();
            }
          }); }
    
    static void load(Properties param1Properties, InputStream param1InputStream) { PROVIDER.load(param1Properties, param1InputStream); }
    
    static void save(Properties param1Properties, OutputStream param1OutputStream, String param1String1, String param1String2) throws IOException { PROVIDER.store(param1Properties, param1OutputStream, param1String1, param1String2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Properties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */