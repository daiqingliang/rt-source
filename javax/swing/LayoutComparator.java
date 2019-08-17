package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

final class LayoutComparator extends Object implements Comparator<Component>, Serializable {
  private static final int ROW_TOLERANCE = 10;
  
  private boolean horizontal = true;
  
  private boolean leftToRight = true;
  
  void setComponentOrientation(ComponentOrientation paramComponentOrientation) {
    this.horizontal = paramComponentOrientation.isHorizontal();
    this.leftToRight = paramComponentOrientation.isLeftToRight();
  }
  
  public int compare(Component paramComponent1, Component paramComponent2) {
    if (paramComponent1 == paramComponent2)
      return 0; 
    if (paramComponent1.getParent() != paramComponent2.getParent()) {
      LinkedList linkedList1 = new LinkedList();
      while (paramComponent1 != null) {
        linkedList1.add(paramComponent1);
        if (paramComponent1 instanceof java.awt.Window)
          break; 
        paramComponent1 = paramComponent1.getParent();
      } 
      if (paramComponent1 == null)
        throw new ClassCastException(); 
      LinkedList linkedList2 = new LinkedList();
      while (paramComponent2 != null) {
        linkedList2.add(paramComponent2);
        if (paramComponent2 instanceof java.awt.Window)
          break; 
        paramComponent2 = paramComponent2.getParent();
      } 
      if (paramComponent2 == null)
        throw new ClassCastException(); 
      ListIterator listIterator1 = linkedList1.listIterator(linkedList1.size());
      ListIterator listIterator2 = linkedList2.listIterator(linkedList2.size());
      do {
        if (listIterator1.hasPrevious()) {
          paramComponent1 = (Component)listIterator1.previous();
        } else {
          return -1;
        } 
        if (listIterator2.hasPrevious()) {
          paramComponent2 = (Component)listIterator2.previous();
        } else {
          return 1;
        } 
      } while (paramComponent1 == paramComponent2);
    } 
    int i = paramComponent1.getX();
    int j = paramComponent1.getY();
    int k = paramComponent2.getX();
    int m = paramComponent2.getY();
    int n = paramComponent1.getParent().getComponentZOrder(paramComponent1) - paramComponent2.getParent().getComponentZOrder(paramComponent2);
    return this.horizontal ? (this.leftToRight ? ((Math.abs(j - m) < 10) ? ((i < k) ? -1 : ((i > k) ? 1 : n)) : ((j < m) ? -1 : 1)) : ((Math.abs(j - m) < 10) ? ((i > k) ? -1 : ((i < k) ? 1 : n)) : ((j < m) ? -1 : 1))) : (this.leftToRight ? ((Math.abs(i - k) < 10) ? ((j < m) ? -1 : ((j > m) ? 1 : n)) : ((i < k) ? -1 : 1)) : ((Math.abs(i - k) < 10) ? ((j < m) ? -1 : ((j > m) ? 1 : n)) : ((i > k) ? -1 : 1)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LayoutComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */