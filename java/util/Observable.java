package java.util;

public class Observable {
  private boolean changed = false;
  
  private Vector<Observer> obs = new Vector();
  
  public void addObserver(Observer paramObserver) {
    if (paramObserver == null)
      throw new NullPointerException(); 
    if (!this.obs.contains(paramObserver))
      this.obs.addElement(paramObserver); 
  }
  
  public void deleteObserver(Observer paramObserver) { this.obs.removeElement(paramObserver); }
  
  public void notifyObservers() { notifyObservers(null); }
  
  public void notifyObservers(Object paramObject) {
    Object[] arrayOfObject;
    synchronized (this) {
      if (!this.changed)
        return; 
      arrayOfObject = this.obs.toArray();
      clearChanged();
    } 
    for (int i = arrayOfObject.length - 1; i >= 0; i--)
      ((Observer)arrayOfObject[i]).update(this, paramObject); 
  }
  
  public void deleteObservers() { this.obs.removeAllElements(); }
  
  protected void setChanged() { this.changed = true; }
  
  protected void clearChanged() { this.changed = false; }
  
  public boolean hasChanged() { return this.changed; }
  
  public int countObservers() { return this.obs.size(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Observable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */