import { Button, Form, Input, Select } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { useDispatch } from 'react-redux';
import * as yup from 'yup';
import ImageEn from '../../assets/imageSvg/image-en.svg';
import ImageVn from '../../assets/imageSvg/image-vn.svg';
import Logo from '../../assets/imageSvg/logo-home.svg';
import ImageLogo from '../../assets/imageSvg/logo.svg';
import Loading from '../../components/loading/Loading';
import useNotify from '../../hooks/useNotify';
import { setWidthSystem } from '../../redux/slice/appSlice';
import { saveInfoUserLogin } from '../../redux/slice/userSlice';
import { loginRequest } from '../../services/loginService';
import { getPublicInfoUser } from '../../services/userService';
import i18n from '../../translations';
import {
  getLanguageStorage,
  getRefreshToken,
  getRole,
  getRoleGroup,
  getUsername,
  saveUserInfo,
  setLanguageStorage
} from '../../utils/storage';
import './style.scss';

let schema = yup.object().shape({
  username: yup.string().required('Username không được để trống'),
  password: yup.string().required('Password không được để trống')
});

const yupSync = {
  async validator({ field }, value) {
    await schema.validateSyncAt(field, { [field]: value });
  }
};

const Option = Select.Option;

const Login = () => {
  const refLogin = useRef(null);
  const dispatch = useDispatch();
  const notify = useNotify();
  const languageStorage = getLanguageStorage();
  const [form] = Form.useForm();

  const [isLoad, setIsLoad] = useState(false);
  const [dataLogin, setDataLogin] = useState(null);
  const [widthDiv, setWidthDiv] = useState(null);

  const handleChangeLang = (value) => {
    setLanguageStorage(value);
    i18n.changeLanguage(value);
  };

  const onLogin = (values) => {
    setIsLoad(true);
    const { username, password } = values;
    loginRequest(
      {
        grant_type: 'password',
        username: username,
        password: password,
        refresh_token: getRefreshToken(),
        lang: languageStorage
      },
      (res) => {
        if (res.data?.access_token) {
          const { access_token, expires_in, roles, roleGroups, token_type, username } = res.data;
          saveUserInfo(access_token, expires_in, roles, roleGroups, token_type, username);
          setDataLogin(res.data);
          setIsLoad(false);
        } else {
          setIsLoad(false);
          notify.error(res?.data?.msg);
        }
      },
      (err) => {
        setIsLoad(false);
        notify.error(err?.data?.msg);
      }
    );
  };

  useEffect(() => {
    setWidthDiv(refLogin?.current?.offsetWidth);
    const getwidth = () => {
      setWidthDiv(refLogin?.current?.offsetWidth);
    };
    window.addEventListener('resize', getwidth);
    return () => window.removeEventListener('resize', getwidth);
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (widthDiv > 0) {
      const delayDebounceFn = setTimeout(() => {
        dispatch(setWidthSystem(widthDiv));
      }, 500);
      return () => clearTimeout(delayDebounceFn);
    }
    // eslint-disable-next-line
  }, [widthDiv]);

  useEffect(() => {
    if (dataLogin?.username) {
      getPublicInfoUser(
        { username: dataLogin.username, roleGroup: dataLogin.roleGroups[0], lang: languageStorage },
        (res) => {
          if (res.data?.username) {
            const { roles, username } = dataLogin;
            dispatch(saveInfoUserLogin({ username: username, roles: roles }));
            setIsLoad(false);
          } else {
            notify.warning('403 Forbidden');
            setIsLoad(false);
          }
        },
        () => {
          setIsLoad(false);
        }
      );
    }
    // eslint-disable-next-line
  }, [dataLogin]);

  useEffect(() => {
    const username = getUsername();
    const roles = getRole();
    const roleGroup = getRoleGroup();
    setLanguageStorage(languageStorage ? languageStorage : 'vi');
    if (username && roleGroup) dispatch(saveInfoUserLogin({ username: username, roles: roles }));
    // eslint-disable-next-line
  }, []);

  return (
    <div className='login-form-new' ref={refLogin}>
      <div className='content-left'>
        <div className='content-right-desc'>
          <img src={Logo} className='image-logo' alt='' style={{ display: 'none' }} />
          <span className='title-platform'></span>
          <span className='desc-platform'></span>
        </div>
      </div>
      <div className='content-right'>
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
          <span className='title-page-login'>Log in to your account</span>
          <span className='desc-page-login'>Welcome back! Please enter your details.</span>
          <Form form={form} size='large' layout='vertical' className='form-input-login' onFinish={onLogin}>
            <Form.Item name='username' label='Email ( Username )' rules={[yupSync]}>
              <Input
                placeholder='Username'
                onChange={(e) => {
                  const value = e.target.value;
                  form.setFieldsValue({ username: String(value).toLowerCase() });
                }}
              />
            </Form.Item>
            <Form.Item name='password' label='Password' rules={[yupSync]}>
              <Input type='password' placeholder='Password' />
            </Form.Item>
            <Form.Item>
              <Button className='btn-login' type='primary' htmlType='submit'>
                <span className='text-bold text-14 text-white'>Sign in</span>
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div className='footer-login'>
          <span>© 2025</span>
          <span></span>
        </div>
      </div>

      {isLoad && <Loading />}
    </div>
  );
};

export default Login;
