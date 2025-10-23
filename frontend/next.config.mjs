/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    const base = (process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
    return [
      { source: "/backapi/:path*", destination: `${base}/:path*` },
    ];
  },
};

export default nextConfig;