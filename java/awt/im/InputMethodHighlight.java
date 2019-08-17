package java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Map;

public class InputMethodHighlight {
  public static final int RAW_TEXT = 0;
  
  public static final int CONVERTED_TEXT = 1;
  
  public static final InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 0);
  
  public static final InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 0);
  
  public static final InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 1);
  
  public static final InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 1);
  
  private boolean selected;
  
  private int state;
  
  private int variation;
  
  private Map<TextAttribute, ?> style;
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt) { this(paramBoolean, paramInt, 0, null); }
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt1, int paramInt2) { this(paramBoolean, paramInt1, paramInt2, null); }
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt1, int paramInt2, Map<TextAttribute, ?> paramMap) {
    this.selected = paramBoolean;
    if (paramInt1 != 0 && paramInt1 != 1)
      throw new IllegalArgumentException("unknown input method highlight state"); 
    this.state = paramInt1;
    this.variation = paramInt2;
    this.style = paramMap;
  }
  
  public boolean isSelected() { return this.selected; }
  
  public int getState() { return this.state; }
  
  public int getVariation() { return this.variation; }
  
  public Map<TextAttribute, ?> getStyle() { return this.style; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\im\InputMethodHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */