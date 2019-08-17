package javax.management;

class BinaryRelQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = -5690656271650491000L;
  
  private int relOp;
  
  private ValueExp exp1;
  
  private ValueExp exp2;
  
  public BinaryRelQueryExp() {}
  
  public BinaryRelQueryExp(int paramInt, ValueExp paramValueExp1, ValueExp paramValueExp2) {
    this.relOp = paramInt;
    this.exp1 = paramValueExp1;
    this.exp2 = paramValueExp2;
  }
  
  public int getOperator() { return this.relOp; }
  
  public ValueExp getLeftValue() { return this.exp1; }
  
  public ValueExp getRightValue() { return this.exp2; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    ValueExp valueExp1 = this.exp1.apply(paramObjectName);
    ValueExp valueExp2 = this.exp2.apply(paramObjectName);
    boolean bool1 = valueExp1 instanceof NumericValueExp;
    boolean bool2 = valueExp1 instanceof BooleanValueExp;
    if (bool1) {
      if (((NumericValueExp)valueExp1).isLong()) {
        long l1 = ((NumericValueExp)valueExp1).longValue();
        long l2 = ((NumericValueExp)valueExp2).longValue();
        switch (this.relOp) {
          case 0:
            return (l1 > l2);
          case 1:
            return (l1 < l2);
          case 2:
            return (l1 >= l2);
          case 3:
            return (l1 <= l2);
          case 4:
            return (l1 == l2);
        } 
      } else {
        double d1 = ((NumericValueExp)valueExp1).doubleValue();
        double d2 = ((NumericValueExp)valueExp2).doubleValue();
        switch (this.relOp) {
          case 0:
            return (d1 > d2);
          case 1:
            return (d1 < d2);
          case 2:
            return (d1 >= d2);
          case 3:
            return (d1 <= d2);
          case 4:
            return (d1 == d2);
        } 
      } 
    } else if (bool2) {
      boolean bool3 = ((BooleanValueExp)valueExp1).getValue().booleanValue();
      boolean bool4 = ((BooleanValueExp)valueExp2).getValue().booleanValue();
      switch (this.relOp) {
        case 0:
          return (bool3 && !bool4);
        case 1:
          return (!bool3 && bool4);
        case 2:
          return (bool3 || !bool4);
        case 3:
          return (!bool3 || bool4);
        case 4:
          return (bool3 == bool4);
      } 
    } else {
      String str1 = ((StringValueExp)valueExp1).getValue();
      String str2 = ((StringValueExp)valueExp2).getValue();
      switch (this.relOp) {
        case 0:
          return (str1.compareTo(str2) > 0);
        case 1:
          return (str1.compareTo(str2) < 0);
        case 2:
          return (str1.compareTo(str2) >= 0);
        case 3:
          return (str1.compareTo(str2) <= 0);
        case 4:
          return (str1.compareTo(str2) == 0);
      } 
    } 
    return false;
  }
  
  public String toString() { return "(" + this.exp1 + ") " + relOpString() + " (" + this.exp2 + ")"; }
  
  private String relOpString() {
    switch (this.relOp) {
      case 0:
        return ">";
      case 1:
        return "<";
      case 2:
        return ">=";
      case 3:
        return "<=";
      case 4:
        return "=";
    } 
    return "=";
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BinaryRelQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */