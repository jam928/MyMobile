#!/bin/sh
# Entry point of the "tests" service in docker-compose.yml:
#   docker compose run --rm tests
# Builds a copy of the sources (so the running "app" service's target/ isn't touched), runs the unit and
# integration tests with coverage, copies the HTML report to target/coverage/ and prints a summary.
rm -rf /build && mkdir -p /build
cp -r /app/pom.xml /app/src /build/
cd /build || exit 1

mvn -B -ntp verify
status=$?

rm -rf /app/target/coverage && mkdir -p /app/target/coverage
cp -r target/site/jacoco/. /app/target/coverage/ 2>/dev/null

sh /app/scripts/coverage-summary.sh /build/target
if [ -f /app/target/coverage/index.html ]; then
	echo "  HTML report: target/coverage/index.html"
	echo
fi
[ "$status" -eq 0 ] && echo "  BUILD PASSED" || echo "  BUILD FAILED (see the test output above)"
echo
exit $status
