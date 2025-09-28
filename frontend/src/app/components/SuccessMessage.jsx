// setShowSuccessPopup is a function to control the visibility of the success popup
const SuccessMessage = ({ successMessage, setShowSuccessPopup }) => {
    return (
        <div className="fixed top-4 right-4 z-50">
            <div className="bg-white rounded-lg p-4 shadow-lg border border-gray-200 max-w-sm animate-fade-in">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <svg className="h-5 w-5 text-green-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7" />
                  </svg>
                </div>
                <div className="ml-3">
                  <p className="text-sm font-medium text-gray-900">{successMessage}</p>
                </div>
                <div className="ml-auto pl-3">
                  <button
                    onClick={() => setShowSuccessPopup(false)}
                    className="inline-flex text-gray-400 hover:text-gray-500"
                  >
                    <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
    )
}

const showSuccessPopupMessage = (setSuccessMessage, setShowSuccessPopup, message) => {
    setSuccessMessage(message);
    setShowSuccessPopup(true);
    setTimeout(() => {
      setShowSuccessPopup(false);
      setSuccessMessage("");
    }, 5000);
  };

export { SuccessMessage, showSuccessPopupMessage };