package javax.management;

public class AttributeValueExp implements ValueExp {
  private static final long serialVersionUID = -7768025046539163385L;
  
  private String attr;
  
  @Deprecated
  public AttributeValueExp() {}
  
  public AttributeValueExp(String paramString) { this.attr = paramString; }
  
  public String getAttributeName() { return this.attr; }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    Object object = getAttribute(paramObjectName);
    if (object instanceof Number)
      return new NumericValueExp((Number)object); 
    if (object instanceof String)
      return new StringValueExp((String)object); 
    if (object instanceof Boolean)
      return new BooleanValueExp((Boolean)object); 
    throw new BadAttributeValueExpException(object);
  }
  
  public String toString() { return this.attr; }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) {}
  
  protected Object getAttribute(ObjectName paramObjectName) {
    try {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      return mBeanServer.getAttribute(paramObjectName, this.attr);
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\AttributeValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */