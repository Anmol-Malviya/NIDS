"use client";

import { useEffect, useState, useCallback } from "react";
import Link from "next/link";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "react-chartjs-2";
import {
  Bell,
  ShieldAlert,
  AlertTriangle,
  Info,
  Server,
  Cloud,
  Network,
  ScanSearch,
  Lock,
  Globe,
  Shield,
  Swords,
  Monitor,
  Radar,
  CheckCircle,
  Activity,
} from "lucide-react";
import dynamic from "next/dynamic";
import softwareDevAnim from "../../../public/Software development Scene.json";

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
  { id: 1, label: "Botnet C2",   status: "Ready", icon: Server },
  { id: 2, label: "DDoS Flood",  status: "Ready", icon: Cloud },
  { id: 3, label: "SYN DoS",     status: "Ready", icon: Network },
  { id: 4, label: "PortScan",    status: "Ready", icon: ScanSearch },
  { id: 5, label: "BruteForce",  status: "Ready", icon: Lock },
  { id: 6, label: "WebAttack",   status: "Ready", icon: Globe },
];

// Dynamically import local wrapper to avoid third-party SSR issues
const Lottie = dynamic(() => import("../../components/LottieWrapper"), { ssr: false });

// Decorative mini bar chart SVG
function MiniBarChart({ color }: { color: string }) {
  const bars = [40, 65, 45, 80, 55, 90, 70];
  return (
    <svg
      width="110"
      height="60"
      viewBox="0 0 110 60"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className="stat-bg-chart"
    >
      {bars.map((h, i) => (
        <rect key={i} x={i * 16} y={60 - h} width="10" height={h} rx="3" fill={color} />
      ))}
    </svg>
  );
}

export default function Dashboard() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [recentAlerts, setRecentAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);
  const [firing, setFiring] = useState<number | null>(null);
  const [toast, setToast] = useState<{ msg: string; type: string } | null>(null);
  const [backendOffline, setBackendOffline] = useState(false);

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
      setBackendOffline(false);
    } catch {
      setBackendOffline(true);
      // Fallback to mock data when offline to keep UI beautiful
      setStats({ total_alerts: 111, high_alerts: 67, medium_alerts: 30, low_alerts: 14 });
      setRecentAlerts([
        { id: 101, timestamp: new Date().toISOString().replace("T", " ").substring(0, 19), src_ip: "192.168.1.104", dst_ip: "10.0.0.5", attack_type: "PortScan", severity: "Low" },
        { id: 102, timestamp: new Date(Date.now() - 60000).toISOString().replace("T", " ").substring(0, 19), src_ip: "45.22.19.8", dst_ip: "10.0.0.2", attack_type: "Botnet C2", severity: "High" },
        { id: 103, timestamp: new Date(Date.now() - 120000).toISOString().replace("T", " ").substring(0, 19), src_ip: "192.168.1.105", dst_ip: "10.0.0.5", attack_type: "WebAttack", severity: "Medium" },
      ]);
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
      borderWidth: 0,
      hoverOffset: 6,
    }],
  };

  const chartOptions = {
    cutout: "72%",
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          label: (ctx: any) => ` ${ctx.label}: ${ctx.raw}`,
        },
      },
    },
  };

  const total = stats?.total_alerts ?? 0;

  return (
    <div className="page-wrapper">
      {/* ─── Toast ─────────────────────────────────── */}
      {toast && (
        <div style={{
          position: "fixed", bottom: 24, right: 24, zIndex: 9999,
          padding: "18px 24px", borderRadius: 16,
          background: "#ffffff",
          border: "1px solid var(--border)",
          boxShadow: "0 20px 60px rgba(15,23,42,0.12)",
          fontFamily: "var(--font-ui)",
          fontSize: 14,
          color: "var(--text-primary)",
          animation: "fade-up 0.3s ease",
          maxWidth: 360,
          display: "flex",
          gap: 14,
          alignItems: "center",
        }}>
          <div style={{
            width: 40, height: 40, borderRadius: 10, flexShrink: 0,
            background: toast.type === "High" ? "var(--red-light)" : toast.type === "Medium" ? "var(--amber-light)" : "var(--green-light)",
            display: "flex", alignItems: "center", justifyContent: "center",
          }}>
            <CheckCircle size={20} color={toast.type === "High" ? "var(--red)" : toast.type === "Medium" ? "var(--amber)" : "var(--green)"} />
          </div>
          <div>
            <div style={{ fontWeight: 700, marginBottom: 2 }}>Threat Detected</div>
            <div style={{ color: "var(--text-secondary)", fontSize: 13 }}>{toast.msg}</div>
          </div>
        </div>
      )}

      {/* ─── Offline Banner ─────────────────────────── */}
      {backendOffline && (
        <div className="banner-offline animate-stagger">
          <div className="flex items-center gap-2">
            <AlertTriangle size={18} />
            <strong>Backend Disconnected</strong>
          </div>
          <span>Showing mock data. Please ensure the Python API server is running on {API_URL}.</span>
        </div>
      )}

      {/* ─── Hero Header ─────────────────────────────── */}
      <div
        className="page-header"
        style={{
          background: "linear-gradient(135deg, #f0f7ff 0%, #e8f0fe 50%, #f0f4ff 100%)",
        }}
      >
        {/* Dot-grid pattern overlay */}
        <div style={{
          position: "absolute", inset: 0, zIndex: 1,
          backgroundImage: "radial-gradient(circle, rgba(37,99,235,0.08) 1px, transparent 1px)",
          backgroundSize: "28px 28px",
          borderRadius: "inherit",
        }} />
        <div className="page-header-content" style={{ position: "relative", zIndex: 2 }}>
          <div className="page-header-title">Real-Time Protection</div>
          <h1>
            <span style={{ color: "var(--text-primary)" }}>Network </span>
            <span style={{ color: "var(--blue)" }}>Operations Center</span>
          </h1>
          <p>Real-time network traffic and threat telemetry powered by intelligent scanning.</p>
          <div className="banner-features">
            <div className="banner-feature">
              <div className="banner-feature-icon"><Monitor size={22} /></div>
              <div className="banner-feature-text">
                <strong>Monitor</strong>
                <span>Network Traffic</span>
              </div>
            </div>
            <div className="banner-feature">
              <div className="banner-feature-icon"><Shield size={22} /></div>
              <div className="banner-feature-text">
                <strong>Detect</strong>
                <span>Threats in Real-Time</span>
              </div>
            </div>
            <div className="banner-feature">
              <div className="banner-feature-icon"><Radar size={22} /></div>
              <div className="banner-feature-text">
                <strong>Stay Secure</strong>
                <span>With Intelligent Scanning</span>
              </div>
            </div>
          </div>
        </div>
        <div style={{ width: 260, height: 200, display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, zIndex: 2, padding: "0 32px" }}>
          <Lottie animationData={softwareDevAnim} loop={true} />
        </div>
      </div>

      {/* ─── Stat Cards ──────────────────────────────── */}
      <div className="stat-grid animate-stagger">
        {/* Total Alerts */}
        <div className="card stat-card card-total">
          <div className="stat-card-header">
            <div className="stat-icon"><Bell size={22} /></div>
            <div>
              <div className="stat-card-label">Total Alerts</div>
              <div className="stat-card-sub-label">All time</div>
            </div>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 44, width: 90, borderRadius: 6 }} />
            : <div className="stat-card-value">{stats?.total_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">Cumulative detections</div>
          <MiniBarChart color="#2563eb" />
        </div>

        {/* High Severity */}
        <div className="card stat-card card-critical">
          <div className="stat-card-header">
            <div className="stat-icon"><ShieldAlert size={22} /></div>
            <div>
              <div className="stat-card-label">High Severity</div>
              <div className="stat-card-sub-label">Critical threats</div>
            </div>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 44, width: 90, borderRadius: 6 }} />
            : <div className="stat-card-value">{stats?.high_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">Requires immediate action</div>
          <MiniBarChart color="#ef4444" />
        </div>

        {/* Medium Severity */}
        <div className="card stat-card card-warning">
          <div className="stat-card-header">
            <div className="stat-icon"><AlertTriangle size={22} /></div>
            <div>
              <div className="stat-card-label">Medium Severity</div>
              <div className="stat-card-sub-label">Elevated risk</div>
            </div>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 44, width: 90, borderRadius: 6 }} />
            : <div className="stat-card-value">{stats?.medium_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">Investigate promptly</div>
          <MiniBarChart color="#f59e0b" />
        </div>

        {/* Low Severity */}
        <div className="card stat-card card-nominal">
          <div className="stat-card-header">
            <div className="stat-icon"><Info size={22} /></div>
            <div>
              <div className="stat-card-label">Low Severity</div>
              <div className="stat-card-sub-label">Informational</div>
            </div>
          </div>
          {loading
            ? <div className="skeleton" style={{ height: 44, width: 90, borderRadius: 6 }} />
            : <div className="stat-card-value">{stats?.low_alerts ?? 0}</div>
          }
          <div className="stat-card-sub">Low-priority anomalies</div>
          <MiniBarChart color="#10b981" />
        </div>
      </div>

      {/* ─── Mid Section ─────────────────────────────── */}
      <div className="two-col mb-8 animate-stagger">
        {/* Threat Distribution */}
        <div className="card" style={{ display: "flex", flexDirection: "column" }}>
          <div className="section-title">
            <Radar size={22} color="var(--blue)" />
            Threat Distribution &amp; Security Posture
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: 32 }}>
            {/* Donut chart with center text */}
            <div style={{ position: "relative", width: 200, height: 200, flexShrink: 0 }}>
              <Doughnut data={chartData} options={chartOptions} />
              <div className="donut-center-text">
                <span className="donut-center-val">{total}</span>
                <span className="donut-center-lbl">Total Alerts</span>
              </div>
            </div>

            {/* Legend + quote */}
            <div style={{ flex: 1 }}>
              <div style={{ display: "flex", flexDirection: "column", gap: 16, marginBottom: 24 }}>
                {[
                  { label: "High / Critical", color: "#ef4444", val: stats?.high_alerts },
                  { label: "Medium", color: "#f59e0b", val: stats?.medium_alerts },
                  { label: "Low / Nominal", color: "#10b981", val: stats?.low_alerts },
                ].map((item) => (
                  <div key={item.label} style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <div style={{ width: 12, height: 12, borderRadius: 3, background: item.color, flexShrink: 0 }} />
                    <span style={{ fontSize: 14, color: "var(--text-primary)", fontWeight: 500, flex: 1 }}>{item.label}</span>
                    <span style={{ fontSize: 14, fontWeight: 700, color: "var(--text-primary)" }}>{item.val ?? 0}</span>
                  </div>
                ))}
              </div>

              {/* Quote */}
              <div style={{
                background: "var(--blue-light)",
                borderRadius: 12,
                padding: 16,
                display: "flex",
                alignItems: "center",
                gap: 12,
              }}>
                <Shield size={28} color="var(--blue)" style={{ flexShrink: 0 }} />
                <p style={{ fontStyle: "italic", color: "var(--blue)", fontWeight: 600, fontSize: 14, lineHeight: 1.4 }}>
                  &ldquo;Monitor Today<br />Prevent Tomorrow&rdquo;
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Attack Simulation */}
        <div className="card">
          <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", marginBottom: 8 }}>
            <div className="section-title" style={{ marginBottom: 0 }}>
              <Swords size={22} color="var(--blue)" />
              Attack Simulation
            </div>
            <div style={{
              display: "flex", gap: 10, fontSize: 11, fontWeight: 700,
              color: "var(--text-muted)", textTransform: "uppercase",
              letterSpacing: "0.06em", paddingTop: 4,
            }}>
              <span style={{ color: "var(--blue)" }}>TEST</span>
              <span>•</span>
              <span style={{ color: "var(--blue)" }}>ANALYZE</span>
              <span>•</span>
              <span style={{ color: "var(--blue)" }}>STAY PREPARED</span>
            </div>
          </div>
          <p style={{ fontSize: 14, color: "var(--text-secondary)", marginBottom: 20 }}>
            Trigger synthetic payloads to verify sensor response.
          </p>
          <div className="attack-grid">
            {ATTACKS.map((a) => {
              const Icon = a.icon;
              const isFiring = firing === a.id;
              return (
                <button
                  key={a.id}
                  className="attack-btn"
                  onClick={() => simulateAttack(a.id, a.label)}
                  disabled={isFiring || backendOffline}
                  style={{ opacity: backendOffline ? 0.6 : 1 }}
                >
                  <div className="attack-btn-icon">
                    <Icon size={22} />
                  </div>
                  <div className="attack-btn-content">
                    <span style={{ fontSize: 13, fontWeight: 600 }}>{a.label}</span>
                    <span
                      className="attack-btn-status"
                      style={{
                        background: isFiring ? "var(--amber-light)" : backendOffline ? "var(--bg-muted)" : "var(--blue-light)",
                        color: isFiring ? "var(--amber)" : backendOffline ? "var(--text-muted)" : "var(--blue)",
                      }}
                    >
                      {backendOffline ? "Offline" : isFiring ? "Firing..." : a.status}
                    </span>
                  </div>
                </button>
              );
            })}
          </div>
          <div style={{ marginTop: 20, textAlign: "right", fontSize: 11, fontWeight: 700, color: "var(--text-muted)", textTransform: "uppercase", letterSpacing: "0.08em" }}>
            Stronger Defenses · Safer Tomorrows
          </div>
        </div>
      </div>

      {/* ─── Recent Alerts Table ─────────────────────── */}
      <div className="card animate-stagger">
        <div className="flex items-center justify-between mb-6">
          <div className="section-title" style={{ margin: 0 }}>
            <Activity size={20} color="var(--blue)" />
            Recent Alerts
          </div>
          <Link href="/alerts" className="btn btn-ghost">
            View All →
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
                  <td style={{ fontWeight: 600 }}>{alert.attack_type}</td>
                  <td>
                    <span className={`badge badge-${alert.severity.toLowerCase()}`}>
                      {alert.severity}
                    </span>
                  </td>
                </tr>
              ))}
              {recentAlerts.length === 0 && !loading && (
                <tr>
                  <td colSpan={5} style={{ textAlign: "center", padding: "48px", color: "var(--text-muted)", fontSize: 14 }}>
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
