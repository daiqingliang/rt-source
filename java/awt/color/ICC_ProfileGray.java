package java.awt.color;

import sun.java2d.cmm.Profile;
import sun.java2d.cmm.ProfileDeferralInfo;

public class ICC_ProfileGray extends ICC_Profile {
  static final long serialVersionUID = -1124721290732002649L;
  
  ICC_ProfileGray(Profile paramProfile) { super(paramProfile); }
  
  ICC_ProfileGray(ProfileDeferralInfo paramProfileDeferralInfo) { super(paramProfileDeferralInfo); }
  
  public float[] getMediaWhitePoint() { return super.getMediaWhitePoint(); }
  
  public float getGamma() { return getGamma(1800688195); }
  
  public short[] getTRC() { return getTRC(1800688195); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\color\ICC_ProfileGray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */