package javax.swing;

public abstract class InputVerifier {
  public abstract boolean verify(JComponent paramJComponent);
  
  public boolean shouldYieldFocus(JComponent paramJComponent) { return verify(paramJComponent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\InputVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */