pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    echo '🚀 Exécution des tests unitaires et Cucumber...'
                    bat 'gradlew.bat test'

                    echo '📊 Génération du rapport JaCoCo...'
                    bat 'gradlew.bat jacocoTestReport'

                    echo 'Archiving Test Results...'
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/TEST-*.xml'

                    echo 'Generating Cucumber Reports...'
                    cucumber buildStatus: 'UNSTABLE',
                            reportTitle: 'Cucumber Report',
                            fileIncludePattern: '**/build/reports/cucumber/report.json',
                            trendsLimit: 10,
                            classifications: [
                                [key: 'Framework', value: 'Cucumber + JUnit 5']
                            ]
                }
            }
        }

        stage('Code Analysis') {
            steps {
                script {
                    echo '🔍 Compilation des classes pour SonarQube...'
                    bat 'gradlew.bat classes'

                    echo 'Analyse du code avec SonarQube...'
                    withSonarQubeEnv('MonSonar') {
                        bat 'gradlew.bat sonarqube --info'
                    }
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
                script {
                    echo '📦 Génération du JAR (sans relancer les tests)...'
                    bat 'gradlew.bat build -x test'

                    echo '📚 Génération de la Javadoc...'
                    try {
                        bat 'gradlew.bat generateJavadoc'
                        archiveArtifacts artifacts: 'build/docs/javadoc/**/*', fingerprint: true, allowEmptyArchive: false
                    } catch (Exception e) {
                        echo '⚠️ Javadoc non configurée – ignorée'
                    }

                    echo 'Archiving artifacts...'
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
        always {
            echo '🧹 Nettoyage du workspace...'
            cleanWs()
        }

        success {
            // 📧 Email
            script {
                try {
                    mail (
                        to: 'kettyasmine2004@gmail.com',
                        subject: "✅ SUCCESS: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                        body: """
Le pipeline s'est exécuté avec succès !
Projet : ${env.JOB_NAME}
Build : ${env.BUILD_NUMBER}
URL : ${env.BUILD_URL}
                        """.stripIndent()
                    )
                    echo '✅ Email de succès envoyé.'
                } catch (Exception e) {
                    echo "❌ Échec envoi email (succès) : ${e.message}"
                }
            }

            // 💬 Slack
            script {
                try {
                    slackSend(
                        tokenCredentialId: 'slack-bot-token',
                        channel: 'webhook',
                        botUser: true,
                        message: "✅ Build réussi : <${env.BUILD_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>"
                    )
                    echo '✅ Message Slack de succès envoyé.'
                } catch (Exception e) {
                    echo "❌ Échec envoi Slack (succès) : ${e.message}"
                }
            }
        }

        failure {
            // 📧 Email
            script {
                try {
                    mail (
                        to: 'kettyasmine2004@gmail.com',
                        subject: "❌ FAILURE: Pipeline ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                        body: """
Le pipeline a échoué.
Projet : ${env.JOB_NAME}
Build : ${env.BUILD_NUMBER}
URL : ${env.BUILD_URL}
Logs : ${env.BUILD_URL}console
Stage échoué : ${env.STAGE_NAME}
                        """.stripIndent()
                    )
                    echo '✅ Email d’échec envoyé.'
                } catch (Exception e) {
                    echo "❌ Échec envoi email (échec) : ${e.message}"
                }
            }

            // 💬 Slack
            script {
                try {
                    slackSend(
                        tokenCredentialId: 'slack-bot-token',
                        channel: 'webhook',
                        botUser: true,
                        message: "❌ Build échoué : <${env.BUILD_URL}|${env.JOB_NAME} #${env.BUILD_NUMBER}>\nStage : ${env.STAGE_NAME}"
                    )
                    echo '✅ Message Slack d’échec envoyé.'
                } catch (Exception e) {
                    echo "❌ Échec envoi Slack (échec) : ${e.message}"
                }
            }
        }
    }
}