"use client";

import { useEffect, useState } from "react";

const API_URL = "http://127.0.0.1:5000/api";

type HealthMetrics = {
  cpu_percent: number;
  memory_percent: number;
  disk_percent: number;
  pps_in: number;
  pps_out: number;
  mb_recv: number;
  mb_sent: number;
  latency: string;
  errors: number;
  drops: number;
  established: number;
  syn_recv: number;
  time_wait: number;
  syn_sent: number;
  db_status: string;
};

export default function Health() {
  const [metrics, setMetrics] = useState<HealthMetrics | null>(null);

  useEffect(() => {
    const fetchHealth = async () => {
      try {
        const res = await fetch(`${API_URL}/health`);
        const data = await res.json();
        setMetrics(data);
      } catch (err) {
        console.error(err);
      }
    };
    fetchHealth();
    const interval = setInterval(fetchHealth, 2000);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="page-wrapper">
      <div className="page-header flex items-center justify-between">
        <div>
          <h1>System Health</h1>
          <p>Real-time resource utilization and node status.</p>
        </div>
      </div>

      <div className="two-col mb-6">
        {/* Hardware Telemetry */}
        <div className="card">
          <div className="section-title">Host Telemetry</div>
          
          {/* CPU */}
          <div className="metric-row">
            <div className="metric-label">CPU Load</div>
            <div className="metric-bar-bg">
              <div
                className={`metric-bar-fill ${metrics && metrics.cpu_percent > 80 ? "danger" : ""}`}
                style={{ width: `${metrics?.cpu_percent ?? 0}%` }}
              />
            </div>
            <div className="metric-value">{metrics?.cpu_percent ?? 0}%</div>
          </div>

          {/* RAM */}
          <div className="metric-row">
            <div className="metric-label">Memory Usage</div>
            <div className="metric-bar-bg">
              <div
                className={`metric-bar-fill ${metrics && metrics.memory_percent > 85 ? "danger" : ""}`}
                style={{ width: `${metrics?.memory_percent ?? 0}%` }}
              />
            </div>
            <div className="metric-value">{metrics?.memory_percent ?? 0}%</div>
          </div>

          {/* DISK */}
          <div className="metric-row">
            <div className="metric-label">Storage IO</div>
            <div className="metric-bar-bg">
              <div
                className={`metric-bar-fill ${metrics && metrics.disk_percent > 90 ? "danger" : ""}`}
                style={{ width: `${metrics?.disk_percent ?? 0}%` }}
              />
            </div>
            <div className="metric-value">{metrics?.disk_percent ?? 0}%</div>
          </div>

          <div style={{ marginTop: 24, paddingTop: 16, borderTop: "1px solid var(--border)" }}>
            <div className="flex items-center justify-between">
              <span style={{ fontSize: 13, fontWeight: 500, color: "var(--text-secondary)" }}>Backend API Service</span>
              <span className="badge badge-low">Online</span>
            </div>
            <div className="flex items-center justify-between mt-4">
              <span style={{ fontSize: 13, fontWeight: 500, color: "var(--text-secondary)" }}>Database Connection</span>
              <span className={`badge badge-${metrics?.db_status === "connected" ? "low" : "high"}`}>
                {metrics?.db_status === "connected" ? "Connected" : "Error"}
              </span>
            </div>
          </div>
        </div>

        {/* Network Metrics */}
        <div className="card">
          <div className="section-title">Network Interfaces</div>
          
          <div className="detail-grid" style={{ marginTop: 0, gap: 12 }}>
            <div className="detail-item">
              <div className="detail-item-label">Packets IN (PPS)</div>
              <div className="detail-item-value">{metrics?.pps_in ?? 0} pps</div>
            </div>
            <div className="detail-item">
              <div className="detail-item-label">Packets OUT (PPS)</div>
              <div className="detail-item-value">{metrics?.pps_out ?? 0} pps</div>
            </div>
            <div className="detail-item">
              <div className="detail-item-label">Data Received</div>
              <div className="detail-item-value">{metrics?.mb_recv ?? 0} MB</div>
            </div>
            <div className="detail-item">
              <div className="detail-item-label">Data Sent</div>
              <div className="detail-item-value">{metrics?.mb_sent ?? 0} MB</div>
            </div>
            <div className="detail-item">
              <div className="detail-item-label">Loopback Latency</div>
              <div className="detail-item-value">{metrics?.latency ?? "— ms"}</div>
            </div>
            <div className="detail-item">
              <div className="detail-item-label">Packet Errors / Drops</div>
              <div className="detail-item-value" style={{ color: (metrics?.errors || metrics?.drops) ? "var(--red)" : "inherit" }}>
                {metrics?.errors ?? 0} / {metrics?.drops ?? 0}
              </div>
            </div>
          </div>

          <div className="section-title" style={{ marginTop: 24 }}>Socket States (IPv4)</div>
          <div className="flex items-center justify-between" style={{ padding: "0 8px" }}>
            <div className="text-center">
              <div style={{ fontSize: 11, fontWeight: 600, color: "var(--text-muted)", textTransform: "uppercase" }}>Established</div>
              <div className="mono" style={{ fontSize: 16, fontWeight: 700, color: "var(--green)", marginTop: 4 }}>{metrics?.established ?? 0}</div>
            </div>
            <div className="text-center">
              <div style={{ fontSize: 11, fontWeight: 600, color: "var(--text-muted)", textTransform: "uppercase" }}>Time Wait</div>
              <div className="mono" style={{ fontSize: 16, fontWeight: 700, color: "var(--amber)", marginTop: 4 }}>{metrics?.time_wait ?? 0}</div>
            </div>
            <div className="text-center">
              <div style={{ fontSize: 11, fontWeight: 600, color: "var(--text-muted)", textTransform: "uppercase" }}>SYN Sent</div>
              <div className="mono" style={{ fontSize: 16, fontWeight: 700, color: "var(--blue)", marginTop: 4 }}>{metrics?.syn_sent ?? 0}</div>
            </div>
            <div className="text-center">
              <div style={{ fontSize: 11, fontWeight: 600, color: "var(--text-muted)", textTransform: "uppercase" }}>SYN Recv</div>
              <div className="mono" style={{ fontSize: 16, fontWeight: 700, color: "var(--blue)", marginTop: 4 }}>{metrics?.syn_recv ?? 0}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
