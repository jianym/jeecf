package org.jeecf.manager.gen.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jeecf.manager.engine.model.SelectTable;
import org.jeecf.manager.engine.model.SelectTableColumn;
import org.jeecf.manager.gen.language.go.model.GoCommonTable;
import org.jeecf.manager.gen.language.go.model.GoTable;
import org.jeecf.manager.gen.language.go.model.GoTableColumn;
import org.jeecf.manager.gen.language.go.utils.HelperUtils;
import org.jeecf.manager.module.template.model.domain.GenTable;
import org.jeecf.manager.module.template.model.result.GenTableResult;
import org.springframework.beans.BeanUtils;
/**
 * Go构建者
 * @author jianyiming
 *
 */
public class GoBuilder  extends AbstractLanguageBuilder {
	
	private GoTable goTable = null;
	
	@Override
	public GoTable build(String tableName) {
		GenTable genTable = (GenTable) super.build(tableName);
		GoTable genTableG = new GoTable();
		BeanUtils.copyProperties(genTable, genTableG);
		genTableG.setGenTableColumns(HelperUtils.toColumn(genTable.getGenTableColumns()));
		GenTable parentTable = AbstractLanguageBuilder.genTableFacade.findParentTable(genTable.getParentTableId()).getData();
		GoCommonTable parentCommonTable = new GoCommonTable();
		if(parentTable != null) {
			BeanUtils.copyProperties(parentTable, parentCommonTable);
			parentCommonTable.setGenTableColumns(HelperUtils.toColumn(parentTable.getGenTableColumns()));
		}
		List<GenTableResult> tableResultList = AbstractLanguageBuilder.genTableFacade.findChildTables(genTable.getId()).getData();
		List<GoCommonTable> childTables = new ArrayList<GoCommonTable>();
		if(CollectionUtils.isNotEmpty(tableResultList)) {
			tableResultList.forEach(tableResult -> {
				GoCommonTable childTable = new GoCommonTable();
				BeanUtils.copyProperties(tableResult, childTable);
				childTable.setGenTableColumns(HelperUtils.toColumn(tableResult.getGenTableColumns()));
				childTables.add(childTable);
			});
		}
		genTableG.setParent(parentCommonTable);
		genTableG.setChildList(childTables);
		this.goTable = genTableG;
		return genTableG;
	}

	@Override
	public String getData(String sql) {
		if(this.goTable != null) {
			SelectTable selectTable = new SelectTable();
			List<SelectTableColumn> columnList = new ArrayList<>();
			List<GoTableColumn> tableColumnList = this.goTable.getGenTableColumns();
			selectTable.setName(this.goTable.getClassName());
			selectTable.setTableName(this.goTable.getName());
			tableColumnList.forEach(tableColumn->{
				SelectTableColumn column = new SelectTableColumn();
				column.setName(tableColumn.getField());
				column.setColumnName(tableColumn.getName());
				columnList.add(column);
			});
			selectTable.setColumnList(columnList);
			return targetTableFacade.selectTable(selectTable).getData();
		}
		return null;
	}

}
