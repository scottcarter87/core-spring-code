#!/bin/bash

set -e

git clean -xffd > git-clean.out
./mvnw clean > mvnw-clean.out

set +e

for x in `echo *solution`; do
  outfile="mvnw-clean-test-${x}-from-top.out"
  echo "======================== testing $x from" `pwd` "- output in ${outfile}"
  (./mvnw -pl $x clean test > ${outfile} 2>&1)
  retVal=$?
  if [ $retVal -ne 0 ]; then
    echo "FAILED!  See ${outfile} for details"
  fi

  outfile="../mvnw-clean-test-${x}-from-${x}.out"
  (cd ${x} && echo "======================== testing ${x} from" `pwd` "- outfile in ${outfile}" && ../mvnw clean test > ${outfile} 2>&1)
  if [ $retVal -ne 0 ]; then
    echo "FAILED!  See ${outfile} for details"
  fi
done
