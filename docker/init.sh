#!/bin/bash
set -euo pipefail

WAIT_INTERVAL=5
TIMEOUT=300
SAFE_MODE_TIMEOUT=300
START=0

echo "Initializer: waiting for HDFS to be ready..."
while ! hdfs dfs -ls / > /dev/null 2>&1; do
  echo "HDFS not ready yet..."
  sleep $WAIT_INTERVAL
  START=$((START + WAIT_INTERVAL))
  if [ $START -ge $TIMEOUT ]; then
    echo "Timeout waiting for HDFS" >&2
    exit 1
  fi
done
echo "HDFS is ready."

echo "Waiting for HDFS to leave safe mode..."
START=0
while hdfs dfsadmin -safemode get | grep -q "ON"; do
  echo "HDFS still in safe mode..."
  sleep $WAIT_INTERVAL
  START=$((START + WAIT_INTERVAL))
  if [ $START -ge $SAFE_MODE_TIMEOUT ]; then
    echo "Timeout waiting for HDFS to leave safe mode" >&2
    exit 1
  fi
done
echo "HDFS left safe mode."

# Șterge output anterior
hdfs dfs -rm -r /output_anagrams || true

# Creează input în HDFS
hdfs dfs -mkdir -p /input || true

# Upload fișiere
if [ -d /input_local ]; then
  hdfs dfs -put -f /input_local/* /input/ || true
else
  echo "/input_local not found or not a directory" >&2
  exit 1
fi

# Rulează job-ul MapReduce (folosim yarn jar; folosește -D pentru a forța 1 reducer)
echo "Running MapReduce job (yarn jar)..."
yarn jar /jars/intership-1.0-SNAPSHOT.jar /input/ /output_anagrams

echo "Initializer finished successfully."

hdfs dfs -get -f /output_anagrams/* /output/ || true

exit 0
