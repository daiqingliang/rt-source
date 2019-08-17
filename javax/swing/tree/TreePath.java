package javax.swing.tree;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class TreePath implements Serializable {
  private TreePath parentPath;
  
  private Object lastPathComponent;
  
  @ConstructorProperties({"path"})
  public TreePath(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      throw new IllegalArgumentException("path in TreePath must be non null and not empty."); 
    this.lastPathComponent = paramArrayOfObject[paramArrayOfObject.length - 1];
    if (this.lastPathComponent == null)
      throw new IllegalArgumentException("Last path component must be non-null"); 
    if (paramArrayOfObject.length > 1)
      this.parentPath = new TreePath(paramArrayOfObject, paramArrayOfObject.length - 1); 
  }
  
  public TreePath(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("path in TreePath must be non null."); 
    this.lastPathComponent = paramObject;
    this.parentPath = null;
  }
  
  protected TreePath(TreePath paramTreePath, Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("path in TreePath must be non null."); 
    this.parentPath = paramTreePath;
    this.lastPathComponent = paramObject;
  }
  
  protected TreePath(Object[] paramArrayOfObject, int paramInt) {
    this.lastPathComponent = paramArrayOfObject[paramInt - 1];
    if (this.lastPathComponent == null)
      throw new IllegalArgumentException("Path elements must be non-null"); 
    if (paramInt > 1)
      this.parentPath = new TreePath(paramArrayOfObject, paramInt - 1); 
  }
  
  protected TreePath() {}
  
  public Object[] getPath() {
    int i = getPathCount();
    Object[] arrayOfObject = new Object[i--];
    for (TreePath treePath = this; treePath != null; treePath = treePath.getParentPath())
      arrayOfObject[i--] = treePath.getLastPathComponent(); 
    return arrayOfObject;
  }
  
  public Object getLastPathComponent() { return this.lastPathComponent; }
  
  public int getPathCount() {
    byte b = 0;
    for (TreePath treePath = this; treePath != null; treePath = treePath.getParentPath())
      b++; 
    return b;
  }
  
  public Object getPathComponent(int paramInt) {
    int i = getPathCount();
    if (paramInt < 0 || paramInt >= i)
      throw new IllegalArgumentException("Index " + paramInt + " is out of the specified range"); 
    TreePath treePath = this;
    for (int j = i - 1; j != paramInt; j--)
      treePath = treePath.getParentPath(); 
    return treePath.getLastPathComponent();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof TreePath) {
      TreePath treePath1 = (TreePath)paramObject;
      if (getPathCount() != treePath1.getPathCount())
        return false; 
      for (TreePath treePath2 = this; treePath2 != null; treePath2 = treePath2.getParentPath()) {
        if (!treePath2.getLastPathComponent().equals(treePath1.getLastPathComponent()))
          return false; 
        treePath1 = treePath1.getParentPath();
      } 
      return true;
    } 
    return false;
  }
  
  public int hashCode() { return getLastPathComponent().hashCode(); }
  
  public boolean isDescendant(TreePath paramTreePath) {
    if (paramTreePath == this)
      return true; 
    if (paramTreePath != null) {
      int i = getPathCount();
      int j = paramTreePath.getPathCount();
      if (j < i)
        return false; 
      while (j-- > i)
        paramTreePath = paramTreePath.getParentPath(); 
      return equals(paramTreePath);
    } 
    return false;
  }
  
  public TreePath pathByAddingChild(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException("Null child not allowed"); 
    return new TreePath(this, paramObject);
  }
  
  public TreePath getParentPath() { return this.parentPath; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("[");
    byte b = 0;
    int i = getPathCount();
    while (b < i) {
      if (b)
        stringBuffer.append(", "); 
      stringBuffer.append(getPathComponent(b));
      b++;
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\TreePath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */