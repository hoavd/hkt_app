import { getRequest, putRequest } from '../api/apiCaller';
import { apiPath } from '../config/apiPath';

export const getPublicInfoUser = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.infoPublicUser, params, successCallback, errorCallback);
};

export const savePublicInfoUser = async (params, successCallback, errorCallback) => {
  await putRequest(apiPath.savePublicUser, params, successCallback, errorCallback);
};
