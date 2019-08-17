package javax.swing.text;

public class NavigationFilter {
  public void setDot(FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias) { paramFilterBypass.setDot(paramInt, paramBias); }
  
  public void moveDot(FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias) { paramFilterBypass.moveDot(paramInt, paramBias); }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException { return paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias); }
  
  public static abstract class FilterBypass {
    public abstract Caret getCaret();
    
    public abstract void setDot(int param1Int, Position.Bias param1Bias);
    
    public abstract void moveDot(int param1Int, Position.Bias param1Bias);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\NavigationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */