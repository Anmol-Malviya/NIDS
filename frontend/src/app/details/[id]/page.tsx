"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";

const API_URL = "http://127.0.0.1:5000/api";

type AlertDetail = {
  id: number;
  timestamp: string;
  src_ip: string;
  dst_ip: string;
  attack_type: string;
  severity: string;
  payload: string;
  status: string;
};

export default function AlertDetails() {
  const { id } = useParams();
  const router = useRouter();
  const [alert, setAlert] = useState<AlertDetail | null>(null);

  useEffect(() => {
    const fetchDetail = async () => {
      try {
        const res = await fetch(`${API_URL}/alerts/${id}`);
        if (!res.ok) throw new Error("Not found");
        const data = await res.json();
        setAlert(data);
      } catch (err) {
        console.error(err);
      }
    };
    if (id) fetchDetail();
  }, [id]);

  const resolveAlert = async () => {
    try {
      await fetch(`${API_URL}/resolve/${id}`);
      router.push("/alerts");
    } catch (err) {
      console.error(err);
    }
  };

  if (!alert) {
    return <div className="page-wrapper"><div className="skeleton" style={{ height: 300, borderRadius: 12 }}></div></div>;
  }

  return (
    <div className="page-wrapper">
      <Link href="/alerts" className="btn btn-ghost" style={{ marginBottom: 20 }}>
        ← Back to Alerts
      </Link>

      <div className="card">
        <div className="flex items-center justify-between mb-6 pb-6" style={{ borderBottom: "1px solid var(--border)" }}>
          <div>
            <h1 style={{ fontSize: 22, fontWeight: 700, color: "var(--text-primary)", marginBottom: 4 }}>
              Incident #{alert.id}
            </h1>
            <p style={{ color: "var(--text-secondary)", fontSize: 13 }}>
              Captured at {alert.timestamp}
            </p>
          </div>
          <div className="flex items-center gap-4">
            <span className={`badge badge-${alert.severity.toLowerCase()}`}>
              {alert.severity} Severity
            </span>
            <span className={`badge badge-${alert.status === "RESOLVED" ? "resolved" : "active"}`}>
              {alert.status === "RESOLVED" ? "Resolved" : "Active"}
            </span>
          </div>
        </div>

        <div className="section-title">Network Metadata</div>
        <div className="detail-grid mb-6">
          <div className="detail-item">
            <div className="detail-item-label">Source IP (Attacker)</div>
            <div className="detail-item-value">{alert.src_ip}</div>
          </div>
          <div className="detail-item">
            <div className="detail-item-label">Destination IP (Target)</div>
            <div className="detail-item-value">{alert.dst_ip}</div>
          </div>
          <div className="detail-item">
            <div className="detail-item-label">Classification</div>
            <div className="detail-item-value">{alert.attack_type}</div>
          </div>
          <div className="detail-item">
            <div className="detail-item-label">Mitigation Status</div>
            <div className="detail-item-value">{alert.status}</div>
          </div>
        </div>

        <div className="section-title">Packet Payload Inspection</div>
        <div
          className="mono"
          style={{
            background: "var(--bg-base)",
            padding: 16,
            borderRadius: 6,
            border: "1px solid var(--border)",
            color: "var(--text-secondary)",
            whiteSpace: "pre-wrap",
            wordBreak: "break-all",
            fontSize: 13,
            lineHeight: 1.5,
          }}
        >
          {alert.payload || "No packet payload data available for this event."}
        </div>

        {alert.status !== "RESOLVED" && (
          <div className="flex items-center justify-between mt-6 pt-6" style={{ borderTop: "1px solid var(--border)" }}>
            <p style={{ fontSize: 13, color: "var(--text-muted)" }}>
              After verifying the threat, resolve this incident to update global metrics.
            </p>
            <button className="btn btn-resolve" onClick={resolveAlert} style={{ padding: "10px 20px" }}>
              Mark as Resolved
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
