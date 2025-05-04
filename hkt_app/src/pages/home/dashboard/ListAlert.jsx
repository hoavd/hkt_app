import { useEffect, useState } from 'react';
// import { useTranslation } from 'react-i18next';
// import { useNavigate } from 'react-router-dom';
import CommonTable from '../../../components/table/CommonTable';
import useNotify from '../../../hooks/useNotify';
import { findAlertRequest } from '../../../services/dashboard/alert';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

const ListAlert = () => {
  // const { t } = useTranslation();
  const notify = useNotify();
  const navigate = useNavigate();
  const [dataTable, setDataTable] = useState({});
  const [loading, setLoading] = useState(false);
  const [pageNumber, setPageNumber] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [query, setQuery] = useState(null);

  const { alert } = useSelector((state) => state.appReducer);
  const onRowClick = (record) => {
    navigate(`/dashboard/alert?${String(record.id)}`);
  };

  useEffect(() => {
    fetchData({ max: 10, currentPage: 0, offset: 0 });
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    fetchData({ max: 10, currentPage: 0, offset: 0 });
    // eslint-disable-next-line
  }, [alert]);

  useEffect(() => {
    if (query || query === '') {
      const delayDebounceFn = setTimeout(() => {
        fetchData({ max: 10, currentPage: 0, offset: 0, query: query });
      }, 500);
      return () => clearTimeout(delayDebounceFn);
    }
    // eslint-disable-next-line
  }, [query]);

  const fetchData = (params) => {
    setLoading(true);
    findAlertRequest(
      params,
      (res) => {
        setDataTable(res.data);
        setPageNumber(params.currentPage);
        setRowsPerPage(params.max);
        setLoading(false);
      },
      (err) => {
        notify.error(err?.data?.msg);
        setLoading(false);
      }
    );
  };

  const handleQuery = (e) => {
    const value = e.target.value.trimStart();
    setQuery(value);
  };

  const getPageData = (params) => {
    fetchData(params);
  };

  const columns = [
    {
      title: 'STT',
      dataIndex: 'key',
      key: 'key',
      width: 50
    },
    {
      title: 'Code',
      dataIndex: 'code',
      key: 'code',
      width: 50
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      width: 200
    },
    {
      title: 'Type',
      dataIndex: 'type',
      key: 'type',
      width: 100
    },
    {
      title: 'Time',
      dataIndex: 'createDate',
      key: 'createDate',
      width: 100
    },
    {
      title: 'Desc',
      dataIndex: 'desc',
      key: 'desc',
      width: 200
    }
  ];

  return (
    <div className='tran-queue-contaier padding-content-page display-responsive'>
      <div className='header-page d-flex align-items-center justify-content-between'>
        <span className='title-page'>Danh sách cảnh báo sự cố</span>
      </div>
      <CommonTable
        column={columns}
        data={dataTable?.list}
        loading={loading}
        showPage={true}
        totalElements={dataTable?.total}
        currentPage={pageNumber}
        pageSize={rowsPerPage}
        titleTable={''}
        search={{ query: query }}
        showSearch={true}
        handleQuery={handleQuery}
        getPageData={getPageData}
        onRowClick={onRowClick}
      />
    </div>
  );
};

export default ListAlert;
