package sun.font;

import java.awt.Font;

public abstract class FontAccess {
  private static FontAccess access;
  
  public static void setFontAccess(FontAccess paramFontAccess) {
    if (access != null)
      throw new InternalError("Attempt to set FontAccessor twice"); 
    access = paramFontAccess;
  }
  
  public static FontAccess getFontAccess() { return access; }
  
  public abstract Font2D getFont2D(Font paramFont);
  
  public abstract void setFont2D(Font paramFont, Font2DHandle paramFont2DHandle);
  
  public abstract void setCreatedFont(Font paramFont);
  
  public abstract boolean isCreatedFont(Font paramFont);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */