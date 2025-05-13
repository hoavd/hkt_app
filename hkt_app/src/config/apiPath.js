// export const BASE_URL = 'http://localhost:8080/api';
// export const BASE_URL_SOCKET = 'http://localhost:8080/stomp';
// export const BASE_URL = 'http://10.84.251.210:8080/api';
// export const BASE_URL = 'http://10.254.61.24:8080/fx_core/api';
export const BASE_URL = window.CONSTANT.BASE_URL;
export const BASE_URL_SERVICE = window.CONSTANT.BASE_URL_SERVICE;
export const BASE_URL_SOCKET = window.CONSTANT.BASE_URL_SOCKET;

export const apiPath = {
  // Account
  login: BASE_URL + '/login',
  changePass: BASE_URL + '/user/changePassWord',
  infoPublicUser: BASE_URL + '/user/publicInfo',
  savePublicUser: BASE_URL + '/user/savePublicInfo',
  // sidebar
  navItem: BASE_URL + '/fingerprint/navItem',
  sidebar: BASE_URL + '/sidebarItem',
  listRoleSideBar: BASE_URL + '/sidebarItem/listRole',
  // Branch
  branch: BASE_URL + '/branch',
  branchTree: BASE_URL + '/branch/tree',
  //
  getVolume: BASE_URL + '/dashboard/volume',
  //
  findAlertRequest: BASE_URL + '/dashboard/findAlert',
  findSolution: BASE_URL + '/dashboard/findSolution',
  getAlert: BASE_URL + '/dashboard/alert',
  chatAISystem: BASE_URL + '/dashboard/pushMessage',
  getChatAISystem: BASE_URL + '/dashboard/getMessage'
};

export const topicPath = {
  topicAlert: '/topic/alert',
  topicVolume: '/topic/volume',
  topicMessage: '/topic/message'
};
