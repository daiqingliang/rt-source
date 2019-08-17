package javax.swing.text;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class FieldView extends PlainView {
  public FieldView(Element paramElement) { super(paramElement); }
  
  protected FontMetrics getFontMetrics() {
    Container container = getContainer();
    return container.getFontMetrics(container.getFont());
  }
  
  protected Shape adjustAllocation(Shape paramShape) {
    if (paramShape != null) {
      Rectangle rectangle = paramShape.getBounds();
      int i = (int)getPreferredSpan(1);
      int j = (int)getPreferredSpan(0);
      if (rectangle.height != i) {
        int k = rectangle.height - i;
        rectangle.y += k / 2;
        rectangle.height -= k;
      } 
      Container container = getContainer();
      if (container instanceof JTextField) {
        JTextField jTextField = (JTextField)container;
        BoundedRangeModel boundedRangeModel = jTextField.getHorizontalVisibility();
        int k = Math.max(j, rectangle.width);
        int m = boundedRangeModel.getValue();
        int n = Math.min(k, rectangle.width - 1);
        if (m + n > k)
          m = k - n; 
        boundedRangeModel.setRangeProperties(m, n, boundedRangeModel.getMinimum(), k, false);
        if (j < rectangle.width) {
          int i1 = rectangle.width - 1 - j;
          int i2 = ((JTextField)container).getHorizontalAlignment();
          if (Utilities.isLeftToRight(container)) {
            if (i2 == 10) {
              i2 = 2;
            } else if (i2 == 11) {
              i2 = 4;
            } 
          } else if (i2 == 10) {
            i2 = 4;
          } else if (i2 == 11) {
            i2 = 2;
          } 
          switch (i2) {
            case 0:
              rectangle.x += i1 / 2;
              rectangle.width -= i1;
              break;
            case 4:
              rectangle.x += i1;
              rectangle.width -= i1;
              break;
          } 
        } else {
          rectangle.width = j;
          rectangle.x -= boundedRangeModel.getValue();
        } 
      } 
      return rectangle;
    } 
    return null;
  }
  
  void updateVisibilityModel() {
    Container container = getContainer();
    if (container instanceof JTextField) {
      JTextField jTextField = (JTextField)container;
      BoundedRangeModel boundedRangeModel = jTextField.getHorizontalVisibility();
      int i = (int)getPreferredSpan(0);
      int j = boundedRangeModel.getExtent();
      int k = Math.max(i, j);
      j = (j == 0) ? k : j;
      int m = k - j;
      int n = boundedRangeModel.getValue();
      if (n + j > k)
        n = k - j; 
      m = Math.max(0, Math.min(m, n));
      boundedRangeModel.setRangeProperties(m, j, 0, k, false);
    } 
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = (Rectangle)paramShape;
    paramGraphics.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    super.paint(paramGraphics, paramShape);
  }
  
  Shape adjustPaintRegion(Shape paramShape) { return adjustAllocation(paramShape); }
  
  public float getPreferredSpan(int paramInt) {
    int i;
    Document document;
    Segment segment;
    switch (paramInt) {
      case 0:
        segment = SegmentCache.getSharedSegment();
        document = getDocument();
        try {
          FontMetrics fontMetrics = getFontMetrics();
          document.getText(0, document.getLength(), segment);
          i = Utilities.getTabbedTextWidth(segment, fontMetrics, 0, this, 0);
          if (segment.count > 0) {
            Container container = getContainer();
            this.firstLineOffset = SwingUtilities2.getLeftSideBearing((container instanceof JComponent) ? (JComponent)container : null, fontMetrics, segment.array[segment.offset]);
            this.firstLineOffset = Math.max(0, -this.firstLineOffset);
          } else {
            this.firstLineOffset = 0;
          } 
        } catch (BadLocationException badLocationException) {
          i = 0;
        } 
        SegmentCache.releaseSharedSegment(segment);
        return (i + this.firstLineOffset);
    } 
    return super.getPreferredSpan(paramInt);
  }
  
  public int getResizeWeight(int paramInt) { return (paramInt == 0) ? 1 : 0; }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException { return super.modelToView(paramInt, adjustAllocation(paramShape), paramBias); }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) { return super.viewToModel(paramFloat1, paramFloat2, adjustAllocation(paramShape), paramArrayOfBias); }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.insertUpdate(paramDocumentEvent, adjustAllocation(paramShape), paramViewFactory);
    updateVisibilityModel();
  }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.removeUpdate(paramDocumentEvent, adjustAllocation(paramShape), paramViewFactory);
    updateVisibilityModel();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\FieldView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */