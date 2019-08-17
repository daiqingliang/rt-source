package javax.swing;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class DefaultRowSorter<M, I> extends RowSorter<M> {
  private boolean sortsOnUpdates;
  
  private Row[] viewToModel;
  
  private int[] modelToView;
  
  private Comparator[] comparators;
  
  private boolean[] isSortable;
  
  private RowSorter.SortKey[] cachedSortKeys;
  
  private Comparator[] sortComparators;
  
  private RowFilter<? super M, ? super I> filter;
  
  private FilterEntry filterEntry;
  
  private List<RowSorter.SortKey> sortKeys = Collections.emptyList();
  
  private boolean[] useToString;
  
  private boolean sorted;
  
  private int maxSortKeys = 3;
  
  private ModelWrapper<M, I> modelWrapper;
  
  private int modelRowCount;
  
  protected final void setModelWrapper(ModelWrapper<M, I> paramModelWrapper) {
    if (paramModelWrapper == null)
      throw new IllegalArgumentException("modelWrapper most be non-null"); 
    ModelWrapper modelWrapper1 = this.modelWrapper;
    this.modelWrapper = paramModelWrapper;
    if (modelWrapper1 != null) {
      modelStructureChanged();
    } else {
      this.modelRowCount = getModelWrapper().getRowCount();
    } 
  }
  
  protected final ModelWrapper<M, I> getModelWrapper() { return this.modelWrapper; }
  
  public final M getModel() { return (M)getModelWrapper().getModel(); }
  
  public void setSortable(int paramInt, boolean paramBoolean) {
    checkColumn(paramInt);
    if (this.isSortable == null) {
      this.isSortable = new boolean[getModelWrapper().getColumnCount()];
      for (int i = this.isSortable.length - 1; i >= 0; i--)
        this.isSortable[i] = true; 
    } 
    this.isSortable[paramInt] = paramBoolean;
  }
  
  public boolean isSortable(int paramInt) {
    checkColumn(paramInt);
    return (this.isSortable == null) ? true : this.isSortable[paramInt];
  }
  
  public void setSortKeys(List<? extends RowSorter.SortKey> paramList) {
    List list = this.sortKeys;
    if (paramList != null && paramList.size() > 0) {
      int i = getModelWrapper().getColumnCount();
      for (RowSorter.SortKey sortKey : paramList) {
        if (sortKey == null || sortKey.getColumn() < 0 || sortKey.getColumn() >= i)
          throw new IllegalArgumentException("Invalid SortKey"); 
      } 
      this.sortKeys = Collections.unmodifiableList(new ArrayList(paramList));
    } else {
      this.sortKeys = Collections.emptyList();
    } 
    if (!this.sortKeys.equals(list)) {
      fireSortOrderChanged();
      if (this.viewToModel == null) {
        sort();
      } else {
        sortExistingData();
      } 
    } 
  }
  
  public List<? extends RowSorter.SortKey> getSortKeys() { return this.sortKeys; }
  
  public void setMaxSortKeys(int paramInt) {
    if (paramInt < 1)
      throw new IllegalArgumentException("Invalid max"); 
    this.maxSortKeys = paramInt;
  }
  
  public int getMaxSortKeys() { return this.maxSortKeys; }
  
  public void setSortsOnUpdates(boolean paramBoolean) { this.sortsOnUpdates = paramBoolean; }
  
  public boolean getSortsOnUpdates() { return this.sortsOnUpdates; }
  
  public void setRowFilter(RowFilter<? super M, ? super I> paramRowFilter) {
    this.filter = paramRowFilter;
    sort();
  }
  
  public RowFilter<? super M, ? super I> getRowFilter() { return this.filter; }
  
  public void toggleSortOrder(int paramInt) {
    checkColumn(paramInt);
    if (isSortable(paramInt)) {
      List list = new ArrayList(getSortKeys());
      int i;
      for (i = list.size() - 1; i >= 0 && ((RowSorter.SortKey)list.get(i)).getColumn() != paramInt; i--);
      if (i == -1) {
        RowSorter.SortKey sortKey = new RowSorter.SortKey(paramInt, SortOrder.ASCENDING);
        list.add(0, sortKey);
      } else if (i == 0) {
        list.set(0, toggle((RowSorter.SortKey)list.get(0)));
      } else {
        list.remove(i);
        list.add(0, new RowSorter.SortKey(paramInt, SortOrder.ASCENDING));
      } 
      if (list.size() > getMaxSortKeys())
        list = list.subList(0, getMaxSortKeys()); 
      setSortKeys(list);
    } 
  }
  
  private RowSorter.SortKey toggle(RowSorter.SortKey paramSortKey) { return (paramSortKey.getSortOrder() == SortOrder.ASCENDING) ? new RowSorter.SortKey(paramSortKey.getColumn(), SortOrder.DESCENDING) : new RowSorter.SortKey(paramSortKey.getColumn(), SortOrder.ASCENDING); }
  
  public int convertRowIndexToView(int paramInt) {
    if (this.modelToView == null) {
      if (paramInt < 0 || paramInt >= getModelWrapper().getRowCount())
        throw new IndexOutOfBoundsException("Invalid index"); 
      return paramInt;
    } 
    return this.modelToView[paramInt];
  }
  
  public int convertRowIndexToModel(int paramInt) {
    if (this.viewToModel == null) {
      if (paramInt < 0 || paramInt >= getModelWrapper().getRowCount())
        throw new IndexOutOfBoundsException("Invalid index"); 
      return paramInt;
    } 
    return (this.viewToModel[paramInt]).modelIndex;
  }
  
  private boolean isUnsorted() {
    List list = getSortKeys();
    int i = list.size();
    return (i == 0 || ((RowSorter.SortKey)list.get(false)).getSortOrder() == SortOrder.UNSORTED);
  }
  
  private void sortExistingData() {
    int[] arrayOfInt = getViewToModelAsInts(this.viewToModel);
    updateUseToString();
    cacheSortKeys(getSortKeys());
    if (isUnsorted()) {
      if (getRowFilter() == null) {
        this.viewToModel = null;
        this.modelToView = null;
      } else {
        byte b1 = 0;
        for (byte b2 = 0; b2 < this.modelToView.length; b2++) {
          if (this.modelToView[b2] != -1) {
            (this.viewToModel[b1]).modelIndex = b2;
            this.modelToView[b2] = b1++;
          } 
        } 
      } 
    } else {
      Arrays.sort(this.viewToModel);
      setModelToViewFromViewToModel(false);
    } 
    fireRowSorterChanged(arrayOfInt);
  }
  
  public void sort() {
    this.sorted = true;
    int[] arrayOfInt = getViewToModelAsInts(this.viewToModel);
    updateUseToString();
    if (isUnsorted()) {
      this.cachedSortKeys = new RowSorter.SortKey[0];
      if (getRowFilter() == null) {
        if (this.viewToModel != null) {
          this.viewToModel = null;
          this.modelToView = null;
        } else {
          return;
        } 
      } else {
        initializeFilteredMapping();
      } 
    } else {
      cacheSortKeys(getSortKeys());
      if (getRowFilter() != null) {
        initializeFilteredMapping();
      } else {
        createModelToView(getModelWrapper().getRowCount());
        createViewToModel(getModelWrapper().getRowCount());
      } 
      Arrays.sort(this.viewToModel);
      setModelToViewFromViewToModel(false);
    } 
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void updateUseToString() {
    int i = getModelWrapper().getColumnCount();
    if (this.useToString == null || this.useToString.length != i)
      this.useToString = new boolean[i]; 
    while (--i >= 0) {
      this.useToString[i] = useToString(i);
      i--;
    } 
  }
  
  private void initializeFilteredMapping() {
    int i = getModelWrapper().getRowCount();
    int j = 0;
    createModelToView(i);
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      if (include(b1)) {
        this.modelToView[b1] = b1 - j;
      } else {
        this.modelToView[b1] = -1;
        j++;
      } 
    } 
    createViewToModel(i - j);
    b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      if (this.modelToView[b1] != -1)
        (this.viewToModel[b2++]).modelIndex = b1; 
      b1++;
    } 
  }
  
  private void createModelToView(int paramInt) {
    if (this.modelToView == null || this.modelToView.length != paramInt)
      this.modelToView = new int[paramInt]; 
  }
  
  private void createViewToModel(int paramInt) {
    int i = 0;
    if (this.viewToModel != null) {
      i = Math.min(paramInt, this.viewToModel.length);
      if (this.viewToModel.length != paramInt) {
        Row[] arrayOfRow = this.viewToModel;
        this.viewToModel = new Row[paramInt];
        System.arraycopy(arrayOfRow, 0, this.viewToModel, 0, i);
      } 
    } else {
      this.viewToModel = new Row[paramInt];
    } 
    int j;
    for (j = 0; j < i; j++)
      (this.viewToModel[j]).modelIndex = j; 
    for (j = i; j < paramInt; j++)
      this.viewToModel[j] = new Row(this, j); 
  }
  
  private void cacheSortKeys(List<? extends RowSorter.SortKey> paramList) {
    int i = paramList.size();
    this.sortComparators = new Comparator[i];
    for (byte b = 0; b < i; b++)
      this.sortComparators[b] = getComparator0(((RowSorter.SortKey)paramList.get(b)).getColumn()); 
    this.cachedSortKeys = (SortKey[])paramList.toArray(new RowSorter.SortKey[i]);
  }
  
  protected boolean useToString(int paramInt) { return (getComparator(paramInt) == null); }
  
  private void setModelToViewFromViewToModel(boolean paramBoolean) {
    if (paramBoolean)
      for (int j = this.modelToView.length - 1; j >= 0; j--)
        this.modelToView[j] = -1;  
    for (int i = this.viewToModel.length - 1; i >= 0; i--)
      this.modelToView[(this.viewToModel[i]).modelIndex] = i; 
  }
  
  private int[] getViewToModelAsInts(Row[] paramArrayOfRow) {
    if (paramArrayOfRow != null) {
      int[] arrayOfInt = new int[paramArrayOfRow.length];
      for (int i = paramArrayOfRow.length - 1; i >= 0; i--)
        arrayOfInt[i] = (paramArrayOfRow[i]).modelIndex; 
      return arrayOfInt;
    } 
    return new int[0];
  }
  
  public void setComparator(int paramInt, Comparator<?> paramComparator) {
    checkColumn(paramInt);
    if (this.comparators == null)
      this.comparators = new Comparator[getModelWrapper().getColumnCount()]; 
    this.comparators[paramInt] = paramComparator;
  }
  
  public Comparator<?> getComparator(int paramInt) {
    checkColumn(paramInt);
    return (this.comparators != null) ? this.comparators[paramInt] : null;
  }
  
  private Comparator getComparator0(int paramInt) {
    Comparator comparator = getComparator(paramInt);
    return (comparator != null) ? comparator : Collator.getInstance();
  }
  
  private RowFilter.Entry<M, I> getFilterEntry(int paramInt) {
    if (this.filterEntry == null)
      this.filterEntry = new FilterEntry(null); 
    this.filterEntry.modelIndex = paramInt;
    return this.filterEntry;
  }
  
  public int getViewRowCount() { return (this.viewToModel != null) ? this.viewToModel.length : getModelWrapper().getRowCount(); }
  
  public int getModelRowCount() { return getModelWrapper().getRowCount(); }
  
  private void allChanged() {
    this.modelToView = null;
    this.viewToModel = null;
    this.comparators = null;
    this.isSortable = null;
    if (isUnsorted()) {
      sort();
    } else {
      setSortKeys(null);
    } 
  }
  
  public void modelStructureChanged() {
    allChanged();
    this.modelRowCount = getModelWrapper().getRowCount();
  }
  
  public void allRowsChanged() {
    this.modelRowCount = getModelWrapper().getRowCount();
    sort();
  }
  
  public void rowsInserted(int paramInt1, int paramInt2) {
    checkAgainstModel(paramInt1, paramInt2);
    int i = getModelWrapper().getRowCount();
    if (paramInt2 >= i)
      throw new IndexOutOfBoundsException("Invalid range"); 
    this.modelRowCount = i;
    if (shouldOptimizeChange(paramInt1, paramInt2))
      rowsInserted0(paramInt1, paramInt2); 
  }
  
  public void rowsDeleted(int paramInt1, int paramInt2) {
    checkAgainstModel(paramInt1, paramInt2);
    if (paramInt1 >= this.modelRowCount || paramInt2 >= this.modelRowCount)
      throw new IndexOutOfBoundsException("Invalid range"); 
    this.modelRowCount = getModelWrapper().getRowCount();
    if (shouldOptimizeChange(paramInt1, paramInt2))
      rowsDeleted0(paramInt1, paramInt2); 
  }
  
  public void rowsUpdated(int paramInt1, int paramInt2) {
    checkAgainstModel(paramInt1, paramInt2);
    if (paramInt1 >= this.modelRowCount || paramInt2 >= this.modelRowCount)
      throw new IndexOutOfBoundsException("Invalid range"); 
    if (getSortsOnUpdates()) {
      if (shouldOptimizeChange(paramInt1, paramInt2))
        rowsUpdated0(paramInt1, paramInt2); 
    } else {
      this.sorted = false;
    } 
  }
  
  public void rowsUpdated(int paramInt1, int paramInt2, int paramInt3) {
    checkColumn(paramInt3);
    rowsUpdated(paramInt1, paramInt2);
  }
  
  private void checkAgainstModel(int paramInt1, int paramInt2) {
    if (paramInt1 > paramInt2 || paramInt1 < 0 || paramInt2 < 0 || paramInt1 > this.modelRowCount)
      throw new IndexOutOfBoundsException("Invalid range"); 
  }
  
  private boolean include(int paramInt) {
    RowFilter rowFilter = getRowFilter();
    return (rowFilter != null) ? rowFilter.include(getFilterEntry(paramInt)) : 1;
  }
  
  private int compare(int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.cachedSortKeys.length; b++) {
      int j;
      int i = this.cachedSortKeys[b].getColumn();
      SortOrder sortOrder = this.cachedSortKeys[b].getSortOrder();
      if (sortOrder == SortOrder.UNSORTED) {
        j = paramInt1 - paramInt2;
      } else {
        Object object2;
        Object object1;
        if (this.useToString[i]) {
          object1 = getModelWrapper().getStringValueAt(paramInt1, i);
          object2 = getModelWrapper().getStringValueAt(paramInt2, i);
        } else {
          object1 = getModelWrapper().getValueAt(paramInt1, i);
          object2 = getModelWrapper().getValueAt(paramInt2, i);
        } 
        if (object1 == null) {
          if (object2 == null) {
            j = 0;
          } else {
            j = -1;
          } 
        } else if (object2 == null) {
          j = 1;
        } else {
          j = this.sortComparators[b].compare(object1, object2);
        } 
        if (sortOrder == SortOrder.DESCENDING)
          j *= -1; 
      } 
      if (j != 0)
        return j; 
    } 
    return paramInt1 - paramInt2;
  }
  
  private boolean isTransformed() { return (this.viewToModel != null); }
  
  private void insertInOrder(List<Row> paramList, Row[] paramArrayOfRow) {
    int i = 0;
    int j = paramList.size();
    for (int k = 0; k < j; k++) {
      int m = Arrays.binarySearch(paramArrayOfRow, paramList.get(k));
      if (m < 0)
        m = -1 - m; 
      System.arraycopy(paramArrayOfRow, i, this.viewToModel, i + k, m - i);
      this.viewToModel[m + k] = (Row)paramList.get(k);
      i = m;
    } 
    System.arraycopy(paramArrayOfRow, i, this.viewToModel, i + j, paramArrayOfRow.length - i);
  }
  
  private boolean shouldOptimizeChange(int paramInt1, int paramInt2) {
    if (!isTransformed())
      return false; 
    if (!this.sorted || paramInt2 - paramInt1 > this.viewToModel.length / 10) {
      sort();
      return false;
    } 
    return true;
  }
  
  private void rowsInserted0(int paramInt1, int paramInt2) {
    int[] arrayOfInt = getViewToModelAsInts(this.viewToModel);
    int j = paramInt2 - paramInt1 + 1;
    ArrayList arrayList = new ArrayList(j);
    int i;
    for (i = paramInt1; i <= paramInt2; i++) {
      if (include(i))
        arrayList.add(new Row(this, i)); 
    } 
    for (i = this.modelToView.length - 1; i >= paramInt1; i--) {
      int k = this.modelToView[i];
      if (k != -1)
        (this.viewToModel[k]).modelIndex += j; 
    } 
    if (arrayList.size() > 0) {
      Collections.sort(arrayList);
      Row[] arrayOfRow = this.viewToModel;
      this.viewToModel = new Row[this.viewToModel.length + arrayList.size()];
      insertInOrder(arrayList, arrayOfRow);
    } 
    createModelToView(getModelWrapper().getRowCount());
    setModelToViewFromViewToModel(true);
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void rowsDeleted0(int paramInt1, int paramInt2) {
    int[] arrayOfInt = getViewToModelAsInts(this.viewToModel);
    int i = 0;
    int j;
    for (j = paramInt1; j <= paramInt2; j++) {
      int m = this.modelToView[j];
      if (m != -1) {
        i++;
        this.viewToModel[m] = null;
      } 
    } 
    int k = paramInt2 - paramInt1 + 1;
    for (j = this.modelToView.length - 1; j > paramInt2; j--) {
      int m = this.modelToView[j];
      if (m != -1)
        (this.viewToModel[m]).modelIndex -= k; 
    } 
    if (i > 0) {
      Row[] arrayOfRow = new Row[this.viewToModel.length - i];
      int m = 0;
      int n = 0;
      for (j = 0; j < this.viewToModel.length; j++) {
        if (this.viewToModel[j] == null) {
          System.arraycopy(this.viewToModel, n, arrayOfRow, m, j - n);
          m += j - n;
          n = j + 1;
        } 
      } 
      System.arraycopy(this.viewToModel, n, arrayOfRow, m, this.viewToModel.length - n);
      this.viewToModel = arrayOfRow;
    } 
    createModelToView(getModelWrapper().getRowCount());
    setModelToViewFromViewToModel(true);
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void rowsUpdated0(int paramInt1, int paramInt2) {
    int[] arrayOfInt = getViewToModelAsInts(this.viewToModel);
    int i = paramInt2 - paramInt1 + 1;
    if (getRowFilter() == null) {
      Row[] arrayOfRow1 = new Row[i];
      byte b = 0;
      int j = paramInt1;
      while (j <= paramInt2) {
        arrayOfRow1[b] = this.viewToModel[this.modelToView[j]];
        j++;
        b++;
      } 
      Arrays.sort(arrayOfRow1);
      Row[] arrayOfRow2 = new Row[this.viewToModel.length - i];
      j = 0;
      b = 0;
      while (j < this.viewToModel.length) {
        int k = (this.viewToModel[j]).modelIndex;
        if (k < paramInt1 || k > paramInt2)
          arrayOfRow2[b++] = this.viewToModel[j]; 
        j++;
      } 
      insertInOrder(Arrays.asList(arrayOfRow1), arrayOfRow2);
      setModelToViewFromViewToModel(false);
    } else {
      ArrayList arrayList = new ArrayList(i);
      int k = 0;
      int m = 0;
      int n = 0;
      int j;
      for (j = paramInt1; j <= paramInt2; j++) {
        if (this.modelToView[j] == -1) {
          if (include(j)) {
            arrayList.add(new Row(this, j));
            k++;
          } 
        } else {
          if (!include(j)) {
            m++;
          } else {
            arrayList.add(this.viewToModel[this.modelToView[j]]);
          } 
          this.modelToView[j] = -2;
          n++;
        } 
      } 
      Collections.sort(arrayList);
      Row[] arrayOfRow = new Row[this.viewToModel.length - n];
      j = 0;
      byte b = 0;
      while (j < this.viewToModel.length) {
        int i1 = (this.viewToModel[j]).modelIndex;
        if (this.modelToView[i1] != -2)
          arrayOfRow[b++] = this.viewToModel[j]; 
        j++;
      } 
      if (k != m)
        this.viewToModel = new Row[this.viewToModel.length + k - m]; 
      insertInOrder(arrayList, arrayOfRow);
      setModelToViewFromViewToModel(true);
    } 
    fireRowSorterChanged(arrayOfInt);
  }
  
  private void checkColumn(int paramInt) {
    if (paramInt < 0 || paramInt >= getModelWrapper().getColumnCount())
      throw new IndexOutOfBoundsException("column beyond range of TableModel"); 
  }
  
  private class FilterEntry extends RowFilter.Entry<M, I> {
    int modelIndex;
    
    private FilterEntry() {}
    
    public M getModel() { return (M)DefaultRowSorter.this.getModelWrapper().getModel(); }
    
    public int getValueCount() { return DefaultRowSorter.this.getModelWrapper().getColumnCount(); }
    
    public Object getValue(int param1Int) { return DefaultRowSorter.this.getModelWrapper().getValueAt(this.modelIndex, param1Int); }
    
    public String getStringValue(int param1Int) { return DefaultRowSorter.this.getModelWrapper().getStringValueAt(this.modelIndex, param1Int); }
    
    public I getIdentifier() { return (I)DefaultRowSorter.this.getModelWrapper().getIdentifier(this.modelIndex); }
  }
  
  protected static abstract class ModelWrapper<M, I> extends Object {
    public abstract M getModel();
    
    public abstract int getColumnCount();
    
    public abstract int getRowCount();
    
    public abstract Object getValueAt(int param1Int1, int param1Int2);
    
    public String getStringValueAt(int param1Int1, int param1Int2) {
      Object object = getValueAt(param1Int1, param1Int2);
      if (object == null)
        return ""; 
      String str = object.toString();
      return (str == null) ? "" : str;
    }
    
    public abstract I getIdentifier(int param1Int);
  }
  
  private static class Row extends Object implements Comparable<Row> {
    private DefaultRowSorter sorter;
    
    int modelIndex;
    
    public Row(DefaultRowSorter param1DefaultRowSorter, int param1Int) {
      this.sorter = param1DefaultRowSorter;
      this.modelIndex = param1Int;
    }
    
    public int compareTo(Row param1Row) { return this.sorter.compare(this.modelIndex, param1Row.modelIndex); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultRowSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */