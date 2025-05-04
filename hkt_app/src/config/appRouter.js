import React, { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
import Header from '../components/header/Header';
import Sidebar from '../components/sidebar/SideBar';
import Fobbidden from '../pages/Fobbidden';
import NotFound from '../pages/NotFound';
import Home from '../pages/home';
import ChangePassword from '../pages/login/ChangePass';
import Login from '../pages/login/Login';
import ListAlert from '../pages/home/dashboard/ListAlert';
import Alert from '../pages/home/dashboard/Alert';
import { setWidthSystem } from '../redux/slice/appSlice';
import { getToken } from '../utils/storage';
import { appPath } from './appPath';

const AppRouters = () => {
  const refHome = useRef(null);
  const location = useLocation().pathname;
  const { userInfo } = useSelector((state) => state.userReducer);
  const [widthDiv, setWidthDiv] = useState(0);

  const dispatch = useDispatch();

  const isAuthenticated = () => {
    const token = getToken();
    if (token || userInfo.username) return true;
    return false;
  };

  useEffect(() => {
    setWidthDiv(refHome?.current?.offsetWidth);
    const getwidth = () => {
      setWidthDiv(refHome?.current?.offsetWidth);
    };
    window.addEventListener('resize', getwidth);
    return () => window.removeEventListener('resize', getwidth);
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (widthDiv) {
      const delayDebounceFn = setTimeout(() => {
        dispatch(setWidthSystem(widthDiv));
      }, 500);
      return () => clearTimeout(delayDebounceFn);
    }
    // eslint-disable-next-line
  }, [widthDiv]);

  if (!isAuthenticated()) {
    return (
      <Routes>
        <Route exact path={appPath.login} element={<Login />} />
        <Route path='*' element={<Navigate to='/login' />} />
      </Routes>
    );
  }

  return (
    <React.Fragment>
      <div className='app-body' ref={refHome}>
        {location === appPath.changePass ? (
          <Routes>
            <Route path={appPath.changePass} element={<ChangePassword />} exact />
          </Routes>
        ) : (
          <div className='body-app'>
            <Header />
            <Sidebar widthDiv={widthDiv} />
            <div className='container-app'>
              <Routes>
                {/* Not found */}
                <Route path={appPath.notFound} element={<NotFound />} exact />
                <Route path={appPath.fobbidden} element={<Fobbidden />} exact />
                <Route path={appPath.notFoundRouter} element={<NotFound />} exact />
                {/* Default */}
                <Route path={appPath.login} element={<Navigate to={appPath.default} />} />
                <Route path={appPath.default} element={<Home />} exact />

                {/* Dashboard */}
                <Route path={appPath.listWarning} element={<ListAlert />} exact />
                <Route path={appPath.alert} element={<Alert />} exact />
                {/* Báo cáo */}
              </Routes>
            </div>
          </div>
        )}
      </div>
    </React.Fragment>
  );
};

export default AppRouters;
