pipeline {
    agent any

    tools {
        nodejs 'NodeJS_club'  // Jenkins 里配置的 Node.js 工具名
        maven 'Maven_club'    // Jenkins 里配置的 Maven 工具名
    }

    environment {
        // 定义环境变量
        BACKEND_DIR = "Club-Web"
        FRONTEND_PC_DIR = "web/club-pc"
        FRONTEND_ADMIN_DIR = "web/club-admin"
        
        // 使用国内npm镜像加速
        NPM_REGISTRY = "https://registry.npmmirror.com"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend (SpringBoot)') {
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
            emailext body: """
                构建失败详情：
                项目: ${env.JOB_NAME}
                构建编号: ${env.BUILD_NUMBER}
                错误日志: ${env.BUILD_URL}console
            """, 
            subject: 'Jenkins构建失败告警 - ${env.JOB_NAME}', 
            to: 'wsssfun@icloud.com'
        }
    }
}
