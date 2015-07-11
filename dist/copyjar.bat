@echo off
@title Jar Copier
Color 0A
xcopy /y "C:\Users\Administrator\Desktop\Helisium\dist\Helisium.jar" "C:\Users\Administrator\Desktop\XenoMs Source Files\dist\Helisium.jar"
del "C:\Users\Administrator\Desktop\Helisium\dist\Helisium.jar"
pause