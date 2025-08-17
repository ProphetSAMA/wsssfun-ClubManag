pipeline {
    agent any

    tools {
        nodejs 'NodeJS_club' 
        maven 'Maven_club'    
    }

    environment {
        // 基础路径
        BACKEND_DIR = "Club-Web"
        FRONTEND_PC_DIR = "web/club-pc"
        FRONTEND_ADMIN_DIR = "web/club-admin"
        
        // 构建优化配置
        NPM_REGISTRY = "https://registry.npmmirror.com"
        MAVEN_OPTS = "-Xmx2048m -XX:MaxPermSize=512m"
        NODE_OPTIONS = "--max-old-space-size=4096"
        
        // 超时设置（分钟）
        BUILD_TIMEOUT = 30 
    }

    options {
        timeout(time: env.BUILD_TIMEOUT, unit: 'MINUTES')
        retry(1) // 失败后自动重试1次
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    echo """[环境配置]
                    Node.js 版本: ${sh(returnStdout: true, script: 'node -v').trim()}
                    npm 版本: ${sh(returnStdout: true, script: 'npm -v').trim()}
                    Maven 版本: ${sh(returnStdout: true, script: 'mvn -v | head -n 1').trim()}"""
                }
            }
        }

        stage('Checkout SCM') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [
                        [$class: 'RelativeTargetDirectory', relativeTargetDir: '.']
                    ],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ProphetSAMA/wsssfun-ClubManag.git',
                        credentialsId: 'github-credential'
                    ]]
                ])
                
                // 验证目录结构
                sh """
                    ls -la ${env.BACKEND_DIR}/pom.xml || exit 1
                    ls -la ${env.FRONTEND_PC_DIR}/package.json || exit 1
                    ls -la ${env.FRONTEND_ADMIN_DIR}/package.json || exit 1
                """
            }
        }

        stage('Build Backend') {
            steps {
                dir(env.BACKEND_DIR) {
                    sh """
                        mvn clean package \
                        -DskipTests \
                        -Dmaven.wagon.http.retryHandler.count=3 \
                        -s ${env.JENKINS_HOME}/settings.xml
                    """
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: "${env.BACKEND_DIR}/target/*.jar", fingerprint: true
                    stash name: 'backend-artifact', includes: "${env.BACKEND_DIR}/target/*.jar"
                }
            }
        }

        stage('Build Frontends') {
            parallel {
                stage('PC Frontend') {
                    steps {
                        dir(env.FRONTEND_PC_DIR) {
                            script {
                                try {
                                    sh """
                                        export NODE_OPTIONS="${env.NODE_OPTIONS}"
                                        npm config set registry ${env.NPM_REGISTRY}
                                        npm ci --prefer-offline --no-audit
                                        npm run build -- --modern
                                    """
                                } catch (e) {
                                    error "PC端构建失败: ${e}"
                                }
                            }
                        }
                    }
                    post {
                        success {
                            stash name: 'pc-frontend', includes: 'dist/**'
                        }
                    }
                }

                stage('Admin Frontend') {
                    steps {
                        dir(env.FRONTEND_ADMIN_DIR) {
                            script {
                                try {
                                    sh """
                                        export NODE_OPTIONS="${env.NODE_OPTIONS}"
                                        npm config set registry ${env.NPM_REGISTRY}
                                        npm ci --prefer-offline --no-audit
                                        npm run build -- --modern
                                    """
                                } catch (e) {
                                    error "Admin端构建失败: ${e}"
                                }
                            }
                        }
                    }
                    post {
                        success {
                            stash name: 'admin-frontend', includes: 'dist/**'
                        }
                    }
                }
            }
        }

        stage('Archive Artifacts') {
            steps {
                unstash 'backend-artifact'
                unstash 'pc-frontend'
                unstash 'admin-frontend'
                
                archiveArtifacts artifacts: "**/target/*.jar, **/dist/**", fingerprint: true
            }
        }
    }

    post {
        always {
            script {
                // 记录构建用时
                currentBuild.description = "用时: ${currentBuild.durationString}"
                
                // 清理工作空间（保留最近3次构建）
                cleanWs(
                    cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    deleteDirs: true,
                    patterns: [
                        [pattern: '.npm/_logs/**', type: 'INCLUDE'],
                        [pattern: '**/node_modules/**', type: 'EXCLUDE']
                    ]
                )
            }
        }
        
        success {
            emailext (
                subject: "构建成功: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <h2>构建成功</h2>
                <p><b>项目:</b> ${env.JOB_NAME}</p>
                <p><b>版本:</b> ${env.BUILD_NUMBER}</p>
                <p><b>用时:</b> ${currentBuild.durationString}</p>
                <p><a href="${env.BUILD_URL}">查看详情</a></p>
                """,
                to: 'wsssfun@icloud.com',
                mimeType: 'text/html'
            )
        }
        
        failure {
            emailext (
                subject: "构建失败: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                <h2>构建失败</h2>
                <p><b>项目:</b> ${env.JOB_NAME}</p>
                <p><b>失败阶段:</b> ${env.STAGE_NAME}</p>
                <p><b>日志:</b> <a href="${env.BUILD_URL}console">查看错误详情</a></p>
                <hr>
                <p><b>最近修改:</b></p>
                ${sh(returnStdout: true, script: 'git log -3 --pretty=format:"%h - %an, %ar : %s"')}
                """,
                to: 'wsssfun@icloud.com',
                mimeType: 'text/html'
            )
        }
        
        unstable {
            emailext (
                subject: "构建不稳定: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                to: 'wsssfun@icloud.com'
            )
        }
    }
}
