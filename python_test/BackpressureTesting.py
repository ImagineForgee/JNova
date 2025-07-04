import socket
import json
import threading
import time

class JsonClient:
    def __init__(self, host='127.0.0.1', port=7070, ping_interval=0.01, ping_count=1000):
        self.host = host
        self.port = port
        self.sock = None
        self.running = False
        self.ping_interval = ping_interval
        self.ping_count = ping_count

    def connect(self):
        self.sock = socket.create_connection((self.host, self.port))
        self.running = True
        print(f"Connected to {self.host}:{self.port}")
        threading.Thread(target=self.receive_loop, daemon=True).start()
        threading.Thread(target=self.spam_ping_loop, daemon=True).start()

    def send_json(self, data: dict):
        try:
            msg = json.dumps(data) + "\n"
            self.sock.sendall(msg.encode('utf-8'))
        except Exception as e:
            print("Send error:", e)
            self.running = False

    def receive_loop(self):
        buffer = ""
        while self.running:
            try:
                data = self.sock.recv(4096)
                if not data:
                    print("Server closed connection")
                    self.running = False
                    break
                buffer += data.decode('utf-8')
                while "\n" in buffer:
                    line, buffer = buffer.split("\n", 1)
                    if line.strip():
                        self.handle_message(line.strip())
            except Exception as e:
                print("Receive error:", e)
                self.running = False
                break

    def handle_message(self, msg):
        try:
            data = json.loads(msg)
            print("Received:", data)
        except json.JSONDecodeError:
            print("Invalid JSON received:", msg)

    def spam_ping_loop(self):
        print(f"Spamming {self.ping_count} PING commands every {self.ping_interval}s")
        for _ in range(self.ping_count):
            if not self.running:
                break
            self.send_json({
                "type": "command",
                "command": "PING",
                "args": []
            })
            time.sleep(self.ping_interval)
        print("Finished spamming pings.")

    def close(self):
        self.running = False
        if self.sock:
            self.sock.close()

if __name__ == "__main__":
    import sys

    host = sys.argv[1] if len(sys.argv) > 1 else "127.0.0.1"
    port = int(sys.argv[2]) if len(sys.argv) > 2 else 7070
    interval = float(sys.argv[3]) if len(sys.argv) > 3 else 0.005
    count = int(sys.argv[4]) if len(sys.argv) > 4 else 1000

    client = JsonClient(host=host, port=port, ping_interval=interval, ping_count=count)
    client.connect()

    try:
        while client.running:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Disconnecting...")
        client.close()
