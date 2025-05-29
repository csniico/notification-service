pipeline {
    agent any

    environment {
        IMAGE_NAME = 'notification-service:latest'
    }

    tools {
        maven 'Maven 3'
        jdk 'JDK 21'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/csniico/notification-service.git'
            }
        }

        /*
        stage('Build Java App') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        */

        stage('Build Docker Image') {
            steps {
                sh "docker build --no-cache -t ${IMAGE_NAME} ."
            }
        }

        stage('Trivy Scan') {
            steps {
                sh '''
                    echo "Scanning image with Trivy (FAIL on HIGH or CRITICAL)..."
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v $HOME/.cache:/root/.cache/ \
                        aquasec/trivy image \
                        --severity HIGH,CRITICAL \
                        --exit-code 1 \
                        --no-progress \
                        ${IMAGE_NAME}

                    echo "Showing full vulnerability report (all severities)..."
                    docker run --rm \
                        -v /var/run/docker.sock:/var/run/docker.sock \
                        -v $HOME/.cache:/root/.cache/ \
                        aquasec/trivy image \
                        --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL \
                        --no-progress \
                        ${IMAGE_NAME}
                '''
            }
        }
    }

    post {
        failure {
            echo 'Pipeline failed (likely due to HIGH or CRITICAL vulnerabilities).'
        }
        success {
            echo 'Build and scan succeeded.'
        }
    }
}
