from flask import Blueprint, render_template
from utils.db import get_db

pages_bp = Blueprint("pages", __name__)

@pages_bp.route("/")

def login():
    return render_template("login.html")

@pages_bp.route("/dashboard")

def dashboard():
    return render_template("dashboard.html")

@pages_bp.route("/alerts")

def alerts():

    conn = get_db()

    alerts = conn.execute("""

    SELECT * FROM alerts
    ORDER BY timestamp DESC

    """).fetchall()

    conn.close()

    return render_template("alerts.html", alerts=alerts)

@pages_bp.route("/details/<int:id>")

def details(id):

    conn = get_db()

    alert = conn.execute("""

    SELECT * FROM alerts
    WHERE id=?

    """, (id,)).fetchone()

    conn.close()

    return render_template("details.html", alert=alert)

@pages_bp.route("/health")

def health():
    return render_template("health.html")