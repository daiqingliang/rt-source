package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class StringEditor extends PropertyEditorSupport {
  public String getJavaInitializationString() {
    Object object = getValue();
    if (object == null)
      return "null"; 
    String str = object.toString();
    int i = str.length();
    StringBuilder stringBuilder = new StringBuilder(i + 2);
    stringBuilder.append('"');
    for (byte b = 0; b < i; b++) {
      char c = str.charAt(b);
      switch (c) {
        case '\b':
          stringBuilder.append("\\b");
          break;
        case '\t':
          stringBuilder.append("\\t");
          break;
        case '\n':
          stringBuilder.append("\\n");
          break;
        case '\f':
          stringBuilder.append("\\f");
          break;
        case '\r':
          stringBuilder.append("\\r");
          break;
        case '"':
          stringBuilder.append("\\\"");
          break;
        case '\\':
          stringBuilder.append("\\\\");
          break;
        default:
          if (c < ' ' || c > '~') {
            stringBuilder.append("\\u");
            String str1 = Integer.toHexString(c);
            for (int j = str1.length(); j < 4; j++)
              stringBuilder.append('0'); 
            stringBuilder.append(str1);
            break;
          } 
          stringBuilder.append(c);
          break;
      } 
    } 
    stringBuilder.append('"');
    return stringBuilder.toString();
  }
  
  public void setAsText(String paramString) { setValue(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\StringEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */