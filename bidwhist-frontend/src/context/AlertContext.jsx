// src/context/AlertContext.jsx

import React, { createContext, useState, useContext } from 'react';
import Alert from '../components/Alert';

const AlertContext = createContext();

/**
 * Custom hook to access the Alert context.
 *
 * @returns {{ showAlert: (message: string, type?: string) => void }}
 * @throws Will throw if used outside an AlertProvider
 */
export const useAlert = () => useContext(AlertContext);

/**
 * Provides a shared alert system with `showAlert()` and automatic rendering of the <Alert /> component.
 *
 * @param {React.ReactNode} children - Components that should have access to alert logic
 * @returns {JSX.Element} Provider with alert capabilities
 */
export const AlertProvider = ({ children }) => {
  const [alert, setAlert] = useState(null);

  /**
   * Triggers an alert with a message and optional type (e.g., 'info', 'error', 'success').
   *
   * @param {string} message - The alert message
   * @param {string} [type='info'] - The alert type
   */
  const showAlert = (message, type = 'info') => {
    setAlert({ message, type });
  };

  /**
   * Hides the currently visible alert.
   */
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

