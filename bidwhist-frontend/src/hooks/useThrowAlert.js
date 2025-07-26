// src/hooks/useThrowAlert.js
import { useAlert } from '../context/AlertContext';

/**
 * Custom hook to throw an alert from an API response or message string.
 *
 * @returns {(data: { message?: string } | string, type?: string) => void}
 */
export const useThrowAlert = () => {
  const { showAlert } = useAlert();

  return (data, type = 'error') => {
    const msg = typeof data === 'string' ? data : data?.message || 'Something went wrong.';
    showAlert(msg, type);
  };
};
