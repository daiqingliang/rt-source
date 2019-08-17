package sun.text.normalizer;

import java.util.HashMap;

public final class VersionInfo {
  private int m_version_;
  
  private static final HashMap<Integer, Object> MAP_ = new HashMap();
  
  private static final String INVALID_VERSION_NUMBER_ = "Invalid version number: Version number may be negative or greater than 255";
  
  public static VersionInfo getInstance(String paramString) {
    int i = paramString.length();
    int[] arrayOfInt = { 0, 0, 0, 0 };
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b1 < 4 && b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if (c == '.') {
        b1++;
      } else {
        c = (char)(c - '0');
        if (c < '\000' || c > '\t')
          throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255"); 
        arrayOfInt[b1] = arrayOfInt[b1] * 10;
        arrayOfInt[b1] = arrayOfInt[b1] + c;
      } 
    } 
    if (b2 != i)
      throw new IllegalArgumentException("Invalid version number: String '" + paramString + "' exceeds version format"); 
    for (byte b3 = 0; b3 < 4; b3++) {
      if (arrayOfInt[b3] < 0 || arrayOfInt[b3] > 255)
        throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255"); 
    } 
    return getInstance(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
  }
  
  public static VersionInfo getInstance(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 < 0 || paramInt1 > 255 || paramInt2 < 0 || paramInt2 > 255 || paramInt3 < 0 || paramInt3 > 255 || paramInt4 < 0 || paramInt4 > 255)
      throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255"); 
    int i = getInt(paramInt1, paramInt2, paramInt3, paramInt4);
    Integer integer = Integer.valueOf(i);
    Object object = MAP_.get(integer);
    if (object == null) {
      object = new VersionInfo(i);
      MAP_.put(integer, object);
    } 
    return (VersionInfo)object;
  }
  
  public int compareTo(VersionInfo paramVersionInfo) { return this.m_version_ - paramVersionInfo.m_version_; }
  
  private VersionInfo(int paramInt) { this.m_version_ = paramInt; }
  
  private static int getInt(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return paramInt1 << 24 | paramInt2 << 16 | paramInt3 << 8 | paramInt4; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\VersionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */