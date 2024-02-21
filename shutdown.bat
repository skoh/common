@REM usage: shutdown oh-common

set MODULE_NAME=%1
set /P PID=<%MODULE_NAME%.pid

taskkill /f /pid %PID%
