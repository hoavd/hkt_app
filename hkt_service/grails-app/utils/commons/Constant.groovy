package commons

import java.util.regex.Pattern

class Constant {
    public static final Integer TIMEZONE_VN = 7
    public static final String PASSWORD_PATTERN = ~/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?*~$^+=<>]).{8,20}$/
    public static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN)
    public static final String defaultPassWord = "abcde@123#2023"
    public static final String SYSTEM = "SYSTEM"
    public static final String PASS = "PASS"
    public static final String FAIL = "FAIL"
    public static final String YES = "YES"
    public static final String NO = "NO"

    public static final Integer DEAL_EXPRIRE_ACTION_SECOND = 30

    public static final String LANDSCAPE = "LANDSCAPE"
    public static final String PORTRAIT = "PORTRAIT"

    //giao dịch tạo bởi Upload File Excel hay tạo thủ công
    public static final String CREATE_TYPE_DEAL_MANUAL = "MANUAL"
    public static final String CREATE_TYPE_DEAL_EXCEL = "EXCEL"
    public static final String CREATE_TYPE_DEAL_IBMB = "IBMB"

    public static final String CREATE_FW_MANUAL = "MANUAL"
    public static final String CREATE_FW_AUTO = "AUTO"

    public static final String CREATE_CIF_MANUAL = "MANUAL"
    public static final String CREATE_CIF_AUTO = "AUTO"

    //Role group
    public static final String ROLE_GROUP_ADMIN = "ROLE_GROUP_ADMIN"
    public static final String ROLE_GROUP_ADMIN_BU = "ROLE_GROUP_ADMIN_BU"
    public static final String ROLE_GROUP_ADMIN_IT = "ROLE_GROUP_ADMIN_IT"
    public static final String ROLE_GROUP_CLIENT = "ROLE_GROUP_CLIENT"
    public static final String ROLE_GROUP_SALE = "ROLE_GROUP_SALE"
    public static final String ROLE_GROUP_TRADER = "ROLE_GROUP_TRADER"
    //--

    //Currency
    public static final String CURRENCY_VND = "VND"
    public static final String CURRENCY_USD = "USD"
    //


    //Trạng thái phê duyệt model
    public static final String STATUS_APPROVE_MODEL_OPEN = "0"
    public static final String STATUS_APPROVE_MODEL_PENDING = "1"
    public static final String STATUS_APPROVE_MODEL_ACCEPT = "2"
    public static final String STATUS_APPROVE_MODEL_DENIED = "3"

    public static final List STATUS_APPROVE_MODEL = [
            ["code": STATUS_APPROVE_MODEL_OPEN,
             "name": "Khởi tạo"],
            ["code": STATUS_APPROVE_MODEL_PENDING,
             "name": "Chờ phê duyệt"],
            ["code": STATUS_APPROVE_MODEL_ACCEPT,
             "name": "Đã phê duyệt"],
            ["code": STATUS_APPROVE_MODEL_DENIED,
             "name": "Từ chối phê duyệt"],
    ]

    static String findStatusApproveModel(String key) {
        return STATUS_APPROVE_MODEL.find({
            it.code == key
        }).name
    }
    //--

    public static final String SYNC_EXCHANGE_TYPE_RATE = "RATE"
    public static final String SYNC_EXCHANGE_TYPE_SPOT = "SPOT"
    public static final String SYNC_EXCHANGE_TYPE_MONITOR = "MONITOR"

    //component
    public static final String COMPONENT_GROUP_DYNAMIC = "dynamic"
    public static final String COMPONENT_GROUP_FILTER = "filter"

    public static final String COMPONENT_CONTRUCTOR_DEFAULT = "DEFAULT"
    public static final String COMPONENT_CONTRUCTOR_VARIABLE = "VARIABLE"
    //--
    //component_type
    public static final String COMPONENT_TYPE_COMBOBOX = "COMBOBOX"
    public static final String COMPONENT_TYPE_CHECKBOX = "CHECKBOX"
    public static final String COMPONENT_TYPE_NUMBER = "NUMBER"
    public static final String COMPONENT_TYPE_DATE_SHORT = "DATESHORT"
    public static final String COMPONENT_TYPE_TIME = "TIME"
    //--
    //data_type
    public static final String DATA_TYPE_NUMBER = "NUMBER"
    public static final String DATA_TYPE_STRING = "STRING"
    public static final String DATA_TYPE_BOOLEAN = "BOOLEAN"
    //--

    /*Tiêu chí đáp ứng*/
    public static final String CONDITION_DEAL_STATUS_ACCEPT = "ACCEPT"
    public static final String CONDITION_DEAL_STATUS_NO_DATA = "NO_DATA"
    public static final String CONDITION_DEAL_STATUS_REJECT = "REJECT"
    public static final String CONDITION_DEAL_STATUS_RFQ2T = "RFQ2T"
    public static final String CONDITION_DEAL_STATUS_RFQ2S = "RFQ2S"
    public static final String CONDITION_DEAL_STATUS_NOT2K = "NOT2K"
    public static final String CONDITION_DEAL_STATUS_NO_STATIC_PENALTY = "NO_STATIC_PENALTY"
    public static final String CONDITION_DEAL_STATUS_NO_PENALTY = "NO_PENALTY"

    public static final List CONDITION_DEAL_STATUS = [
            ["code": CONDITION_DEAL_STATUS_ACCEPT, "name": "Accept"],
            ["code": CONDITION_DEAL_STATUS_REJECT, "name": "Reject"],
            ["code": CONDITION_DEAL_STATUS_RFQ2T, "name": "Rfq2t"],
            ["code": CONDITION_DEAL_STATUS_RFQ2S, "name": "Rfq2s"],
            ["code": CONDITION_DEAL_STATUS_NOT2K, "name": "Không đồng bộ K+"],
            ["code": CONDITION_DEAL_STATUS_NO_STATIC_PENALTY, "name": "No Static Penalty"],
            ["code": CONDITION_DEAL_STATUS_NO_PENALTY, "name": "No Penalty"],
    ]
    //--

    /*Trạng thái deal*/
    public static final String DEAL_STATUS_PROCESSING = "PROCESSING"
    public static final String DEAL_STATUS_REJECT = "REJECT"
    public static final String DEAL_STATUS_RFQ2T = "RFQ2T"
    public static final String DEAL_STATUS_RFQ2S = "RFQ2S"
    public static final String DEAL_STATUS_PENDING = "PENDING"
    public static final String DEAL_STATUS_CANCEL = "CANCEL"
    public static final String DEAL_STATUS_DONE = "DONE"
    public static final String DEAL_STATUS_DELETE = "DELETE"

    public static final List DEAL_STATUS = [
            ["code": DEAL_STATUS_PROCESSING, "name": "Processing"],
            ["code": DEAL_STATUS_REJECT, "name": "Reject"],
            ["code": DEAL_STATUS_RFQ2T, "name": "Rfq2t"],
            ["code": DEAL_STATUS_RFQ2S, "name": "Rfq2s"],
            ["code": DEAL_STATUS_PENDING, "name": "Pending"],
            ["code": DEAL_STATUS_CANCEL, "name": "Cancel"],
            ["code": DEAL_STATUS_DONE, "name": "Done"],
            ["code": DEAL_STATUS_DELETE, "name": "Delete"],
    ]
    //--

    //--Loại reject: Tự động hay NSD reject
    public static final String REJECT_DEAL_TYPE_AUTO = "auto"
    public static final String REJECT_DEAL_TYPE_MANUAL = "manual"
    //--

    //--Loại type deal
    public static final String DEAL_TYPE_AUTO = "AUTO_DONE"
    public static final String DEAL_TYPE_RFQ = "RFQ"
    //--

    /*Trạng thái update deal*/
    public static final String DEAL_UPDATE_STATUS_ADDNEW = "addnew"
    public static final String DEAL_UPDATE_STATUS_UPDATE = "update"
    public static final String DEAL_UPDATE_STATUS_DELETE = "delete"

    public static final List DEAL_UPDATE_STATUS = [
            ["code": DEAL_UPDATE_STATUS_ADDNEW, "name": "Add New"],
            ["code": DEAL_UPDATE_STATUS_UPDATE, "name": "Update"],
            ["code": DEAL_UPDATE_STATUS_DELETE, "name": "Delete"]
    ]
    //--

    /*Loại margin*/
    public static final String MARGIN_TYPE_BASE = "BASE"
    public static final String MARGIN_TYPE_VOLUME = "VOLUME"
    public static final String MARGIN_TYPE_BRANCH = "BRANCH"
    public static final String MARGIN_TYPE_CUSTOMER = "CUSTOMER"
    public static final String MARGIN_TYPE_BASE_EXT = "BASE_EXT"
    public static final String MARGIN_TYPE_VOLUME_EXT = "VOLUME_EXT"
    public static final String MARGIN_TYPE_BRANCH_EXT = "BRANCH_EXT"
    public static final String MARGIN_TYPE_CUSTOMER_EXT = "CUSTOMER_EXT"

    public static final List MARGIN_TYPE = [
            ["code": MARGIN_TYPE_BASE, "name": "base"],
            ["code": MARGIN_TYPE_VOLUME, "name": "volume"],
            ["code": MARGIN_TYPE_BRANCH, "name": "branch"],
            ["code": MARGIN_TYPE_CUSTOMER, "name": "customer"],
            ["code": MARGIN_TYPE_BASE_EXT, "name": "base_ext"],
            ["code": MARGIN_TYPE_VOLUME_EXT, "name": "volume_ext"],
            ["code": MARGIN_TYPE_BRANCH_EXT, "name": "branch_ext"],
            ["code": MARGIN_TYPE_CUSTOMER_EXT, "name": "customer_ext"],
    ]

    /*Loại giao dịch*/
    public static final String TRANSACTION_TYPE_TODAY = "TODAY"
    public static final String TRANSACTION_TYPE_TOM = "TOM"
    public static final String TRANSACTION_TYPE_SPOT = "SPOT"
    public static final String TRANSACTION_TYPE_SN = "SN"
    public static final String TRANSACTION_TYPE_FW = "FW"
    public static final String TRANSACTION_TYPE_SWAP = "SWAP"

    public static final List TRANSACTION_TYPE = [
            ["code": TRANSACTION_TYPE_TODAY, "name": "Today"],
            ["code": TRANSACTION_TYPE_TOM, "name": "Tom"],
            ["code": TRANSACTION_TYPE_SPOT, "name": "Spot"],
            ["code": TRANSACTION_TYPE_SN, "name": "Spot+1"],
            ["code": TRANSACTION_TYPE_FW, "name": "Fw"],
            ["code": TRANSACTION_TYPE_SWAP, "name": "Swap"],
    ]
    /*--*/

    /*Kênh tiền đi mặc định*/
    public static final String CHANNEL_CURRENCY_CODE_NN = "NN"
    public static final String CHANNEL_CURRENCY_CODE_MSB = "MSB"
    public static final String CHANNEL_CURRENCY_CODE_VCB = "VCB"
    //--

    /*Chiều giao dịch*/
    public static final String TRANSACTION_DIRECTION_BUY = "BUY"
    public static final String TRANSACTION_DIRECTION_SELL = "SELL"
    //--

    /*Chiều giao dịch*/
    public static final String TRANSACTION_SWAP_NEAR = "NEAR"
    public static final String TRANSACTION_SWAP_FAR = "FAR"
    //--

    /*Loại hình giao dịch*/
    public static final String TRANSACTION_GROUP_BUY = "BUY"
    public static final String TRANSACTION_GROUP_SELL = "SELL"
    public static final String TRANSACTION_GROUP_ALL = "ALL"

    public static final List TRANSACTION_GROUP = [
            ["code": TRANSACTION_GROUP_BUY, "name": "Buy"],
            ["code": TRANSACTION_GROUP_SELL, "name": "Sell"],
            ["code": TRANSACTION_GROUP_ALL, "name": "All"],
    ]
    /*--*/

    /**/

    /*Loại bộ điều khiển*/
    public static final String CONDITION_TYPE_MARGIN = "MARGIN"
    public static final String CONDITION_TYPE_MARGIN_FW = "MARGIN_FW"
    public static final String CONDITION_TYPE_DEAL = "DEAL"

    /*Loại LOGIC*/
    public static final String CONDITION_LOGIC_AND = "AND"
    public static final String CONDITION_LOGIC_OR = "OR"

    /*Loại action deal*/
    public static final String ACTION_DEAL_INIT = "INIT"
    public static final String ACTION_DEAL_SUBMIT = "SUBMIT"
    public static final String ACTION_DEAL_ACCEPT = "ACCEPT"
    public static final String ACTION_DEAL_REJECT = "REJECT"
    public static final String ACTION_DEAL_EDIT = "EDIT"
    public static final String ACTION_DEAL_DELETE = "DELETE"
    public static final String ACTION_DEAL_CANCEL = "CANCEL"

    /*Trạng thái import Excel*/
    public static final String ACTION_ADD = "ADD"
    public static final String ACTION_DELETE = "DELETE"
    public static final String ACTION_EDIT = "EDIT"


    public static final List ACTION_DEAL = [
            ["code": ACTION_DEAL_SUBMIT, "name": "Submit"],
            ["code": ACTION_DEAL_ACCEPT, "name": "Accept"],
            ["code": ACTION_DEAL_EDIT, "name": "Edit"],
            ["code": ACTION_DEAL_DELETE, "name": "Delete"],
            ["code": ACTION_DEAL_REJECT, "name": "Reject"],
    ]

    public static final String GROUP_CIF_NONAME = "NONAME"

    public static final String BRANCH_CLEARING_MODE_BRMSB = "BRMSB"
    public static final String BRANCH_CLEARING_MODE_VCB = "VCB"
    public static final String BRANCH_CLEARING_MODE_CITAD = "CITAD"
    public static final String BRANCH_CLEARING_MODE_SWIFT = "SWIFT"
    public static final String BRANCH_CLEARING_MODE_OTHERS = "OTHERS"
    public static final String BRANCH_CLEARING_MODE_PDF = "PDF"

    public static final List BRANCH_CLEARING_MODE = [
            ["code": BRANCH_CLEARING_MODE_BRMSB, "name": "BRMSB"],
            ["code": BRANCH_CLEARING_MODE_VCB, "name": "VCB"],
            ["code": BRANCH_CLEARING_MODE_CITAD, "name": "CITAD"],
            ["code": BRANCH_CLEARING_MODE_SWIFT, "name": "SWIFT"],
            ["code": BRANCH_CLEARING_MODE_OTHERS, "name": "OTHERS"],
            ["code": BRANCH_CLEARING_MODE_PDF, "name": "PDF"],
    ]

    public static final String BRANCH_REGION_MB = "MB"
    public static final String BRANCH_REGION_MN = "MN"
    public static final List BRANCH_REGION = [
            ["code": BRANCH_REGION_MB, "name": "Miền Bắc"],
            ["code": BRANCH_REGION_MN, "name": "Miền Nam"],
    ]

    public static final String FX_RATE_SUBCATEGORY_FX_DN = "FX_DN"
    public static final String FX_RATE_SUBCATEGORY_RATE_SBV = "RATE_SBV"
    public static final String FX_RATE_SUBCATEGORY_FWD_POINT = "FWD_POINT"
    public static final String FX_RATE_SUBCATEGORY_RATE_QUYDOI = "RATE_QUYDOI"
    public static final String FX_RATE_SUBCATEGORY_GAP_LS = "GAP_LS"

    public static final String DEAL_CREATE_TYPE_IBMB = "IBMB"


    public static final String FOWARD_POINT_TENOR_ON = "ON"
    public static final String FOWARD_POINT_TENOR_TN = "TN"
    public static final String FOWARD_POINT_TENOR_SN = "SN"

    public static final String TYPE_OF_PURCHASE_CASH = "CASH"
    public static final String TYPE_OF_PURCHASE_TRANSFER = "TRANSFER"

    //Biên mậu
    public static final String BORDER_TRADER_TT = "TT"
    public static final String BORDER_TRADER_UT = "UT"

    //type action import deal
    public static final String IMPORT_DEAL_ACTION_EXPORT = "EXPORT"
    public static final String IMPORT_DEAL_ACTION_IMPORT = "IMPORT"

    public static final String PROJECT_SHEET = "Abc@12345!MSB!2023"

    //report
    public static final String QUOTE_RATE_TYPE_SPOT = "SPOT"
    public static final String QUOTE_RATE_TYPE_FW = "FW"

    public static final String ENTERPRISE_CLOSE_TRADING_CHANNEL_TQ = "TQ"
    public static final String ENTERPRISE_CLOSE_TRADING_CHANNEL_IBMB = "IBMB"

    public static final def ENTERPRISE_CLOSE_TRADING_CHANNEL = [
            ["code": ENTERPRISE_CLOSE_TRADING_CHANNEL_TQ, "name": "Tại quầy"],
            ["code": ENTERPRISE_CLOSE_TRADING_CHANNEL_IBMB, "name": "IBMB"],
    ]

    public static final String SALE_DAILY_REPORT_TYPE_PSD = "PSD"
    public static final String SALE_DAILY_REPORT_TYPE_NHCD = "NHCD"
    public static final List SALE_DAILY_REPORT_TYPE = [
            ["code": SALE_DAILY_REPORT_TYPE_PSD, "name": "Báo cáo kinh doanh ngoại tệ theo phân khúc"],
            ["code": SALE_DAILY_REPORT_TYPE_NHCD, "name": "Báo cáo kinh doanh ngoại tệ theo NHCD"],
    ]

    public static final String SALE_DAILY_CROSS_SELLING_TYPE_1 = "CS1"
    public static final String SALE_DAILY_CROSS_SELLING_TYPE_2 = "CS2"
    public static final String SALE_DAILY_CROSS_SELLING_TYPE_3 = "CS3"

    public static final def SALE_DAILY_CROSS_SELLING_TYPE = [
            ["code": SALE_DAILY_CROSS_SELLING_TYPE_1, "name": "Ghi nhận 100%"],
            ["code": SALE_DAILY_CROSS_SELLING_TYPE_2, "name": "Ghi nhận lợi nhuận của PGD hạch toán"],
            ["code": SALE_DAILY_CROSS_SELLING_TYPE_3, "name": "Ghi nhận lợi nhuận của PGD bán chéo"],
    ]
    //--report
}
