import { deleteRequest, getRequest, postRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';
// role
export const getListRole = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.role, params, successCallback, errorCallback);
};

export const getRoleById = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.role}/${params}`, {}, successCallback, errorCallback);
};

export const getListModule = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListModule, params, successCallback, errorCallback);
};

export const getListChucNang = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListChucNang, params, successCallback, errorCallback);
};

export const getListSidebarInRole = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListSidebarInRole, params, successCallback, errorCallback);
};

export const postCreateRole = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.role, params, successCallback, errorCallback);
};

export const postEditRole = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.role}/${params.id}`, params, successCallback, errorCallback);
};

export const clearCachedRequestmaps = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.clearCachedRequestmaps, params, successCallback, errorCallback);
};

export const clearCachedRequestmapsService = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.clearCachedRequestmapsService, params, successCallback, errorCallback, 10000);
};

export const putConfigConditionDeal = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.role}/configConditionDeal/${params.id}`, params, successCallback, errorCallback);
};

export const putConfigSidebarItem = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.role}/configSidebarItem/${params.id}`, params, successCallback, errorCallback);
};

export const deleteRole = async (params, successCallback, errorCallback) => {
  await deleteRequest(`${apiPath.role}/${params.id}`, {}, successCallback, errorCallback);
};

export const getListTransactionType = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListTransactionType, params, successCallback, errorCallback, 10000);
};

export const getListRoleGroup = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListRoleGroup, params, successCallback, errorCallback, 10000);
};

export const getListConditionDeal = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListConditionDealFromRole, params, successCallback, errorCallback, 10000);
};
export const getListCurrencyPair = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListCurrencyPairFromRole, params, successCallback, errorCallback, 10000);
};

/*phan quyen group cif*/
export const getRoleGroupCif = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.roleGroupCif}/${params}`, {}, successCallback, errorCallback);
};

export const getListGroupCifInRole = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getListGroupCifInRole, params, successCallback, errorCallback);
};

export const putPermissionGroupCif = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.roleGroupCif}/permissionGroupCif/${params.id}`, params, successCallback, errorCallback);
};
/*--*/

//
