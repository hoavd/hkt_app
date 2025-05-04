import React from 'react';
import { Navigate } from 'react-router-dom';
import { getRole } from '../utils/storage';

const PrivateRoute = ({ children, auth }) => {
  const isAuthenticated = () => {
    const role = getRole();
    if (role === auth) return true;
    return false;
  };

  return isAuthenticated() ? children : <Navigate to={'/'} />;
};

export default PrivateRoute;
