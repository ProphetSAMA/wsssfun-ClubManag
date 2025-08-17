pipeline {
    agent any

    tools {
        nodejs 'NodeJS_club'  // Jenkins 里配置的 Node.js 工具名
        maven 'Maven_club'     // Jenkins 里配置的 Maven 工具名
    }

    environment {
        // 可选：定义环境变量（如后端端口、前端部署路径等）
        BACKEND_DIR = "Club-Web"
        FRONTEND_DIR = "web"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm  // 自动拉取当前仓库代码
            }
        }

        stage('Build Backend (SpringBoot)') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh 'mvn clean package -DskipTests'  // 跳过测试（可选）
                    // 如果使用 Gradle：
                    // sh './gradlew build -x test'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "${env.BACKEND_DIR}/target/*.jar", fingerprint: true
                }
            }
        }

        stage('Build Frontend (Vue)') {
            steps {
                dir(env.FRONTEND_DIR) {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "${env.FRONTEND_DIR}/dist/**", fingerprint: true
                }
            }
        }

        
    }

    post {
        always {
            // 清理工作空间（可选）
            cleanWs()
        }
        failure {
            // 构建失败时发送通知（需配置邮件/钉钉等插件）
            emailext body: '构建失败，请检查日志: ${BUILD_URL}', subject: 'Jenkins Build Failed', to: 'your-email@example.com'
        }
    }
}
