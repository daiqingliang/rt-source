package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

class TextLayoutStrategy extends FlowView.FlowStrategy {
  private LineBreakMeasurer measurer;
  
  private AttributedSegment text = new AttributedSegment();
  
  public void insertUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle) {
    sync(paramFlowView);
    super.insertUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void removeUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle) {
    sync(paramFlowView);
    super.removeUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void changedUpdate(FlowView paramFlowView, DocumentEvent paramDocumentEvent, Rectangle paramRectangle) {
    sync(paramFlowView);
    super.changedUpdate(paramFlowView, paramDocumentEvent, paramRectangle);
  }
  
  public void layout(FlowView paramFlowView) { super.layout(paramFlowView); }
  
  protected int layoutRow(FlowView paramFlowView, int paramInt1, int paramInt2) {
    int i = super.layoutRow(paramFlowView, paramInt1, paramInt2);
    View view = paramFlowView.getView(paramInt1);
    Document document = paramFlowView.getDocument();
    Object object = document.getProperty("i18n");
    if (object != null && object.equals(Boolean.TRUE)) {
      int j = view.getViewCount();
      if (j > 1) {
        AbstractDocument abstractDocument = (AbstractDocument)paramFlowView.getDocument();
        Element element = abstractDocument.getBidiRootElement();
        byte[] arrayOfByte = new byte[j];
        View[] arrayOfView = new View[j];
        for (byte b = 0; b < j; b++) {
          View view1 = view.getView(b);
          int k = element.getElementIndex(view1.getStartOffset());
          Element element1 = element.getElement(k);
          arrayOfByte[b] = (byte)StyleConstants.getBidiLevel(element1.getAttributes());
          arrayOfView[b] = view1;
        } 
        BidiUtils.reorderVisually(arrayOfByte, arrayOfView);
        view.replace(0, j, arrayOfView);
      } 
    } 
    return i;
  }
  
  protected void adjustRow(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3) {}
  
  protected View createView(FlowView paramFlowView, int paramInt1, int paramInt2, int paramInt3) {
    View view4;
    View view1 = getLogicalView(paramFlowView);
    View view2 = paramFlowView.getView(paramInt3);
    boolean bool = !(this.viewBuffer.size() == 0);
    int i = view1.getViewIndex(paramInt1, Position.Bias.Forward);
    View view3 = view1.getView(i);
    int j = getLimitingOffset(view3, paramInt1, paramInt2, bool);
    if (j == paramInt1)
      return null; 
    if (paramInt1 == view3.getStartOffset() && j == view3.getEndOffset()) {
      view4 = view3;
    } else {
      view4 = view3.createFragment(paramInt1, j);
    } 
    if (view4 instanceof GlyphView && this.measurer != null) {
      boolean bool1 = false;
      int k = view4.getStartOffset();
      int m = view4.getEndOffset();
      if (m - k == 1) {
        Segment segment = ((GlyphView)view4).getText(k, m);
        char c = segment.first();
        if (c == '\t')
          bool1 = true; 
      } 
      TextLayout textLayout = bool1 ? null : this.measurer.nextLayout(paramInt2, this.text.toIteratorIndex(j), bool);
      if (textLayout != null)
        ((GlyphView)view4).setGlyphPainter(new GlyphPainter2(textLayout)); 
    } 
    return view4;
  }
  
  int getLimitingOffset(View paramView, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = paramView.getEndOffset();
    Document document = paramView.getDocument();
    if (document instanceof AbstractDocument) {
      AbstractDocument abstractDocument = (AbstractDocument)document;
      Element element = abstractDocument.getBidiRootElement();
      if (element.getElementCount() > 1) {
        int k = element.getElementIndex(paramInt1);
        Element element1 = element.getElement(k);
        i = Math.min(element1.getEndOffset(), i);
      } 
    } 
    if (paramView instanceof GlyphView) {
      Segment segment = ((GlyphView)paramView).getText(paramInt1, i);
      char c = segment.first();
      if (c == '\t') {
        i = paramInt1 + 1;
      } else {
        for (c = segment.next(); c != Character.MAX_VALUE; c = segment.next()) {
          if (c == '\t') {
            i = paramInt1 + segment.getIndex() - segment.getBeginIndex();
            break;
          } 
        } 
      } 
    } 
    int j = this.text.toIteratorIndex(i);
    if (this.measurer != null) {
      int k = this.text.toIteratorIndex(paramInt1);
      if (this.measurer.getPosition() != k)
        this.measurer.setPosition(k); 
      j = this.measurer.nextOffset(paramInt2, j, paramBoolean);
    } 
    return this.text.toModelPosition(j);
  }
  
  void sync(FlowView paramFlowView) {
    BreakIterator breakIterator;
    View view = getLogicalView(paramFlowView);
    this.text.setView(view);
    Container container1 = paramFlowView.getContainer();
    FontRenderContext fontRenderContext = SwingUtilities2.getFontRenderContext(container1);
    Container container2 = paramFlowView.getContainer();
    if (container2 != null) {
      breakIterator = BreakIterator.getLineInstance(container2.getLocale());
    } else {
      breakIterator = BreakIterator.getLineInstance();
    } 
    Object object = null;
    if (container2 instanceof JComponent)
      object = ((JComponent)container2).getClientProperty(TextAttribute.NUMERIC_SHAPING); 
    this.text.setShaper(object);
    this.measurer = new LineBreakMeasurer(this.text, breakIterator, fontRenderContext);
    int i = view.getViewCount();
    for (byte b = 0; b < i; b++) {
      View view1 = view.getView(b);
      if (view1 instanceof GlyphView) {
        int j = view1.getStartOffset();
        int k = view1.getEndOffset();
        this.measurer.setPosition(this.text.toIteratorIndex(j));
        TextLayout textLayout = this.measurer.nextLayout(Float.MAX_VALUE, this.text.toIteratorIndex(k), false);
        ((GlyphView)view1).setGlyphPainter(new GlyphPainter2(textLayout));
      } 
    } 
    this.measurer.setPosition(this.text.getBeginIndex());
  }
  
  static class AttributedSegment extends Segment implements AttributedCharacterIterator {
    View v;
    
    static Set<AttributedCharacterIterator.Attribute> keys = new HashSet();
    
    private Object shaper = null;
    
    View getView() { return this.v; }
    
    void setView(View param1View) {
      this.v = param1View;
      Document document = param1View.getDocument();
      int i = param1View.getStartOffset();
      int j = param1View.getEndOffset();
      try {
        document.getText(i, j - i, this);
      } catch (BadLocationException badLocationException) {
        throw new IllegalArgumentException("Invalid view");
      } 
      first();
    }
    
    int getFontBoundary(int param1Int1, int param1Int2) {
      View view = this.v.getView(param1Int1);
      Font font = getFont(param1Int1);
      for (param1Int1 += param1Int2; param1Int1 >= 0 && param1Int1 < this.v.getViewCount(); param1Int1 += param1Int2) {
        Font font1 = getFont(param1Int1);
        if (font1 != font)
          break; 
        view = this.v.getView(param1Int1);
      } 
      return (param1Int2 < 0) ? view.getStartOffset() : view.getEndOffset();
    }
    
    Font getFont(int param1Int) {
      View view = this.v.getView(param1Int);
      return (view instanceof GlyphView) ? ((GlyphView)view).getFont() : null;
    }
    
    int toModelPosition(int param1Int) { return this.v.getStartOffset() + param1Int - getBeginIndex(); }
    
    int toIteratorIndex(int param1Int) { return param1Int - this.v.getStartOffset() + getBeginIndex(); }
    
    private void setShaper(Object param1Object) { this.shaper = param1Object; }
    
    public int getRunStart() {
      int i = toModelPosition(getIndex());
      int j = this.v.getViewIndex(i, Position.Bias.Forward);
      View view = this.v.getView(j);
      return toIteratorIndex(view.getStartOffset());
    }
    
    public int getRunStart(AttributedCharacterIterator.Attribute param1Attribute) {
      if (param1Attribute instanceof TextAttribute) {
        int i = toModelPosition(getIndex());
        int j = this.v.getViewIndex(i, Position.Bias.Forward);
        if (param1Attribute == TextAttribute.FONT)
          return toIteratorIndex(getFontBoundary(j, -1)); 
      } 
      return getBeginIndex();
    }
    
    public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> param1Set) {
      int i = getBeginIndex();
      Object[] arrayOfObject = param1Set.toArray();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        TextAttribute textAttribute = (TextAttribute)arrayOfObject[b];
        i = Math.max(getRunStart(textAttribute), i);
      } 
      return Math.min(getIndex(), i);
    }
    
    public int getRunLimit() {
      int i = toModelPosition(getIndex());
      int j = this.v.getViewIndex(i, Position.Bias.Forward);
      View view = this.v.getView(j);
      return toIteratorIndex(view.getEndOffset());
    }
    
    public int getRunLimit(AttributedCharacterIterator.Attribute param1Attribute) {
      if (param1Attribute instanceof TextAttribute) {
        int i = toModelPosition(getIndex());
        int j = this.v.getViewIndex(i, Position.Bias.Forward);
        if (param1Attribute == TextAttribute.FONT)
          return toIteratorIndex(getFontBoundary(j, 1)); 
      } 
      return getEndIndex();
    }
    
    public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> param1Set) {
      int i = getEndIndex();
      Object[] arrayOfObject = param1Set.toArray();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        TextAttribute textAttribute = (TextAttribute)arrayOfObject[b];
        i = Math.min(getRunLimit(textAttribute), i);
      } 
      return Math.max(getIndex(), i);
    }
    
    public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
      Object[] arrayOfObject = keys.toArray();
      Hashtable hashtable = new Hashtable();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        TextAttribute textAttribute = (TextAttribute)arrayOfObject[b];
        Object object = getAttribute(textAttribute);
        if (object != null)
          hashtable.put(textAttribute, object); 
      } 
      return hashtable;
    }
    
    public Object getAttribute(AttributedCharacterIterator.Attribute param1Attribute) {
      int i = toModelPosition(getIndex());
      int j = this.v.getViewIndex(i, Position.Bias.Forward);
      return (param1Attribute == TextAttribute.FONT) ? getFont(j) : ((param1Attribute == TextAttribute.RUN_DIRECTION) ? this.v.getDocument().getProperty(TextAttribute.RUN_DIRECTION) : ((param1Attribute == TextAttribute.NUMERIC_SHAPING) ? this.shaper : null));
    }
    
    public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() { return keys; }
    
    static  {
      keys.add(TextAttribute.FONT);
      keys.add(TextAttribute.RUN_DIRECTION);
      keys.add(TextAttribute.NUMERIC_SHAPING);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\TextLayoutStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */