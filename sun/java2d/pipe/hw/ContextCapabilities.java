package sun.java2d.pipe.hw;

public class ContextCapabilities {
  public static final int CAPS_EMPTY = 0;
  
  public static final int CAPS_RT_PLAIN_ALPHA = 2;
  
  public static final int CAPS_RT_TEXTURE_ALPHA = 4;
  
  public static final int CAPS_RT_TEXTURE_OPAQUE = 8;
  
  public static final int CAPS_MULTITEXTURE = 16;
  
  public static final int CAPS_TEXNONPOW2 = 32;
  
  public static final int CAPS_TEXNONSQUARE = 64;
  
  public static final int CAPS_PS20 = 128;
  
  public static final int CAPS_PS30 = 256;
  
  protected static final int FIRST_PRIVATE_CAP = 65536;
  
  protected final int caps;
  
  protected final String adapterId;
  
  protected ContextCapabilities(int paramInt, String paramString) {
    this.caps = paramInt;
    this.adapterId = (paramString != null) ? paramString : "unknown adapter";
  }
  
  public String getAdapterId() { return this.adapterId; }
  
  public int getCaps() { return this.caps; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("ContextCapabilities: adapter=" + this.adapterId + ", caps=");
    if (this.caps == 0) {
      stringBuffer.append("CAPS_EMPTY");
    } else {
      if ((this.caps & 0x2) != 0)
        stringBuffer.append("CAPS_RT_PLAIN_ALPHA|"); 
      if ((this.caps & 0x4) != 0)
        stringBuffer.append("CAPS_RT_TEXTURE_ALPHA|"); 
      if ((this.caps & 0x8) != 0)
        stringBuffer.append("CAPS_RT_TEXTURE_OPAQUE|"); 
      if ((this.caps & 0x10) != 0)
        stringBuffer.append("CAPS_MULTITEXTURE|"); 
      if ((this.caps & 0x20) != 0)
        stringBuffer.append("CAPS_TEXNONPOW2|"); 
      if ((this.caps & 0x40) != 0)
        stringBuffer.append("CAPS_TEXNONSQUARE|"); 
      if ((this.caps & 0x80) != 0)
        stringBuffer.append("CAPS_PS20|"); 
      if ((this.caps & 0x100) != 0)
        stringBuffer.append("CAPS_PS30|"); 
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\pipe\hw\ContextCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */