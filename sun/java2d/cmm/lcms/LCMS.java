package sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;
import sun.java2d.cmm.Profile;

public class LCMS implements PCMM {
  private static LCMS theLcms = null;
  
  public Profile loadProfile(byte[] paramArrayOfByte) {
    Object object = new Object();
    long l = loadProfileNative(paramArrayOfByte, object);
    return (l != 0L) ? new LCMSProfile(l, object) : null;
  }
  
  private native long loadProfileNative(byte[] paramArrayOfByte, Object paramObject);
  
  private LCMSProfile getLcmsProfile(Profile paramProfile) {
    if (paramProfile instanceof LCMSProfile)
      return (LCMSProfile)paramProfile; 
    throw new CMMException("Invalid profile: " + paramProfile);
  }
  
  public void freeProfile(Profile paramProfile) {}
  
  public int getProfileSize(Profile paramProfile) {
    synchronized (paramProfile) {
      return getProfileSizeNative(getLcmsProfile(paramProfile).getLcmsPtr());
    } 
  }
  
  private native int getProfileSizeNative(long paramLong);
  
  public void getProfileData(Profile paramProfile, byte[] paramArrayOfByte) {
    synchronized (paramProfile) {
      getProfileDataNative(getLcmsProfile(paramProfile).getLcmsPtr(), paramArrayOfByte);
    } 
  }
  
  private native void getProfileDataNative(long paramLong, byte[] paramArrayOfByte);
  
  public int getTagSize(Profile paramProfile, int paramInt) {
    LCMSProfile lCMSProfile = getLcmsProfile(paramProfile);
    synchronized (lCMSProfile) {
      LCMSProfile.TagData tagData = lCMSProfile.getTag(paramInt);
      return (tagData == null) ? 0 : tagData.getSize();
    } 
  }
  
  static native byte[] getTagNative(long paramLong, int paramInt);
  
  public void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte) {
    LCMSProfile lCMSProfile = getLcmsProfile(paramProfile);
    synchronized (lCMSProfile) {
      LCMSProfile.TagData tagData = lCMSProfile.getTag(paramInt);
      if (tagData != null)
        tagData.copyDataTo(paramArrayOfByte); 
    } 
  }
  
  public void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte) {
    LCMSProfile lCMSProfile = getLcmsProfile(paramProfile);
    synchronized (lCMSProfile) {
      lCMSProfile.clearTagCache();
      setTagDataNative(lCMSProfile.getLcmsPtr(), paramInt, paramArrayOfByte);
    } 
  }
  
  private native void setTagDataNative(long paramLong, int paramInt, byte[] paramArrayOfByte);
  
  public static native LCMSProfile getProfileID(ICC_Profile paramICC_Profile);
  
  static long createTransform(LCMSProfile[] paramArrayOfLCMSProfile, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject) {
    long[] arrayOfLong = new long[paramArrayOfLCMSProfile.length];
    for (byte b = 0; b < paramArrayOfLCMSProfile.length; b++) {
      if (paramArrayOfLCMSProfile[b] == null)
        throw new CMMException("Unknown profile ID"); 
      arrayOfLong[b] = paramArrayOfLCMSProfile[b].getLcmsPtr();
    } 
    return createNativeTransform(arrayOfLong, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2, paramObject);
  }
  
  private static native long createNativeTransform(long[] paramArrayOfLong, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, Object paramObject);
  
  public ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2) { return new LCMSTransform(paramICC_Profile, paramInt1, paramInt1); }
  
  public ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform) { return new LCMSTransform(paramArrayOfColorTransform); }
  
  public static native void colorConvert(LCMSTransform paramLCMSTransform, LCMSImageLayout paramLCMSImageLayout1, LCMSImageLayout paramLCMSImageLayout2);
  
  public static native void freeTransform(long paramLong);
  
  public static native void initLCMS(Class paramClass1, Class paramClass2, Class paramClass3);
  
  static PCMM getModule() {
    if (theLcms != null)
      return theLcms; 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            System.loadLibrary("awt");
            System.loadLibrary("lcms");
            return null;
          }
        });
    initLCMS(LCMSTransform.class, LCMSImageLayout.class, ICC_Profile.class);
    theLcms = new LCMS();
    return theLcms;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\lcms\LCMS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */