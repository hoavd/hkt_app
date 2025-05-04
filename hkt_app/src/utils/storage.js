/* eslint-env browser */
export const checkDataInLocalStorage = (data) => {
  if (!data || data === null || data === undefined || data === 'null' || data === 'undefined') {
    return false;
  }
  return true;
};

export const saveUserInfo = (access_token, expires_in, role, roleGroup, token_type, username) => {
  localStorage.setItem('_token', access_token);
  localStorage.setItem('_expiresIn', expires_in);
  localStorage.setItem('_role', JSON.stringify(role));
  localStorage.setItem('_token_type', token_type);
  localStorage.setItem('_username', username);
  localStorage.setItem('_role_group', roleGroup);
};

export const clearUserInfo = () => {
  localStorage.removeItem('_token');
  localStorage.removeItem('_refreshToken');
  localStorage.removeItem('_expiresIn');
  localStorage.removeItem('_jti');
  localStorage.removeItem('_role');
  localStorage.removeItem('_token_type');
  localStorage.removeItem('_uuid');
  localStorage.removeItem('_username');
  localStorage.removeItem('_role_group');
  localStorage.removeItem('_list_type');
};

export const getUsername = () => {
  const token = localStorage.getItem('_username');
  return token;
};

export const clearUsername = () => {
  localStorage.removeItem('username_rmb');
};

export const getToken = () => {
  const token = localStorage.getItem('_token');
  return token;
};

export const getRole = () => {
  const role = localStorage.getItem('_role');
  return role;
};

export const getRoleGroup = () => {
  const roleGroup = localStorage.getItem('_role_group');
  return roleGroup;
};

export const getTokenType = () => {
  const tokenType = localStorage.getItem('_token_type');
  return tokenType;
};

export const getRefreshToken = () => {
  const rfToken = localStorage.getItem('_refreshToken');
  return rfToken;
};

export const getTimeExpr = () => {
  const time = localStorage.getItem('_timeExpr');
  return time;
};

export const getUserInfo = () => {
  const userInfo = localStorage.getItem('_currentUser');
  return JSON.parse(userInfo);
};

export const saveUserRemember = (username) => {
  if (username) {
    localStorage.setItem('username_rmb', username);
  }
};

export const removeUserRemember = () => {
  localStorage.removeItem('username_rmb');
};

export const getUsernameRemember = () => {
  const username = localStorage.getItem('username_rmb');
  if (checkDataInLocalStorage(username)) {
    return username;
  }
  return '';
};

export const setToken = (token) => {
  localStorage.setItem('_token', token);
};

export const setRefeshToken = (refeshToken) => {
  localStorage.setItem('_refeshToken', refeshToken);
};

export const getUid = () => {
  const uid = localStorage.getItem('_uuid');
  if (checkDataInLocalStorage(uid)) {
    return +uid;
  }
  return '';
};

export const getAuthenticationName = () => {
  const username = localStorage.getItem('authenticationName');
  if (checkDataInLocalStorage(username)) {
    return username;
  }
  return '';
};

export const setListAvatarDevice = (listAvatar) => {
  localStorage.setItem('_listAvatarDevice', JSON.stringify(listAvatar));
};

export const getListAvatarDevice = () => {
  const listAvatars = localStorage.getItem('_listAvatarDevice');

  return JSON.parse(listAvatars);
};

export const getEmail = () => {
  const email = localStorage.getItem('_email');
  if (checkDataInLocalStorage(email)) {
    return email;
  }
  return '';
};

export const setLanguageStorage = (lang) => {
  localStorage.setItem('language', lang);
};

export const getLanguageStorage = () => {
  return localStorage.getItem('language');
};

export const saveCurrenPair = (listCurren) => {
  localStorage.setItem('_currenPair', JSON.stringify(listCurren));
};
