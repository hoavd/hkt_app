export const DEFAULT_TIME_AUTO_HIDE_TOASTIFY = 5000;
export const DEFAULT_TIME_AUTO_HIDE_TOASTIFY_TRANSACTION = 5000;
export const DEFAULT_TIME_AUTO_HIDE_TOASTIFY_ERROR = 5000;
export const DEFAULT_TIME_LOADING = 1000;
export const DEFAULT_TIME_INTERVAL_FW = 5000;

export const REQUEST_SUCCESS_CODE = 200;

export const ROLE_TRADER = 'ROLE_GROUP_TRADER';
export const ROLE_SALE_HO = 'ROLE_GROUP_SALE';
export const ROLE_GROUP_CLIENT = 'ROLE_GROUP_CLIENT';
export const CASH = 'cash';
export const TRANSFER = 'transfer';

export const LIST_TRAN_TYPE = [
  { label: 'ALL', value: 'ALL' },
  { label: 'TODAY', value: 'TODAY' },
  { label: 'TOM', value: 'TOM' },
  { label: 'SPOT', value: 'SPOT' },
  { label: 'SPOT +1', value: 'SN' },
  { label: 'FW', value: 'FW' },
  { label: 'SWAP', value: 'SWAP' }
];

export const LIST_TRAN_TYPE_NOT_ALL = [
  { label: 'TODAY', value: 'TODAY' },
  { label: 'TOM', value: 'TOM' },
  { label: 'SPOT', value: 'SPOT' },
  { label: 'SPOT +1', value: 'SN' },
  { label: 'FW', value: 'FW' },
  { label: 'SWAP', value: 'SWAP' }
];

export const LIST_MONTH = [
  { value: 1, label: 1 },
  { value: 2, label: 2 },
  { value: 3, label: 3 },
  { value: 4, label: 4 },
  { value: 5, label: 5 },
  { value: 6, label: 6 },
  { value: 7, label: 7 },
  { value: 8, label: 8 },
  { value: 9, label: 9 },
  { value: 10, label: 10 },
  { value: 11, label: 11 },
  { value: 12, label: 12 }
];

export const LIST_CHANNEL_TRAN = [
  { label: 'Tại quầy', value: 'TQ' },
  { label: 'IBMB', value: 'IBMB' }
];

export const LIST_TRAN_TYPE_SEARCH = [
  { label: 'TODAY', value: 'TODAY' },
  { label: 'TOM', value: 'TOM' },
  { label: 'SPOT', value: 'SPOT' },
  { label: 'SPOT +1', value: 'SN' },
  { label: 'FW', value: 'FW' },
  { label: 'SWAP', value: 'SWAP' }
];

export const LIST_TRAN_TYPE_CUR = [
  { label: 'TODAY', value: 'TODAY' },
  { label: 'TOM', value: 'TOM' },
  { label: 'SPOT', value: 'SPOT' }
  // { label: 'SPOT +1', value: 'SN' }
];

export const LIST_TRAN_TYPE_FAR = [
  { label: 'TODAY', value: 'TODAY' },
  { label: 'TOM', value: 'TOM' },
  { label: 'SPOT', value: 'SPOT' },
  { label: 'FW', value: 'FW' }
];

export const LIST_TRAN_GROUP = [
  { label: 'ALL', value: 'ALL' },
  { label: 'MUA', value: 'BUY' },
  { label: 'BÁN', value: 'SELL' }
];

export const LIST_LOGIC_CON = [
  { label: 'AND', value: 'AND' },
  { label: 'OR', value: 'OR' }
];

export const LIST_OPERATOR_NUMBER = [
  { label: '==', value: '==' },
  { label: '!=', value: '!=' },
  { label: '>', value: '>' },
  { label: '<', value: '<' },
  { label: '>=', value: '>=' },
  { label: '<=', value: '<=' }
];

export const LIST_OPERATOR_OTHER = [
  { label: '==', value: '==' },
  { label: '!=', value: '!=' }
];

export const LIST_CRITERIA = [
  { value: 'ACCEPT', label: 'Accept' },
  { value: 'REJECT', label: 'Reject' },
  { value: 'RFQ2S', label: 'RFQ2S' },
  { value: 'RFQ2T', label: 'RFQ2T' },
  { value: 'NOT2K', label: 'Không đồng bộ K+' }
];

export const LIST_MARGIN_TYPE = [
  { value: 'BASE', label: 'Base' },
  { value: 'VOLUME', label: 'Volume' },
  { value: 'BRANCH', label: 'Branch' },
  { value: 'CUSTOMER', label: 'Customer' },
  { value: 'BASE_EXT', label: 'Base_ext' },
  { value: 'VOLUME_EXT', label: 'Volume_ext' },
  { value: 'BRANCH_EXT', label: 'Branch_ext' },
  { value: 'CUSTOMER_EXT', label: 'Customer_ext' }
];

export const LIST_CUSTOMER_TYPE = [
  { value: 'IC', label: 'Cá nhân' },
  { value: 'CC', label: 'Doanh nghiệp' },
  { label: 'Other02', value: 'OT2' },
  { label: 'Other01', value: 'OT1' },
  { label: 'Công ty tài chính', value: 'FI' },
  { label: 'Qũy đầu tư', value: 'IF' },
  { label: 'Bảo hiểm', value: 'IN' },
  { label: 'Chứng Khoán', value: 'ST' },
  { label: 'Non Bank', value: 'NB' },
  { label: 'Bank', value: 'BA' }
];

export const LIST_CUSTOMER_TYPE_HIDDEN = [
  { label: 'Other02', value: 'OT2' },
  { label: 'Other01', value: 'OT1' },
  { label: 'Công ty tài chính', value: 'FI' },
  { label: 'Qũy đầu tư', value: 'IF' },
  { label: 'Bảo hiểm', value: 'IN' },
  { label: 'Chứng Khoán', value: 'ST' },
  { label: 'Non Bank', value: 'NB' },
  { label: 'Bank', value: 'BA' }
];

export const LIST_METHOD_TRAN = [
  { value: 'AUTO', label: 'Auto' },
  { value: 'MANUAL', label: 'Manual' }
];

export const LIST_SEGMENT = [
  { value: 'U2B', label: 'Dưới 200 tỷ' },
  { value: 'A2B', label: 'Trên 200 tỷ' }
];

export const LIST_TRAN_DIRECTION = [
  { value: 'BUY', label: 'Mua' },
  { value: 'SELL', label: 'Bán' }
];

export const LIST_KONDOR_STATUS = [
  { value: 1, label: 'Chưa đồng bộ' },
  { value: 2, label: 'Đồng bộ thành công' },
  { value: 3, label: 'Đồng bộ không thành công' }
];

export const LIST_PURCHASE_TYPE = [
  { value: 'CASH', label: 'Tiền mặt' },
  { value: 'TRANSFER', label: 'Chuyển khoản' }
];

export const LIST_CREATE_TYPE_DEAL = [
  { value: 'MANUAL', label: 'Manual' },
  { value: 'EXCEL', label: 'Excel' },
  { value: 'IBMB', label: 'IM / MB' }
];

export const LIST_UPDATE_STATUS = [
  { value: 'addnew', label: 'Add new' },
  { value: 'update', label: 'Update' },
  { value: 'delete', label: 'Delete' }
];

export const LIST_DEAL_STATUS = [
  { value: 'PROCESSING', label: 'Processing' },
  { value: 'REJECT', label: 'Reject' },
  { value: 'RFQ2S', label: 'RFQ2S' },
  { value: 'RFQ2T', label: 'RFQ2T' },
  { value: 'PENDING', label: 'Pending' },
  { value: 'CANCEL', label: 'Cancel' },
  { value: 'DONE', label: 'Done' }
];

export const LIST_EFFECT_STATUS = [
  { value: true, label: 'Hiệu lực' },
  { value: false, label: 'Hết hiệu lực' }
];

export const LIST_SOURCE_STATUS = [
  { value: true, label: 'Thành công' },
  { value: false, label: 'Không thành công' }
];

export const LIST_TYPE_CUSTOMER = [
  { label: 'Cá nhân', value: 'IC' },
  { label: 'Doanh nghiệp', value: 'CC' }
];

export const LIST_TYPE_PURCHASE = [
  { value: 'CASH', label: 'Tiền mặt' },
  { value: 'TRANSFER', label: 'Chuyển khoản' }
];

export const LIST_CHECK_ROUTER = [
  '/transaction/individual/today',
  '/transaction/individual/tom',
  '/transaction/individual/spot',
  '/transaction/individual/fw',
  '/transaction/individual/swap',
  '/transaction/enterprise/today',
  '/transaction/enterprise/tom',
  '/transaction/enterprise/spot',
  '/transaction/enterprise/swap',
  '/transaction/enterprise/fw'
];

export const LIST_CHECK_ROUTER_HANDLE = [
  '/transaction/handle/today',
  '/transaction/handle/tom',
  '/transaction/handle/spot',
  '/transaction/handle/fw',
  '/transaction/handle/swap'
];

export const CHECK_ROUTER_RATE = '/transaction/rate-monitor';
export const CHECK_ROUTER_REPORT = '/transaction/list';
