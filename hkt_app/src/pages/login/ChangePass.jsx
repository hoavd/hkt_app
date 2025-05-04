import { Button, Form, Input, Select } from 'antd';
import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import ImageEn from '../../assets/imageSvg/image-en.svg';
import ImageVn from '../../assets/imageSvg/image-vn.svg';
import Logo from '../../assets/imageSvg/logo-home.svg';
import ImageLogo from '../../assets/imageSvg/logo.svg';
import Loading from '../../components/loading/Loading';
import useNotify from '../../hooks/useNotify';
import { changePassRequest } from '../../services/loginService';
import i18n from '../../translations';
import { clearUserInfo, getLanguageStorage, setLanguageStorage } from '../../utils/storage';
import './style.scss';

const Option = Select.Option;

const ChangePass = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const notify = useNotify();
  const languageStorage = getLanguageStorage();
  const [form] = Form.useForm();

  const [isLoad, setIsLoad] = useState(false);

  const handleChangeLang = (value) => {
    setLanguageStorage(value);
    i18n.changeLanguage(value);
  };

  const onChangePass = (values) => {
    setIsLoad(true);
    const { currentPassword, newPassword } = values;
    changePassRequest(
      {
        currentPassword: currentPassword,
        newPassword: newPassword
      },
      (res) => {
        if (res.data.success) {
          clearUserInfo();
          navigate('/login');
          setIsLoad(false);
        } else {
          setIsLoad(false);
          notify.error(res.data.msg);
        }
      },
      (err) => {
        setIsLoad(false);
        notify.error(err?.data?.msg);
      }
    );
  };

  useEffect(() => {
    setLanguageStorage(languageStorage ? languageStorage : 'vi');
    // eslint-disable-next-line
  }, []);

  return (
    <div className='login-form-new'>
      <div className='content-left'>
        <div className='header-logo'>
          <img src={ImageLogo} alt='' />
          <Select defaultValue={languageStorage || 'vi'} onChange={handleChangeLang}>
            <Option value='vi'>
              <div className='ops-item-lang'>
                <img src={ImageVn} alt='' />
                <span>Việt Nam</span>
              </div>
            </Option>
            <Option value='en'>
              <div className='ops-item-lang'>
                <img src={ImageEn} alt='' />
                <span>English</span>
              </div>
            </Option>
          </Select>
        </div>
        <div className='content-form'>
          <span className='title-page-login'>{t('changePass')}</span>
          <span className='desc-page-login'></span>
          <Form form={form} size='large' layout='vertical' className='form-input-change' onFinish={onChangePass}>
            <Form.Item
              name='currentPassword'
              label={t('oldPass')}
              rules={[{ required: true, message: t('requidPass') }]}
            >
              <Input.Password type='password' placeholder='' />
            </Form.Item>
            <Form.Item
              name='newPassword'
              label={t('newPass')}
              rules={[
                { required: true, message: t('requidPass') },
                { min: 8, message: t('minLengPass') },
                {
                  pattern: new RegExp(
                    /(?=[A-Za-z0-9@#$%^&+!=]+$)^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+!=])(?=.{8,}).*$/
                  ),
                  message: t('checkPass')
                }
              ]}
            >
              <Input.Password type='password' placeholder='' />
            </Form.Item>
            <Form.Item
              name='confirmPassword'
              label={t('confirmPass')}
              rules={[
                { required: true, message: t('requidConfirmPass') },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error(t('checkConfirmPass')));
                  }
                })
              ]}
            >
              <Input.Password type='password' placeholder='' />
            </Form.Item>
            <Form.Item>
              <div className='d-flex'>
                <Button className='btn-login' type='primary' htmlType='submit'>
                  <span className='text-bold text-14 text-white'>{t('confirm')}</span>
                </Button>
                <Button
                  className='btn-back'
                  type='primary'
                  htmlType='submit'
                  style={{ marginLeft: 8 }}
                  onClick={() => {
                    navigate('/');
                  }}
                >
                  <span className='text-bold text-14 text-white'>{t('backHome')}</span>
                </Button>
              </div>
            </Form.Item>
          </Form>
        </div>
        <div className='footer-login'>
          <span>© APP_DEMO 2025</span>
          <span>help@APP_DEMO.com</span>
        </div>
      </div>
      <div className='content-right'>
        <div className='content-right-desc'>
          <img src={Logo} className='image-logo' alt='' />
          <span className='title-platform'>Welcome to APP_DEMO platform</span>
          <span className='desc-platform'>
            We are glad to see you again! Instant deposits, with drawals & payouts trusted by millions worldwide.
          </span>
        </div>
      </div>
      {isLoad && <Loading />}
    </div>
  );
};

export default ChangePass;
