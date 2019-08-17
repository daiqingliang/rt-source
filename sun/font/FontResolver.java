package sun.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Map;
import sun.text.CodePointIterator;

public final class FontResolver {
  private Font[] allFonts;
  
  private Font[] supplementaryFonts;
  
  private int[] supplementaryIndices;
  
  private static final int DEFAULT_SIZE = 12;
  
  private Font defaultFont = new Font("Dialog", 0, 12);
  
  private static final int SHIFT = 9;
  
  private static final int BLOCKSIZE = 128;
  
  private static final int MASK = 127;
  
  private int[][] blocks = new int[512][];
  
  private static FontResolver INSTANCE;
  
  private Font[] getAllFonts() {
    if (this.allFonts == null) {
      this.allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
      for (byte b = 0; b < this.allFonts.length; b++)
        this.allFonts[b] = this.allFonts[b].deriveFont(12.0F); 
    } 
    return this.allFonts;
  }
  
  private int getIndexFor(char paramChar) {
    if (this.defaultFont.canDisplay(paramChar))
      return 1; 
    for (byte b = 0; b < getAllFonts().length; b++) {
      if (this.allFonts[b].canDisplay(paramChar))
        return b + 2; 
    } 
    return 1;
  }
  
  private Font[] getAllSCFonts() {
    if (this.supplementaryFonts == null) {
      ArrayList arrayList1 = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      int i;
      for (i = 0; i < getAllFonts().length; i++) {
        Font font = this.allFonts[i];
        Font2D font2D = FontUtilities.getFont2D(font);
        if (font2D.hasSupplementaryChars()) {
          arrayList1.add(font);
          arrayList2.add(Integer.valueOf(i));
        } 
      } 
      i = arrayList1.size();
      this.supplementaryIndices = new int[i];
      for (byte b = 0; b < i; b++)
        this.supplementaryIndices[b] = ((Integer)arrayList2.get(b)).intValue(); 
      this.supplementaryFonts = (Font[])arrayList1.toArray(new Font[i]);
    } 
    return this.supplementaryFonts;
  }
  
  private int getIndexFor(int paramInt) {
    if (this.defaultFont.canDisplay(paramInt))
      return 1; 
    for (byte b = 0; b < getAllSCFonts().length; b++) {
      if (this.supplementaryFonts[b].canDisplay(paramInt))
        return this.supplementaryIndices[b] + 2; 
    } 
    return 1;
  }
  
  public int getFontIndex(char paramChar) {
    char c1 = paramChar >> '\t';
    int[] arrayOfInt = this.blocks[c1];
    if (arrayOfInt == null) {
      arrayOfInt = new int[128];
      this.blocks[c1] = arrayOfInt;
    } 
    char c2 = paramChar & 0x7F;
    if (arrayOfInt[c2] == 0)
      arrayOfInt[c2] = getIndexFor(paramChar); 
    return arrayOfInt[c2];
  }
  
  public int getFontIndex(int paramInt) { return (paramInt < 65536) ? getFontIndex((char)paramInt) : getIndexFor(paramInt); }
  
  public int nextFontRunIndex(CodePointIterator paramCodePointIterator) {
    int i = paramCodePointIterator.next();
    int j = 1;
    if (i != -1) {
      j = getFontIndex(i);
      while ((i = paramCodePointIterator.next()) != -1) {
        if (getFontIndex(i) != j) {
          paramCodePointIterator.prev();
          break;
        } 
      } 
    } 
    return j;
  }
  
  public Font getFont(int paramInt, Map paramMap) {
    Font font = this.defaultFont;
    if (paramInt >= 2)
      font = this.allFonts[paramInt - 2]; 
    return font.deriveFont(paramMap);
  }
  
  public static FontResolver getInstance() {
    if (INSTANCE == null)
      INSTANCE = new FontResolver(); 
    return INSTANCE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */