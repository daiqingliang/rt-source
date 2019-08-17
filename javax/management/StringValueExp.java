package javax.management;

public class StringValueExp implements ValueExp {
  private static final long serialVersionUID = -3256390509806284044L;
  
  private String val;
  
  public StringValueExp() {}
  
  public StringValueExp(String paramString) { this.val = paramString; }
  
  public String getValue() { return this.val; }
  
  public String toString() { return "'" + this.val.replace("'", "''") + "'"; }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\StringValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */