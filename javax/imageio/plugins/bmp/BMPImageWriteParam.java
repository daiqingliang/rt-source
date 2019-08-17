package javax.imageio.plugins.bmp;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class BMPImageWriteParam extends ImageWriteParam {
  private boolean topDown = false;
  
  public BMPImageWriteParam(Locale paramLocale) { super(paramLocale); }
  
  public BMPImageWriteParam() { this(null); }
  
  public void setTopDown(boolean paramBoolean) { this.topDown = paramBoolean; }
  
  public boolean isTopDown() { return this.topDown; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\plugins\bmp\BMPImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */