#!/usr/bin/env groovy
def releaseBranch = "release-1.16";

pipeline {
    agent any
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build') {
            steps {
                echo 'Building'
                sh './gradlew build'
            }
        }

        stage('Publish') {
            when {
                branch releaseBranch
            }
            steps {
                echo 'Deploying to Maven'
                sh './gradlew publish'
            }
        }
    }
    post {
        always {
            archive 'build/libs/**.jar'
        }
    }
}
