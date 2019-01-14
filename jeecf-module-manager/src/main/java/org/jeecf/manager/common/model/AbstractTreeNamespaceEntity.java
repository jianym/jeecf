package org.jeecf.manager.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.ScriptAssert;
import org.jeecf.manager.validate.groups.Add;

import io.swagger.annotations.ApiModelProperty;

/**
 * 树状命名空间实体
 * @author jianyiming
 *
 * @param <T>
 */
@ScriptAssert.List({
	@ScriptAssert(lang = "javascript", script = "org.jeecf.manager.validate.constraints.Script.notBlank(_this.id,_this.name)",message="名称输入不能为空",groups= {Add.class}),
	@ScriptAssert(lang = "javascript", script = "org.jeecf.manager.validate.constraints.Script.notNull(_this.id,_this.sort)",message="排序输入不能为空",groups= {Add.class})
})
public abstract class AbstractTreeNamespaceEntity<T> extends NamespaceAuthEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 名称
	 */
	@ApiModelProperty(value="名称",name="name")
	private String name;	
	
	/**
	 * 父层索引
	 */
	@ApiModelProperty(value="父层索引",name="parentId")
	private String parentId;
	
	/**
	 * 父层索引集
	 */
	@ApiModelProperty(value="所有父层索引",name="parentIds")
	private String parentIds;
	
	/**
	 * 等级
	 */
	@ApiModelProperty(value="等级",name="level")
	private Integer level;
	
	/**
	 * 同级排序
	 */
	@ApiModelProperty(value="同级排序",name="sort")
	private Integer sort;
	
	/* ---------追加字段-------------- */
	/**
	 * 是否有子节点
	 */
	@ApiModelProperty(value="是否有子节点",name="hasChild")
	private boolean hasChild;
	
	/**
	 * 父菜单
	 */
	@ApiModelProperty(value="父菜单",name="parent")
	private T parent;
	
	public AbstractTreeNamespaceEntity() {
		super();
	}

	public AbstractTreeNamespaceEntity(String id) {
		super(id);
	}
	
	@Length(min = 1, max = 20, message = "名称长度必须介于 1 和 20 之间",groups={Add.class})
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Min(value=1,message="输入错误",groups= {Add.class})
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}
	

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public T getParent() {
		return parent;
	}

	public void setParent(T parent) {
		this.parent = parent;
	}
	
	public static String getRootId() {
		return "0";
	}

	public List<String> getParentIdList(){
		List<String> parentIdList = new ArrayList<String>();
		if(StringUtils.isNotEmpty(this.getParentId())) {
			String[] parentIds = this.getParentId().split(",");
			for (String parentId : parentIds) {
				parentIdList.add(parentId);
			}
		}
		return parentIdList;
	}
	/**
	 * 根据sort 列表排序
	 * @param newList 排序后列表
	 * @param sourceList 源列表
	 * @param rootId 根节点
	 */
	public abstract void sortList(List<T> newList, List<T> sourceList, String rootId);

}