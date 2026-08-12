package TVS_SFL.BearingHousingBackend.constants;

public class BearingHousingConstants {

    // API Endpoints
    public static final String PARAMETER_API_BASE_PATH = "/api/production-data";
    public static final String IMAGE_API_BASE_PATH = "/api/bearing-housing";
    public static final String GET_ALL_ENDPOINT = "";
    public static final String GET_BY_BARCODE_ENDPOINT = "/barcode/{barcode}";
    public static final String CREATE_ENDPOINT = "";
    public static final String UPDATE_ENDPOINT = "/{id}";
    public static final String DELETE_ENDPOINT = "/{id}";
    public static final String GET_BY_SKU_ENDPOINT = "/sku/{sku}";
    public static final String GET_BY_OPERATOR_ENDPOINT = "/operator/{operatorName}";
    public static final String GET_BY_STATUS_ENDPOINT = "/status/{status}";
    public static final String GET_BY_BARCODE_WITH_PHOTOS_ENDPOINT = "/barcode/{barcode}/with-photos";
    public static final String GET_PHOTO_ENDPOINT = "/barcode/{barcode}/photo/{fileName:.+}";
    public static final String GET_BY_SHIFT_ENDPOINT = "/shift";
    public static final String GET_BY_DATE_RANGE_ENDPOINT = "/daterange";

    // Final Status Constants
    public static final Integer STATUS_OK = 1;
    public static final Integer STATUS_NOT_OK = 0;
    public static final Integer STATUS_PENDING = 2;

    // Glue Status Constants
    public static final Integer GLUE_STATUS_PENDING = 0;
    public static final Integer GLUE_STATUS_COMPLETED = 1;
    public static final Integer GLUE_STATUS_FAILED = 2;

    // Error Messages
    public static final String RECORD_NOT_FOUND = "Record not found";
    public static final String INVALID_REQUEST = "Invalid request data";
    public static final String DATABASE_ERROR = "Database operation failed";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";

    // Database Constants
    public static final String TABLE_NAME = "bearing_housing_production_data";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_NUMBER_OF_PROCESS = "number_of_process";
    public static final String COLUMN_CYCLE_START_TIME = "cycle_start_time";
    // public static final String COLUMN_CYCLE_END_TIME = "cycle_end_time";
    public static final String COLUMN_CYCLE_TIME = "cycle_time";
    public static final String COLUMN_SKU = "sku";
    public static final String COLUMN_BEFORE_GLUE_STATUS = "before_glue_status";
    public static final String COLUMN_AFTER_GLUE_STATUS = "after_glue_status";
    public static final String COLUMN_TOX_LOAD_MAX = "tox_load_max";
    public static final String COLUMN_TOX_LOAD_MIN = "tox_load_min";
    public static final String COLUMN_TOX_LOAD_ACTUAL_VALUE = "tox_load_actual";
    public static final String COLUMN_TOX_DISPLACEMENT_MAX = "tox_displacement_max";
    public static final String COLUMN_TOX_DISPLACEMENT_MIN = "tox_displacement_min";
    public static final String COLUMN_TOX_DISPLACEMENT_ACTUAL_VALUE = "tox_displacement_actual";
    public static final String COLUMN_FINAL_STATUS = "final_status";
    public static final String COLUMN_PRODUCTION_DATETIME = "production_date_time";
    public static final String COLUMN_SHIFT = "shift";    
    public static final String COLUMN_P1_BEFORE_GLUE_STATUS = "p1_before_glue_status";
    public static final String COLUMN_P1_AFTER_GLUE_STATUS = "p1_after_glue_status";
    public static final String COLUMN_P1_TOX_LOAD_MAX = "p1_tox_load_max";
    public static final String COLUMN_P1_TOX_LOAD_MIN = "p1_tox_load_min";
    public static final String COLUMN_P1_TOX_LOAD_ACTUAL_VALUE = "p1_tox_load_actual";
    public static final String COLUMN_P1_TOX_DISPLACEMENT_MAX = "p1_tox_displacement_max";
    public static final String COLUMN_P1_TOX_DISPLACEMENT_MIN = "p1_tox_displacement_min";
    public static final String COLUMN_P1_TOX_DISPLACEMENT_ACTUAL_VALUE = "p1_tox_displacement_actual";
    public static final String COLUMN_P1_GRAPH_STATUS = "p1_graph_status";
    public static final String COLUMN_P2_BEFORE_GLUE_STATUS = "p2_before_glue_status";
    public static final String COLUMN_P2_AFTER_GLUE_STATUS = "p2_after_glue_status";
    public static final String COLUMN_P2_TOX_LOAD_MAX = "p2_tox_load_max";
    public static final String COLUMN_P2_TOX_LOAD_MIN = "p2_tox_load_min";
    public static final String COLUMN_P2_TOX_LOAD_ACTUAL_VALUE = "p2_tox_load_actual";
    public static final String COLUMN_P2_TOX_DISPLACEMENT_MAX = "p2_tox_displacement_max";
    public static final String COLUMN_P2_TOX_DISPLACEMENT_MIN = "p2_tox_displacement_min";
    public static final String COLUMN_P2_TOX_DISPLACEMENT_ACTUAL_VALUE = "p2_tox_displacement_actual";
    public static final String COLUMN_P2_GRAPH_STATUS = "p2_graph_status";
    public static final String COLUMN_TOTAL_PART_COUNT = "total_part_count";
    public static final String COLUMN_OK_COUNT = "ok_count";
    public static final String COLUMN_NOT_OK_COUNT = "not_ok_count";
    public static final String COLUMN_OPERATOR_NAME = "operator_name";

    private BearingHousingConstants() {
        // Private constructor to prevent instantiation
    }
}
