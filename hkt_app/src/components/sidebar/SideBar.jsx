import { Dropdown, Select, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import Logo from '../../assets/imageSvg/logo-home.svg';
import ImageEn from '../../assets/imageSvg/image-en.svg';
import ImageVn from '../../assets/imageSvg/image-vn.svg';
import IconLogo from '../../assets/imageSvg/tcb-logo.svg';
import IconClose from '../../assets/images/close.png';
import IconUser from '../../assets/images/icon-user.png';
import useNotify from '../../hooks/useNotify';
import { setCollapseMenu, setShowMenu, setValueLang } from '../../redux/slice/appSlice';
// import { setListCurrencyExpire } from '../../redux/slice/rateMonitor';
import { savePublicInfoUser } from '../../redux/slice/userSlice';
import { getPublicInfoUser } from '../../services/userService';
import i18n from '../../translations';
import { clearUserInfo, getLanguageStorage, getRoleGroup, getUsername, setLanguageStorage } from '../../utils/storage';
import MenuSide from '../menu/MenuSide';
import './sidebar.scss';

const Option = Select.Option;

const Sidebar = () => {
  const { t } = useTranslation();
  const notify = useNotify();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const roleGroup = getRoleGroup();
  const username = getUsername();
  const languageStorage = getLanguageStorage();
  const { isCollapseMenu, isShowMenu, widthSys } = useSelector((state) => state.appReducer);

  const [collapState, setCollapState] = useState(true);
  const [showMenu, setShowMenuState] = useState(null);
  const [publicInfo, setPublicInfo] = useState(null);

  const handleChangeLang = (value) => {
    setLanguageStorage(value);
    dispatch(setValueLang(value));
    i18n.changeLanguage(value);
  };

  const onClick = (value) => {
    const { key } = value;
    if (key === 'dx') {
      clearUserInfo();
      window.location.replace('/');
    } else if (key === 'cp') {
      navigate('/change-password');
    }
  };

  const handleCloseMenu = () => {
    dispatch(setShowMenu(false));
    dispatch(setCollapseMenu(true));
  };

  const handleOnMouse = (type) => {
    if (widthSys >= 876) {
      if (type === 'over') {
        setCollapState(false);
      } else {
        setCollapState(true);
      }
    }
  };

  const items = [
    {
      label: t('logOut'),
      key: 'dx'
    }
  ];

  useEffect(() => {
    setCollapState(isCollapseMenu);
  }, [isCollapseMenu]);

  useEffect(() => {
    setShowMenuState(isShowMenu);
  }, [isShowMenu]);

  useEffect(() => {
    if (widthSys <= 876) {
      dispatch(setShowMenu(false));
    }
    // eslint-disable-next-line
  }, [widthSys]);

  useEffect(() => {
    getPublicInfoUser(
      { username: username, roleGroup: roleGroup, lang: languageStorage },
      (res) => {
        const data = res.data;
        setPublicInfo(data);
        dispatch(savePublicInfoUser(res.data));
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
    // eslint-disable-next-line
  }, [username, roleGroup]);

  return (
    <React.Fragment>
      <div
        className={collapState ? 'left-side-bar side-bar-collap' : 'left-side-bar'}
        onMouseOver={() => handleOnMouse('over')}
        onMouseLeave={() => handleOnMouse('leave')}
      >
        <div className='top-side-bar'>
          <div className='logo-sys'>{collapState ? <img src={Logo} alt='' /> : <img src={IconLogo} alt='' />}</div>
          {collapState ? (
            <div className='user-info-collap'>
              <Dropdown menu={{ items, onClick }} trigger={'hover'}>
                <Space>
                  <img src={IconUser} alt='' />
                </Space>
              </Dropdown>
            </div>
          ) : (
            <div className='user-notify'>
              <div className='user-info'>
                <Dropdown menu={{ items, onClick }} trigger={'hover'}>
                  <Space>
                    <img src={IconUser} alt='' />
                    <span>{publicInfo?.fullname}</span>
                  </Space>
                </Dropdown>
              </div>
              {showMenu ? (
                <img src={IconClose} alt='' onClick={handleCloseMenu} className='img-close-menu' />
              ) : (
                <React.Fragment>{/*<div className='dot-notify'></div>*/}</React.Fragment>
              )}
            </div>
          )}
          <hr className='hr-line' style={{ width: '90%' }}></hr>
          <MenuSide collapsed={collapState} />
        </div>
        <div className={collapState ? 'select-box-lang lang-collap' : 'select-box-lang'}>
          <Select value={languageStorage} onChange={handleChangeLang}>
            <Option value='vi'>
              <div className='ops-item-lang'>
                <img src={ImageVn} alt='' />
                {collapState ? <span></span> : <span>Việt Nam</span>}
              </div>
            </Option>
            <Option value='en'>
              <div className='ops-item-lang'>
                <img src={ImageEn} alt='' />
                {collapState ? <span></span> : <span>English</span>}
              </div>
            </Option>
          </Select>
        </div>
      </div>
      {showMenu && <div className='side-bar-menu'></div>}
    </React.Fragment>
  );
};

export default Sidebar;
