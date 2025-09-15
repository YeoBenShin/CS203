import { clerkMiddleware } from '@clerk/nextjs/server';
// import { NextResponse } from "next/server";

// const isAdminRoute = createRouteMatcher('/admin(.*)');

// export default clerkMiddleware(async (req) => {
//   if (isAdminRoute(req)) {
//     const { user } = req.auth;
//   }

//   return NextResponse.next();
// });

export default clerkMiddleware();

export const config = {
  matcher: [
    // Skip Next.js internals and all static files, unless found in search params
    '/((?!_next|[^?]*\\.(?:html?|css|js(?!on)|jpe?g|webp|png|gif|svg|ttf|woff2?|ico|csv|docx?|xlsx?|zip|webmanifest)).*)',
    // Always run for API routes
    '/(api|trpc)(.*)',
  ],
};