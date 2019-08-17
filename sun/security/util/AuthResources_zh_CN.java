package sun.security.util;

import java.util.ListResourceBundle;

public class AuthResources_zh_CN extends ListResourceBundle {
  private static final Object[][] contents = { 
      { "invalid.null.input.value", "无效的空输入: {0}" }, { "NTDomainPrincipal.name", "NTDomainPrincipal: {0}" }, { "NTNumericCredential.name", "NTNumericCredential: {0}" }, { "Invalid.NTSid.value", "无效的 NTSid 值" }, { "NTSid.name", "NTSid: {0}" }, { "NTSidDomainPrincipal.name", "NTSidDomainPrincipal: {0}" }, { "NTSidGroupPrincipal.name", "NTSidGroupPrincipal: {0}" }, { "NTSidPrimaryGroupPrincipal.name", "NTSidPrimaryGroupPrincipal: {0}" }, { "NTSidUserPrincipal.name", "NTSidUserPrincipal: {0}" }, { "NTUserPrincipal.name", "NTUserPrincipal: {0}" }, 
      { "UnixNumericGroupPrincipal.Primary.Group.name", "UnixNumericGroupPrincipal [主组]: {0}" }, { "UnixNumericGroupPrincipal.Supplementary.Group.name", "UnixNumericGroupPrincipal [补充组]: {0}" }, { "UnixNumericUserPrincipal.name", "UnixNumericUserPrincipal: {0}" }, { "UnixPrincipal.name", "UnixPrincipal: {0}" }, { "Unable.to.properly.expand.config", "无法正确扩展{0}" }, { "extra.config.No.such.file.or.directory.", "{0} (没有这样的文件或目录)" }, { "Configuration.Error.No.such.file.or.directory", "配置错误:\n\t没有此文件或目录" }, { "Configuration.Error.Invalid.control.flag.flag", "配置错误: \n\t无效的控制标记, {0}" }, { "Configuration.Error.Can.not.specify.multiple.entries.for.appName", "配置错误:\n\t无法指定{0}的多个条目" }, { "Configuration.Error.expected.expect.read.end.of.file.", "配置错误: \n\t应为 [{0}], 读取的是 [文件结尾]" }, 
      { "Configuration.Error.Line.line.expected.expect.found.value.", "配置错误: \n\t行 {0}: 应为 [{1}], 找到 [{2}]" }, { "Configuration.Error.Line.line.expected.expect.", "配置错误: \n\t行 {0}: 应为 [{1}]" }, { "Configuration.Error.Line.line.system.property.value.expanded.to.empty.value", "配置错误: \n\t行 {0}: 系统属性 [{1}] 扩展到空值" }, { "username.", "用户名: " }, { "password.", "口令: " }, { "Please.enter.keystore.information", "请输入密钥库信息" }, { "Keystore.alias.", "密钥库别名: " }, { "Keystore.password.", "密钥库口令: " }, { "Private.key.password.optional.", "私有密钥口令 (可选): " }, { "Kerberos.username.defUsername.", "Kerberos 用户名 [{0}]: " }, 
      { "Kerberos.password.for.username.", "{0}的 Kerberos 口令: " }, { ".error.parsing.", ": 解析时出错 " }, { "COLON", ": " }, { ".error.adding.Permission.", ": 添加权限时出错 " }, { "SPACE", " " }, { ".error.adding.Entry.", ": 添加条目时出错 " }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "attempt.to.add.a.Permission.to.a.readonly.PermissionCollection", "尝试将权限添加至只读的 PermissionCollection" }, { "expected.keystore.type", "应为密钥库类型" }, 
      { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "没有通配符名称, 无法使用通配符类指定主用户" }, { "expected.codeBase.or.SignedBy", "应为 codeBase 或 SignedBy" }, { "only.Principal.based.grant.entries.permitted", "只允许基于主用户的授权条目" }, { "expected.permission.entry", "应为权限条目" }, { "number.", "编号 " }, { "expected.expect.read.end.of.file.", "应为{0}, 读取的是文件结尾" }, { "expected.read.end.of.file", "应为 ';', 读取的是文件结尾" }, { "line.", "行 " }, { ".expected.", ": 应为 '" }, { ".found.", "', 找到 '" }, 
      { "QUOTE", "'" }, { "SolarisNumericGroupPrincipal.Primary.Group.", "SolarisNumericGroupPrincipal [主组]: " }, { "SolarisNumericGroupPrincipal.Supplementary.Group.", "SolarisNumericGroupPrincipal [补充组]: " }, { "SolarisNumericUserPrincipal.", "SolarisNumericUserPrincipal: " }, { "SolarisPrincipal.", "SolarisPrincipal: " }, { "provided.null.name", "提供的名称为空值" } };
  
  public Object[][] getContents() { return contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\AuthResources_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */