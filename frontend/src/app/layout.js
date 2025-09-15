import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Navbar from "./components/Navbar";
import {
  ClerkProvider,
  SignedIn,
  SignedOut,
  SignIn,
} from '@clerk/nextjs'

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata = {
  title: "Tariffy",
  description: "One stop solution for your tariff tracking",
  icons: {
    icon: "images/favicon.ico",
  }
};

export default function RootLayout({ children }) {
  return (
    <ClerkProvider>
      <html lang="en">
        <body
          className={`${geistSans.variable} ${geistMono.variable} antialiased`}
        >
          <SignedOut>
            <div className="flex items-center justify-center min-h-screen">
                <div className="w-full max-w-md">
                  <div className="text-center mb-8">
                    <h1 className="text-2xl font-bold text-gray-900">Your Favourite Tariff Platform</h1>
                    <p className="text-gray-600 mt-2">Sign in to have a time of your life</p>
                  </div>
                  <SignIn
                    routing="hash"
                    appearance={{
                      elements: {
                        rootBox: "mx-auto",
                        card: "shadow-lg",
                      },
                    }}
                  />
                </div>
              </div>
          </SignedOut>
          <SignedIn>
            <Navbar />
            {children}
          </SignedIn>
            
        </body>
      </html>
    </ClerkProvider>
  );
}
