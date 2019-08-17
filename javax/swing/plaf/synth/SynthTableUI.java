package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SynthTableUI extends BasicTableUI implements SynthUI, PropertyChangeListener {
  private SynthStyle style;
  
  private boolean useTableColors;
  
  private boolean useUIBorder;
  
  private Color alternateColor;
  
  private TableCellRenderer dateRenderer;
  
  private TableCellRenderer numberRenderer;
  
  private TableCellRenderer doubleRender;
  
  private TableCellRenderer floatRenderer;
  
  private TableCellRenderer iconRenderer;
  
  private TableCellRenderer imageIconRenderer;
  
  private TableCellRenderer booleanRenderer;
  
  private TableCellRenderer objectRenderer;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTableUI(); }
  
  protected void installDefaults() {
    this.dateRenderer = installRendererIfPossible(java.util.Date.class, null);
    this.numberRenderer = installRendererIfPossible(Number.class, null);
    this.doubleRender = installRendererIfPossible(Double.class, null);
    this.floatRenderer = installRendererIfPossible(Float.class, null);
    this.iconRenderer = installRendererIfPossible(Icon.class, null);
    this.imageIconRenderer = installRendererIfPossible(javax.swing.ImageIcon.class, null);
    this.booleanRenderer = installRendererIfPossible(Boolean.class, new SynthBooleanTableCellRenderer());
    this.objectRenderer = installRendererIfPossible(Object.class, new SynthTableCellRenderer(null));
    updateStyle(this.table);
  }
  
  private TableCellRenderer installRendererIfPossible(Class paramClass, TableCellRenderer paramTableCellRenderer) {
    TableCellRenderer tableCellRenderer = this.table.getDefaultRenderer(paramClass);
    if (tableCellRenderer instanceof javax.swing.plaf.UIResource)
      this.table.setDefaultRenderer(paramClass, paramTableCellRenderer); 
    return tableCellRenderer;
  }
  
  private void updateStyle(JTable paramJTable) {
    SynthContext synthContext = getContext(paramJTable, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      synthContext.setComponentState(513);
      Color color1 = this.table.getSelectionBackground();
      if (color1 == null || color1 instanceof javax.swing.plaf.UIResource)
        this.table.setSelectionBackground(this.style.getColor(synthContext, ColorType.TEXT_BACKGROUND)); 
      Color color2 = this.table.getSelectionForeground();
      if (color2 == null || color2 instanceof javax.swing.plaf.UIResource)
        this.table.setSelectionForeground(this.style.getColor(synthContext, ColorType.TEXT_FOREGROUND)); 
      synthContext.setComponentState(1);
      Color color3 = this.table.getGridColor();
      if (color3 == null || color3 instanceof javax.swing.plaf.UIResource) {
        color3 = (Color)this.style.get(synthContext, "Table.gridColor");
        if (color3 == null)
          color3 = this.style.getColor(synthContext, ColorType.FOREGROUND); 
        this.table.setGridColor((color3 == null) ? new ColorUIResource(Color.GRAY) : color3);
      } 
      this.useTableColors = this.style.getBoolean(synthContext, "Table.rendererUseTableColors", true);
      this.useUIBorder = this.style.getBoolean(synthContext, "Table.rendererUseUIBorder", true);
      Object object = this.style.get(synthContext, "Table.rowHeight");
      if (object != null)
        LookAndFeel.installProperty(this.table, "rowHeight", object); 
      boolean bool = this.style.getBoolean(synthContext, "Table.showGrid", true);
      if (!bool)
        this.table.setShowGrid(false); 
      Dimension dimension = this.table.getIntercellSpacing();
      if (dimension != null)
        dimension = (Dimension)this.style.get(synthContext, "Table.intercellSpacing"); 
      this.alternateColor = (Color)this.style.get(synthContext, "Table.alternateRowColor");
      if (dimension != null)
        this.table.setIntercellSpacing(dimension); 
      if (synthStyle != null) {
        uninstallKeyboardActions();
        installKeyboardActions();
      } 
    } 
    synthContext.dispose();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.table.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults() {
    this.table.setDefaultRenderer(java.util.Date.class, this.dateRenderer);
    this.table.setDefaultRenderer(Number.class, this.numberRenderer);
    this.table.setDefaultRenderer(Double.class, this.doubleRender);
    this.table.setDefaultRenderer(Float.class, this.floatRenderer);
    this.table.setDefaultRenderer(Icon.class, this.iconRenderer);
    this.table.setDefaultRenderer(javax.swing.ImageIcon.class, this.imageIconRenderer);
    this.table.setDefaultRenderer(Boolean.class, this.booleanRenderer);
    this.table.setDefaultRenderer(Object.class, this.objectRenderer);
    if (this.table.getTransferHandler() instanceof javax.swing.plaf.UIResource)
      this.table.setTransferHandler(null); 
    SynthContext synthContext = getContext(this.table, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners() {
    this.table.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintTableBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTableBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    Rectangle rectangle1 = paramGraphics.getClipBounds();
    Rectangle rectangle2 = this.table.getBounds();
    rectangle2.x = rectangle2.y = 0;
    if (this.table.getRowCount() <= 0 || this.table.getColumnCount() <= 0 || !rectangle2.intersects(rectangle1)) {
      paintDropLines(paramSynthContext, paramGraphics);
      return;
    } 
    boolean bool = this.table.getComponentOrientation().isLeftToRight();
    Point point1 = rectangle1.getLocation();
    Point point2 = new Point(rectangle1.x + rectangle1.width - 1, rectangle1.y + rectangle1.height - 1);
    int i = this.table.rowAtPoint(point1);
    int j = this.table.rowAtPoint(point2);
    if (i == -1)
      i = 0; 
    if (j == -1)
      j = this.table.getRowCount() - 1; 
    int k = this.table.columnAtPoint(bool ? point1 : point2);
    int m = this.table.columnAtPoint(bool ? point2 : point1);
    if (k == -1)
      k = 0; 
    if (m == -1)
      m = this.table.getColumnCount() - 1; 
    paintCells(paramSynthContext, paramGraphics, i, j, k, m);
    paintGrid(paramSynthContext, paramGraphics, i, j, k, m);
    paintDropLines(paramSynthContext, paramGraphics);
  }
  
  private void paintDropLines(SynthContext paramSynthContext, Graphics paramGraphics) {
    JTable.DropLocation dropLocation = this.table.getDropLocation();
    if (dropLocation == null)
      return; 
    Color color1 = (Color)this.style.get(paramSynthContext, "Table.dropLineColor");
    Color color2 = (Color)this.style.get(paramSynthContext, "Table.dropLineShortColor");
    if (color1 == null && color2 == null)
      return; 
    Rectangle rectangle = getHDropLineRect(dropLocation);
    if (rectangle != null) {
      int i = rectangle.x;
      int j = rectangle.width;
      if (color1 != null) {
        extendRect(rectangle, true);
        paramGraphics.setColor(color1);
        paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (!dropLocation.isInsertColumn() && color2 != null) {
        paramGraphics.setColor(color2);
        paramGraphics.fillRect(i, rectangle.y, j, rectangle.height);
      } 
    } 
    rectangle = getVDropLineRect(dropLocation);
    if (rectangle != null) {
      int i = rectangle.y;
      int j = rectangle.height;
      if (color1 != null) {
        extendRect(rectangle, false);
        paramGraphics.setColor(color1);
        paramGraphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (!dropLocation.isInsertRow() && color2 != null) {
        paramGraphics.setColor(color2);
        paramGraphics.fillRect(rectangle.x, i, rectangle.width, j);
      } 
    } 
  }
  
  private Rectangle getHDropLineRect(JTable.DropLocation paramDropLocation) {
    if (!paramDropLocation.isInsertRow())
      return null; 
    int i = paramDropLocation.getRow();
    int j = paramDropLocation.getColumn();
    if (j >= this.table.getColumnCount())
      j--; 
    Rectangle rectangle = this.table.getCellRect(i, j, true);
    if (i >= this.table.getRowCount()) {
      Rectangle rectangle1 = this.table.getCellRect(--i, j, true);
      rectangle1.y += rectangle1.height;
    } 
    if (rectangle.y == 0) {
      rectangle.y = -1;
    } else {
      rectangle.y -= 2;
    } 
    rectangle.height = 3;
    return rectangle;
  }
  
  private Rectangle getVDropLineRect(JTable.DropLocation paramDropLocation) {
    if (!paramDropLocation.isInsertColumn())
      return null; 
    boolean bool = this.table.getComponentOrientation().isLeftToRight();
    int i = paramDropLocation.getColumn();
    Rectangle rectangle = this.table.getCellRect(paramDropLocation.getRow(), i, true);
    if (i >= this.table.getColumnCount()) {
      rectangle = this.table.getCellRect(paramDropLocation.getRow(), --i, true);
      if (bool)
        rectangle.x += rectangle.width; 
    } else if (!bool) {
      rectangle.x += rectangle.width;
    } 
    if (rectangle.x == 0) {
      rectangle.x = -1;
    } else {
      rectangle.x -= 2;
    } 
    rectangle.width = 3;
    return rectangle;
  }
  
  private Rectangle extendRect(Rectangle paramRectangle, boolean paramBoolean) {
    if (paramRectangle == null)
      return paramRectangle; 
    if (paramBoolean) {
      paramRectangle.x = 0;
      paramRectangle.width = this.table.getWidth();
    } else {
      paramRectangle.y = 0;
      if (this.table.getRowCount() != 0) {
        Rectangle rectangle = this.table.getCellRect(this.table.getRowCount() - 1, 0, true);
        paramRectangle.height = rectangle.y + rectangle.height;
      } else {
        paramRectangle.height = this.table.getHeight();
      } 
    } 
    return paramRectangle;
  }
  
  private void paintGrid(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramGraphics.setColor(this.table.getGridColor());
    Rectangle rectangle1 = this.table.getCellRect(paramInt1, paramInt3, true);
    Rectangle rectangle2 = this.table.getCellRect(paramInt2, paramInt4, true);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    SynthGraphicsUtils synthGraphicsUtils = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext);
    if (this.table.getShowHorizontalLines()) {
      int i = rectangle3.x + rectangle3.width;
      int j = rectangle3.y;
      for (int k = paramInt1; k <= paramInt2; k++) {
        j += this.table.getRowHeight(k);
        synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, rectangle3.x, j - 1, i - 1, j - 1);
      } 
    } 
    if (this.table.getShowVerticalLines()) {
      TableColumnModel tableColumnModel = this.table.getColumnModel();
      int i = rectangle3.y + rectangle3.height;
      if (this.table.getComponentOrientation().isLeftToRight()) {
        int j = rectangle3.x;
        for (int k = paramInt3; k <= paramInt4; k++) {
          int m = tableColumnModel.getColumn(k).getWidth();
          j += m;
          synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, j - 1, 0, j - 1, i - 1);
        } 
      } else {
        int j = rectangle3.x;
        for (int k = paramInt4; k >= paramInt3; k--) {
          int m = tableColumnModel.getColumn(k).getWidth();
          j += m;
          synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, j - 1, 0, j - 1, i - 1);
        } 
      } 
    } 
  }
  
  private int viewIndexForColumn(TableColumn paramTableColumn) {
    TableColumnModel tableColumnModel = this.table.getColumnModel();
    for (byte b = 0; b < tableColumnModel.getColumnCount(); b++) {
      if (tableColumnModel.getColumn(b) == paramTableColumn)
        return b; 
    } 
    return -1;
  }
  
  private void paintCells(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    JTableHeader jTableHeader = this.table.getTableHeader();
    TableColumn tableColumn = (jTableHeader == null) ? null : jTableHeader.getDraggedColumn();
    TableColumnModel tableColumnModel = this.table.getColumnModel();
    int i = tableColumnModel.getColumnMargin();
    if (this.table.getComponentOrientation().isLeftToRight()) {
      for (int j = paramInt1; j <= paramInt2; j++) {
        Rectangle rectangle = this.table.getCellRect(j, paramInt3, false);
        for (int k = paramInt3; k <= paramInt4; k++) {
          TableColumn tableColumn1 = tableColumnModel.getColumn(k);
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          if (tableColumn1 != tableColumn)
            paintCell(paramSynthContext, paramGraphics, rectangle, j, k); 
          rectangle.x += m;
        } 
      } 
    } else {
      for (int j = paramInt1; j <= paramInt2; j++) {
        Rectangle rectangle = this.table.getCellRect(j, paramInt3, false);
        TableColumn tableColumn1 = tableColumnModel.getColumn(paramInt3);
        if (tableColumn1 != tableColumn) {
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          paintCell(paramSynthContext, paramGraphics, rectangle, j, paramInt3);
        } 
        for (int k = paramInt3 + 1; k <= paramInt4; k++) {
          tableColumn1 = tableColumnModel.getColumn(k);
          int m = tableColumn1.getWidth();
          rectangle.width = m - i;
          rectangle.x -= m;
          if (tableColumn1 != tableColumn)
            paintCell(paramSynthContext, paramGraphics, rectangle, j, k); 
        } 
      } 
    } 
    if (tableColumn != null)
      paintDraggedArea(paramSynthContext, paramGraphics, paramInt1, paramInt2, tableColumn, jTableHeader.getDraggedDistance()); 
    this.rendererPane.removeAll();
  }
  
  private void paintDraggedArea(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, TableColumn paramTableColumn, int paramInt3) {
    int i = viewIndexForColumn(paramTableColumn);
    Rectangle rectangle1 = this.table.getCellRect(paramInt1, i, true);
    Rectangle rectangle2 = this.table.getCellRect(paramInt2, i, true);
    Rectangle rectangle3 = rectangle1.union(rectangle2);
    paramGraphics.setColor(this.table.getParent().getBackground());
    paramGraphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
    rectangle3.x += paramInt3;
    paramGraphics.setColor(paramSynthContext.getStyle().getColor(paramSynthContext, ColorType.BACKGROUND));
    paramGraphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
    SynthGraphicsUtils synthGraphicsUtils = paramSynthContext.getStyle().getGraphicsUtils(paramSynthContext);
    if (this.table.getShowVerticalLines()) {
      paramGraphics.setColor(this.table.getGridColor());
      int k = rectangle3.x;
      int m = rectangle3.y;
      int n = k + rectangle3.width - 1;
      int i1 = m + rectangle3.height - 1;
      synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, k - 1, m, k - 1, i1);
      synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, n, m, n, i1);
    } 
    for (int j = paramInt1; j <= paramInt2; j++) {
      Rectangle rectangle = this.table.getCellRect(j, i, false);
      rectangle.x += paramInt3;
      paintCell(paramSynthContext, paramGraphics, rectangle, j, i);
      if (this.table.getShowHorizontalLines()) {
        paramGraphics.setColor(this.table.getGridColor());
        Rectangle rectangle4 = this.table.getCellRect(j, i, true);
        rectangle4.x += paramInt3;
        int k = rectangle4.x;
        int m = rectangle4.y;
        int n = k + rectangle4.width - 1;
        int i1 = m + rectangle4.height - 1;
        synthGraphicsUtils.drawLine(paramSynthContext, "Table.grid", paramGraphics, k, i1, n, i1);
      } 
    } 
  }
  
  private void paintCell(SynthContext paramSynthContext, Graphics paramGraphics, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (this.table.isEditing() && this.table.getEditingRow() == paramInt1 && this.table.getEditingColumn() == paramInt2) {
      Component component = this.table.getEditorComponent();
      component.setBounds(paramRectangle);
      component.validate();
    } else {
      TableCellRenderer tableCellRenderer = this.table.getCellRenderer(paramInt1, paramInt2);
      Component component = this.table.prepareRenderer(tableCellRenderer, paramInt1, paramInt2);
      Color color = component.getBackground();
      if ((color == null || color instanceof javax.swing.plaf.UIResource || component instanceof SynthBooleanTableCellRenderer) && !this.table.isCellSelected(paramInt1, paramInt2) && this.alternateColor != null && paramInt1 % 2 != 0)
        component.setBackground(this.alternateColor); 
      this.rendererPane.paintComponent(paramGraphics, component, this.table, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height, true);
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTable)paramPropertyChangeEvent.getSource()); 
  }
  
  private class SynthBooleanTableCellRenderer extends JCheckBox implements TableCellRenderer {
    private boolean isRowSelected;
    
    public SynthBooleanTableCellRenderer() {
      setHorizontalAlignment(0);
      setName("Table.cellRenderer");
    }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      this.isRowSelected = param1Boolean1;
      if (param1Boolean1) {
        setForeground(unwrap(param1JTable.getSelectionForeground()));
        setBackground(unwrap(param1JTable.getSelectionBackground()));
      } else {
        setForeground(unwrap(param1JTable.getForeground()));
        setBackground(unwrap(param1JTable.getBackground()));
      } 
      setSelected((param1Object != null && ((Boolean)param1Object).booleanValue()));
      return this;
    }
    
    private Color unwrap(Color param1Color) { return (param1Color instanceof javax.swing.plaf.UIResource) ? new Color(param1Color.getRGB()) : param1Color; }
    
    public boolean isOpaque() { return this.isRowSelected ? true : super.isOpaque(); }
  }
  
  private class SynthTableCellRenderer extends DefaultTableCellRenderer {
    private Object numberFormat;
    
    private Object dateFormat;
    
    private boolean opaque;
    
    private SynthTableCellRenderer() {}
    
    public void setOpaque(boolean param1Boolean) { this.opaque = param1Boolean; }
    
    public boolean isOpaque() { return this.opaque; }
    
    public String getName() {
      String str = super.getName();
      return (str == null) ? "Table.cellRenderer" : str;
    }
    
    public void setBorder(Border param1Border) {
      if (SynthTableUI.this.useUIBorder || param1Border instanceof SynthBorder)
        super.setBorder(param1Border); 
    }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      if (!SynthTableUI.this.useTableColors && (param1Boolean1 || param1Boolean2)) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), param1Boolean1, param1Boolean2, param1JTable.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      } 
      super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
      setIcon(null);
      if (param1JTable != null)
        configureValue(param1Object, param1JTable.getColumnClass(param1Int2)); 
      return this;
    }
    
    private void configureValue(Object param1Object, Class param1Class) {
      if (param1Class == Object.class || param1Class == null) {
        setHorizontalAlignment(10);
      } else if (param1Class == Float.class || param1Class == Double.class) {
        if (this.numberFormat == null)
          this.numberFormat = NumberFormat.getInstance(); 
        setHorizontalAlignment(11);
        setText((param1Object == null) ? "" : ((NumberFormat)this.numberFormat).format(param1Object));
      } else if (param1Class == Number.class) {
        setHorizontalAlignment(11);
      } else if (param1Class == Icon.class || param1Class == javax.swing.ImageIcon.class) {
        setHorizontalAlignment(0);
        setIcon((param1Object instanceof Icon) ? (Icon)param1Object : null);
        setText("");
      } else if (param1Class == java.util.Date.class) {
        if (this.dateFormat == null)
          this.dateFormat = DateFormat.getDateInstance(); 
        setHorizontalAlignment(10);
        setText((param1Object == null) ? "" : ((Format)this.dateFormat).format(param1Object));
      } else {
        configureValue(param1Object, param1Class.getSuperclass());
      } 
    }
    
    public void paint(Graphics param1Graphics) {
      super.paint(param1Graphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTableUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */