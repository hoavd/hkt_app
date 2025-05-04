import { getRequest } from '../api/apiCaller';
import { BASE_URL } from '../config/apiPath';

export const getCompleteRequest = async (url, params, successCallback, errorCallback) => {
  await getRequest(`${BASE_URL}/${url}`, params, successCallback, errorCallback);
};
