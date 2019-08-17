package sun.awt;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.awt.windows.WFontConfiguration;
import sun.font.SunFontManager;
import sun.font.TrueTypeFont;

public final class Win32FontManager extends SunFontManager {
  private static TrueTypeFont eudcFont;
  
  static String fontsForPrinting;
  
  private static native String getEUDCFontFile();
  
  public TrueTypeFont getEUDCFont() { return eudcFont; }
  
  public Win32FontManager() { AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
            return null;
          }
        }); }
  
  protected boolean useAbsoluteFontFileNames() { return false; }
  
  protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) {
    byte b;
    if (this.registeredFontFiles.contains(paramString))
      return; 
    this.registeredFontFiles.add(paramString);
    if (getTrueTypeFilter().accept(null, paramString)) {
      b = 0;
    } else if (getType1Filter().accept(null, paramString)) {
      b = 1;
    } else {
      return;
    } 
    if (this.fontPath == null)
      this.fontPath = getPlatformFontPath(noType1Font); 
    String str = jreFontDirName + File.pathSeparator + this.fontPath;
    StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
    boolean bool = false;
    try {
      while (!bool && stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        boolean bool1 = str1.equals(jreFontDirName);
        File file = new File(str1, paramString);
        if (file.canRead()) {
          bool = true;
          String str2 = file.getAbsolutePath();
          if (paramBoolean) {
            registerDeferredFont(paramString, str2, paramArrayOfString, b, bool1, paramInt);
            break;
          } 
          registerFontFile(str2, paramArrayOfString, b, bool1, paramInt);
          break;
        } 
      } 
    } catch (NoSuchElementException noSuchElementException) {
      System.err.println(noSuchElementException);
    } 
    if (!bool)
      addToMissingFontFileList(paramString); 
  }
  
  protected FontConfiguration createFontConfiguration() {
    WFontConfiguration wFontConfiguration = new WFontConfiguration(this);
    wFontConfiguration.init();
    return wFontConfiguration;
  }
  
  public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2) { return new WFontConfiguration(this, paramBoolean1, paramBoolean2); }
  
  protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap3, Locale paramLocale) { populateFontFileNameMap0(paramHashMap1, paramHashMap2, paramHashMap3, paramLocale); }
  
  private static native void populateFontFileNameMap0(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap3, Locale paramLocale);
  
  protected native String getFontPath(boolean paramBoolean);
  
  protected String[] getDefaultPlatformFont() {
    String[] arrayOfString1 = new String[2];
    arrayOfString1[0] = "Arial";
    arrayOfString1[1] = "c:\\windows\\fonts";
    final String[] dirs = getPlatformFontDirs(true);
    if (arrayOfString2.length > 1) {
      String str = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              for (byte b = 0; b < dirs.length; b++) {
                String str = dirs[b] + File.separator + "arial.ttf";
                File file = new File(str);
                if (file.exists())
                  return dirs[b]; 
              } 
              return null;
            }
          });
      if (str != null)
        arrayOfString1[1] = str; 
    } else {
      arrayOfString1[1] = arrayOfString2[0];
    } 
    arrayOfString1[1] = arrayOfString1[1] + File.separator + "arial.ttf";
    return arrayOfString1;
  }
  
  protected void registerJREFontsWithPlatform(String paramString) { fontsForPrinting = paramString; }
  
  public static void registerJREFontsForPrinting() {
    final String pathName;
    synchronized (Win32GraphicsEnvironment.class) {
      GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (fontsForPrinting == null)
        return; 
      str = fontsForPrinting;
      fontsForPrinting = null;
    } 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            File file = new File(pathName);
            String[] arrayOfString = file.list(SunFontManager.getInstance().getTrueTypeFilter());
            if (arrayOfString == null)
              return null; 
            for (byte b = 0; b < arrayOfString.length; b++) {
              File file1 = new File(file, arrayOfString[b]);
              Win32FontManager.registerFontWithPlatform(file1.getAbsolutePath());
            } 
            return null;
          }
        });
  }
  
  protected static native void registerFontWithPlatform(String paramString);
  
  protected static native void deRegisterFontWithPlatform(String paramString);
  
  public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap() {
    HashMap hashMap = new HashMap();
    SunFontManager.FamilyDescription familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Segoe UI";
    familyDescription.plainFullName = "Segoe UI";
    familyDescription.plainFileName = "segoeui.ttf";
    familyDescription.boldFullName = "Segoe UI Bold";
    familyDescription.boldFileName = "segoeuib.ttf";
    familyDescription.italicFullName = "Segoe UI Italic";
    familyDescription.italicFileName = "segoeuii.ttf";
    familyDescription.boldItalicFullName = "Segoe UI Bold Italic";
    familyDescription.boldItalicFileName = "segoeuiz.ttf";
    hashMap.put("segoe", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Tahoma";
    familyDescription.plainFullName = "Tahoma";
    familyDescription.plainFileName = "tahoma.ttf";
    familyDescription.boldFullName = "Tahoma Bold";
    familyDescription.boldFileName = "tahomabd.ttf";
    hashMap.put("tahoma", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Verdana";
    familyDescription.plainFullName = "Verdana";
    familyDescription.plainFileName = "verdana.TTF";
    familyDescription.boldFullName = "Verdana Bold";
    familyDescription.boldFileName = "verdanab.TTF";
    familyDescription.italicFullName = "Verdana Italic";
    familyDescription.italicFileName = "verdanai.TTF";
    familyDescription.boldItalicFullName = "Verdana Bold Italic";
    familyDescription.boldItalicFileName = "verdanaz.TTF";
    hashMap.put("verdana", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Arial";
    familyDescription.plainFullName = "Arial";
    familyDescription.plainFileName = "ARIAL.TTF";
    familyDescription.boldFullName = "Arial Bold";
    familyDescription.boldFileName = "ARIALBD.TTF";
    familyDescription.italicFullName = "Arial Italic";
    familyDescription.italicFileName = "ARIALI.TTF";
    familyDescription.boldItalicFullName = "Arial Bold Italic";
    familyDescription.boldItalicFileName = "ARIALBI.TTF";
    hashMap.put("arial", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Symbol";
    familyDescription.plainFullName = "Symbol";
    familyDescription.plainFileName = "Symbol.TTF";
    hashMap.put("symbol", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "WingDings";
    familyDescription.plainFullName = "WingDings";
    familyDescription.plainFileName = "WINGDING.TTF";
    hashMap.put("wingdings", familyDescription);
    return hashMap;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str = Win32FontManager.getEUDCFontFile();
            if (str != null)
              try {
                eudcFont = new TrueTypeFont(str, null, 0, true, false);
              } catch (FontFormatException fontFormatException) {} 
            return null;
          }
        });
    fontsForPrinting = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Win32FontManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */