package com.sun.jndi.dns;

import java.util.Hashtable;

class NameNode {
  private String label;
  
  private Hashtable<String, NameNode> children = null;
  
  private boolean isZoneCut = false;
  
  private int depth = 0;
  
  NameNode(String paramString) { this.label = paramString; }
  
  protected NameNode newNameNode(String paramString) { return new NameNode(paramString); }
  
  String getLabel() { return this.label; }
  
  int depth() { return this.depth; }
  
  boolean isZoneCut() { return this.isZoneCut; }
  
  void setZoneCut(boolean paramBoolean) { this.isZoneCut = paramBoolean; }
  
  Hashtable<String, NameNode> getChildren() { return this.children; }
  
  NameNode get(String paramString) { return (this.children != null) ? (NameNode)this.children.get(paramString) : null; }
  
  NameNode get(DnsName paramDnsName, int paramInt) {
    NameNode nameNode = this;
    for (int i = paramInt; i < paramDnsName.size() && nameNode != null; i++)
      nameNode = nameNode.get(paramDnsName.getKey(i)); 
    return nameNode;
  }
  
  NameNode add(DnsName paramDnsName, int paramInt) {
    NameNode nameNode = this;
    for (int i = paramInt; i < paramDnsName.size(); i++) {
      String str1 = paramDnsName.get(i);
      String str2 = paramDnsName.getKey(i);
      NameNode nameNode1 = null;
      if (nameNode.children == null) {
        nameNode.children = new Hashtable();
      } else {
        nameNode1 = (NameNode)nameNode.children.get(str2);
      } 
      if (nameNode1 == null) {
        nameNode1 = newNameNode(str1);
        nameNode.depth++;
        nameNode.children.put(str2, nameNode1);
      } 
      nameNode = nameNode1;
    } 
    return nameNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\NameNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */