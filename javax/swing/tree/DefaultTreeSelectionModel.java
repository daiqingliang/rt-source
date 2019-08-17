package javax.swing.tree;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class DefaultTreeSelectionModel implements Cloneable, Serializable, TreeSelectionModel {
  public static final String SELECTION_MODE_PROPERTY = "selectionMode";
  
  protected SwingPropertyChangeSupport changeSupport;
  
  protected TreePath[] selection;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  protected RowMapper rowMapper;
  
  protected DefaultListSelectionModel listSelectionModel = new DefaultListSelectionModel();
  
  protected int selectionMode = 4;
  
  protected TreePath leadPath;
  
  protected int leadIndex = this.leadRow = -1;
  
  protected int leadRow;
  
  private Hashtable<TreePath, Boolean> uniquePaths = new Hashtable();
  
  private Hashtable<TreePath, Boolean> lastPaths = new Hashtable();
  
  private TreePath[] tempPaths = new TreePath[1];
  
  public void setRowMapper(RowMapper paramRowMapper) {
    this.rowMapper = paramRowMapper;
    resetRowSelection();
  }
  
  public RowMapper getRowMapper() { return this.rowMapper; }
  
  public void setSelectionMode(int paramInt) {
    int i = this.selectionMode;
    this.selectionMode = paramInt;
    if (this.selectionMode != 1 && this.selectionMode != 2 && this.selectionMode != 4)
      this.selectionMode = 4; 
    if (i != this.selectionMode && this.changeSupport != null)
      this.changeSupport.firePropertyChange("selectionMode", Integer.valueOf(i), Integer.valueOf(this.selectionMode)); 
  }
  
  public int getSelectionMode() { return this.selectionMode; }
  
  public void setSelectionPath(TreePath paramTreePath) {
    if (paramTreePath == null) {
      setSelectionPaths(null);
    } else {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      setSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public void setSelectionPaths(TreePath[] paramArrayOfTreePath) {
    int j;
    int i;
    TreePath[] arrayOfTreePath = paramArrayOfTreePath;
    if (arrayOfTreePath == null) {
      i = 0;
    } else {
      i = arrayOfTreePath.length;
    } 
    if (this.selection == null) {
      j = 0;
    } else {
      j = this.selection.length;
    } 
    if (i + j != 0) {
      if (this.selectionMode == 1) {
        if (i > 1) {
          arrayOfTreePath = new TreePath[1];
          arrayOfTreePath[0] = paramArrayOfTreePath[0];
          i = 1;
        } 
      } else if (this.selectionMode == 2 && i > 0 && !arePathsContiguous(arrayOfTreePath)) {
        arrayOfTreePath = new TreePath[1];
        arrayOfTreePath[0] = paramArrayOfTreePath[0];
        i = 1;
      } 
      TreePath treePath = this.leadPath;
      Vector vector = new Vector(i + j);
      ArrayList arrayList = new ArrayList(i);
      this.lastPaths.clear();
      this.leadPath = null;
      for (byte b1 = 0; b1 < i; b1++) {
        TreePath treePath1 = arrayOfTreePath[b1];
        if (treePath1 != null && this.lastPaths.get(treePath1) == null) {
          this.lastPaths.put(treePath1, Boolean.TRUE);
          if (this.uniquePaths.get(treePath1) == null)
            vector.addElement(new PathPlaceHolder(treePath1, true)); 
          this.leadPath = treePath1;
          arrayList.add(treePath1);
        } 
      } 
      TreePath[] arrayOfTreePath1 = (TreePath[])arrayList.toArray(new TreePath[arrayList.size()]);
      for (byte b2 = 0; b2 < j; b2++) {
        if (this.selection[b2] != null && this.lastPaths.get(this.selection[b2]) == null)
          vector.addElement(new PathPlaceHolder(this.selection[b2], false)); 
      } 
      this.selection = arrayOfTreePath1;
      Hashtable hashtable = this.uniquePaths;
      this.uniquePaths = this.lastPaths;
      this.lastPaths = hashtable;
      this.lastPaths.clear();
      insureUniqueness();
      updateLeadIndex();
      resetRowSelection();
      if (vector.size() > 0)
        notifyPathChange(vector, treePath); 
    } 
  }
  
  public void addSelectionPath(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      addSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public void addSelectionPaths(TreePath[] paramArrayOfTreePath) {
    boolean bool = (paramArrayOfTreePath == null) ? 0 : paramArrayOfTreePath.length;
    if (bool)
      if (this.selectionMode == 1) {
        setSelectionPaths(paramArrayOfTreePath);
      } else if (this.selectionMode == 2 && !canPathsBeAdded(paramArrayOfTreePath)) {
        if (arePathsContiguous(paramArrayOfTreePath)) {
          setSelectionPaths(paramArrayOfTreePath);
        } else {
          TreePath[] arrayOfTreePath = new TreePath[1];
          arrayOfTreePath[0] = paramArrayOfTreePath[0];
          setSelectionPaths(arrayOfTreePath);
        } 
      } else {
        int k;
        TreePath treePath = this.leadPath;
        Vector vector = null;
        if (this.selection == null) {
          k = 0;
        } else {
          k = this.selection.length;
        } 
        this.lastPaths.clear();
        int i = 0;
        int j = 0;
        while (i < bool) {
          if (paramArrayOfTreePath[i] != null) {
            if (this.uniquePaths.get(paramArrayOfTreePath[i]) == null) {
              j++;
              if (vector == null)
                vector = new Vector(); 
              vector.addElement(new PathPlaceHolder(paramArrayOfTreePath[i], true));
              this.uniquePaths.put(paramArrayOfTreePath[i], Boolean.TRUE);
              this.lastPaths.put(paramArrayOfTreePath[i], Boolean.TRUE);
            } 
            this.leadPath = paramArrayOfTreePath[i];
          } 
          i++;
        } 
        if (this.leadPath == null)
          this.leadPath = treePath; 
        if (j > 0) {
          TreePath[] arrayOfTreePath = new TreePath[k + j];
          if (k > 0)
            System.arraycopy(this.selection, 0, arrayOfTreePath, 0, k); 
          if (j != paramArrayOfTreePath.length) {
            Enumeration enumeration = this.lastPaths.keys();
            i = k;
            while (enumeration.hasMoreElements())
              arrayOfTreePath[i++] = (TreePath)enumeration.nextElement(); 
          } else {
            System.arraycopy(paramArrayOfTreePath, 0, arrayOfTreePath, k, j);
          } 
          this.selection = arrayOfTreePath;
          insureUniqueness();
          updateLeadIndex();
          resetRowSelection();
          notifyPathChange(vector, treePath);
        } else {
          this.leadPath = treePath;
        } 
        this.lastPaths.clear();
      }  
  }
  
  public void removeSelectionPath(TreePath paramTreePath) {
    if (paramTreePath != null) {
      TreePath[] arrayOfTreePath = new TreePath[1];
      arrayOfTreePath[0] = paramTreePath;
      removeSelectionPaths(arrayOfTreePath);
    } 
  }
  
  public void removeSelectionPaths(TreePath[] paramArrayOfTreePath) {
    if (paramArrayOfTreePath != null && this.selection != null && paramArrayOfTreePath.length > 0)
      if (!canPathsBeRemoved(paramArrayOfTreePath)) {
        clearSelection();
      } else {
        Vector vector = null;
        int i;
        for (i = paramArrayOfTreePath.length - 1; i >= 0; i--) {
          if (paramArrayOfTreePath[i] != null && this.uniquePaths.get(paramArrayOfTreePath[i]) != null) {
            if (vector == null)
              vector = new Vector(paramArrayOfTreePath.length); 
            this.uniquePaths.remove(paramArrayOfTreePath[i]);
            vector.addElement(new PathPlaceHolder(paramArrayOfTreePath[i], false));
          } 
        } 
        if (vector != null) {
          i = vector.size();
          TreePath treePath = this.leadPath;
          if (i == this.selection.length) {
            this.selection = null;
          } else {
            Enumeration enumeration = this.uniquePaths.keys();
            byte b = 0;
            this.selection = new TreePath[this.selection.length - i];
            while (enumeration.hasMoreElements())
              this.selection[b++] = (TreePath)enumeration.nextElement(); 
          } 
          if (this.leadPath != null && this.uniquePaths.get(this.leadPath) == null) {
            if (this.selection != null) {
              this.leadPath = this.selection[this.selection.length - 1];
            } else {
              this.leadPath = null;
            } 
          } else if (this.selection != null) {
            this.leadPath = this.selection[this.selection.length - 1];
          } else {
            this.leadPath = null;
          } 
          updateLeadIndex();
          resetRowSelection();
          notifyPathChange(vector, treePath);
        } 
      }  
  }
  
  public TreePath getSelectionPath() { return (this.selection != null && this.selection.length > 0) ? this.selection[0] : null; }
  
  public TreePath[] getSelectionPaths() {
    if (this.selection != null) {
      int i = this.selection.length;
      TreePath[] arrayOfTreePath = new TreePath[i];
      System.arraycopy(this.selection, 0, arrayOfTreePath, 0, i);
      return arrayOfTreePath;
    } 
    return new TreePath[0];
  }
  
  public int getSelectionCount() { return (this.selection == null) ? 0 : this.selection.length; }
  
  public boolean isPathSelected(TreePath paramTreePath) { return (paramTreePath != null) ? ((this.uniquePaths.get(paramTreePath) != null)) : false; }
  
  public boolean isSelectionEmpty() { return (this.selection == null || this.selection.length == 0); }
  
  public void clearSelection() {
    if (this.selection != null && this.selection.length > 0) {
      int i = this.selection.length;
      boolean[] arrayOfBoolean = new boolean[i];
      for (byte b = 0; b < i; b++)
        arrayOfBoolean[b] = false; 
      TreeSelectionEvent treeSelectionEvent = new TreeSelectionEvent(this, this.selection, arrayOfBoolean, this.leadPath, null);
      this.leadPath = null;
      this.leadIndex = this.leadRow = -1;
      this.uniquePaths.clear();
      this.selection = null;
      resetRowSelection();
      fireValueChanged(treeSelectionEvent);
    } 
  }
  
  public void addTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) { this.listenerList.add(TreeSelectionListener.class, paramTreeSelectionListener); }
  
  public void removeTreeSelectionListener(TreeSelectionListener paramTreeSelectionListener) { this.listenerList.remove(TreeSelectionListener.class, paramTreeSelectionListener); }
  
  public TreeSelectionListener[] getTreeSelectionListeners() { return (TreeSelectionListener[])this.listenerList.getListeners(TreeSelectionListener.class); }
  
  protected void fireValueChanged(TreeSelectionEvent paramTreeSelectionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TreeSelectionListener.class)
        ((TreeSelectionListener)arrayOfObject[i + 1]).valueChanged(paramTreeSelectionEvent); 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  public int[] getSelectionRows() {
    if (this.rowMapper != null && this.selection != null && this.selection.length > 0) {
      int[] arrayOfInt = this.rowMapper.getRowsForPaths(this.selection);
      if (arrayOfInt != null) {
        int i = 0;
        for (int j = arrayOfInt.length - 1; j >= 0; j--) {
          if (arrayOfInt[j] == -1)
            i++; 
        } 
        if (i > 0)
          if (i == arrayOfInt.length) {
            arrayOfInt = null;
          } else {
            int[] arrayOfInt1 = new int[arrayOfInt.length - i];
            int k = arrayOfInt.length - 1;
            byte b = 0;
            while (k >= 0) {
              if (arrayOfInt[k] != -1)
                arrayOfInt1[b++] = arrayOfInt[k]; 
              k--;
            } 
            arrayOfInt = arrayOfInt1;
          }  
      } 
      return arrayOfInt;
    } 
    return new int[0];
  }
  
  public int getMinSelectionRow() { return this.listSelectionModel.getMinSelectionIndex(); }
  
  public int getMaxSelectionRow() { return this.listSelectionModel.getMaxSelectionIndex(); }
  
  public boolean isRowSelected(int paramInt) { return this.listSelectionModel.isSelectedIndex(paramInt); }
  
  public void resetRowSelection() {
    this.listSelectionModel.clearSelection();
    if (this.selection != null && this.rowMapper != null) {
      boolean bool = false;
      int[] arrayOfInt = this.rowMapper.getRowsForPaths(this.selection);
      byte b = 0;
      int i = this.selection.length;
      while (b < i) {
        int j = arrayOfInt[b];
        if (j != -1)
          this.listSelectionModel.addSelectionInterval(j, j); 
        b++;
      } 
      if (this.leadIndex != -1 && arrayOfInt != null) {
        this.leadRow = arrayOfInt[this.leadIndex];
      } else if (this.leadPath != null) {
        this.tempPaths[0] = this.leadPath;
        arrayOfInt = this.rowMapper.getRowsForPaths(this.tempPaths);
        this.leadRow = (arrayOfInt != null) ? arrayOfInt[0] : -1;
      } else {
        this.leadRow = -1;
      } 
      insureRowContinuity();
    } else {
      this.leadRow = -1;
    } 
  }
  
  public int getLeadSelectionRow() { return this.leadRow; }
  
  public TreePath getLeadSelectionPath() { return this.leadPath; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      this.changeSupport = new SwingPropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      return; 
    this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  protected void insureRowContinuity() {
    if (this.selectionMode == 2 && this.selection != null && this.rowMapper != null) {
      DefaultListSelectionModel defaultListSelectionModel = this.listSelectionModel;
      int i = defaultListSelectionModel.getMinSelectionIndex();
      if (i != -1) {
        int j = i;
        int k = defaultListSelectionModel.getMaxSelectionIndex();
        while (j <= k) {
          if (!defaultListSelectionModel.isSelectedIndex(j))
            if (j == i) {
              clearSelection();
            } else {
              TreePath[] arrayOfTreePath = new TreePath[j - i];
              int[] arrayOfInt = this.rowMapper.getRowsForPaths(this.selection);
              for (byte b = 0; b < arrayOfInt.length; b++) {
                if (arrayOfInt[b] < j)
                  arrayOfTreePath[arrayOfInt[b] - i] = this.selection[b]; 
              } 
              setSelectionPaths(arrayOfTreePath);
              break;
            }  
          j++;
        } 
      } 
    } else if (this.selectionMode == 1 && this.selection != null && this.selection.length > 1) {
      setSelectionPath(this.selection[0]);
    } 
  }
  
  protected boolean arePathsContiguous(TreePath[] paramArrayOfTreePath) {
    if (this.rowMapper == null || paramArrayOfTreePath.length < 2)
      return true; 
    BitSet bitSet = new BitSet(32);
    int k = paramArrayOfTreePath.length;
    int m = 0;
    TreePath[] arrayOfTreePath = new TreePath[1];
    arrayOfTreePath[0] = paramArrayOfTreePath[0];
    int j = this.rowMapper.getRowsForPaths(arrayOfTreePath)[0];
    int i;
    for (i = 0; i < k; i++) {
      if (paramArrayOfTreePath[i] != null) {
        arrayOfTreePath[0] = paramArrayOfTreePath[i];
        int[] arrayOfInt = this.rowMapper.getRowsForPaths(arrayOfTreePath);
        if (arrayOfInt == null)
          return false; 
        int i1 = arrayOfInt[0];
        if (i1 == -1 || i1 < j - k || i1 > j + k)
          return false; 
        if (i1 < j)
          j = i1; 
        if (!bitSet.get(i1)) {
          bitSet.set(i1);
          m++;
        } 
      } 
    } 
    int n = m + j;
    for (i = j; i < n; i++) {
      if (!bitSet.get(i))
        return false; 
    } 
    return true;
  }
  
  protected boolean canPathsBeAdded(TreePath[] paramArrayOfTreePath) {
    if (paramArrayOfTreePath == null || paramArrayOfTreePath.length == 0 || this.rowMapper == null || this.selection == null || this.selectionMode == 4)
      return true; 
    BitSet bitSet = new BitSet();
    DefaultListSelectionModel defaultListSelectionModel = this.listSelectionModel;
    int j = defaultListSelectionModel.getMinSelectionIndex();
    int k = defaultListSelectionModel.getMaxSelectionIndex();
    TreePath[] arrayOfTreePath = new TreePath[1];
    if (j != -1) {
      for (int m = j; m <= k; m++) {
        if (defaultListSelectionModel.isSelectedIndex(m))
          bitSet.set(m); 
      } 
    } else {
      arrayOfTreePath[0] = paramArrayOfTreePath[0];
      j = k = this.rowMapper.getRowsForPaths(arrayOfTreePath)[0];
    } 
    int i;
    for (i = paramArrayOfTreePath.length - 1; i >= 0; i--) {
      if (paramArrayOfTreePath[i] != null) {
        arrayOfTreePath[0] = paramArrayOfTreePath[i];
        int[] arrayOfInt = this.rowMapper.getRowsForPaths(arrayOfTreePath);
        if (arrayOfInt == null)
          return false; 
        int m = arrayOfInt[0];
        j = Math.min(m, j);
        k = Math.max(m, k);
        if (m == -1)
          return false; 
        bitSet.set(m);
      } 
    } 
    for (i = j; i <= k; i++) {
      if (!bitSet.get(i))
        return false; 
    } 
    return true;
  }
  
  protected boolean canPathsBeRemoved(TreePath[] paramArrayOfTreePath) {
    if (this.rowMapper == null || this.selection == null || this.selectionMode == 4)
      return true; 
    BitSet bitSet = new BitSet();
    int j = paramArrayOfTreePath.length;
    int k = -1;
    int m = 0;
    TreePath[] arrayOfTreePath = new TreePath[1];
    this.lastPaths.clear();
    int i;
    for (i = 0; i < j; i++) {
      if (paramArrayOfTreePath[i] != null)
        this.lastPaths.put(paramArrayOfTreePath[i], Boolean.TRUE); 
    } 
    for (i = this.selection.length - 1; i >= 0; i--) {
      if (this.lastPaths.get(this.selection[i]) == null) {
        arrayOfTreePath[0] = this.selection[i];
        int[] arrayOfInt = this.rowMapper.getRowsForPaths(arrayOfTreePath);
        if (arrayOfInt != null && arrayOfInt[0] != -1 && !bitSet.get(arrayOfInt[0])) {
          m++;
          if (k == -1) {
            k = arrayOfInt[0];
          } else {
            k = Math.min(k, arrayOfInt[0]);
          } 
          bitSet.set(arrayOfInt[0]);
        } 
      } 
    } 
    this.lastPaths.clear();
    if (m > 1)
      for (i = k + m - 1; i >= k; i--) {
        if (!bitSet.get(i))
          return false; 
      }  
    return true;
  }
  
  @Deprecated
  protected void notifyPathChange(Vector<?> paramVector, TreePath paramTreePath) {
    int i = paramVector.size();
    boolean[] arrayOfBoolean = new boolean[i];
    TreePath[] arrayOfTreePath = new TreePath[i];
    for (byte b = 0; b < i; b++) {
      PathPlaceHolder pathPlaceHolder = (PathPlaceHolder)paramVector.elementAt(b);
      arrayOfBoolean[b] = pathPlaceHolder.isNew;
      arrayOfTreePath[b] = pathPlaceHolder.path;
    } 
    TreeSelectionEvent treeSelectionEvent = new TreeSelectionEvent(this, arrayOfTreePath, arrayOfBoolean, paramTreePath, this.leadPath);
    fireValueChanged(treeSelectionEvent);
  }
  
  protected void updateLeadIndex() {
    if (this.leadPath != null) {
      if (this.selection == null) {
        this.leadPath = null;
        this.leadIndex = this.leadRow = -1;
      } else {
        this.leadRow = this.leadIndex = -1;
        for (int i = this.selection.length - 1; i >= 0; i--) {
          if (this.selection[i] == this.leadPath) {
            this.leadIndex = i;
            break;
          } 
        } 
      } 
    } else {
      this.leadIndex = -1;
    } 
  }
  
  protected void insureUniqueness() {}
  
  public String toString() {
    Object object;
    int i = getSelectionCount();
    StringBuffer stringBuffer = new StringBuffer();
    if (this.rowMapper != null) {
      object = this.rowMapper.getRowsForPaths(this.selection);
    } else {
      object = null;
    } 
    stringBuffer.append(getClass().getName() + " " + hashCode() + " [ ");
    for (byte b = 0; b < i; b++) {
      if (object != null) {
        stringBuffer.append(this.selection[b].toString() + "@" + Integer.toString(object[b]) + " ");
      } else {
        stringBuffer.append(this.selection[b].toString() + " ");
      } 
    } 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public Object clone() throws CloneNotSupportedException {
    DefaultTreeSelectionModel defaultTreeSelectionModel = (DefaultTreeSelectionModel)super.clone();
    defaultTreeSelectionModel.changeSupport = null;
    if (this.selection != null) {
      int i = this.selection.length;
      defaultTreeSelectionModel.selection = new TreePath[i];
      System.arraycopy(this.selection, 0, defaultTreeSelectionModel.selection, 0, i);
    } 
    defaultTreeSelectionModel.listenerList = new EventListenerList();
    defaultTreeSelectionModel.listSelectionModel = (DefaultListSelectionModel)this.listSelectionModel.clone();
    defaultTreeSelectionModel.uniquePaths = new Hashtable();
    defaultTreeSelectionModel.lastPaths = new Hashtable();
    defaultTreeSelectionModel.tempPaths = new TreePath[1];
    return defaultTreeSelectionModel;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Object[] arrayOfObject;
    paramObjectOutputStream.defaultWriteObject();
    if (this.rowMapper != null && this.rowMapper instanceof Serializable) {
      arrayOfObject = new Object[2];
      arrayOfObject[0] = "rowMapper";
      arrayOfObject[1] = this.rowMapper;
    } else {
      arrayOfObject = new Object[0];
    } 
    paramObjectOutputStream.writeObject(arrayOfObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Object[] arrayOfObject = (Object[])paramObjectInputStream.readObject();
    if (arrayOfObject.length > 0 && arrayOfObject[0].equals("rowMapper"))
      this.rowMapper = (RowMapper)arrayOfObject[1]; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\DefaultTreeSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */