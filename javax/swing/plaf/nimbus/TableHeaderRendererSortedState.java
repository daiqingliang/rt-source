package javax.swing.plaf.nimbus;

import javax.swing.JComponent;

class TableHeaderRendererSortedState extends State {
  TableHeaderRendererSortedState() { super("Sorted"); }
  
  protected boolean isInState(JComponent paramJComponent) {
    String str = (String)paramJComponent.getClientProperty("Table.sortOrder");
    return (str != null && ("ASCENDING".equals(str) || "DESCENDING".equals(str)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TableHeaderRendererSortedState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */