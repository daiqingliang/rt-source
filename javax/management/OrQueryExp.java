package javax.management;

class OrQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = 2962973084421716523L;
  
  private QueryExp exp1;
  
  private QueryExp exp2;
  
  public OrQueryExp() {}
  
  public OrQueryExp(QueryExp paramQueryExp1, QueryExp paramQueryExp2) {
    this.exp1 = paramQueryExp1;
    this.exp2 = paramQueryExp2;
  }
  
  public QueryExp getLeftExp() { return this.exp1; }
  
  public QueryExp getRightExp() { return this.exp2; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException { return (this.exp1.apply(paramObjectName) || this.exp2.apply(paramObjectName)); }
  
  public String toString() { return "(" + this.exp1 + ") or (" + this.exp2 + ")"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\OrQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */