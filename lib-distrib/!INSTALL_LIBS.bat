rem >>>>> add Tween Engine library
cd tween
start cmd /c !install.bat
cd ../

rem >>>>> add Post-processing library
cd post_processing
start cmd /c !install.bat
cd ../

rem >>>>> add Libgdx  tools
cd libgdx_tools
start cmd /c !install.bat
cd ../

rem >>>>> add LibGdx
CALL install_gdx-natives.bat
pause