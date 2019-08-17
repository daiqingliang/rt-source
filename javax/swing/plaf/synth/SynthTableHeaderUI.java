package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import sun.swing.table.DefaultTableCellHeaderRenderer;

public class SynthTableHeaderUI extends BasicTableHeaderUI implements PropertyChangeListener, SynthUI {
  private TableCellRenderer prevRenderer = null;
  
  private SynthStyle style;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new SynthTableHeaderUI(); }
  
  protected void installDefaults() {
    this.prevRenderer = this.header.getDefaultRenderer();
    if (this.prevRenderer instanceof javax.swing.plaf.UIResource)
      this.header.setDefaultRenderer(new HeaderRenderer()); 
    updateStyle(this.header);
  }
  
  private void updateStyle(JTableHeader paramJTableHeader) {
    SynthContext synthContext = getContext(paramJTableHeader, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle && synthStyle != null) {
      uninstallKeyboardActions();
      installKeyboardActions();
    } 
    synthContext.dispose();
  }
  
  protected void installListeners() {
    super.installListeners();
    this.header.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults() {
    if (this.header.getDefaultRenderer() instanceof HeaderRenderer)
      this.header.setDefaultRenderer(this.prevRenderer); 
    SynthContext synthContext = getContext(this.header, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
  }
  
  protected void uninstallListeners() {
    this.header.removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintTableHeaderBackground(synthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    SynthContext synthContext = getContext(paramJComponent);
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) { super.paint(paramGraphics, paramSynthContext.getComponent()); }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintTableHeaderBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent)); }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  protected void rolloverColumnUpdated(int paramInt1, int paramInt2) {
    this.header.repaint(this.header.getHeaderRect(paramInt1));
    this.header.repaint(this.header.getHeaderRect(paramInt2));
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
      updateStyle((JTableHeader)paramPropertyChangeEvent.getSource()); 
  }
  
  private class HeaderRenderer extends DefaultTableCellHeaderRenderer {
    HeaderRenderer() {
      setHorizontalAlignment(10);
      setName("TableHeader.renderer");
    }
    
    public Component getTableCellRendererComponent(JTable param1JTable, Object param1Object, boolean param1Boolean1, boolean param1Boolean2, int param1Int1, int param1Int2) {
      boolean bool = (param1Int2 == SynthTableHeaderUI.this.getRolloverColumn());
      if (param1Boolean1 || bool || param1Boolean2) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), param1Boolean1, param1Boolean2, param1JTable.isEnabled(), bool);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      } 
      RowSorter rowSorter = (param1JTable == null) ? null : param1JTable.getRowSorter();
      List list = (rowSorter == null) ? null : rowSorter.getSortKeys();
      if (list != null && list.size() > 0 && ((RowSorter.SortKey)list.get(0)).getColumn() == param1JTable.convertColumnIndexToModel(param1Int2)) {
        switch (SynthTableHeaderUI.null.$SwitchMap$javax$swing$SortOrder[((RowSorter.SortKey)list.get(0)).getSortOrder().ordinal()]) {
          case 1:
            putClientProperty("Table.sortOrder", "ASCENDING");
            super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
            return this;
          case 2:
            putClientProperty("Table.sortOrder", "DESCENDING");
            super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
            return this;
          case 3:
            putClientProperty("Table.sortOrder", "UNSORTED");
            super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
            return this;
        } 
        throw new AssertionError("Cannot happen");
      } 
      putClientProperty("Table.sortOrder", "UNSORTED");
      super.getTableCellRendererComponent(param1JTable, param1Object, param1Boolean1, param1Boolean2, param1Int1, param1Int2);
      return this;
    }
    
    public void setBorder(Border param1Border) {
      if (param1Border instanceof SynthBorder)
        super.setBorder(param1Border); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthTableHeaderUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */