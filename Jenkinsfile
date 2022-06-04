#!/usr/bin/env groovy
def releaseBranch = "release-1.18";

pipeline {
    agent any
    tools {
        jdk "jdk-17.0.1"
    }
    environment {
        CURSE_API = credentials('curseforge_api_key')
    }
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

                echo 'Deploying to CurseForge'
                sh './gradlew curseforge'
            }
        }
    }
    post {
        always {
            archive 'build/libs/**.jar'
        }
    }
}
