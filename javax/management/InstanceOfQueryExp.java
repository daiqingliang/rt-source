package javax.management;

class InstanceOfQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = -1081892073854801359L;
  
  private StringValueExp classNameValue;
  
  public InstanceOfQueryExp(StringValueExp paramStringValueExp) {
    if (paramStringValueExp == null)
      throw new IllegalArgumentException("Null class name."); 
    this.classNameValue = paramStringValueExp;
  }
  
  public StringValueExp getClassNameValue() { return this.classNameValue; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    StringValueExp stringValueExp;
    try {
      stringValueExp = (StringValueExp)this.classNameValue.apply(paramObjectName);
    } catch (ClassCastException classCastException) {
      BadStringOperationException badStringOperationException = new BadStringOperationException(classCastException.toString());
      badStringOperationException.initCause(classCastException);
      throw badStringOperationException;
    } 
    try {
      return getMBeanServer().isInstanceOf(paramObjectName, stringValueExp.getValue());
    } catch (InstanceNotFoundException instanceNotFoundException) {
      return false;
    } 
  }
  
  public String toString() { return "InstanceOf " + this.classNameValue.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\InstanceOfQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */