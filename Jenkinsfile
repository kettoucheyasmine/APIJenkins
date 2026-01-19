pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                echo '🚀 Exécution des tests unitaires...'
                bat 'gradlew.bat test jacocoTestReport'
            }
            post {
                always {
                    junit 'build/test-results/test/TEST-*.xml'
                }
            }
        }

        stage('Code Analysis') {
            steps {
                echo '🔍 Analyse du code avec SonarQube...'
                withSonarQubeEnv('MonSonar') {
                    bat 'gradlew.bat sonarqube --info'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo '✅ Vérification de la Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build') {
            steps {
                echo '📦 Génération du JAR...'
                bat 'gradlew.bat build'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '📤 Publication sur MyMavenRepo...'
                withCredentials([
                    usernamePassword(
                        credentialsId: 'mymavenrepo-credentials',
                        usernameVariable: 'MAVEN_USER',
                        passwordVariable: 'MAVEN_PASSWORD'
                    )
                ]) {
                    bat 'gradlew.bat publish -PmavenUser=%MAVEN_USER% -PmavenPassword=%MAVEN_PASSWORD%'
                }
            }
        }
    }

        post {
            success {
                emailext (
                    subject: "Build réussi",
                    body: "Pipeline terminé avec succès.",
                    to: 'kettyasmine2004@gmail.com'
                )
            }
            failure {
                emailext (
                    subject: "Build échoué",
                    body: "Le pipeline a échoué.",
                    to: 'kettyasmine2004@gmail.com'
                )
            }
        }
    }
