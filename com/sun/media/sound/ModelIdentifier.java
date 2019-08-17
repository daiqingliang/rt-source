package com.sun.media.sound;

public final class ModelIdentifier {
  private String object = null;
  
  private String variable = null;
  
  private int instance = 0;
  
  public ModelIdentifier(String paramString) { this.object = paramString; }
  
  public ModelIdentifier(String paramString, int paramInt) {
    this.object = paramString;
    this.instance = paramInt;
  }
  
  public ModelIdentifier(String paramString1, String paramString2) {
    this.object = paramString1;
    this.variable = paramString2;
  }
  
  public ModelIdentifier(String paramString1, String paramString2, int paramInt) {
    this.object = paramString1;
    this.variable = paramString2;
    this.instance = paramInt;
  }
  
  public int getInstance() { return this.instance; }
  
  public void setInstance(int paramInt) { this.instance = paramInt; }
  
  public String getObject() { return this.object; }
  
  public void setObject(String paramString) { this.object = paramString; }
  
  public String getVariable() { return this.variable; }
  
  public void setVariable(String paramString) { this.variable = paramString; }
  
  public int hashCode() {
    int i = this.instance;
    if (this.object != null)
      i |= this.object.hashCode(); 
    if (this.variable != null)
      i |= this.variable.hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ModelIdentifier))
      return false; 
    ModelIdentifier modelIdentifier = (ModelIdentifier)paramObject;
    return (((this.object == null) ? 1 : 0) != ((modelIdentifier.object == null) ? 1 : 0)) ? false : ((((this.variable == null) ? 1 : 0) != ((modelIdentifier.variable == null) ? 1 : 0)) ? false : ((modelIdentifier.getInstance() != getInstance()) ? false : ((this.object != null && !this.object.equals(modelIdentifier.object)) ? false : (!(this.variable != null && !this.variable.equals(modelIdentifier.variable))))));
  }
  
  public String toString() { return (this.variable == null) ? (this.object + "[" + this.instance + "]") : (this.object + "[" + this.instance + "]." + this.variable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */