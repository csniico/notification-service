#!/bin/bash

# Exit on any error
set -e

# Function to check if a variable exists in .env file
check_env_var() {
    local var_name=$1
    if ! grep -q "^${var_name}=" .env; then
        echo "Error: ${var_name} is not set in .env file"
        exit 1
    fi
}

# Check if .env file exists
if [ ! -f .env ]; then
    echo "Error: .env file not found"
    echo "Please create a .env file with required variables:"
    echo "- TRACING_URL"
    echo "- SNS_TOPIC_ARN"
    echo "- AWS_REGION"
    exit 1
fi

# Check required environment variables
echo "Checking required environment variables..."
check_env_var "TRACING_URL"
check_env_var "SNS_TOPIC_ARN"
check_env_var "AWS_REGION"

# Verify that the variables are not empty
while IFS='=' read -r key value; do
    # Skip comments and empty lines
    [[ $key =~ ^#.*$ ]] && continue
    [[ -z "$key" ]] && continue
    
    # Remove any surrounding quotes from value
    value=$(echo "$value" | sed -e 's/^"//' -e 's/"$//' -e "s/^'//" -e "s/'$//")
    
    if [[ -z "$value" ]]; then
        echo "Error: $key has empty value in .env file"
        exit 1
    fi
done < .env

echo "Environment validation successful!"

# Build the Docker image
echo "Building Docker image..."
docker build -t notification-service .

# Run the container with environment variables
echo "Running Docker container..."
docker run --env-file .env -p 8080:8080 notification-service