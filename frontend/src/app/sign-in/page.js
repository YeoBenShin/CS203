import { SignIn } from '@clerk/nextjs';

export default function SignInPage() {
    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="w-full max-w-md">
                <div className="text-center mb-8">
                    <h1 className="text-2xl font-bold text-gray-900">Your Favourite Tariff Platform</h1>
                    <p className="text-gray-600 mt-2">Sign in to have a time of your life</p>
                </div>
                <SignIn
                    routing='hash'
                    forceRedirectUrl="/"
                    appearance={{
                        elements: {
                            rootBox: "mx-auto",
                            card: "shadow-lg",
                        },
                    }}
                />
            </div>
        </div>
    );
}