package javax.naming;

public class LinkRef extends Reference {
  static final String linkClassName = LinkRef.class.getName();
  
  static final String linkAddrType = "LinkAddress";
  
  private static final long serialVersionUID = -5386290613498931298L;
  
  public LinkRef(Name paramName) { super(linkClassName, new StringRefAddr("LinkAddress", paramName.toString())); }
  
  public LinkRef(String paramString) { super(linkClassName, new StringRefAddr("LinkAddress", paramString)); }
  
  public String getLinkName() throws NamingException {
    if (this.className != null && this.className.equals(linkClassName)) {
      RefAddr refAddr = get("LinkAddress");
      if (refAddr != null && refAddr instanceof StringRefAddr)
        return (String)((StringRefAddr)refAddr).getContent(); 
    } 
    throw new MalformedLinkException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\LinkRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */