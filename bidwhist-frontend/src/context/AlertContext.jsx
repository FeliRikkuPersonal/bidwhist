import React, { createContext, useState, useContext } from 'react';
import Alert from '../components/Alert'

// Create the Alert context
const AlertContext = createContext();

// Create a custom hook to use the Alert context
export const useAlert = () => useContext(AlertContext);

// Create the Alert provider
export const AlertProvider = ({ children }) => {
  const [alert, setAlert] = useState(null);

  const showAlert = (message, type = 'info') => {
    setAlert({ message, type });
  };

  const hideAlert = () => {
    setAlert(null);
  };

  return (
    <AlertContext.Provider value={{ showAlert }}>
      {alert && <Alert message={alert.message} type={alert.type} onClose={hideAlert} />}
      {children}
    </AlertContext.Provider>
  );
};
