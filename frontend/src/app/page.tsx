"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import dynamic from "next/dynamic";
import dataProtectionAnim from "../../public/Data protection isometric.json";

// Dynamically import local wrapper to avoid third-party SSR issues
const Lottie = dynamic(() => import("../components/LottieWrapper"), { ssr: false });

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    await new Promise((r) => setTimeout(r, 600)); // Simulate auth delay
    if (username === "admin" && password === "admin") {
      router.push("/dashboard");
    } else {
      setLoading(false);
      alert("Invalid credentials. Try admin / admin");
    }
  };

  return (
    <div className="login-page">
      <div style={{ maxWidth: "500px", width: "100%", display: "flex", flexDirection: "column", alignItems: "center" }}>
        <h1 style={{ fontSize: "36px", fontWeight: 600, color: "var(--text-primary)", marginBottom: "16px", textAlign: "center", letterSpacing: "-0.02em" }}>
          Secure Enterprise Security
        </h1>
        <p style={{ color: "var(--text-secondary)", fontSize: "16px", textAlign: "center", marginBottom: "40px" }}>
          Monitor, detect, and neutralize threats with next-generation machine learning protection.
        </p>
        <div style={{ width: "100%", maxWidth: "480px" }}>
          <Lottie animationData={dataProtectionAnim} loop={true} />
        </div>
      </div>

      <div className="login-card">
        <div className="login-logo">
          <div className="login-logo-icon">🛡️</div>
          <div className="login-logo-title">NIDS</div>
          <div className="login-logo-sub">Network Intrusion Detection System</div>
        </div>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label className="form-label" htmlFor="username">Email address</label>
            <input
              type="text"
              id="username"
              className="form-input"
              placeholder="admin"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="off"
              required
            />
          </div>

          <div className="form-group" style={{ marginBottom: 24 }}>
            <label className="form-label" htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-login" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <div className="login-hint">
          Hint: admin / admin
        </div>
      </div>
    </div>
  );
}
