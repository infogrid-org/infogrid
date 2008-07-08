#!/bin/sh
#
# Performs some sanity checks for common errors. Should be run prior to
# check-in

echo '** Checking that no funny paths exist **'
grep '\.\./\.\./\.\.' {modules,tests}/*/nbproject/project.properties
