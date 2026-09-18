"use client";

import { useEffect, useState, useCallback } from "react";
import Link from "next/link";

const API_URL = "http://127.0.0.1:5000/api";

type Alert = {
  id: number;
  timestamp: string;
  src_ip: string;
  dst_ip: string;
  attack_type: string;
  severity: string;
  status?: string;
};

export default function Alerts() {
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAlerts = useCallback(async () => {
    try {
      const res = await fetch(`${API_URL}/alerts`);
      const data = await res.json();
      setAlerts(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchAlerts();
    const interval = setInterval(fetchAlerts, 5000);
    return () => clearInterval(interval);
  }, [fetchAlerts]);

  const resolveAlert = async (id: number) => {
    try {
      const res = await fetch(`${API_URL}/resolve/${id}`);
      if (res.ok) fetchAlerts();
    } catch (err) {
      console.error("Failed to resolve alert", err);
    }
  };

  return (
    <div className="page-wrapper">
      <div className="page-header flex items-center justify-between">
        <div>
          <h1>Alert Log</h1>
          <p>Complete history of detected intrusions and anomalies.</p>
        </div>
      </div>

      <div className="card" style={{ padding: 0 }}>
        <div className="table-wrapper" style={{ border: "none", borderRadius: "var(--radius-md)" }}>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Timestamp</th>
                <th>Source IP</th>
                <th>Target IP</th>
                <th>Classification</th>
                <th>Severity</th>
                <th>Status</th>
                <th className="text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {alerts.map((alert) => (
                <tr key={alert.id}>
                  <td className="mono" style={{ color: "var(--text-muted)" }}>#{alert.id}</td>
                  <td className="mono">{alert.timestamp}</td>
                  <td className="mono">{alert.src_ip}</td>
                  <td className="mono">{alert.dst_ip}</td>
                  <td style={{ fontWeight: 500 }}>{alert.attack_type}</td>
                  <td>
                    <span className={`badge badge-${alert.severity.toLowerCase()}`}>
                      {alert.severity}
                    </span>
                  </td>
                  <td>
                    <span className={`badge badge-${alert.status === "RESOLVED" ? "resolved" : "active"}`}>
                      {alert.status === "RESOLVED" ? "Resolved" : "Active"}
                    </span>
                  </td>
                  <td className="text-right">
                    <div className="flex items-center gap-2" style={{ justifyContent: "flex-end" }}>
                      <Link href={`/details/${alert.id}`} className="btn btn-ghost">
                        Details
                      </Link>
                      {alert.status !== "RESOLVED" && (
                        <button
                          className="btn btn-resolve"
                          onClick={() => resolveAlert(alert.id)}
                        >
                          Resolve
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {alerts.length === 0 && !loading && (
                <tr>
                  <td colSpan={8} style={{ textAlign: "center", padding: "40px", color: "var(--text-muted)", fontSize: 13 }}>
                    No alerts found.
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
