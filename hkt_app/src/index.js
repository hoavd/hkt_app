import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { store } from './redux/store';
import { Provider } from 'react-redux';
import 'bootstrap/dist/css/bootstrap.css';

import { I18nextProvider } from 'react-i18next';
import i18n from './translations';

ReactDOM.createRoot(document.getElementById('root')).render(
  <I18nextProvider i18n={i18n}>
    <Provider store={store}>
      <App />
    </Provider>
  </I18nextProvider>
);
