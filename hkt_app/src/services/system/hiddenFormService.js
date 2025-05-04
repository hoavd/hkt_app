import { getRequest, postRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

export const getListFormHidden = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.formHidden, params, successCallback, errorCallback);
};

export const getListColumn = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.columnFormHidden, params, successCallback, errorCallback);
};

export const addFormHidden = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.formHidden, params, successCallback, errorCallback);
};

export const editFormHidden = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.formHidden}/${params.id}`, params, successCallback, errorCallback);
};

export const getFormHiddenDetail = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.formHidden}/${params.id}`, {}, successCallback, errorCallback);
};
