export default function PopUpWrapper({ 
    children,
    OnClick = () => {},
 }) {
    return (
        <div className="fixed inset-0 flex items-center justify-center z-50" >
            <div className="fixed inset-0 bg-white/50 backdrop-blur-sm" onClick={OnClick}></div>
            <div className="bg-white rounded-lg p-6 shadow-xl border border-gray-200 max-w-2xl w-full mx-4 animate-fade-in relative">
                {children}
            </div>
        </div>
    )
}