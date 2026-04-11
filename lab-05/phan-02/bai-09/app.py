from flask import Flask
app = Flask(__name__)

@app.route('/')
def hello():
    return "<h1>Hello!</h1><p>Ứng dụng Python Flask đang chạy trong Docker.</p>"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
