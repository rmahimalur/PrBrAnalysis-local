@Library('CISharedLibraries@master') _

Map sqBrConfig=[
      sonarqube:[
        qualityGate:[
          invoke:true,
          timeout:0
        ],
        url:'https://imp.sonarqube.backends.cms.gov',
        credentialId:'mrma-imp-sonarqube',
        projectKey:'OC-Nimbus-PrBrAnalysis-Demo',
        sources: './src',
        cmdOptions:'-Dsonar.java.binaries="./target/classes"'
      ]
    ]
Map sqPrConfig=[
      sonarqube:[
        qualityGate:[
          invoke:true,
          timeout:0
        ],
        url:'https://imp.sonarqube.backends.cms.gov',
        credentialId:'mrma-imp-sonarqube',
        projectKey:'OC-Nimbus-PrBrAnalysis-Demo',
        sources: './src',
        cmdOptions:'-Dsonar.java.binaries="./target/classes" -Dsonar.pullrequest.key="$CHANGE_ID" -Dsonar.pullrequest.branch="$CHANGE_BRANCH" -Dsonar.pullrequest.base="$CHANGE_TARGET"'
      ]
    ]

    pipeline {
        agent {
            kubernetes {
                defaultContainer 'maven'
                yaml """
    kind: Pod
    metadata:
      name: sonarscanner
    spec:
      containers:
      - name: maven
        image: maven:latest
        imagePullPolicy: Always
        command: ["tail", "-f", "/dev/null"]
        tty: true
      - name: sonarscanner
        image: sonarsource/sonar-scanner-cli:latest
        imagePullPolicy: Always
        command: ["tail", "-f", "/dev/null"]
        tty: true
      nodeSelector:
         Agents: true
    """
            }
        }

    stages {
        stage('Performing the Build') {
            steps {
                script {
                    container('maven') {
                            if (env.BRANCH_NAME ==~ "PR-.*") {
                                echo "This is PR analysis"
                                sh "mvn clean package"
                            } else {
                                echo 'This is Branch Analysis'
                                sh "mvn clean package"
                            }
                    }
                }
            }
        }
        stage('SonarScanner Analysis') {
            steps {
                script {
                    container('sonarscanner') {
                            if (env.BRANCH_NAME ==~ "PR-.*") {
                                echo "Performing PR analysis"
                                sonarqubeCodeScan(sqPrConfig)
                            } else {
                                echo 'Performing Branch Analysis'
                                sonarqubeCodeScan(sqBrConfig)
                            }
                    }
                }
            }
        }
    }
    post{
      always{
            slackNotification([:],currentBuild.currentResult,'#test-cbcore-slack-notification','','','','nimbus-jenkins-slack-testcbcore-token')
      }
    }
}
