package javax.swing.text;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public class BoxView extends CompositeView {
  int majorAxis;
  
  int majorSpan;
  
  int minorSpan;
  
  boolean majorReqValid;
  
  boolean minorReqValid;
  
  SizeRequirements majorRequest;
  
  SizeRequirements minorRequest;
  
  boolean majorAllocValid;
  
  int[] majorOffsets;
  
  int[] majorSpans;
  
  boolean minorAllocValid;
  
  int[] minorOffsets;
  
  int[] minorSpans;
  
  Rectangle tempRect = new Rectangle();
  
  public BoxView(Element paramElement, int paramInt) {
    super(paramElement);
    this.majorAxis = paramInt;
    this.majorOffsets = new int[0];
    this.majorSpans = new int[0];
    this.majorReqValid = false;
    this.majorAllocValid = false;
    this.minorOffsets = new int[0];
    this.minorSpans = new int[0];
    this.minorReqValid = false;
    this.minorAllocValid = false;
  }
  
  public int getAxis() { return this.majorAxis; }
  
  public void setAxis(int paramInt) {
    boolean bool = (paramInt != this.majorAxis) ? 1 : 0;
    this.majorAxis = paramInt;
    if (bool)
      preferenceChanged(null, true, true); 
  }
  
  public void layoutChanged(int paramInt) {
    if (paramInt == this.majorAxis) {
      this.majorAllocValid = false;
    } else {
      this.minorAllocValid = false;
    } 
  }
  
  protected boolean isLayoutValid(int paramInt) { return (paramInt == this.majorAxis) ? this.majorAllocValid : this.minorAllocValid; }
  
  protected void paintChild(Graphics paramGraphics, Rectangle paramRectangle, int paramInt) {
    View view = getView(paramInt);
    view.paint(paramGraphics, paramRectangle);
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {
    super.replace(paramInt1, paramInt2, paramArrayOfView);
    int i = (paramArrayOfView != null) ? paramArrayOfView.length : 0;
    this.majorOffsets = updateLayoutArray(this.majorOffsets, paramInt1, i);
    this.majorSpans = updateLayoutArray(this.majorSpans, paramInt1, i);
    this.majorReqValid = false;
    this.majorAllocValid = false;
    this.minorOffsets = updateLayoutArray(this.minorOffsets, paramInt1, i);
    this.minorSpans = updateLayoutArray(this.minorSpans, paramInt1, i);
    this.minorReqValid = false;
    this.minorAllocValid = false;
  }
  
  int[] updateLayoutArray(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = getViewCount();
    int[] arrayOfInt = new int[i];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    System.arraycopy(paramArrayOfInt, paramInt1, arrayOfInt, paramInt1 + paramInt2, i - paramInt2 - paramInt1);
    return arrayOfInt;
  }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    boolean bool = isLayoutValid(this.majorAxis);
    super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
    if (bool && !isLayoutValid(this.majorAxis)) {
      Container container = getContainer();
      if (paramShape != null && container != null) {
        int i = paramDocumentEvent.getOffset();
        int j = getViewIndexAtPosition(i);
        Rectangle rectangle = getInsideAllocation(paramShape);
        if (this.majorAxis == 0) {
          rectangle.x += this.majorOffsets[j];
          rectangle.width -= this.majorOffsets[j];
        } else {
          rectangle.y += this.minorOffsets[j];
          rectangle.height -= this.minorOffsets[j];
        } 
        container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
    } 
  }
  
  public void preferenceChanged(View paramView, boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool1 = (this.majorAxis == 0) ? paramBoolean1 : paramBoolean2;
    boolean bool2 = (this.majorAxis == 0) ? paramBoolean2 : paramBoolean1;
    if (bool1) {
      this.majorReqValid = false;
      this.majorAllocValid = false;
    } 
    if (bool2) {
      this.minorReqValid = false;
      this.minorAllocValid = false;
    } 
    super.preferenceChanged(paramView, paramBoolean1, paramBoolean2);
  }
  
  public int getResizeWeight(int paramInt) {
    checkRequests(paramInt);
    if (paramInt == this.majorAxis) {
      if (this.majorRequest.preferred != this.majorRequest.minimum || this.majorRequest.preferred != this.majorRequest.maximum)
        return 1; 
    } else if (this.minorRequest.preferred != this.minorRequest.minimum || this.minorRequest.preferred != this.minorRequest.maximum) {
      return 1;
    } 
    return 0;
  }
  
  void setSpanOnAxis(int paramInt, float paramFloat) {
    if (paramInt == this.majorAxis) {
      if (this.majorSpan != (int)paramFloat)
        this.majorAllocValid = false; 
      if (!this.majorAllocValid) {
        this.majorSpan = (int)paramFloat;
        checkRequests(this.majorAxis);
        layoutMajorAxis(this.majorSpan, paramInt, this.majorOffsets, this.majorSpans);
        this.majorAllocValid = true;
        updateChildSizes();
      } 
    } else {
      if ((int)paramFloat != this.minorSpan)
        this.minorAllocValid = false; 
      if (!this.minorAllocValid) {
        this.minorSpan = (int)paramFloat;
        checkRequests(paramInt);
        layoutMinorAxis(this.minorSpan, paramInt, this.minorOffsets, this.minorSpans);
        this.minorAllocValid = true;
        updateChildSizes();
      } 
    } 
  }
  
  void updateChildSizes() {
    int i = getViewCount();
    if (this.majorAxis == 0) {
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        view.setSize(this.majorSpans[b], this.minorSpans[b]);
      } 
    } else {
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        view.setSize(this.minorSpans[b], this.majorSpans[b]);
      } 
    } 
  }
  
  float getSpanOnAxis(int paramInt) { return (paramInt == this.majorAxis) ? this.majorSpan : this.minorSpan; }
  
  public void setSize(float paramFloat1, float paramFloat2) { layout(Math.max(0, (int)(paramFloat1 - getLeftInset() - getRightInset())), Math.max(0, (int)(paramFloat2 - getTopInset() - getBottomInset()))); }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle1 = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
    int i = getViewCount();
    int j = rectangle1.x + getLeftInset();
    int k = rectangle1.y + getTopInset();
    Rectangle rectangle2 = paramGraphics.getClipBounds();
    for (byte b = 0; b < i; b++) {
      this.tempRect.x = j + getOffset(0, b);
      this.tempRect.y = k + getOffset(1, b);
      this.tempRect.width = getSpan(0, b);
      this.tempRect.height = getSpan(1, b);
      int m = this.tempRect.x;
      int n = m + this.tempRect.width;
      int i1 = this.tempRect.y;
      int i2 = i1 + this.tempRect.height;
      int i3 = rectangle2.x;
      int i4 = i3 + rectangle2.width;
      int i5 = rectangle2.y;
      int i6 = i5 + rectangle2.height;
      if (n >= i3 && i2 >= i5 && i4 >= m && i6 >= i1)
        paintChild(paramGraphics, this.tempRect, b); 
    } 
  }
  
  public Shape getChildAllocation(int paramInt, Shape paramShape) {
    if (paramShape != null) {
      Shape shape = super.getChildAllocation(paramInt, paramShape);
      if (shape != null && !isAllocationValid()) {
        Rectangle rectangle = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
        if (rectangle.width == 0 && rectangle.height == 0)
          return null; 
      } 
      return shape;
    } 
    return null;
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    if (!isAllocationValid()) {
      Rectangle rectangle = paramShape.getBounds();
      setSize(rectangle.width, rectangle.height);
    } 
    return super.modelToView(paramInt, paramShape, paramBias);
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    if (!isAllocationValid()) {
      Rectangle rectangle = paramShape.getBounds();
      setSize(rectangle.width, rectangle.height);
    } 
    return super.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias);
  }
  
  public float getAlignment(int paramInt) {
    checkRequests(paramInt);
    return (paramInt == this.majorAxis) ? this.majorRequest.alignment : this.minorRequest.alignment;
  }
  
  public float getPreferredSpan(int paramInt) {
    checkRequests(paramInt);
    float f = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
    return (paramInt == this.majorAxis) ? (this.majorRequest.preferred + f) : (this.minorRequest.preferred + f);
  }
  
  public float getMinimumSpan(int paramInt) {
    checkRequests(paramInt);
    float f = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
    return (paramInt == this.majorAxis) ? (this.majorRequest.minimum + f) : (this.minorRequest.minimum + f);
  }
  
  public float getMaximumSpan(int paramInt) {
    checkRequests(paramInt);
    float f = (paramInt == 0) ? (getLeftInset() + getRightInset()) : (getTopInset() + getBottomInset());
    return (paramInt == this.majorAxis) ? (this.majorRequest.maximum + f) : (this.minorRequest.maximum + f);
  }
  
  protected boolean isAllocationValid() { return (this.majorAllocValid && this.minorAllocValid); }
  
  protected boolean isBefore(int paramInt1, int paramInt2, Rectangle paramRectangle) { return (this.majorAxis == 0) ? ((paramInt1 < paramRectangle.x)) : ((paramInt2 < paramRectangle.y)); }
  
  protected boolean isAfter(int paramInt1, int paramInt2, Rectangle paramRectangle) { return (this.majorAxis == 0) ? ((paramInt1 > paramRectangle.width + paramRectangle.x)) : ((paramInt2 > paramRectangle.height + paramRectangle.y)); }
  
  protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle) {
    int i = getViewCount();
    if (this.majorAxis == 0) {
      if (paramInt1 < paramRectangle.x + this.majorOffsets[0]) {
        childAllocation(0, paramRectangle);
        return getView(0);
      } 
      for (byte b1 = 0; b1 < i; b1++) {
        if (paramInt1 < paramRectangle.x + this.majorOffsets[b1]) {
          childAllocation(b1 - true, paramRectangle);
          return getView(b1 - true);
        } 
      } 
      childAllocation(i - 1, paramRectangle);
      return getView(i - 1);
    } 
    if (paramInt2 < paramRectangle.y + this.majorOffsets[0]) {
      childAllocation(0, paramRectangle);
      return getView(0);
    } 
    for (byte b = 0; b < i; b++) {
      if (paramInt2 < paramRectangle.y + this.majorOffsets[b]) {
        childAllocation(b - true, paramRectangle);
        return getView(b - true);
      } 
    } 
    childAllocation(i - 1, paramRectangle);
    return getView(i - 1);
  }
  
  protected void childAllocation(int paramInt, Rectangle paramRectangle) {
    paramRectangle.x += getOffset(0, paramInt);
    paramRectangle.y += getOffset(1, paramInt);
    paramRectangle.width = getSpan(0, paramInt);
    paramRectangle.height = getSpan(1, paramInt);
  }
  
  protected void layout(int paramInt1, int paramInt2) {
    setSpanOnAxis(0, paramInt1);
    setSpanOnAxis(1, paramInt2);
  }
  
  public int getWidth() {
    if (this.majorAxis == 0) {
      null = this.majorSpan;
    } else {
      null = this.minorSpan;
    } 
    return getLeftInset() - getRightInset();
  }
  
  public int getHeight() {
    if (this.majorAxis == 1) {
      null = this.majorSpan;
    } else {
      null = this.minorSpan;
    } 
    return getTopInset() - getBottomInset();
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    long l1 = 0L;
    int i = getViewCount();
    for (byte b1 = 0; b1 < i; b1++) {
      View view = getView(b1);
      paramArrayOfInt2[b1] = (int)view.getPreferredSpan(paramInt2);
      l1 += paramArrayOfInt2[b1];
    } 
    long l2 = paramInt1 - l1;
    float f = 0.0F;
    int[] arrayOfInt = null;
    if (l2 != 0L) {
      long l = 0L;
      arrayOfInt = new int[i];
      for (byte b = 0; b < i; b++) {
        int k;
        View view = getView(b);
        if (l2 < 0L) {
          k = (int)view.getMinimumSpan(paramInt2);
          arrayOfInt[b] = paramArrayOfInt2[b] - k;
        } else {
          k = (int)view.getMaximumSpan(paramInt2);
          arrayOfInt[b] = k - paramArrayOfInt2[b];
        } 
        l += k;
      } 
      float f1 = (float)Math.abs(l - l1);
      f = (float)l2 / f1;
      f = Math.min(f, 1.0F);
      f = Math.max(f, -1.0F);
    } 
    int j = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      paramArrayOfInt1[b2] = j;
      if (l2 != 0L) {
        float f1 = f * arrayOfInt[b2];
        paramArrayOfInt2[b2] = paramArrayOfInt2[b2] + Math.round(f1);
      } 
      j = (int)Math.min(j + paramArrayOfInt2[b2], 2147483647L);
    } 
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      int j = (int)view.getMaximumSpan(paramInt2);
      if (j < paramInt1) {
        float f = view.getAlignment(paramInt2);
        paramArrayOfInt1[b] = (int)((paramInt1 - j) * f);
        paramArrayOfInt2[b] = j;
      } else {
        int k = (int)view.getMinimumSpan(paramInt2);
        paramArrayOfInt1[b] = 0;
        paramArrayOfInt2[b] = Math.max(k, paramInt1);
      } 
    } 
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      f1 += view.getMinimumSpan(paramInt);
      f2 += view.getPreferredSpan(paramInt);
      f3 += view.getMaximumSpan(paramInt);
    } 
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    paramSizeRequirements.alignment = 0.5F;
    paramSizeRequirements.minimum = (int)f1;
    paramSizeRequirements.preferred = (int)f2;
    paramSizeRequirements.maximum = (int)f3;
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    int i = 0;
    long l = 0L;
    int j = Integer.MAX_VALUE;
    int k = getViewCount();
    for (byte b = 0; b < k; b++) {
      View view = getView(b);
      i = Math.max((int)view.getMinimumSpan(paramInt), i);
      l = Math.max((int)view.getPreferredSpan(paramInt), l);
      j = Math.max((int)view.getMaximumSpan(paramInt), j);
    } 
    if (paramSizeRequirements == null) {
      paramSizeRequirements = new SizeRequirements();
      paramSizeRequirements.alignment = 0.5F;
    } 
    paramSizeRequirements.preferred = (int)l;
    paramSizeRequirements.minimum = i;
    paramSizeRequirements.maximum = j;
    return paramSizeRequirements;
  }
  
  void checkRequests(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Invalid axis: " + paramInt); 
    if (paramInt == this.majorAxis) {
      if (!this.majorReqValid) {
        this.majorRequest = calculateMajorAxisRequirements(paramInt, this.majorRequest);
        this.majorReqValid = true;
      } 
    } else if (!this.minorReqValid) {
      this.minorRequest = calculateMinorAxisRequirements(paramInt, this.minorRequest);
      this.minorReqValid = true;
    } 
  }
  
  protected void baselineLayout(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    int i = (int)(paramInt1 * getAlignment(paramInt2));
    int j = paramInt1 - i;
    int k = getViewCount();
    for (byte b = 0; b < k; b++) {
      float f2;
      View view = getView(b);
      float f1 = view.getAlignment(paramInt2);
      if (view.getResizeWeight(paramInt2) > 0) {
        float f3 = view.getMinimumSpan(paramInt2);
        float f4 = view.getMaximumSpan(paramInt2);
        if (f1 == 0.0F) {
          f2 = Math.max(Math.min(f4, j), f3);
        } else if (f1 == 1.0F) {
          f2 = Math.max(Math.min(f4, i), f3);
        } else {
          float f = Math.min(i / f1, j / (1.0F - f1));
          f2 = Math.max(Math.min(f4, f), f3);
        } 
      } else {
        f2 = view.getPreferredSpan(paramInt2);
      } 
      paramArrayOfInt1[b] = i - (int)(f2 * f1);
      paramArrayOfInt2[b] = (int)f2;
    } 
  }
  
  protected SizeRequirements baselineRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    SizeRequirements sizeRequirements1 = new SizeRequirements();
    SizeRequirements sizeRequirements2 = new SizeRequirements();
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    paramSizeRequirements.alignment = 0.5F;
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      float f1 = view.getAlignment(paramInt);
      float f2 = view.getPreferredSpan(paramInt);
      int j = (int)(f1 * f2);
      int k = (int)(f2 - j);
      sizeRequirements1.preferred = Math.max(j, sizeRequirements1.preferred);
      sizeRequirements2.preferred = Math.max(k, sizeRequirements2.preferred);
      if (view.getResizeWeight(paramInt) > 0) {
        f2 = view.getMinimumSpan(paramInt);
        j = (int)(f1 * f2);
        k = (int)(f2 - j);
        sizeRequirements1.minimum = Math.max(j, sizeRequirements1.minimum);
        sizeRequirements2.minimum = Math.max(k, sizeRequirements2.minimum);
        f2 = view.getMaximumSpan(paramInt);
        j = (int)(f1 * f2);
        k = (int)(f2 - j);
        sizeRequirements1.maximum = Math.max(j, sizeRequirements1.maximum);
        sizeRequirements2.maximum = Math.max(k, sizeRequirements2.maximum);
      } else {
        sizeRequirements1.minimum = Math.max(j, sizeRequirements1.minimum);
        sizeRequirements2.minimum = Math.max(k, sizeRequirements2.minimum);
        sizeRequirements1.maximum = Math.max(j, sizeRequirements1.maximum);
        sizeRequirements2.maximum = Math.max(k, sizeRequirements2.maximum);
      } 
    } 
    paramSizeRequirements.preferred = (int)Math.min(sizeRequirements1.preferred + sizeRequirements2.preferred, 2147483647L);
    if (paramSizeRequirements.preferred > 0)
      paramSizeRequirements.alignment = sizeRequirements1.preferred / paramSizeRequirements.preferred; 
    if (paramSizeRequirements.alignment == 0.0F) {
      paramSizeRequirements.minimum = sizeRequirements2.minimum;
      paramSizeRequirements.maximum = sizeRequirements2.maximum;
    } else if (paramSizeRequirements.alignment == 1.0F) {
      paramSizeRequirements.minimum = sizeRequirements1.minimum;
      paramSizeRequirements.maximum = sizeRequirements1.maximum;
    } else {
      paramSizeRequirements.minimum = Math.round(Math.max(sizeRequirements1.minimum / paramSizeRequirements.alignment, sizeRequirements2.minimum / (1.0F - paramSizeRequirements.alignment)));
      paramSizeRequirements.maximum = Math.round(Math.min(sizeRequirements1.maximum / paramSizeRequirements.alignment, sizeRequirements2.maximum / (1.0F - paramSizeRequirements.alignment)));
    } 
    return paramSizeRequirements;
  }
  
  protected int getOffset(int paramInt1, int paramInt2) {
    int[] arrayOfInt = (paramInt1 == this.majorAxis) ? this.majorOffsets : this.minorOffsets;
    return arrayOfInt[paramInt2];
  }
  
  protected int getSpan(int paramInt1, int paramInt2) {
    int[] arrayOfInt = (paramInt1 == this.majorAxis) ? this.majorSpans : this.minorSpans;
    return arrayOfInt[paramInt2];
  }
  
  protected boolean flipEastAndWestAtEnds(int paramInt, Position.Bias paramBias) {
    if (this.majorAxis == 1) {
      int i = (paramBias == Position.Bias.Backward) ? Math.max(0, paramInt - 1) : paramInt;
      int j = getViewIndexAtPosition(i);
      if (j != -1) {
        View view = getView(j);
        if (view != null && view instanceof CompositeView)
          return ((CompositeView)view).flipEastAndWestAtEnds(paramInt, paramBias); 
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\BoxView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */