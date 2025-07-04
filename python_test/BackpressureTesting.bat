@echo off
setlocal

set NUM_CLIENTS=5
set HOST=127.0.0.1
set PORT=7070
set PING_INTERVAL=0.001
set PING_COUNT=100000
set PYTHON_SCRIPT=BackpressureTesting.py

echo Launching %NUM_CLIENTS% Python clients...

for /L %%i in (1,1,%NUM_CLIENTS%) do (
    start "Client %%i" cmd /c py %PYTHON_SCRIPT% %HOST% %PORT% %PING_INTERVAL% %PING_COUNT%
)

echo All clients launched. Press any key to exit this script window.
pause > nul
