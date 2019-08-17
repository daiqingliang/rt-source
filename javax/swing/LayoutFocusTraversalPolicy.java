package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import sun.awt.SunToolkit;

public class LayoutFocusTraversalPolicy extends SortingFocusTraversalPolicy implements Serializable {
  private static final SwingDefaultFocusTraversalPolicy fitnessTestPolicy = new SwingDefaultFocusTraversalPolicy();
  
  public LayoutFocusTraversalPolicy() { super(new LayoutComparator()); }
  
  LayoutFocusTraversalPolicy(Comparator<? super Component> paramComparator) { super(paramComparator); }
  
  public Component getComponentAfter(Container paramContainer, Component paramComponent) {
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    Comparator comparator = getComparator();
    if (comparator instanceof LayoutComparator)
      ((LayoutComparator)comparator).setComponentOrientation(paramContainer.getComponentOrientation()); 
    return super.getComponentAfter(paramContainer, paramComponent);
  }
  
  public Component getComponentBefore(Container paramContainer, Component paramComponent) {
    if (paramContainer == null || paramComponent == null)
      throw new IllegalArgumentException("aContainer and aComponent cannot be null"); 
    Comparator comparator = getComparator();
    if (comparator instanceof LayoutComparator)
      ((LayoutComparator)comparator).setComponentOrientation(paramContainer.getComponentOrientation()); 
    return super.getComponentBefore(paramContainer, paramComponent);
  }
  
  public Component getFirstComponent(Container paramContainer) {
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    Comparator comparator = getComparator();
    if (comparator instanceof LayoutComparator)
      ((LayoutComparator)comparator).setComponentOrientation(paramContainer.getComponentOrientation()); 
    return super.getFirstComponent(paramContainer);
  }
  
  public Component getLastComponent(Container paramContainer) {
    if (paramContainer == null)
      throw new IllegalArgumentException("aContainer cannot be null"); 
    Comparator comparator = getComparator();
    if (comparator instanceof LayoutComparator)
      ((LayoutComparator)comparator).setComponentOrientation(paramContainer.getComponentOrientation()); 
    return super.getLastComponent(paramContainer);
  }
  
  protected boolean accept(Component paramComponent) {
    if (!super.accept(paramComponent))
      return false; 
    if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.JTable"))
      return true; 
    if (SunToolkit.isInstanceOf(paramComponent, "javax.swing.JComboBox")) {
      JComboBox jComboBox = (JComboBox)paramComponent;
      return jComboBox.getUI().isFocusTraversable(jComboBox);
    } 
    if (paramComponent instanceof JComponent) {
      JComponent jComponent = (JComponent)paramComponent;
      InputMap inputMap;
      for (inputMap = jComponent.getInputMap(0, false); inputMap != null && inputMap.size() == 0; inputMap = inputMap.getParent());
      if (inputMap != null)
        return true; 
    } 
    return fitnessTestPolicy.accept(paramComponent);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getComparator());
    paramObjectOutputStream.writeBoolean(getImplicitDownCycleTraversal());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    setComparator((Comparator)paramObjectInputStream.readObject());
    setImplicitDownCycleTraversal(paramObjectInputStream.readBoolean());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\LayoutFocusTraversalPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */