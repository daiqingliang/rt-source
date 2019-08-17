package sun.security.tools.policytool;

import java.util.ListResourceBundle;

public class Resources_zh_CN extends ListResourceBundle {
  private static final Object[][] contents = { 
      { "NEWLINE", "\n" }, { "Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.", "警告: 别名 {0} 的公共密钥不存在。请确保已正确配置密钥库。" }, { "Warning.Class.not.found.class", "警告: 找不到类: {0}" }, { "Warning.Invalid.argument.s.for.constructor.arg", "警告: 构造器的参数无效: {0}" }, { "Illegal.Principal.Type.type", "非法的主用户类型: {0}" }, { "Illegal.option.option", "非法选项: {0}" }, { "Usage.policytool.options.", "用法: policytool [选项]" }, { ".file.file.policy.file.location", "  [-file <file>]    策略文件位置" }, { "New", "新建(&N)" }, { "Open", "打开(&O)..." }, 
      { "Save", "保存(&S)" }, { "Save.As", "另存为(&A)..." }, { "View.Warning.Log", "查看警告日志(&W)" }, { "Exit", "退出(&X)" }, { "Add.Policy.Entry", "添加策略条目(&A)" }, { "Edit.Policy.Entry", "编辑策略条目(&E)" }, { "Remove.Policy.Entry", "删除策略条目(&R)" }, { "Edit", "编辑(&E)" }, { "Retain", "保留" }, { "Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes", "警告: 文件名包含转义的反斜杠字符。不需要对反斜杠字符进行转义 (该工具在将策略内容写入永久存储时会根据需要对字符进行转义)。\n\n单击“保留”可保留输入的名称, 或者单击“编辑”可编辑该名称。" }, 
      { "Add.Public.Key.Alias", "添加公共密钥别名" }, { "Remove.Public.Key.Alias", "删除公共密钥别名" }, { "File", "文件(&F)" }, { "KeyStore", "密钥库(&K)" }, { "Policy.File.", "策略文件:" }, { "Could.not.open.policy.file.policyFile.e.toString.", "无法打开策略文件: {0}: {1}" }, { "Policy.Tool", "策略工具" }, { "Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.", "打开策略配置时出错。有关详细信息, 请查看警告日志。" }, { "Error", "错误" }, { "OK", "确定" }, 
      { "Status", "状态" }, { "Warning", "警告" }, { "Permission.", "权限:                                                       " }, { "Principal.Type.", "主用户类型:" }, { "Principal.Name.", "主用户名称:" }, { "Target.Name.", "目标名称:                                                    " }, { "Actions.", "操作:                                                             " }, { "OK.to.overwrite.existing.file.filename.", "确认覆盖现有的文件{0}?" }, { "Cancel", "取消" }, { "CodeBase.", "CodeBase(&C):" }, 
      { "SignedBy.", "SignedBy(&S):" }, { "Add.Principal", "添加主用户(&A)" }, { "Edit.Principal", "编辑主用户(&E)" }, { "Remove.Principal", "删除主用户(&R)" }, { "Principals.", "主用户(&P):" }, { ".Add.Permission", "  添加权限(&D)" }, { ".Edit.Permission", "  编辑权限(&I)" }, { "Remove.Permission", "删除权限(&M)" }, { "Done", "完成" }, { "KeyStore.URL.", "密钥库 URL(&U):" }, 
      { "KeyStore.Type.", "密钥库类型(&T):" }, { "KeyStore.Provider.", "密钥库提供方(&P):" }, { "KeyStore.Password.URL.", "密钥库口令 URL(&W):" }, { "Principals", "主用户" }, { ".Edit.Principal.", "  编辑主用户:" }, { ".Add.New.Principal.", "  添加新主用户:" }, { "Permissions", "权限" }, { ".Edit.Permission.", "  编辑权限:" }, { ".Add.New.Permission.", "  加入新的权限:" }, { "Signed.By.", "签署人: " }, 
      { "Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name", "没有通配符名称, 无法使用通配符类指定主用户" }, { "Cannot.Specify.Principal.without.a.Name", "没有名称, 无法指定主用户" }, { "Permission.and.Target.Name.must.have.a.value", "权限及目标名必须有一个值" }, { "Remove.this.Policy.Entry.", "是否删除此策略条目?" }, { "Overwrite.File", "覆盖文件" }, { "Policy.successfully.written.to.filename", "策略已成功写入到{0}" }, { "null.filename", "空文件名" }, { "Save.changes.", "是否保存所做的更改?" }, { "Yes", "是(&Y)" }, { "No", "否(&N)" }, 
      { "Policy.Entry", "策略条目" }, { "Save.Changes", "保存更改" }, { "No.Policy.Entry.selected", "没有选择策略条目" }, { "Unable.to.open.KeyStore.ex.toString.", "无法打开密钥库: {0}" }, { "No.principal.selected", "未选择主用户" }, { "No.permission.selected", "没有选择权限" }, { "name", "名称" }, { "configuration.type", "配置类型" }, { "environment.variable.name", "环境变量名" }, { "library.name", "库名称" }, 
      { "package.name", "程序包名称" }, { "policy.type", "策略类型" }, { "property.name", "属性名称" }, { "provider.name", "提供方名称" }, { "url", "URL" }, { "method.list", "方法列表" }, { "request.headers.list", "请求标头列表" }, { "Principal.List", "主用户列表" }, { "Permission.List", "权限列表" }, { "Code.Base", "代码库" }, 
      { "KeyStore.U.R.L.", "密钥库 URL:" }, { "KeyStore.Password.U.R.L.", "密钥库口令 URL:" } };
  
  public Object[][] getContents() { return contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\Resources_zh_CN.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */