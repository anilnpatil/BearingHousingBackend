package TVS_SFL.BearingHousingBackend.constants;

public final class SqlQueries {

    private SqlQueries() { }

    public static final String SELECT_ALL = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME;

    public static final String SELECT_BY_ID = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_ID + " = ?";
        public static final String SELECT_BY_BARCODE = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_BARCODE + " = ? LIMIT 1";
    public static final String SELECT_BY_BARCODE_ARCHIVE = "SELECT * FROM " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_BARCODE + " = ? LIMIT 1";

    public static final String SELECT_OLD_RECORDS_FOR_ARCHIVE =
            "SELECT * FROM " + BearingHousingConstants.TABLE_NAME +
            " WHERE production_date_time < NOW() - INTERVAL '? years'";

    public static final String INSERT_INTO_ARCHIVE =
            "INSERT INTO " + BearingHousingConstants.ARCHIVE_TABLE_NAME + " (" +
            "id, barcode, operator_name, shift, sku, number_of_process, cycle_start_time, " +
            "p1_before_glue_status, p1_after_glue_status,p1_tox_start_load, p1_tox_mid_load, p1_tox_end_load, p1_tox_start_displacement, " +
            "p1_tox_mid_displacement, p1_tox_end_displacement, p1_graph_status, " +
            "p2_before_glue_status, p2_after_glue_status, p2_tox_start_load, p2_tox_mid_load, p2_tox_end_load, p2_tox_start_displacement, " +
            "p2_tox_mid_displacement, p2_tox_end_displacement, p2_graph_status, cup_consumed, " +
            "final_status, ok_count, not_ok_count, total_part_count, cycle_time, production_date_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String DELETE_ARCHIVED_RECORDS =
            "DELETE FROM " + BearingHousingConstants.TABLE_NAME +
            " WHERE production_date_time < NOW() - INTERVAL '? years'";

    public static final String INSERT = "INSERT INTO " + BearingHousingConstants.TABLE_NAME + " (" +
            BearingHousingConstants.COLUMN_ID + ", " +
            BearingHousingConstants.COLUMN_BARCODE + ", " +
            BearingHousingConstants.COLUMN_CYCLE_START_TIME + ", " +
            BearingHousingConstants.COLUMN_CYCLE_TIME + ", " +
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ", " +
            BearingHousingConstants.COLUMN_SKU + ", " +
            BearingHousingConstants.COLUMN_SHIFT + ", " +
            BearingHousingConstants.COLUMN_NUMBER_OF_PROCESS + ", " +
            BearingHousingConstants.COLUMN_P1_BEFORE_GLUE_STATUS + ", " +
            BearingHousingConstants.COLUMN_P1_AFTER_GLUE_STATUS + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_START_LOAD + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_MID_LOAD + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_END_LOAD + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_START_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_MID_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P1_TOX_END_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P1_GRAPH_STATUS + ", " +
            BearingHousingConstants.COLUMN_P2_BEFORE_GLUE_STATUS + ", " +
            BearingHousingConstants.COLUMN_P2_AFTER_GLUE_STATUS + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_START_LOAD + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_MID_LOAD + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_END_LOAD + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_START_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_MID_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P2_TOX_END_DISPLACEMENT + ", " +
            BearingHousingConstants.COLUMN_P2_GRAPH_STATUS + ", " +
            BearingHousingConstants.COLUMN_CUP_CONSUMED + ", " +
            BearingHousingConstants.COLUMN_FINAL_STATUS + ", " +
            BearingHousingConstants.COLUMN_TOTAL_PART_COUNT + ", " +
            BearingHousingConstants.COLUMN_OK_COUNT + ", " +
            BearingHousingConstants.COLUMN_NOT_OK_COUNT + ", " +
            BearingHousingConstants.COLUMN_OPERATOR_NAME + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SELECT_LAST_INSERTED = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " ORDER BY " + BearingHousingConstants.COLUMN_ID + " DESC LIMIT 1";

    public static final String UPDATE_BY_ID = "UPDATE " + BearingHousingConstants.TABLE_NAME + " SET " +
            BearingHousingConstants.COLUMN_BARCODE + " = ?, " + 
            BearingHousingConstants.COLUMN_CYCLE_START_TIME + " = ?, " +             
            BearingHousingConstants.COLUMN_CYCLE_TIME + " = ?, " +
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + " = ?, " + BearingHousingConstants.COLUMN_SKU + " = ?, " +
            BearingHousingConstants.COLUMN_SHIFT + " = ?, " + 
            BearingHousingConstants.COLUMN_NUMBER_OF_PROCESS + " = ?, " +
            BearingHousingConstants.COLUMN_P1_BEFORE_GLUE_STATUS + " = ?, " + BearingHousingConstants.COLUMN_P1_AFTER_GLUE_STATUS + " = ?, " +
            BearingHousingConstants.COLUMN_P1_TOX_START_LOAD + " = ?, " + BearingHousingConstants.COLUMN_P1_TOX_MID_LOAD + " = ?, " + BearingHousingConstants.COLUMN_P1_TOX_END_LOAD + " = ?, " +
            BearingHousingConstants.COLUMN_P1_TOX_START_DISPLACEMENT + " = ?, " + BearingHousingConstants.COLUMN_P1_TOX_MID_DISPLACEMENT + " = ?, " + BearingHousingConstants.COLUMN_P1_TOX_END_DISPLACEMENT + " = ?, " +
            BearingHousingConstants.COLUMN_P1_GRAPH_STATUS + " = ?, " +
            BearingHousingConstants.COLUMN_P2_BEFORE_GLUE_STATUS + " = ?, " + BearingHousingConstants.COLUMN_P2_AFTER_GLUE_STATUS + " = ?, " +
            BearingHousingConstants.COLUMN_P2_TOX_START_LOAD + " = ?, " + BearingHousingConstants.COLUMN_P2_TOX_MID_LOAD + " = ?, " + BearingHousingConstants.COLUMN_P2_TOX_END_LOAD + " = ?, " +
            BearingHousingConstants.COLUMN_P2_TOX_START_DISPLACEMENT + " = ?, " + BearingHousingConstants.COLUMN_P2_TOX_MID_DISPLACEMENT + " = ?, " + BearingHousingConstants.COLUMN_P2_TOX_END_DISPLACEMENT + " = ?, " +
            BearingHousingConstants.COLUMN_P2_GRAPH_STATUS + " = ?, " +
            BearingHousingConstants.COLUMN_CUP_CONSUMED + " = ?, " + BearingHousingConstants.COLUMN_FINAL_STATUS + " = ?, " + BearingHousingConstants.COLUMN_TOTAL_PART_COUNT + " = ?, " +
            BearingHousingConstants.COLUMN_OK_COUNT + " = ?, " + 
            BearingHousingConstants.COLUMN_NOT_OK_COUNT + " = ?, " +
            BearingHousingConstants.COLUMN_OPERATOR_NAME + " = ? WHERE " + BearingHousingConstants.COLUMN_ID + " = ?";

    public static final String DELETE_BY_ID = "DELETE FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_ID + " = ?";

    public static final String SELECT_BY_SKU = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_SKU + " = ?";

    public static final String SELECT_BY_OPERATOR = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_OPERATOR_NAME + " = ?";

    public static final String SELECT_BY_SHIFT = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_SHIFT + " = ?";

        public static final String SELECT_LATEST_BY_SHIFT = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_SHIFT + " = ? ORDER BY " + BearingHousingConstants.COLUMN_ID + " DESC LIMIT 1";

        public static final String SELECT_LATEST = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " ORDER BY " + BearingHousingConstants.COLUMN_ID + " DESC LIMIT 1";

    public static final String SELECT_BY_FINAL_STATUS = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + " WHERE " + BearingHousingConstants.COLUMN_FINAL_STATUS + " = ?";

    // Dynamic date range filters
    public static final String SELECT_BY_DATE_RANGE = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? ORDER BY " + 
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + " DESC LIMIT ? OFFSET ?";

    public static final String SELECT_BY_DATE_RANGE_WITH_SHIFT = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SHIFT + " = ? ORDER BY " + 
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + " DESC LIMIT ? OFFSET ?";

    public static final String SELECT_BY_DATE_RANGE_WITH_SKU = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SKU + " = ? ORDER BY " + 
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + " DESC LIMIT ? OFFSET ?";

    public static final String SELECT_BY_DATE_RANGE_WITH_SHIFT_AND_SKU = "SELECT * FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SHIFT + " = ? AND " + 
            BearingHousingConstants.COLUMN_SKU + " = ? ORDER BY " + 
            BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + " DESC LIMIT ? OFFSET ?";

    public static final String COUNT_BY_DATE_RANGE = "SELECT COUNT(*) FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ?";

    public static final String COUNT_BY_DATE_RANGE_WITH_SHIFT = "SELECT COUNT(*) FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SHIFT + " = ?";

    public static final String COUNT_BY_DATE_RANGE_WITH_SKU = "SELECT COUNT(*) FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SKU + " = ?";

    public static final String COUNT_BY_DATE_RANGE_WITH_SHIFT_AND_SKU = "SELECT COUNT(*) FROM " + BearingHousingConstants.TABLE_NAME + 
            " WHERE DATE(" + BearingHousingConstants.COLUMN_PRODUCTION_DATETIME + ") BETWEEN ? AND ? AND " + 
            BearingHousingConstants.COLUMN_SHIFT + " = ? AND " + 
            BearingHousingConstants.COLUMN_SKU + " = ?";

}
