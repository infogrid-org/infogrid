#!/bin/sh
#
# Build all of InfoGrid.
# Synopsis:
#     tools/buildall.sh [-r]
#
# -r stands for rebuild

script=tools/`basename $0`

if [ ! -d modules ]; then
	echo "ERROR: this script must be invoked from the root directory of the branch using the command ${script}"
	exit 1;
fi;

CLEANFLAGS=-Dno.deps=1;
BUILDFLAGS=-Dno.deps=1;
RUNFLAGS=-Dno.deps=1;
TMPFILE=`mktemp /tmp/infogrid-build-temp.XXXX`;

do_clean=1;
do_build=1;
do_run=1;
do_modules=1;
do_apps=1;
do_tests=1;
do_nothing=1;
do_all=1;
verbose=1;
help=1;
ANTFLAGS=;

for arg in $*; do
	if [ "${ANTFLAGS}" = 'ANTFLAGS' ]; then
		ANTFLAGS="$arg";
	elif [ "$arg" = '-clean' ]; then
		do_clean=0;
	elif [ "$arg" = '-build' ]; then
		do_build=0;
	elif [ "$arg" = '-run' ]; then
		do_run=0;
	elif [ "$arg" = '-n' ]; then
		do_nothing=0;
	elif [ "$arg" = '-v' ]; then
		verbose=0;
	elif [ "$arg" = '-h' ]; then
		help=0;
	elif [ "$arg" = '-a' ]; then
		do_all=0;
	elif [ "$arg" = '-antflags' ]; then
		ANTFLAGS='ANTFLAGS';
	elif [ "$arg" = 'modules' ]; then
		do_modules=0;
	elif [ "$arg" = 'apps' ]; then
		do_apps=0;
	elif [ "$arg" = 'tests' ]; then
		do_tests=0;
	else
		echo "ERROR: Unknown argument: $arg"
		exit 1;
	fi
	shift;
done

# echo args ${do_clean} ${do_build} ${do_run} ${do_modules} ${do_apps} ${do_tests} ${do_nothing} ${do_all} ${verbose} ${ANTFLAGS}
# exit 0;

if [ "${help}" = 0 -o "${ANTFLAGS}" = 'ANTFLAGS' ]; then
	echo Synopsis:
	echo "    ${script} [-v][-h][-n] [-clean][-build][-run] [-antflags <flags>] [<category>...]"
	echo "        -v: verbose output"
	echo "        -h: this help"
	echo "        -n: do not execute, only print"
	echo "        -a: complete rebuild, equivalent to -clean -build -run modules apps tests"
	echo "        -clean: remove old build artifacts"
	echo "        -build: build"
	echo "        -run: run"
	echo "            (more than one of -clean,-build,-run may be given. Default is -build,-run)"
	echo "        -antflags <flags>: pass flags to ant invokation"
	echo "        category: one or more of modules, apps, tests"
	exit 1;
fi

if [ "${do_all}" = 0 ]; then
	do_clean=0;
	do_build=0;
	do_run=0;
	do_modules=0;
	do_apps=0;
	do_tests=0;
else
	if [ "${do_clean}" = 1 -a "${do_build}" = 1 -a "${do_run}" = 1 ]; then
		do_clean=1;
		do_build=0;
		do_run=0;
	fi
	if [ "${do_modules}" = 1 -a "${do_apps}" = 1 -a "${do_tests}" = 1 ]; then
		do_modules=0;
		do_apps=0;
		do_tests=0;
	fi
fi

if [ "${do_nothing}" = 0 ]; then
	/bin/echo -n Will
	if [ "${do_clean}" = 0 ]; then
		/bin/echo -n " clean"
	fi
	if [ "${do_build}" = 0 ]; then
		/bin/echo -n " build"
	fi
	if [ "${do_run}" = 0 ]; then
		/bin/echo -n " run"
	fi
	/bin/echo -n :
	if [ "${do_modules}" = 0 ]; then
		/bin/echo -n " modules"
	fi
	if [ "${do_apps}" = 0 ]; then
		/bin/echo -n " apps"
	fi
	if [ "${do_tests}" = 0 ]; then
		/bin/echo -n " tests"
	fi
	if [ "${verbose}" = 0 ]; then
		/bin/echo -n " (verbose)"
	fi
	/bin/echo .
	exit 0;
fi

trap stop SIGINT
function stop
{
	echo 'Interrupted by control-c. Exiting ...'
	exit 1;
}

run_command()
{
	echo ' -' `date '+%Y%m%e-%H:%M:%S'` : $1
	shift
	if [ "${verbose}" = 0 ]; then
		$*
        	if [ "${?}" -gt 0 ]; then
			echo FAILED: $*
			return 1;
		fi
	else
		$* >${TMPFILE} 2>&1
        	if [ "${?}" -gt 0 ]; then
			cat ${TMPFILE};
			echo FAILED: $*
			return 1;
		fi
	fi
	return 0;
}

filter_modules()
{
	egrep -v '^(\\s)*#' $1 | grep -v "$2" | sed -e 's/\[.*\]//g'
}

clean_module()
{
	run_command $1 ant ${ANTFLAGS} -f $1/build.xml ${CLEANFLAGS} clean
	return "${?}";
}

build_module()
{
	run_command $1 ant ${ANTFLAGS} -f $1/build.xml ${BUILDFLAGS} $2
	return "${?}";
}

run_module()
{
	run_command $1 ant ${ANTFLAGS} -f $1/build.xml ${RUNFLAGS} run
	return "${?}";
}

if [ "${do_clean}" = 0 ]; then
	if [ "${do_modules}" = 0 ]; then
		echo '**** Cleaning modules ****'
		for f in `filter_modules modules/ALLMODULES '\[noclean\]'`; do
			clean_module modules/$f || exit 1;
		done;
	fi

	if [ "${do_apps}" = 0 ]; then
		echo '**** Cleaning apps ****'
		for f in `filter_modules apps/ALLAPPS '\[noclean\]'`; do
			clean_module apps/$f || exit 1;
		done;
	fi

	if [ "${do_tests}" = 0 ]; then
		echo '**** Cleaning tests ****'
		for f in `filter_modules tests/ALLTESTS '\[noclean\]'`; do
			clean_module tests/$f || exit 1;
		done;
	fi
fi

if [ "${do_build}" = 0 ]; then
	if [ "${do_modules}" = 0 ]; then
		echo '**** Building modules ****'
		for f in `filter_modules modules/ALLMODULES '\[nobuild\]'`; do
			build_module modules/$f jar || exit 1;
		done;
	fi

	if [ "${do_apps}" = 0 ]; then
		echo '**** Building apps ****'
		for f in `filter_modules apps/ALLAPPS '\[nobuild\]'`; do
			build_module apps/$f dist || exit 1;
		done;
	fi

	if [ "${do_tests}" = 0 ]; then
		echo '**** Building tests ****'
		for f in `filter_modules tests/ALLTESTS '\[nobuild\]'`; do
			build_module tests/$f jar || exit 1;
		done;
	fi
fi

if [ "${do_run}" = 0 ]; then
	if [ "${do_tests}" = 0 ]; then
		echo '**** Running tests ****'
		for f in `filter_modules tests/ALLTESTS '\[norun\]'`; do
			run_module tests/$f run || exit 1;
		done;
	fi
fi

