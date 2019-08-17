package javax.management;

class BooleanValueExp extends QueryEval implements ValueExp {
  private static final long serialVersionUID = 7754922052666594581L;
  
  private boolean val = false;
  
  BooleanValueExp(boolean paramBoolean) { this.val = paramBoolean; }
  
  BooleanValueExp(Boolean paramBoolean) { this.val = paramBoolean.booleanValue(); }
  
  public Boolean getValue() { return Boolean.valueOf(this.val); }
  
  public String toString() { return String.valueOf(this.val); }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException { return this; }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) { super.setMBeanServer(paramMBeanServer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BooleanValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */