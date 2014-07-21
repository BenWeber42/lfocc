#!/usr/bin/env sh

languages="C Cpp Functional Java Javali PureState SampleLanguage"

# delete all existing compilers
rm -r compilers/*

pass=0
fail=0
total=0

# generate compilers
for language in $languages
do
    echo "Generating language $language:"
    output=$(java -cp bin lfocc.framework.Main configs/$language/Configuration.xml)
    if [ $? -ne 0 ]
    then
        echo ">>> Failure!"
        echo "$output"
    else
        echo ">>> Success!"

        # go over all testcases:
        tests=$(find tests/$language/ -name "*.lang" 2>/dev/null)

        for test in $tests
        do
            echo -n ">>> Testcase $test... "

            output=$(java -cp compilers/$language/bin lfocc.compilers.$language.Main $test $test.out)

            if [ $? -ne 0 ]
            then
                echo "FAILURE"
                echo "$output"
                fail=$((fail+1))
            else
                echo "OK"
                pass=$((pass+1))
            fi
            total=$((total+1))
        done
    fi
done

echo
echo "Finished ($fail failed, $pass passed, $total in total)"
