package java.security;

import java.io.Serializable;

public interface Key extends Serializable {
  public static final long serialVersionUID = 6603384152749567654L;
  
  String getAlgorithm();
  
  String getFormat();
  
  byte[] getEncoded();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */