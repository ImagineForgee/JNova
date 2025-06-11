import socket
import threading
import time

SERVER_HOST = '127.0.0.1'
SERVER_PORT = 7070
KEEP_ALIVE_INTERVAL = 10

def keep_alive(sock):
    while True:
        try:
            time.sleep(KEEP_ALIVE_INTERVAL)
            sock.sendall(b"PING\n")
        except Exception as e:
            print(f"[!] Keep-alive failed: {e}")
            break

def listen_for_messages(sock):
    while True:
        try:
            data = sock.recv(1024)
            if not data:
                print("[-] Connection closed by server.")
                break
            print(f"[Server] {data.decode().strip()}")
        except Exception as e:
            print(f"[!] Listening failed: {e}")
            break

def main():
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((SERVER_HOST, SERVER_PORT))
        print(f"[+] Connected to {SERVER_HOST}:{SERVER_PORT}")

        threading.Thread(target=listen_for_messages, args=(sock,), daemon=True).start()
        threading.Thread(target=keep_alive, args=(sock,), daemon=True).start()

        while True:
            msg = input("You: ")
            if msg.strip().lower() in ("exit", "quit"):
                print("[-] Exiting...")
                break
            sock.sendall((msg + "\n").encode())

    except Exception as e:
        print(f"[!] Error: {e}")
    finally:
        try:
            sock.close()
        except:
            pass

if __name__ == "__main__":
    main()
