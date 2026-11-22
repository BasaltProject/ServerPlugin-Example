#!/bin/sh
# Runs every test/*scenario.txt against build/plugin.wasm and diffs each trace with its expected.trace.
# Needs basalt-wasm-trace on PATH or in BASALT_WASM_TRACE. Pass --update to rewrite the expected traces.
set -eu
root=$(cd "$(dirname "$0")/.." && pwd)
trace=${BASALT_WASM_TRACE:-basalt-wasm-trace}
stage=$(mktemp -d)
trap 'rm -rf "$stage"' EXIT
cp "$root/plugin.toml" "$root/build/plugin.wasm" "$stage/"
cp -r "$root/resources" "$stage/"
status=0
for scenario in "$root"/test/scenario.txt "$root"/test/*.scenario.txt; do
    [ -f "$scenario" ] || continue
    expected="${scenario%scenario.txt}expected.trace"
    "$trace" "$stage" "$scenario" >"$stage/actual.trace"
    if [ "${1:-}" = "--update" ]; then
        cp "$stage/actual.trace" "$expected"
    elif ! diff -u "$expected" "$stage/actual.trace"; then
        echo "FAIL $(basename "$scenario")" >&2
        status=1
    fi
done
exit $status
