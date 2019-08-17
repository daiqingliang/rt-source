package javax.swing.text.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

class TableView extends BoxView implements ViewFactory {
  private AttributeSet attr;
  
  private StyleSheet.BoxPainter painter;
  
  private int cellSpacing;
  
  private int borderWidth;
  
  private int captionIndex = -1;
  
  private boolean relativeCells;
  
  private boolean multiRowCells;
  
  int[] columnSpans;
  
  int[] columnOffsets;
  
  SizeRequirements totalColumnRequirements = new SizeRequirements();
  
  SizeRequirements[] columnRequirements;
  
  RowIterator rowIterator = new RowIterator();
  
  ColumnIterator colIterator = new ColumnIterator();
  
  Vector<RowView> rows = new Vector();
  
  boolean skipComments = false;
  
  boolean gridValid = false;
  
  private static final BitSet EMPTY = new BitSet();
  
  public TableView(Element paramElement) { super(paramElement, 1); }
  
  protected RowView createTableRow(Element paramElement) {
    Object object = paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    return (object == HTML.Tag.TR) ? new RowView(paramElement) : null;
  }
  
  public int getColumnCount() { return this.columnSpans.length; }
  
  public int getColumnSpan(int paramInt) { return (paramInt < this.columnSpans.length) ? this.columnSpans[paramInt] : 0; }
  
  public int getRowCount() { return this.rows.size(); }
  
  public int getMultiRowSpan(int paramInt1, int paramInt2) {
    RowView rowView1 = getRow(paramInt1);
    RowView rowView2 = getRow(paramInt2);
    if (rowView1 != null && rowView2 != null) {
      int i = rowView1.viewIndex;
      int j = rowView2.viewIndex;
      return getOffset(1, j) - getOffset(1, i) + getSpan(1, j);
    } 
    return 0;
  }
  
  public int getRowSpan(int paramInt) {
    RowView rowView = getRow(paramInt);
    return (rowView != null) ? getSpan(1, rowView.viewIndex) : 0;
  }
  
  RowView getRow(int paramInt) { return (paramInt < this.rows.size()) ? (RowView)this.rows.elementAt(paramInt) : null; }
  
  protected View getViewAtPoint(int paramInt1, int paramInt2, Rectangle paramRectangle) {
    int i = getViewCount();
    Rectangle rectangle = new Rectangle();
    for (byte b = 0; b < i; b++) {
      rectangle.setBounds(paramRectangle);
      childAllocation(b, rectangle);
      View view = getView(b);
      if (view instanceof RowView) {
        view = ((RowView)view).findViewAtPoint(paramInt1, paramInt2, rectangle);
        if (view != null) {
          paramRectangle.setBounds(rectangle);
          return view;
        } 
      } 
    } 
    return super.getViewAtPoint(paramInt1, paramInt2, paramRectangle);
  }
  
  protected int getColumnsOccupied(View paramView) {
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    if (attributeSet.isDefined(HTML.Attribute.COLSPAN)) {
      String str = (String)attributeSet.getAttribute(HTML.Attribute.COLSPAN);
      if (str != null)
        try {
          return Integer.parseInt(str);
        } catch (NumberFormatException numberFormatException) {} 
    } 
    return 1;
  }
  
  protected int getRowsOccupied(View paramView) {
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    if (attributeSet.isDefined(HTML.Attribute.ROWSPAN)) {
      String str = (String)attributeSet.getAttribute(HTML.Attribute.ROWSPAN);
      if (str != null)
        try {
          return Integer.parseInt(str);
        } catch (NumberFormatException numberFormatException) {} 
    } 
    return 1;
  }
  
  protected void invalidateGrid() { this.gridValid = false; }
  
  protected StyleSheet getStyleSheet() {
    HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
    return hTMLDocument.getStyleSheet();
  }
  
  void updateInsets() {
    short s1 = (short)(int)this.painter.getInset(1, this);
    short s2 = (short)(int)this.painter.getInset(3, this);
    if (this.captionIndex != -1) {
      View view = getView(this.captionIndex);
      short s = (short)(int)view.getPreferredSpan(1);
      AttributeSet attributeSet = view.getAttributes();
      Object object = attributeSet.getAttribute(CSS.Attribute.CAPTION_SIDE);
      if (object != null && object.equals("bottom")) {
        s2 = (short)(s2 + s);
      } else {
        s1 = (short)(s1 + s);
      } 
    } 
    setInsets(s1, (short)(int)this.painter.getInset(2, this), s2, (short)(int)this.painter.getInset(4, this));
  }
  
  protected void setPropertiesFromAttributes() {
    StyleSheet styleSheet = getStyleSheet();
    this.attr = styleSheet.getViewAttributes(this);
    this.painter = styleSheet.getBoxPainter(this.attr);
    if (this.attr != null) {
      setInsets((short)(int)this.painter.getInset(1, this), (short)(int)this.painter.getInset(2, this), (short)(int)this.painter.getInset(3, this), (short)(int)this.painter.getInset(4, this));
      CSS.LengthValue lengthValue = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_SPACING);
      if (lengthValue != null) {
        this.cellSpacing = (int)lengthValue.getValue();
      } else {
        this.cellSpacing = 2;
      } 
      lengthValue = (CSS.LengthValue)this.attr.getAttribute(CSS.Attribute.BORDER_TOP_WIDTH);
      if (lengthValue != null) {
        this.borderWidth = (int)lengthValue.getValue();
      } else {
        this.borderWidth = 0;
      } 
    } 
  }
  
  void updateGrid() {
    if (!this.gridValid) {
      this.relativeCells = false;
      this.multiRowCells = false;
      this.captionIndex = -1;
      this.rows.removeAllElements();
      int i = getViewCount();
      int j;
      for (j = 0; j < i; j++) {
        View view = getView(j);
        if (view instanceof RowView) {
          this.rows.addElement((RowView)view);
          RowView rowView = (RowView)view;
          rowView.clearFilledColumns();
          rowView.rowIndex = this.rows.size() - 1;
          rowView.viewIndex = j;
        } else {
          Object object = view.getElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
          if (object instanceof HTML.Tag) {
            HTML.Tag tag = (HTML.Tag)object;
            if (tag == HTML.Tag.CAPTION)
              this.captionIndex = j; 
          } 
        } 
      } 
      j = 0;
      int k = this.rows.size();
      int m;
      for (m = 0; m < k; m++) {
        RowView rowView = getRow(m);
        int n = 0;
        byte b = 0;
        while (b < rowView.getViewCount()) {
          View view = rowView.getView(b);
          if (!this.relativeCells) {
            AttributeSet attributeSet = view.getAttributes();
            CSS.LengthValue lengthValue = (CSS.LengthValue)attributeSet.getAttribute(CSS.Attribute.WIDTH);
            if (lengthValue != null && lengthValue.isPercentage())
              this.relativeCells = true; 
          } 
          while (rowView.isFilled(n))
            n++; 
          int i1 = getRowsOccupied(view);
          if (i1 > 1)
            this.multiRowCells = true; 
          int i2 = getColumnsOccupied(view);
          if (i2 > 1 || i1 > 1) {
            int i3 = m + i1;
            int i4 = n + i2;
            for (int i5 = m; i5 < i3; i5++) {
              for (int i6 = n; i6 < i4; i6++) {
                if (i5 != m || i6 != n)
                  addFill(i5, i6); 
              } 
            } 
            if (i2 > 1)
              n += i2 - 1; 
          } 
          b++;
          n++;
        } 
        j = Math.max(j, n);
      } 
      this.columnSpans = new int[j];
      this.columnOffsets = new int[j];
      this.columnRequirements = new SizeRequirements[j];
      for (m = 0; m < j; m++) {
        this.columnRequirements[m] = new SizeRequirements();
        (this.columnRequirements[m]).maximum = Integer.MAX_VALUE;
      } 
      this.gridValid = true;
    } 
  }
  
  void addFill(int paramInt1, int paramInt2) {
    RowView rowView = getRow(paramInt1);
    if (rowView != null)
      rowView.fillColumn(paramInt2); 
  }
  
  protected void layoutColumns(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, SizeRequirements[] paramArrayOfSizeRequirements) {
    Arrays.fill(paramArrayOfInt1, 0);
    Arrays.fill(paramArrayOfInt2, 0);
    this.colIterator.setLayoutArrays(paramArrayOfInt1, paramArrayOfInt2, paramInt);
    CSS.calculateTiledLayout(this.colIterator, paramInt);
  }
  
  void calculateColumnRequirements(int paramInt) {
    for (SizeRequirements sizeRequirements : this.columnRequirements) {
      sizeRequirements.minimum = 0;
      sizeRequirements.preferred = 0;
      sizeRequirements.maximum = Integer.MAX_VALUE;
    } 
    Container container = getContainer();
    if (container != null)
      if (container instanceof JTextComponent) {
        this.skipComments = !((JTextComponent)container).isEditable();
      } else {
        this.skipComments = true;
      }  
    boolean bool = false;
    int i = getRowCount();
    byte b;
    for (b = 0; b < i; b++) {
      RowView rowView = getRow(b);
      int j = 0;
      int k = rowView.getViewCount();
      for (byte b1 = 0; b1 < k; b1++) {
        View view = rowView.getView(b1);
        if (!this.skipComments || view instanceof CellView) {
          while (rowView.isFilled(j))
            j++; 
          int m = getRowsOccupied(view);
          int n = getColumnsOccupied(view);
          if (n == 1) {
            checkSingleColumnCell(paramInt, j, view);
          } else {
            bool = true;
            j += n - 1;
          } 
          j++;
        } 
      } 
    } 
    if (bool)
      for (b = 0; b < i; b++) {
        RowView rowView = getRow(b);
        int j = 0;
        int k = rowView.getViewCount();
        for (byte b1 = 0; b1 < k; b1++) {
          View view = rowView.getView(b1);
          if (!this.skipComments || view instanceof CellView) {
            while (rowView.isFilled(j))
              j++; 
            int m = getColumnsOccupied(view);
            if (m > 1) {
              checkMultiColumnCell(paramInt, j, m, view);
              j += m - 1;
            } 
            j++;
          } 
        } 
      }  
  }
  
  void checkSingleColumnCell(int paramInt1, int paramInt2, View paramView) {
    SizeRequirements sizeRequirements = this.columnRequirements[paramInt2];
    sizeRequirements.minimum = Math.max((int)paramView.getMinimumSpan(paramInt1), sizeRequirements.minimum);
    sizeRequirements.preferred = Math.max((int)paramView.getPreferredSpan(paramInt1), sizeRequirements.preferred);
  }
  
  void checkMultiColumnCell(int paramInt1, int paramInt2, int paramInt3, View paramView) {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    int i;
    for (i = 0; i < paramInt3; i++) {
      SizeRequirements sizeRequirements = this.columnRequirements[paramInt2 + i];
      l1 += sizeRequirements.minimum;
      l2 += sizeRequirements.preferred;
      l3 += sizeRequirements.maximum;
    } 
    i = (int)paramView.getMinimumSpan(paramInt1);
    if (i > l1) {
      SizeRequirements[] arrayOfSizeRequirements = new SizeRequirements[paramInt3];
      for (int k = 0; k < paramInt3; k++)
        arrayOfSizeRequirements[k] = this.columnRequirements[paramInt2 + k]; 
      int[] arrayOfInt1 = new int[paramInt3];
      int[] arrayOfInt2 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(i, null, arrayOfSizeRequirements, arrayOfInt2, arrayOfInt1);
      for (byte b = 0; b < paramInt3; b++) {
        SizeRequirements sizeRequirements = arrayOfSizeRequirements[b];
        sizeRequirements.minimum = Math.max(arrayOfInt1[b], sizeRequirements.minimum);
        sizeRequirements.preferred = Math.max(sizeRequirements.minimum, sizeRequirements.preferred);
        sizeRequirements.maximum = Math.max(sizeRequirements.preferred, sizeRequirements.maximum);
      } 
    } 
    int j = (int)paramView.getPreferredSpan(paramInt1);
    if (j > l2) {
      SizeRequirements[] arrayOfSizeRequirements = new SizeRequirements[paramInt3];
      for (int k = 0; k < paramInt3; k++)
        arrayOfSizeRequirements[k] = this.columnRequirements[paramInt2 + k]; 
      int[] arrayOfInt1 = new int[paramInt3];
      int[] arrayOfInt2 = new int[paramInt3];
      SizeRequirements.calculateTiledPositions(j, null, arrayOfSizeRequirements, arrayOfInt2, arrayOfInt1);
      for (byte b = 0; b < paramInt3; b++) {
        SizeRequirements sizeRequirements = arrayOfSizeRequirements[b];
        sizeRequirements.preferred = Math.max(arrayOfInt1[b], sizeRequirements.preferred);
        sizeRequirements.maximum = Math.max(sizeRequirements.preferred, sizeRequirements.maximum);
      } 
    } 
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    updateGrid();
    calculateColumnRequirements(paramInt);
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    long l1 = 0L;
    long l2 = 0L;
    int i = this.columnRequirements.length;
    int j;
    for (j = 0; j < i; j++) {
      SizeRequirements sizeRequirements = this.columnRequirements[j];
      l1 += sizeRequirements.minimum;
      l2 += sizeRequirements.preferred;
    } 
    j = (i + 1) * this.cellSpacing + 2 * this.borderWidth;
    l1 += j;
    l2 += j;
    paramSizeRequirements.minimum = (int)l1;
    paramSizeRequirements.preferred = (int)l2;
    paramSizeRequirements.maximum = (int)l2;
    AttributeSet attributeSet = getAttributes();
    CSS.LengthValue lengthValue = (CSS.LengthValue)attributeSet.getAttribute(CSS.Attribute.WIDTH);
    if (BlockView.spanSetFromAttributes(paramInt, paramSizeRequirements, lengthValue, null) && paramSizeRequirements.minimum < (int)l1)
      paramSizeRequirements.maximum = paramSizeRequirements.minimum = paramSizeRequirements.preferred = (int)l1; 
    this.totalColumnRequirements.minimum = paramSizeRequirements.minimum;
    this.totalColumnRequirements.preferred = paramSizeRequirements.preferred;
    this.totalColumnRequirements.maximum = paramSizeRequirements.maximum;
    Object object = attributeSet.getAttribute(CSS.Attribute.TEXT_ALIGN);
    if (object != null) {
      String str = object.toString();
      if (str.equals("left")) {
        paramSizeRequirements.alignment = 0.0F;
      } else if (str.equals("center")) {
        paramSizeRequirements.alignment = 0.5F;
      } else if (str.equals("right")) {
        paramSizeRequirements.alignment = 1.0F;
      } else {
        paramSizeRequirements.alignment = 0.0F;
      } 
    } else {
      paramSizeRequirements.alignment = 0.0F;
    } 
    return paramSizeRequirements;
  }
  
  protected SizeRequirements calculateMajorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    updateInsets();
    this.rowIterator.updateAdjustments();
    paramSizeRequirements = CSS.calculateTiledRequirements(this.rowIterator, paramSizeRequirements);
    paramSizeRequirements.maximum = paramSizeRequirements.preferred;
    return paramSizeRequirements;
  }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    updateGrid();
    int i = getRowCount();
    for (byte b = 0; b < i; b++) {
      RowView rowView = getRow(b);
      rowView.layoutChanged(paramInt2);
    } 
    layoutColumns(paramInt1, this.columnOffsets, this.columnSpans, this.columnRequirements);
    super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    this.rowIterator.setLayoutArrays(paramArrayOfInt1, paramArrayOfInt2);
    CSS.calculateTiledLayout(this.rowIterator, paramInt1);
    if (this.captionIndex != -1) {
      View view = getView(this.captionIndex);
      int i = (int)view.getPreferredSpan(1);
      paramArrayOfInt2[this.captionIndex] = i;
      short s = (short)(int)this.painter.getInset(3, this);
      if (s != getBottomInset()) {
        paramArrayOfInt1[this.captionIndex] = paramInt1 + s;
      } else {
        paramArrayOfInt1[this.captionIndex] = -getTopInset();
      } 
    } 
  }
  
  protected View getViewAtPosition(int paramInt, Rectangle paramRectangle) {
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      int j = view.getStartOffset();
      int k = view.getEndOffset();
      if (paramInt >= j && paramInt < k) {
        if (paramRectangle != null)
          childAllocation(b, paramRectangle); 
        return view;
      } 
    } 
    if (paramInt == getEndOffset()) {
      View view = getView(i - 1);
      if (paramRectangle != null)
        childAllocation(i - 1, paramRectangle); 
      return view;
    } 
    return null;
  }
  
  public AttributeSet getAttributes() {
    if (this.attr == null) {
      StyleSheet styleSheet = getStyleSheet();
      this.attr = styleSheet.getViewAttributes(this);
    } 
    return this.attr;
  }
  
  public void paint(Graphics paramGraphics, Shape paramShape) {
    Rectangle rectangle = paramShape.getBounds();
    setSize(rectangle.width, rectangle.height);
    if (this.captionIndex != -1) {
      short s1 = (short)(int)this.painter.getInset(1, this);
      short s2 = (short)(int)this.painter.getInset(3, this);
      if (s1 != getTopInset()) {
        short s = getTopInset() - s1;
        rectangle.y += s;
        rectangle.height -= s;
      } else {
        rectangle.height -= getBottomInset() - s2;
      } 
    } 
    this.painter.paint(paramGraphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
    int i = getViewCount();
    for (byte b = 0; b < i; b++) {
      View view = getView(b);
      view.paint(paramGraphics, getChildAllocation(b, paramShape));
    } 
  }
  
  public void setParent(View paramView) {
    super.setParent(paramView);
    if (paramView != null)
      setPropertiesFromAttributes(); 
  }
  
  public ViewFactory getViewFactory() { return this; }
  
  public void insertUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { super.insertUpdate(paramDocumentEvent, paramShape, this); }
  
  public void removeUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { super.removeUpdate(paramDocumentEvent, paramShape, this); }
  
  public void changedUpdate(DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) { super.changedUpdate(paramDocumentEvent, paramShape, this); }
  
  protected void forwardUpdate(DocumentEvent.ElementChange paramElementChange, DocumentEvent paramDocumentEvent, Shape paramShape, ViewFactory paramViewFactory) {
    super.forwardUpdate(paramElementChange, paramDocumentEvent, paramShape, paramViewFactory);
    if (paramShape != null) {
      Container container = getContainer();
      if (container != null) {
        Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
        container.repaint(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
    } 
  }
  
  public void replace(int paramInt1, int paramInt2, View[] paramArrayOfView) {
    super.replace(paramInt1, paramInt2, paramArrayOfView);
    invalidateGrid();
  }
  
  public View create(Element paramElement) {
    Object object = paramElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (object instanceof HTML.Tag) {
      HTML.Tag tag = (HTML.Tag)object;
      if (tag == HTML.Tag.TR)
        return createTableRow(paramElement); 
      if (tag == HTML.Tag.TD || tag == HTML.Tag.TH)
        return new CellView(paramElement); 
      if (tag == HTML.Tag.CAPTION)
        return new ParagraphView(paramElement); 
    } 
    View view = getParent();
    if (view != null) {
      ViewFactory viewFactory = view.getViewFactory();
      if (viewFactory != null)
        return viewFactory.create(paramElement); 
    } 
    return null;
  }
  
  class CellView extends BlockView {
    public CellView(Element param1Element) { super(param1Element, 1); }
    
    protected void layoutMajorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      super.layoutMajorAxis(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2);
      int i = 0;
      int j = param1ArrayOfInt2.length;
      int k;
      for (k = 0; k < j; k++)
        i += param1ArrayOfInt2[k]; 
      k = 0;
      if (i < param1Int1) {
        String str = (String)getElement().getAttributes().getAttribute(HTML.Attribute.VALIGN);
        if (str == null) {
          AttributeSet attributeSet = getElement().getParentElement().getAttributes();
          str = (String)attributeSet.getAttribute(HTML.Attribute.VALIGN);
        } 
        if (str == null || str.equals("middle")) {
          k = (param1Int1 - i) / 2;
        } else if (str.equals("bottom")) {
          k = param1Int1 - i;
        } 
      } 
      if (k != 0)
        for (byte b = 0; b < j; b++)
          param1ArrayOfInt1[b] = param1ArrayOfInt1[b] + k;  
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      SizeRequirements sizeRequirements = super.calculateMajorAxisRequirements(param1Int, param1SizeRequirements);
      sizeRequirements.maximum = Integer.MAX_VALUE;
      return sizeRequirements;
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      SizeRequirements sizeRequirements = super.calculateMinorAxisRequirements(param1Int, param1SizeRequirements);
      int i = getViewCount();
      int j = 0;
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        j = Math.max((int)view.getMinimumSpan(param1Int), j);
      } 
      sizeRequirements.minimum = Math.min(sizeRequirements.minimum, j);
      return sizeRequirements;
    }
  }
  
  class ColumnIterator implements CSS.LayoutIterator {
    private int col;
    
    private int[] percentages;
    
    private int[] adjustmentWeights;
    
    private int[] offsets;
    
    private int[] spans;
    
    void disablePercentages() { this.percentages = null; }
    
    private void updatePercentagesAndAdjustmentWeights(int param1Int) {
      this.adjustmentWeights = new int[TableView.this.columnRequirements.length];
      int i;
      for (i = 0; i < TableView.this.columnRequirements.length; i++)
        this.adjustmentWeights[i] = 0; 
      if (TableView.this.relativeCells) {
        this.percentages = new int[TableView.this.columnRequirements.length];
      } else {
        this.percentages = null;
      } 
      i = TableView.this.getRowCount();
      for (byte b = 0; b < i; b++) {
        TableView.RowView rowView = TableView.this.getRow(b);
        int j = 0;
        int k = rowView.getViewCount();
        byte b1 = 0;
        while (b1 < k) {
          View view = rowView.getView(b1);
          while (rowView.isFilled(j))
            j++; 
          int m = TableView.this.getRowsOccupied(view);
          int n = TableView.this.getColumnsOccupied(view);
          AttributeSet attributeSet = view.getAttributes();
          CSS.LengthValue lengthValue = (CSS.LengthValue)attributeSet.getAttribute(CSS.Attribute.WIDTH);
          if (lengthValue != null) {
            int i1 = (int)(lengthValue.getValue(param1Int) / n + 0.5F);
            for (byte b2 = 0; b2 < n; b2++) {
              if (lengthValue.isPercentage()) {
                this.percentages[j + b2] = Math.max(this.percentages[j + b2], i1);
                this.adjustmentWeights[j + b2] = Math.max(this.adjustmentWeights[j + b2], 2);
              } else {
                this.adjustmentWeights[j + b2] = Math.max(this.adjustmentWeights[j + b2], 1);
              } 
            } 
          } 
          j += n - 1;
          b1++;
          j++;
        } 
      } 
    }
    
    public void setLayoutArrays(int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int param1Int) {
      this.offsets = param1ArrayOfInt1;
      this.spans = param1ArrayOfInt2;
      updatePercentagesAndAdjustmentWeights(param1Int);
    }
    
    public int getCount() { return TableView.this.columnRequirements.length; }
    
    public void setIndex(int param1Int) { this.col = param1Int; }
    
    public void setOffset(int param1Int) { this.offsets[this.col] = param1Int; }
    
    public int getOffset() { return this.offsets[this.col]; }
    
    public void setSpan(int param1Int) { this.spans[this.col] = param1Int; }
    
    public int getSpan() { return this.spans[this.col]; }
    
    public float getMinimumSpan(float param1Float) { return (this.this$0.columnRequirements[this.col]).minimum; }
    
    public float getPreferredSpan(float param1Float) { return (this.percentages != null && this.percentages[this.col] != 0) ? Math.max(this.percentages[this.col], (this.this$0.columnRequirements[this.col]).minimum) : (this.this$0.columnRequirements[this.col]).preferred; }
    
    public float getMaximumSpan(float param1Float) { return (this.this$0.columnRequirements[this.col]).maximum; }
    
    public float getBorderWidth() { return TableView.this.borderWidth; }
    
    public float getLeadingCollapseSpan() { return TableView.this.cellSpacing; }
    
    public float getTrailingCollapseSpan() { return TableView.this.cellSpacing; }
    
    public int getAdjustmentWeight() { return this.adjustmentWeights[this.col]; }
  }
  
  class RowIterator implements CSS.LayoutIterator {
    private int row;
    
    private int[] adjustments;
    
    private int[] offsets;
    
    private int[] spans;
    
    void updateAdjustments() {
      byte b = 1;
      if (TableView.this.multiRowCells) {
        int i = TableView.this.getRowCount();
        this.adjustments = new int[i];
        for (byte b1 = 0; b1 < i; b1++) {
          TableView.RowView rowView = TableView.this.getRow(b1);
          if (rowView.multiRowCells == true) {
            int j = rowView.getViewCount();
            for (byte b2 = 0; b2 < j; b2++) {
              View view = rowView.getView(b2);
              int k = TableView.this.getRowsOccupied(view);
              if (k > 1) {
                int m = (int)view.getPreferredSpan(b);
                adjustMultiRowSpan(m, k, b1);
              } 
            } 
          } 
        } 
      } else {
        this.adjustments = null;
      } 
    }
    
    void adjustMultiRowSpan(int param1Int1, int param1Int2, int param1Int3) {
      if (param1Int3 + param1Int2 > getCount()) {
        param1Int2 = getCount() - param1Int3;
        if (param1Int2 < 1)
          return; 
      } 
      int i = 0;
      int j;
      for (j = 0; j < param1Int2; j++) {
        TableView.RowView rowView = TableView.this.getRow(param1Int3 + j);
        i = (int)(i + rowView.getPreferredSpan(1));
      } 
      if (param1Int1 > i) {
        j = param1Int1 - i;
        int k = j / param1Int2;
        int m = k + j - k * param1Int2;
        TableView.RowView rowView = TableView.this.getRow(param1Int3);
        this.adjustments[param1Int3] = Math.max(this.adjustments[param1Int3], m);
        for (int n = 1; n < param1Int2; n++)
          this.adjustments[param1Int3 + n] = Math.max(this.adjustments[param1Int3 + n], k); 
      } 
    }
    
    void setLayoutArrays(int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      this.offsets = param1ArrayOfInt1;
      this.spans = param1ArrayOfInt2;
    }
    
    public void setOffset(int param1Int) {
      TableView.RowView rowView = TableView.this.getRow(this.row);
      if (rowView != null)
        this.offsets[rowView.viewIndex] = param1Int; 
    }
    
    public int getOffset() {
      TableView.RowView rowView = TableView.this.getRow(this.row);
      return (rowView != null) ? this.offsets[rowView.viewIndex] : 0;
    }
    
    public void setSpan(int param1Int) {
      TableView.RowView rowView = TableView.this.getRow(this.row);
      if (rowView != null)
        this.spans[rowView.viewIndex] = param1Int; 
    }
    
    public int getSpan() {
      TableView.RowView rowView = TableView.this.getRow(this.row);
      return (rowView != null) ? this.spans[rowView.viewIndex] : 0;
    }
    
    public int getCount() { return TableView.this.rows.size(); }
    
    public void setIndex(int param1Int) { this.row = param1Int; }
    
    public float getMinimumSpan(float param1Float) { return getPreferredSpan(param1Float); }
    
    public float getPreferredSpan(float param1Float) {
      TableView.RowView rowView = TableView.this.getRow(this.row);
      if (rowView != null) {
        int i = (this.adjustments != null) ? this.adjustments[this.row] : 0;
        return rowView.getPreferredSpan(TableView.this.getAxis()) + i;
      } 
      return 0.0F;
    }
    
    public float getMaximumSpan(float param1Float) { return getPreferredSpan(param1Float); }
    
    public float getBorderWidth() { return TableView.this.borderWidth; }
    
    public float getLeadingCollapseSpan() { return TableView.this.cellSpacing; }
    
    public float getTrailingCollapseSpan() { return TableView.this.cellSpacing; }
    
    public int getAdjustmentWeight() { return 0; }
  }
  
  public class RowView extends BoxView {
    private StyleSheet.BoxPainter painter;
    
    private AttributeSet attr;
    
    BitSet fillColumns = new BitSet();
    
    int rowIndex;
    
    int viewIndex;
    
    boolean multiRowCells;
    
    public RowView(Element param1Element) {
      super(param1Element, 0);
      setPropertiesFromAttributes();
    }
    
    void clearFilledColumns() { this.fillColumns.and(EMPTY); }
    
    void fillColumn(int param1Int) { this.fillColumns.set(param1Int); }
    
    boolean isFilled(int param1Int) { return this.fillColumns.get(param1Int); }
    
    int getColumnCount() {
      int i = 0;
      int j = this.fillColumns.size();
      for (byte b = 0; b < j; b++) {
        if (this.fillColumns.get(b))
          i++; 
      } 
      return getViewCount() + i;
    }
    
    public AttributeSet getAttributes() { return this.attr; }
    
    View findViewAtPoint(int param1Int1, int param1Int2, Rectangle param1Rectangle) {
      int i = getViewCount();
      for (byte b = 0; b < i; b++) {
        if (getChildAllocation(b, param1Rectangle).contains(param1Int1, param1Int2)) {
          childAllocation(b, param1Rectangle);
          return getView(b);
        } 
      } 
      return null;
    }
    
    protected StyleSheet getStyleSheet() {
      HTMLDocument hTMLDocument = (HTMLDocument)getDocument();
      return hTMLDocument.getStyleSheet();
    }
    
    public void preferenceChanged(View param1View, boolean param1Boolean1, boolean param1Boolean2) {
      super.preferenceChanged(param1View, param1Boolean1, param1Boolean2);
      if (TableView.this.multiRowCells && param1Boolean2)
        for (int i = this.rowIndex - 1; i >= 0; i--) {
          RowView rowView = TableView.this.getRow(i);
          if (rowView.multiRowCells) {
            rowView.preferenceChanged(null, false, true);
            break;
          } 
        }  
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      SizeRequirements sizeRequirements = new SizeRequirements();
      sizeRequirements.minimum = this.this$0.totalColumnRequirements.minimum;
      sizeRequirements.maximum = this.this$0.totalColumnRequirements.maximum;
      sizeRequirements.preferred = this.this$0.totalColumnRequirements.preferred;
      sizeRequirements.alignment = 0.0F;
      return sizeRequirements;
    }
    
    public float getMinimumSpan(int param1Int) {
      float f;
      if (param1Int == 0) {
        f = (this.this$0.totalColumnRequirements.minimum + getLeftInset() + getRightInset());
      } else {
        f = super.getMinimumSpan(param1Int);
      } 
      return f;
    }
    
    public float getMaximumSpan(int param1Int) {
      float f;
      if (param1Int == 0) {
        f = 2.14748365E9F;
      } else {
        f = super.getMaximumSpan(param1Int);
      } 
      return f;
    }
    
    public float getPreferredSpan(int param1Int) {
      float f;
      if (param1Int == 0) {
        f = (this.this$0.totalColumnRequirements.preferred + getLeftInset() + getRightInset());
      } else {
        f = super.getPreferredSpan(param1Int);
      } 
      return f;
    }
    
    public void changedUpdate(DocumentEvent param1DocumentEvent, Shape param1Shape, ViewFactory param1ViewFactory) {
      super.changedUpdate(param1DocumentEvent, param1Shape, param1ViewFactory);
      int i = param1DocumentEvent.getOffset();
      if (i <= getStartOffset() && i + param1DocumentEvent.getLength() >= getEndOffset())
        setPropertiesFromAttributes(); 
    }
    
    public void paint(Graphics param1Graphics, Shape param1Shape) {
      Rectangle rectangle = (Rectangle)param1Shape;
      this.painter.paint(param1Graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, this);
      super.paint(param1Graphics, rectangle);
    }
    
    public void replace(int param1Int1, int param1Int2, View[] param1ArrayOfView) {
      super.replace(param1Int1, param1Int2, param1ArrayOfView);
      TableView.this.invalidateGrid();
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(int param1Int, SizeRequirements param1SizeRequirements) {
      long l1 = 0L;
      long l2 = 0L;
      long l3 = 0L;
      this.multiRowCells = false;
      int i = getViewCount();
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        if (TableView.this.getRowsOccupied(view) > 1) {
          this.multiRowCells = true;
          l3 = Math.max((int)view.getMaximumSpan(param1Int), l3);
        } else {
          l1 = Math.max((int)view.getMinimumSpan(param1Int), l1);
          l2 = Math.max((int)view.getPreferredSpan(param1Int), l2);
          l3 = Math.max((int)view.getMaximumSpan(param1Int), l3);
        } 
      } 
      if (param1SizeRequirements == null) {
        param1SizeRequirements = new SizeRequirements();
        param1SizeRequirements.alignment = 0.5F;
      } 
      param1SizeRequirements.preferred = (int)l2;
      param1SizeRequirements.minimum = (int)l1;
      param1SizeRequirements.maximum = (int)l3;
      return param1SizeRequirements;
    }
    
    protected void layoutMajorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      int i = 0;
      int j = getViewCount();
      for (byte b = 0; b < j; b++) {
        View view = getView(b);
        if (!TableView.this.skipComments || view instanceof TableView.CellView) {
          while (isFilled(i))
            i++; 
          int k = TableView.this.getColumnsOccupied(view);
          param1ArrayOfInt2[b] = TableView.this.columnSpans[i];
          param1ArrayOfInt1[b] = TableView.this.columnOffsets[i];
          if (k > 1) {
            int m = TableView.this.columnSpans.length;
            for (byte b1 = 1; b1 < k; b1++) {
              if (i + b1 < m) {
                param1ArrayOfInt2[b] = param1ArrayOfInt2[b] + TableView.this.columnSpans[i + b1];
                param1ArrayOfInt2[b] = param1ArrayOfInt2[b] + TableView.this.cellSpacing;
              } 
            } 
            i += k - 1;
          } 
          i++;
        } 
      } 
    }
    
    protected void layoutMinorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      super.layoutMinorAxis(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2);
      int i = 0;
      int j = getViewCount();
      byte b = 0;
      while (b < j) {
        View view = getView(b);
        while (isFilled(i))
          i++; 
        int k = TableView.this.getColumnsOccupied(view);
        int m = TableView.this.getRowsOccupied(view);
        if (m > 1) {
          int n = this.rowIndex;
          int i1 = Math.min(this.rowIndex + m - 1, TableView.this.getRowCount() - 1);
          param1ArrayOfInt2[b] = TableView.this.getMultiRowSpan(n, i1);
        } 
        if (k > 1)
          i += k - 1; 
        b++;
        i++;
      } 
    }
    
    public int getResizeWeight(int param1Int) { return 1; }
    
    protected View getViewAtPosition(int param1Int, Rectangle param1Rectangle) {
      int i = getViewCount();
      for (byte b = 0; b < i; b++) {
        View view = getView(b);
        int j = view.getStartOffset();
        int k = view.getEndOffset();
        if (param1Int >= j && param1Int < k) {
          if (param1Rectangle != null)
            childAllocation(b, param1Rectangle); 
          return view;
        } 
      } 
      if (param1Int == getEndOffset()) {
        View view = getView(i - 1);
        if (param1Rectangle != null)
          childAllocation(i - 1, param1Rectangle); 
        return view;
      } 
      return null;
    }
    
    void setPropertiesFromAttributes() {
      StyleSheet styleSheet = getStyleSheet();
      this.attr = styleSheet.getViewAttributes(this);
      this.painter = styleSheet.getBoxPainter(this.attr);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\TableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */