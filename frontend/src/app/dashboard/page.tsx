"use client";

import { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";

ChartJS.register(ArcElement, Tooltip, Legend);

const API_URL = "http://127.0.0.1:5000/api";

type Stats = {
  total_alerts: number;
  high_alerts: number;
  medium_alerts: number;
  low_alerts: number;
};

type Alert = {
  id: number;
  timestamp: string;
  src_ip: string;
  dst_ip: string;
  attack_type: string;
  severity: string;
};

const ATTACKS = [
  { id: 1, label: "Botnet C2",    status: "Ready" },
  { id: 2, label: "DDoS Flood",   status: "Ready" },
  { id: 3, label: "SYN DoS",      status: "Ready" },
  { id: 4, label: "PortScan",     status: "Ready" },
  { id: 5, label: "BruteForce",   status: "Ready" },
  { id: 6, label: "WebAttack",    status: "Ready" },
];

import dynamic from "next/dynamic";
import softwareDevAnim from "../../../public/Software development Scene.json";
import dataProtectionAnim from "../../../public/Data protection isometric.json";

// Dynamically import local wrapper to avoid third-party SSR issues
const Lottie = dynamic(() => import("../../components/LottieWrapper"), { ssr: false });

export default function Dashboard() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [recentAlerts, setRecentAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);
  const [firing, setFiring] = useState<number | null>(null);
  const [toast, setToast] = useState<{ msg: string; type: string } | null>(null);

  const fetchDashboardData = useCallback(async () => {
    try {
      const [statsRes, alertsRes] = await Promise.all([
        fetch(`${API_URL}/stats`),
        fetch(`${API_URL}/alerts`),
      ]);
      const statsData = await statsRes.json();
      const alertsData = await alertsRes.json();
      setStats(statsData);
      setRecentAlerts(alertsData.slice(0, 6));
    } catch (err) {
      console.error("Fetch error:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboardData();
    const interval = setInterval(fetchDashboardData, 3000);
    return () => clearInterval(interval);
  }, [fetchDashboardData]);

  const simulateAttack = async (typeId: number, label: string) => {
    setFiring(typeId);
    try {
      const res = await fetch(`${API_URL}/simulate/${typeId}`);
      if (res.ok) {
        const data = await res.json();
        setToast({ msg: `${label} detected — ${data.severity} severity`, type: data.severity });
        await fetchDashboardData();
      }
    } catch {
      setToast({ msg: "Simulation failed — backend unreachable", type: "error" });
    } finally {
      setFiring(null);
      setTimeout(() => setToast(null), 4000);
    }
  };

  const chartData = {
    labels: ["High / Critical", "Medium", "Low / Nominal"],
    datasets: [{
      data: stats ? [stats.high_alerts, stats.medium_alerts, stats.low_alerts] : [1, 1, 1],
      backgroundColor: ["#ef4444", "#f59e0b", "#10b981"],
      borderColor: ["transparent", "transparent", "transparent"],
      borderWidth: 2,
    }],
  };

  const chartOptions = {
    cutout: "75%",
    plugins: {
      legend: {
        position: "right" as const,
        labels: { color: "#475569", font: { family: "Inter", size: 12 }, padding: 16, usePointStyle: true, boxWidth: 8 },
      },
    },
  };

  return (
    <div className="page-wrapper">
      {/* Toast */}
      {toast && (
        <div style={{
          position: "fixed", bottom: 24, right: 24, zIndex: 9999,
          padding: "16px 20px", borderRadius: 10,
          background: "#ffffff",
          border: "1px solid var(--border)",
          boxShadow: "var(--shadow-md)",
          fontFamily: "var(--font-ui)",
          fontSize: 13,
          color: "var(--text-primary)",
          animation: "fade-up 0.3s ease",
          maxWidth: 360,
          display: "flex",
          gap: 12,
          alignItems: "center"
        }}>
          <div style={{
            width: 8, height: 8, borderRadius: "50%",
            background: toast.type === "High" ? "var(--red)" : toast.type === "Medium" ? "var(--amber)" : "var(--green)"
          }} />
          <div>
            <div style={{ fontWeight: 600, marginBottom: 2 }}>Threat Detected</div>
            <div style={{ color: "var(--text-secondary)", fontSize: 12 }}>{toast.msg}</div>
          </div>
        </div>
      )}

      {/* Header */}
      <div className="page-header flex items-center justify-between" style={{ background: "var(--bg-surface)", padding: "24px", borderRadius: "var(--radius-md)", border: "1px solid var(--border)", boxShadow: "var(--shadow-sm)" }}>
        <div>
          <h1 style={{ fontSize: "28px" }}>Network Operations Center</h1>
          <p style={{ fontSize: "14px", marginTop: "8px" }}>Real-time network traffic and threat telemetry powered by intelligent scanning.</p>
        </div>
        <div style={{ width: "200px", height: "120px", display: "flex", alignItems: "center", justifyContent: "center" }}>
          <Lottie animationData={softwareDevAnim} loop={true} />
        </div>
      </div>

      {/* Stat Cards */}
      <div className="stat-grid animate-stagger">
        {/* Total */}
        <div className="card">
          <div className="stat-card-label">
            <span>Total Alerts</span>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 36, width: 80, borderRadius: 4 }} />
            : <div className="stat-card-value">{stats?.total_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">
            <span>All time</span>
          </div>
        </div>

        {/* High */}
        <div className="card card-critical">
          <div className="stat-card-label">
            <span>High Severity</span>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 36, width: 80, borderRadius: 4 }} />
            : <div className="stat-card-value">{stats?.high_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">
            <span>Critical threats</span>
          </div>
        </div>

        {/* Medium */}
        <div className="card card-warning">
          <div className="stat-card-label">
            <span>Medium Severity</span>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 36, width: 80, borderRadius: 4 }} />
            : <div className="stat-card-value">{stats?.medium_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">
            <span>Elevated risk</span>
          </div>
        </div>

        {/* Low */}
        <div className="card card-nominal">
          <div className="stat-card-label">
            <span>Low Severity</span>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 36, width: 80, borderRadius: 4 }} />
            : <div className="stat-card-value">{stats?.low_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">
            <span>Informational</span>
          </div>
        </div>
      </div>

      {/* Mid section */}
      <div className="two-col mb-6 animate-stagger">
        {/* Chart & Animation */}
        <div className="card" style={{ display: "flex", flexDirection: "column" }}>
          <div className="section-title">Threat Distribution & Security Posture</div>
          <div style={{ display: "flex", flex: 1, alignItems: "center", justifyContent: "space-between", padding: "0 20px" }}>
            <div className="chart-container" style={{ width: "220px", height: "220px", flexShrink: 0 }}>
              <Doughnut data={chartData} options={chartOptions} />
            </div>
            <div style={{ width: "240px", height: "240px", flexShrink: 0, opacity: 0.9, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Lottie animationData={dataProtectionAnim} loop={true} />
            </div>
          </div>
        </div>

        {/* Attack Simulation */}
        <div className="card">
          <div className="section-title">Attack Simulation</div>
          <p style={{ fontSize: 13, color: "var(--text-secondary)", marginBottom: 14 }}>
            Trigger synthetic payloads to verify sensor response.
          </p>
          <div className="attack-grid">
            {ATTACKS.map((a) => (
              <button
                key={a.id}
                className="attack-btn"
                onClick={() => simulateAttack(a.id, a.label)}
                disabled={firing === a.id}
              >
                <span>{a.label}</span>
                <span className="attack-btn-status" style={{ background: firing === a.id ? "var(--amber-light)" : "", color: firing === a.id ? "var(--amber)" : "", borderColor: firing === a.id ? "var(--amber-mid)" : "" }}>
                  {firing === a.id ? "Firing..." : a.status}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Recent Alerts Table */}
      <div className="card animate-stagger">
        <div className="flex items-center justify-between mb-4">
          <div className="section-title" style={{ margin: 0, border: "none" }}>
            Recent Alerts
          </div>
          <Link href="/alerts" className="btn btn-ghost">
            View All
          </Link>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Source IP</th>
                <th>Target IP</th>
                <th>Classification</th>
                <th>Severity</th>
              </tr>
            </thead>
            <tbody>
              {recentAlerts.map((alert) => (
                <tr key={alert.id}>
                  <td className="mono" style={{ color: "var(--text-muted)" }}>{alert.timestamp}</td>
                  <td className="mono">{alert.src_ip}</td>
                  <td className="mono">{alert.dst_ip}</td>
                  <td style={{ fontWeight: 500 }}>{alert.attack_type}</td>
                  <td>
                    <span className={`badge badge-${alert.severity.toLowerCase()}`}>
                      {alert.severity}
                    </span>
                  </td>
                </tr>
              ))}
              {recentAlerts.length === 0 && !loading && (
                <tr>
                  <td colSpan={5} style={{ textAlign: "center", padding: "40px", color: "var(--text-muted)", fontSize: 13 }}>
                    No recent alerts found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
