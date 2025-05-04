import axios from 'axios';
import { toast } from 'react-toastify';
import { DEFAULT_TIME_AUTO_HIDE_TOASTIFY } from '../constants/appConstant';
import { clearUserInfo, getRoleGroup, getToken } from '../utils/storage';
const BASE_URL = window.CONSTANT.BASE_URL;

const notificationWarning = (message, position = 'top-right', autoClose) => {
  toast.warning(message, {
    position: position,
    autoClose: autoClose || DEFAULT_TIME_AUTO_HIDE_TOASTIFY,
    hideProgressBar: true
  });
};

axios.interceptors.request.use((config) => {
  const token = getToken();
  const roleGroup = getRoleGroup();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  if (roleGroup) {
    config.headers.roleGroup = roleGroup;
  }
  return config;
});

axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    return error;
  }
);

export const getFileRequest = async (url = '', params, successCallback, errorCallback, timeout) => {
  return axios
    .get(url, {
      params,
      timeout,
      responseType: 'blob',
      headers: {
        'Content-Type': 'application/vnd.ms-excel',
        'Content-Disposition': 'attachment;filename=report.xls'
      }
    })
    .then((response) => {
      if (successCallback) {
        try {
          if (response?.status === 401 || response?.response?.status === 401) {
            // notificationWarning('Token expried');
            setTimeout(() => logout(), 1000);
          } else if (response?.status === 403 || response?.response?.status === 403) {
            notificationWarning('403 Forbidden');
            setTimeout(() => changeHome(), 1000);
          } else if (response?.status < 300 || response?.response?.status < 300) successCallback(response);
          else {
            errorCallback(response);
            notificationWarning('Có lỗi xảy ra!');
          }
        } catch (error) {
          console.log(error);
        }
      }
    })
    .catch((error) => {
      if (errorCallback)
        try {
          errorCallback(error);
        } finally {
          console.log(error);
        }
    });
};

export const getRequest = async (url = '', params, successCallback, errorCallback, timeout) => {
  return axios
    .get(url, { params, timeout })
    .then((response) => {
      if (successCallback) {
        try {
          if (response?.status === 401 || response?.response?.status === 401) {
            // notificationWarning('Token expried');
            setTimeout(() => logout(), 1000);
          } else if (response?.status === 403 || response?.response?.status === 403) {
            if (url.indexOf('/transactionStatusReport') !== -1) {
              return;
            } else if (url.indexOf('/transaction/getListTrans') !== -1) {
              successCallback(response);
            } else if (url.indexOf('/exportTempImportDeal') !== -1) {
              successCallback(response);
            } else if (url.indexOf('/user/publicInfo') !== -1) {
              successCallback(response);
            } else {
              notificationWarning('403 Forbidden');
              setTimeout(() => changeHome(), 1000);
            }
          } else if (response?.status < 300 || response?.response?.status < 300) successCallback(response);
          else errorCallback(response);
        } catch (error) {
          console.log(error);
        }
      }
    })
    .catch((error) => {
      if (errorCallback)
        try {
          errorCallback(error);
        } finally {
          console.log(error);
        }
    });
};

export const postRequest = async (url = '', body, successCallback, errorCallback) => {
  return await axios
    .post(url, body)
    .then((response) => {
      if (successCallback) {
        try {
          if (response?.status === 401 || response?.response?.status === 401) {
            if (url.indexOf(`${BASE_URL}/login`) > -1) {
              notificationWarning(response?.response?.data?.message);
              errorCallback(response);
            } else {
              setTimeout(() => logout(), 1000);
            }
          } else if (
            (response?.status === 403 && `${BASE_URL}/login` !== url) ||
            (response?.response?.status === 403 && `${BASE_URL}/login` !== url)
          ) {
            if (url.indexOf('/importDeal') !== -1) {
              successCallback(response);
            } else {
              notificationWarning('403 Forbidden');
              setTimeout(() => changeHome(), 1000);
            }
          } else if (response?.status < 300 || response?.response?.status < 300) successCallback(response);
          else errorCallback(response);
        } catch (error) {
          console.log(error);
        }
      }
    })
    .catch((error) => {
      if (errorCallback)
        try {
          errorCallback(error);
        } finally {
          console.log(error);
        }
    });
};

export const putRequest = async (url = '', body = {}, successCallback, errorCallback, headers = {}, timeout) => {
  return axios
    .put(url, body, {
      headers,
      timeout
    })
    .then((response) => {
      if (successCallback) {
        try {
          if (response?.status === 401 || response?.response?.status === 401) {
            // notificationWarning('Token expried');
            setTimeout(() => logout(), 1000);
          } else if (response?.status === 403 || response?.response?.status === 403) {
            notificationWarning('403 Forbidden');
            setTimeout(() => changeHome(), 1000);
          } else if (response?.status < 300 || response?.response?.status < 300) successCallback(response);
          else errorCallback(response);
        } catch (error) {
          console.log(error);
        }
      }
    })
    .catch((error) => {
      if (errorCallback)
        try {
          errorCallback(error);
        } finally {
          console.log(error);
        }
    });
};

export const deleteRequest = async (url = '', params = {}, successCallback, errorCallback) => {
  return axios
    .delete(url, {
      data: params
    })
    .then((response) => {
      if (successCallback) {
        try {
          if (response?.status === 401 || response?.response?.status === 401) {
            // notificationWarning('Token expried');
            setTimeout(() => logout(), 1000);
          } else if (response?.status === 403 || response?.response?.status === 403) {
            notificationWarning('403 Forbidden');
            setTimeout(() => changeHome(), 1000);
          } else if (response?.status < 300 || response?.response?.status < 300) successCallback(response);
          else errorCallback(response);
        } catch (error) {
          console.log(error);
        }
      }
    })
    .catch((error) => {
      if (errorCallback)
        try {
          errorCallback(error);
        } finally {
          console.log(error);
        }
    });
};

const logout = () => {
  clearUserInfo();
  window.location.replace('/');
};

const changeHome = () => {
  window.location.replace('/#/fobbidden');
};
