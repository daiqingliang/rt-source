package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class SimpleNode implements Node {
  protected Node parent;
  
  protected Node[] children;
  
  protected int id;
  
  protected Parser parser;
  
  public SimpleNode(int paramInt) { this.id = paramInt; }
  
  public SimpleNode(Parser paramParser, int paramInt) {
    this(paramInt);
    this.parser = paramParser;
  }
  
  public static Node jjtCreate(int paramInt) { return new SimpleNode(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new SimpleNode(paramParser, paramInt); }
  
  public void jjtOpen() {}
  
  public void jjtClose() {}
  
  public void jjtSetParent(Node paramNode) { this.parent = paramNode; }
  
  public Node jjtGetParent() { return this.parent; }
  
  public void jjtAddChild(Node paramNode, int paramInt) {
    if (this.children == null) {
      this.children = new Node[paramInt + 1];
    } else if (paramInt >= this.children.length) {
      Node[] arrayOfNode = new Node[paramInt + 1];
      System.arraycopy(this.children, 0, arrayOfNode, 0, this.children.length);
      this.children = arrayOfNode;
    } 
    this.children[paramInt] = paramNode;
  }
  
  public Node jjtGetChild(int paramInt) { return this.children[paramInt]; }
  
  public int jjtGetNumChildren() { return (this.children == null) ? 0 : this.children.length; }
  
  public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {
    if (this.children != null)
      for (byte b = 0; b < this.children.length; b++) {
        SimpleNode simpleNode = (SimpleNode)this.children[b];
        if (simpleNode != null)
          simpleNode.buildTrapEntries(paramHashtable); 
      }  
  }
  
  public void buildInformEntries(Hashtable<InetAddress, Vector<String>> paramHashtable) {
    if (this.children != null)
      for (byte b = 0; b < this.children.length; b++) {
        SimpleNode simpleNode = (SimpleNode)this.children[b];
        if (simpleNode != null)
          simpleNode.buildInformEntries(paramHashtable); 
      }  
  }
  
  public void buildAclEntries(PrincipalImpl paramPrincipalImpl, AclImpl paramAclImpl) {
    if (this.children != null)
      for (byte b = 0; b < this.children.length; b++) {
        SimpleNode simpleNode = (SimpleNode)this.children[b];
        if (simpleNode != null)
          simpleNode.buildAclEntries(paramPrincipalImpl, paramAclImpl); 
      }  
  }
  
  public String toString() { return ParserTreeConstants.jjtNodeName[this.id]; }
  
  public String toString(String paramString) { return paramString + toString(); }
  
  public void dump(String paramString) {
    if (this.children != null)
      for (byte b = 0; b < this.children.length; b++) {
        SimpleNode simpleNode = (SimpleNode)this.children[b];
        if (simpleNode != null)
          simpleNode.dump(paramString + " "); 
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\SimpleNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */