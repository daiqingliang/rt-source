package sun.awt.shell;

import java.util.Comparator;
import javax.swing.SortOrder;

public class ShellFolderColumnInfo {
  private String title;
  
  private Integer width;
  
  private boolean visible;
  
  private Integer alignment;
  
  private SortOrder sortOrder;
  
  private Comparator comparator;
  
  private boolean compareByColumn;
  
  public ShellFolderColumnInfo(String paramString, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean1, SortOrder paramSortOrder, Comparator paramComparator, boolean paramBoolean2) {
    this.title = paramString;
    this.width = paramInteger1;
    this.alignment = paramInteger2;
    this.visible = paramBoolean1;
    this.sortOrder = paramSortOrder;
    this.comparator = paramComparator;
    this.compareByColumn = paramBoolean2;
  }
  
  public ShellFolderColumnInfo(String paramString, Integer paramInteger1, Integer paramInteger2, boolean paramBoolean, SortOrder paramSortOrder, Comparator paramComparator) { this(paramString, paramInteger1, paramInteger2, paramBoolean, paramSortOrder, paramComparator, false); }
  
  public ShellFolderColumnInfo(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) { this(paramString, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramBoolean, null, null); }
  
  public String getTitle() { return this.title; }
  
  public void setTitle(String paramString) { this.title = paramString; }
  
  public Integer getWidth() { return this.width; }
  
  public void setWidth(Integer paramInteger) { this.width = paramInteger; }
  
  public Integer getAlignment() { return this.alignment; }
  
  public void setAlignment(Integer paramInteger) { this.alignment = paramInteger; }
  
  public boolean isVisible() { return this.visible; }
  
  public void setVisible(boolean paramBoolean) { this.visible = paramBoolean; }
  
  public SortOrder getSortOrder() { return this.sortOrder; }
  
  public void setSortOrder(SortOrder paramSortOrder) { this.sortOrder = paramSortOrder; }
  
  public Comparator getComparator() { return this.comparator; }
  
  public void setComparator(Comparator paramComparator) { this.comparator = paramComparator; }
  
  public boolean isCompareByColumn() { return this.compareByColumn; }
  
  public void setCompareByColumn(boolean paramBoolean) { this.compareByColumn = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\shell\ShellFolderColumnInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */