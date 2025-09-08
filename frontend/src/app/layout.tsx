import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Link from "next/link";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Tariffy",
  description: "One stop solution for your tariff tracking",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <nav className="w-full bg-white shadow-md border-b border-gray-200 p-6 flex items-center justify-between">
          <Link href="/" className="text-2xl font-bold text-black">TARRIFY</Link>

          <ul className="flex space-x-6 text-sm font-medium">
            <li>
              <Link href="/calculator" className="text-black text-base hover:text-blue-600">Calculator</Link>
            </li>
            <li>
              <Link href="#" className="text-black text-base hover:text-blue-600">About Us</Link>
            </li>
          </ul>
        </nav>
        {children}
      </body>
    </html>
  );
}
