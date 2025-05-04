import { postRequest, putRequest } from '../api/apiCaller';
import { apiPath } from '../config/apiPath';

export const loginRequest = async (params, successCallback, errorCallback) => {
  await postRequest(`${apiPath.login}?lang=${params.lang}`, params, successCallback, errorCallback);
};

export const changePassRequest = async (params, successCallback, errorCallback) => {
  await putRequest(apiPath.changePass, params, successCallback, errorCallback);
};
