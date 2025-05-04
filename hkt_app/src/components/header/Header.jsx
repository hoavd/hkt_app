import { Dropdown } from 'antd';
import { useDispatch } from 'react-redux';
import iconCall from '../../assets/imageSvg/call.svg';
import iconSupport from '../../assets/imageSvg/icon-support.svg';
import IconLogo from '../../assets/imageSvg/logo.svg';
import ImgMenu from '../../assets/imageSvg/menu.svg';
import logoTcb from '../../assets/imageSvg/tcb-logo.svg';
import iconSms from '../../assets/imageSvg/sms.svg';
import { setCollapseMenu, setShowMenu } from '../../redux/slice/appSlice';
import './header.scss';

const MAIL_CONFIG = window.CONSTANT.MAIL_SUPPORT;
const PHONE_CONFIG = window.CONSTANT.PHONE_SUPPORT;

const Header = () => {
  const dispatch = useDispatch();

  const items = [
    {
      label: (
        <div className='d-flex'>
          <img src={iconSms} alt='' />
          <span style={{ marginLeft: 8 }}>{MAIL_CONFIG}</span>
        </div>
      ),
      key: 'mail'
    },
    {
      label: (
        <div className='d-flex'>
          <img src={iconCall} alt='' />
          <span style={{ marginLeft: 8 }}>{PHONE_CONFIG}</span>
        </div>
      ),
      key: 'number'
    }
  ];

  const handleShowMenu = () => {
    dispatch(setShowMenu(true));
    dispatch(setCollapseMenu(false));
  };

  return (
    <div className='header-container'>
      <div className='header-content'>
        <div className='header-content-left'>
          <img src={ImgMenu} alt='' onClick={handleShowMenu} />
          <img src={IconLogo} alt='' className='logo' />
        </div>
        <div className='header-content-right'>
          <Dropdown menu={{ items }} trigger={'hover'}>
            <div className='support'>
              <img src={iconSupport} alt='' />
            </div>
          </Dropdown>
          <div className='logo-personal'>
            <img src={logoTcb} alt='' />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Header;
