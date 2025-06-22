import socket
import json
import threading
import time

class JsonClient:
    def __init__(self, host='127.0.0.1', port=7070):
        self.host = host
        self.port = port
        self.sock = None
        self.running = False

    def connect(self):
        self.sock = socket.create_connection((self.host, self.port))
        self.running = True
        print(f"Connected to {self.host}:{self.port}")
        threading.Thread(target=self.receive_loop, daemon=True).start()
        threading.Thread(target=self.ping_loop, daemon=True).start()

    def send_json(self, data: dict):
        msg = json.dumps(data) + "\n"
        self.sock.sendall(msg.encode('utf-8'))

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

    def ping_loop(self):
        while self.running:
            self.send_json({
                "type": "command",
                "command": "PING",
                "args": []
            })
            time.sleep(10)

    def send_command(self, command, args=None):
        if command.upper() == "BROADCAST":
            content = " ".join(args or [])
            data = {
                "type": "command",
                "command": "BROADCAST",
                "message": {
                    "type": "message",
                    "sender": "PythonClient",
                    "content": content
                }
            }
        else:
            if args is None:
                args = []
            data = {
                "type": "command",
                "command": command,
                "args": args
            }

        self.send_json(data)

    def close(self):
        self.running = False
        if self.sock:
            self.sock.close()

if __name__ == "__main__":
    client = JsonClient()
    client.connect()

    time.sleep(1)
    client.send_command("BROADCAST", ["Hello", "from", "Python", "client!"])

    try:
        while client.running:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Disconnecting...")
        client.close()
