package sun.tracing;

class PrintStreamProbe extends ProbeSkeleton {
  private PrintStreamProvider provider;
  
  private String name;
  
  PrintStreamProbe(PrintStreamProvider paramPrintStreamProvider, String paramString, Class<?>[] paramArrayOfClass) {
    super(paramArrayOfClass);
    this.provider = paramPrintStreamProvider;
    this.name = paramString;
  }
  
  public boolean isEnabled() { return true; }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.provider.getName());
    stringBuffer.append(".");
    stringBuffer.append(this.name);
    stringBuffer.append("(");
    boolean bool = true;
    for (Object object : paramArrayOfObject) {
      if (!bool) {
        stringBuffer.append(",");
      } else {
        bool = false;
      } 
      stringBuffer.append(object.toString());
    } 
    stringBuffer.append(")");
    this.provider.getStream().println(stringBuffer.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\PrintStreamProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */