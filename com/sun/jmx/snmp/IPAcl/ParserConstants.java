package com.sun.jmx.snmp.IPAcl;

interface ParserConstants {
  public static final int EOF = 0;
  
  public static final int ACCESS = 7;
  
  public static final int ACL = 8;
  
  public static final int ASSIGN = 9;
  
  public static final int COMMUNITIES = 10;
  
  public static final int ENTERPRISE = 11;
  
  public static final int HOSTS = 12;
  
  public static final int LBRACE = 13;
  
  public static final int MANAGERS = 14;
  
  public static final int RANGE = 15;
  
  public static final int RBRACE = 16;
  
  public static final int RO = 17;
  
  public static final int RW = 18;
  
  public static final int TRAP = 19;
  
  public static final int INFORM = 20;
  
  public static final int TRAPCOMMUNITY = 21;
  
  public static final int INFORMCOMMUNITY = 22;
  
  public static final int TRAPNUM = 23;
  
  public static final int INTEGER_LITERAL = 24;
  
  public static final int DECIMAL_LITERAL = 25;
  
  public static final int HEX_LITERAL = 26;
  
  public static final int OCTAL_LITERAL = 27;
  
  public static final int V6_ADDRESS = 28;
  
  public static final int H = 29;
  
  public static final int D = 30;
  
  public static final int IDENTIFIER = 31;
  
  public static final int LETTER = 32;
  
  public static final int SEPARATOR = 33;
  
  public static final int DIGIT = 34;
  
  public static final int CSTRING = 35;
  
  public static final int COMMA = 36;
  
  public static final int DOT = 37;
  
  public static final int MARK = 38;
  
  public static final int MASK = 39;
  
  public static final int DEFAULT = 0;
  
  public static final String[] tokenImage = { 
      "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "<token of kind 5>", "<token of kind 6>", "\"access\"", "\"acl\"", "\"=\"", 
      "\"communities\"", "\"enterprise\"", "\"hosts\"", "\"{\"", "\"managers\"", "\"-\"", "\"}\"", "\"read-only\"", "\"read-write\"", "\"trap\"", 
      "\"inform\"", "\"trap-community\"", "\"inform-community\"", "\"trap-num\"", "<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<HEX_LITERAL>", "<OCTAL_LITERAL>", "<V6_ADDRESS>", "<H>", 
      "<D>", "<IDENTIFIER>", "<LETTER>", "<SEPARATOR>", "<DIGIT>", "<CSTRING>", "\",\"", "\".\"", "\"!\"", "\"/\"" };
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\ParserConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */