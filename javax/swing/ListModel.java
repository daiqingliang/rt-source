package javax.swing;

import javax.swing.event.ListDataListener;

public interface ListModel<E> {
  int getSize();
  
  E getElementAt(int paramInt);
  
  void addListDataListener(ListDataListener paramListDataListener);
  
  void removeListDataListener(ListDataListener paramListDataListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */