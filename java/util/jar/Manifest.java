package java.util.jar;

import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Manifest implements Cloneable {
  private final Attributes attr = new Attributes();
  
  private final Map<String, Attributes> entries = new HashMap();
  
  private final JarVerifier jv;
  
  public Manifest() { this.jv = null; }
  
  public Manifest(InputStream paramInputStream) throws IOException { this(null, paramInputStream); }
  
  Manifest(JarVerifier paramJarVerifier, InputStream paramInputStream) throws IOException {
    read(paramInputStream);
    this.jv = paramJarVerifier;
  }
  
  public Manifest(Manifest paramManifest) {
    this.attr.putAll(paramManifest.getMainAttributes());
    this.entries.putAll(paramManifest.getEntries());
    this.jv = paramManifest.jv;
  }
  
  public Attributes getMainAttributes() { return this.attr; }
  
  public Map<String, Attributes> getEntries() { return this.entries; }
  
  public Attributes getAttributes(String paramString) { return (Attributes)getEntries().get(paramString); }
  
  Attributes getTrustedAttributes(String paramString) {
    Attributes attributes = getAttributes(paramString);
    if (attributes != null && this.jv != null && !this.jv.isTrustedManifestEntry(paramString))
      throw new SecurityException("Untrusted manifest entry: " + paramString); 
    return attributes;
  }
  
  public void clear() {
    this.attr.clear();
    this.entries.clear();
  }
  
  public void write(OutputStream paramOutputStream) throws IOException {
    DataOutputStream dataOutputStream = new DataOutputStream(paramOutputStream);
    this.attr.writeMain(dataOutputStream);
    for (Map.Entry entry : this.entries.entrySet()) {
      StringBuffer stringBuffer = new StringBuffer("Name: ");
      String str = (String)entry.getKey();
      if (str != null) {
        byte[] arrayOfByte = str.getBytes("UTF8");
        str = new String(arrayOfByte, 0, 0, arrayOfByte.length);
      } 
      stringBuffer.append(str);
      stringBuffer.append("\r\n");
      make72Safe(stringBuffer);
      dataOutputStream.writeBytes(stringBuffer.toString());
      ((Attributes)entry.getValue()).write(dataOutputStream);
    } 
    dataOutputStream.flush();
  }
  
  static void make72Safe(StringBuffer paramStringBuffer) {
    int i = paramStringBuffer.length();
    if (i > 72) {
      byte b = 70;
      while (b < i - 2) {
        paramStringBuffer.insert(b, "\r\n ");
        b += 72;
        i += 3;
      } 
    } 
  }
  
  public void read(InputStream paramInputStream) throws IOException {
    FastInputStream fastInputStream = new FastInputStream(paramInputStream);
    byte[] arrayOfByte1 = new byte[512];
    this.attr.read(fastInputStream, arrayOfByte1);
    int i = 0;
    int j = 0;
    int k = 2;
    String str = null;
    boolean bool = true;
    byte[] arrayOfByte2 = null;
    int m;
    while ((m = fastInputStream.readLine(arrayOfByte1)) != -1) {
      if (arrayOfByte1[--m] != 10)
        throw new IOException("manifest line too long"); 
      if (m > 0 && arrayOfByte1[m - 1] == 13)
        m--; 
      if (m == 0 && bool)
        continue; 
      bool = false;
      if (str == null) {
        str = parseName(arrayOfByte1, m);
        if (str == null)
          throw new IOException("invalid manifest format"); 
        if (fastInputStream.peek() == 32) {
          arrayOfByte2 = new byte[m - 6];
          System.arraycopy(arrayOfByte1, 6, arrayOfByte2, 0, m - 6);
          continue;
        } 
      } else {
        byte[] arrayOfByte = new byte[arrayOfByte2.length + m - 1];
        System.arraycopy(arrayOfByte2, 0, arrayOfByte, 0, arrayOfByte2.length);
        System.arraycopy(arrayOfByte1, 1, arrayOfByte, arrayOfByte2.length, m - 1);
        if (fastInputStream.peek() == 32) {
          arrayOfByte2 = arrayOfByte;
          continue;
        } 
        str = new String(arrayOfByte, 0, arrayOfByte.length, "UTF8");
        arrayOfByte2 = null;
      } 
      Attributes attributes = getAttributes(str);
      if (attributes == null) {
        attributes = new Attributes(k);
        this.entries.put(str, attributes);
      } 
      attributes.read(fastInputStream, arrayOfByte1);
      i++;
      j += attributes.size();
      k = Math.max(2, j / i);
      str = null;
      bool = true;
    } 
  }
  
  private String parseName(byte[] paramArrayOfByte, int paramInt) {
    if (toLower(paramArrayOfByte[0]) == 110 && toLower(paramArrayOfByte[1]) == 97 && toLower(paramArrayOfByte[2]) == 109 && toLower(paramArrayOfByte[3]) == 101 && paramArrayOfByte[4] == 58 && paramArrayOfByte[5] == 32)
      try {
        return new String(paramArrayOfByte, 6, paramInt - 6, "UTF8");
      } catch (Exception exception) {} 
    return null;
  }
  
  private int toLower(int paramInt) { return (paramInt >= 65 && paramInt <= 90) ? (97 + paramInt - 65) : paramInt; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Manifest) {
      Manifest manifest = (Manifest)paramObject;
      return (this.attr.equals(manifest.getMainAttributes()) && this.entries.equals(manifest.getEntries()));
    } 
    return false;
  }
  
  public int hashCode() { return this.attr.hashCode() + this.entries.hashCode(); }
  
  public Object clone() { return new Manifest(this); }
  
  static class FastInputStream extends FilterInputStream {
    private byte[] buf;
    
    private int count = 0;
    
    private int pos = 0;
    
    FastInputStream(InputStream param1InputStream) throws IOException { this(param1InputStream, 8192); }
    
    FastInputStream(InputStream param1InputStream, int param1Int) {
      super(param1InputStream);
      this.buf = new byte[param1Int];
    }
    
    public int read() {
      if (this.pos >= this.count) {
        fill();
        if (this.pos >= this.count)
          return -1; 
      } 
      return Byte.toUnsignedInt(this.buf[this.pos++]);
    }
    
    public int read(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      int i = this.count - this.pos;
      if (i <= 0) {
        if (param1Int2 >= this.buf.length)
          return this.in.read(param1ArrayOfByte, param1Int1, param1Int2); 
        fill();
        i = this.count - this.pos;
        if (i <= 0)
          return -1; 
      } 
      if (param1Int2 > i)
        param1Int2 = i; 
      System.arraycopy(this.buf, this.pos, param1ArrayOfByte, param1Int1, param1Int2);
      this.pos += param1Int2;
      return param1Int2;
    }
    
    public int readLine(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      byte[] arrayOfByte = this.buf;
      int i = 0;
      while (i < param1Int2) {
        int j = this.count - this.pos;
        if (j <= 0) {
          fill();
          j = this.count - this.pos;
          if (j <= 0)
            return -1; 
        } 
        int k = param1Int2 - i;
        if (k > j)
          k = j; 
        int m = this.pos;
        int n = m + k;
        while (m < n && arrayOfByte[m++] != 10);
        k = m - this.pos;
        System.arraycopy(arrayOfByte, this.pos, param1ArrayOfByte, param1Int1, k);
        param1Int1 += k;
        i += k;
        this.pos = m;
        if (arrayOfByte[m - 1] == 10)
          break; 
      } 
      return i;
    }
    
    public byte peek() throws IOException {
      if (this.pos == this.count)
        fill(); 
      return (this.pos == this.count) ? -1 : this.buf[this.pos];
    }
    
    public int readLine(byte[] param1ArrayOfByte) throws IOException { return readLine(param1ArrayOfByte, 0, param1ArrayOfByte.length); }
    
    public long skip(long param1Long) throws IOException {
      if (param1Long <= 0L)
        return 0L; 
      long l = (this.count - this.pos);
      if (l <= 0L)
        return this.in.skip(param1Long); 
      if (param1Long > l)
        param1Long = l; 
      this.pos = (int)(this.pos + param1Long);
      return param1Long;
    }
    
    public int available() { return this.count - this.pos + this.in.available(); }
    
    public void close() {
      if (this.in != null) {
        this.in.close();
        this.in = null;
        this.buf = null;
      } 
    }
    
    private void fill() {
      this.count = this.pos = 0;
      int i = this.in.read(this.buf, 0, this.buf.length);
      if (i > 0)
        this.count = i; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */