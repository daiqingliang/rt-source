package sun.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;

public interface FontManager {
  public static final int NO_FALLBACK = 0;
  
  public static final int PHYSICAL_FALLBACK = 1;
  
  public static final int LOGICAL_FALLBACK = 2;
  
  boolean registerFont(Font paramFont);
  
  void deRegisterBadFont(Font2D paramFont2D);
  
  Font2D findFont2D(String paramString, int paramInt1, int paramInt2);
  
  Font2D createFont2D(File paramFile, int paramInt, boolean paramBoolean, CreatedFontTracker paramCreatedFontTracker) throws FontFormatException;
  
  boolean usingPerAppContextComposites();
  
  Font2DHandle getNewComposite(String paramString, int paramInt, Font2DHandle paramFont2DHandle);
  
  void preferLocaleFonts();
  
  void preferProportionalFonts();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */