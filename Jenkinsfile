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
            script {
                // 📧 Email
                mail (
                    to: 'kettyasmine2004@gmail.com',
                    subject: "✅ SUCCESS: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                    body: """
                    Le pipeline s'est exécuté avec succès !
                    Projet : ${env.JOB_NAME}
                    Build : ${env.BUILD_NUMBER}
                    URL : ${env.BUILD_URL}
                    """
                )
                // 💬 Slack
                withCredentials([string(credentialsId: 'slack-webhook-url', variable: 'SLACK_WEBHOOK')]) {
                    slackSend(
                        webhookUrl: "${SLACK_WEBHOOK}",
                        channel: '#ci-cd',
                        color: 'good',
                        message: "✅ Build réussi : <${env.BUILD_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>"
                    )
                }
            }
        }
        failure {
            script {
                // 📧 Email
                mail (
                    to: 'kettyasmine2004@gmail.com',
                    subject: "❌ FAILURE: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                    body: """
                    Le pipeline a échoué.
                    Projet : ${env.JOB_NAME}
                    Build : ${env.BUILD_NUMBER}
                    URL : ${env.BUILD_URL}
                    Logs : ${env.BUILD_URL}console
                    """
                )
                // 💬 Slack
                withCredentials([string(credentialsId: 'slack-webhook-url', variable: 'SLACK_WEBHOOK')]) {
                    slackSend(
                        webhookUrl: "${SLACK_WEBHOOK}",
                        channel: '#ci-cd',
                        color: 'danger',
                        message: "❌ Build échoué : <${env.BUILD_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>"
                    )
                }
            }
        }
    }
}