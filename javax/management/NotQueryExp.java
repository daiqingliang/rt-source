package javax.management;

class NotQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = 5269643775896723397L;
  
  private QueryExp exp;
  
  public NotQueryExp() {}
  
  public NotQueryExp(QueryExp paramQueryExp) { this.exp = paramQueryExp; }
  
  public QueryExp getNegatedExp() { return this.exp; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException { return !this.exp.apply(paramObjectName); }
  
  public String toString() { return "not (" + this.exp + ")"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\NotQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */