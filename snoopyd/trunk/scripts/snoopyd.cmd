@echo off

rem
rem  Copyrigth 2011, Snoopy Project
rem 

set CP=lib/snoopyd.jar;lib/log4j-1.2.15.jar;lib/Ice.jar;config/log4j.properies;lib/sigar.jar;
set DEFINES=-Dsnoopyd.configuration=config/snoopyd.conf -Dlog4j.configuration=config/log4j.properties
set LIB=native/;
set ENTRY_POINT=com.googlecode.snoopyd.Launcher 

java -Xms64m -Xmx128m -Xss64m -classpath %CP% -Djava.library.path=%LIB% %DEFINES% %ENTRY_POINT%