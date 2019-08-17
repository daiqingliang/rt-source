package java.awt.font;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import sun.font.Decoration;
import sun.font.FontResolver;
import sun.text.CodePointIterator;

final class StyledParagraph {
  private int length;
  
  private Decoration decoration;
  
  private Object font;
  
  private Vector<Decoration> decorations;
  
  int[] decorationStarts;
  
  private Vector<Object> fonts;
  
  int[] fontStarts;
  
  private static int INITIAL_SIZE = 8;
  
  public StyledParagraph(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar) {
    int i = paramAttributedCharacterIterator.getBeginIndex();
    int j = paramAttributedCharacterIterator.getEndIndex();
    this.length = j - i;
    int k = i;
    paramAttributedCharacterIterator.first();
    do {
      int m = paramAttributedCharacterIterator.getRunLimit();
      int n = k - i;
      Map map = paramAttributedCharacterIterator.getAttributes();
      map = addInputMethodAttrs(map);
      Decoration decoration1 = Decoration.getDecoration(map);
      addDecoration(decoration1, n);
      Object object = getGraphicOrFont(map);
      if (object == null) {
        addFonts(paramArrayOfChar, map, n, m - i);
      } else {
        addFont(object, n);
      } 
      paramAttributedCharacterIterator.setIndex(m);
      k = m;
    } while (k < j);
    if (this.decorations != null)
      this.decorationStarts = addToVector(this, this.length, this.decorations, this.decorationStarts); 
    if (this.fonts != null)
      this.fontStarts = addToVector(this, this.length, this.fonts, this.fontStarts); 
  }
  
  private static void insertInto(int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    while (paramArrayOfInt[--paramInt2] > paramInt1)
      paramArrayOfInt[paramInt2] = paramArrayOfInt[paramInt2] + 1; 
  }
  
  public static StyledParagraph insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, int paramInt, StyledParagraph paramStyledParagraph) {
    char c = paramAttributedCharacterIterator.setIndex(paramInt);
    int i = Math.max(paramInt - paramAttributedCharacterIterator.getBeginIndex() - 1, 0);
    Map map = addInputMethodAttrs(paramAttributedCharacterIterator.getAttributes());
    Decoration decoration1 = Decoration.getDecoration(map);
    if (!paramStyledParagraph.getDecorationAt(i).equals(decoration1))
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar); 
    Object object = getGraphicOrFont(map);
    if (object == null) {
      FontResolver fontResolver = FontResolver.getInstance();
      int j = fontResolver.getFontIndex(c);
      object = fontResolver.getFont(j, map);
    } 
    if (!paramStyledParagraph.getFontOrGraphicAt(i).equals(object))
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar); 
    paramStyledParagraph.length++;
    if (paramStyledParagraph.decorations != null)
      insertInto(i, paramStyledParagraph.decorationStarts, paramStyledParagraph.decorations.size()); 
    if (paramStyledParagraph.fonts != null)
      insertInto(i, paramStyledParagraph.fontStarts, paramStyledParagraph.fonts.size()); 
    return paramStyledParagraph;
  }
  
  private static void deleteFrom(int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    while (paramArrayOfInt[--paramInt2] > paramInt1)
      paramArrayOfInt[paramInt2] = paramArrayOfInt[paramInt2] - 1; 
  }
  
  public static StyledParagraph deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, char[] paramArrayOfChar, int paramInt, StyledParagraph paramStyledParagraph) {
    paramInt -= paramAttributedCharacterIterator.getBeginIndex();
    if (paramStyledParagraph.decorations == null && paramStyledParagraph.fonts == null) {
      paramStyledParagraph.length--;
      return paramStyledParagraph;
    } 
    if (paramStyledParagraph.getRunLimit(paramInt) == paramInt + 1 && (paramInt == 0 || paramStyledParagraph.getRunLimit(paramInt - 1) == paramInt))
      return new StyledParagraph(paramAttributedCharacterIterator, paramArrayOfChar); 
    paramStyledParagraph.length--;
    if (paramStyledParagraph.decorations != null)
      deleteFrom(paramInt, paramStyledParagraph.decorationStarts, paramStyledParagraph.decorations.size()); 
    if (paramStyledParagraph.fonts != null)
      deleteFrom(paramInt, paramStyledParagraph.fontStarts, paramStyledParagraph.fonts.size()); 
    return paramStyledParagraph;
  }
  
  public int getRunLimit(int paramInt) {
    if (paramInt < 0 || paramInt >= this.length)
      throw new IllegalArgumentException("index out of range"); 
    int i = this.length;
    if (this.decorations != null) {
      int k = findRunContaining(paramInt, this.decorationStarts);
      i = this.decorationStarts[k + 1];
    } 
    int j = this.length;
    if (this.fonts != null) {
      int k = findRunContaining(paramInt, this.fontStarts);
      j = this.fontStarts[k + 1];
    } 
    return Math.min(i, j);
  }
  
  public Decoration getDecorationAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.length)
      throw new IllegalArgumentException("index out of range"); 
    if (this.decorations == null)
      return this.decoration; 
    int i = findRunContaining(paramInt, this.decorationStarts);
    return (Decoration)this.decorations.elementAt(i);
  }
  
  public Object getFontOrGraphicAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.length)
      throw new IllegalArgumentException("index out of range"); 
    if (this.fonts == null)
      return this.font; 
    int i = findRunContaining(paramInt, this.fontStarts);
    return this.fonts.elementAt(i);
  }
  
  private static int findRunContaining(int paramInt, int[] paramArrayOfInt) {
    for (byte b = 1;; b++) {
      if (paramArrayOfInt[b] > paramInt)
        return b - true; 
    } 
  }
  
  private static int[] addToVector(Object paramObject, int paramInt, Vector paramVector, int[] paramArrayOfInt) {
    if (!paramVector.lastElement().equals(paramObject)) {
      paramVector.addElement(paramObject);
      int i = paramVector.size();
      if (paramArrayOfInt.length == i) {
        int[] arrayOfInt = new int[paramArrayOfInt.length * 2];
        System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);
        paramArrayOfInt = arrayOfInt;
      } 
      paramArrayOfInt[i - 1] = paramInt;
    } 
    return paramArrayOfInt;
  }
  
  private void addDecoration(Decoration paramDecoration, int paramInt) {
    if (this.decorations != null) {
      this.decorationStarts = addToVector(paramDecoration, paramInt, this.decorations, this.decorationStarts);
    } else if (this.decoration == null) {
      this.decoration = paramDecoration;
    } else if (!this.decoration.equals(paramDecoration)) {
      this.decorations = new Vector(INITIAL_SIZE);
      this.decorations.addElement(this.decoration);
      this.decorations.addElement(paramDecoration);
      this.decorationStarts = new int[INITIAL_SIZE];
      this.decorationStarts[0] = 0;
      this.decorationStarts[1] = paramInt;
    } 
  }
  
  private void addFont(Object paramObject, int paramInt) {
    if (this.fonts != null) {
      this.fontStarts = addToVector(paramObject, paramInt, this.fonts, this.fontStarts);
    } else if (this.font == null) {
      this.font = paramObject;
    } else if (!this.font.equals(paramObject)) {
      this.fonts = new Vector(INITIAL_SIZE);
      this.fonts.addElement(this.font);
      this.fonts.addElement(paramObject);
      this.fontStarts = new int[INITIAL_SIZE];
      this.fontStarts[0] = 0;
      this.fontStarts[1] = paramInt;
    } 
  }
  
  private void addFonts(char[] paramArrayOfChar, Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt1, int paramInt2) {
    FontResolver fontResolver = FontResolver.getInstance();
    CodePointIterator codePointIterator = CodePointIterator.create(paramArrayOfChar, paramInt1, paramInt2);
    int i;
    for (i = codePointIterator.charIndex(); i < paramInt2; i = codePointIterator.charIndex()) {
      int j = fontResolver.nextFontRunIndex(codePointIterator);
      addFont(fontResolver.getFont(j, paramMap), i);
    } 
  }
  
  static Map<? extends AttributedCharacterIterator.Attribute, ?> addInputMethodAttrs(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    Object object = paramMap.get(TextAttribute.INPUT_METHOD_HIGHLIGHT);
    try {
      if (object != null) {
        if (object instanceof Annotation)
          object = ((Annotation)object).getValue(); 
        InputMethodHighlight inputMethodHighlight = (InputMethodHighlight)object;
        Map map = null;
        try {
          map = inputMethodHighlight.getStyle();
        } catch (NoSuchMethodError noSuchMethodError) {}
        if (map == null) {
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          map = toolkit.mapInputMethodHighlight(inputMethodHighlight);
        } 
        if (map != null) {
          HashMap hashMap = new HashMap(5, 0.9F);
          hashMap.putAll(paramMap);
          hashMap.putAll(map);
          return hashMap;
        } 
      } 
    } catch (ClassCastException classCastException) {}
    return paramMap;
  }
  
  private static Object getGraphicOrFont(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    Object object = paramMap.get(TextAttribute.CHAR_REPLACEMENT);
    if (object != null)
      return object; 
    object = paramMap.get(TextAttribute.FONT);
    return (object != null) ? object : ((paramMap.get(TextAttribute.FAMILY) != null) ? Font.getFont(paramMap) : null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\StyledParagraph.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */