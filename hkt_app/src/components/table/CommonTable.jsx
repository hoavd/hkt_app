import { Button, Input, Pagination, Select, Table } from 'antd';
import _ from 'lodash';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch } from 'react-redux';
import IconExport from '../../assets/imageSvg/export.svg';
import IconSearch from '../../assets/imageSvg/search.svg';
import AllowSet from '../../assets/imageSvg/setting.svg';
import IconImport from '../../assets/imageSvg/import.svg';
import IconRefre from '../../assets/imageSvg/refresh.svg';
import { CASH, TRANSFER } from '../../constants/appConstant';
import { setRowSelection, setRowSelectionAll } from '../../redux/slice/appSlice';
import './table.scss';

const SIZE_PER_PAGE = [
  { label: 5, value: 5 },
  { label: 10, value: 10 },
  { label: 25, value: 25 },
  { label: 50, value: 50 }
];
const Option = Select.Option;

const CommonTable = ({
  data,
  column,
  loading,
  rowSelection,
  currentPage,
  pageSize,
  totalElements,
  search,
  showPage,
  showSearch,
  showBtn,
  showBtnImport,
  showBtnExport,
  showBtnSearch,
  showBtnRefresh,
  showSelect,
  titleTable,
  listSelected,
  handleBtnTable,
  handleRefreshDeal,
  handleImportDeal,
  handleShowDivSearch,
  handleExportExcel,
  handleChangePurchase,
  getPageData,
  handleQuery,
  onRowClick,
  onContextMenu
}) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const dataCopy = [];

  const [listData, setListData] = useState([]);
  const [columns, setColumns] = useState([]);
  const [selectedRows, setSelectedRows] = useState([]);
  const [page, setPage] = useState(1);
  const [pageSizeState, setPageSizeState] = useState(10);
  const [totalElementState, setTotalElementState] = useState(0);
  const [sort, setSort] = useState('');
  const [order, setOrder] = useState('');

  const scrollToTop = () => {
    window.scrollTo(0, 0);
  };

  const handleRowSelectionChange = (selectedRowKeys, selectedRows) => {
    setSelectedRows(selectedRows);
  };

  const resetRowSelection = (data) => {
    if (data?.length) setSelectedRows(data);
    else setSelectedRows([]);
  };

  const convertData = async (data) => {
    if (data?.length) {
      await data.map((element, i) => {
        let elementCopy = _.cloneDeep(element);
        elementCopy['key'] = currentPage * pageSize + i + 1;
        dataCopy.push(elementCopy);
      });
      setListData(dataCopy);
    } else setListData([]);
  };

  const getData = (data) => {
    let params = {};
    params['currentPage'] = data.page - 1;
    params['max'] = data.size;
    params['offset'] = data.size * (data.page - 1);
    if (data.sort && data.order) {
      params['sort'] = data.sort;
    }
    if (data.order === 'ascend') params['order'] = 'asc';
    if (data.order === 'descend') params['order'] = 'desc';
    if (search) {
      for (const [key, value] of Object.entries(search || {})) {
        if ((value && value !== -1) || value === 0) {
          params[key] = value;
        }
      }
    }
    getPageData(params);
    scrollToTop();
  };

  const handleChange = (pagination, filters, sorter) => {
    setSort(sorter.field);
    setOrder(sorter.order);
    getData({ page: page, size: pageSize, sort: sorter.field, order: sorter.order });
  };

  useEffect(() => {
    setPage(currentPage + 1);
    setPageSizeState(pageSize);
    setTotalElementState(totalElements || 0);
    resetRowSelection(listSelected);
    setColumns(column?.filter((item) => !item.hidden));
    convertData(data);
    // eslint-disable-next-line
  }, [data, column, loading]);

  return (
    <div className='table-content'>
      <div className='title-table'>
        <span className='title'>{titleTable}</span>
        <div className='d-flex'>
          {showBtnRefresh && (
            <Button
              className='btn-custom-table'
              style={{ marginRight: 8 }}
              icon={<img src={IconRefre} alt='' />}
              onClick={handleRefreshDeal}
            >
              Refresh
            </Button>
          )}
          {showBtnImport && (
            <Button
              className='btn-custom-table'
              style={{ marginRight: 8 }}
              icon={<img src={IconImport} alt='' />}
              onClick={handleImportDeal}
            >
              Import deal
            </Button>
          )}
          {showBtnExport && (
            <Button
              className='btn-custom-table'
              style={{ marginRight: 8 }}
              icon={<img src={IconExport} alt='' />}
              onClick={handleExportExcel}
            >
              {t('rendering')}
            </Button>
          )}
          {showBtnSearch && (
            <Button
              className='btn-custom-table'
              style={{ marginRight: 8 }}
              icon={<img src={IconSearch} alt='' />}
              onClick={handleShowDivSearch}
            >
              {t('search')}
            </Button>
          )}
          {showBtn && (
            <Button className='btn-custom-table' icon={<img src={AllowSet} alt='' />} onClick={handleBtnTable}>
              {t('setting')}
            </Button>
          )}
          {showSearch && (
            <div className='box-search'>
              <span>{t('quickSearch')}</span>
              <Input value={search?.query} placeholder='' onChange={(e) => handleQuery(e)} />
            </div>
          )}
          {showSelect && (
            <Select value={search.typeOfPurchase} style={{ width: 160 }} onChange={handleChangePurchase}>
              <Option value={'all'}>--</Option>
              <Option value={CASH}>{t('cash')}</Option>
              <Option value={TRANSFER}>{t('transfer')}</Option>
            </Select>
          )}
        </div>
      </div>
      <div className='custom-page-table'>
        <Table
          onChange={handleChange}
          showSorterTooltip={false}
          loading={loading}
          columns={columns}
          dataSource={listData}
          sortDirections={['ascend', 'descend']}
          scroll={{ x: true }}
          onRow={(record, rowIndex) => {
            return {
              onClick: () => {
                if (onRowClick) onRowClick(record, rowIndex);
              },
              onContextMenu: (e) => {
                if (onContextMenu) onContextMenu(e, record);
              }
            };
          }}
          rowSelection={
            rowSelection && {
              selectedRowKeys: selectedRows.map((row) => row.key),
              // hideSelectAll: props['isSelect']
              onSelect: (record, selected, selectedRows) => {
                dispatch(setRowSelection({ record: record, selected: selected, selectedRows: selectedRows }));
              },
              onSelectAll: (selected, selectedRows) => {
                dispatch(setRowSelectionAll({ selected: selected, selectedRows: selectedRows }));
              },
              onChange: handleRowSelectionChange
            }
          }
          pagination={false}
          locale={{
            emptyText: <div className='text-center'>Data not found</div>
          }}
        />
        {showPage && (
          <Pagination
            total={totalElementState}
            current={page}
            pageSize={pageSizeState}
            showSizeChanger={false}
            showTitle={false}
            onChange={(pageChange, pageSize) => {
              getData({ page: pageChange, size: pageSize, sort: sort, order: order });
            }}
            showTotal={() => (
              <div className='footer-table'>
                <span className='mr-1'>{t('numberRecord')}</span>
                <Select
                  value={pageSizeState || pageSize}
                  onChange={(e) => {
                    setPageSizeState(Number(e));
                    getData({ page: 1, size: Number(e), sort: sort, order: order });
                  }}
                  className='d-inline-block w-auto custom-select'
                  options={SIZE_PER_PAGE}
                />
                <div className='pagination pagination-rounded d-inline-flex ms-auto'>
                  <label className='me-1 showing'>
                    {t('showFrom')} {Math.min((page - 1) * pageSizeState + 1, totalElementState)} -{' '}
                    {Math.min(page * pageSizeState, totalElementState)} {t('of')} {totalElementState} {t('record')}
                  </label>
                </div>
              </div>
            )}
          />
        )}
      </div>
    </div>
  );
};

export default CommonTable;
