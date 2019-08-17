package javax.management;

class MatchQueryExp extends QueryEval implements QueryExp {
  private static final long serialVersionUID = -7156603696948215014L;
  
  private AttributeValueExp exp;
  
  private String pattern;
  
  public MatchQueryExp() {}
  
  public MatchQueryExp(AttributeValueExp paramAttributeValueExp, StringValueExp paramStringValueExp) {
    this.exp = paramAttributeValueExp;
    this.pattern = paramStringValueExp.getValue();
  }
  
  public AttributeValueExp getAttribute() { return this.exp; }
  
  public String getPattern() { return this.pattern; }
  
  public boolean apply(ObjectName paramObjectName) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
    ValueExp valueExp = this.exp.apply(paramObjectName);
    return !(valueExp instanceof StringValueExp) ? false : wildmatch(((StringValueExp)valueExp).getValue(), this.pattern);
  }
  
  public String toString() { return this.exp + " like " + new StringValueExp(this.pattern); }
  
  private static boolean wildmatch(String paramString1, String paramString2) {
    byte b1 = 0;
    byte b2 = 0;
    int i = paramString1.length();
    int j = paramString2.length();
    while (b2 < j) {
      char c = paramString2.charAt(b2++);
      if (c == '?') {
        if (++b1 > i)
          return false; 
        continue;
      } 
      if (c == '[') {
        if (b1 >= i)
          return false; 
        boolean bool1 = true;
        boolean bool2 = false;
        if (paramString2.charAt(b2) == '!') {
          bool1 = false;
          b2++;
        } 
        while ((c = paramString2.charAt(b2)) != ']' && ++b2 < j) {
          if (paramString2.charAt(b2) == '-' && b2 + 1 < j && paramString2.charAt(b2 + 1) != ']') {
            if (paramString1.charAt(b1) >= paramString2.charAt(b2 - 1) && paramString1.charAt(b1) <= paramString2.charAt(b2 + 1))
              bool2 = true; 
            b2++;
            continue;
          } 
          if (c == paramString1.charAt(b1))
            bool2 = true; 
        } 
        if (b2 >= j || bool1 != bool2)
          return false; 
        b2++;
        b1++;
        continue;
      } 
      if (c == '*') {
        if (b2 >= j)
          return true; 
        do {
          if (wildmatch(paramString1.substring(b1), paramString2.substring(b2)))
            return true; 
        } while (++b1 < i);
        return false;
      } 
      if (c == '\\') {
        if (b2 >= j || b1 >= i || paramString2.charAt(b2++) != paramString1.charAt(b1++))
          return false; 
        continue;
      } 
      if (b1 >= i || c != paramString1.charAt(b1++))
        return false; 
    } 
    return (b1 == i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MatchQueryExp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */