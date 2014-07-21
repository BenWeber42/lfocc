#!/usr/bin/env sh

# delete all existing compilers
rm -r ./compilers/*

for i in `find configs -name "Configuration.xml"`; do
    echo Generating language $i\:
    java -cp bin lfocc.framework.Main $i 1>/dev/null
    if [ $? -ne 0 ]; then
        echo ">>> Failure!"
    else
        echo ">>> Success!"
    fi
done
