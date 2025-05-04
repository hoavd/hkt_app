import { getRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';
// sidebar
export const getListSidebar = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.sidebar, params, successCallback, errorCallback);
};

export const getSidebarItem = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.sidebar}/${params}`, {}, successCallback, errorCallback);
};

export const getListRoleSidebar = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.listRoleSideBar, params, successCallback, errorCallback);
};

export const putRoleSidebar = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.sidebar}/${params.id}`, params, successCallback, errorCallback);
};

export const getNavItemRequest = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.navItem, params, successCallback, errorCallback);
};
