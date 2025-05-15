import { Dropdown, Spin } from 'antd';
import React, { useCallback, useState, useEffect, useRef, useMemo } from 'react';
import iconSupport from '../../assets/imageSvg/icon-support.svg';
import './style.scss';
import Dashboard from './dashboard/Dashboard';
import { useNavigate } from 'react-router-dom';
import { findAlertRequest } from '../../services/dashboard/alert';
import useNotify from '../../hooks/useNotify';
import { debounce } from 'lodash';
import { useSelector } from 'react-redux';

const PAGE_SIZE = 5;

const Home = () => {
  const [errorList, setErrorList] = useState([]);
  const [pageNumber, setPageNumber] = useState(1);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const notify = useNotify();
  const navigate = useNavigate();
  const dropdownRef = useRef(null);

  const [totalCount, setTotalCount] = useState(0);
  const { alert } = useSelector((state) => state.appReducer);

  useEffect(() => {
    if (alert?.data) {
      // Thêm alert.data vào danh sách lỗi
      setErrorList((prev) => {
        // Tránh trùng lỗi theo uuid
        const exists = prev.find((e) => e.uuid === alert.data.uuid);
        if (exists) return prev;
        return [alert.data, ...prev];
      });
      // Tăng totalCount lên 1 hoặc set lại tùy logic
      setTotalCount((prev) => prev + 1);
    }
  }, [alert]);

  useEffect(() => {
    // Kiểm tra xem còn bản ghi chưa load nữa không
    if (errorList.length >= totalCount) {
      setHasMore(false);
    } else {
      setHasMore(true);
    }
  }, [errorList.length, totalCount]);

  const fetchData = useCallback(
    (page) => {
      if (loading) return;
      setLoading(true);
      const offset = (page - 1) * PAGE_SIZE;
      findAlertRequest(
        {
          max: PAGE_SIZE,
          currentPage: page,
          offset: offset
        },
        (res) => {
          const newData = Array.isArray(res.data?.list) ? res.data.list : [];
          const total = res.data?.total || 0;
          setTotalCount(total);

          setErrorList((prev) => {
            const updatedList = [...prev, ...newData];
            // Tính hasMore dựa trên updatedList
            setHasMore(total > updatedList.length);
            return updatedList;
          });

          setPageNumber(page);
          setLoading(false);
        },
        (err) => {
          notify.error(err?.data?.msg || 'Lỗi khi tải dữ liệu');
          setLoading(false);
        }
      );
    },
    // eslint-disable-next-line
    []
  );
  // Load page 1 khi mount
  useEffect(() => {
    fetchData(1);
  }, [fetchData]);

  // Xử lý scroll trong dropdown
  const onScrollHandler = useCallback(
    (e) => {
      const { scrollTop, scrollHeight, clientHeight } = e.target;
      if (!loading && hasMore && scrollHeight - scrollTop - clientHeight < 100) {
        fetchData(pageNumber + 1);
      }
    },
    [loading, hasMore, pageNumber, fetchData]
  );

  const debouncedOnScroll = useMemo(() => debounce(onScrollHandler, 200), [onScrollHandler]);

  useEffect(() => {
    return () => {
      debouncedOnScroll.cancel(); // cleanup debounce khi component unmount
    };
  }, [debouncedOnScroll]);

  const formatDate = (str) => {
    if (!str) return '';
    const [datePart, timePart] = str.split(' ');
    if (!datePart || !timePart) return str; // phòng lỗi
    const [year, month, day] = datePart.split('-');
    const [hour, minute] = timePart.split(':');
    return `${day}/${month}/${year} ${hour}:${minute}`;
  };

  // Nội dung dropdown tùy chỉnh với scroll
  const dropdownContent = (
    <div
      className='dropdown-content'
      style={{ maxHeight: 50000, overflowY: 'hidden', minWidth: 300 }}
      onScroll={debouncedOnScroll}
      ref={dropdownRef}
    >
      {errorList.length === 0 && !loading && <div className='no-error-message'>Không có lỗi nào</div>}
      {errorList.map((error, idx) => {
        const bgClass = error.type === 'error' ? 'error-type' : 'warning-type';
        return (
          <div
            key={error.uuid || idx}
            className={`error-item ${bgClass}`}
            onClick={() => navigate(`/dashboard/alert/${error.uuid}`)}
            tabIndex={0}
            role='button'
            onKeyPress={(e) => {
              if (e.key === 'Enter') navigate(`/dashboard/alert/${error.uuid}`);
            }}
          >
            <div className='error-desc'>{error.desc?.trim() ? error.desc : error.code}</div>
            <div className='error-date'>{formatDate(error.createDate)}</div>
          </div>
        );
      })}

      {loading && (
        <div style={{ textAlign: 'center', padding: 10 }}>
          <Spin size='small' />
        </div>
      )}
    </div>
  );

  // Menu hiển thị trực tiếp danh sách lỗi
  /*const supportMenu = errorList.map((error, idx) => ({
    key: error.uuid || `error_${idx}`,
    label: (
      <div className={`error-item ${error.type === 'error' ? 'error-type' : 'warning-type'}`}>
        <span className='error-index'>{idx + 1}.</span> {error.desc?.trim() ? error.desc : error.code}
      </div>
    ),
    onClick: () => navigate(`/dashboard/alert/${error.uuid}`)
  }));*/

  return (
    <React.Fragment>
      <div className='container-home'>
        <div className='top-exchange-home'>
          <div className='left-top'></div>
          <div className='right-top'>
            {/* Logo Support click riêng */}
            <Dropdown
              trigger={['click']}
              placement='bottomRight'
              dropdownRender={() => (
                <div
                  style={{
                    maxHeight: 200,
                    overflowY: 'auto',
                    background: '#fff',
                    borderRadius: 8,
                    boxShadow: '0 4px 12px rgba(0,0,0,0.15)'
                  }}
                  onScroll={debouncedOnScroll}
                  ref={dropdownRef}
                >
                  {dropdownContent /* danh sách lỗi render ở đây */}
                </div>
              )}
            >
              <div className='logo-personal' style={{ cursor: 'pointer', position: 'relative' }}>
                <img src={iconSupport} alt='Support' />
                <span className='error-count'>{totalCount}</span>
              </div>
            </Dropdown>
            <div className='logo-personal'></div>
          </div>
        </div>
      </div>
      <Dashboard />
    </React.Fragment>
  );
};

export default Home;
