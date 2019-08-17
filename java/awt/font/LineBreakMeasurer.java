package java.awt.font;

import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;

public final class LineBreakMeasurer {
  private BreakIterator breakIter;
  
  private int start;
  
  private int pos;
  
  private int limit;
  
  private TextMeasurer measurer;
  
  private CharArrayIterator charIter;
  
  public LineBreakMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, FontRenderContext paramFontRenderContext) { this(paramAttributedCharacterIterator, BreakIterator.getLineInstance(), paramFontRenderContext); }
  
  public LineBreakMeasurer(AttributedCharacterIterator paramAttributedCharacterIterator, BreakIterator paramBreakIterator, FontRenderContext paramFontRenderContext) {
    if (paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex() < 1)
      throw new IllegalArgumentException("Text must contain at least one character."); 
    this.breakIter = paramBreakIterator;
    this.measurer = new TextMeasurer(paramAttributedCharacterIterator, paramFontRenderContext);
    this.limit = paramAttributedCharacterIterator.getEndIndex();
    this.pos = this.start = paramAttributedCharacterIterator.getBeginIndex();
    this.charIter = new CharArrayIterator(this.measurer.getChars(), this.start);
    this.breakIter.setText(this.charIter);
  }
  
  public int nextOffset(float paramFloat) { return nextOffset(paramFloat, this.limit, false); }
  
  public int nextOffset(float paramFloat, int paramInt, boolean paramBoolean) {
    int i = this.pos;
    if (this.pos < this.limit) {
      if (paramInt <= this.pos)
        throw new IllegalArgumentException("offsetLimit must be after current position"); 
      int j = this.measurer.getLineBreakIndex(this.pos, paramFloat);
      if (j == this.limit) {
        i = this.limit;
      } else if (Character.isWhitespace(this.measurer.getChars()[j - this.start])) {
        i = this.breakIter.following(j);
      } else {
        int k = j + 1;
        if (k == this.limit) {
          this.breakIter.last();
          i = this.breakIter.previous();
        } else {
          i = this.breakIter.preceding(k);
        } 
        if (i <= this.pos)
          if (paramBoolean) {
            i = this.pos;
          } else {
            i = Math.max(this.pos + 1, j);
          }  
      } 
    } 
    if (i > paramInt)
      i = paramInt; 
    return i;
  }
  
  public TextLayout nextLayout(float paramFloat) { return nextLayout(paramFloat, this.limit, false); }
  
  public TextLayout nextLayout(float paramFloat, int paramInt, boolean paramBoolean) {
    if (this.pos < this.limit) {
      int i = nextOffset(paramFloat, paramInt, paramBoolean);
      if (i == this.pos)
        return null; 
      TextLayout textLayout = this.measurer.getLayout(this.pos, i);
      this.pos = i;
      return textLayout;
    } 
    return null;
  }
  
  public int getPosition() { return this.pos; }
  
  public void setPosition(int paramInt) {
    if (paramInt < this.start || paramInt > this.limit)
      throw new IllegalArgumentException("position is out of range"); 
    this.pos = paramInt;
  }
  
  public void insertChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt) {
    this.measurer.insertChar(paramAttributedCharacterIterator, paramInt);
    this.limit = paramAttributedCharacterIterator.getEndIndex();
    this.pos = this.start = paramAttributedCharacterIterator.getBeginIndex();
    this.charIter.reset(this.measurer.getChars(), paramAttributedCharacterIterator.getBeginIndex());
    this.breakIter.setText(this.charIter);
  }
  
  public void deleteChar(AttributedCharacterIterator paramAttributedCharacterIterator, int paramInt) {
    this.measurer.deleteChar(paramAttributedCharacterIterator, paramInt);
    this.limit = paramAttributedCharacterIterator.getEndIndex();
    this.pos = this.start = paramAttributedCharacterIterator.getBeginIndex();
    this.charIter.reset(this.measurer.getChars(), this.start);
    this.breakIter.setText(this.charIter);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\LineBreakMeasurer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */