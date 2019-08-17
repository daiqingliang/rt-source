package javax.naming.directory;

import java.io.Serializable;

public class ModificationItem implements Serializable {
  private int mod_op;
  
  private Attribute attr;
  
  private static final long serialVersionUID = 7573258562534746850L;
  
  public ModificationItem(int paramInt, Attribute paramAttribute) {
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
        if (paramAttribute == null)
          throw new IllegalArgumentException("Must specify non-null attribute for modification"); 
        this.mod_op = paramInt;
        this.attr = paramAttribute;
        return;
    } 
    throw new IllegalArgumentException("Invalid modification code " + paramInt);
  }
  
  public int getModificationOp() { return this.mod_op; }
  
  public Attribute getAttribute() { return this.attr; }
  
  public String toString() {
    switch (this.mod_op) {
      case 1:
        return "Add attribute: " + this.attr.toString();
      case 2:
        return "Replace attribute: " + this.attr.toString();
      case 3:
        return "Remove attribute: " + this.attr.toString();
    } 
    return "";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\ModificationItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */