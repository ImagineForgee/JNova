import socket

def main():
    host = '127.0.0.1'
    port = 7070

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((host, port))
        print(f"Connected to {host}:{port}")

        message = "PING\n"
        sock.sendall(message.encode('utf-8'))

        buffer = bytearray()
        while True:
            chunk = sock.recv(1)
            if not chunk or chunk == b'\n':
                break
            buffer += chunk

        print("Server replied:", buffer.decode('utf-8'))

if __name__ == "__main__":
    main()
