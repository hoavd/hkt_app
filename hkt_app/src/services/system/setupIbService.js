import { getRequest, postRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

export const getListSetupIbReq = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.ibmbApiList, params, successCallback, errorCallback);
};

export const addConfigIbReq = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.ibmbApiAdd, params, successCallback, errorCallback);
};

export const editConfigIbReq = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.ibmbApiEdit}/${params.id}`, params, successCallback, errorCallback);
};
