package com.liquidskr.btclient

import android.net.http.UrlRequest.Status.CONNECTING
import android.net.wifi.p2p.WifiP2pDevice.CONNECTED
import java.lang.reflect.Type

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
    enum class BluetoothMessageType(val processMessage:String,val processEndMessage:String){
        NULL("불러올 수 없는 타입입니다...","NULL"),

        MEMBERSHIP_ALL("사원 기준정보 불러오는 중...",""),
        MEMBERSHIP_ALL_COUNT("전체 사원 정보 크기 확인 중...",""),
        TOOL_ALL("공기구 기준정보 불러오는 중...","기준정보를 전부 불러왔습니다."),//TODO 기준정보 끝이 여기가 맞아?
        TOOL_ALL_COUNT("전체 공기구 정보 크기 확인 중...",""),
        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX("대여 신청 정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_PAGE_BY_TOOLBOX_COUNT("대여 신청 정보 크기 확인 중...",""),

        //TODO("이 아래로 메시지 변경 해야함")

        RENTAL_REQUEST_SHEET_LIST_BY_TOOLBOX("대여 신청 정보 불러오는 중...",""),
        RENTAL_SHEET_PAGE_BY_MEMBERSHIP("대여 신청 정보 불러오는 중...",""),
        RETURN_SHEET_PAGE_BY_MEMBERSHIP("그없",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP("미반납 정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_LIST_BY_MEMBERSHIP("미반납 정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX("미반납 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_LIST_BY_TOOLBOX("미반납 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_FORM("대여 신청 처리 중...",""),
        RENTAL_REQUEST_SHEET_APPROVE("대여 승인 처리 중...","정상적으로 처리되었습니다."),
        RETURN_SHEET_FORM("반납 승인 처리 중...",""),
        TOOLBOX_TOOL_LABEL_FORM("선반 QR코드 등록 중...",""),
        RETURN_SHEET_REQUEST("반납 신청 처리 중...",""),
        TAG_FORM("개별 QR코드 등록 중...",""),
        TOOLBOX_TOOL_LABEL("선반 QR코드 정보 불러오는 중...",""),
        TAG_LIST("개별 QR코드 정보 불러오는 중...",""),
        TAG_ALL("개별 QR코드 정보 불러오는 중...",""),
        TOOLBOX_TOOL_LABEL_ALL("선반 QR코드 정보 불러오는 중...",""),
        TAG_GROUP("개별 QR코드 목록 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_BY_TAG("미반납 정보 불러오는 중...",""),
        TAG_ALL_COUNT("사원 정보 불러오는 중...",""),
        TOOLBOX_TOOL_LABEL_ALL_COUNT("선반 QR코드 정보 크기 확인 중...",""),
        TAG("QR 확인 중...",""),
        RENTAL_REQUEST_SHEET_APPROVE_STANDBY("사원 정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_FORM_STANDBY("정보 불러오는 중...",""),
        RETURN_SHEET_FORM_STANDBY("정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_CANCEL("정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP("정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_MEMBERSHIP_COUNT("정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_READY_PAGE_BY_MEMBERSHIP_COUNT("정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_BY_TOOLBOX_COUNT("정보 불러오는 중...",""),
        RENTAL_REQUEST_SHEET_APPLY("정보 불러오는 중...",""),
        TAG_AND_TOOLBOX_TOOL_LABEL_FORM("정보 불러오는 중...",""),
        TAG_AND_TOOLBOX_TOOL_LABEL("정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL_COUNT("정보 불러오는 중...",""),
        OUTSTANDING_RENTAL_SHEET_PAGE_ALL("정보 불러오는 중...",""),
        TOOLBOX_ALL("정비실 정보 불러오는 중...",""),

        TEST("사원 정보 불러오는 중...",""),

        DATA_TYPE_EXCEPTION("오류",""),
        DATA_SEMANTIC_EXCEPTION("잘못된 입력",""),
        UNKNOWN_EXCEPTION("오류",""),

        // 연결 상태 확인용. CommunicationHandler에서 사용.
        HI("heartBeat set","")

/*
        // 각 함수들도 정의해줍니다.
        fun membershipAll(size: Int, index: Int): String {
            return MEMBERSHIP_ALL.toString() + ",{\"size\":${size},\"page\":${index}}"
        }

        fun membershipAllCount(): String {
            return MEMBERSHIP_ALL_COUNT.toString()
        }

        fun toolAll(size: Int, index: Int): String {
            return TOOL_ALL.toString() + ",{\"size\":${size},\"page\":${index}}"
        }

        fun toolAllCount(): String {
            return TOOL_ALL_COUNT.toString()
        }
        fun rentalRequestSheetPageByToolbox() {
            // 함수 구현
        }

        fun rentalRequestSheetListByToolbox() {
            // 함수 구현
        }

        fun rentalSheetPageByMembership() {
            // 함수 구현
        }

        fun returnSheetPageByMembership() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageByMembership() {
            // 함수 구현
        }

        fun outstandingRentalSheetListByMembership() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageByToolbox() {
            // 함수 구현
        }

        fun outstandingRentalSheetListByToolbox() {
            // 함수 구현
        }

        fun rentalRequestSheetForm() {
            // 함수 구현
        }

        fun rentalRequestSheetApprove() {
            // 함수 구현
        }

        fun returnSheetForm() {
            // 함수 구현
        }

        fun toolboxToolLabelForm() {
            // 함수 구현
        }

        fun returnSheetRequest() {
            // 함수 구현
        }

        fun tagForm() {
            // 함수 구현
        }

        fun toolboxToolLabel() {
            // 함수 구현
        }

        fun tagList() {
            // 함수 구현
        }

        fun tagAll() {
            // 함수 구현
        }

        fun toolboxToolLabelAll() {
            // 함수 구현
        }

        fun tagGroup() {
            // 함수 구현
        }

        fun outstandingRentalSheetByTag() {
            // 함수 구현
        }

        fun tagAllCount() {
            // 함수 구현
        }

        fun toolboxToolLabelAllCount() {
            // 함수 구현
        }

        fun tag() {
            // 함수 구현
        }

        fun rentalRequestSheetApproveStandby() {
            // 함수 구현
        }

        fun rentalRequestSheetFormStandby() {
            // 함수 구현
        }

        fun returnSheetFormStandby() {
            // 함수 구현
        }

        fun rentalRequestSheetCancel() {
            // 함수 구현
        }

        fun rentalRequestSheetReadyPageByMembership() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageByMembershipCount() {
            // 함수 구현
        }

        fun rentalRequestSheetReadyPageByMembershipCount() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageByToolboxCount() {
            // 함수 구현
        }

        fun rentalRequestSheetPageByToolboxCount() {
            // 함수 구현
        }

        fun rentalRequestSheetApply() {
            // 함수 구현
        }

        fun tagAndToolboxToolLabelForm() {
            // 함수 구현
        }

        fun tagAndToolboxToolLabel() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageAllCount() {
            // 함수 구현
        }

        fun outstandingRentalSheetPageAll() {
            // 함수 구현
        }

        fun toolboxAll() {
            // 함수 구현
        }

        fun test() {
            // 함수 구현
        }

 */
    }

    enum class ConnectionState(val value: Int)
    {
        DISCONNECTED(0),
        CONNECTED(1),
        CONNECTING(2)
    }


    const val BACK_BUTTON_DOUBLE_PRESS_CHECK_INTERVAL = 2000 //2초 안에 백버튼 두번 누르면 앱 종료
    const val MEMBERSHIP_PAGE_SIZE = 10 // 한 페이지당 처리할 사원정보 수
    const val TOOL_PAGE_SIZE = 10 // 한 페이지당 처리할 공기구 수
    const val SHEET_PAGE_SIZE = 1 // 한 페이지당 처리할 시트(전표) 수
    const val COMMUNICATION_TIMEOUT = 3000 //송신 3초 후 타임아웃
    const val INITIAL_MESSAGE_DELAY = 10000L // 최초 연결 후 메시지는 최소 100밀리초 이후에 전송.
    const val VALIDCHECK_INTERVAL = 500L //0.5초 간격 연결상태 확인, 재접속 시도.
    const val HEARTBEAT_INTERVAL = 60000L //  1분 간격으로 연결상태 확인 ( 서버 리소스 체크용 )
    const val BLUETOOTH_MAX_CHUNK_LENGTH = 1024 // 블루투스 데이터 통신 간 1회 처리 바이트 수
    const val INTEGER_BYTE_SIZE = 4
    const val REQUEST_CODE = 123 // permissionManager에서 사용.
    const val BLUETOOTH_MAX_RECONNECT_ATTEMPT = 10 // 블루투스 연결 실패 시 자동 재접속 시도 횟수. communicationHandler에서 사용.


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