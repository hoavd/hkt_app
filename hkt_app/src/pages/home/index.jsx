import { Dropdown } from 'antd';
import React from 'react';
import iconCall from '../../assets/imageSvg/call.svg';
import iconSupport from '../../assets/imageSvg/icon-support.svg';
import iconSms from '../../assets/imageSvg/sms.svg';
import './style.scss';
import { t } from 'i18next';
import Dashboard from './dashboard/Dashboard';

const MAIL_CONFIG = window.CONSTANT.MAIL_SUPPORT;
const PHONE_CONFIG = window.CONSTANT.PHONE_SUPPORT;
// const TEXT_EXCHANGE = window.CONSTANT.TEXT_EXCHANGE;

const Home = () => {
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

  return (
    <React.Fragment>
      <div className='container-home'>
        <div className='top-exchange-home'>
          <div className='left-top'></div>
          <div className='right-top'>
            <Dropdown menu={{ items }} trigger={'hover'}>
              <div className='support'>
                <span>{t('support')}</span>
                <img src={iconSupport} alt='' />
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
