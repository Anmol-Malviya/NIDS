"use client";

import { useLottie } from "lottie-react";

export default function LottieWrapper({ animationData, loop = true }: { animationData: any, loop?: boolean }) {
  const { View } = useLottie({ animationData, loop });
  return <>{View}</>;
}
