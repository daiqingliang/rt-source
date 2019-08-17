package sun.java2d.cmm.lcms;

import java.util.Arrays;
import java.util.HashMap;
import sun.java2d.cmm.Profile;

final class LCMSProfile extends Profile {
  private final TagCache tagCache;
  
  private final Object disposerReferent;
  
  LCMSProfile(long paramLong, Object paramObject) {
    super(paramLong);
    this.disposerReferent = paramObject;
    this.tagCache = new TagCache(this);
  }
  
  final long getLcmsPtr() { return getNativePtr(); }
  
  TagData getTag(int paramInt) { return this.tagCache.getTag(paramInt); }
  
  void clearTagCache() { this.tagCache.clear(); }
  
  static class TagCache {
    final LCMSProfile profile;
    
    private HashMap<Integer, LCMSProfile.TagData> tags;
    
    TagCache(LCMSProfile param1LCMSProfile) {
      this.profile = param1LCMSProfile;
      this.tags = new HashMap();
    }
    
    LCMSProfile.TagData getTag(int param1Int) {
      LCMSProfile.TagData tagData = (LCMSProfile.TagData)this.tags.get(Integer.valueOf(param1Int));
      if (tagData == null) {
        byte[] arrayOfByte = LCMS.getTagNative(this.profile.getNativePtr(), param1Int);
        if (arrayOfByte != null) {
          tagData = new LCMSProfile.TagData(param1Int, arrayOfByte);
          this.tags.put(Integer.valueOf(param1Int), tagData);
        } 
      } 
      return tagData;
    }
    
    void clear() { this.tags.clear(); }
  }
  
  static class TagData {
    private int signature;
    
    private byte[] data;
    
    TagData(int param1Int, byte[] param1ArrayOfByte) {
      this.signature = param1Int;
      this.data = param1ArrayOfByte;
    }
    
    int getSize() { return this.data.length; }
    
    byte[] getData() { return Arrays.copyOf(this.data, this.data.length); }
    
    void copyDataTo(byte[] param1ArrayOfByte) { System.arraycopy(this.data, 0, param1ArrayOfByte, 0, this.data.length); }
    
    int getSignature() { return this.signature; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\lcms\LCMSProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */