package javax.accessibility;

public interface AccessibleHypertext extends AccessibleText {
  int getLinkCount();
  
  AccessibleHyperlink getLink(int paramInt);
  
  int getLinkIndex(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleHypertext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */