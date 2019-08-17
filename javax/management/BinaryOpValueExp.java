package javax.management;

class BinaryOpValueExp extends QueryEval implements ValueExp {
  private static final long serialVersionUID = 1216286847881456786L;
  
  private int op;
  
  private ValueExp exp1;
  
  private ValueExp exp2;
  
  public BinaryOpValueExp() {}
  
  public BinaryOpValueExp(int paramInt, ValueExp paramValueExp1, ValueExp paramValueExp2) {
    this.op = paramInt;
    this.exp1 = paramValueExp1;
    this.exp2 = paramValueExp2;
  }
  
  public int getOperator() { return this.op; }
  
  public ValueExp getLeftValue() { return this.exp1; }
  
  public ValueExp getRightValue() { return this.exp2; }
  
  public ValueExp apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    ValueExp valueExp1 = this.exp1.apply(paramObjectName);
    ValueExp valueExp2 = this.exp2.apply(paramObjectName);
    boolean bool = valueExp1 instanceof NumericValueExp;
    if (bool) {
      if (((NumericValueExp)valueExp1).isLong()) {
        long l1 = ((NumericValueExp)valueExp1).longValue();
        long l2 = ((NumericValueExp)valueExp2).longValue();
        switch (this.op) {
          case 0:
            return Query.value(l1 + l2);
          case 2:
            return Query.value(l1 * l2);
          case 1:
            return Query.value(l1 - l2);
          case 3:
            return Query.value(l1 / l2);
        } 
      } else {
        double d1 = ((NumericValueExp)valueExp1).doubleValue();
        double d2 = ((NumericValueExp)valueExp2).doubleValue();
        switch (this.op) {
          case 0:
            return Query.value(d1 + d2);
          case 2:
            return Query.value(d1 * d2);
          case 1:
            return Query.value(d1 - d2);
          case 3:
            return Query.value(d1 / d2);
        } 
      } 
    } else {
      String str1 = ((StringValueExp)valueExp1).getValue();
      String str2 = ((StringValueExp)valueExp2).getValue();
      switch (this.op) {
        case 0:
          return new StringValueExp(str1 + str2);
      } 
      throw new BadStringOperationException(opString());
    } 
    throw new BadBinaryOpValueExpException(this);
  }
  
  public String toString() {
    try {
      return parens(this.exp1, true) + " " + opString() + " " + parens(this.exp2, false);
    } catch (BadBinaryOpValueExpException badBinaryOpValueExpException) {
      return "invalid expression";
    } 
  }
  
  private String parens(ValueExp paramValueExp, boolean paramBoolean) throws BadBinaryOpValueExpException {
    boolean bool;
    if (paramValueExp instanceof BinaryOpValueExp) {
      int i = ((BinaryOpValueExp)paramValueExp).op;
      if (paramBoolean) {
        bool = (precedence(i) >= precedence(this.op)) ? 1 : 0;
      } else {
        bool = (precedence(i) > precedence(this.op)) ? 1 : 0;
      } 
    } else {
      bool = true;
    } 
    return bool ? paramValueExp.toString() : ("(" + paramValueExp + ")");
  }
  
  private int precedence(int paramInt) throws BadBinaryOpValueExpException {
    switch (paramInt) {
      case 0:
      case 1:
        return 0;
      case 2:
      case 3:
        return 1;
    } 
    throw new BadBinaryOpValueExpException(this);
  }
  
  private String opString() {
    switch (this.op) {
      case 0:
        return "+";
      case 2:
        return "*";
      case 1:
        return "-";
      case 3:
        return "/";
    } 
    throw new BadBinaryOpValueExpException(this);
  }
  
  @Deprecated
  public void setMBeanServer(MBeanServer paramMBeanServer) { super.setMBeanServer(paramMBeanServer); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BinaryOpValueExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */