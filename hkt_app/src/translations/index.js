import { initReactI18next } from 'react-i18next';
import i18n from 'i18next';
import translationEN from './en/translation.json';
import translationVI from './vi/translation.json';

const resources = {
  en: {
    translation: translationEN
  },
  vi: {
    translation: translationVI
  }
};
// eslint-disable-next-line
i18n.use(initReactI18next).init({
  resources: resources,
  lng: 'vi',
  fallbackLng: 'vi',
  debug: true,
  interpolation: {
    escapeValue: false
  }
});

export default i18n;
