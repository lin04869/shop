package com.ouc.lenovoshop.utils.generate;
import java.util.List;
public class Table {
    private String tableName;
    private String tableComment;
    private List<TableColumn> tableColumns;
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTableComment() {
        return tableComment;
    }
    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }
    public List<TableColumn> getTableColumns() {
        return tableColumns;
    }
    public void setTableColumns(List<TableColumn> tableColumns) {
        this.tableColumns = tableColumns;
    }
}
