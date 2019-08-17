package com.sun.imageio.plugins.gif;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class GIFImageWriteParam extends ImageWriteParam {
  GIFImageWriteParam(Locale paramLocale) { super(paramLocale); }
  
  public void setCompressionMode(int paramInt) {
    if (paramInt == 0)
      throw new UnsupportedOperationException("MODE_DISABLED is not supported."); 
    super.setCompressionMode(paramInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */