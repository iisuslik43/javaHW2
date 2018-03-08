#!/bin/bash


rootdir=$(pwd)
for gradlewfile in $(find . -name gradlew); do
    dir=$(dirname $gradlewfile)
    cd $dir
    chmod +x ./gradlew
    ./gradlew build --stacktrace
    if [ $? -ne 0 ]; then
        echo "Error while running in "
        echo $dir
        exit 1
    fi
    cd $rootdir
done
