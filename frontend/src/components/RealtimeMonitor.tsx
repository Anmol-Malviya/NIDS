"use client";

import { useEffect, useRef, useState, useCallback } from "react";
import {
  Activity,
  Pause,
  Play,
  Trash2,
  Shield,
  ShieldAlert,
  CheckCircle,
  Wifi,
  WifiOff,
} from "lucide-react";

const API_URL = "http://127.0.0.1:5000/api";
const MAX_EVENTS = 50;
const POLL_INTERVAL = 2000;

type RTEvent = {
  id: number;
  timestamp: string;
  src_ip: string;
  dst_ip: string;
  attack_type: string;
  severity: string;
  status: string;
  isNew?: boolean;
};

function getActivityMeta(event: RTEvent) {
  const isLegal = event.attack_type === "BENIGN";
  if (isLegal) {
    return {
      label: "Legal",
      sublabel: "BENIGN",
      rowBg: "rgba(16, 185, 129, 0.03)",
      rowBorder: "rgba(16, 185, 129, 0.12)",
      badgeBg: "var(--green-light)",
      badgeColor: "var(--green)",
      iconColor: "var(--green)",
      Icon: CheckCircle,
      statusLabel: "Normal",
    };
  }
  if (event.severity === "High") {
    return {
      label: "Threat",
      sublabel: event.attack_type,
      rowBg: "rgba(239, 68, 68, 0.03)",
      rowBorder: "rgba(239, 68, 68, 0.12)",
      badgeBg: "var(--red-light)",
      badgeColor: "var(--red)",
      iconColor: "var(--red)",
      Icon: ShieldAlert,
      statusLabel: "High",
    };
  }
  if (event.severity === "Medium") {
    return {
      label: "Threat",
      sublabel: event.attack_type,
      rowBg: "rgba(245, 158, 11, 0.03)",
      rowBorder: "rgba(245, 158, 11, 0.12)",
      badgeBg: "var(--amber-light)",
      badgeColor: "var(--amber)",
      iconColor: "var(--amber)",
      Icon: ShieldAlert,
      statusLabel: "Medium",
    };
  }
  // Low
  return {
    label: "Threat",
    sublabel: event.attack_type,
    rowBg: "rgba(37, 99, 235, 0.03)",
    rowBorder: "rgba(37, 99, 235, 0.10)",
    badgeBg: "var(--blue-light)",
    badgeColor: "var(--blue)",
    iconColor: "var(--blue)",
    Icon: Shield,
    statusLabel: "Low",
  };
}

function formatTime(ts: string) {
  try {
    const d = new Date(ts.replace(" ", "T"));
    return d.toLocaleTimeString("en-US", { hour12: false });
  } catch {
    return ts.slice(11, 19) || ts;
  }
}

interface Props {
  backendOffline: boolean;
}

export default function RealtimeMonitor({ backendOffline }: Props) {
  const [events, setEvents] = useState<RTEvent[]>([]);
  const [paused, setPaused] = useState(false);
  const [sessionCount, setSessionCount] = useState(0);
  const [legalCount, setLegalCount] = useState(0);
  const [threatCount, setThreatCount] = useState(0);
  const [connected, setConnected] = useState(!backendOffline);
  const lastIdRef = useRef<number>(0);
  const pausedRef = useRef(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Keep pausedRef in sync with state
  useEffect(() => {
    pausedRef.current = paused;
  }, [paused]);

  const poll = useCallback(async () => {
    if (pausedRef.current) return;
    try {
      const url =
        lastIdRef.current > 0
          ? `${API_URL}/realtime?since=${lastIdRef.current}&limit=20`
          : `${API_URL}/realtime?limit=20`;
      const res = await fetch(url);
      if (!res.ok) throw new Error("non-ok");
      const data: RTEvent[] = await res.json();
      setConnected(true);

      if (data.length === 0) return;

      // Track the highest ID seen
      const maxId = Math.max(...data.map((e) => e.id));
      if (maxId > lastIdRef.current) lastIdRef.current = maxId;

      // Mark as new if incremental
      const tagged = data.map((e) => ({ ...e, isNew: true }));

      setEvents((prev) => {
        const existingIds = new Set(prev.map((e) => e.id));
        const fresh = tagged.filter((e) => !existingIds.has(e.id));
        if (fresh.length === 0) return prev;
        return [...fresh, ...prev].slice(0, MAX_EVENTS);
      });

      setSessionCount((c) => c + data.length);
      setLegalCount((c) => c + data.filter((e) => e.attack_type === "BENIGN").length);
      setThreatCount((c) => c + data.filter((e) => e.attack_type !== "BENIGN").length);
    } catch {
      setConnected(false);
    }
  }, []);

  useEffect(() => {
    poll(); // initial load
    intervalRef.current = setInterval(poll, POLL_INTERVAL);
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current);
    };
  }, [poll]);

  const handlePauseResume = () => {
    setPaused((p) => !p);
  };

  const handleClear = () => {
    setEvents([]);
    setSessionCount(0);
    setLegalCount(0);
    setThreatCount(0);
    lastIdRef.current = 0;
  };

  return (
    <div className="rt-monitor card mb-8">
      {/* Header */}
      <div className="rt-header">
        <div className="rt-header-left">
          <div className="rt-icon">
            <Activity size={20} />
          </div>
          <div>
            <div className="rt-title">Real-Time Network Activity Monitor</div>
            <div className="rt-subtitle">
              Detecting all inbound &amp; outbound traffic — legal and illegal
            </div>
          </div>
        </div>

        <div className="rt-header-right">
          {/* Counters */}
          <div className="rt-counters">
            <div className="rt-counter rt-counter-total">
              <span>{sessionCount}</span>
              <label>Events</label>
            </div>
            <div className="rt-counter rt-counter-legal">
              <span>{legalCount}</span>
              <label>Legal</label>
            </div>
            <div className="rt-counter rt-counter-threat">
              <span>{threatCount}</span>
              <label>Threats</label>
            </div>
          </div>

          {/* Live badge */}
          <div className={`rt-live-badge ${paused ? "rt-paused" : ""}`}>
            {paused ? (
              <>
                <div className="rt-live-dot rt-paused-dot" />
                PAUSED
              </>
            ) : connected ? (
              <>
                <div className="rt-live-dot" />
                LIVE
              </>
            ) : (
              <>
                <WifiOff size={12} />
                OFFLINE
              </>
            )}
          </div>

          {/* Controls */}
          <button
            className={`rt-btn ${paused ? "rt-btn-active" : ""}`}
            onClick={handlePauseResume}
            title={paused ? "Resume" : "Pause"}
          >
            {paused ? <Play size={15} /> : <Pause size={15} />}
            {paused ? "Resume" : "Pause"}
          </button>
          <button className="rt-btn" onClick={handleClear} title="Clear feed">
            <Trash2 size={15} />
            Clear
          </button>
        </div>
      </div>

      {/* Connection banner */}
      {!connected && (
        <div className="rt-offline-bar">
          <WifiOff size={14} />
          <span>
            Backend offline — connect the Python server at{" "}
            <strong>{API_URL}</strong> to see live events
          </span>
        </div>
      )}

      {/* Table */}
      <div className="rt-table-wrap">
        <table className="rt-table">
          <thead>
            <tr>
              <th style={{ width: 110 }}>Status</th>
              <th style={{ width: 110 }}>Time</th>
              <th>Source IP</th>
              <th>Target IP</th>
              <th>Activity Type</th>
              <th style={{ width: 90 }}>Severity</th>
              <th style={{ width: 90 }}>State</th>
            </tr>
          </thead>
          <tbody>
            {events.length === 0 ? (
              <tr>
                <td colSpan={7} className="rt-empty">
                  <Wifi size={28} style={{ opacity: 0.3, marginBottom: 8 }} />
                  <div>Waiting for network events…</div>
                  <div style={{ fontSize: 13, marginTop: 4, opacity: 0.6 }}>
                    {connected
                      ? "Events will appear here in real-time as traffic is detected"
                      : "Connect the backend server to start receiving events"}
                  </div>
                </td>
              </tr>
            ) : (
              events.map((event) => {
                const meta = getActivityMeta(event);
                const Icon = meta.Icon;
                return (
                  <tr
                    key={event.id}
                    className={`rt-row ${event.isNew ? "rt-row-new" : ""}`}
                    style={{
                      background: meta.rowBg,
                      borderLeft: `3px solid ${meta.rowBorder}`,
                    }}
                  >
                    {/* Status */}
                    <td>
                      <div className="rt-status-cell">
                        <Icon size={15} color={meta.iconColor} />
                        <span
                          className="rt-badge"
                          style={{
                            background: meta.badgeBg,
                            color: meta.badgeColor,
                          }}
                        >
                          {meta.label}
                        </span>
                      </div>
                    </td>

                    {/* Time */}
                    <td className="mono rt-time">{formatTime(event.timestamp)}</td>

                    {/* IPs */}
                    <td className="mono rt-ip">{event.src_ip}</td>
                    <td className="mono rt-ip">{event.dst_ip}</td>

                    {/* Activity */}
                    <td>
                      <span className="rt-activity">{meta.sublabel}</span>
                    </td>

                    {/* Severity */}
                    <td>
                      <span
                        className="rt-severity-badge"
                        style={{
                          background: meta.badgeBg,
                          color: meta.badgeColor,
                        }}
                      >
                        {meta.statusLabel}
                      </span>
                    </td>

                    {/* State */}
                    <td>
                      <span
                        className="rt-state-badge"
                        style={{
                          background:
                            event.status === "RESOLVED"
                              ? "var(--green-light)"
                              : "var(--bg-muted)",
                          color:
                            event.status === "RESOLVED"
                              ? "var(--green)"
                              : "var(--text-muted)",
                        }}
                      >
                        {event.status === "RESOLVED" ? "Resolved" : "Active"}
                      </span>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {events.length >= MAX_EVENTS && (
        <div className="rt-cap-notice">
          Showing latest {MAX_EVENTS} events — older events are auto-removed
        </div>
      )}
    </div>
  );
}
