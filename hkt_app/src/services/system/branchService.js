import { getRequest, postRequest, deleteRequest, putRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

export const getBranchRequest = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.branch, params, successCallback, errorCallback);
};

export const getBranchTreeRequest = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.branchTree, params, successCallback, errorCallback);
};

export const addNewBranchRequest = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.branch, params, successCallback, errorCallback);
};

export const editBranchRequest = async (params, successCallback, errorCallback) => {
  await putRequest(`${apiPath.branch}/${params.id}`, params, successCallback, errorCallback);
};

export const deleteBranchRequest = async (params, successCallback, errorCallback) => {
  await deleteRequest(`${apiPath.branch}/${params.id}`, params, successCallback, errorCallback);
};

export const getListClearingMode = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.branch}/listClearingMode`, params, successCallback, errorCallback);
};

export const getListBranchRegion = async (params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.branch}/listBranchRegion`, params, successCallback, errorCallback);
};

export const searchBranchRequest = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.branch, params, successCallback, errorCallback, 10000);
};

export const getBranchRegion = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.reionList, params, successCallback, errorCallback);
};
