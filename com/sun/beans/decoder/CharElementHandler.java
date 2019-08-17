package com.sun.beans.decoder;

final class CharElementHandler extends StringElementHandler {
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("code")) {
      int i = Integer.decode(paramString2).intValue();
      for (char c : Character.toChars(i))
        addCharacter(c); 
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  public Object getValue(String paramString) {
    if (paramString.length() != 1)
      throw new IllegalArgumentException("Wrong characters count"); 
    return Character.valueOf(paramString.charAt(0));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\CharElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */