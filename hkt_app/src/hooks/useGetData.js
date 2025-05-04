import { useState } from 'react';
import { getCifRequest } from '../services/Categories/cifService';
import {
  getChannelReq,
  getListFilterGroupCommonReq,
  getManagerReq,
  getUpdateStatusReq
} from '../services/Categories/commonCategoriesService';
import useNotify from './useNotify';
// import { getGroupCifRequest } from '../services/Categories/groupCif';

const useGetData = () => {
  const notify = useNotify();
  //cif
  const [loadingCif, setLoadingCif] = useState(false);
  const [pageNumber, setPageNumber] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [dataCif, setDataCif] = useState({});
  //customer type
  const [customerTypeList, setCustomerTypeList] = useState([]);
  //Segment
  const [segmentList, setSegmentList] = useState([]);
  //groupMail
  const [groupMailList, setGroupMailList] = useState([]);
  //marginType
  const [marginTypeList, setMarginTypeList] = useState([]);
  // branch
  const [branchList, setBranchList] = useState([]);
  // ngan hang chuyen donah
  const [specializedBankList, setSpecializedBankList] = useState([]);
  // cap tien te
  const [currenPairList, setCurrenPairList] = useState([]);
  // tien te
  const [currencyList, setCurrencyList] = useState([]);
  // filter group
  const [filterGroupList, setFilterGroupList] = useState([]);
  //groupCif
  const [groupCifList, setGroupCifList] = useState([]);
  // list customer
  const [listCustomerType, setListCustomerType] = useState([]);
  // list channel
  const [listChannel, setListChannel] = useState([]);
  // update status
  const [listUpdateStatus, setListUpdateStatus] = useState([]);

  const getListUpdateStatus = (url, params) => {
    getUpdateStatusReq(
      url,
      params,
      (res) => {
        let options = res.data.map((item) => ({
          label: item.name,
          value: item.code
        }));
        setListUpdateStatus(options);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getListChannel = (url, params) => {
    getChannelReq(
      url,
      params,
      (res) => {
        let options = res.data.map((item) => ({
          label: item.name,
          value: item.code
        }));
        setListChannel(options);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getDataCif = (params) => {
    setLoadingCif(true);
    getCifRequest(
      params,
      (res) => {
        setRowsPerPage(params.max);
        setPageNumber(params.currentPage);
        setDataCif(res.data);
        setLoadingCif(false);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getCustomerType = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let customerTypeOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setCustomerTypeList(customerTypeOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getSegment = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let segmentOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.code
        }));
        setSegmentList(segmentOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getListCustomerType = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let customerType = res.data.list.map((item) => ({
          label: item.name,
          value: item.code
        }));
        setListCustomerType(customerType);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getMarginType = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let marginTypeOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setMarginTypeList(marginTypeOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getGroupMail = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let groupMailOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setGroupMailList(groupMailOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getGroupCif = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let groupCifOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setGroupCifList(groupCifOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getBranch = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let branchOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setBranchList(branchOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getSpecializedBank = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let sepecializedBankOptions = res.data.list.map((item) => ({
          label: item.name,
          value: item.id
        }));
        setSpecializedBankList(sepecializedBankOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getCurrenPair = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let listOptions = res.data?.list.map((item) => ({
          label: item.code,
          value: item.id
        }));
        if (params.all) listOptions?.unshift({ label: 'ALL', value: 'ALL' });
        setCurrenPairList(listOptions);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getCurrency = (url, params) => {
    getManagerReq(
      url,
      params,
      (res) => {
        let currencyLoad = res.data.list.map((item) => ({
          label: item.code,
          value: item.id
        }));
        setCurrencyList(currencyLoad);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  const getFilterGroup = (url) => {
    getListFilterGroupCommonReq(
      url,
      { max: 1000, offset: 0, order: 'desc' },
      (res) => {
        let filter = res.data.filterGroupList.map((item) => ({
          label: item.code,
          value: item.id
        }));
        setFilterGroupList(filter);
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
  };

  return {
    // cif
    loadingCif,
    pageNumber,
    rowsPerPage,
    dataCif,
    getDataCif,
    //customerType
    customerTypeList,
    getCustomerType,
    //segment
    segmentList,
    getSegment,
    //marginType
    marginTypeList,
    getMarginType,
    //groupMailList
    groupMailList,
    getGroupMail,
    // branchList
    branchList,
    getBranch,
    //
    specializedBankList,
    getSpecializedBank,
    //curren pair
    currenPairList,
    getCurrenPair,
    // curren
    currencyList,
    getCurrency,
    // filter group
    filterGroupList,
    getFilterGroup,
    // groupCif
    groupCifList,
    getGroupCif,
    // customer type
    listCustomerType,
    getListCustomerType,
    // channel
    listChannel,
    getListChannel,
    // update status
    listUpdateStatus,
    getListUpdateStatus
  };
};

export default useGetData;
