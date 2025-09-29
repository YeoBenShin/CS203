import { LoadingSpinner } from "./MessageComponents";
export default function Button({
    onClick = () => {},
    needSpinner = true,
    isLoading = false,
    isLoadingText = "Loading...",
    buttonText = "Submit",
    type = "button",
    textConfig = "text-sm text-white font-medium",
    colorBg = "bg-blue-600 hover:bg-blue-700 focus:ring-blue-500",
    width = "w-full",
    className = "" }) {
    return (
        <button
            type={type}
            onClick={onClick}
            disabled={isLoading}
            className={`group relative flex justify-center items-center py-2 px-4 border border-black rounded-md
        focus:outline-none focus:ring-2 focus:ring-offset-2 cursor-pointer
        disabled:opacity-50 disabled:cursor-not-allowed ${textConfig} ${colorBg} ${className} ${width}`}
        >
            {isLoading && needSpinner && <LoadingSpinner />}
            {isLoading ? isLoadingText : buttonText}
        </button>
    );
}