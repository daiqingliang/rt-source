package javax.swing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SpinnerListModel extends AbstractSpinnerModel implements Serializable {
  private List list;
  
  private int index;
  
  public SpinnerListModel(List<?> paramList) {
    if (paramList == null || paramList.size() == 0)
      throw new IllegalArgumentException("SpinnerListModel(List) expects non-null non-empty List"); 
    this.list = paramList;
    this.index = 0;
  }
  
  public SpinnerListModel(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      throw new IllegalArgumentException("SpinnerListModel(Object[]) expects non-null non-empty Object[]"); 
    this.list = Arrays.asList(paramArrayOfObject);
    this.index = 0;
  }
  
  public SpinnerListModel() { this(new Object[] { "empty" }); }
  
  public List<?> getList() { return this.list; }
  
  public void setList(List<?> paramList) {
    if (paramList == null || paramList.size() == 0)
      throw new IllegalArgumentException("invalid list"); 
    if (!paramList.equals(this.list)) {
      this.list = paramList;
      this.index = 0;
      fireStateChanged();
    } 
  }
  
  public Object getValue() { return this.list.get(this.index); }
  
  public void setValue(Object paramObject) {
    int i = this.list.indexOf(paramObject);
    if (i == -1)
      throw new IllegalArgumentException("invalid sequence element"); 
    if (i != this.index) {
      this.index = i;
      fireStateChanged();
    } 
  }
  
  public Object getNextValue() { return (this.index >= this.list.size() - 1) ? null : this.list.get(this.index + 1); }
  
  public Object getPreviousValue() { return (this.index <= 0) ? null : this.list.get(this.index - 1); }
  
  Object findNextMatch(String paramString) {
    int i = this.list.size();
    if (i == 0)
      return null; 
    int j = this.index;
    do {
      Object object = this.list.get(j);
      String str = object.toString();
      if (str != null && str.startsWith(paramString))
        return object; 
      j = (j + 1) % i;
    } while (j != this.index);
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\SpinnerListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */