package commons

class ResultMsgConstant {
    public static final String CODE_DUPLICATE = "Mã đã tồn tại trong hệ thống!"
    public static final String RECORD_UNDEFINED = "Không tim thấy thông tin bản ghi!"
    public static final String RECORD_DELETED = "Bản ghi không tồn tại hoặc đã xóa"
    public static final String EDIT_ERROR = "Bản ghi không được sửa"
    public static final String TIME_ERROR = "Thời gian không hợp lệ!"

    public static final String DELETED_SUCCESS = "Xóa thành công!"
    public static final String DELETED_ERROR = "Bị lỗi khi xóa!"
    public static final String DELETED_FALSE = "Bản ghi không tồn tại hoặc không thể xóa!"
    public static final String DELETED_CONSTRAINT = "Bản ghi ràng buộc constraint, không thể xóa!"

    public static final String SAVE_SUCCESS = "Cập nhật thành công!"
    public static final String SAVE_ERROR = "Bị lỗi khi lưu! Đề nghị liên hệ với admin IT để kiểm tra lại!"

    public static final String EDIT_FALSE = "Không thể sửa bản ghi này!"

    public static final String REQUIRED_CODE = "Mã không được để trống!"
    public static final String REQUIRED_NAME = "Tên không được để trống!"
    public static final String REQUIRED_CODE_NAME = "Mã, tên không được để trống!"
    public static final String REQUIRED_CUSTOMER_TYPE = "Loại khách hàng không được để trống!"
    public static final String REQUIRED_CUSTOMER_TYPE_ERR = "Chỉ cho phép cấu hình cho khách hàng doanh nghiệp!"
    public static final String REQUIRED_TRANSACTION_TYPE = "Loại giao dịch không được để trống!"
    public static final String ERR_CREATE_FORM = "Không được tạo mới cấu hình !"

    public static final String REQUIRED_CURRENCY = "Tiền tệ không được để trống!"
    public static final String REQUIRED_DATE_HOLIDAY = "Date không được để trống!"

    public static final String DATA_NOT_FOUND = "Dữ liệu không hợp lệ!"
    public static final String DATA_OK = "Dữ liệu hợp lệ!"

    public static final String IMPORT_SUCCESS = "Import file thành công!"
    public static final String IMPORT_ERROR = "Bị lỗi khi import file!"

    public static final String EXPORT_CIF_SUCCES = "Thỏa mãn điều kiện export!"
    public static final String EXPORT_CIF_ERR = "Bị lỗi khi export! Số lượng bản ghi đã lớn hơn 20000 bản"

    public static final String REQUIRED_DATA = "Bạn không có quyền truy cập dữ liệu này!"
    public static final String PROFILE_NOT_EXISTS = "Không tìm thấy thông tin hồ sơ!"
    public static final String FINANCIAL_NOT_EXISTS = "Không tìm thấy thông tin báo cáo tài chính!"

    public static final String SUCCESS = "Thành công!"
    public static final String SYSTEM_ERROR = "Lỗi hệ thống!"
    public static final String SOMETHING_WENT_WRONG = "Có lỗi xảy ra!"
    public static final String DEAL_HAS_DONE = "Deal đã được DONE!"
    public static final String DEAL_HAS_HOLD = "Deal đã được hold!"
    public static final String DEAL_HAS_ACTIVE = "Thao tác không chính xác!"

    public static final String ERROR_FORMAT_CURRENCY = "Lỗi cặp tiền tệ chưa có format giá!"

    public static final String DONT_DOWNLOAD_FILE = "Không tải được bản ghi!"

    public static final String NOT_FOUND_MARGIN = "Yêu cầu liên hệ với Admin BU, CIF chưa được setup!"

    public static final String NOT_EXCHANGE_RATE = "Không có tỷ giá quy đổi cho đồng tiền này!"

    public static final String NOT_EXCHANGE_FORMAT_PRICE = "Không cấu hình tỷ giá cho cặp tiền này!"
}
