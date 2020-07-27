#!/bin/sh

RESULT_DIR=results/$(date "+%y%d%mT%H%M%S")

docker pull infoblox/ghz:0.0.1

GRPC_BENCHMARK_SELECT=${GRPC_BENCHMARK_SELECT:-"."}

for NAME in rust_tonic_mt rust_tonic_st rust_thruster \
            go_grpc \
            cpp_grpc_mt cpp_grpc_st \
            ruby_grpc \
            python_grpc \
            java_grpc java_micronaut java_aot \
            kotlin_grpc \
            node_grpc \
            dart_grpc \
            crystal_grpc \
            swift_grpc \
            csharp_grpc \
            lua_grpc \
            php_grpc \
            elixir_grpc; do
    if grep -qE "$GRPC_BENCHMARK_SELECT" <<< "$NAME"; then
        ./run_single_bench.sh ${NAME}_test "${RESULT_DIR}"
    fi
done

echo "-----"
echo "Benchmark finished. Detailed results are located in: ${RESULT_DIR}"

docker run --name analyzer --rm \
  -v "${PWD}"/analyze:/analyze:ro \
  -v "${PWD}"/"${RESULT_DIR}":/reports:ro \
  ruby:2.7-buster ruby /analyze/results_analyze.rb reports
