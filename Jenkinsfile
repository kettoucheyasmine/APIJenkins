pipeline {
    agent any

    tools {
        // Assurez-vous que ce nom correspond à celui défini dans Jenkins → Global Tool Configuration
        jdk 'jdk11'
    }

    stages {
        stage('Test') {
            steps {
                echo '🚀 Exécution des tests unitaires et Cucumber...'
                sh './gradlew test jacocoTestReport'
            }
            post {
                always {
                    // Publier les résultats des tests
                    junit 'build/test-results/test/TEST-*.xml'
                    // Publier la couverture de code Jacoco
                    publishCoverage adapters: [jacocoAdapter('build/reports/jacoco/test/jacocoTestReport.xml')], calculateDiffForChangeRequests: false, sourceFileResolver: sourceFiles('NEVER_STORE')
                }
            }
        }

        stage('Code Analysis') {
            steps {
                echo '🔍 Analyse du code avec SonarQube...'
                withSonarQubeEnv('MonSonar') {
                    sh './gradlew sonar --info'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo '✅ Vérification de la Quality Gate SonarQube...'
                // Attendre max 5 minutes
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build') {
            steps {
                echo '📦 Génération du JAR...'
                sh './gradlew build'
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
                    sh './gradlew publish -PmavenUser=$MAVEN_USER -PmavenPassword=$MAVEN_PASSWORD'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès !'
        }
        failure {
            echo '❌ Le pipeline a échoué.'
        }
    }
}