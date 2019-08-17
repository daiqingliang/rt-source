package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibTree {
  private SnmpMibAgent defaultAgent = null;
  
  private TreeNode root = new TreeNode(-1L, null, null, null);
  
  public void setDefaultAgent(SnmpMibAgent paramSnmpMibAgent) {
    this.defaultAgent = paramSnmpMibAgent;
    this.root.agent = paramSnmpMibAgent;
  }
  
  public SnmpMibAgent getDefaultAgent() { return this.defaultAgent; }
  
  public void register(SnmpMibAgent paramSnmpMibAgent) { this.root.registerNode(paramSnmpMibAgent); }
  
  public void register(SnmpMibAgent paramSnmpMibAgent, long[] paramArrayOfLong) { this.root.registerNode(paramArrayOfLong, 0, paramSnmpMibAgent); }
  
  public SnmpMibAgent getAgentMib(SnmpOid paramSnmpOid) {
    TreeNode treeNode = this.root.retrieveMatchingBranch(paramSnmpOid.longValue(), 0);
    return (treeNode == null) ? this.defaultAgent : ((treeNode.getAgentMib() == null) ? this.defaultAgent : treeNode.getAgentMib());
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent, SnmpOid[] paramArrayOfSnmpOid) {
    for (byte b = 0; b < paramArrayOfSnmpOid.length; b++) {
      long[] arrayOfLong = paramArrayOfSnmpOid[b].longValue();
      TreeNode treeNode = this.root.retrieveMatchingBranch(arrayOfLong, 0);
      if (treeNode != null)
        treeNode.removeAgent(paramSnmpMibAgent); 
    } 
  }
  
  public void unregister(SnmpMibAgent paramSnmpMibAgent) { this.root.removeAgentFully(paramSnmpMibAgent); }
  
  public void printTree() { this.root.printTree(">"); }
  
  final class TreeNode {
    private Vector<TreeNode> children = new Vector();
    
    private Vector<SnmpMibAgent> agents = new Vector();
    
    private long nodeValue;
    
    private SnmpMibAgent agent;
    
    private TreeNode parent;
    
    void registerNode(SnmpMibAgent param1SnmpMibAgent) {
      long[] arrayOfLong = param1SnmpMibAgent.getRootOid();
      registerNode(arrayOfLong, 0, param1SnmpMibAgent);
    }
    
    TreeNode retrieveMatchingBranch(long[] param1ArrayOfLong, int param1Int) {
      TreeNode treeNode1 = retrieveChild(param1ArrayOfLong, param1Int);
      if (treeNode1 == null)
        return this; 
      if (this.children.isEmpty())
        return treeNode1; 
      if (param1Int + 1 == param1ArrayOfLong.length)
        return treeNode1; 
      TreeNode treeNode2 = treeNode1.retrieveMatchingBranch(param1ArrayOfLong, param1Int + 1);
      return (treeNode2.agent == null) ? this : treeNode2;
    }
    
    SnmpMibAgent getAgentMib() { return this.agent; }
    
    public void printTree(String param1String) {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.agents == null)
        return; 
      Enumeration enumeration = this.agents.elements();
      while (enumeration.hasMoreElements()) {
        SnmpMibAgent snmpMibAgent = (SnmpMibAgent)enumeration.nextElement();
        if (snmpMibAgent == null) {
          stringBuilder.append("empty ");
          continue;
        } 
        stringBuilder.append(snmpMibAgent.getMibName()).append(" ");
      } 
      param1String = param1String + " ";
      if (this.children == null)
        return; 
      enumeration = this.children.elements();
      while (enumeration.hasMoreElements()) {
        TreeNode treeNode = (TreeNode)enumeration.nextElement();
        treeNode.printTree(param1String);
      } 
    }
    
    private TreeNode(long param1Long, SnmpMibAgent param1SnmpMibAgent, TreeNode param1TreeNode) {
      this.nodeValue = param1Long;
      this.parent = param1TreeNode;
      this.agents.addElement(param1SnmpMibAgent);
    }
    
    private void removeAgentFully(SnmpMibAgent param1SnmpMibAgent) {
      Vector vector = new Vector();
      Enumeration enumeration = this.children.elements();
      while (enumeration.hasMoreElements()) {
        TreeNode treeNode = (TreeNode)enumeration.nextElement();
        treeNode.removeAgentFully(param1SnmpMibAgent);
        if (treeNode.agents.isEmpty())
          vector.add(treeNode); 
      } 
      enumeration = vector.elements();
      while (enumeration.hasMoreElements())
        this.children.removeElement(enumeration.nextElement()); 
      removeAgent(param1SnmpMibAgent);
    }
    
    private void removeAgent(SnmpMibAgent param1SnmpMibAgent) {
      if (!this.agents.contains(param1SnmpMibAgent))
        return; 
      this.agents.removeElement(param1SnmpMibAgent);
      if (!this.agents.isEmpty())
        this.agent = (SnmpMibAgent)this.agents.firstElement(); 
    }
    
    private void setAgent(SnmpMibAgent param1SnmpMibAgent) { this.agent = param1SnmpMibAgent; }
    
    private void registerNode(long[] param1ArrayOfLong, int param1Int, SnmpMibAgent param1SnmpMibAgent) {
      if (param1Int >= param1ArrayOfLong.length)
        return; 
      TreeNode treeNode = retrieveChild(param1ArrayOfLong, param1Int);
      if (treeNode == null) {
        long l = param1ArrayOfLong[param1Int];
        treeNode = new TreeNode(SnmpMibTree.this, l, param1SnmpMibAgent, this);
        this.children.addElement(treeNode);
      } else if (!this.agents.contains(param1SnmpMibAgent)) {
        this.agents.addElement(param1SnmpMibAgent);
      } 
      if (param1Int == param1ArrayOfLong.length - 1) {
        treeNode.setAgent(param1SnmpMibAgent);
      } else {
        treeNode.registerNode(param1ArrayOfLong, param1Int + 1, param1SnmpMibAgent);
      } 
    }
    
    private TreeNode retrieveChild(long[] param1ArrayOfLong, int param1Int) {
      long l = param1ArrayOfLong[param1Int];
      Enumeration enumeration = this.children.elements();
      while (enumeration.hasMoreElements()) {
        TreeNode treeNode = (TreeNode)enumeration.nextElement();
        if (treeNode.match(l))
          return treeNode; 
      } 
      return null;
    }
    
    private boolean match(long param1Long) { return (this.nodeValue == param1Long); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\daemon\SnmpMibTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */