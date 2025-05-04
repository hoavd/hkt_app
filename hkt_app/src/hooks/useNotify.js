import { toast } from 'react-toastify';
import {
  DEFAULT_TIME_AUTO_HIDE_TOASTIFY,
  DEFAULT_TIME_AUTO_HIDE_TOASTIFY_ERROR,
  DEFAULT_TIME_AUTO_HIDE_TOASTIFY_TRANSACTION
} from '../constants/appConstant';

const useNotify = () => {
  const error = (message, position = 'top-right') => {
    toast.error(message, {
      position: position,
      autoClose: DEFAULT_TIME_AUTO_HIDE_TOASTIFY_ERROR,
      hideProgressBar: false,
      theme: 'colored',
      closeOnClick: true,
      pauseOnHover: true
    });
  };

  const success = (message, isCheck, position = 'top-right') => {
    toast.success(message, {
      position: position,
      autoClose: isCheck ? DEFAULT_TIME_AUTO_HIDE_TOASTIFY_TRANSACTION : DEFAULT_TIME_AUTO_HIDE_TOASTIFY,
      hideProgressBar: false,
      theme: 'colored',
      closeOnClick: true,
      pauseOnHover: true
    });
  };

  const warning = (message, position = 'top-right', autoClose) => {
    toast.warning(message, {
      position: position,
      autoClose: autoClose ? autoClose : DEFAULT_TIME_AUTO_HIDE_TOASTIFY,
      hideProgressBar: false,
      theme: 'colored',
      closeOnClick: true,
      pauseOnHover: true
    });
  };

  const info = (message, position = 'top-right', autoClose) => {
    toast.info(message, {
      position: position,
      autoClose: autoClose || DEFAULT_TIME_AUTO_HIDE_TOASTIFY,
      hideProgressBar: true,
      theme: 'colored',
      closeButton: false
    });
  };

  return { error, success, warning, info };
};

export default useNotify;
