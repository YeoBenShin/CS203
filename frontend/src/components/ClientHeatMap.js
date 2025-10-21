"use client";

import dynamic from "next/dynamic";

const HeatMap = dynamic(() => import("@/components/HeatMap"), {
  ssr: false,
  loading: () => <p>Loading map...</p>,
});

export default function ClientHeatMap() {
  return <HeatMap />;
}