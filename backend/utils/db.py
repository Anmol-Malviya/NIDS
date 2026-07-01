import sqlite3

DATABASE = "database.db"

def get_db():

    conn = sqlite3.connect(DATABASE)

    conn.row_factory = sqlite3.Row

    return conn

def init_db():

    conn = get_db()

    conn.execute("""

    CREATE TABLE IF NOT EXISTS alerts(

        id INTEGER PRIMARY KEY AUTOINCREMENT,

        attack_type TEXT,

        prediction INTEGER,

        severity TEXT,

        src_ip TEXT,

        dst_ip TEXT,

        source_port INTEGER,

        destination_port INTEGER,

        protocol INTEGER,

        flow_duration REAL,

        status TEXT DEFAULT 'ACTIVE',

        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP

    )

    """)

    conn.commit()

    conn.close()