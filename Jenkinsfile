node {
    def server
    def buildInfo
    def rtGradle

    stage ('Clone') {
        checkout([
          $class: 'GitSCM',
          branches: [[name: '*/jenkins']],
          userRemoteConfigs: [[url: 'git@github.com:ElyxorCorp/hexbid.git'],
          [credentialsId:'elx-bot-ssh']]
        ])
    }

    stage ('Artifactory configuration') {
        server = Artifactory.server "artifactory-elyxor"

        rtGradle = Artifactory.newGradleBuild()
        rtGradle.tool = "GRADLE_LATEST"
        rtGradle.deployer repo: (env.BRANCH_NAME=='release' ? 'libs-release-local' : 'libs-snapshot-local'), server: server
        rtGradle.resolver repo: 'libs-release', server: server
        rtGradle.deployer.deployArtifacts = false // Disable artifacts deployment during Gradle run

        buildInfo = Artifactory.newBuildInfo()
    }

    stage ('Build Java Refimpl Project') {
        rtGradle.run rootDir: 'java/', buildFile: 'build.gradle', tasks: 'clean build'
    }

    stage ('Test') {
        rtGradle.run rootDir: 'java/', buildFile: 'build.gradle', tasks: 'clean test'
    }

    stage ('Deploy') {
        rtGradle.run rootDir: 'java/', buildFile: 'build.gradle', tasks: 'artifactoryPublish', buildInfo: buildInfo
        rtGradle.deployer.deployArtifacts buildInfo
    }

    stage ('Publish build info') {
        server.publishBuildInfo buildInfo
    }
}
