package javax.management;

class QualifiedAttributeValueExp extends AttributeValueExp {
  private static final long serialVersionUID = 8832517277410933254L;
  
  private String className;
  
  @Deprecated
  public QualifiedAttributeValueExp() {}
  
  public QualifiedAttributeValueExp(String paramString1, String paramString2) {
    super(paramString2);
    this.className = paramString1;
  }
  
  public String getAttrClassName() { return this.className; }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    try {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      String str = mBeanServer.getObjectInstance(paramObjectName).getClassName();
      if (str.equals(this.className))
        return super.apply(paramObjectName); 
      throw new InvalidApplicationException("Class name is " + str + ", should be " + this.className);
    } catch (Exception exception) {
      throw new InvalidApplicationException("Qualified attribute: " + exception);
    } 
  }
  
  public String toString() { return (this.className != null) ? (this.className + "." + super.toString()) : super.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\QualifiedAttributeValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */