package org.jeecf.manager.module.userpower.model.domain;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jeecf.manager.common.model.BaseEntity;
import org.jeecf.manager.validate.constraints.English;
import org.jeecf.manager.validate.groups.Add;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 系统用户
 * 
 * @author GloryJian
 * @version 1.0
 */
@ApiModel(value="sysUser",description="系统用户实体")
public class SysUser extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 账户
	 */
	@ApiModelProperty(value="账户",name="username")
	private String username;
	
	/**
	 * 密码
	 */
	@ApiModelProperty(value="密码",name="password")
	private String password;
	
	/**
	 * 用户名
	 */
	@ApiModelProperty(value="用户名",name="name")
	private String name;
	
	/**
	 * 组织结构id
	 */
	private Integer sysOfficeId;
	
	/**
	 * 角色集合
	 */
	@ApiModelProperty(value="角色集合",name="sysRoleIds")
	private List<String> sysRoleIds;


	public SysUser() {
		super();
	}

	public SysUser(String id) {
		super(id);
	}
	
	
	@NotBlank(message="账号输入不能为空",groups= {Add.class})
	@Length(min = 1, max = 20, message = "账户长度必须介于 1 和 20 之间",groups= {Add.class})
	@English(message="账号只能为英文字符",groups= {Add.class})
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Length(min = 1, max = 64, message = "密码长度必须介于 1 和 64 之间",groups= {Add.class})
	@English(message="密码只能为英文字符",groups= {Add.class})
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@NotBlank(message="用户名输入不能为空",groups= {Add.class})
	@Length(min = 1, max = 20, message = "用户名长度必须介于 1 和 20 之间",groups= {Add.class})
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@NotNull(message="角色输入不能为空",groups= {Add.class})
	@Size(min=1,max=5,message="超过范围，最大可添加5个参数",groups= {Add.class})
	public List<String> getSysRoleIds() {
		return sysRoleIds;
	}

	public void setSysRoleIds(List<String> sysRoleIds) {
		this.sysRoleIds = sysRoleIds;
	}

	public Integer getSysOfficeId() {
		return sysOfficeId;
	}

	public void setSysOfficeId(Integer sysOfficeId) {
		this.sysOfficeId = sysOfficeId;
	}
	
	
}