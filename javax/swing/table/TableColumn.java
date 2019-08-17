package javax.swing.table;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

public class TableColumn implements Serializable {
  public static final String COLUMN_WIDTH_PROPERTY = "columWidth";
  
  public static final String HEADER_VALUE_PROPERTY = "headerValue";
  
  public static final String HEADER_RENDERER_PROPERTY = "headerRenderer";
  
  public static final String CELL_RENDERER_PROPERTY = "cellRenderer";
  
  protected int modelIndex;
  
  protected Object identifier;
  
  protected int width;
  
  protected int minWidth;
  
  private int preferredWidth;
  
  protected int maxWidth;
  
  protected TableCellRenderer headerRenderer;
  
  protected Object headerValue;
  
  protected TableCellRenderer cellRenderer;
  
  protected TableCellEditor cellEditor;
  
  protected boolean isResizable;
  
  @Deprecated
  protected int resizedPostingDisableCount;
  
  private SwingPropertyChangeSupport changeSupport;
  
  public TableColumn() { this(0); }
  
  public TableColumn(int paramInt) { this(paramInt, 75, null, null); }
  
  public TableColumn(int paramInt1, int paramInt2) { this(paramInt1, paramInt2, null, null); }
  
  public TableColumn(int paramInt1, int paramInt2, TableCellRenderer paramTableCellRenderer, TableCellEditor paramTableCellEditor) {
    this.modelIndex = paramInt1;
    this.preferredWidth = this.width = Math.max(paramInt2, 0);
    this.cellRenderer = paramTableCellRenderer;
    this.cellEditor = paramTableCellEditor;
    this.minWidth = Math.min(15, this.width);
    this.maxWidth = Integer.MAX_VALUE;
    this.isResizable = true;
    this.resizedPostingDisableCount = 0;
    this.headerValue = null;
  }
  
  private void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (this.changeSupport != null)
      this.changeSupport.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  private void firePropertyChange(String paramString, int paramInt1, int paramInt2) {
    if (paramInt1 != paramInt2)
      firePropertyChange(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2)); 
  }
  
  private void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1 != paramBoolean2)
      firePropertyChange(paramString, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2)); 
  }
  
  public void setModelIndex(int paramInt) {
    int i = this.modelIndex;
    this.modelIndex = paramInt;
    firePropertyChange("modelIndex", i, paramInt);
  }
  
  public int getModelIndex() { return this.modelIndex; }
  
  public void setIdentifier(Object paramObject) {
    Object object = this.identifier;
    this.identifier = paramObject;
    firePropertyChange("identifier", object, paramObject);
  }
  
  public Object getIdentifier() { return (this.identifier != null) ? this.identifier : getHeaderValue(); }
  
  public void setHeaderValue(Object paramObject) {
    Object object = this.headerValue;
    this.headerValue = paramObject;
    firePropertyChange("headerValue", object, paramObject);
  }
  
  public Object getHeaderValue() { return this.headerValue; }
  
  public void setHeaderRenderer(TableCellRenderer paramTableCellRenderer) {
    TableCellRenderer tableCellRenderer = this.headerRenderer;
    this.headerRenderer = paramTableCellRenderer;
    firePropertyChange("headerRenderer", tableCellRenderer, paramTableCellRenderer);
  }
  
  public TableCellRenderer getHeaderRenderer() { return this.headerRenderer; }
  
  public void setCellRenderer(TableCellRenderer paramTableCellRenderer) {
    TableCellRenderer tableCellRenderer = this.cellRenderer;
    this.cellRenderer = paramTableCellRenderer;
    firePropertyChange("cellRenderer", tableCellRenderer, paramTableCellRenderer);
  }
  
  public TableCellRenderer getCellRenderer() { return this.cellRenderer; }
  
  public void setCellEditor(TableCellEditor paramTableCellEditor) {
    TableCellEditor tableCellEditor = this.cellEditor;
    this.cellEditor = paramTableCellEditor;
    firePropertyChange("cellEditor", tableCellEditor, paramTableCellEditor);
  }
  
  public TableCellEditor getCellEditor() { return this.cellEditor; }
  
  public void setWidth(int paramInt) {
    int i = this.width;
    this.width = Math.min(Math.max(paramInt, this.minWidth), this.maxWidth);
    firePropertyChange("width", i, this.width);
  }
  
  public int getWidth() { return this.width; }
  
  public void setPreferredWidth(int paramInt) {
    int i = this.preferredWidth;
    this.preferredWidth = Math.min(Math.max(paramInt, this.minWidth), this.maxWidth);
    firePropertyChange("preferredWidth", i, this.preferredWidth);
  }
  
  public int getPreferredWidth() { return this.preferredWidth; }
  
  public void setMinWidth(int paramInt) {
    int i = this.minWidth;
    this.minWidth = Math.max(Math.min(paramInt, this.maxWidth), 0);
    if (this.width < this.minWidth)
      setWidth(this.minWidth); 
    if (this.preferredWidth < this.minWidth)
      setPreferredWidth(this.minWidth); 
    firePropertyChange("minWidth", i, this.minWidth);
  }
  
  public int getMinWidth() { return this.minWidth; }
  
  public void setMaxWidth(int paramInt) {
    int i = this.maxWidth;
    this.maxWidth = Math.max(this.minWidth, paramInt);
    if (this.width > this.maxWidth)
      setWidth(this.maxWidth); 
    if (this.preferredWidth > this.maxWidth)
      setPreferredWidth(this.maxWidth); 
    firePropertyChange("maxWidth", i, this.maxWidth);
  }
  
  public int getMaxWidth() { return this.maxWidth; }
  
  public void setResizable(boolean paramBoolean) {
    boolean bool = this.isResizable;
    this.isResizable = paramBoolean;
    firePropertyChange("isResizable", bool, this.isResizable);
  }
  
  public boolean getResizable() { return this.isResizable; }
  
  public void sizeWidthToFit() {
    if (this.headerRenderer == null)
      return; 
    Component component = this.headerRenderer.getTableCellRendererComponent(null, getHeaderValue(), false, false, 0, 0);
    setMinWidth((component.getMinimumSize()).width);
    setMaxWidth((component.getMaximumSize()).width);
    setPreferredWidth((component.getPreferredSize()).width);
    setWidth(getPreferredWidth());
  }
  
  @Deprecated
  public void disableResizedPosting() { this.resizedPostingDisableCount++; }
  
  @Deprecated
  public void enableResizedPosting() { this.resizedPostingDisableCount--; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      this.changeSupport = new SwingPropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport != null)
      this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener); 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  protected TableCellRenderer createDefaultHeaderRenderer() {
    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer() {
        public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
          if (param1JTable != null) {
            JTableHeader jTableHeader = param1JTable.getTableHeader();
            if (jTableHeader != null) {
              setForeground(jTableHeader.getForeground());
              setBackground(jTableHeader.getBackground());
              setFont(jTableHeader.getFont());
            } 
          } 
          setText((param1Object == null) ? "" : param1Object.toString());
          setBorder(UIManager.getBorder("TableHeader.cellBorder"));
          return this;
        }
      };
    defaultTableCellRenderer.setHorizontalAlignment(0);
    return defaultTableCellRenderer;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\TableColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */