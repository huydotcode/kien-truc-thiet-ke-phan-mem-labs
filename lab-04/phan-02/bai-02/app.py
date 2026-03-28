from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return "Hello, Docker Flask!"

if __name__ == '__main__':
    # Bind to 0.0.0.0 to allow external access in Docker
    app.run(host='0.0.0.0', port=5000)
