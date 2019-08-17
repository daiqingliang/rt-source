package javax.management;

class InQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = -5801329450358952434L;
  
  private ValueExp val;
  
  private ValueExp[] valueList;
  
  public InQueryExp() {}
  
  public InQueryExp(ValueExp paramValueExp, ValueExp[] paramArrayOfValueExp) {
    this.val = paramValueExp;
    this.valueList = paramArrayOfValueExp;
  }
  
  public ValueExp getCheckedValue() { return this.val; }
  
  public ValueExp[] getExplicitValues() { return this.valueList; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    if (this.valueList != null) {
      ValueExp valueExp = this.val.apply(paramObjectName);
      boolean bool = valueExp instanceof NumericValueExp;
      for (ValueExp valueExp1 : this.valueList) {
        valueExp1 = valueExp1.apply(paramObjectName);
        if (bool) {
          if (((NumericValueExp)valueExp1).doubleValue() == ((NumericValueExp)valueExp).doubleValue())
            return true; 
        } else if (((StringValueExp)valueExp1).getValue().equals(((StringValueExp)valueExp).getValue())) {
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public String toString() { return this.val + " in (" + generateValueList() + ")"; }
  
  private String generateValueList() {
    if (this.valueList == null || this.valueList.length == 0)
      return ""; 
    StringBuilder stringBuilder = new StringBuilder(this.valueList[0].toString());
    for (byte b = 1; b < this.valueList.length; b++) {
      stringBuilder.append(", ");
      stringBuilder.append(this.valueList[b]);
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\InQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */