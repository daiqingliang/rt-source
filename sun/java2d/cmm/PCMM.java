package sun.java2d.cmm;

import java.awt.color.ICC_Profile;

public interface PCMM {
  Profile loadProfile(byte[] paramArrayOfByte);
  
  void freeProfile(Profile paramProfile);
  
  int getProfileSize(Profile paramProfile);
  
  void getProfileData(Profile paramProfile, byte[] paramArrayOfByte);
  
  void getTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte);
  
  int getTagSize(Profile paramProfile, int paramInt);
  
  void setTagData(Profile paramProfile, int paramInt, byte[] paramArrayOfByte);
  
  ColorTransform createTransform(ICC_Profile paramICC_Profile, int paramInt1, int paramInt2);
  
  ColorTransform createTransform(ColorTransform[] paramArrayOfColorTransform);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\PCMM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */