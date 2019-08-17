package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class Discarder extends Loader {
  public static final Loader INSTANCE = new Discarder();
  
  private Discarder() { super(false); }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName) {
    paramState.setTarget(null);
    paramState.setLoader(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Discarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */