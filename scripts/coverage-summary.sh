#!/bin/sh
# Prints test results and a JaCoCo coverage summary after `mvn verify`.
# Usage: scripts/coverage-summary.sh [build directory, default: target]
TARGET="${1:-target}"

suite_totals() {
	files=$(ls "$1"/TEST-*.xml 2>/dev/null)
	if [ -z "$files" ]; then
		echo "not run"
		return
	fi
	grep -h '<testsuite ' $files | awk '
		function attr(name,   m) {
			if (match($0, name "=\"[0-9]+\"")) { m = substr($0, RSTART, RLENGTH); gsub(/[^0-9]/, "", m); return m + 0 }
			return 0
		}
		{ t += attr("tests"); f += attr("failures"); e += attr("errors"); s += attr("skipped") }
		END { printf "%d passed, %d failed, %d skipped\n", t - f - e - s, f + e, s }'
}

echo
echo "================================ Test results ================================"
printf "  %-40s %s\n" "Unit tests (JUnit 5 + Mockito)" "$(suite_totals "$TARGET/surefire-reports")"
printf "  %-40s %s\n" "Integration tests (Testcontainers MySQL)" "$(suite_totals "$TARGET/failsafe-reports")"

CSV="$TARGET/site/jacoco/jacoco.csv"
echo
echo "============================== Coverage (JaCoCo) ============================="
if [ ! -f "$CSV" ]; then
	echo "  No coverage report: the build stopped before the integration tests ran."
	exit 0
fi
awk -F, '
	function pct(covered, missed) {
		return (covered + missed) == 0 ? "     -" : sprintf("%5.1f%%", 100 * covered / (covered + missed))
	}
	function row(name, lc, lm, bc, bm) {
		printf "  %-24s %s %12s     %s %12s\n", name, pct(lc, lm), "(" lc "/" lc + lm ")", pct(bc, bm), "(" bc "/" bc + bm ")"
	}
	NR > 1 {
		p = $2; sub(/^com\.mymobile\.?/, "", p); if (p == "") p = "(app)"
		if (!(p in seen)) { seen[p] = 1; order[++n] = p }
		bm[p] += $6; bc[p] += $7; lm[p] += $8; lc[p] += $9
		BM += $6; BC += $7; LM += $8; LC += $9
	}
	END {
		printf "  %-24s %-20s     %-20s\n", "Package", "Lines", "Branches"
		printf "  %-24s %-20s     %-20s\n", "-------", "-----", "--------"
		for (i = 1; i <= n; i++) row(order[i], lc[order[i]], lm[order[i]], bc[order[i]], bm[order[i]])
		printf "  %-24s %-20s     %-20s\n", "", "--------------------", "--------------------"
		row("TOTAL", LC, LM, BC, BM)
	}' "$CSV"
echo
