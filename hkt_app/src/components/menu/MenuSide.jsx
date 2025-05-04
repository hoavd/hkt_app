import { Menu } from 'antd';
import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router-dom';
import useNotify from '../../hooks/useNotify';
import { setCollapseMenu, setShowMenu } from '../../redux/slice/appSlice';
import { getNavItemRequest } from '../../services/system/sidebarService';
import { getLanguageStorage } from '../../utils/storage';

const MenuSide = ({ width, collapsed }) => {
  const { pathname } = useLocation();
  const languageStorage = getLanguageStorage();
  const notify = useNotify();
  const arrUrl = pathname.split('/');
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { valueLang } = useSelector((state) => state.appReducer);

  const [keySelect, setKeySelect] = useState(null);
  const [openKey, setOpenKey] = useState(null);
  const [open, setOpen] = useState(false);
  const [navItem, setNavItem] = useState([]);

  const onClickSubMenu = (data) => {
    const { key } = data;
    dispatch(setShowMenu(false));
    dispatch(setCollapseMenu(true));
    if (!data.items?.length) {
      setKeySelect(null);
      setOpen(!open);
      navigate(`/${key === '/' ? '' : key}`);
    }
  };

  useEffect(() => {
    setOpenKey(arrUrl[1]);
    setKeySelect(`${arrUrl[1]}/${arrUrl[2]}`);
    setOpen(true);
  }, [arrUrl]);

  useEffect(() => {
    getNavItemRequest(
      { lang: languageStorage },
      (res) => {
        const dataNav = res.data;
        if (dataNav?.length) {
          let dataNavMap = [];
          dataNav.forEach((element) => {
            let itemChild = element.items;
            let itemNav = {
              label: element.title,
              icon: <i className={element.icon} />,
              key: element.href
            };
            if (itemChild.length) {
              let itemChildMap = [];
              itemChild.forEach((eleChild) => {
                let itemNavChi = {
                  label: eleChild.title,
                  icon: <i className={eleChild.icon} />,
                  key: eleChild.href
                };
                itemChildMap.push(itemNavChi);
              });
              itemNav.children = itemChildMap;
            }
            dataNavMap.push(itemNav);
          });
          setNavItem(dataNavMap);
        }
      },
      (err) => {
        notify.error(err?.data?.msg);
      }
    );
    // eslint-disable-next-line
  }, [valueLang]);

  return (
    <Menu
      style={{ width: width ? width : '100%' }}
      selectedKeys={[keySelect !== '/' ? keySelect : null]}
      defaultOpenKeys={[openKey]}
      mode='inline'
      inlineCollapsed={collapsed}
      items={navItem}
      onClick={onClickSubMenu}
    />
  );
};

export default MenuSide;
