package sun.font;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FontFamily {
  private static ConcurrentHashMap<String, FontFamily> familyNameMap = new ConcurrentHashMap();
  
  private static HashMap<String, FontFamily> allLocaleNames;
  
  protected String familyName;
  
  protected Font2D plain;
  
  protected Font2D bold;
  
  protected Font2D italic;
  
  protected Font2D bolditalic;
  
  protected boolean logicalFont = false;
  
  protected int familyRank;
  
  private int familyWidth = 0;
  
  public static FontFamily getFamily(String paramString) { return (FontFamily)familyNameMap.get(paramString.toLowerCase(Locale.ENGLISH)); }
  
  public static String[] getAllFamilyNames() { return null; }
  
  static void remove(Font2D paramFont2D) {
    String str = paramFont2D.getFamilyName(Locale.ENGLISH);
    FontFamily fontFamily = getFamily(str);
    if (fontFamily == null)
      return; 
    if (fontFamily.plain == paramFont2D)
      fontFamily.plain = null; 
    if (fontFamily.bold == paramFont2D)
      fontFamily.bold = null; 
    if (fontFamily.italic == paramFont2D)
      fontFamily.italic = null; 
    if (fontFamily.bolditalic == paramFont2D)
      fontFamily.bolditalic = null; 
    if (fontFamily.plain == null && fontFamily.bold == null && fontFamily.plain == null && fontFamily.bold == null)
      familyNameMap.remove(str); 
  }
  
  public FontFamily(String paramString, boolean paramBoolean, int paramInt) {
    this.logicalFont = paramBoolean;
    this.familyName = paramString;
    this.familyRank = paramInt;
    familyNameMap.put(paramString.toLowerCase(Locale.ENGLISH), this);
  }
  
  FontFamily(String paramString) {
    this.logicalFont = false;
    this.familyName = paramString;
    this.familyRank = 4;
  }
  
  public String getFamilyName() { return this.familyName; }
  
  public int getRank() { return this.familyRank; }
  
  private boolean isFromSameSource(Font2D paramFont2D) {
    if (!(paramFont2D instanceof FileFont))
      return false; 
    FileFont fileFont1 = null;
    if (this.plain instanceof FileFont) {
      fileFont1 = (FileFont)this.plain;
    } else if (this.bold instanceof FileFont) {
      fileFont1 = (FileFont)this.bold;
    } else if (this.italic instanceof FileFont) {
      fileFont1 = (FileFont)this.italic;
    } else if (this.bolditalic instanceof FileFont) {
      fileFont1 = (FileFont)this.bolditalic;
    } 
    if (fileFont1 == null)
      return false; 
    File file1 = (new File(fileFont1.platName)).getParentFile();
    FileFont fileFont2 = (FileFont)paramFont2D;
    File file2 = (new File(fileFont2.platName)).getParentFile();
    if (file1 != null)
      try {
        file1 = file1.getCanonicalFile();
      } catch (IOException iOException) {} 
    if (file2 != null)
      try {
        file2 = file2.getCanonicalFile();
      } catch (IOException iOException) {} 
    return Objects.equals(file2, file1);
  }
  
  private boolean preferredWidth(Font2D paramFont2D) {
    int i = paramFont2D.getWidth();
    if (this.familyWidth == 0) {
      this.familyWidth = i;
      return true;
    } 
    if (i == this.familyWidth)
      return true; 
    if (Math.abs(5 - i) < Math.abs(5 - this.familyWidth)) {
      if (FontUtilities.debugFonts())
        FontUtilities.getLogger().info("Found more preferred width. New width = " + i + " Old width = " + this.familyWidth + " in font " + paramFont2D + " nulling out fonts plain: " + this.plain + " bold: " + this.bold + " italic: " + this.italic + " bolditalic: " + this.bolditalic); 
      this.familyWidth = i;
      this.plain = this.bold = this.italic = this.bolditalic = null;
      return true;
    } 
    if (FontUtilities.debugFonts())
      FontUtilities.getLogger().info("Family rejecting font " + paramFont2D + " of less preferred width " + i); 
    return false;
  }
  
  private boolean closerWeight(Font2D paramFont2D1, Font2D paramFont2D2, int paramInt) {
    if (this.familyWidth != paramFont2D2.getWidth())
      return false; 
    if (paramFont2D1 == null)
      return true; 
    if (FontUtilities.debugFonts())
      FontUtilities.getLogger().info("New weight for style " + paramInt + ". Curr.font=" + paramFont2D1 + " New font=" + paramFont2D2 + " Curr.weight=" + paramFont2D1.getWeight() + " New weight=" + paramFont2D2.getWeight()); 
    int i = paramFont2D2.getWeight();
    switch (paramInt) {
      case 0:
      case 2:
        return (i <= 400 && i > paramFont2D1.getWeight());
      case 1:
      case 3:
        return (Math.abs(i - 700) < Math.abs(paramFont2D1.getWeight() - 700));
    } 
    return false;
  }
  
  public void setFont(Font2D paramFont2D, int paramInt) {
    if (FontUtilities.isLogging()) {
      String str;
      if (paramFont2D instanceof CompositeFont) {
        str = "Request to add " + paramFont2D.getFamilyName(null) + " with style " + paramInt + " to family " + this.familyName;
      } else {
        str = "Request to add " + paramFont2D + " with style " + paramInt + " to family " + this;
      } 
      FontUtilities.getLogger().info(str);
    } 
    if (paramFont2D.getRank() > this.familyRank && !isFromSameSource(paramFont2D)) {
      if (FontUtilities.isLogging())
        FontUtilities.getLogger().warning("Rejecting adding " + paramFont2D + " of lower rank " + paramFont2D.getRank() + " to family " + this + " of rank " + this.familyRank); 
      return;
    } 
    switch (paramInt) {
      case 0:
        if (preferredWidth(paramFont2D) && closerWeight(this.plain, paramFont2D, paramInt))
          this.plain = paramFont2D; 
        break;
      case 1:
        if (preferredWidth(paramFont2D) && closerWeight(this.bold, paramFont2D, paramInt))
          this.bold = paramFont2D; 
        break;
      case 2:
        if (preferredWidth(paramFont2D) && closerWeight(this.italic, paramFont2D, paramInt))
          this.italic = paramFont2D; 
        break;
      case 3:
        if (preferredWidth(paramFont2D) && closerWeight(this.bolditalic, paramFont2D, paramInt))
          this.bolditalic = paramFont2D; 
        break;
    } 
  }
  
  public Font2D getFontWithExactStyleMatch(int paramInt) {
    switch (paramInt) {
      case 0:
        return this.plain;
      case 1:
        return this.bold;
      case 2:
        return this.italic;
      case 3:
        return this.bolditalic;
    } 
    return null;
  }
  
  public Font2D getFont(int paramInt) {
    switch (paramInt) {
      case 0:
        return this.plain;
      case 1:
        return (this.bold != null) ? this.bold : ((this.plain != null && this.plain.canDoStyle(paramInt)) ? this.plain : null);
      case 2:
        return (this.italic != null) ? this.italic : ((this.plain != null && this.plain.canDoStyle(paramInt)) ? this.plain : null);
      case 3:
        return (this.bolditalic != null) ? this.bolditalic : ((this.bold != null && this.bold.canDoStyle(paramInt)) ? this.bold : ((this.italic != null && this.italic.canDoStyle(paramInt)) ? this.italic : ((this.plain != null && this.plain.canDoStyle(paramInt)) ? this.plain : null)));
    } 
    return null;
  }
  
  Font2D getClosestStyle(int paramInt) {
    switch (paramInt) {
      case 0:
        return (this.bold != null) ? this.bold : ((this.italic != null) ? this.italic : this.bolditalic);
      case 1:
        return (this.plain != null) ? this.plain : ((this.bolditalic != null) ? this.bolditalic : this.italic);
      case 2:
        return (this.bolditalic != null) ? this.bolditalic : ((this.plain != null) ? this.plain : this.bold);
      case 3:
        return (this.italic != null) ? this.italic : ((this.bold != null) ? this.bold : this.plain);
    } 
    return null;
  }
  
  static void addLocaleNames(FontFamily paramFontFamily, String[] paramArrayOfString) {
    if (allLocaleNames == null)
      allLocaleNames = new HashMap(); 
    for (byte b = 0; b < paramArrayOfString.length; b++)
      allLocaleNames.put(paramArrayOfString[b].toLowerCase(), paramFontFamily); 
  }
  
  public static FontFamily getLocaleFamily(String paramString) { return (allLocaleNames == null) ? null : (FontFamily)allLocaleNames.get(paramString.toLowerCase()); }
  
  public static FontFamily[] getAllFontFamilies() {
    Collection collection = familyNameMap.values();
    return (FontFamily[])collection.toArray(new FontFamily[0]);
  }
  
  public String toString() { return "Font family: " + this.familyName + " plain=" + this.plain + " bold=" + this.bold + " italic=" + this.italic + " bolditalic=" + this.bolditalic; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontFamily.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */