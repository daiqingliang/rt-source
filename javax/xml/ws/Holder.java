package javax.xml.ws;

import java.io.Serializable;

public final class Holder<T> extends Object implements Serializable {
  private static final long serialVersionUID = 2623699057546497185L;
  
  public T value;
  
  public Holder() {}
  
  public Holder(T paramT) { this.value = paramT; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\Holder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */