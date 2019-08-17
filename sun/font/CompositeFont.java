package sun.font;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public final class CompositeFont extends Font2D {
  private boolean[] deferredInitialisation;
  
  String[] componentFileNames;
  
  String[] componentNames;
  
  private PhysicalFont[] components;
  
  int numSlots;
  
  int numMetricsSlots;
  
  int[] exclusionRanges;
  
  int[] maxIndices;
  
  int numGlyphs = 0;
  
  int localeSlot = -1;
  
  boolean isStdComposite = true;
  
  public CompositeFont(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean, SunFontManager paramSunFontManager) {
    this.handle = new Font2DHandle(this);
    this.fullName = paramString;
    this.componentFileNames = paramArrayOfString1;
    this.componentNames = paramArrayOfString2;
    if (paramArrayOfString2 == null) {
      this.numSlots = this.componentFileNames.length;
    } else {
      this.numSlots = this.componentNames.length;
    } 
    this.numSlots = (this.numSlots <= 254) ? this.numSlots : 254;
    this.numMetricsSlots = paramInt;
    this.exclusionRanges = paramArrayOfInt1;
    this.maxIndices = paramArrayOfInt2;
    if (paramSunFontManager.getEUDCFont() != null) {
      int j = this.numMetricsSlots;
      int k = this.numSlots - j;
      this.numSlots++;
      if (this.componentNames != null) {
        this.componentNames = new String[this.numSlots];
        System.arraycopy(paramArrayOfString2, 0, this.componentNames, 0, j);
        this.componentNames[j] = paramSunFontManager.getEUDCFont().getFontName(null);
        System.arraycopy(paramArrayOfString2, j, this.componentNames, j + 1, k);
      } 
      if (this.componentFileNames != null) {
        this.componentFileNames = new String[this.numSlots];
        System.arraycopy(paramArrayOfString1, 0, this.componentFileNames, 0, j);
        System.arraycopy(paramArrayOfString1, j, this.componentFileNames, j + 1, k);
      } 
      this.components = new PhysicalFont[this.numSlots];
      this.components[j] = paramSunFontManager.getEUDCFont();
      this.deferredInitialisation = new boolean[this.numSlots];
      if (paramBoolean)
        for (byte b = 0; b < this.numSlots - 1; b++)
          this.deferredInitialisation[b] = true;  
    } else {
      this.components = new PhysicalFont[this.numSlots];
      this.deferredInitialisation = new boolean[this.numSlots];
      if (paramBoolean)
        for (byte b = 0; b < this.numSlots; b++)
          this.deferredInitialisation[b] = true;  
    } 
    this.fontRank = 2;
    int i = this.fullName.indexOf('.');
    if (i > 0) {
      this.familyName = this.fullName.substring(0, i);
      if (i + 1 < this.fullName.length()) {
        String str = this.fullName.substring(i + 1);
        if ("plain".equals(str)) {
          this.style = 0;
        } else if ("bold".equals(str)) {
          this.style = 1;
        } else if ("italic".equals(str)) {
          this.style = 2;
        } else if ("bolditalic".equals(str)) {
          this.style = 3;
        } 
      } 
    } else {
      this.familyName = this.fullName;
    } 
  }
  
  CompositeFont(PhysicalFont[] paramArrayOfPhysicalFont) {
    this.isStdComposite = false;
    this.handle = new Font2DHandle(this);
    this.fullName = (paramArrayOfPhysicalFont[0]).fullName;
    this.familyName = (paramArrayOfPhysicalFont[0]).familyName;
    this.style = (paramArrayOfPhysicalFont[0]).style;
    this.numMetricsSlots = 1;
    this.numSlots = paramArrayOfPhysicalFont.length;
    this.components = new PhysicalFont[this.numSlots];
    System.arraycopy(paramArrayOfPhysicalFont, 0, this.components, 0, this.numSlots);
    this.deferredInitialisation = new boolean[this.numSlots];
  }
  
  CompositeFont(PhysicalFont paramPhysicalFont, CompositeFont paramCompositeFont) {
    this.isStdComposite = false;
    this.handle = new Font2DHandle(this);
    this.fullName = paramPhysicalFont.fullName;
    this.familyName = paramPhysicalFont.familyName;
    this.style = paramPhysicalFont.style;
    this.numMetricsSlots = 1;
    paramCompositeFont.numSlots++;
    synchronized (FontManagerFactory.getInstance()) {
      this.components = new PhysicalFont[this.numSlots];
      this.components[0] = paramPhysicalFont;
      System.arraycopy(paramCompositeFont.components, 0, this.components, 1, paramCompositeFont.numSlots);
      if (paramCompositeFont.componentNames != null) {
        this.componentNames = new String[this.numSlots];
        this.componentNames[0] = paramPhysicalFont.fullName;
        System.arraycopy(paramCompositeFont.componentNames, 0, this.componentNames, 1, paramCompositeFont.numSlots);
      } 
      if (paramCompositeFont.componentFileNames != null) {
        this.componentFileNames = new String[this.numSlots];
        this.componentFileNames[0] = null;
        System.arraycopy(paramCompositeFont.componentFileNames, 0, this.componentFileNames, 1, paramCompositeFont.numSlots);
      } 
      this.deferredInitialisation = new boolean[this.numSlots];
      this.deferredInitialisation[0] = false;
      System.arraycopy(paramCompositeFont.deferredInitialisation, 0, this.deferredInitialisation, 1, paramCompositeFont.numSlots);
    } 
  }
  
  private void doDeferredInitialisation(int paramInt) {
    if (!this.deferredInitialisation[paramInt])
      return; 
    SunFontManager sunFontManager = SunFontManager.getInstance();
    synchronized (sunFontManager) {
      if (this.componentNames == null)
        this.componentNames = new String[this.numSlots]; 
      if (this.components[paramInt] == null) {
        if (this.componentFileNames != null && this.componentFileNames[paramInt] != null)
          this.components[paramInt] = sunFontManager.initialiseDeferredFont(this.componentFileNames[paramInt]); 
        if (this.components[paramInt] == null)
          this.components[paramInt] = sunFontManager.getDefaultPhysicalFont(); 
        String str = this.components[paramInt].getFontName(null);
        if (this.componentNames[paramInt] == null) {
          this.componentNames[paramInt] = str;
        } else if (!this.componentNames[paramInt].equalsIgnoreCase(str)) {
          try {
            this.components[paramInt] = (PhysicalFont)sunFontManager.findFont2D(this.componentNames[paramInt], this.style, 1);
          } catch (ClassCastException classCastException) {
            this.components[paramInt] = sunFontManager.getDefaultPhysicalFont();
          } 
        } 
      } 
      this.deferredInitialisation[paramInt] = false;
    } 
  }
  
  void replaceComponentFont(PhysicalFont paramPhysicalFont1, PhysicalFont paramPhysicalFont2) {
    if (this.components == null)
      return; 
    for (byte b = 0; b < this.numSlots; b++) {
      if (this.components[b] == paramPhysicalFont1) {
        this.components[b] = paramPhysicalFont2;
        if (this.componentNames != null)
          this.componentNames[b] = paramPhysicalFont2.getFontName(null); 
      } 
    } 
  }
  
  public boolean isExcludedChar(int paramInt1, int paramInt2) {
    if (this.exclusionRanges == null || this.maxIndices == null || paramInt1 >= this.numMetricsSlots)
      return false; 
    int i = 0;
    int j = this.maxIndices[paramInt1];
    if (paramInt1 > 0)
      i = this.maxIndices[paramInt1 - 1]; 
    for (int k = i; j > k; k += 2) {
      if (paramInt2 >= this.exclusionRanges[k] && paramInt2 <= this.exclusionRanges[k + 1])
        return true; 
    } 
    return false;
  }
  
  public void getStyleMetrics(float paramFloat, float[] paramArrayOfFloat, int paramInt) {
    PhysicalFont physicalFont = getSlotFont(0);
    if (physicalFont == null) {
      super.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
    } else {
      physicalFont.getStyleMetrics(paramFloat, paramArrayOfFloat, paramInt);
    } 
  }
  
  public int getNumSlots() { return this.numSlots; }
  
  public PhysicalFont getSlotFont(int paramInt) {
    if (this.deferredInitialisation[paramInt])
      doDeferredInitialisation(paramInt); 
    SunFontManager sunFontManager = SunFontManager.getInstance();
    try {
      PhysicalFont physicalFont = this.components[paramInt];
      if (physicalFont == null)
        try {
          physicalFont = (PhysicalFont)sunFontManager.findFont2D(this.componentNames[paramInt], this.style, 1);
          this.components[paramInt] = physicalFont;
        } catch (ClassCastException classCastException) {
          physicalFont = sunFontManager.getDefaultPhysicalFont();
        }  
      return physicalFont;
    } catch (Exception exception) {
      return sunFontManager.getDefaultPhysicalFont();
    } 
  }
  
  FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc) { return new CompositeStrike(this, paramFontStrikeDesc); }
  
  public boolean isStdComposite() { return this.isStdComposite; }
  
  protected int getValidatedGlyphCode(int paramInt) {
    int i = paramInt >>> 24;
    if (i >= this.numSlots)
      return getMapper().getMissingGlyphCode(); 
    int j = paramInt & 0xFFFFFF;
    PhysicalFont physicalFont = getSlotFont(i);
    return (physicalFont.getValidatedGlyphCode(j) == physicalFont.getMissingGlyphCode()) ? getMapper().getMissingGlyphCode() : paramInt;
  }
  
  public CharToGlyphMapper getMapper() {
    if (this.mapper == null)
      this.mapper = new CompositeGlyphMapper(this); 
    return this.mapper;
  }
  
  public boolean hasSupplementaryChars() {
    for (byte b = 0; b < this.numSlots; b++) {
      if (getSlotFont(b).hasSupplementaryChars())
        return true; 
    } 
    return false;
  }
  
  public int getNumGlyphs() {
    if (this.numGlyphs == 0)
      this.numGlyphs = getMapper().getNumGlyphs(); 
    return this.numGlyphs;
  }
  
  public int getMissingGlyphCode() { return getMapper().getMissingGlyphCode(); }
  
  public boolean canDisplay(char paramChar) { return getMapper().canDisplay(paramChar); }
  
  public boolean useAAForPtSize(int paramInt) {
    if (this.localeSlot == -1) {
      int i = this.numMetricsSlots;
      if (i == 1 && !isStdComposite())
        i = this.numSlots; 
      for (byte b = 0; b < i; b++) {
        if (getSlotFont(b).supportsEncoding(null)) {
          this.localeSlot = b;
          break;
        } 
      } 
      if (this.localeSlot == -1)
        this.localeSlot = 0; 
    } 
    return getSlotFont(this.localeSlot).useAAForPtSize(paramInt);
  }
  
  public String toString() {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
    String str2 = "";
    for (byte b = 0; b < this.numSlots; b++)
      str2 = str2 + "    Slot[" + b + "]=" + getSlotFont(b) + str1; 
    return "** Composite Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + str1 + str2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CompositeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */