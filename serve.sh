#!/bin/bash
pushd src/client
python -m SimpleHTTPServer 8079 &
popd

websocketd --port=8080 java -jar build/libs/Chess.jar &

# Kill children on SIGTERM.
trap "kill 0" SIGINT SIGTERM EXIT

wait
