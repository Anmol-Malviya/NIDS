from flask import Flask
from routes.api import api_bp
from routes.pages import pages_bp
from utils.db import init_db

app = Flask(__name__)

init_db()

app.register_blueprint(api_bp)
app.register_blueprint(pages_bp)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)