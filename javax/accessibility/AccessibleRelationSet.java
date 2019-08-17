package javax.accessibility;

import java.util.Vector;

public class AccessibleRelationSet {
  protected Vector<AccessibleRelation> relations = null;
  
  public AccessibleRelationSet() { this.relations = null; }
  
  public AccessibleRelationSet(AccessibleRelation[] paramArrayOfAccessibleRelation) {
    if (paramArrayOfAccessibleRelation.length != 0) {
      this.relations = new Vector(paramArrayOfAccessibleRelation.length);
      for (byte b = 0; b < paramArrayOfAccessibleRelation.length; b++)
        add(paramArrayOfAccessibleRelation[b]); 
    } 
  }
  
  public boolean add(AccessibleRelation paramAccessibleRelation) {
    if (this.relations == null)
      this.relations = new Vector(); 
    AccessibleRelation accessibleRelation = get(paramAccessibleRelation.getKey());
    if (accessibleRelation == null) {
      this.relations.addElement(paramAccessibleRelation);
      return true;
    } 
    Object[] arrayOfObject1 = accessibleRelation.getTarget();
    Object[] arrayOfObject2 = paramAccessibleRelation.getTarget();
    int i = arrayOfObject1.length + arrayOfObject2.length;
    Object[] arrayOfObject3 = new Object[i];
    int j;
    for (j = 0; j < arrayOfObject1.length; j++)
      arrayOfObject3[j] = arrayOfObject1[j]; 
    j = arrayOfObject1.length;
    for (byte b = 0; j < i; b++) {
      arrayOfObject3[j] = arrayOfObject2[b];
      j++;
    } 
    accessibleRelation.setTarget(arrayOfObject3);
    return true;
  }
  
  public void addAll(AccessibleRelation[] paramArrayOfAccessibleRelation) {
    if (paramArrayOfAccessibleRelation.length != 0) {
      if (this.relations == null)
        this.relations = new Vector(paramArrayOfAccessibleRelation.length); 
      for (byte b = 0; b < paramArrayOfAccessibleRelation.length; b++)
        add(paramArrayOfAccessibleRelation[b]); 
    } 
  }
  
  public boolean remove(AccessibleRelation paramAccessibleRelation) { return (this.relations == null) ? false : this.relations.removeElement(paramAccessibleRelation); }
  
  public void clear() {
    if (this.relations != null)
      this.relations.removeAllElements(); 
  }
  
  public int size() { return (this.relations == null) ? 0 : this.relations.size(); }
  
  public boolean contains(String paramString) { return (get(paramString) != null); }
  
  public AccessibleRelation get(String paramString) {
    if (this.relations == null)
      return null; 
    int i = this.relations.size();
    for (byte b = 0; b < i; b++) {
      AccessibleRelation accessibleRelation = (AccessibleRelation)this.relations.elementAt(b);
      if (accessibleRelation != null && accessibleRelation.getKey().equals(paramString))
        return accessibleRelation; 
    } 
    return null;
  }
  
  public AccessibleRelation[] toArray() {
    if (this.relations == null)
      return new AccessibleRelation[0]; 
    AccessibleRelation[] arrayOfAccessibleRelation = new AccessibleRelation[this.relations.size()];
    for (byte b = 0; b < arrayOfAccessibleRelation.length; b++)
      arrayOfAccessibleRelation[b] = (AccessibleRelation)this.relations.elementAt(b); 
    return arrayOfAccessibleRelation;
  }
  
  public String toString() {
    String str = "";
    if (this.relations != null && this.relations.size() > 0) {
      str = ((AccessibleRelation)this.relations.elementAt(0)).toDisplayString();
      for (byte b = 1; b < this.relations.size(); b++)
        str = str + "," + ((AccessibleRelation)this.relations.elementAt(b)).toDisplayString(); 
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleRelationSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */