package commons

class ConstantCategories {
    //code CIF mặc định
    public static final String CODE_CIF_DEFAULT = "1"

    // Loại khách hàng
    public static final String INDIVIDUAL_CUSTOMER_TYPE = "IC"
    public static final String CORPORATE_CUSTOMER_TYPE = "CC"
    public static final String BANK_CUSTOMER_TYPE = "BA"
    public static final String NON_BANK_CUSTOMER_TYPE = "NB"
    public static final String STOCK_CUSTOMER_TYPE = "ST"
    public static final String INSURANCE_CUSTOMER_TYPE = "IN"
    public static final String INVESTMENT_FUNDS_CUSTOMER_TYPE = "IF"
    public static final String FINANCE_CUSTOMER_TYPE = "FI"
    public static final String OTHER01_CUSTOMER_TYPE = "OT1"
    public static final String OTHER02_CUSTOMER_TYPE = "OT2"

    public static final def CUSTOMER_TYPE_LIST = [
            ["code": INDIVIDUAL_CUSTOMER_TYPE, "name": "Cá nhân"],
            ["code": CORPORATE_CUSTOMER_TYPE, "name": "Doanh nghiệp"],
            ["code": BANK_CUSTOMER_TYPE, "name": "Bank"],
            ["code": NON_BANK_CUSTOMER_TYPE, "name": "Non Bank"],
            ["code": STOCK_CUSTOMER_TYPE, "name": "Chứng Khoán"],
            ["code": INSURANCE_CUSTOMER_TYPE, "name": "Bảo hiểm"],
            ["code": INVESTMENT_FUNDS_CUSTOMER_TYPE, "name": "Qũy đầu tư"],
            ["code": FINANCE_CUSTOMER_TYPE, "name": "Công ty tài chính"],
            ["code": OTHER01_CUSTOMER_TYPE, "name": "Other01"],
            ["code": OTHER02_CUSTOMER_TYPE, "name": "Other02"]
    ]
    //--

    // Phân khúc khách hàng
    public static final String ABOVE_200B = "A2B"
    public static final String UNDER_200B = "U2B"

    public static final def SEGMENT_LIST = [
            ["code": ABOVE_200B, "name": "Trên 200 tỷ"],
            ["code": UNDER_200B, "name": "Dưới 200 tỷ"]
    ]
    //--

    static String findNameByCode(String key, def list) {
        return list.find({
            it.code == key
        })?.name
    }
}
