import { clerkMiddleware, createClerkClient, createRouteMatcher } from '@clerk/nextjs/server';
import { NextResponse } from "next/server";

const isAdminRoute = createRouteMatcher('/admin(.*)');
const isSignInRoute = createRouteMatcher('/sign-in(.*)');

export default clerkMiddleware(async (auth, req) => {
  const { userId, sessionId } = await auth();

  // console.log(`Middleware: userId=${userId}, sessionId=${sessionId}, url=${req.url}`);

  if (!userId || !sessionId) {
    if (isSignInRoute(req)) { // prevent redirect loop
      return NextResponse.next();
    }
    return NextResponse.redirect(new URL('/sign-in', req.url));

  }

  // fetch user role from backend and update Clerk public metadata
  const clerkClient = createClerkClient({ secretKey: process.env.CLERK_SECRET_KEY });
  const user = await clerkClient.users.getUser(userId);
  if (user.publicMetadata.role === undefined) { // use this method in the event that webhook doesn't work
    try {
      const base_url = process.env.BASE_URL
      const response = await fetch(`${base_url}/user/${userId}`, {
        method: "GET",
      });
      if (response.ok) {
        const result = await response.json();
        // console.log("result", result);
        await clerkClient.users.updateUserMetadata(userId, {
          publicMetadata: { role: result.admin ? "admin" : "user" },
        });
      }
    } catch (error) {
      console.error("Error: " + error.message);
    }
  }

  if (isSignInRoute(req)) { // cannot go to sign in page if already signed in
    return NextResponse.redirect(new URL('/', req.url));
  }

  // const user = await clerkClient.users.getUser(userId);
  // console.log("meta", user.publicMetadata.role);

  // restrict /admin routes to admin users only
  if (isAdminRoute(req)) {
    if (user.publicMetadata.role !== "admin") {
      // console.log(user.publicMetadata.role)
      return NextResponse.redirect(new URL("/", req.url)); // change to unauthorised page if needed
    }
  }

  return NextResponse.next();
});

export const config = {
  matcher: [
    // Skip Next.js internals and all static files, unless found in search params
    '/((?!_next|[^?]*\\.(?:html?|css|js(?!on)|jpe?g|webp|png|gif|svg|ttf|woff2?|ico|csv|docx?|xlsx?|zip|webmanifest)).*)',
    // Always run for API routes
    '/(api|trpc)(.*)',
  ],
};