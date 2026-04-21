#!/usr/bin/env bash
set -e

SPRING_BOOT_VERSION="4.0.2"
JAVA_VERSION="17"
PROJECT_VERSION="0.0.1-SNAPSHOT"

generate_service() {
  SERVICE_NAME="$1"
  PACKAGE_NAME="$2"
  GROUP_ID="$3"
  DEPENDENCIES="$4"

  if [ -d "$SERVICE_NAME" ]; then
    echo "Skipping $SERVICE_NAME (already exists)"
    return
  fi

  echo "Generating $SERVICE_NAME ..."
  spring init \
    --boot-version="$SPRING_BOOT_VERSION" \
    --build=gradle \
    --type=gradle-project \
    --java-version="$JAVA_VERSION" \
    --packaging=jar \
    --name="$SERVICE_NAME" \
    --package-name="$PACKAGE_NAME" \
    --groupId="$GROUP_ID" \
    --dependencies="$DEPENDENCIES" \
    --version="$PROJECT_VERSION" \
    "$SERVICE_NAME"
}

generate_service "api-gateway" "com.champsoft.vrms.gateway" "com.champsoft" "web,validation"
generate_service "customers-service" "com.champsoft.vrms.customers" "com.champsoft" "web,validation"
generate_service "event-service" "com.champsoft.vrms.event" "com.champsoft" "web,validation"
generate_service "ticketinventory-service" "com.champsoft.vrms.ticketinventory" "com.champsoft" "web,validation"
generate_service "ticketordermanagement-service" "com.champsoft.vrms.ticketordermanagement" "com.champsoft" "web,validation"

echo
echo "All service skeletons generated successfully."