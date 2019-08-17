package com.sun.xml.internal.ws.client;

public static enum ContentNegotiation {
  none, pessimistic, optimistic;
  
  public static final String PROPERTY = "com.sun.xml.internal.ws.client.ContentNegotiation";
  
  public static ContentNegotiation obtainFromSystemProperty() {
    try {
      String str = System.getProperty("com.sun.xml.internal.ws.client.ContentNegotiation");
      return (str == null) ? none : valueOf(str);
    } catch (Exception exception) {
      return none;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\ContentNegotiation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */