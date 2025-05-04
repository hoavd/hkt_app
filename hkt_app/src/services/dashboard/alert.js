import { getRequest, postRequest } from '../../api/apiCaller';
import { apiPath } from '../../config/apiPath';

export const getAlert = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getAlert, params, successCallback, errorCallback);
};
export const findAlertRequest = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.findAlertRequest, params, successCallback, errorCallback);
};

export const chatAiSystem = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.chatAISystem, params, successCallback, errorCallback);
};

export const getChatAiSystem = async (params, successCallback, errorCallback) => {
  await getRequest(apiPath.getChatAISystem, params, successCallback, errorCallback);
};
