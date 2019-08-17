package com.sun.org.apache.xerces.internal.xs;

public interface PSVIProvider {
  ElementPSVI getElementPSVI();
  
  AttributePSVI getAttributePSVI(int paramInt);
  
  AttributePSVI getAttributePSVIByName(String paramString1, String paramString2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xs\PSVIProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */