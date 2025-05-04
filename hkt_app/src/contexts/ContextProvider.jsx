import React, { useState, createContext } from 'react';

export const ContextProvider = createContext({});

const ContextProviderLayout = ({ children }) => {
  const [popup, setPopup] = useState({ show: false, text: '' });

  return <ContextProvider.Provider value={{ popup, setPopup }}>{children}</ContextProvider.Provider>;
};

export default ContextProviderLayout;
