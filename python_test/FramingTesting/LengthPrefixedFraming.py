import socket
import struct

def main():
    host = '127.0.0.1'
    port = 7070

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((host, port))
        print(f"Connected to {host}:{port}")

        message = "PING\n".encode('utf-8')
        length = struct.pack('>I', len(message))

        sock.sendall(length + message)

        length_data = sock.recv(4)
        if not length_data:
            print("Server closed connection.")
            return

        response_length = struct.unpack('>I', length_data)[0]
        response = sock.recv(response_length).decode()
        print(f"Server replied: {response}")

if __name__ == "__main__":
    main()
