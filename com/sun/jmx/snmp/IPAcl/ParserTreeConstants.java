package com.sun.jmx.snmp.IPAcl;

interface ParserTreeConstants {
  public static final int JJTSECURITYDEFS = 0;
  
  public static final int JJTACLBLOCK = 1;
  
  public static final int JJTACLITEM = 2;
  
  public static final int JJTCOMMUNITIES = 3;
  
  public static final int JJTCOMMUNITY = 4;
  
  public static final int JJTACCESS = 5;
  
  public static final int JJTMANAGERS = 6;
  
  public static final int JJTHOST = 7;
  
  public static final int JJTHOSTNAME = 8;
  
  public static final int JJTIPADDRESS = 9;
  
  public static final int JJTIPV6ADDRESS = 10;
  
  public static final int JJTIPMASK = 11;
  
  public static final int JJTNETMASK = 12;
  
  public static final int JJTNETMASKV6 = 13;
  
  public static final int JJTTRAPBLOCK = 14;
  
  public static final int JJTTRAPITEM = 15;
  
  public static final int JJTTRAPCOMMUNITY = 16;
  
  public static final int JJTTRAPINTERESTEDHOST = 17;
  
  public static final int JJTHOSTTRAP = 18;
  
  public static final int JJTENTERPRISE = 19;
  
  public static final int JJTTRAPNUM = 20;
  
  public static final int JJTINFORMBLOCK = 21;
  
  public static final int JJTINFORMITEM = 22;
  
  public static final int JJTINFORMCOMMUNITY = 23;
  
  public static final int JJTINFORMINTERESTEDHOST = 24;
  
  public static final int JJTHOSTINFORM = 25;
  
  public static final String[] jjtNodeName = { 
      "SecurityDefs", "AclBlock", "AclItem", "Communities", "Community", "Access", "Managers", "Host", "HostName", "IpAddress", 
      "IpV6Address", "IpMask", "NetMask", "NetMaskV6", "TrapBlock", "TrapItem", "TrapCommunity", "TrapInterestedHost", "HostTrap", "Enterprise", 
      "TrapNum", "InformBlock", "InformItem", "InformCommunity", "InformInterestedHost", "HostInform" };
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\ParserTreeConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */