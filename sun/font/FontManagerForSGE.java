package sun.font;

import java.awt.Font;
import java.util.Locale;
import java.util.TreeMap;

public interface FontManagerForSGE extends FontManager {
  Font[] getCreatedFonts();
  
  TreeMap<String, String> getCreatedFontFamilyNames();
  
  Font[] getAllInstalledFonts();
  
  String[] getInstalledFontFamilyNames(Locale paramLocale);
  
  void useAlternateFontforJALocales();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontManagerForSGE.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */