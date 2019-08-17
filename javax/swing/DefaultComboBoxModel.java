package javax.swing;

import java.io.Serializable;
import java.util.Vector;

public class DefaultComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable {
  Vector<E> objects = new Vector();
  
  Object selectedObject;
  
  public DefaultComboBoxModel() {}
  
  public DefaultComboBoxModel(E[] paramArrayOfE) {
    byte b = 0;
    int i = paramArrayOfE.length;
    while (b < i) {
      this.objects.addElement(paramArrayOfE[b]);
      b++;
    } 
    if (getSize() > 0)
      this.selectedObject = getElementAt(0); 
  }
  
  public DefaultComboBoxModel(Vector<E> paramVector) {
    if (getSize() > 0)
      this.selectedObject = getElementAt(0); 
  }
  
  public void setSelectedItem(Object paramObject) {
    if ((this.selectedObject != null && !this.selectedObject.equals(paramObject)) || (this.selectedObject == null && paramObject != null)) {
      this.selectedObject = paramObject;
      fireContentsChanged(this, -1, -1);
    } 
  }
  
  public Object getSelectedItem() { return this.selectedObject; }
  
  public int getSize() { return this.objects.size(); }
  
  public E getElementAt(int paramInt) { return (paramInt >= 0 && paramInt < this.objects.size()) ? (E)this.objects.elementAt(paramInt) : null; }
  
  public int getIndexOf(Object paramObject) { return this.objects.indexOf(paramObject); }
  
  public void addElement(E paramE) {
    this.objects.addElement(paramE);
    fireIntervalAdded(this, this.objects.size() - 1, this.objects.size() - 1);
    if (this.objects.size() == 1 && this.selectedObject == null && paramE != null)
      setSelectedItem(paramE); 
  }
  
  public void insertElementAt(E paramE, int paramInt) {
    this.objects.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public void removeElementAt(int paramInt) {
    if (getElementAt(paramInt) == this.selectedObject)
      if (paramInt == 0) {
        setSelectedItem((getSize() == 1) ? null : getElementAt(paramInt + 1));
      } else {
        setSelectedItem(getElementAt(paramInt - 1));
      }  
    this.objects.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
  }
  
  public void removeElement(Object paramObject) {
    int i = this.objects.indexOf(paramObject);
    if (i != -1)
      removeElementAt(i); 
  }
  
  public void removeAllElements() {
    if (this.objects.size() > 0) {
      byte b = 0;
      int i = this.objects.size() - 1;
      this.objects.removeAllElements();
      this.selectedObject = null;
      fireIntervalRemoved(this, b, i);
    } else {
      this.selectedObject = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultComboBoxModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */