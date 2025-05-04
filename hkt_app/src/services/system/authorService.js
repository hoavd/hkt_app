import { getRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';
// history
export const getListHistoryAction = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListHistory, params, successCallback, errorCallback);
};
//
// log filter
export const getListLogFilter = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListLogFilter, params, successCallback, errorCallback);
};
// error system
export const getListErrorSystem = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListErrSys, params, successCallback, errorCallback);
};
// param system
export const getListParamSystem = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListParamSys, params, successCallback, errorCallback);
};

export const editParamSystem = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.eidtParamSys}/${params.id}`, params, successCallback, errorCallback);
};
