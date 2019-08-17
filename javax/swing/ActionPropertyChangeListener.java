package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

abstract class ActionPropertyChangeListener<T extends JComponent> extends Object implements PropertyChangeListener, Serializable {
  private static ReferenceQueue<JComponent> queue;
  
  private OwnedWeakReference<T> target;
  
  private Action action;
  
  private static ReferenceQueue<JComponent> getQueue() {
    synchronized (ActionPropertyChangeListener.class) {
      if (queue == null)
        queue = new ReferenceQueue(); 
    } 
    return queue;
  }
  
  public ActionPropertyChangeListener(T paramT, Action paramAction) {
    setTarget(paramT);
    this.action = paramAction;
  }
  
  public final void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    JComponent jComponent = getTarget();
    if (jComponent == null) {
      getAction().removePropertyChangeListener(this);
    } else {
      actionPropertyChanged(jComponent, getAction(), paramPropertyChangeEvent);
    } 
  }
  
  protected abstract void actionPropertyChanged(T paramT, Action paramAction, PropertyChangeEvent paramPropertyChangeEvent);
  
  private void setTarget(T paramT) {
    ReferenceQueue referenceQueue = getQueue();
    OwnedWeakReference ownedWeakReference;
    while ((ownedWeakReference = (OwnedWeakReference)referenceQueue.poll()) != null) {
      ActionPropertyChangeListener actionPropertyChangeListener = ownedWeakReference.getOwner();
      Action action1 = actionPropertyChangeListener.getAction();
      if (action1 != null)
        action1.removePropertyChangeListener(actionPropertyChangeListener); 
    } 
    this.target = new OwnedWeakReference(paramT, referenceQueue, this);
  }
  
  public T getTarget() { return (this.target == null) ? null : (T)(JComponent)this.target.get(); }
  
  public Action getAction() { return this.action; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(getTarget());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    JComponent jComponent = (JComponent)paramObjectInputStream.readObject();
    if (jComponent != null)
      setTarget(jComponent); 
  }
  
  private static class OwnedWeakReference<U extends JComponent> extends WeakReference<U> {
    private ActionPropertyChangeListener<?> owner;
    
    OwnedWeakReference(U param1U, ReferenceQueue<? super U> param1ReferenceQueue, ActionPropertyChangeListener<?> param1ActionPropertyChangeListener) {
      super(param1U, param1ReferenceQueue);
      this.owner = param1ActionPropertyChangeListener;
    }
    
    public ActionPropertyChangeListener<?> getOwner() { return this.owner; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ActionPropertyChangeListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */