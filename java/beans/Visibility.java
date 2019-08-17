package java.beans;

public interface Visibility {
  boolean needsGui();
  
  void dontUseGui();
  
  void okToUseGui();
  
  boolean avoidingGui();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\Visibility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */