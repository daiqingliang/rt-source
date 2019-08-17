package com.sun.org.apache.xalan.internal.utils;

public abstract class FeaturePropertyBase {
  String[] values = null;
  
  State[] states = { State.DEFAULT, State.DEFAULT };
  
  public void setValue(Enum paramEnum, State paramState, String paramString) {
    if (paramState.compareTo(this.states[paramEnum.ordinal()]) >= 0) {
      this.values[paramEnum.ordinal()] = paramString;
      this.states[paramEnum.ordinal()] = paramState;
    } 
  }
  
  public void setValue(int paramInt, State paramState, String paramString) {
    if (paramState.compareTo(this.states[paramInt]) >= 0) {
      this.values[paramInt] = paramString;
      this.states[paramInt] = paramState;
    } 
  }
  
  public boolean setValue(String paramString, State paramState, Object paramObject) {
    int i = getIndex(paramString);
    if (i > -1) {
      setValue(i, paramState, (String)paramObject);
      return true;
    } 
    return false;
  }
  
  public boolean setValue(String paramString, State paramState, boolean paramBoolean) {
    int i = getIndex(paramString);
    if (i > -1) {
      if (paramBoolean) {
        setValue(i, paramState, "true");
      } else {
        setValue(i, paramState, "false");
      } 
      return true;
    } 
    return false;
  }
  
  public String getValue(Enum paramEnum) { return this.values[paramEnum.ordinal()]; }
  
  public String getValue(String paramString) {
    int i = getIndex(paramString);
    return (i > -1) ? getValueByIndex(i) : null;
  }
  
  public String getValueAsString(String paramString) {
    int i = getIndex(paramString);
    return (i > -1) ? getValueByIndex(i) : null;
  }
  
  public String getValueByIndex(int paramInt) { return this.values[paramInt]; }
  
  public abstract int getIndex(String paramString);
  
  public <E extends Enum<E>> int getIndex(Class<E> paramClass, String paramString) {
    for (Enum enum : (Enum[])paramClass.getEnumConstants()) {
      if (enum.toString().equals(paramString))
        return enum.ordinal(); 
    } 
    return -1;
  }
  
  void getSystemProperty(Enum paramEnum, String paramString) {
    try {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null) {
        this.values[paramEnum.ordinal()] = str;
        this.states[paramEnum.ordinal()] = State.SYSTEMPROPERTY;
        return;
      } 
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null) {
        this.values[paramEnum.ordinal()] = str;
        this.states[paramEnum.ordinal()] = State.JAXPDOTPROPERTIES;
      } 
    } catch (NumberFormatException numberFormatException) {}
  }
  
  public enum State {
    DEFAULT, FSP, JAXPDOTPROPERTIES, SYSTEMPROPERTY, APIPROPERTY;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\FeaturePropertyBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */