package javax.management;

class BetweenQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = -2933597532866307444L;
  
  private ValueExp exp1;
  
  private ValueExp exp2;
  
  private ValueExp exp3;
  
  public BetweenQueryExp() {}
  
  public BetweenQueryExp(ValueExp paramValueExp1, ValueExp paramValueExp2, ValueExp paramValueExp3) {
    this.exp1 = paramValueExp1;
    this.exp2 = paramValueExp2;
    this.exp3 = paramValueExp3;
  }
  
  public ValueExp getCheckedValue() { return this.exp1; }
  
  public ValueExp getLowerBound() { return this.exp2; }
  
  public ValueExp getUpperBound() { return this.exp3; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    ValueExp valueExp1 = this.exp1.apply(paramObjectName);
    ValueExp valueExp2 = this.exp2.apply(paramObjectName);
    ValueExp valueExp3 = this.exp3.apply(paramObjectName);
    boolean bool = valueExp1 instanceof NumericValueExp;
    if (bool) {
      if (((NumericValueExp)valueExp1).isLong()) {
        long l1 = ((NumericValueExp)valueExp1).longValue();
        long l2 = ((NumericValueExp)valueExp2).longValue();
        long l3 = ((NumericValueExp)valueExp3).longValue();
        return (l2 <= l1 && l1 <= l3);
      } 
      double d1 = ((NumericValueExp)valueExp1).doubleValue();
      double d2 = ((NumericValueExp)valueExp2).doubleValue();
      double d3 = ((NumericValueExp)valueExp3).doubleValue();
      return (d2 <= d1 && d1 <= d3);
    } 
    String str1 = ((StringValueExp)valueExp1).getValue();
    String str2 = ((StringValueExp)valueExp2).getValue();
    String str3 = ((StringValueExp)valueExp3).getValue();
    return (str2.compareTo(str1) <= 0 && str1.compareTo(str3) <= 0);
  }
  
  public String toString() { return "(" + this.exp1 + ") between (" + this.exp2 + ") and (" + this.exp3 + ")"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BetweenQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */