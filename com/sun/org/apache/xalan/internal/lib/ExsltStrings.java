package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.NodeSet;
import java.util.StringTokenizer;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ExsltStrings extends ExsltBase {
  public static String align(String paramString1, String paramString2, String paramString3) {
    if (paramString1.length() >= paramString2.length())
      return paramString1.substring(0, paramString2.length()); 
    if (paramString3.equals("right"))
      return paramString2.substring(0, paramString2.length() - paramString1.length()) + paramString1; 
    if (paramString3.equals("center")) {
      int i = (paramString2.length() - paramString1.length()) / 2;
      return paramString2.substring(0, i) + paramString1 + paramString2.substring(i + paramString1.length());
    } 
    return paramString1 + paramString2.substring(paramString1.length());
  }
  
  public static String align(String paramString1, String paramString2) { return align(paramString1, paramString2, "left"); }
  
  public static String concat(NodeList paramNodeList) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramNodeList.getLength(); b++) {
      Node node = paramNodeList.item(b);
      String str = toString(node);
      if (str != null && str.length() > 0)
        stringBuffer.append(str); 
    } 
    return stringBuffer.toString();
  }
  
  public static String padding(double paramDouble, String paramString) {
    if (paramString == null || paramString.length() == 0)
      return ""; 
    StringBuffer stringBuffer = new StringBuffer();
    int i = (int)paramDouble;
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      if (b2 == paramString.length())
        b2 = 0; 
      stringBuffer.append(paramString.charAt(b2));
      b2++;
      b1++;
    } 
    return stringBuffer.toString();
  }
  
  public static String padding(double paramDouble) { return padding(paramDouble, " "); }
  
  public static NodeList split(String paramString1, String paramString2) {
    NodeSet nodeSet = new NodeSet();
    nodeSet.setShouldCacheNodes(true);
    boolean bool = false;
    int i = 0;
    int j = 0;
    String str = null;
    while (!bool && i < paramString1.length()) {
      j = paramString1.indexOf(paramString2, i);
      if (j >= 0) {
        str = paramString1.substring(i, j);
        i = j + paramString2.length();
      } else {
        bool = true;
        str = paramString1.substring(i);
      } 
      Document document = JdkXmlUtils.getDOMDocument();
      synchronized (document) {
        Element element = document.createElement("token");
        Text text = document.createTextNode(str);
        element.appendChild(text);
        nodeSet.addNode(element);
      } 
    } 
    return nodeSet;
  }
  
  public static NodeList split(String paramString) { return split(paramString, " "); }
  
  public static NodeList tokenize(String paramString1, String paramString2) {
    NodeSet nodeSet = new NodeSet();
    if (paramString2 != null && paramString2.length() > 0) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString1, paramString2);
      Document document = JdkXmlUtils.getDOMDocument();
      synchronized (document) {
        while (stringTokenizer.hasMoreTokens()) {
          Element element = document.createElement("token");
          element.appendChild(document.createTextNode(stringTokenizer.nextToken()));
          nodeSet.addNode(element);
        } 
      } 
    } else {
      Document document = JdkXmlUtils.getDOMDocument();
      synchronized (document) {
        for (byte b = 0; b < paramString1.length(); b++) {
          Element element = document.createElement("token");
          element.appendChild(document.createTextNode(paramString1.substring(b, b + true)));
          nodeSet.addNode(element);
        } 
      } 
    } 
    return nodeSet;
  }
  
  public static NodeList tokenize(String paramString) { return tokenize(paramString, " \t\n\r"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltStrings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */