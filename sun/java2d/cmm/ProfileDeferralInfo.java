package sun.java2d.cmm;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDeferralInfo extends InputStream {
  public int colorSpaceType;
  
  public int numComponents;
  
  public int profileClass;
  
  public String filename;
  
  public ProfileDeferralInfo(String paramString, int paramInt1, int paramInt2, int paramInt3) {
    this.filename = paramString;
    this.colorSpaceType = paramInt1;
    this.numComponents = paramInt2;
    this.profileClass = paramInt3;
  }
  
  public int read() throws IOException { return 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\cmm\ProfileDeferralInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */