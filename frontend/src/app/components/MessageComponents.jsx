import React from 'react';

/**
 * Reusable error display component
 */
const ErrorDisplay = ({ errors, title = "Please fix the following issues:" }) => {
  if (!errors || errors.length === 0) {
    return null;
  }

  return (
    <div className="mt-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
      <div className="font-bold mb-2">{title}</div>
      <ul className="list-disc list-inside space-y-1">
        {errors.map((error, index) => (
          <li key={index} className="text-sm">{error}</li>
        ))}
      </ul>
    </div>
  );
};

/**
 * Reusable success display component
 */
const SuccessDisplay = ({ message }) => {
  if (!message) {
    return null;
  }

  return (
    <div className="mt-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
      {message}
    </div>
  );
};

/**
 * Loading spinner component
 */
const LoadingSpinner = () => (
  <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
  </svg>
);

export { ErrorDisplay, SuccessDisplay, LoadingSpinner };