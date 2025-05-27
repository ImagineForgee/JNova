import socket

def main():
    host = '127.0.0.1'
    port = 7070

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((host, port))
        print(f"Connected to {host}:{port}")

        message = "Hello from Python!\n" 
        sock.sendall(message.encode())

        response = sock.recv(1024).decode()
        print(f"Server replied: {response}")

if __name__ == "__main__":
    main()
