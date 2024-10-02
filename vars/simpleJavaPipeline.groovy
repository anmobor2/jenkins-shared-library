// vars/javaPipeline.groovy jenkins-shared-library
def call(Map params) {
    def projectName = params.get('projectName', 'default-project')
    def emailToNotify = params.get('emailToNotify', 'tonibandal@gmail.com')
    def stopOnFailedQG = params.get('stopOnFailedQG', true)
    def testContainers = params.get('testContainers', false)
    def javaVersion = params.get('javaVersion', 11)  // Default to Java 11

    // Dynamically select the agent based on the Java version
    def agentLabel = (javaVersion == 17) ? 'java17-agent' : 'java11-agent'

    pipeline {
        agent { label agentLabel }

        stages {
            stage('Build') {
                steps {
                    echo "Building project: ${projectName} with Java ${javaVersion}"
                    sh "mvn clean install"  // Maven command
                }
            }

            stage('Test') {
                steps {
                  script {
                    if (testContainers) {
                        echo "Running tests with TestContainers"
                        // Insert test steps here
                    } else {
                        echo "Running standard tests"
                        // Insert standard test steps here
                    }
                }
            }

            stage('Quality Gate') {
                when {
                    expression { return stopOnFailedQG }
                }
                steps {
                    echo "Running quality gate checks"
                    // Insert quality gate steps here, e.g., SonarQube
                    //sh "sonar-scanner -Dsonar.projectKey=${projectName} -Dsonar.qualitygate.wait=true"
                }
            }

            stage('Notify') {
                steps {
                    mail to: emailToNotify,
                         subject: "Build result for ${projectName}",
                         body: "The build has completed. Check the results."
                }
            }
        }

        post {
            failure {
                echo "The build failed"
                // Additional failure handling
            }
        }
    }
}