package javax.accessibility;

import java.util.Vector;

public class AccessibleStateSet {
  protected Vector<AccessibleState> states = null;
  
  public AccessibleStateSet() { this.states = null; }
  
  public AccessibleStateSet(AccessibleState[] paramArrayOfAccessibleState) {
    if (paramArrayOfAccessibleState.length != 0) {
      this.states = new Vector(paramArrayOfAccessibleState.length);
      for (byte b = 0; b < paramArrayOfAccessibleState.length; b++) {
        if (!this.states.contains(paramArrayOfAccessibleState[b]))
          this.states.addElement(paramArrayOfAccessibleState[b]); 
      } 
    } 
  }
  
  public boolean add(AccessibleState paramAccessibleState) {
    if (this.states == null)
      this.states = new Vector(); 
    if (!this.states.contains(paramAccessibleState)) {
      this.states.addElement(paramAccessibleState);
      return true;
    } 
    return false;
  }
  
  public void addAll(AccessibleState[] paramArrayOfAccessibleState) {
    if (paramArrayOfAccessibleState.length != 0) {
      if (this.states == null)
        this.states = new Vector(paramArrayOfAccessibleState.length); 
      for (byte b = 0; b < paramArrayOfAccessibleState.length; b++) {
        if (!this.states.contains(paramArrayOfAccessibleState[b]))
          this.states.addElement(paramArrayOfAccessibleState[b]); 
      } 
    } 
  }
  
  public boolean remove(AccessibleState paramAccessibleState) { return (this.states == null) ? false : this.states.removeElement(paramAccessibleState); }
  
  public void clear() {
    if (this.states != null)
      this.states.removeAllElements(); 
  }
  
  public boolean contains(AccessibleState paramAccessibleState) { return (this.states == null) ? false : this.states.contains(paramAccessibleState); }
  
  public AccessibleState[] toArray() {
    if (this.states == null)
      return new AccessibleState[0]; 
    AccessibleState[] arrayOfAccessibleState = new AccessibleState[this.states.size()];
    for (byte b = 0; b < arrayOfAccessibleState.length; b++)
      arrayOfAccessibleState[b] = (AccessibleState)this.states.elementAt(b); 
    return arrayOfAccessibleState;
  }
  
  public String toString() {
    String str = null;
    if (this.states != null && this.states.size() > 0) {
      str = ((AccessibleState)this.states.elementAt(0)).toDisplayString();
      for (byte b = 1; b < this.states.size(); b++)
        str = str + "," + ((AccessibleState)this.states.elementAt(b)).toDisplayString(); 
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleStateSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */