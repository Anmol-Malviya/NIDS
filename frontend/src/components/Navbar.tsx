"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Shield, LayoutDashboard, Activity, HeartPulse } from "lucide-react";

export default function Navbar() {
  const pathname = usePathname();
  if (pathname === "/") return null;

  return (
    <header className="nav">
      {/* Brand */}
      <Link href="/dashboard" className="nav-brand">
        <div className="nav-brand-icon">
          <Shield size={24} />
        </div>
        <div>
          <div className="nav-brand-text">NIDS</div>
          <div className="nav-brand-tag">Network Intrusion Detection</div>
        </div>
      </Link>

      {/* Nav Links */}
      <div className="nav-links">
        <Link href="/dashboard" className={`nav-link ${pathname === "/dashboard" ? "active" : ""}`}>
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </Link>
        <Link
          href="/alerts"
          className={`nav-link ${pathname.startsWith("/alerts") || pathname.startsWith("/details") ? "active" : ""}`}
        >
          <Activity size={18} />
          <span>Alert Log</span>
        </Link>
        <Link href="/health" className={`nav-link ${pathname === "/health" ? "active" : ""}`}>
          <HeartPulse size={18} />
          <span>System Health</span>
        </Link>
      </div>

      {/* Status */}
      <div className="nav-status">
        <div className="nav-status-dot" />
        <span>System Active</span>
        <span className="nav-status-sub">All systems operational</span>
      </div>
    </header>
  );
}
