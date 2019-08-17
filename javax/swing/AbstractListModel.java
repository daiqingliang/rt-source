package javax.swing;

import java.io.Serializable;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class AbstractListModel<E> extends Object implements ListModel<E>, Serializable {
  protected EventListenerList listenerList = new EventListenerList();
  
  public void addListDataListener(ListDataListener paramListDataListener) { this.listenerList.add(ListDataListener.class, paramListDataListener); }
  
  public void removeListDataListener(ListDataListener paramListDataListener) { this.listenerList.remove(ListDataListener.class, paramListDataListener); }
  
  public ListDataListener[] getListDataListeners() { return (ListDataListener[])this.listenerList.getListeners(ListDataListener.class); }
  
  protected void fireContentsChanged(Object paramObject, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListDataEvent listDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class) {
        if (listDataEvent == null)
          listDataEvent = new ListDataEvent(paramObject, 0, paramInt1, paramInt2); 
        ((ListDataListener)arrayOfObject[i + 1]).contentsChanged(listDataEvent);
      } 
    } 
  }
  
  protected void fireIntervalAdded(Object paramObject, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListDataEvent listDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class) {
        if (listDataEvent == null)
          listDataEvent = new ListDataEvent(paramObject, 1, paramInt1, paramInt2); 
        ((ListDataListener)arrayOfObject[i + 1]).intervalAdded(listDataEvent);
      } 
    } 
  }
  
  protected void fireIntervalRemoved(Object paramObject, int paramInt1, int paramInt2) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    ListDataEvent listDataEvent = null;
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ListDataListener.class) {
        if (listDataEvent == null)
          listDataEvent = new ListDataEvent(paramObject, 2, paramInt1, paramInt2); 
        ((ListDataListener)arrayOfObject[i + 1]).intervalRemoved(listDataEvent);
      } 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\AbstractListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */