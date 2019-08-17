package java.awt.datatransfer;

public class UnsupportedFlavorException extends Exception {
  private static final long serialVersionUID = 5383814944251665601L;
  
  public UnsupportedFlavorException(DataFlavor paramDataFlavor) { super((paramDataFlavor != null) ? paramDataFlavor.getHumanPresentableName() : null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\UnsupportedFlavorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */