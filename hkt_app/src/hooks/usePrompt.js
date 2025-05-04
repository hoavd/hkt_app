import { useEffect } from 'react';

export const FormPrompt = ({ hasUnsavedChanges }) => {
  useEffect(() => {
    if (hasUnsavedChanges) {
      const alertUser = (e) => {
        e.preventDefault();
        e.returnValue = '';
      };
      window.addEventListener('beforeunload', alertUser);
      return () => {
        window.removeEventListener('beforeunload', alertUser);
      };
    }
  }, [hasUnsavedChanges]);
};
