package org.jeecf.engine.mysql.model.update;

import org.jeecf.engine.mysql.model.BaseTableColumn;
import org.jeecf.engine.mysql.utils.JniValidate;

/**
 * 更新表列表
 * 
 * @author jianyiming
 *
 */
public class UpdateTableColumn extends BaseTableColumn {

    protected UpdateTableColumn() {
    }

    public static class Builder {

        public static UpdateTableColumn build(String name, String columnName) {
            UpdateTableColumn updateTableColumn = new UpdateTableColumn();
            updateTableColumn.setColumnName(JniValidate.columnValidate(columnName));
            updateTableColumn.setName(name);
            return updateTableColumn;
        }
    }

}
