package javax.xml.bind;

import java.io.Serializable;
import javax.xml.namespace.QName;

public class JAXBElement<T> extends Object implements Serializable {
  protected final QName name;
  
  protected final Class<T> declaredType;
  
  protected final Class scope;
  
  protected T value;
  
  protected boolean nil = false;
  
  private static final long serialVersionUID = 1L;
  
  public JAXBElement(QName paramQName, Class<T> paramClass1, Class paramClass2, T paramT) {
    if (paramClass1 == null || paramQName == null)
      throw new IllegalArgumentException(); 
    this.declaredType = paramClass1;
    if (paramClass2 == null)
      paramClass2 = GlobalScope.class; 
    this.scope = paramClass2;
    this.name = paramQName;
    setValue(paramT);
  }
  
  public JAXBElement(QName paramQName, Class<T> paramClass, T paramT) { this(paramQName, paramClass, GlobalScope.class, paramT); }
  
  public Class<T> getDeclaredType() { return this.declaredType; }
  
  public QName getName() { return this.name; }
  
  public void setValue(T paramT) { this.value = paramT; }
  
  public T getValue() { return (T)this.value; }
  
  public Class getScope() { return this.scope; }
  
  public boolean isNil() { return (this.value == null || this.nil); }
  
  public void setNil(boolean paramBoolean) { this.nil = paramBoolean; }
  
  public boolean isGlobalScope() { return (this.scope == GlobalScope.class); }
  
  public boolean isTypeSubstituted() { return (this.value == null) ? false : ((this.value.getClass() != this.declaredType)); }
  
  public static final class GlobalScope {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\JAXBElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */