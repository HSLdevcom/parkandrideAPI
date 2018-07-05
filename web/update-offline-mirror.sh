#!/usr/bin/env bash

readonly MIRROR_DIR=yarn-offline-mirror
readonly EXPECTED_SIZE_MB=70

usage() {
    echo "Usage: $0 [--rebuild]" 1>&2
    exit 1
}

cleanup() {
    rm -rf "${MIRROR_DIR}"
    mkdir -p "${MIRROR_DIR}"
    echo '*' > "${MIRROR_DIR}/.gitignore"
    echo "Emptied the ${MIRROR_DIR} directory."
}

main() {
    yarn install --frozen-lockfile --modules-folder /dev/null
    echo "(The \"ENOTDIR: not a directory\" error is to be expected. Don't mind it.)"

    local size_mb=$(du -m "${MIRROR_DIR}" | cut -f1)

    if [[ "${size_mb}" -gt "${EXPECTED_SIZE_MB}" ]]; then
        echo
        echo "Yarn offline mirror updated."
        exit 0
    else
        echo
        echo "The resulting directory ${MIRROR_DIR} is unexpectedly small (${size_mb} MB)."
        echo "Please check the above output to see what went wrong, or update the threshold."
        exit 1
    fi
}

for arg in "$@"; do
    case "$arg" in
        --rebuild) cleanup; shift ;;
        *) usage ;;
    esac
done

main
