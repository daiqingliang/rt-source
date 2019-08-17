package com.sun.corba.se.impl.presentation.rmi;

public class IDLType {
  private Class cl_;
  
  private String[] modules_;
  
  private String memberName_;
  
  public IDLType(Class paramClass, String[] paramArrayOfString, String paramString) {
    this.cl_ = paramClass;
    this.modules_ = paramArrayOfString;
    this.memberName_ = paramString;
  }
  
  public IDLType(Class paramClass, String paramString) { this(paramClass, new String[0], paramString); }
  
  public Class getJavaClass() { return this.cl_; }
  
  public String[] getModules() { return this.modules_; }
  
  public String makeConcatenatedName(char paramChar, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.modules_.length; b++) {
      String str = this.modules_[b];
      if (b)
        stringBuffer.append(paramChar); 
      if (paramBoolean && IDLNameTranslatorImpl.isIDLKeyword(str))
        str = IDLNameTranslatorImpl.mangleIDLKeywordClash(str); 
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
  
  public String getModuleName() { return makeConcatenatedName('_', false); }
  
  public String getExceptionName() {
    String str1 = makeConcatenatedName('/', true);
    String str2 = "Exception";
    String str3 = this.memberName_;
    if (str3.endsWith(str2)) {
      int i = str3.length() - str2.length();
      str3 = str3.substring(0, i);
    } 
    str3 = str3 + "Ex";
    return (str1.length() == 0) ? ("IDL:" + str3 + ":1.0") : ("IDL:" + str1 + '/' + str3 + ":1.0");
  }
  
  public String getMemberName() { return this.memberName_; }
  
  public boolean hasModule() { return (this.modules_.length > 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\presentation\rmi\IDLType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */