package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class QName {
  private final String _localname;
  
  private String _prefix;
  
  private String _namespace;
  
  private String _stringRep;
  
  private int _hashCode;
  
  public QName(String paramString1, String paramString2, String paramString3) {
    this._namespace = paramString1;
    this._prefix = paramString2;
    this._localname = paramString3;
    this._stringRep = (paramString1 != null && !paramString1.equals("")) ? (paramString1 + ':' + paramString3) : paramString3;
    this._hashCode = this._stringRep.hashCode() + 19;
  }
  
  public void clearNamespace() { this._namespace = ""; }
  
  public String toString() { return this._stringRep; }
  
  public String getStringRep() { return this._stringRep; }
  
  public boolean equals(Object paramObject) { return (this == paramObject || (paramObject instanceof QName && this._stringRep.equals(((QName)paramObject).getStringRep()))); }
  
  public String getLocalPart() { return this._localname; }
  
  public String getNamespace() { return this._namespace; }
  
  public String getPrefix() { return this._prefix; }
  
  public int hashCode() { return this._hashCode; }
  
  public String dump() { return "QName: " + this._namespace + "(" + this._prefix + "):" + this._localname; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\QName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */