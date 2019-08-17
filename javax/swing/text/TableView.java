package javax.swing.text;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.html.HTML;

public abstract class TableView extends BoxView {
  int[] columnSpans;
  
  int[] columnOffsets;
  
  SizeRequirements[] columnRequirements;
  
  Vector<TableRow> rows = new Vector();
  
  boolean gridValid = false;
  
  private static final BitSet EMPTY = new BitSet();
  
  public TableView(Element paramElement) { super(paramElement, 1); }
  
  protected TableRow createTableRow(Element paramElement) { return new TableRow(paramElement); }
  
  @Deprecated
  protected TableCell createTableCell(Element paramElement) { return new TableCell(paramElement); }
  
  int getColumnCount() { return this.columnSpans.length; }
  
  int getColumnSpan(int paramInt) { return this.columnSpans[paramInt]; }
  
  int getRowCount() { return this.rows.size(); }
  
  int getRowSpan(int paramInt) {
    TableRow tableRow = getRow(paramInt);
    return (tableRow != null) ? (int)tableRow.getPreferredSpan(1) : 0;
  }
  
  TableRow getRow(int paramInt) { return (paramInt < this.rows.size()) ? (TableRow)this.rows.elementAt(paramInt) : null; }
  
  int getColumnsOccupied(View paramView) {
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.COLSPAN);
    if (str != null)
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {} 
    return 1;
  }
  
  int getRowsOccupied(View paramView) {
    AttributeSet attributeSet = paramView.getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.ROWSPAN);
    if (str != null)
      try {
        return Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {} 
    return 1;
  }
  
  void invalidateGrid() { this.gridValid = false; }
  
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
  
  void updateGrid() {
    if (!this.gridValid) {
      this.rows.removeAllElements();
      int i = getViewCount();
      int j;
      for (j = 0; j < i; j++) {
        View view = getView(j);
        if (view instanceof TableRow) {
          this.rows.addElement((TableRow)view);
          TableRow tableRow = (TableRow)view;
          tableRow.clearFilledColumns();
          tableRow.setRow(j);
        } 
      } 
      j = 0;
      int k = this.rows.size();
      int m;
      for (m = 0; m < k; m++) {
        TableRow tableRow = getRow(m);
        int n = 0;
        byte b = 0;
        while (b < tableRow.getViewCount()) {
          View view = tableRow.getView(b);
          while (tableRow.isFilled(n))
            n++; 
          int i1 = getRowsOccupied(view);
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
      for (m = 0; m < j; m++)
        this.columnRequirements[m] = new SizeRequirements(); 
      this.gridValid = true;
    } 
  }
  
  void addFill(int paramInt1, int paramInt2) {
    TableRow tableRow = getRow(paramInt1);
    if (tableRow != null)
      tableRow.fillColumn(paramInt2); 
  }
  
  protected void layoutColumns(int paramInt, int[] paramArrayOfInt1, int[] paramArrayOfInt2, SizeRequirements[] paramArrayOfSizeRequirements) { SizeRequirements.calculateTiledPositions(paramInt, null, paramArrayOfSizeRequirements, paramArrayOfInt1, paramArrayOfInt2); }
  
  protected void layoutMinorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    updateGrid();
    int i = getRowCount();
    for (byte b = 0; b < i; b++) {
      TableRow tableRow = getRow(b);
      tableRow.layoutChanged(paramInt2);
    } 
    layoutColumns(paramInt1, this.columnOffsets, this.columnSpans, this.columnRequirements);
    super.layoutMinorAxis(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected SizeRequirements calculateMinorAxisRequirements(int paramInt, SizeRequirements paramSizeRequirements) {
    updateGrid();
    calculateColumnRequirements(paramInt);
    if (paramSizeRequirements == null)
      paramSizeRequirements = new SizeRequirements(); 
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    for (SizeRequirements sizeRequirements : this.columnRequirements) {
      l1 += sizeRequirements.minimum;
      l2 += sizeRequirements.preferred;
      l3 += sizeRequirements.maximum;
    } 
    paramSizeRequirements.minimum = (int)l1;
    paramSizeRequirements.preferred = (int)l2;
    paramSizeRequirements.maximum = (int)l3;
    paramSizeRequirements.alignment = 0.0F;
    return paramSizeRequirements;
  }
  
  void calculateColumnRequirements(int paramInt) {
    boolean bool = false;
    int i = getRowCount();
    byte b;
    for (b = 0; b < i; b++) {
      TableRow tableRow = getRow(b);
      int j = 0;
      int k = tableRow.getViewCount();
      byte b1 = 0;
      while (b1 < k) {
        View view = tableRow.getView(b1);
        while (tableRow.isFilled(j))
          j++; 
        int m = getRowsOccupied(view);
        int n = getColumnsOccupied(view);
        if (n == 1) {
          checkSingleColumnCell(paramInt, j, view);
        } else {
          bool = true;
          j += n - 1;
        } 
        b1++;
        j++;
      } 
    } 
    if (bool)
      for (b = 0; b < i; b++) {
        TableRow tableRow = getRow(b);
        int j = 0;
        int k = tableRow.getViewCount();
        byte b1 = 0;
        while (b1 < k) {
          View view = tableRow.getView(b1);
          while (tableRow.isFilled(j))
            j++; 
          int m = getColumnsOccupied(view);
          if (m > 1) {
            checkMultiColumnCell(paramInt, j, m, view);
            j += m - 1;
          } 
          b1++;
          j++;
        } 
      }  
  }
  
  void checkSingleColumnCell(int paramInt1, int paramInt2, View paramView) {
    SizeRequirements sizeRequirements = this.columnRequirements[paramInt2];
    sizeRequirements.minimum = Math.max((int)paramView.getMinimumSpan(paramInt1), sizeRequirements.minimum);
    sizeRequirements.preferred = Math.max((int)paramView.getPreferredSpan(paramInt1), sizeRequirements.preferred);
    sizeRequirements.maximum = Math.max((int)paramView.getMaximumSpan(paramInt1), sizeRequirements.maximum);
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
      for (int k = 0; k < paramInt3; k++) {
        SizeRequirements sizeRequirements = arrayOfSizeRequirements[k] = this.columnRequirements[paramInt2 + k];
        sizeRequirements.maximum = Math.max(sizeRequirements.maximum, (int)paramView.getMaximumSpan(paramInt1));
      } 
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
        SizeRequirements sizeRequirements = arrayOfSizeRequirements[k] = this.columnRequirements[paramInt2 + k]; 
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
  
  static interface GridCell {
    void setGridLocation(int param1Int1, int param1Int2);
    
    int getGridRow();
    
    int getGridColumn();
    
    int getColumnCount();
    
    int getRowCount();
  }
  
  @Deprecated
  public class TableCell extends BoxView implements GridCell {
    int row;
    
    int col;
    
    public TableCell(Element param1Element) { super(param1Element, 1); }
    
    public int getColumnCount() { return 1; }
    
    public int getRowCount() { return 1; }
    
    public void setGridLocation(int param1Int1, int param1Int2) {
      this.row = param1Int1;
      this.col = param1Int2;
    }
    
    public int getGridRow() { return this.row; }
    
    public int getGridColumn() { return this.col; }
  }
  
  public class TableRow extends BoxView {
    BitSet fillColumns = new BitSet();
    
    int row;
    
    public TableRow(Element param1Element) { super(param1Element, 0); }
    
    void clearFilledColumns() { this.fillColumns.and(EMPTY); }
    
    void fillColumn(int param1Int) { this.fillColumns.set(param1Int); }
    
    boolean isFilled(int param1Int) { return this.fillColumns.get(param1Int); }
    
    int getRow() { return this.row; }
    
    void setRow(int param1Int) { this.row = param1Int; }
    
    int getColumnCount() {
      int i = 0;
      int j = this.fillColumns.size();
      for (byte b = 0; b < j; b++) {
        if (this.fillColumns.get(b))
          i++; 
      } 
      return getViewCount() + i;
    }
    
    public void replace(int param1Int1, int param1Int2, View[] param1ArrayOfView) {
      super.replace(param1Int1, param1Int2, param1ArrayOfView);
      TableView.this.invalidateGrid();
    }
    
    protected void layoutMajorAxis(int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) {
      int i = 0;
      int j = getViewCount();
      byte b = 0;
      while (b < j) {
        View view = getView(b);
        while (isFilled(i))
          i++; 
        int k = TableView.this.getColumnsOccupied(view);
        param1ArrayOfInt2[b] = TableView.this.columnSpans[i];
        param1ArrayOfInt1[b] = TableView.this.columnOffsets[i];
        if (k > 1) {
          int m = TableView.this.columnSpans.length;
          for (byte b1 = 1; b1 < k; b1++) {
            if (i + b1 < m)
              param1ArrayOfInt2[b] = param1ArrayOfInt2[b] + TableView.this.columnSpans[i + b1]; 
          } 
          i += k - 1;
        } 
        b++;
        i++;
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
        if (m > 1)
          for (int n = 1; n < m; n++) {
            int i1 = getRow() + n;
            if (i1 < TableView.this.getViewCount()) {
              int i2 = TableView.this.getSpan(1, getRow() + n);
              param1ArrayOfInt2[b] = param1ArrayOfInt2[b] + i2;
            } 
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
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\TableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */