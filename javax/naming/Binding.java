package javax.naming;

public class Binding extends NameClassPair {
  private Object boundObj;
  
  private static final long serialVersionUID = 8839217842691845890L;
  
  public Binding(String paramString, Object paramObject) {
    super(paramString, null);
    this.boundObj = paramObject;
  }
  
  public Binding(String paramString, Object paramObject, boolean paramBoolean) {
    super(paramString, null, paramBoolean);
    this.boundObj = paramObject;
  }
  
  public Binding(String paramString1, String paramString2, Object paramObject) {
    super(paramString1, paramString2);
    this.boundObj = paramObject;
  }
  
  public Binding(String paramString1, String paramString2, Object paramObject, boolean paramBoolean) {
    super(paramString1, paramString2, paramBoolean);
    this.boundObj = paramObject;
  }
  
  public String getClassName() {
    String str = super.getClassName();
    return (str != null) ? str : ((this.boundObj != null) ? this.boundObj.getClass().getName() : null);
  }
  
  public Object getObject() { return this.boundObj; }
  
  public void setObject(Object paramObject) { this.boundObj = paramObject; }
  
  public String toString() { return super.toString() + ":" + getObject(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */