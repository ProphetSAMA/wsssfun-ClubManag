pipeline {
    agent any

    tools {
        nodejs 'NodeJS_club'
        maven 'Maven_club'
    }

    environment {
        BACKEND_DIR = "Club-Web"
        FRONTEND_PC_DIR = "web/club-pc"
        FRONTEND_ADMIN_DIR = "web/club-admin"
        NPM_REGISTRY = "https://registry.npmmirror.com"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh 'mvn clean package -DskipTests'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "${env.BACKEND_DIR}/target/*.jar", fingerprint: true
                }
            }
        }

        stage('Build Frontends') {
            parallel {
                stage('Build PC Frontend') {
                    steps {
                        dir(env.FRONTEND_PC_DIR) {
                            sh """
                                npm config set registry ${env.NPM_REGISTRY}
                                npm install
                                npm run build
                            """
                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: "${env.FRONTEND_PC_DIR}/dist/**", fingerprint: true
                        }
                    }
                }
                stage('Build Admin Frontend') {
                    steps {
                        dir(env.FRONTEND_ADMIN_DIR) {
                            sh """
                                npm config set registry ${env.NPM_REGISTRY}
                                npm install
                                npm run build
                            """
                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: "${env.FRONTEND_ADMIN_DIR}/dist/**", fingerprint: true
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        failure {
            emailext(
                subject: "构建失败: ${env.JOB_NAME}",
                body: """
                项目名称: ${env.JOB_NAME}
                构建编号: ${env.BUILD_NUMBER}
                构建日志: ${env.BUILD_URL}console
                """,
                to: 'wsssfun@icloud.com'
            )
        }
        success {
            emailext(
                subject: "构建成功: ${env.JOB_NAME}",
                body: """
                项目名称: ${env.JOB_NAME}
                构建编号: ${env.BUILD_NUMBER}
                构建用时: ${currentBuild.durationString}
                """,
                to: 'wsssfun@icloud.com'
            )
        }
    }
}
