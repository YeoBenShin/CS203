"use client";
import Link from "next/link";
import React, { useState, useRef } from "react";
import { UserButton } from "@clerk/nextjs";
import { User } from "@clerk/nextjs/server";

export default function Navbar() {
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const timeoutRef = useRef();

  const handleMouseEnter = () => {
    clearTimeout(timeoutRef.current);
    setDropdownOpen(true);
  };
  const handleMouseLeave = () => {
    timeoutRef.current = setTimeout(() => setDropdownOpen(false), 200);
  };

  return (
    <nav className="w-full bg-white shadow-md border-b border-gray-200 p-6 flex items-center justify-between">
      <Link href="/" className="text-3xl font-bold text-black">TARRIFY</Link>
      <ul className="flex space-x-6 text-sm font-medium">
        <li
          className="relative group"
          onMouseEnter={handleMouseEnter}
          onMouseLeave={handleMouseLeave}
        >
          <button className="text-black text-lg hover:text-blue-600 focus:outline-none">Admin Page</button>
          <div
            className={`absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded shadow-lg z-50 transition-opacity duration-200 ${dropdownOpen ? "opacity-100 pointer-events-auto" : "opacity-0 pointer-events-none"}`}
          >
            <Link href="/admin" className="block px-4 py-2 text-black hover:bg-blue-100">Create Tariff</Link>
            <Link href="/admin/product" className="block px-4 py-2 text-black hover:bg-blue-100">Add Product</Link>
            <Link href="/admin/view-tariffs" className="block px-4 py-2 text-black hover:bg-blue-100">View All Tariffs</Link>
            <Link href="/admin/view-mappings" className="block px-4 py-2 text-black hover:bg-blue-100">View All Mappings</Link>
          </div>
        </li>
        <li>
          {/* <Link href="#" className="text-black text-lg hover:text-blue-600">About Us</Link> */}
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
