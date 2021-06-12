#!/usr/bin/env groovy
def releaseBranch = "release-1.16";

pipeline {
    agent any
    tools {
        jdk "jdk8u292-b10"
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
