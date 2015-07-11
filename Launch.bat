@echo off
@title JudoMS Console
set CLASSPATH=.;dist\*
java -client -Dnet.sf.odinms.wzpath=wz server.Start
pause