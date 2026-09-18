"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

export default function Navbar() {
  const pathname = usePathname();
  if (pathname === "/") return null;

  return (
    <header className="nav">
      {/* Brand */}
      <Link href="/dashboard" className="nav-brand">
        <div className="nav-brand-icon">🛡️</div>
        <div>
          <div className="nav-brand-text">NIDS</div>
          <div className="nav-brand-tag">Network Intrusion Detection</div>
        </div>
      </Link>

      {/* Nav Links */}
      <Link href="/dashboard" className={pathname === "/dashboard" ? "active" : ""}>
        Dashboard
      </Link>
      <Link
        href="/alerts"
        className={pathname.startsWith("/alerts") || pathname.startsWith("/details") ? "active" : ""}
      >
        Alert Log
      </Link>
      <Link href="/health" className={pathname === "/health" ? "active" : ""}>
        System Health
      </Link>

      {/* Status */}
      <div className="nav-status" style={{ marginLeft: "auto" }}>
        <div className="nav-status-dot" />
        System Active
      </div>
    </header>
  );
}
