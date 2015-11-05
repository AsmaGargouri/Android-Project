@echo off
time /T
REM ======================================================================================
REM
REM user args :
REM ~~~~~~~~~~~
REM %1 %2 %3 			: call ant command with given args
REM -g  %2 %3 %4  : call ant command with given args and -g for filter Trying - 
REM 			requirement: grep function installed on your PC 
REM -gg 					: 
REM build-all			: run cvs update and full-build for each user
REM rtag-delivery	: run cvs update for each user, run rtag then run delivery for each user
REM 
REM history: 
REM ~~~~~~~~
REM changed by guermazi 20/10/2010 add commands build-all and rtag-delivery
REM
REM ======================================================================================

set SETTING_FILE=.buildSettings
IF "%1"=="-g" goto GREP:
IF "%1"=="-gg" goto GREP2:

IF "%1"=="u-qq" goto GREP3:

IF "%1"=="build-all" goto BUILDALL:
IF "%1"=="rtag-delivery" goto FULLRTAG:

rem Starting ant
ant -f ant/build.xml %1 %2 %3 
goto END:

:GREP
ant -f ant/build.xml %2 %3 %4 | grep -v Trying
goto END:

:GREP2
ant -f ant/build.xml %2 %3 %4 | grep -v Trying | grep -i %2
goto END:

:GREP3
ant -f ant/build.xml -q u-q | grep -v passfile| grep -v "_cvs.exec:"| grep -v "cvs -q update -dP" | grep -i cvs
goto END:

REM ====================================================================
REM For all users in .runantSettings run:
REM 	ant u-q
REM 	ant full-build
REM ====================================================================
:BUILDALL
:checkfile
if exist "%SETTING_FILE%" goto :doping_BUILDALL
echo Input file %SETTING_FILE% does not exist
echo.
goto :eof

:doping_BUILDALL
for /f "tokens=*" %%u in (%SETTING_FILE%) do call :full_build_user %%u
goto END:

:full_build_user
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ
echo  BUILDALL: run UPDATE and FULL-BUILD for user : %1 
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ

echo username=%1> ant/.systemSettings
call ant -f ant/build.xml u-q | grep -v Trying
call ant -f ant/build.xml full-build | grep -v Trying
del ant\.systemSettings
::DONE

goto END:


REM ====================================================================
REM run rtag then 
REM Run for all users in .runantSettings ant delivery
REM ====================================================================
:FULLRTAG
:checkfile
if exist %SETTING_FILE% goto :doping_FULLRTAG
echo Input file %SETTING_FILE% does not exist
echo.
goto :eof

:doping_FULLRTAG
for /f "tokens=*" %%u in (%SETTING_FILE%) do call :cvs_update_user %%u

echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ
echo  FULLRTAG:	run RTAG  
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ
call ant -f ant/build.xml rtag | grep -v Trying

rem for /f "tokens=*" %%u in (%SETTING_FILE%) do call :delivery_user %%u

goto END:

:delivery_user
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ
echo  FULLRTAG:	run DELIVERY for user : %1 
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ

echo username=%1> ant/.systemSettings
call ant -f ant/build.xml delivery | grep -v Trying
del ant\.systemSettings
::DONE


:cvs_update_user
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ
echo  FULLRTAG:	run UPDATE for user : %1 
echo  õõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõõ

echo username=%1> ant/.systemSettings
call ant -f ant/build.xml u-q | grep -v Trying
del ant\.systemSettings
::DONE

goto END:


:END
time /T
