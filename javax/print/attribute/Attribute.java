package javax.print.attribute;

import java.io.Serializable;

public interface Attribute extends Serializable {
  Class<? extends Attribute> getCategory();
  
  String getName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */