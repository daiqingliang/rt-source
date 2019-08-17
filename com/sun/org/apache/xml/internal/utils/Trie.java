package com.sun.org.apache.xml.internal.utils;

public class Trie {
  public static final int ALPHA_SIZE = 128;
  
  Node m_Root = new Node();
  
  private char[] m_charBuffer = new char[0];
  
  public Object put(String paramString, Object paramObject) {
    int i = paramString.length();
    if (i > this.m_charBuffer.length)
      this.m_charBuffer = new char[i]; 
    Node node = this.m_Root;
    for (byte b = 0; b < i; b++) {
      Node node1 = node.m_nextChar[Character.toUpperCase(paramString.charAt(b))];
      if (node1 != null) {
        node = node1;
      } else {
        while (b < i) {
          Node node2 = new Node();
          node.m_nextChar[Character.toUpperCase(paramString.charAt(b))] = node2;
          node.m_nextChar[Character.toLowerCase(paramString.charAt(b))] = node2;
          node = node2;
          b++;
        } 
        break;
      } 
    } 
    Object object = node.m_Value;
    node.m_Value = paramObject;
    return object;
  }
  
  public Object get(String paramString) {
    int i = paramString.length();
    if (this.m_charBuffer.length < i)
      return null; 
    Node node = this.m_Root;
    switch (i) {
      case 0:
        return null;
      case 1:
        c = paramString.charAt(0);
        if (c < '') {
          node = node.m_nextChar[c];
          if (node != null)
            return node.m_Value; 
        } 
        return null;
    } 
    paramString.getChars(0, i, this.m_charBuffer, 0);
    for (char c = Character.MIN_VALUE; c < i; c++) {
      char c1 = this.m_charBuffer[c];
      if ('' <= c1)
        return null; 
      node = node.m_nextChar[c1];
      if (node == null)
        return null; 
    } 
    return node.m_Value;
  }
  
  class Node {
    Node[] m_nextChar = new Node[128];
    
    Object m_Value = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\Trie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */