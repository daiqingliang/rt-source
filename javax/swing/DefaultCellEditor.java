package javax.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellEditor;

public class DefaultCellEditor extends AbstractCellEditor implements TableCellEditor, TreeCellEditor {
  protected JComponent editorComponent;
  
  protected EditorDelegate delegate;
  
  protected int clickCountToStart = 1;
  
  @ConstructorProperties({"component"})
  public DefaultCellEditor(final JTextField textField) {
    this.editorComponent = paramJTextField;
    this.clickCountToStart = 2;
    this.delegate = new EditorDelegate() {
        public void setValue(Object param1Object) { textField.setText((param1Object != null) ? param1Object.toString() : ""); }
        
        public Object getCellEditorValue() { return textField.getText(); }
      };
    paramJTextField.addActionListener(this.delegate);
  }
  
  public DefaultCellEditor(final JCheckBox checkBox) {
    this.editorComponent = paramJCheckBox;
    this.delegate = new EditorDelegate() {
        public void setValue(Object param1Object) {
          boolean bool = false;
          if (param1Object instanceof Boolean) {
            bool = ((Boolean)param1Object).booleanValue();
          } else if (param1Object instanceof String) {
            bool = param1Object.equals("true");
          } 
          checkBox.setSelected(bool);
        }
        
        public Object getCellEditorValue() { return Boolean.valueOf(checkBox.isSelected()); }
      };
    paramJCheckBox.addActionListener(this.delegate);
    paramJCheckBox.setRequestFocusEnabled(false);
  }
  
  public DefaultCellEditor(final JComboBox comboBox) {
    this.editorComponent = paramJComboBox;
    paramJComboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
    this.delegate = new EditorDelegate() {
        public void setValue(Object param1Object) { comboBox.setSelectedItem(param1Object); }
        
        public Object getCellEditorValue() { return comboBox.getSelectedItem(); }
        
        public boolean shouldSelectCell(EventObject param1EventObject) {
          if (param1EventObject instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent)param1EventObject;
            return (mouseEvent.getID() != 506);
          } 
          return true;
        }
        
        public boolean stopCellEditing() {
          if (comboBox.isEditable())
            comboBox.actionPerformed(new ActionEvent(DefaultCellEditor.this, 0, "")); 
          return super.stopCellEditing();
        }
      };
    paramJComboBox.addActionListener(this.delegate);
  }
  
  public Component getComponent() { return this.editorComponent; }
  
  public void setClickCountToStart(int paramInt) { this.clickCountToStart = paramInt; }
  
  public int getClickCountToStart() { return this.clickCountToStart; }
  
  public Object getCellEditorValue() { return this.delegate.getCellEditorValue(); }
  
  public boolean isCellEditable(EventObject paramEventObject) { return this.delegate.isCellEditable(paramEventObject); }
  
  public boolean shouldSelectCell(EventObject paramEventObject) { return this.delegate.shouldSelectCell(paramEventObject); }
  
  public boolean stopCellEditing() { return this.delegate.stopCellEditing(); }
  
  public void cancelCellEditing() { this.delegate.cancelCellEditing(); }
  
  public Component getTreeCellEditorComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt) {
    String str = paramJTree.convertValueToText(paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, false);
    this.delegate.setValue(str);
    return this.editorComponent;
  }
  
  public Component getTableCellEditorComponent(JTable paramJTable, Object paramObject, boolean paramBoolean, int paramInt1, int paramInt2) {
    this.delegate.setValue(paramObject);
    if (this.editorComponent instanceof JCheckBox) {
      TableCellRenderer tableCellRenderer = paramJTable.getCellRenderer(paramInt1, paramInt2);
      Component component = tableCellRenderer.getTableCellRendererComponent(paramJTable, paramObject, paramBoolean, true, paramInt1, paramInt2);
      if (component != null) {
        this.editorComponent.setOpaque(true);
        this.editorComponent.setBackground(component.getBackground());
        if (component instanceof JComponent)
          this.editorComponent.setBorder(((JComponent)component).getBorder()); 
      } else {
        this.editorComponent.setOpaque(false);
      } 
    } 
    return this.editorComponent;
  }
  
  protected class EditorDelegate implements ActionListener, ItemListener, Serializable {
    protected Object value;
    
    public Object getCellEditorValue() { return this.value; }
    
    public void setValue(Object param1Object) { this.value = param1Object; }
    
    public boolean isCellEditable(EventObject param1EventObject) { return (param1EventObject instanceof MouseEvent) ? ((((MouseEvent)param1EventObject).getClickCount() >= DefaultCellEditor.this.clickCountToStart)) : true; }
    
    public boolean shouldSelectCell(EventObject param1EventObject) { return true; }
    
    public boolean startCellEditing(EventObject param1EventObject) { return true; }
    
    public boolean stopCellEditing() {
      DefaultCellEditor.this.fireEditingStopped();
      return true;
    }
    
    public void cancelCellEditing() { DefaultCellEditor.this.fireEditingCanceled(); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { DefaultCellEditor.this.stopCellEditing(); }
    
    public void itemStateChanged(ItemEvent param1ItemEvent) { DefaultCellEditor.this.stopCellEditing(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */