import { deleteRequest, getFileRequest, getRequest, postRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

// user
export const getListUser = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.listUser, params, successCallback, errorCallback);
};
export const getUserById = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.userRole}/${params}`, {}, successCallback, errorCallback);
};

export const deleteUserRole = async (params, successCallback, errorCallback) => {
  await deleteRequest(`${apiPath.userRole}/${params.id}`, params, successCallback, errorCallback);
};
export const addUserRole = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.userRole, params, successCallback, errorCallback);
};
export const editUserRole = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.userRole}/${params.id}`, params, successCallback, errorCallback);
};
export const getListRoleReq = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListRoleFromUser, params, successCallback, errorCallback);
};
export const getListCurrencyPair = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListCurrencyPairFromUser, params, successCallback, errorCallback, 10000);
};
export const getListBranch = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListBranchFromUser, params, successCallback, errorCallback, 10000);
};

export const getListGroupView = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListGroupViewFromUser, params, successCallback, errorCallback, 10000);
};
export const getListConditionDeal = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListConditionDealFromUser, params, successCallback, errorCallback, 10000);
};
export const downloadFileUserReq = async (params, successCallback, errorCallback) => {
  await getFileRequest(apiPath.downloadFileUser, params, successCallback, errorCallback, 10000);
};
export const importFileUserReq = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.importFileUser, params, successCallback, errorCallback, 10000);
};
