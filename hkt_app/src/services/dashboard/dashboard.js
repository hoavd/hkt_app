import { getRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

export const getVolume = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getVolume, params, successCallback, errorCallback);
};
