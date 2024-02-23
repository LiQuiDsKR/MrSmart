package com.liquidskr.btclient

object Constants {
    enum class ExceptionType{
        BLUETOOTH_DEFAULT_EXCEPTION,
        BLUETOOTH_PERMISSION_MISSING,
        BLUETOOTH_NO_PAIRED_DEVICE,
        BLUETOOTH_CONNECTION_FAILED,
        DATABASE_HELPER_NOT_INITIALIZED,
        SQLITE_EXCEPTION,
        EXTERNAL_DATABASE_PERMISSION_MISSING,
        BLUETOOTH_DISCONNECTED,
        BLUETOOTH_IO_EXCEPTION,
        DATABASE_DEFAULT_EXCEPTION,
        NO_QUERY_RESULT,
        DATABASE_INSERT_EXCEPTION,
    }
    enum class BluetoothMessageType {
        MEMBERSHIP_ALL,
        MEMBERSHIP_ALL_COUNT,
        TOOL_ALL,
        TOOL_ALL_COUNT,
        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX,
        RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX,
        RENTAL_SHEET_PAGE_BY_MEMBERSHIP,
        RETURN_SHEET_PAGE_BY_MEMBERSHIP,
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP,
        OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP,
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX,
        OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX,
        RENTAL_REQUEST_SHEET_FORM,
        RENTAL_REQUEST_SHEET_APPROVE,
        RETURN_SHEET_FORM,
        TOOLBOX_TOOL_LABEL_FORM,
        RETURN_SHEET_REQUEST,
        TAG_FORM,
        TOOLBOX_TOOL_LABEL,
        TAG_LIST,
        TAG_ALL,
        TOOLBOX_TOOL_LABEL_ALL,
        TAG_GROUP,
        OUTSTANDING_RENTAL_SHEET_BY_TAG, TAG_ALL_COUNT,
        TOOLBOX_TOOL_LABEL_ALL_COUNT,
        TAG, RENTAL_REQUEST_SHEET_APPROVE_STANDBY,
        RENTAL_REQUEST_SHEET_FORM_STANDBY,
        RETURN_SHEET_FORM_STANDBY,
        RENTAL_REQUEST_SHEET_CANCEL,
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP,
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT,
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT,
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT,
        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT,
        RENTAL_REQUEST_SHEET_APPLY,
        TAG_AND_TOOLBOX_TOOL_LABEL_FORM,
        TAG_AND_TOOLBOX_TOOL_LABEL,
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT,
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL,
        TOOLBOX_ALL,

        TEST,

    }

    const val MEMBERSHIP_PAGE_SIZE = 10
    const val COMMUNICATION_TIMEOUT = 10000 //10초
    const val BLUETOOTH_MAX_CHUNK_LENGTH = 1024
    const val INTEGER_BYTE_SIZE = 4


    // database 테이블,컬럼명
    // DatabaseHelper가 사용함.

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "StandardInfo.db"

    const val TABLE_Membership_NAME = "Membership"
    const val COLUMN_Membership_ID = "id"
    const val COLUMN_Membership_CODE = "code"
    const val COLUMN_Membership_PASSWORD = "password"
    const val COLUMN_Membership_NAME = "name"
    const val COLUMN_Membership_PART = "part"
    const val COLUMN_Membership_SUBPART = "subpart"
    const val COLUMN_Membership_MAINPART = "mainpart"
    const val COLUMN_Membership_ROLE = "role"
    const val COLUMN_Membership_EMPLOYMENT_STATE = "employment_state"

    const val TABLE_TOOL_NAME = "Tool"
    const val COLUMN_TOOL_ID = "tool_id"
    const val COLUMN_TOOL_MAINGROUP = "tool_maingroup"
    const val COLUMN_TOOL_SUBGROUP = "tool_subgroup"
    const val COLUMN_TOOL_CODE = "tool_code"
    const val COLUMN_TOOL_KRNAME = "tool_krname"
    const val COLUMN_TOOL_ENGNAME = "tool_engname"
    const val COLUMN_TOOL_SPEC = "tool_spec"
    const val COLUMN_TOOL_UNIT = "tool_unit"
    const val COLUMN_TOOL_PRICE = "tool_price"
    const val COLUMN_TOOL_REPLACEMENTCYCLE = "tool_replacementcycle"
    const val COLUMN_TOOL_BUYCODE = "tool_buycode"

    const val TABLE_STANDBY_NAME = "Standby"
    const val COLUMN_STANDBY_ID = "standby_id"
    const val COLUMN_STANDBY_JSON = "standby_json"
    const val COLUMN_STANDBY_TYPE = "standby_type"
    const val COLUMN_STANDBY_STATUS = "standby_status"
    const val COLUMN_STANDBY_DETAIL = "standby_detail"

    const val TABLE_TBT_NAME = "ToolboxToolLabel"
    const val COLUMN_TBT_ID = "tbt_id"
    const val COLUMN_TBT_TOOLBOX_ID = "tbt_toolboxid"
    const val COLUMN_TBT_LOCATION = "tbt_location"
    const val COLUMN_TBT_TOOL_ID = "tbt_toolid"
    const val COLUMN_TBT_QRCODE = "tbt_qrcode"

    const val TABLE_TAG_NAME = "Tag"
    const val COLUMN_TAG_ID = "tag_id"
    const val COLUMN_TAG_MACADDRESS = "tag_macaddress"
    const val COLUMN_TAG_TOOL_ID = "tag_toolid"
    const val COLUMN_TAG_TAGGROUP = "tag_taggroup"

    const val TABLE_RENTALSHEET_NAME = "RentalSheet"
    const val COLUMN_RENTALSHEET_ID = "rentalSheet_id"
    const val COLUMN_RENTALSHEET_WOKRER = "rentalSheet_workerid"
    const val COLUMN_RENTALSHEET_LEADER = "rentalSheet_leaderid"
    const val COLUMN_RENTALSHEET_TIMESTAMP = "rentalSheet_timestamp"
    const val COLUMN_RENTALSHEET_TOOLLIST = "rentalSheet_toolList"

    const val TABLE_DEVICE_NAME = "Devices"
    const val COLUMN_DEVICE_ID = "device_id"
    const val COLUMN_DEVICE_NAME = "device_name"

    const val TABLE_TOOLBOX_NAME = "Toolbox"
    const val COLUMN_TOOLBOX_ID = "toolbox_id"
    const val COLUMN_TOOLBOX_TOOLBOX_ID = "toolbox_toolbox_id"
    const val COLUMN_TOOLBOX_NAME = "toolbox_name"


    const val TABLE_OUTSTANDING_NAME = "OutstandingRentalSheet"
    const val COLUMN_OUTSTANDING_ID = "outstanding_id"
    const val COLUMN_OUTSTANDING_RENTALSHEET = "outstanding_rentalSheet"
    const val COLUMN_OUTSTANDING_TOTALCOUNT = "outstanding_count"
    const val COLUMN_OUTSTANDING_OUTSTANDINGCOUNT = "outstanding_outstandingCount"
    const val COLUMN_OUTSTANDING_STATUS = "outstanding_status"
    const val COLUMN_OUTSTANDING_JSON = "outstanding_json"
}