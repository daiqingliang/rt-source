package javax.swing.event;

import java.util.EventObject;
import javax.swing.tree.TreePath;

public class TreeModelEvent extends EventObject {
  protected TreePath path;
  
  protected int[] childIndices;
  
  protected Object[] children;
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject1, int[] paramArrayOfInt, Object[] paramArrayOfObject2) { this(paramObject, (paramArrayOfObject1 == null) ? null : new TreePath(paramArrayOfObject1), paramArrayOfInt, paramArrayOfObject2); }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath, int[] paramArrayOfInt, Object[] paramArrayOfObject) {
    super(paramObject);
    this.path = paramTreePath;
    this.childIndices = paramArrayOfInt;
    this.children = paramArrayOfObject;
  }
  
  public TreeModelEvent(Object paramObject, Object[] paramArrayOfObject) { this(paramObject, (paramArrayOfObject == null) ? null : new TreePath(paramArrayOfObject)); }
  
  public TreeModelEvent(Object paramObject, TreePath paramTreePath) {
    super(paramObject);
    this.path = paramTreePath;
    this.childIndices = new int[0];
  }
  
  public TreePath getTreePath() { return this.path; }
  
  public Object[] getPath() { return (this.path != null) ? this.path.getPath() : null; }
  
  public Object[] getChildren() {
    if (this.children != null) {
      int i = this.children.length;
      Object[] arrayOfObject = new Object[i];
      System.arraycopy(this.children, 0, arrayOfObject, 0, i);
      return arrayOfObject;
    } 
    return null;
  }
  
  public int[] getChildIndices() {
    if (this.childIndices != null) {
      int i = this.childIndices.length;
      int[] arrayOfInt = new int[i];
      System.arraycopy(this.childIndices, 0, arrayOfInt, 0, i);
      return arrayOfInt;
    } 
    return null;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getName() + " " + Integer.toString(hashCode()));
    if (this.path != null)
      stringBuffer.append(" path " + this.path); 
    if (this.childIndices != null) {
      stringBuffer.append(" indices [ ");
      for (byte b = 0; b < this.childIndices.length; b++)
        stringBuffer.append(Integer.toString(this.childIndices[b]) + " "); 
      stringBuffer.append("]");
    } 
    if (this.children != null) {
      stringBuffer.append(" children [ ");
      for (byte b = 0; b < this.children.length; b++)
        stringBuffer.append(this.children[b] + " "); 
      stringBuffer.append("]");
    } 
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\TreeModelEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */