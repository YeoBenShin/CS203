"use client";
import Link from "next/link";
import React, { useState, useRef, useEffect } from "react";
import { UserButton, useUser } from "@clerk/nextjs";

export default function Navbar() {
  const [userDropDown, setUserDropDown] = useState(false);
  const timeoutRef = useRef();
  const { user, isSignedIn } = useUser();
  const role = user?.publicMetadata?.role;

  useEffect(() => {
    if (!isSignedIn && !user) {
      // User has signed out, clear localStorage
      if (typeof window !== 'undefined') {
        localStorage.removeItem('recentCalculations');
        // Clear any other localStorage items you need
        // localStorage.clear(); // Or clear everything
      }
    }
  }, [isSignedIn, user]);

  const handleUserMouseEnter = () => {
    clearTimeout(timeoutRef.current);
    setUserDropDown(true);
  };
  const handleUserMouseLeave = () => {
    timeoutRef.current = setTimeout(() => setUserDropDown(false), 200);
  };

  return (
    <nav className="w-full bg-white shadow-md border-b border-gray-200 p-6 flex items-center justify-between">
      <Link href="/" className="text-3xl font-bold text-black hover:text-blue-600 transition">TARRIFY</Link>
      <ul className="flex items-center space-x-6 text-sm font-medium">
        <li
          className="relative group"
          onMouseEnter={handleUserMouseEnter}
          onMouseLeave={handleUserMouseLeave}
        >
          <button className="text-black text-lg hover:text-blue-600 focus:outline-none font-medium transition">Menu</button>
          <div
            className={`absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-xl z-50 transition-all duration-200 ${userDropDown ? "opacity-100 pointer-events-auto translate-y-0" : "opacity-0 pointer-events-none -translate-y-2"}`}
          >
            <Link href="/" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition rounded-t-lg">Home</Link>
            <Link href="/calculator" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition">Tariff Calculator</Link>
            <Link href="/heatmap" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition">Tariff Heatmap</Link>
            <Link href="/view-tariffs" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition">View All Tariffs</Link>
            {role === "admin" && <Link href="/admin" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition">Add Tariff</Link>}
            {role === "admin" && <Link href="/admin/product" className="block px-4 py-3 text-black hover:bg-blue-50 hover:text-blue-600 transition rounded-b-lg">Add Product</Link>}
          </div>
        </li>
        <li>
          <UserButton showName
            appearance={{
              elements: {
                userButtonAvatarBox: "w-12 h-12", // bigger avatar
                userButtonOuterIdentifier: "text-lg font-semibold", // bigger name
              },
            }} />
        </li>
      </ul>
    </nav>
  );
}
