package com.sun.jmx.snmp.IPAcl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

class Parser implements ParserTreeConstants, ParserConstants {
  protected JJTParserState jjtree = new JJTParserState();
  
  public ParserTokenManager token_source;
  
  ASCII_CharStream jj_input_stream;
  
  public Token token;
  
  public Token jj_nt;
  
  private int jj_ntk;
  
  private Token jj_scanpos;
  
  private Token jj_lastpos;
  
  private int jj_la;
  
  public boolean lookingAhead = false;
  
  private boolean jj_semLA;
  
  private int jj_gen;
  
  private final int[] jj_la1 = new int[22];
  
  private final int[] jj_la1_0 = { 
      256, 524288, 1048576, 8192, 0, 393216, 0, Integer.MIN_VALUE, 285212672, 0, 
      0, 0, 0, 8192, 8192, 0, -1862270976, 0, 32768, 8192, 
      0, -1862270976 };
  
  private final int[] jj_la1_1 = { 
      0, 0, 0, 0, 16, 0, 16, 0, 0, 32, 
      32, 64, 32, 0, 0, 16, 0, 16, 0, 0, 
      16, 0 };
  
  private final JJCalls[] jj_2_rtns = new JJCalls[3];
  
  private boolean jj_rescan = false;
  
  private int jj_gc = 0;
  
  private Vector<int[]> jj_expentries = new Vector();
  
  private int[] jj_expentry;
  
  private int jj_kind = -1;
  
  private int[] jj_lasttokens = new int[100];
  
  private int jj_endpos;
  
  public final JDMSecurityDefs SecurityDefs() throws ParseException {
    jDMSecurityDefs = new JDMSecurityDefs(0);
    bool = true;
    this.jjtree.openNodeScope(jDMSecurityDefs);
    try {
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 8:
          AclBlock();
          break;
        default:
          this.jj_la1[0] = this.jj_gen;
          break;
      } 
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 19:
          TrapBlock();
          break;
        default:
          this.jj_la1[1] = this.jj_gen;
          break;
      } 
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 20:
          InformBlock();
          break;
        default:
          this.jj_la1[2] = this.jj_gen;
          break;
      } 
      jj_consume_token(0);
      this.jjtree.closeNodeScope(jDMSecurityDefs, true);
      bool = false;
      return jDMSecurityDefs;
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMSecurityDefs);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMSecurityDefs, true); 
    } 
  }
  
  public final void AclBlock() throws ParseException {
    jDMAclBlock = new JDMAclBlock(1);
    bool = true;
    this.jjtree.openNodeScope(jDMAclBlock);
    try {
      jj_consume_token(8);
      jj_consume_token(9);
      jj_consume_token(13);
      while (true) {
        AclItem();
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 13:
            continue;
        } 
        break;
      } 
      this.jj_la1[3] = this.jj_gen;
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMAclBlock);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMAclBlock, true); 
    } 
  }
  
  public final void AclItem() throws ParseException {
    jDMAclItem = new JDMAclItem(2);
    bool = true;
    this.jjtree.openNodeScope(jDMAclItem);
    try {
      jj_consume_token(13);
      jDMAclItem.com = Communities();
      jDMAclItem.access = Access();
      Managers();
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMAclItem);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMAclItem, true); 
    } 
  }
  
  public final JDMCommunities Communities() throws ParseException {
    jDMCommunities = new JDMCommunities(3);
    bool = true;
    this.jjtree.openNodeScope(jDMCommunities);
    try {
      jj_consume_token(10);
      jj_consume_token(9);
      Community();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 36:
            break;
          default:
            this.jj_la1[4] = this.jj_gen;
            break;
        } 
        jj_consume_token(36);
        Community();
      } 
      this.jjtree.closeNodeScope(jDMCommunities, true);
      bool = false;
      return jDMCommunities;
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMCommunities);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMCommunities, true); 
    } 
  }
  
  public final void Community() throws ParseException {
    jDMCommunity = new JDMCommunity(4);
    bool = true;
    this.jjtree.openNodeScope(jDMCommunity);
    try {
      Token token1 = jj_consume_token(31);
      this.jjtree.closeNodeScope(jDMCommunity, true);
      bool = false;
      jDMCommunity.communityString = token1.image;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMCommunity, true); 
    } 
  }
  
  public final JDMAccess Access() throws ParseException {
    jDMAccess = new JDMAccess(5);
    bool = true;
    this.jjtree.openNodeScope(jDMAccess);
    try {
      jj_consume_token(7);
      jj_consume_token(9);
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 17:
          jj_consume_token(17);
          jDMAccess.access = 17;
          break;
        case 18:
          jj_consume_token(18);
          jDMAccess.access = 18;
          break;
        default:
          this.jj_la1[5] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
      this.jjtree.closeNodeScope(jDMAccess, true);
      bool = false;
      return jDMAccess;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMAccess, true); 
    } 
  }
  
  public final void Managers() throws ParseException {
    jDMManagers = new JDMManagers(6);
    bool = true;
    this.jjtree.openNodeScope(jDMManagers);
    try {
      jj_consume_token(14);
      jj_consume_token(9);
      Host();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 36:
            break;
          default:
            this.jj_la1[6] = this.jj_gen;
            break;
        } 
        jj_consume_token(36);
        Host();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMManagers);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMManagers, true); 
    } 
  }
  
  public final void Host() throws ParseException {
    jDMHost = new JDMHost(7);
    bool = true;
    this.jjtree.openNodeScope(jDMHost);
    try {
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 31:
          HostName();
          break;
        default:
          this.jj_la1[7] = this.jj_gen;
          if (jj_2_1(2147483647)) {
            NetMask();
            break;
          } 
          if (jj_2_2(2147483647)) {
            NetMaskV6();
            break;
          } 
          if (jj_2_3(2147483647)) {
            IpAddress();
            break;
          } 
          switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
            case 28:
              IpV6Address();
              break;
            case 24:
              IpMask();
              break;
          } 
          this.jj_la1[8] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMHost);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMHost, true); 
    } 
  }
  
  public final void HostName() throws ParseException {
    jDMHostName = new JDMHostName(8);
    bool = true;
    this.jjtree.openNodeScope(jDMHostName);
    try {
      Token token1 = jj_consume_token(31);
      jDMHostName.name.append(token1.image);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 37:
            break;
          default:
            this.jj_la1[9] = this.jj_gen;
            break;
        } 
        jj_consume_token(37);
        token1 = jj_consume_token(31);
        jDMHostName.name.append("." + token1.image);
      } 
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMHostName, true); 
    } 
  }
  
  public final void IpAddress() throws ParseException {
    jDMIpAddress = new JDMIpAddress(9);
    bool = true;
    this.jjtree.openNodeScope(jDMIpAddress);
    try {
      Token token1 = jj_consume_token(24);
      jDMIpAddress.address.append(token1.image);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 37:
            break;
          default:
            this.jj_la1[10] = this.jj_gen;
            break;
        } 
        jj_consume_token(37);
        token1 = jj_consume_token(24);
        jDMIpAddress.address.append("." + token1.image);
      } 
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMIpAddress, true); 
    } 
  }
  
  public final void IpV6Address() throws ParseException {
    jDMIpV6Address = new JDMIpV6Address(10);
    bool = true;
    this.jjtree.openNodeScope(jDMIpV6Address);
    try {
      Token token1 = jj_consume_token(28);
      this.jjtree.closeNodeScope(jDMIpV6Address, true);
      bool = false;
      jDMIpV6Address.address.append(token1.image);
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMIpV6Address, true); 
    } 
  }
  
  public final void IpMask() throws ParseException {
    jDMIpMask = new JDMIpMask(11);
    bool = true;
    this.jjtree.openNodeScope(jDMIpMask);
    try {
      Token token1 = jj_consume_token(24);
      jDMIpMask.address.append(token1.image);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 38:
            break;
          default:
            this.jj_la1[11] = this.jj_gen;
            break;
        } 
        jj_consume_token(38);
        token1 = jj_consume_token(24);
        jDMIpMask.address.append("." + token1.image);
      } 
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMIpMask, true); 
    } 
  }
  
  public final void NetMask() throws ParseException {
    jDMNetMask = new JDMNetMask(12);
    bool = true;
    this.jjtree.openNodeScope(jDMNetMask);
    try {
      Token token1 = jj_consume_token(24);
      jDMNetMask.address.append(token1.image);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 37:
            break;
          default:
            this.jj_la1[12] = this.jj_gen;
            break;
        } 
        jj_consume_token(37);
        token1 = jj_consume_token(24);
        jDMNetMask.address.append("." + token1.image);
      } 
      jj_consume_token(39);
      token1 = jj_consume_token(24);
      this.jjtree.closeNodeScope(jDMNetMask, true);
      bool = false;
      jDMNetMask.mask = token1.image;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMNetMask, true); 
    } 
  }
  
  public final void NetMaskV6() throws ParseException {
    jDMNetMaskV6 = new JDMNetMaskV6(13);
    bool = true;
    this.jjtree.openNodeScope(jDMNetMaskV6);
    try {
      Token token1 = jj_consume_token(28);
      jDMNetMaskV6.address.append(token1.image);
      jj_consume_token(39);
      token1 = jj_consume_token(24);
      this.jjtree.closeNodeScope(jDMNetMaskV6, true);
      bool = false;
      jDMNetMaskV6.mask = token1.image;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMNetMaskV6, true); 
    } 
  }
  
  public final void TrapBlock() throws ParseException {
    jDMTrapBlock = new JDMTrapBlock(14);
    bool = true;
    this.jjtree.openNodeScope(jDMTrapBlock);
    try {
      jj_consume_token(19);
      jj_consume_token(9);
      jj_consume_token(13);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 13:
            break;
          default:
            this.jj_la1[13] = this.jj_gen;
            break;
        } 
        TrapItem();
      } 
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMTrapBlock);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMTrapBlock, true); 
    } 
  }
  
  public final void TrapItem() throws ParseException {
    jDMTrapItem = new JDMTrapItem(15);
    bool = true;
    this.jjtree.openNodeScope(jDMTrapItem);
    try {
      jj_consume_token(13);
      jDMTrapItem.comm = TrapCommunity();
      TrapInterestedHost();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 13:
            break;
          default:
            this.jj_la1[14] = this.jj_gen;
            break;
        } 
        Enterprise();
      } 
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMTrapItem);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMTrapItem, true); 
    } 
  }
  
  public final JDMTrapCommunity TrapCommunity() throws ParseException {
    jDMTrapCommunity = new JDMTrapCommunity(16);
    bool = true;
    this.jjtree.openNodeScope(jDMTrapCommunity);
    try {
      jj_consume_token(21);
      jj_consume_token(9);
      Token token1 = jj_consume_token(31);
      this.jjtree.closeNodeScope(jDMTrapCommunity, true);
      bool = false;
      jDMTrapCommunity.community = token1.image;
      return jDMTrapCommunity;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMTrapCommunity, true); 
    } 
  }
  
  public final void TrapInterestedHost() throws ParseException {
    jDMTrapInterestedHost = new JDMTrapInterestedHost(17);
    bool = true;
    this.jjtree.openNodeScope(jDMTrapInterestedHost);
    try {
      jj_consume_token(12);
      jj_consume_token(9);
      HostTrap();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 36:
            break;
          default:
            this.jj_la1[15] = this.jj_gen;
            break;
        } 
        jj_consume_token(36);
        HostTrap();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMTrapInterestedHost);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMTrapInterestedHost, true); 
    } 
  }
  
  public final void HostTrap() throws ParseException {
    jDMHostTrap = new JDMHostTrap(18);
    bool = true;
    this.jjtree.openNodeScope(jDMHostTrap);
    try {
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 31:
          HostName();
          break;
        case 24:
          IpAddress();
          break;
        case 28:
          IpV6Address();
          break;
        default:
          this.jj_la1[16] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMHostTrap);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMHostTrap, true); 
    } 
  }
  
  public final void Enterprise() throws ParseException {
    jDMEnterprise = new JDMEnterprise(19);
    bool = true;
    this.jjtree.openNodeScope(jDMEnterprise);
    try {
      jj_consume_token(13);
      jj_consume_token(11);
      jj_consume_token(9);
      Token token1 = jj_consume_token(35);
      jDMEnterprise.enterprise = token1.image;
      jj_consume_token(23);
      jj_consume_token(9);
      TrapNum();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 36:
            break;
          default:
            this.jj_la1[17] = this.jj_gen;
            break;
        } 
        jj_consume_token(36);
        TrapNum();
      } 
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMEnterprise);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMEnterprise, true); 
    } 
  }
  
  public final void TrapNum() throws ParseException {
    jDMTrapNum = new JDMTrapNum(20);
    bool = true;
    this.jjtree.openNodeScope(jDMTrapNum);
    try {
      Token token1 = jj_consume_token(24);
      jDMTrapNum.low = Integer.parseInt(token1.image);
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 15:
          jj_consume_token(15);
          token1 = jj_consume_token(24);
          jDMTrapNum.high = Integer.parseInt(token1.image);
          break;
        default:
          this.jj_la1[18] = this.jj_gen;
          break;
      } 
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMTrapNum, true); 
    } 
  }
  
  public final void InformBlock() throws ParseException {
    jDMInformBlock = new JDMInformBlock(21);
    bool = true;
    this.jjtree.openNodeScope(jDMInformBlock);
    try {
      jj_consume_token(20);
      jj_consume_token(9);
      jj_consume_token(13);
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 13:
            break;
          default:
            this.jj_la1[19] = this.jj_gen;
            break;
        } 
        InformItem();
      } 
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMInformBlock);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMInformBlock, true); 
    } 
  }
  
  public final void InformItem() throws ParseException {
    jDMInformItem = new JDMInformItem(22);
    bool = true;
    this.jjtree.openNodeScope(jDMInformItem);
    try {
      jj_consume_token(13);
      jDMInformItem.comm = InformCommunity();
      InformInterestedHost();
      jj_consume_token(16);
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMInformItem);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMInformItem, true); 
    } 
  }
  
  public final JDMInformCommunity InformCommunity() throws ParseException {
    jDMInformCommunity = new JDMInformCommunity(23);
    bool = true;
    this.jjtree.openNodeScope(jDMInformCommunity);
    try {
      jj_consume_token(22);
      jj_consume_token(9);
      Token token1 = jj_consume_token(31);
      this.jjtree.closeNodeScope(jDMInformCommunity, true);
      bool = false;
      jDMInformCommunity.community = token1.image;
      return jDMInformCommunity;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMInformCommunity, true); 
    } 
  }
  
  public final void InformInterestedHost() throws ParseException {
    jDMInformInterestedHost = new JDMInformInterestedHost(24);
    bool = true;
    this.jjtree.openNodeScope(jDMInformInterestedHost);
    try {
      jj_consume_token(12);
      jj_consume_token(9);
      HostInform();
      while (true) {
        switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
          case 36:
            break;
          default:
            this.jj_la1[20] = this.jj_gen;
            break;
        } 
        jj_consume_token(36);
        HostInform();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMInformInterestedHost);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMInformInterestedHost, true); 
    } 
  }
  
  public final void HostInform() throws ParseException {
    jDMHostInform = new JDMHostInform(25);
    bool = true;
    this.jjtree.openNodeScope(jDMHostInform);
    try {
      switch ((this.jj_ntk == -1) ? jj_ntk() : this.jj_ntk) {
        case 31:
          HostName();
          break;
        case 24:
          IpAddress();
          break;
        case 28:
          IpV6Address();
          break;
        default:
          this.jj_la1[21] = this.jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      } 
    } catch (Throwable throwable) {
      if (bool) {
        this.jjtree.clearNodeScope(jDMHostInform);
        bool = false;
      } else {
        this.jjtree.popNode();
      } 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof ParseException)
        throw (ParseException)throwable; 
      throw (Error)throwable;
    } finally {
      if (bool)
        this.jjtree.closeNodeScope(jDMHostInform, true); 
    } 
  }
  
  private final boolean jj_2_1(int paramInt) {
    this.jj_la = paramInt;
    this.jj_lastpos = this.jj_scanpos = this.token;
    boolean bool = !jj_3_1();
    jj_save(0, paramInt);
    return bool;
  }
  
  private final boolean jj_2_2(int paramInt) {
    this.jj_la = paramInt;
    this.jj_lastpos = this.jj_scanpos = this.token;
    boolean bool = !jj_3_2();
    jj_save(1, paramInt);
    return bool;
  }
  
  private final boolean jj_2_3(int paramInt) {
    this.jj_la = paramInt;
    this.jj_lastpos = this.jj_scanpos = this.token;
    boolean bool = !jj_3_3();
    jj_save(2, paramInt);
    return bool;
  }
  
  private final boolean jj_3_3() { return jj_scan_token(24) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(37) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : false))); }
  
  private final boolean jj_3_2() { return jj_scan_token(28) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(39) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(24) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : false))))); }
  
  private final boolean jj_3_1() {
    if (jj_scan_token(24))
      return true; 
    if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
      return false; 
    while (true) {
      Token token1 = this.jj_scanpos;
      if (jj_3R_14()) {
        this.jj_scanpos = token1;
        break;
      } 
      if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
        return false; 
    } 
    return jj_scan_token(39) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(24) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : false)));
  }
  
  private final boolean jj_3R_14() { return jj_scan_token(37) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : (jj_scan_token(24) ? true : ((this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) ? false : false))); }
  
  public Parser(InputStream paramInputStream) {
    this.jj_input_stream = new ASCII_CharStream(paramInputStream, 1, 1);
    this.token_source = new ParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  public void ReInit(InputStream paramInputStream) {
    this.jj_input_stream.ReInit(paramInputStream, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  public Parser(Reader paramReader) {
    this.jj_input_stream = new ASCII_CharStream(paramReader, 1, 1);
    this.token_source = new ParserTokenManager(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  public void ReInit(Reader paramReader) {
    this.jj_input_stream.ReInit(paramReader, 1, 1);
    this.token_source.ReInit(this.jj_input_stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  public Parser(ParserTokenManager paramParserTokenManager) {
    this.token_source = paramParserTokenManager;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  public void ReInit(ParserTokenManager paramParserTokenManager) {
    this.token_source = paramParserTokenManager;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jjtree.reset();
    this.jj_gen = 0;
    byte b;
    for (b = 0; b < 22; b++)
      this.jj_la1[b] = -1; 
    for (b = 0; b < this.jj_2_rtns.length; b++)
      this.jj_2_rtns[b] = new JJCalls(); 
  }
  
  private final Token jj_consume_token(int paramInt) throws ParseException {
    Token token1;
    if ((token1 = this.token).next != null) {
      this.token = this.token.next;
    } else {
      this.token = this.token.next = this.token_source.getNextToken();
    } 
    this.jj_ntk = -1;
    if (this.token.kind == paramInt) {
      this.jj_gen++;
      if (++this.jj_gc > 100) {
        this.jj_gc = 0;
        for (byte b = 0; b < this.jj_2_rtns.length; b++) {
          for (JJCalls jJCalls = this.jj_2_rtns[b]; jJCalls != null; jJCalls = jJCalls.next) {
            if (jJCalls.gen < this.jj_gen)
              jJCalls.first = null; 
          } 
        } 
      } 
      return this.token;
    } 
    this.token = token1;
    this.jj_kind = paramInt;
    throw generateParseException();
  }
  
  private final boolean jj_scan_token(int paramInt) {
    if (this.jj_scanpos == this.jj_lastpos) {
      this.jj_la--;
      if (this.jj_scanpos.next == null) {
        this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
      } else {
        this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
      } 
    } else {
      this.jj_scanpos = this.jj_scanpos.next;
    } 
    if (this.jj_rescan) {
      byte b = 0;
      Token token1;
      for (token1 = this.token; token1 != null && token1 != this.jj_scanpos; token1 = token1.next)
        b++; 
      if (token1 != null)
        jj_add_error_token(paramInt, b); 
    } 
    return (this.jj_scanpos.kind != paramInt);
  }
  
  public final Token getNextToken() {
    if (this.token.next != null) {
      this.token = this.token.next;
    } else {
      this.token = this.token.next = this.token_source.getNextToken();
    } 
    this.jj_ntk = -1;
    this.jj_gen++;
    return this.token;
  }
  
  public final Token getToken(int paramInt) throws ParseException {
    Token token1 = this.lookingAhead ? this.jj_scanpos : this.token;
    for (byte b = 0; b < paramInt; b++) {
      if (token1.next != null) {
        token1 = token1.next;
      } else {
        token1 = token1.next = this.token_source.getNextToken();
      } 
    } 
    return token1;
  }
  
  private final int jj_ntk() { return ((this.jj_nt = this.token.next) == null) ? (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind) : (this.jj_ntk = this.jj_nt.kind); }
  
  private void jj_add_error_token(int paramInt1, int paramInt2) {
    if (paramInt2 >= 100)
      return; 
    if (paramInt2 == this.jj_endpos + 1) {
      this.jj_lasttokens[this.jj_endpos++] = paramInt1;
    } else if (this.jj_endpos != 0) {
      this.jj_expentry = new int[this.jj_endpos];
      byte b;
      for (b = 0; b < this.jj_endpos; b++)
        this.jj_expentry[b] = this.jj_lasttokens[b]; 
      b = 0;
      Enumeration enumeration = this.jj_expentries.elements();
      while (enumeration.hasMoreElements()) {
        int[] arrayOfInt = (int[])enumeration.nextElement();
        if (arrayOfInt.length == this.jj_expentry.length) {
          b = 1;
          for (byte b1 = 0; b1 < this.jj_expentry.length; b1++) {
            if (arrayOfInt[b1] != this.jj_expentry[b1]) {
              b = 0;
              break;
            } 
          } 
          if (b != 0)
            break; 
        } 
      } 
      if (b == 0)
        this.jj_expentries.addElement(this.jj_expentry); 
      if (paramInt2 != 0)
        this.jj_lasttokens[(this.jj_endpos = paramInt2) - 1] = paramInt1; 
    } 
  }
  
  public final ParseException generateParseException() {
    this.jj_expentries.removeAllElements();
    boolean[] arrayOfBoolean = new boolean[40];
    byte b1;
    for (b1 = 0; b1 < 40; b1++)
      arrayOfBoolean[b1] = false; 
    if (this.jj_kind >= 0) {
      arrayOfBoolean[this.jj_kind] = true;
      this.jj_kind = -1;
    } 
    for (b1 = 0; b1 < 22; b1++) {
      if (this.jj_la1[b1] == this.jj_gen)
        for (byte b = 0; b < 32; b++) {
          if ((this.jj_la1_0[b1] & true << b) != 0)
            arrayOfBoolean[b] = true; 
          if ((this.jj_la1_1[b1] & true << b) != 0)
            arrayOfBoolean[32 + b] = true; 
        }  
    } 
    for (b1 = 0; b1 < 40; b1++) {
      if (arrayOfBoolean[b1]) {
        this.jj_expentry = new int[1];
        this.jj_expentry[0] = b1;
        this.jj_expentries.addElement(this.jj_expentry);
      } 
    } 
    this.jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] arrayOfInt = new int[this.jj_expentries.size()][];
    for (byte b2 = 0; b2 < this.jj_expentries.size(); b2++)
      arrayOfInt[b2] = (int[])this.jj_expentries.elementAt(b2); 
    return new ParseException(this.token, arrayOfInt, tokenImage);
  }
  
  public final void enable_tracing() throws ParseException {}
  
  public final void disable_tracing() throws ParseException {}
  
  private final void jj_rescan_token() throws ParseException {
    this.jj_rescan = true;
    for (byte b = 0; b < 3; b++) {
      JJCalls jJCalls = this.jj_2_rtns[b];
      do {
        if (jJCalls.gen > this.jj_gen) {
          this.jj_la = jJCalls.arg;
          this.jj_lastpos = this.jj_scanpos = jJCalls.first;
          switch (b) {
            case false:
              jj_3_1();
              break;
            case true:
              jj_3_2();
              break;
            case true:
              jj_3_3();
              break;
          } 
        } 
        jJCalls = jJCalls.next;
      } while (jJCalls != null);
    } 
    this.jj_rescan = false;
  }
  
  private final void jj_save(int paramInt1, int paramInt2) {
    JJCalls jJCalls;
    for (jJCalls = this.jj_2_rtns[paramInt1]; jJCalls.gen > this.jj_gen; jJCalls = jJCalls.next) {
      if (jJCalls.next == null) {
        jJCalls = jJCalls.next = new JJCalls();
        break;
      } 
    } 
    jJCalls.gen = this.jj_gen + paramInt2 - this.jj_la;
    jJCalls.first = this.token;
    jJCalls.arg = paramInt2;
  }
  
  static final class JJCalls {
    int gen;
    
    Token first;
    
    int arg;
    
    JJCalls next;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */