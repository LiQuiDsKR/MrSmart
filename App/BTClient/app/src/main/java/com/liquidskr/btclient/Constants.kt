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
        //BLUETOOTH_DISCONNECTED,
        BLUETOOTH_IO_EXCEPTION,
        DATABASE_DEFAULT_EXCEPTION,
        NO_QUERY_RESULT,
        DATABASE_INSERT_EXCEPTION,
        BLUETOOTH_CONNECTION_RETRY_FAILED,
    }
    enum class BluetoothMessageType(val processMessage:String) {
        MEMBERSHIP_ALL("사원 기준정보 불러오는 중..."),
        MEMBERSHIP_ALL_COUNT("전체 사원 정보 크기 확인 중..."),
        TOOL_ALL("공기구 기준정보 불러오는 중..."),
        TOOL_ALL_COUNT("전체 공기구 정보 크기 확인 중..."),

        //TODO("이 아래로 메시지 변경 해야함")

        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX("사원 정보 불러오는 중..."),
        RENTAL_SHEET_PAGE_BY_MEMBERSHIP("사원 정보 불러오는 중..."),
        RETURN_SHEET_PAGE_BY_MEMBERSHIP("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_FORM("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_APPROVE("사원 정보 불러오는 중..."),
        RETURN_SHEET_FORM("사원 정보 불러오는 중..."),
        TOOLBOX_TOOL_LABEL_FORM("사원 정보 불러오는 중..."),
        RETURN_SHEET_REQUEST("사원 정보 불러오는 중..."),
        TAG_FORM("사원 정보 불러오는 중..."),
        TOOLBOX_TOOL_LABEL("사원 정보 불러오는 중..."),
        TAG_LIST("사원 정보 불러오는 중..."),
        TAG_ALL("사원 정보 불러오는 중..."),
        TOOLBOX_TOOL_LABEL_ALL("사원 정보 불러오는 중..."),
        TAG_GROUP("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_BY_TAG("사원 정보 불러오는 중..."), TAG_ALL_COUNT("사원 정보 불러오는 중..."),
        TOOLBOX_TOOL_LABEL_ALL_COUNT("사원 정보 불러오는 중..."),
        TAG("사원 정보 불러오는 중..."), RENTAL_REQUEST_SHEET_APPROVE_STANDBY("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_FORM_STANDBY("사원 정보 불러오는 중..."),
        RETURN_SHEET_FORM_STANDBY("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_CANCEL("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT("사원 정보 불러오는 중..."),
        RENTAL_REQUEST_SHEET_APPLY("사원 정보 불러오는 중..."),
        TAG_AND_TOOLBOX_TOOL_LABEL_FORM("사원 정보 불러오는 중..."),
        TAG_AND_TOOLBOX_TOOL_LABEL("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT("사원 정보 불러오는 중..."),
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL("사원 정보 불러오는 중..."),
        TOOLBOX_ALL("사원 정보 불러오는 중..."),

        TEST("사원 정보 불러오는 중..."),

        // 연결 상태 확인용. CommunicationHandler에서 사용.
        HI("heartBeat set")

    }


    const val BACK_BUTTON_DOUBLE_PRESS_CHECK_INTERVAL = 2000 //2초 안에 백버튼 두번 누르면 앱 종료
    const val MEMBERSHIP_PAGE_SIZE = 10 // 한 페이지당 처리할 사원정보 수
    const val COMMUNICATION_TIMEOUT = 3000 //3초
    const val INITIAL_MESSAGE_DELAY = 10000L // 최초 연결 후 메시지는 최소 100밀리초 이후에 전송.
    const val VALIDCHECK_INTERVAL = 3000L // 3초 간격으로 연결상태 확인 ( 메시지 통신 중 타임아웃 체크용 )
    const val HEARTBEAT_INTERVAL = 60000L //  1분 간격으로 연결상태 확인 ( 서버 리소스 체크용 )
    const val BLUETOOTH_MAX_CHUNK_LENGTH = 1024 // 블루투스 데이터 통신 간 1회 처리 바이트 수
    const val INTEGER_BYTE_SIZE = 4
    const val REQUEST_CODE = 123 // permissionManager에서 사용.
    const val BLUETOOTH_MAX_RECONNECT_ATTEMPT = 30 // 블루투스 연결 실패 시 자동 재접속 시도 횟수. communicationHandler에서 사용.
    const val BLUETOOTH_RECONNECT_INTERVAL = 500L // 500밀리초 간격으로 재접속 시도.


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