package sun.font;

public final class FontRunIterator {
  CompositeFont font;
  
  char[] text;
  
  int start;
  
  int limit;
  
  CompositeGlyphMapper mapper;
  
  int slot = -1;
  
  int pos;
  
  static final int SURROGATE_START = 65536;
  
  static final int LEAD_START = 55296;
  
  static final int LEAD_LIMIT = 56320;
  
  static final int TAIL_START = 56320;
  
  static final int TAIL_LIMIT = 57344;
  
  static final int LEAD_SURROGATE_SHIFT = 10;
  
  static final int SURROGATE_OFFSET = -56613888;
  
  static final int DONE = -1;
  
  public void init(CompositeFont paramCompositeFont, char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramCompositeFont == null || paramArrayOfChar == null || paramInt1 < 0 || paramInt2 < paramInt1 || paramInt2 > paramArrayOfChar.length)
      throw new IllegalArgumentException(); 
    this.font = paramCompositeFont;
    this.text = paramArrayOfChar;
    this.start = paramInt1;
    this.limit = paramInt2;
    this.mapper = (CompositeGlyphMapper)paramCompositeFont.getMapper();
    this.slot = -1;
    this.pos = paramInt1;
  }
  
  public PhysicalFont getFont() { return (this.slot == -1) ? null : this.font.getSlotFont(this.slot); }
  
  public int getGlyphMask() { return this.slot << 24; }
  
  public int getPos() { return this.pos; }
  
  public boolean next(int paramInt1, int paramInt2) {
    if (this.pos == paramInt2)
      return false; 
    int i = nextCodePoint(paramInt2);
    int j = this.mapper.charToGlyph(i) & 0xFF000000;
    this.slot = j >>> 24;
    while ((i = nextCodePoint(paramInt2)) != -1 && (this.mapper.charToGlyph(i) & 0xFF000000) == j);
    pushback(i);
    return true;
  }
  
  public boolean next() { return next(0, this.limit); }
  
  final int nextCodePoint() { return nextCodePoint(this.limit); }
  
  final int nextCodePoint(int paramInt) {
    if (this.pos >= paramInt)
      return -1; 
    char c = this.text[this.pos++];
    if (c >= '?' && c < '?' && this.pos < paramInt) {
      char c1 = this.text[this.pos];
      if (c1 >= '?' && c1 < '') {
        this.pos++;
        c = (c << '\n') + c1 + -56613888;
      } 
    } 
    return c;
  }
  
  final void pushback(int paramInt) {
    if (paramInt >= 0)
      if (paramInt >= 65536) {
        this.pos -= 2;
      } else {
        this.pos--;
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontRunIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */