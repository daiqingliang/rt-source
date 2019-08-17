package sun.awt.geom;

final class ChainEnd {
  CurveLink head;
  
  CurveLink tail;
  
  ChainEnd partner;
  
  int etag;
  
  public ChainEnd(CurveLink paramCurveLink, ChainEnd paramChainEnd) {
    this.head = paramCurveLink;
    this.tail = paramCurveLink;
    this.partner = paramChainEnd;
    this.etag = paramCurveLink.getEdgeTag();
  }
  
  public CurveLink getChain() { return this.head; }
  
  public void setOtherEnd(ChainEnd paramChainEnd) { this.partner = paramChainEnd; }
  
  public ChainEnd getPartner() { return this.partner; }
  
  public CurveLink linkTo(ChainEnd paramChainEnd) {
    ChainEnd chainEnd2;
    ChainEnd chainEnd1;
    if (this.etag == 0 || paramChainEnd.etag == 0)
      throw new InternalError("ChainEnd linked more than once!"); 
    if (this.etag == paramChainEnd.etag)
      throw new InternalError("Linking chains of the same type!"); 
    if (this.etag == 1) {
      chainEnd1 = this;
      chainEnd2 = paramChainEnd;
    } else {
      chainEnd1 = paramChainEnd;
      chainEnd2 = this;
    } 
    this.etag = 0;
    paramChainEnd.etag = 0;
    chainEnd1.tail.setNext(chainEnd2.head);
    chainEnd1.tail = chainEnd2.tail;
    if (this.partner == paramChainEnd)
      return chainEnd1.head; 
    ChainEnd chainEnd3 = chainEnd2.partner;
    ChainEnd chainEnd4 = chainEnd1.partner;
    chainEnd3.partner = chainEnd4;
    chainEnd4.partner = chainEnd3;
    if (chainEnd1.head.getYTop() < chainEnd3.head.getYTop()) {
      chainEnd1.tail.setNext(chainEnd3.head);
      chainEnd3.head = chainEnd1.head;
    } else {
      chainEnd4.tail.setNext(chainEnd1.head);
      chainEnd4.tail = chainEnd1.tail;
    } 
    return null;
  }
  
  public void addLink(CurveLink paramCurveLink) {
    if (this.etag == 1) {
      this.tail.setNext(paramCurveLink);
      this.tail = paramCurveLink;
    } else {
      paramCurveLink.setNext(this.head);
      this.head = paramCurveLink;
    } 
  }
  
  public double getX() { return (this.etag == 1) ? this.tail.getXBot() : this.head.getXBot(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\ChainEnd.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */