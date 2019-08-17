package javax.swing.text;

import javax.swing.event.ChangeListener;

public interface Style extends MutableAttributeSet {
  String getName();
  
  void addChangeListener(ChangeListener paramChangeListener);
  
  void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Style.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */