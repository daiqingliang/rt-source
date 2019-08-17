package javax.xml.soap;

public interface SOAPHeaderElement extends SOAPElement {
  void setActor(String paramString);
  
  void setRole(String paramString);
  
  String getActor();
  
  String getRole();
  
  void setMustUnderstand(boolean paramBoolean);
  
  boolean getMustUnderstand();
  
  void setRelay(boolean paramBoolean);
  
  boolean getRelay();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPHeaderElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */