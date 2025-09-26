"use client";
import Link from "next/link";
import React, { useState, useRef } from "react";
import { UserButton } from "@clerk/nextjs";
import { useUser } from "@clerk/nextjs";

export default function Navbar() {
  const [userDropDown, setUserDropDown] = useState(false);
  const timeoutRef = useRef();
  const { user } = useUser();
  const role = user?.publicMetadata?.role;

  const handleUserMouseEnter = () => {
    clearTimeout(timeoutRef.current);
    setUserDropDown(true);
  };
  const handleUserMouseLeave = () => {
    timeoutRef.current = setTimeout(() => setUserDropDown(false), 200);
  };

  return (
    <nav className="w-full bg-white shadow-md border-b border-gray-200 p-6 flex items-center justify-between">
      <Link href="/" className="text-3xl font-bold text-black">TARRIFY</Link>
      <ul className="flex space-x-6 text-sm font-medium">
        <li
          className="relative group"
          onMouseEnter={handleUserMouseEnter}
          onMouseLeave={handleUserMouseLeave}
        >

          <button className="text-black text-lg hover:text-blue-600 focus:outline-none">Menu</button>
          <div
            className={`absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded shadow-lg z-50 transition-opacity duration-200 ${userDropDown ? "opacity-100 pointer-events-auto" : "opacity-0 pointer-events-none"}`}
          >
            <Link href="/" className="block px-4 py-2 text-black hover:bg-blue-100">Home</Link>
            <Link href="/calculator" className="block px-4 py-2 text-black hover:bg-blue-100">Tariff Calculator</Link>
            <Link href="/view-tariffs" className="block px-4 py-2 text-black hover:bg-blue-100">View All Tariffs</Link>
            {role === "admin" ? (
              <Link href="/admin" className="block px-4 py-2 text-black hover:bg-blue-100">Add Tariff</Link>
            ) : <div></div>}
            {role === "admin" ? (
              <Link href="/admin/product" className="block px-4 py-2 text-black hover:bg-blue-100">Add Product</Link>
            ) : <div></div>}
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
